#include <jni.h>
#include <string>
#include <vector>
#include <iomanip>
#include <sstream>
#include <android/log.h>
#include <fcntl.h>
#include <unistd.h>
#include <random>
#include <sys/time.h>
#include <fstream>
#include <cmath> // Added for junk math

#include "monocypher/monocypher.h"
#include "utils.h"

// --- OBFUSCATION GLOBALS ---
// 'volatile' tells the compiler: "Don't delete this, it might change outside your control."
static volatile int g_chaos_state = 0xDEADBEEF;
static std::vector<uint8_t> g_session_key;

// A junk function that looks like a checksum but does nothing useful
// Returns a value based on time to make debugging inconsistent
int perform_entropy_churn(const uint8_t* data, size_t len) {
    volatile int accumulator = g_chaos_state;
    struct timeval tv;
    gettimeofday(&tv, NULL);

    for(size_t i = 0; i < len; i++) {
        // Complex looking math that wastes CPU cycles
        accumulator = (accumulator << 3) ^ (data[i] + tv.tv_usec);
        if (accumulator % 2 == 0) {
            accumulator += 1;
        }
    }
    return accumulator;
}

bool get_random_bytes(unsigned char *buffer, size_t size) {
    int fd = open("/dev/urandom", O_RDONLY);
    if (fd != -1) {
        ssize_t res = read(fd, buffer, size);
        close(fd);
        return res == (ssize_t) size;
    }
    return false;
}

extern "C" JNIEXPORT jstring JNICALL
generateNonce(JNIEnv *env, jobject thiz) {
    unsigned char random_buf[16];

    // OBFUSCATION: Initialize buffer with junk first
    for(int i=0; i<16; i++) random_buf[i] = (uint8_t)g_chaos_state;

    if (!get_random_bytes(random_buf, sizeof(random_buf))) {
        std::random_device rd;
        std::mt19937 gen(rd());
        std::uniform_int_distribution<int> dis(0, 255);
        for (unsigned char &i: random_buf) {
            i = static_cast<unsigned char>(dis(gen));
        }
    }

    std::stringstream ss;
    ss << std::hex << std::setfill('0');
    for (unsigned char c: random_buf) {
        ss << std::setw(2) << static_cast<int>(c);
    }

    // OBFUSCATION: Useless state update
    g_chaos_state++;

    return env->NewStringUTF(ss.str().c_str());
}

