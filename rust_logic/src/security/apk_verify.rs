//! Manual APK Signature Verification (V2 Scheme)
//!
//! Locates the current process's base.apk via /proc/self/maps,
//! parses EOCD -> Central Directory -> APK Signing Block -> V2 Signer -> Certificate,
//! then compares the SHA-256 fingerprint against known good hashes.
//!
//! All I/O uses rustix syscalls directly (no std::fs).
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

// --- Helper Functions ---

#[inline(always)]
fn read_u16_le(buf: &[u8], offset: usize) -> u16 {
    u16::from_le_bytes([buf[offset], buf[offset + 1]])
}

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

/// Retrieve the current APK path by parsing /proc/self/maps
#[cfg(unix)]
fn get_apk_path_inline() -> Option<String> {
    let maps_path = xor_decode(&ENC_MAPS_PATH, K_SIG);
    let base_apk_str = xor_decode(&ENC_BASE_APK, K_SIG);

    let content = read_to_string_rustix(&maps_path)?;
    for line in content.lines() {
        if line.contains(&base_apk_str) && line.contains(" r--p ") {
            if let Some(path) = line.split_whitespace().last() {
                return Some(path.to_string());
            }
        }
    }
    None
}

/// Core APK signature verification — runs once at library load.
/// Parses EOCD -> Central Directory -> APK Signing Block -> V2 Signer -> Certificate,
/// then compares SHA-256 fingerprint against known good hashes.
#[cold]
pub fn verify_apk_signature_inline() {
    #[cfg(unix)]
    {
        let apk_path = match get_apk_path_inline() {
            Some(p) => p,
            None => {
                log::warn!("APK verify: could not locate base.apk in /proc/self/maps");
                return;
            }
        };

        let result = (|| -> Option<String> {
            let fd = openat(CWD, apk_path.as_str(), OFlags::RDONLY, Mode::empty()).ok()?;

            // Get file size via fstat
            let stat = rustix::fs::fstat(&fd).ok()?;
            let file_size = stat.st_size as u64;
            if file_size < 22 {
                return None; // Too small to be a valid ZIP
            }

            // 1. Locate EOCD — search backwards from end of file
            //    EOCD is at least 22 bytes; comment can be up to 65535 bytes
            let max_search = core::cmp::min(file_size, 22 + 65535) as usize;
            let eocd_buf = read_exact_at(&fd, file_size - max_search as u64, max_search)?;

            let eocd_sig: [u8; 4] = [0x50, 0x4b, 0x05, 0x06];
            let idx = eocd_buf.windows(4).rposition(|w| w == eocd_sig)?;

            // Validate EOCD: need at least 22 bytes from idx
            if idx + 22 > eocd_buf.len() {
                return None;
            }

            // Central Directory offset is at EOCD + 16
            let cd_offset = read_u32_le(&eocd_buf, idx + 16) as u64;
            if cd_offset < 32 || cd_offset >= file_size {
                return None;
            }

            // 2. Locate APK Signing Block — read 24-byte footer just before CD
            if cd_offset < 24 {
                return None;
            }
            let footer = read_exact_at(&fd, cd_offset - 24, 24)?;

            // Verify magic: bytes [8..24] must be "APK Sig Block 42"
            let magic = xor_decode(&ENC_SIG_MAGIC, K_SIG);
            if footer[8..24] != *magic.as_bytes() {
                return None;
            }

            let block_size_in_footer = read_u64_le(&footer, 0);
            let total_block_size = block_size_in_footer + 8; // +8 for the size field at the start
            if cd_offset < total_block_size {
                return None;
            }
            let block_start = cd_offset - total_block_size;

            // 3. Read the entire signing block
            let block_data = read_exact_at(&fd, block_start, total_block_size as usize)?;

            // Parse ID-Value pairs
            // Layout: [8-byte size_of_block] [pairs...] [8-byte size_of_block] [16-byte magic]
            let mut offset = 8usize; // skip the leading size field
            let pairs_end = (total_block_size as usize).saturating_sub(24); // before trailing size+magic
            let v2_id: u32 = 0x7109871a;
            let mut v2_data: Option<Vec<u8>> = None;

            while offset + 12 <= pairs_end {
                // Each pair: [8-byte pair_size] [4-byte id] [pair_size-4 bytes value]
                let pair_size = read_u64_le(&block_data, offset) as usize;
                if pair_size < 4 {
                    break;
                }
                offset += 8;
                if offset + pair_size > pairs_end {
                    break;
                }
                let pair_id = read_u32_le(&block_data, offset);
                if pair_id == v2_id {
                    v2_data = Some(block_data[offset + 4..offset + pair_size].to_vec());
                    break;
                }
                offset += pair_size;
            }

            // 4. Parse V2 Signer to extract the certificate
            let v2 = v2_data?;
            if v2.len() < 4 {
                return None;
            }

            // signers sequence (length-prefixed)
            let signers_seq_len = read_u32_le(&v2, 0) as usize;
            if 4 + signers_seq_len > v2.len() || signers_seq_len < 4 {
                return None;
            }
            let signers_seq = &v2[4..4 + signers_seq_len];

            // first signer (length-prefixed)
            let first_signer_len = read_u32_le(signers_seq, 0) as usize;
            if 4 + first_signer_len > signers_seq.len() {
                return None;
            }
            let signer = &signers_seq[4..4 + first_signer_len];

            // signed_data (length-prefixed)
            if signer.len() < 4 {
                return None;
            }
            let signed_data_len = read_u32_le(signer, 0) as usize;
            if 4 + signed_data_len > signer.len() {
                return None;
            }
            let signed_data = &signer[4..4 + signed_data_len];

            // Skip digests sequence to reach certificates sequence
            if signed_data.len() < 4 {
                return None;
            }
            let digests_len = read_u32_le(signed_data, 0) as usize;
            let certs_seq_start = 4 + digests_len;
            if certs_seq_start + 4 > signed_data.len() {
                return None;
            }
            let certs_seq_len = read_u32_le(signed_data, certs_seq_start) as usize;
            if certs_seq_start + 4 + certs_seq_len > signed_data.len() {
                return None;
            }
            let certs_seq = &signed_data[certs_seq_start + 4..certs_seq_start + 4 + certs_seq_len];

            // First certificate (length-prefixed)
            if certs_seq.len() < 4 {
                return None;
            }
            let cert_len = read_u32_le(certs_seq, 0) as usize;
            if 4 + cert_len > certs_seq.len() {
                return None;
            }
            let cert = &certs_seq[4..4 + cert_len];

            // 5. Calculate SHA-256 fingerprint
            let mut hasher = Sha256::new();
            hasher.update(cert);
            Some(format!("{:X}", hasher.finalize()))
        })();

        // Decrypt target hashes at runtime for comparison
        let target_hash_1 = xor_decode(&ENC_TARGET_HASH_1, K_SIG);
        let target_hash_2 = xor_decode(&ENC_TARGET_HASH_2, K_SIG);

        match result {
            Some(hash) => {
                log::info!("APK cert SHA-256: {}", hash);
                if hash == target_hash_1 || hash == target_hash_2 {
                    log::info!("APK signature verification PASSED");
                } else {
                    log::warn!("APK signature verification FAILED — hash mismatch");
                }
            }
            None => {
                log::warn!("APK signature verification FAILED — could not parse signing block");
            }
        }
    }
}
