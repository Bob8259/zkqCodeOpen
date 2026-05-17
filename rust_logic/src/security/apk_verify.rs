//! Manual APK Signature Verification (V2 Scheme)
//!
//! Locates the current process's base.apk via /proc/self/maps,
//! parses EOCD -> Central Directory -> APK Signing Block -> V2 Signer -> Certificate,
//! then compares the SHA-256 fingerprint against known good hashes.
//!
//! All sensitive strings are XOR-encrypted at compile time.

#[cfg(unix)]
use rustix::fs::{openat, Mode, OFlags, CWD};
use sha2::{Sha256, Digest};

// --- Compile-time XOR encoder (same pattern as anti_debug.rs) ---
const fn xor_bytes<const N: usize>(input: [u8; N], key: u8) -> [u8; N] {
    let mut out = [0u8; N];
    let mut i = 0;
    while i < N {
        out[i] = input[i] ^ key;
        i += 1;
    }
    out
}

// Runtime XOR decoder — force-inlined so no single "decode" callsite exists
#[inline(always)]
fn xor_decode(encrypted: &[u8], key: u8) -> String {
    encrypted.iter().map(|&b| (b ^ key) as char).collect()
}

// --- Encrypted Constants ---
const K_SIG: u8 = 0xA3;

// "APK Sig Block 42"
const ENC_SIG_MAGIC: [u8; 16] = xor_bytes(*b"APK Sig Block 42", K_SIG);
// "/proc/self/maps"
const ENC_MAPS_PATH: [u8; 15] = xor_bytes(*b"/proc/self/maps", K_SIG);
// "base.apk"
const ENC_BASE_APK: [u8; 8] = xor_bytes(*b"base.apk", K_SIG);

// Target SHA-256 fingerprints (either one passing means verification succeeds)
const ENC_TARGET_HASH_1: [u8; 64] = xor_bytes(
    *b"73618E6AC898ABFC9C433521BBC49F801E6A19AE684B56A45AC62A7A6265C2DC",
    K_SIG,
);
const ENC_TARGET_HASH_2: [u8; 64] = xor_bytes(
    *b"BD6CACAC7CE26263E5652FACC4B5D158FA4C988EEEFF2476BB2546BE0D971DAE",
    K_SIG,
);
// Debug signing key SHA-256 fingerprint
const ENC_TARGET_HASH_3: [u8; 64] = xor_bytes(
    *b"447F7932222A19904789A1D46A392FF0B99AC43DC4845E0ECCF91A9DCC942DA1",
    K_SIG,
);

// --- Helper Functions ---

#[inline(always)]
fn read_u32_le(buf: &[u8], offset: usize) -> u32 {
    u32::from_le_bytes([
        buf[offset],
        buf[offset + 1],
        buf[offset + 2],
        buf[offset + 3],
    ])
}

#[inline(always)]
fn read_u64_le(buf: &[u8], offset: usize) -> u64 {
    u64::from_le_bytes([
        buf[offset],
        buf[offset + 1],
        buf[offset + 2],
        buf[offset + 3],
        buf[offset + 4],
        buf[offset + 5],
        buf[offset + 6],
        buf[offset + 7],
    ])
}

/// Read a file to String using only rustix syscalls (no std::fs)
#[cfg(unix)]
#[cold]
fn read_to_string_rustix(path: &str) -> Option<String> {
    let fd = openat(CWD, path, OFlags::RDONLY, Mode::empty()).ok()?;
    let mut buf = Vec::new();
    let mut tmp = [0u8; 4096];
    loop {
        match rustix::io::read(&fd, &mut tmp) {
            Ok(0) => break,
            Ok(n) => buf.extend_from_slice(&tmp[..n]),
            Err(_) => return None,
        }
    }
    String::from_utf8(buf).ok()
}

