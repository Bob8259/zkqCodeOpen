## Implementation Plan: X25519 Key Pair Generation

### 1. Create `cypherhelper.cpp` (new file)

* Implement `generateX25519KeyPair()` function that:

  * Uses the `get_random_bytes()` function from `login.cpp` to generate a 32-byte secret key

  * Calls `crypto_x25519_public_key()` from monocypher to derive the public key

  * Converts both keys to hex strings

  * Returns them as a comma-separated string (format: "public\_key\_hex,secret\_key\_hex")

### 2. Update `native-lib.cpp`

* Add external declaration for `generateX25519KeyPair`

* Register the function in `gMethods[]` array with signature: `()Ljava/lang/String;`

### 3. Update `NativeTools.kt`

* Add external function declaration: `external fun generateX25519KeyPair(): String`

### 4. Update `MainActivity.kt`

* Call `NativeTools.generateX25519KeyPair()` in `onCreate()`

* Parse the returned string to extract public and secret keys

* Print both keys using `println()`

### 5. Update `CMakeLists.txt`

* Add `monocypher/monocypher.c` to the `add_library()` source list

* Add `monocypher` directory to include path using `target_include_directories()`

### 6. Technical Details

* Secret key: 32 random bytes

* Public key: 32 bytes derived from secret key using X25519

* Both keys will be returned as 64-character hex strings

* Format: `publicKeyHex,secretKeyHex`

