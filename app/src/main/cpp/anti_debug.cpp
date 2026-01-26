#include <unistd.h>
#include <pthread.h>
#include <cstdlib>
#include <ctime>
#include <sys/ptrace.h>
#include <sys/types.h>
#include <sys/syscall.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unwind.h>
#include <dlfcn.h>
#include <cstring>
#include <cstdio>
#include <cerrno>
#include <random>

// Global shared variable to poison calculations if checks fail
// In production, this should be used in critical business logic
volatile int g_security_poison_flag = 0;

/**
 * Syscall number definitions for ARM64, x86_64, ARM32, and x86
 */
#if defined(__aarch64__)
#define MY_NR_openat  56
#define MY_NR_read    63
#define MY_NR_close   57
#define MY_NR_ptrace  117
#elif defined(__x86_64__)
#define MY_NR_openat  257
#define MY_NR_read    0
#define MY_NR_close   3
#define MY_NR_ptrace  101
#elif defined(__arm__)
#define MY_NR_openat  322
#define MY_NR_read    3
#define MY_NR_close   6
#define MY_NR_ptrace  26
#elif defined(__i386__)
#define MY_NR_openat  295
#define MY_NR_read    3
#define MY_NR_close   6
#define MY_NR_ptrace  26
#else
// Fallback to system headers if unknown
#define MY_NR_openat  __NR_openat
#define MY_NR_read    __NR_read
#define MY_NR_close   __NR_close
#define MY_NR_ptrace  __NR_ptrace
#endif

/**
 * Comprehensive Syscall wrapper.
 * Includes basic error handling logic.
 */
static inline long my_syscall3(long number, long arg1, long arg2, long arg3) {
    long result;
#if defined(__aarch64__)
    register long x8 __asm__("x8") = number;
    register long x0 __asm__("x0") = arg1;
    register long x1 __asm__("x1") = arg2;
    register long x2 __asm__("x2") = arg3;

    __asm__ __volatile__ (
        "svc #0"
        : "=r"(x0)
        : "r"(x8), "r"(x0), "r"(x1), "r"(x2)
        : "memory"
    );
    result = x0;
#elif defined(__x86_64__)
    register long rax __asm__("rax") = number;
    register long rdi __asm__("rdi") = arg1;
    register long rsi __asm__("rsi") = arg2;
    register long rdx __asm__("rdx") = arg3;

    __asm__ __volatile__ (
        "syscall"
        : "=a"(rax)
        : "a"(rax), "D"(rdi), "S"(rsi), "d"(rdx)
        : "rcx", "r11", "memory"
    );
    result = rax;
#else
    // For 32-bit architectures or unknown, fallback to standard syscall
    result = syscall(number, arg1, arg2, arg3);
#endif
    return result;
}

/**
 * Poison trigger: Sets the global poison flag if it's currently 0.
 */
/**
 * Poison trigger: Sets the global poison flag if it's currently 0.
 * Uses C++11 <random> to avoid weak randomness warnings.
 */
static void trigger_poison() {
    if (g_security_poison_flag == 0) {
        std::random_device rd;
        std::mt19937 gen(rd());
        std::uniform_int_distribution<> dis(1, 100);
        g_security_poison_flag = dis(gen);
    }
}

/**
 * Detection Implementations
 */

// 1. Ptrace Anti-Debug
void check_ptrace() {
    // Attempt PTRACE_TRACEME via syscall
    long ret = my_syscall3(MY_NR_ptrace, PTRACE_TRACEME, 0, 0);
    if (ret < 0) {
        // Fallback or explicit check
        if (ptrace(PTRACE_TRACEME, 0, 1, 0) < 0) {
            trigger_poison();
            return;
        }
    }
    ptrace(PTRACE_DETACH, 0, 1, 0);
}

// 2. LD_PRELOAD Injection Check
void check_env_injection() {
    const char *ld_preload = getenv("LD_PRELOAD");
    if (ld_preload != nullptr) {
        trigger_poison();
    }
}

