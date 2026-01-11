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

// Global variable to store the nonce
static std::string g_nonce;

#include "xtea_hash.h"

// Get simple hardware gene (no permission required, direct read from system file)
std::string get_hardware_gene() {
    std::string gene;
    std::ifstream ifs("/proc/cpuinfo");
    std::string line;
    if (ifs.is_open()) {
        int count = 0;
        while (std::getline(ifs, line) && count < 10) { // Read only the first 10 lines to get characteristics
            gene += line;
            count++;
        }
        ifs.close();
    }
    // If reading fails, use some invariants as a fallback
    if (gene.empty()) gene = "default_hardware_platform";
    return gene;
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

extern "C" JNIEXPORT jstring JNICALL
generateNonce(JNIEnv *env, jobject thiz) {
    // 1. Get 16 bytes of true random numbers
    unsigned char random_buf[16];
    if (!get_random_bytes(random_buf, sizeof(random_buf))) {
        // 2. If /dev/urandom fails, use C++11 standard high-quality random engine
        // std::random_device will try to call hardware random number generator
        std::random_device rd;
        std::mt19937 gen(rd()); // Mersenne Twister engine
        std::uniform_int_distribution<int> dis(0, 255);

        for (unsigned char &i: random_buf) {
            i = static_cast<unsigned char>(dis(gen));
        }
    }
    std::string random_str(reinterpret_cast<char *>(random_buf), 16);

    // 2. Get hardware gene
    std::string hardware_gene = get_hardware_gene();

    // 3. Get current millisecond timestamp (add timeliness)
    struct timeval tv{};
    gettimeofday(&tv, nullptr);
    long long timestamp = (long long) tv.tv_sec * 1000 + tv.tv_usec / 1000;
    std::string time_str = std::to_string(timestamp);

    // 4. Fusion calculation: use your private hash function to weld these three together
    // Note: A different internal salt can be added here to increase security
    std::string fused_hash = XTEA_generate_hash(random_str + hardware_gene, time_str + "1234567812345678");

    g_nonce = fused_hash; // Store in global variable for verifyHash validation
    return env->NewStringUTF(g_nonce.c_str());
}
extern "C" JNIEXPORT jdouble JNICALL
verifyHash(JNIEnv *env, jobject thiz, jstring content_before_hash, jstring server_hash) {
    const char *content_ptr = env->GetStringUTFChars(content_before_hash, nullptr);
    const char *server_hash_ptr = env->GetStringUTFChars(server_hash, nullptr);

    std::string content(content_ptr);

    // 1. Add check if nonce is consistent
    // Assuming content format is "...nonce=VALUE,..."
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

    std::string secret_key = "yJt13rhfHoeTyuronnecty5ur";

    // Use custom hash algorithm implemented in C++
    // Secret Key needs to be 16 chars for XTEA
    uint8_t key_pad[16] = {0};
    if (secret_key.length() > 16) {
        memcpy(key_pad, secret_key.c_str(), 16);
    } else {
        memcpy(key_pad, secret_key.c_str(), secret_key.length());
    }
    
    std::string calculated_hash_str = XTEA_generate_hash(content, std::string((char*)key_pad, 16));

    bool hash_match = strcasecmp(calculated_hash_str.c_str(), server_hash_ptr) == 0;

    env->ReleaseStringUTFChars(content_before_hash, content_ptr);
    env->ReleaseStringUTFChars(server_hash, server_hash_ptr);

    // 2 & 3. Return random number close to 10 or abnormal number (algebraic obfuscation)
    bool is_valid = hash_match && nonce_match;

    // Immediately clear nonce to prevent replay
    g_nonce = "";

    // Validation passed: is_valid is true (1), penalty is 0.0
    // Validation failed: is_valid is false (0), penalty is 5000.0 + random large number
    unsigned char r_buf[4];
    get_random_bytes(r_buf, 4);

    double penalty = static_cast<double>(!is_valid) *
                     (5000.0 + static_cast<double>((r_buf[0] << 16) | (r_buf[1] << 8) | r_buf[2]));

    // Random tiny offset (0.000001 to 0.000099)
    double offset = (static_cast<double>(r_buf[3]) / 255.0) * 0.000098 + 0.000001;

    return 10.0 + penalty + offset;
}
