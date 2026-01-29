#ifndef KEYS_H
#define KEYS_H

#include <stdint.h>

// Obfuscated Key Parts
// MASK: Random bytes acting as a one-time pad for storage
extern const uint8_t KEY_MASK[32];

// OBFUSCATED: XORed version of the actual key
// Resulting Key = KEY_MASK ^ OBFUSCATED
extern const uint8_t OBFUSCATED_KEY[32];

#endif // KEYS_H
