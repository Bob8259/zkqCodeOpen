#ifndef UTILS_H
#define UTILS_H

#include <vector>
#include <string>
#include <cstdint>

// Function to get random bytes from /dev/urandom
bool get_random_bytes(unsigned char *buffer, size_t size);

// Helper to convert hex string to binary vector
std::vector<uint8_t> hexToBin(const std::string &hex);

// Helper to convert binary data to hex string
std::string binToHex(const uint8_t *data, size_t len);

#endif // UTILS_H
