#ifndef ENCRYPT_H
#define ENCRYPT_H

#include <string>
#include <vector>
#include <cstdint>

// 强制内联
#if defined(_MSC_VER)
#define FORCE_INLINE __forceinline
#else
#define FORCE_INLINE __attribute__((always_inline)) inline
#endif

class StealthXTEA {
private:
    static FORCE_INLINE uint32_t GetDelta() {
        uint32_t delta = 0;

        // 把 0x9E3779B9 拆成 4 个字节：B9, 79, 37, 9E
        // 我们定义一个字节数组，并在里面混入垃圾数据
        // 真实数据索引: 0, 2, 4, 6
        volatile uint8_t raw[] = {
                0xB9, 0x00, // 0: B9 (真实), 1: 垃圾
                0x79, 0xFF, // 2: 79 (真实), 3: 垃圾
                0x37, 0xAB, // 4: 37 (真实), 5: 垃圾
                0x9E, 0xCC  // 6: 9E (真实), 7: 垃圾
        };

        // 用一个看起来很蠢的循环来还原
        for (int i = 0; i < 4; i++) {
            // 取出偶数位的真实数据，移位放到 delta 里
            // raw[i*2] 这种写法会让编译器很难预测
            delta |= ((uint32_t) raw[i * 2] << (i * 8));
        }

        return delta;
    }

public:
    static void encrypt(std::vector<uint8_t> &data, const uint32_t key[4]);

    static void decrypt(std::vector<uint8_t> &data, const uint32_t key[4]);
};

std::string XTEA_encrypt(const std::string &plainText, const std::string &key);

std::string XTEA_decrypt(const std::string &cipherText, const std::string &key);

#endif
