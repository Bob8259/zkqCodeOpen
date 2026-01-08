#include <jni.h>
#include <string>
#include <vector>
#include <iomanip>
#include <sstream>
#include <android/log.h>
#include <fcntl.h>
#include <unistd.h>
#include <random>

// Global variable to store the nonce
static std::string g_nonce;

/**
 * 私有加固哈希算法 - Native 版
 * 必须与服务器逻辑像素级对应
 */
uint64_t calculate_secure_hash(const std::string &data, const std::string &secret) {
    std::string input = data + secret;

    // 必须使用相同的私有常数
    uint64_t h = 0x6C61707572706C65ULL;
    const uint64_t prime = 0x100000001B3ULL;
    for (unsigned char c: input) {
        // 1. 异或
        h ^= (uint64_t) c;
        // 2. 乘法
        h *= prime;
        // 3. 循环左移 13 位
        h = (h << 13) | (h >> (64 - 13));
        // 4. 异或折叠
        h ^= (h >> 33);
    }
    return h;
}

// Function to get random bytes from /dev/urandom
bool get_random_bytes(unsigned char *buffer, size_t size) {
    int fd = open("/dev/urandom", O_RDONLY);
    if (fd != -1) {
        ssize_t res = read(fd, buffer, size);
        close(fd);
        return res == (ssize_t) size;
    }
    return false;
}

// Function to generate a random nonce using /dev/urandom
extern "C" JNIEXPORT jstring JNICALL
generateNonce(JNIEnv *env, jobject thiz) {
    unsigned char buffer[16];
    if (!get_random_bytes(buffer, sizeof(buffer))) {
        // Fallback if /dev/urandom is not available
        std::random_device rd;
        std::mt19937 gen(rd());
        std::uniform_int_distribution<> dis(0, 255);
        for (unsigned char &i: buffer) {
            i = static_cast<unsigned char>(dis(gen));
        }
    }

    std::stringstream ss;
    for (unsigned char i: buffer) {
        ss << std::hex << std::setw(2) << std::setfill('0') << static_cast<int>(i);
    }

    g_nonce = ss.str();
    return env->NewStringUTF(g_nonce.c_str());
}

extern "C" JNIEXPORT jdouble JNICALL
verifyHash(JNIEnv *env, jobject thiz, jstring content_before_hash, jstring server_hash) {
    const char *content_ptr = env->GetStringUTFChars(content_before_hash, nullptr);
    const char *server_hash_ptr = env->GetStringUTFChars(server_hash, nullptr);

    std::string content(content_ptr);

    // 1. 增加检测 nonce 是否一致
    // 假设 content 格式为 "...nonce=VALUE,..."
    std::string nonce_marker = "nonce=";
    size_t pos = content.find(nonce_marker);
    bool nonce_match = false;
    if (pos != std::string::npos) {
        size_t start = pos + nonce_marker.length();
        size_t end = content.find(',', start);
        std::string extracted_nonce = (end == std::string::npos) ? content.substr(start)
                                                                 : content.substr(start,
                                                                                  end - start);

        if (extracted_nonce == g_nonce && !g_nonce.empty()) {
            nonce_match = true;
        }
    }

    std::string secret_key = "yur13Jty5rhfHoeTyur";

    // 使用 C++ 实现的自定义哈希算法
    uint64_t calculated_h = calculate_secure_hash(content, secret_key);

    // 转换为 16 进制字符串
    char buf[17];
    snprintf(buf, sizeof(buf), "%016llx", (unsigned long long) calculated_h);
    std::string calculated_hash_str(buf);

    bool hash_match = strcasecmp(calculated_hash_str.c_str(), server_hash_ptr) == 0;

    env->ReleaseStringUTFChars(content_before_hash, content_ptr);
    env->ReleaseStringUTFChars(server_hash, server_hash_ptr);

    // 2 & 3. 返回接近 10 的随机数字或异常数字 (代数法混淆)
    bool is_valid = hash_match && nonce_match;

    // 立即清空 nonce 防止重放
    g_nonce = "";

    // 验证通过：is_valid 为 true (1)，penalty 为 0.0
    // 验证失败：is_valid 为 false (0)，penalty 为 5000.0 + 随机大数
    unsigned char r_buf[4];
    get_random_bytes(r_buf, 4);

    double penalty = static_cast<double>(!is_valid) *
                     (5000.0 + static_cast<double>((r_buf[0] << 16) | (r_buf[1] << 8) | r_buf[2]));

    // 随机微小偏移 (0.000001 到 0.000099)
    double offset = (static_cast<double>(r_buf[3]) / 255.0) * 0.000098 + 0.000001;

    return 10.0 + penalty + offset;
}
