use blake2::{Blake2b512, Digest};
use chacha20::cipher::{KeyIvInit, StreamCipher};
use chacha20::ChaCha20;
use hex;
use jni::objects::{JByteArray, JClass, JString};
use jni::sys::{jbyteArray, jstring};
use jni::JNIEnv;
use rand::RngCore;
use x25519_dalek::{PublicKey, StaticSecret};

use super::keys::key;

//You need to create a key.rs at the same level as this file and add the following code:

// use obfstr::obfstr;

//  ChaCha20 encryption key (hex-encoded), obfuscated at compile time
// #[inline(always)]
// pub fn key() -> String {
//     obfstr!("758xxxxx Your Own ChaCha20 Key").to_string()
// }

// Server public key for login encryption (X25519), obfuscated at compile time
// #[inline(always)]
// pub fn server_public_key_hex() -> String {
//     obfstr!("aacxxxx Your Own Server Public Key").to_string()
// }


// Decode the hex-encoded ChaCha20 key at runtime
#[inline(always)]
fn get_runtime_key() -> [u8; 32] {
    let decoded = hex::decode(key()).expect("Invalid hex in KEY");
    let mut key = [0u8; 32];
    key.copy_from_slice(&decoded);
    key
}

#[allow(non_snake_case)]
pub fn generateX25519KeyPair(env: JNIEnv, _class: JClass) -> jstring {
    let mut secret_key = [0u8; 32];
    rand::thread_rng().fill_bytes(&mut secret_key);

    let secret = StaticSecret::from(secret_key);
    let public = PublicKey::from(&secret);

    let result = format!(
        "{},{}",
        hex::encode(public.as_bytes()),
        hex::encode(secret_key)
    );

    env.new_string(result).unwrap().into_raw()
}

#[allow(non_snake_case)]
pub fn chacha20Encrypt(mut env: JNIEnv, _class: JClass, data: JString, nonce: JString) -> jstring {
    let data_str: String = env
        .get_string(&data)
        .expect("Couldn't get data string")
        .into();
    let nonce_hex: String = env
        .get_string(&nonce)
        .expect("Couldn't get nonce string")
        .into();
    let nonce_bin = hex::decode(nonce_hex).expect("Invalid hex in nonce");

    let key = get_runtime_key();
    let mut cipher = ChaCha20::new(&key.into(), nonce_bin.as_slice().into());

    let mut buffer = data_str.into_bytes();
    cipher.apply_keystream(&mut buffer);

    let result = hex::encode(buffer);
    env.new_string(result).unwrap().into_raw()
}

#[allow(non_snake_case)]
pub fn chacha20Decrypt(mut env: JNIEnv, _class: JClass, data: JString, nonce: JString) -> jstring {
    let data_hex: String = env
        .get_string(&data)
        .expect("Couldn't get data string")
        .into();
    let data_bin = hex::decode(data_hex).expect("Invalid hex in data");

    let nonce_hex: String = env
        .get_string(&nonce)
        .expect("Couldn't get nonce string")
        .into();
    let nonce_bin = hex::decode(nonce_hex).expect("Invalid hex in nonce");

    let key = get_runtime_key();
    let mut cipher = ChaCha20::new(&key.into(), nonce_bin.as_slice().into());

    let mut buffer = data_bin;
    cipher.apply_keystream(&mut buffer);

    let result = String::from_utf8(buffer).expect("Invalid UTF-8 in decrypted data");
    env.new_string(result).unwrap().into_raw()
}

pub fn blake2b(mut env: JNIEnv, _class: JClass, data: JString) -> jstring {
    let data_str: String = env
        .get_string(&data)
        .expect("Couldn't get data string")
        .into();

    let mut hasher = Blake2b512::new();
    hasher.update(data_str.as_bytes());
    let res = hasher.finalize();

    let result = hex::encode(res);
    env.new_string(result).unwrap().into_raw()
}

#[allow(non_snake_case)]
#[cold]
pub fn decryptJar(env: JNIEnv, _class: JClass, data: JByteArray) -> jbyteArray {
    let data_bytes = env
        .convert_byte_array(&data)
        .expect("Couldn't convert byte array");
    if data_bytes.len() <= 12 {
        return std::ptr::null_mut();
    }

    let nonce = &data_bytes[0..12];
    let ciphertext = &data_bytes[12..];

    let key = get_runtime_key();
    let mut cipher = ChaCha20::new(&key.into(), nonce.into());

    let mut plaintext = ciphertext.to_vec();
    cipher.apply_keystream(&mut plaintext);

    let result = env
        .byte_array_from_slice(&plaintext)
        .expect("Couldn't create byte array");
    result.into_raw()
}

#[allow(non_snake_case)]
pub fn computeSharedSecret(
    mut env: JNIEnv,
    _class: JClass,
    your_secret_key: JString,
    their_public_key: JString,
) -> jstring {
    let secret_hex: String = env
        .get_string(&your_secret_key)
        .expect("Couldn't get secret key string")
        .into();
    let public_hex: String = env
        .get_string(&their_public_key)
        .expect("Couldn't get public key string")
        .into();

    let secret_bin = hex::decode(secret_hex).expect("Invalid hex in secret key");
    let public_bin = hex::decode(public_hex).expect("Invalid hex in public key");

    let mut secret_bytes = [0u8; 32];
    secret_bytes.copy_from_slice(&secret_bin);
    let secret = StaticSecret::from(secret_bytes);

    let mut public_bytes = [0u8; 32];
    public_bytes.copy_from_slice(&public_bin);
    let public = PublicKey::from(public_bytes);

    let shared = secret.diffie_hellman(&public);

    let result = hex::encode(shared.as_bytes());
    env.new_string(result).unwrap().into_raw()
}
