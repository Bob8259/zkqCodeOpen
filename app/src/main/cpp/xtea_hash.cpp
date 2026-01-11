#include "xtea_hash.h"
#include "encrypt.h"
#include <vector>
#include <cstring>
#include <sstream>
#include <iomanip>

// Helper function to convert bytes to hex string
static std::string bytesToHex(const std::vector<uint8_t>& data) {
    std::stringstream ss;
    ss << std::hex << std::setfill('0');
    for (uint8_t b : data) {
        ss << std::setw(2) << (int)b;
    }
    return ss.str();
}

std::string XTEA_generate_hash(const std::string& inputData, const std::string& key) {
    if (key.length() < 16) return "";

    // 1. 准备密钥
    uint32_t k[4];
    memcpy(k, key.c_str(), 16);

    // 2. 准备数据 (转成 vector)
    std::vector<uint8_t> data(inputData.begin(), inputData.end());

    // 3. 初始化哈希桶 (8字节，因为 XTEA 块大小是 8)
    // 我们可以给它一个初始值 (Magic Number)，增加破解难度
    std::vector<uint8_t> hashState = {0xDE, 0xAD, 0xBE, 0xEF, 0xCA, 0xFE, 0xBA, 0xBE};

    // 4. 开始循环处理数据
    // 每次处理 8 字节
    for (size_t i = 0; i < data.size(); ++i) {
        // A. 混合：把当前数据字节 异或 进哈希桶
        // i % 8 让数据循环填入桶的 0-7 位置
        hashState[i % 8] ^= data[i];

        // B. 关键点：每填满 8 字节（或者处理完最后一段），就加密一次
        // 这会让之前的微小变化在整个桶里“剧烈爆炸” (雪崩效应)
        if ((i + 1) % 8 == 0 || i == data.size() - 1) {
            StealthXTEA::encrypt(hashState, k); // 原地修改 hashState
        }
    }

    // 5. 返回结果 (Hex字符串)
    return bytesToHex(hashState);
}
