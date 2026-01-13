## Implementation Plan for X25519 Key Pair Generation Helper

### 1. Update CMakeLists.txt

* Add the Monocypher library files to the build configuration

### 2. Implement X25519 Key Generation in native-lib.cpp

* Add necessary includes for Monocypher

* Create a new JNI function `generateX25519KeyPair` that:

  * Generates a random 32-byte secret key using the existing `get_random_bytes` function from login.cpp

  * Uses Monocypher's `crypto_x25519_public_key` to generate the corresponding public key

  * Converts both keys to hex strings and returns them as a single string (secret key first, then public key, separated by a delimiter)

### 3. Update JNI Registration in native-lib.cpp

* Add the new `generateX25519KeyPair` function to the `gMethods` array for JNI registration

### 4. Declare External Function in NativeTools.kt

* Add the `generateX25519KeyPair` external function declaration to the `NativeTools` object

### 5. Demonstrate Usage in MainActivity.kt

* Add a call to `NativeTools.generateX25519KeyPair()` in the `onCreate` method of `MainActivity`

* Print the generated key pair using `println`&#x20;

### Expected Output

* The app will generate an X25519 key pair when launched

* The secret key and public key will be printed in the logcat

### Files to Modify

1. `@/app/src/main/cpp/CMakeLists.txt` - Add Monocypher to build
2. `@/app/src/main/cpp/native-lib.cpp` - Implement and register the new function
3. `@/app/src/main/java/com/coc/zkqcode/zkqnative/NativeTools.kt` - Declare the external function
4. `@/app/src/main/java/com/coc/zkqcode/MainActivity.kt` - Add demo usage

### Key Dependencies

* Monocypher library for X25519 implementation

* Existing random number generator from login.cpp

* JNI for communication between C++ and Kotlin