/// Read exactly `len` bytes from fd at the given absolute offset.
/// Uses lseek + read loop. Returns None on any I/O error.
#[cfg(unix)]
#[inline(always)]
fn read_exact_at(fd: &rustix::fd::OwnedFd, offset: u64, len: usize) -> Option<Vec<u8>> {
    use rustix::fs::SeekFrom;
    rustix::fs::seek(fd, SeekFrom::Start(offset)).ok()?;
    let mut buf = vec![0u8; len];
    let mut filled = 0;
    while filled < len {
        match rustix::io::read(fd, &mut buf[filled..]) {
            Ok(0) => return None, // unexpected EOF
            Ok(n) => filled += n,
            Err(_) => return None,
        }
    }
    Some(buf)
}

/// Retrieve the current APK path by parsing /proc/self/maps.
/// Uses a tiered approach for maximum device compatibility:
///   Pass 1: prefer r--s (standard shared APK mapping)
///   Pass 2: fallback to r--p (some custom ROMs use private mapping)
///   Pass 3: any permission flags — ultimate fallback
#[cfg(unix)]
fn get_apk_path_inline() -> Result<Option<String>, &'static str> {
    let maps_path = xor_decode(&ENC_MAPS_PATH, K_SIG);
    let base_apk_str = xor_decode(&ENC_BASE_APK, K_SIG);

    let content = read_to_string_rustix(&maps_path).ok_or("Unable to read /proc/self/maps")?;

    // Pass 1: r--s (standard shared mapping for APK files on most devices)
    for line in content.lines() {
        if line.contains(" r--s ") {
            if let Some(path) = line.split_whitespace().last() {
                if path.ends_with(&base_apk_str) {
                    return Ok(Some(path.to_string()));
                }
            }
        }
    }

    // Pass 2: r--p (some ROMs may use private mapping)
    for line in content.lines() {
        if line.contains(" r--p ") {
            if let Some(path) = line.split_whitespace().last() {
                if path.ends_with(&base_apk_str) {
                    return Ok(Some(path.to_string()));
                }
            }
        }
    }

    // Pass 3: any permission flags — ultimate fallback for exotic mappings
    for line in content.lines() {
        if let Some(path) = line.split_whitespace().last() {
            if path.ends_with(&base_apk_str) {
                return Ok(Some(path.to_string()));
            }
        }
    }

    Ok(None)
}

