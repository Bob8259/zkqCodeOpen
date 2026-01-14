#include <jni.h>
#include <string>
#include <sstream>
#include <iomanip>
#include <vector>
#include "monocypher/monocypher.h"
#include "utils.h"

extern "C" JNIEXPORT jstring JNICALL
generateX25519KeyPair(JNIEnv *env, jobject thiz) {
    uint8_t secret_key[32];
    uint8_t public_key[32];

    if (!get_random_bytes(secret_key, sizeof(secret_key))) {
        for (unsigned char &i : secret_key) {
            i = static_cast<unsigned char>(rand() % 256);
        }
    }

    crypto_x25519_public_key(public_key, secret_key);

    std::stringstream ss;
    ss << std::hex << std::setfill('0');

    for (unsigned char c : public_key) {
        ss << std::setw(2) << static_cast<int>(c);
    }
    ss << ",";

    for (unsigned char c : secret_key) {
        ss << std::setw(2) << static_cast<int>(c);
    }

    return env->NewStringUTF(ss.str().c_str());
}
// Helper to convert hex string to binary vector
std::vector<uint8_t> hexToBin(const std::string &hex) {
    std::vector<uint8_t> bin;
    for (size_t i = 0; i < hex.length(); i += 2) {
        std::string byteString = hex.substr(i, 2);
        uint8_t byte = (uint8_t) strtol(byteString.c_str(), nullptr, 16);
        bin.push_back(byte);
    }
    return bin;
}

// Helper to convert binary data to hex string
std::string binToHex(const uint8_t *data, size_t len) {
    std::stringstream ss;
    ss << std::hex << std::setfill('0');
    for (size_t i = 0; i < len; ++i) {
        ss << std::setw(2) << static_cast<int>(data[i]);
    }
    return ss.str();
}

extern "C" JNIEXPORT jstring JNICALL
chacha20Encrypt(JNIEnv *env, jobject thiz, jstring data, jstring key, jstring nonce) {
    const char *dataChars = env->GetStringUTFChars(data, nullptr);
    const char *keyChars = env->GetStringUTFChars(key, nullptr);
    const char *nonceChars = env->GetStringUTFChars(nonce, nullptr);

    std::vector<uint8_t> keyBin = hexToBin(keyChars);
    std::vector<uint8_t> nonceBin = hexToBin(nonceChars);
    size_t dataLen = strlen(dataChars);

    std::vector<uint8_t> ciphertext(dataLen);
    
    // IETF variant uses 12 byte nonce, 32 byte key
    crypto_chacha20_ietf(ciphertext.data(), (const uint8_t *)dataChars, dataLen, 
                         keyBin.data(), nonceBin.data(), 0);

    env->ReleaseStringUTFChars(data, dataChars);
    env->ReleaseStringUTFChars(key, keyChars);
    env->ReleaseStringUTFChars(nonce, nonceChars);

    return env->NewStringUTF(binToHex(ciphertext.data(), dataLen).c_str());
}

extern "C" JNIEXPORT jstring JNICALL
chacha20Decrypt(JNIEnv *env, jobject thiz, jstring data, jstring key, jstring nonce) {
    const char *dataChars = env->GetStringUTFChars(data, nullptr); // Hex encoded ciphertext
    const char *keyChars = env->GetStringUTFChars(key, nullptr);
    const char *nonceChars = env->GetStringUTFChars(nonce, nullptr);

    std::vector<uint8_t> dataBin = hexToBin(dataChars);
    std::vector<uint8_t> keyBin = hexToBin(keyChars);
    std::vector<uint8_t> nonceBin = hexToBin(nonceChars);

    std::vector<uint8_t> plaintext(dataBin.size());
    
    crypto_chacha20_ietf(plaintext.data(), dataBin.data(), dataBin.size(), 
                         keyBin.data(), nonceBin.data(), 0);
                         
    // Null terminate the result to treat it as a string
    std::string result((char*)plaintext.data(), plaintext.size());

    env->ReleaseStringUTFChars(data, dataChars);
    env->ReleaseStringUTFChars(key, keyChars);
    env->ReleaseStringUTFChars(nonce, nonceChars);

    return env->NewStringUTF(result.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
blake2b(JNIEnv *env, jobject thiz, jstring data) {
    const char *dataChars = env->GetStringUTFChars(data, nullptr);
    size_t dataLen = strlen(dataChars);
    
    uint8_t hash[64]; // default hash size for blake2b in monocypher is often malleable, but standard is 64
    crypto_blake2b(hash, 64, (const uint8_t *)dataChars, dataLen);
    
    env->ReleaseStringUTFChars(data, dataChars);
    
    return env->NewStringUTF(binToHex(hash, 64).c_str());
}

extern "C" JNIEXPORT jstring JNICALL
computeSharedSecret(JNIEnv *env, jobject thiz, jstring your_secret_key, jstring their_public_key) {
    const char *secretChars = env->GetStringUTFChars(your_secret_key, nullptr);
    const char *publicChars = env->GetStringUTFChars(their_public_key, nullptr);
    
    std::vector<uint8_t> secretBin = hexToBin(secretChars);
    std::vector<uint8_t> publicBin = hexToBin(publicChars);
    
    uint8_t shared_secret[32];
    crypto_x25519(shared_secret, secretBin.data(), publicBin.data());
    
    env->ReleaseStringUTFChars(your_secret_key, secretChars);
    env->ReleaseStringUTFChars(their_public_key, publicChars);
    
    return env->NewStringUTF(binToHex(shared_secret, 32).c_str());
}