// 3. Frida Memory Scan (/proc/self/maps)
void check_frida_memory() {
    char line[512];
    bool detected = false;

    // Try Syscall Open
    long fd = my_syscall3(MY_NR_openat, -100, (long) "/proc/self/maps", 0);
    if (fd >= 0) {
        char buf[4096];
        long bytes;
        while ((bytes = my_syscall3(MY_NR_read, fd, (long) buf, sizeof(buf) - 1)) > 0) {
            buf[bytes] = '\0';
            // Production Tip: Obfuscate "frida", "gadget", "gum-js" strings
            if (strstr(buf, "frida") || strstr(buf, "gadget") || strstr(buf, "gum-js")) {
                detected = true;
                break;
            }
        }
        my_syscall3(MY_NR_close, fd, 0, 0);
    } else {
        // Fallback to fopen
        FILE *fp = fopen("/proc/self/maps", "r");
        if (fp) {
            while (fgets(line, sizeof(line), fp)) {
                if (strstr(line, "frida") || strstr(line, "gadget") || strstr(line, "gum-js")) {
                    detected = true;
                    break;
                }
            }
            fclose(fp);
        }
    }

    if (detected) trigger_poison();
}

// 4. Frida Default Port Check (27042)
void check_frida_port() {
    struct sockaddr_in sa = {};
    sa.sin_family = AF_INET;
    sa.sin_port = htons(27042);
    inet_aton("127.0.0.1", &sa.sin_addr);

    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock >= 0) {
        if (connect(sock, (struct sockaddr *) &sa, sizeof(sa)) == 0) {
            trigger_poison();
        }
        close(sock);
    }
}

// 5. Unauthorized Libs / Memory Segments
void check_unauthorized_libs() {
    FILE *fp = fopen("/proc/self/maps", "r");
    char line[512];
    if (fp) {
        while (fgets(line, sizeof(line), fp)) {
            // Check for suspicious paths
            if (strstr(line, "/data/local/tmp")) {
                trigger_poison();
                break;
            }
            // Check for rwx anonymous segments (Shellcode / Manual injection)
            if (strstr(line, "rwxp") && strstr(line, "[anon]")) {
                trigger_poison();
                break;
            }
        }
        fclose(fp);
    }
}

// 6. Native Hooks via Stack Unwinding
struct BacktraceState {
    void **current;
    void **end;
};

_Unwind_Reason_Code unwind_callback(struct _Unwind_Context *context, void *arg) {
    auto *state = static_cast<BacktraceState *>(arg);
    uintptr_t pc = _Unwind_GetIP(context);
    if (pc) {
        if (state->current == state->end) {
            return _URC_END_OF_STACK;
        } else {
            *state->current++ = reinterpret_cast<void *>(pc);
        }
    }
    return _URC_NO_REASON;
}

void check_native_hooks() {
    const size_t max_depth = 30;
    void *buffer[max_depth];
    BacktraceState state = {buffer, buffer + max_depth};

    _Unwind_Backtrace(unwind_callback, &state);

    size_t count = state.current - buffer;
    for (size_t i = 0; i < count; ++i) {
        Dl_info info;
        if (dladdr(buffer[i], &info) && info.dli_fname) {
            // Check for known hooking frameworks
            if (strstr(info.dli_fname, "lsposed") ||
                strstr(info.dli_fname, "xposed") ||
                strstr(info.dli_fname, "frida")) {
                trigger_poison();
                return;
            }
        }
    }
}

/**
 * Security Monitor Thread: dual-frequency execution
 */
void *security_monitor_thread(void *arg) {
    // Random engine handled locally in trigger_poison
    int heavy_check_counter = 0;

    while (true) {
        // --- Lightweight Checks (High Frequency: ~5s) ---
        check_ptrace();
        check_env_injection();

        // --- Heavyweight Checks (Low Frequency: ~60s) ---
        if (heavy_check_counter >= 12) {
            check_frida_memory();
            check_frida_port();
            check_unauthorized_libs();
            check_native_hooks();
            heavy_check_counter = 0;
        }

        heavy_check_counter++;
        sleep(5);
    }
    return nullptr;
}

extern "C" void start_security_monitor() {
    pthread_t thread;
    pthread_create(&thread, nullptr, security_monitor_thread, nullptr);
    pthread_detach(thread);
}