extern "C" JNIEXPORT jstring JNICALL
encryptLoginPayload(JNIEnv *env, jobject thiz, jstring payload, jstring server_public_key_hex) {
    const char *payload_ptr = env->GetStringUTFChars(payload, nullptr);
    const char *server_pub_ptr = env->GetStringUTFChars(server_public_key_hex, nullptr);

    if (!payload_ptr || !server_pub_ptr) return nullptr;

    std::string payload_str(payload_ptr);
    std::string server_pub_str(server_pub_ptr);

    env->ReleaseStringUTFChars(payload, payload_ptr);
    env->ReleaseStringUTFChars(server_public_key_hex, server_pub_ptr);

    // --- Execution Flow ---

    uint8_t my_secret[32];
    uint8_t my_public[32];

    // OBFUSCATION: False dependency. Create a dummy buffer that looks critical.
    uint8_t ghost_buffer[32];

    bool random_success = get_random_bytes(my_secret, 32);
    if (!random_success) {
        for (int i = 0; i < 32; i++) my_secret[i] = rand() % 256;
    }

    // OBFUSCATION: Mix the secret into the ghost buffer.
    // A reverse engineer sees this loop and thinks ghost_buffer is the key.
    for(int i=0; i<32; i++) {
        ghost_buffer[i] = my_secret[i] ^ 0xAA;
    }

    crypto_x25519_public_key(my_public, my_secret);

    std::vector<uint8_t> server_pub_bin = hexToBin(server_pub_str.c_str());
    if (server_pub_bin.size() != 32) return env->NewStringUTF("Error: Invalid key");

    uint8_t shared_secret[32];
    crypto_x25519(shared_secret, my_secret, server_pub_bin.data());

    // OBFUSCATION: "Opaque Predicate"
    // This looks like a validity check on the shared secret, but actually does nothing important.
    // If the chaos state is non-zero (always true), we do a useless calculation.
    if (g_chaos_state != 0) {
        volatile int check = perform_entropy_churn(shared_secret, 32);
        // This line confuses decompilers about data flow
        if (check == 0x123456) g_chaos_state = check;
    }

    g_session_key.resize(32);
    crypto_blake2b(g_session_key.data(), 32, shared_secret, 32);

    uint8_t nonce[12];
    get_random_bytes(nonce, 12);

    std::vector<uint8_t> ciphertext(payload_str.length());

    // OBFUSCATION: Red Herring encryption
    // We encrypt a dummy buffer. A reverser stepping through code might follow this instead of the real one.
    std::vector<uint8_t> dummy_cipher(16);
    crypto_chacha20_ietf(dummy_cipher.data(), ghost_buffer, 16, g_session_key.data(), nonce, 1);

    // Real Encryption
    crypto_chacha20_ietf(ciphertext.data(),
                         (const uint8_t *) payload_str.c_str(),
                         payload_str.length(),
                         g_session_key.data(),
                         nonce,
                         0);

    std::string my_pub_hex = binToHex(my_public, 32);
    std::string nonce_hex = binToHex(nonce, 12);
    std::string cipher_hex = binToHex(ciphertext.data(), ciphertext.size());

    // OBFUSCATION: Append a "checksum" to the result that is actually ignored by the server?
    // No, keep format clean, but we can mutate local vars.
    g_chaos_state += ciphertext.size();

    std::string result = my_pub_hex + "," + nonce_hex + "," + cipher_hex;
    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
decryptLoginResponse(JNIEnv *env, jobject thiz, jstring encrypted_response) {
    const char *resp_chars = env->GetStringUTFChars(encrypted_response, nullptr);
    if (!resp_chars) return nullptr;

    std::string resp_str(resp_chars);
    env->ReleaseStringUTFChars(encrypted_response, resp_chars);

    if (g_session_key.empty()) return env->NewStringUTF("Error: Session key not active");

    // OBFUSCATION: String parsing decoy
    // We look for a fake tag called "signature=" which doesn't exist.
    // This wastes the analyst's time verifying if the signature logic is mandatory.
    if (resp_str.find("signature=") != std::string::npos) {
        g_chaos_state = 0; // Branch never taken
    }

    std::string nonce_marker = "nonce=";
    std::string data_marker = "data=";

    size_t nonce_pos = resp_str.find(nonce_marker);
    size_t data_pos = resp_str.find(data_marker);

    if (nonce_pos == std::string::npos || data_pos == std::string::npos) {
        return env->NewStringUTF("Error: Invalid response format");
    }

    size_t nonce_start = nonce_pos + nonce_marker.length();
    size_t nonce_end = resp_str.find('&', nonce_start);
    if (nonce_end == std::string::npos) nonce_end = resp_str.length();

    size_t data_start = data_pos + data_marker.length();
    size_t data_end = resp_str.find('&', data_start);
    if (data_end == std::string::npos) data_end = resp_str.length();

    std::string nonce_hex = resp_str.substr(nonce_start, nonce_end - nonce_start);
    std::string data_hex = resp_str.substr(data_start, data_end - data_start);

    std::vector<uint8_t> nonce_bytes = hexToBin(nonce_hex.c_str());
    std::vector<uint8_t> data_bytes = hexToBin(data_hex.c_str());

    if (nonce_bytes.size() != 12) return env->NewStringUTF("Error: Invalid nonce length");
    if (data_bytes.empty()) return env->NewStringUTF("");

    // OBFUSCATION: Fake integrity check loop
    // Iterate over data_bytes performing an XOR check that is never used.
    volatile uint8_t integrity_check = 0;
    for(auto b : data_bytes) {
        integrity_check ^= b;
    }
    // We conditionally branch on a volatile variable so the compiler keeps the loop above.
    if (integrity_check == 0x42 && g_chaos_state == 0xFFFFFFFF) {
        return nullptr; // Impossible condition
    }

    std::vector<uint8_t> plaintext(data_bytes.size());
    crypto_chacha20_ietf(plaintext.data(),
                         data_bytes.data(),
                         data_bytes.size(),
                         g_session_key.data(),
                         nonce_bytes.data(),
                         0);

    // OBFUSCATION: Memory scramble
    // After decryption, we "clean up" the data_bytes by filling them with junk.
    // This looks like a security feature but is just noise.
    for(size_t i=0; i<data_bytes.size(); i++) data_bytes[i] = (uint8_t)(i % 255);

    std::string result(plaintext.begin(), plaintext.end());
    return env->NewStringUTF(result.c_str());
}