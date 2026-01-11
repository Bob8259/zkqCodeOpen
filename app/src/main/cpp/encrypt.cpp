#include "encrypt.h"
#include <cstring>
#include <sstream>
#include <iomanip>
#include <stdexcept>
#include <vector>

void StealthXTEA::encrypt(std::vector<uint8_t>& data, const uint32_t key[4]) {
    size_t len = data.size();
    uint32_t delta = GetDelta();

    for (size_t i = 0; i < len / 8 * 8; i += 8) {
        uint32_t* v = (uint32_t*)&data[i];
        uint32_t v0 = v[0];
        uint32_t v1 = v[1];
        uint32_t sum = 0;

        for (int j = 0; j < 8; j++) { 
            v0 += (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);
            sum += delta;
            v1 += (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);

            v0 += (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);
            sum += delta;
            v1 += (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);

            v0 += (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);
            sum += delta;
            v1 += (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);

            v0 += (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);
            sum += delta;
            v1 += (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);
        }
        
        v[0] = v0;
        v[1] = v1;
    }
}

void StealthXTEA::decrypt(std::vector<uint8_t>& data, const uint32_t key[4]) {
    size_t len = data.size();
    uint32_t delta = GetDelta();

    for (size_t i = 0; i < len / 8 * 8; i += 8) {
        uint32_t* v = (uint32_t*)&data[i];
        uint32_t v0 = v[0];
        uint32_t v1 = v[1];
        
        uint32_t sum = delta * 32; 

        for (int j = 0; j < 8; j++) {
            v1 -= (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);
            sum -= delta;
            v0 -= (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);
            
            v1 -= (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);
            sum -= delta;
            v0 -= (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);

            v1 -= (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);
            sum -= delta;
            v0 -= (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);

            v1 -= (((v0 << 4) ^ (v0 >> 5)) + v0) ^ (sum + key[(sum >> 11) & 3]);
            sum -= delta;
            v0 -= (((v1 << 4) ^ (v1 >> 5)) + v1) ^ (sum + key[sum & 3]);
        }
        v[0] = v0;
        v[1] = v1;
    }
}

static std::vector<uint8_t> hexToBytes(const std::string& hex) {
    std::vector<uint8_t> bytes;
    for (unsigned int i = 0; i < hex.length(); i += 2) {
        std::string byteString = hex.substr(i, 2);
        uint8_t byte = (uint8_t)strtol(byteString.c_str(), nullptr, 16);
        bytes.push_back(byte);
    }
    return bytes;
}

static std::string bytesToHex(const std::vector<uint8_t>& bytes) {
    std::stringstream ss;
    ss << std::hex << std::setfill('0');
    for (uint8_t b : bytes) {
        ss << std::setw(2) << (int)b;
    }
    return ss.str();
}

std::string XTEA_encrypt(const std::string& plainText, const std::string& key) {
    if (key.length() < 16) {
        return ""; // Key must be at least 16 bytes
    }

    uint32_t k[4];
    memcpy(k, key.c_str(), 16);

    std::vector<uint8_t> data(plainText.begin(), plainText.end());
    
    // PKCS#7 Padding for 8-byte blocks
    uint8_t padding = 8 - (data.size() % 8);
    for (int i = 0; i < padding; ++i) {
        data.push_back(padding);
    }

    StealthXTEA::encrypt(data, k);

    return bytesToHex(data);
}

std::string XTEA_decrypt(const std::string& cipherText, const std::string& key) {
    if (key.length() < 16) {
        return ""; 
    }
    if (cipherText.length() % 16 != 0) {
        return ""; // XTEA blocks are 8 bytes, so hex should be multiple of 16
    }

    uint32_t k[4];
    memcpy(k, key.c_str(), 16);

    std::vector<uint8_t> data = hexToBytes(cipherText);
    StealthXTEA::decrypt(data, k);

    // PKCS#7 Unpadding
    if (!data.empty()) {
        uint8_t padding = data.back();
        if (padding > 0 && padding <= 8 && padding <= data.size()) {
             bool valid = true;
             for (size_t i = data.size() - padding; i < data.size(); ++i) {
                 if (data[i] != padding) {
                     valid = false;
                     break;
                 }
             }
             if (valid) {
                 data.resize(data.size() - padding);
             }
        }
    }

    return std::string(data.begin(), data.end());
}
