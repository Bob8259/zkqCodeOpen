import sys
import os
import hashlib
from Crypto.Cipher import ChaCha20
from cryptography.hazmat.primitives.asymmetric import ed25519

try:
    from local_secrets import KEY_HEX, PRIVATE_KEY_HEX
except ImportError:
    print(
        "Missing local_secrets.py. Copy script/local_secrets.example.py "
        "to script/local_secrets.py and fill in the real values."
    )
    sys.exit(1)

KEY = bytes.fromhex(KEY_HEX)


def encrypt_jar(jar_path: str) -> str:
    with open(jar_path, "rb") as f:
        plaintext = f.read()

    # 12-byte nonce for IETF ChaCha20, matching the Rust decryptJar expectation
    nonce = os.urandom(12)

    cipher = ChaCha20.new(key=KEY, nonce=nonce)
    ciphertext = cipher.encrypt(plaintext)

    # Combine nonce and ciphertext
    encrypted_data = nonce + ciphertext

    # Compute SHA-256 hash of the encrypted data
    sha256_hash = hashlib.sha256(encrypted_data).digest()

    # Sign the SHA-256 hash
    private_key = ed25519.Ed25519PrivateKey.from_private_bytes(
        bytes.fromhex(PRIVATE_KEY_HEX)
    )
    signature = private_key.sign(sha256_hash)

    jar_dir = os.path.dirname(jar_path)
    jar_name = os.path.basename(jar_path)
    output_name = f"encrypted_{jar_name}"
    output_path = os.path.join(jar_dir, output_name)

    # Write: [12-byte nonce][ciphertext][64-byte signature]
    with open(output_path, "wb") as f:
        f.write(encrypted_data + signature)

    os.remove(jar_path)
    print(f"Encrypted: {jar_name} -> {output_name} (with embedded signature)")
    return output_path


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python encrypt_jar.py <path_to_jar>")
        sys.exit(1)

    jar_path = sys.argv[1]
    if not os.path.isfile(jar_path):
        print(f"Error: file not found: {jar_path}")
        sys.exit(1)

    encrypt_jar(jar_path)