/// Core APK signature verification — runs once at library load.
/// Parses EOCD -> Central Directory -> APK Signing Block -> V2 Signer -> Certificate,
/// then compares SHA-256 fingerprint against known good hashes.
/// Kills the process immediately on any verification failure.
#[cold]
pub fn verify_apk_signature_inline() {
    #[cfg(unix)]
    {
        let verification_result = (|| -> Result<String, &'static str> {
            let apk_path = match get_apk_path_inline()? {
                Some(path) => path,
                None => return Err("APK path not found in /proc/self/maps"),
            };

            let fd = openat(CWD, apk_path.as_str(), OFlags::RDONLY, Mode::empty())
                .map_err(|_| "Unable to open APK file")?;

            // Get file size via fstat.
            let stat = rustix::fs::fstat(&fd).map_err(|_| "Unable to stat APK file")?;
            let file_size = stat.st_size as u64;
            if file_size < 22 {
                return Err("APK file is too small");
            }

            // 1. Locate EOCD — search backwards from end of file.
            let max_search = core::cmp::min(file_size, 22 + 65535) as usize;
            let eocd_buf = read_exact_at(&fd, file_size - max_search as u64, max_search)
                .ok_or("Unable to read EOCD")?;

            let eocd_sig: [u8; 4] = [0x50, 0x4b, 0x05, 0x06];
            let idx = eocd_buf
                .windows(4)
                .rposition(|w| w == eocd_sig)
                .ok_or("EOCD signature not found")?;

            if idx + 22 > eocd_buf.len() {
                return Err("EOCD record is truncated");
            }

            let cd_offset = read_u32_le(&eocd_buf, idx + 16) as u64;
            if cd_offset < 32 || cd_offset >= file_size {
                return Err("Central directory offset is invalid");
            }

            // 2. Locate APK Signing Block — read 24-byte footer just before CD.
            if cd_offset < 24 {
                return Err("Signing block footer is not present");
            }
            let footer = read_exact_at(&fd, cd_offset - 24, 24)
                .ok_or("Unable to read signing block footer")?;

            let magic = xor_decode(&ENC_SIG_MAGIC, K_SIG);
            if footer[8..24] != *magic.as_bytes() {
                return Err("Signing block magic mismatch");
            }

            let block_size_in_footer = read_u64_le(&footer, 0);
            let total_block_size = block_size_in_footer + 8;
            if cd_offset < total_block_size {
                return Err("Signing block size exceeds file bounds");
            }
            let block_start = cd_offset - total_block_size;

            // 3. Read the entire signing block.
            let block_data = read_exact_at(&fd, block_start, total_block_size as usize)
                .ok_or("Unable to read signing block")?;

            // Parse ID-Value pairs.
            let mut offset = 8usize;
            let pairs_end = (total_block_size as usize).saturating_sub(24);
            let v2_id: u32 = 0x7109871a;
            let mut v2_data: Option<Vec<u8>> = None;

            while offset + 12 <= pairs_end {
                let pair_size = read_u64_le(&block_data, offset) as usize;
                if pair_size < 4 {
                    return Err("Signing block entry size is invalid");
                }
                offset += 8;
                if offset + pair_size > pairs_end {
                    return Err("Signing block entry exceeds block bounds");
                }
                let pair_id = read_u32_le(&block_data, offset);
                if pair_id == v2_id {
                    v2_data = Some(block_data[offset + 4..offset + pair_size].to_vec());
                    break;
                }
                offset += pair_size;
            }

            // 4. Parse V2 Signer to extract the certificate.
            let v2 = v2_data.ok_or("V2 signer block not found")?;
            if v2.len() < 4 {
                return Err("V2 signer block is truncated");
            }

            let signers_seq_len = read_u32_le(&v2, 0) as usize;
            if 4 + signers_seq_len > v2.len() || signers_seq_len < 4 {
                return Err("Signer sequence length is invalid");
            }
            let signers_seq = &v2[4..4 + signers_seq_len];

            let first_signer_len = read_u32_le(signers_seq, 0) as usize;
            if 4 + first_signer_len > signers_seq.len() {
                return Err("First signer length is invalid");
            }
            let signer = &signers_seq[4..4 + first_signer_len];

            if signer.len() < 4 {
                return Err("Signer record is truncated");
            }
            let signed_data_len = read_u32_le(signer, 0) as usize;
            if 4 + signed_data_len > signer.len() {
                return Err("Signed data length is invalid");
            }
            let signed_data = &signer[4..4 + signed_data_len];

            if signed_data.len() < 4 {
                return Err("Signed data is truncated");
            }
            let digests_len = read_u32_le(signed_data, 0) as usize;
            let certs_seq_start = 4 + digests_len;
            if certs_seq_start + 4 > signed_data.len() {
                return Err("Certificate sequence header is missing");
            }
            let certs_seq_len = read_u32_le(signed_data, certs_seq_start) as usize;
            if certs_seq_start + 4 + certs_seq_len > signed_data.len() {
                return Err("Certificate sequence length is invalid");
            }
            let certs_seq = &signed_data[certs_seq_start + 4..certs_seq_start + 4 + certs_seq_len];

            if certs_seq.len() < 4 {
                return Err("Certificate entry is truncated");
            }
            let cert_len = read_u32_le(certs_seq, 0) as usize;
            if 4 + cert_len > certs_seq.len() {
                return Err("Certificate length is invalid");
            }
            let cert = &certs_seq[4..4 + cert_len];

            // 5. Calculate SHA-256 fingerprint.
            let mut hasher = Sha256::new();
            hasher.update(cert);
            Ok(format!("{:X}", hasher.finalize()))
        })();

        let target_hash_1 = xor_decode(&ENC_TARGET_HASH_1, K_SIG);
        let target_hash_2 = xor_decode(&ENC_TARGET_HASH_2, K_SIG);
        let target_hash_3 = xor_decode(&ENC_TARGET_HASH_3, K_SIG);

        match verification_result {
            Ok(ref hash) if hash == &target_hash_1 || hash == &target_hash_2 || hash == &target_hash_3 => {}
            Ok(_) | Err(_) => {
                unsafe { libc::exit(1) };
            }
        }
    }
}
