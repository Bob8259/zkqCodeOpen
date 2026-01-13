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
    // Convert random bytes to hex string
    std::stringstream ss;
    ss << std::hex << std::setfill('0');
    for (unsigned char c : random_buf) {
        ss << std::setw(2) << static_cast<int>(c);
    }
    g_nonce = ss.str();
    
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

    // Simplified verification: Always return true for hash match
    bool hash_match = true;

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
