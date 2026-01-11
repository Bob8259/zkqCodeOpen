#ifndef XTEA_HASH_H
#define XTEA_HASH_H

#include <string>

std::string XTEA_generate_hash(const std::string& inputData, const std::string& key);

#endif // XTEA_HASH_H
