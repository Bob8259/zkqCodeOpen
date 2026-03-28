use blake2::digest::{Update, VariableOutput};
use blake2::Blake2bVar;
use chacha20::cipher::{KeyIvInit, StreamCipher};
use chacha20::ChaCha20;
use hex;
use jni::objects::JString;
use jni::sys::jstring;
use jni::JNIEnv;
use lazy_static::lazy_static;
use rand::RngCore;
use std::sync::atomic::Ordering;
use std::sync::Mutex;
use std::time::SystemTime;
use x25519_dalek::{PublicKey, StaticSecret};

use super::keys::server_public_key_hex;
use crate::auth::ad_track::IS_AUTH_PASS;

lazy_static! {
    static ref SESSION_KEY: Mutex<Option<[u8; 32]>> = Mutex::new(None);
}

#[allow(non_snake_case)]
pub fn generateNonce(env: JNIEnv, _class: jni::objects::JClass) -> jstring {
    let mut nonce = [0u8; 16];
    rand::thread_rng().fill_bytes(&mut nonce);
    let result = hex::encode(nonce);
    env.new_string(result).unwrap().into_raw()
}

#[allow(non_snake_case)]
pub fn encryptLoginPayload(
    mut env: JNIEnv,
    _class: jni::objects::JClass,
    payload: JString,
) -> jstring {
    let payload_str: String = env
        .get_string(&payload)
        .expect("Couldn't get payload")
        .into();

    // Read server public key directly from hardcoded constant
    let server_pub_bin = match hex::decode(server_public_key_hex()) {
        Ok(bin) => bin,
        Err(_) => {
            return env
                .new_string("Error: Invalid server key hex")
                .unwrap()
                .into_raw();
        }
    };
    if server_pub_bin.len() != 32 {
        return env.new_string("Error: Invalid key").unwrap().into_raw();
    }

    // 1. Generate ephemeral key pair
    let mut my_secret_bytes = [0u8; 32];
    rand::thread_rng().fill_bytes(&mut my_secret_bytes);
    let my_secret = StaticSecret::from(my_secret_bytes);
    let my_public = PublicKey::from(&my_secret);

    // 2. Compute shared secret
    let mut server_pub_bytes = [0u8; 32];
    server_pub_bytes.copy_from_slice(&server_pub_bin);
    let server_pub = PublicKey::from(server_pub_bytes);
    let shared_secret = my_secret.diffie_hellman(&server_pub);

    // 3. Derive session key via Blake2b (32 bytes output)
    let mut hasher = Blake2bVar::new(32).expect("Invalid Blake2b output size");
    hasher.update(shared_secret.as_bytes());
    let mut session_key = [0u8; 32];
    hasher
        .finalize_variable(&mut session_key)
        .expect("Blake2b finalize failed");

    // Store session key
    {
        let mut key_lock = SESSION_KEY.lock().unwrap();
        *key_lock = Some(session_key);
    }

    // 4. Encrypt payload
    let mut nonce = [0u8; 12];
    rand::thread_rng().fill_bytes(&mut nonce);
    let mut cipher = ChaCha20::new(&session_key.into(), &nonce.into());
    let mut buffer = payload_str.into_bytes();
    cipher.apply_keystream(&mut buffer);

    // 5. Format result: public_hex,nonce_hex,cipher_hex
    let result = format!(
        "{},{},{}",
        hex::encode(my_public.as_bytes()),
        hex::encode(nonce),
        hex::encode(buffer)
    );
    env.new_string(result).unwrap().into_raw()
}

// XOR-obfuscated "gem_not_enough" to avoid plain-text exposure in the binary
const _GNE_KEY: u8 = 0x5A;
const _GNE_ENC: [u8; 14] = [
    0x3D, 0x3F, 0x37, 0x05, 0x34, 0x35, 0x2E, 0x05,
    0x3F, 0x34, 0x35, 0x2F, 0x3D, 0x32,
];

#[inline(always)]
fn _decode_gne() -> String {
    _GNE_ENC.iter().map(|b| (b ^ _GNE_KEY) as char).collect()
}

#[allow(non_snake_case)]
pub fn decryptLoginResponse(
    mut env: JNIEnv,
    _class: jni::objects::JClass,
    encrypted_response: JString,
) -> jstring {
    let resp_str: String = env
        .get_string(&encrypted_response)
        .expect("Couldn't get response")
        .into();

    let session_key = {
        let key_lock = SESSION_KEY.lock().unwrap();
        match *key_lock {
            Some(key) => key,
            None => {
                return env
                    .new_string("Error: Session key not active")
                    .unwrap()
                    .into_raw();
            }
        }
    };

    // Parse format: nonce=...&data=...
    let mut nonce_hex = "";
    let mut data_hex = "";

    for part in resp_str.split('&') {
        if let Some(val) = part.strip_prefix("nonce=") {
            nonce_hex = val;
        } else if let Some(val) = part.strip_prefix("data=") {
            data_hex = val;
        }
    }

    if nonce_hex.is_empty() || data_hex.is_empty() {
        return env
            .new_string("Error: Invalid response format")
            .unwrap()
            .into_raw();
    }

    let nonce_bin = match hex::decode(nonce_hex) {
        Ok(bin) => bin,
        Err(_) => {
            return env
                .new_string("Error: Invalid nonce hex")
                .unwrap()
                .into_raw();
        }
    };
    let data_bin = match hex::decode(data_hex) {
        Ok(bin) => bin,
        Err(_) => {
            return env
                .new_string("Error: Invalid data hex")
                .unwrap()
                .into_raw();
        }
    };

    if nonce_bin.len() != 12 {
        return env
            .new_string("Error: Invalid nonce length")
            .unwrap()
            .into_raw();
    }

    let mut cipher = ChaCha20::new(&session_key.into(), nonce_bin.as_slice().into());
    let mut buffer = data_bin;
    cipher.apply_keystream(&mut buffer);

    match String::from_utf8(buffer) {
        Ok(result) => {
            // If the decrypted result is a valid timestamp within 150s of now, mark ad-auth as passed
            if let Ok(ts) = result.trim().parse::<u64>() {
                if let Ok(elapsed) = SystemTime::now().duration_since(SystemTime::UNIX_EPOCH) {
                    let now = elapsed.as_secs();
                    if now.abs_diff(ts) < 150 {
                        IS_AUTH_PASS.store(true, Ordering::SeqCst);
                    }
                }
            } else if result.contains(&_decode_gne()) {
                // Server indicated insufficient gems; revoke auth
                IS_AUTH_PASS.store(false, Ordering::SeqCst);
            }
            env.new_string(result).unwrap().into_raw()
        }
        Err(_) => env.new_string("Error: Invalid UTF-8").unwrap().into_raw(),
    }
}
