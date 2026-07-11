import sys
import os
import hashlib
import requests

try:
    from local_secrets import ADMIN_PASSWORD, BASE_URL
except ImportError:
    print(
        "Missing local_secrets.py. Copy script/local_secrets.example.py "
        "to script/local_secrets.py and fill in the real values."
    )
    sys.exit(1)


def extract_signature_and_md5(file_path: str) -> tuple[str, str]:
    """Read the file, extract the last 64 bytes (signature), and compute MD5 of the whole file."""
    with open(file_path, "rb") as f:
        data = f.read()

    if len(data) < 76:  # 12 bytes nonce + 64 bytes signature
        raise ValueError("File is too small to contain an embedded signature.")

    # Extract signature (last 64 bytes)
    signature_bytes = data[-64:]
    signature_hex = signature_bytes.hex()

    # Compute MD5 of the whole file (with signature appended)
    md5 = hashlib.md5()
    md5.update(data)
    md5_hash = md5.hexdigest()

    return signature_hex, md5_hash


def upload_jar(jar_path: str):
    url = f"{BASE_URL}api/hot-update-upload"
    jar_name = os.path.basename(jar_path)

    # 1. Extract the signature and compute MD5 of the file
    try:
        signature_hex, md5_hash = extract_signature_and_md5(jar_path)
        print(f"Extracted Signature (hex): {signature_hex}")
        print(f"File MD5: {md5_hash}")
    except Exception as e:
        print(f"Failed to extract signature and hash: {e}")
        sys.exit(1)

    print(f"Uploading {jar_name} (MD5: {md5_hash}) to {url}")

    headers = {
        "x-admin-password": ADMIN_PASSWORD
    }

    with open(jar_path, "rb") as jar_file:
        files = {
            "jar": (jar_name, jar_file),
            "signature": (f"{jar_name}.signature", signature_hex.encode("utf-8"), "text/plain")
        }
        data = {
            "md5": md5_hash,
        }
        response = requests.post(url, headers=headers, files=files, data=data)

    print(f"Status: {response.status_code}")
    try:
        print(f"Response: {response.json()}")
    except Exception:
        print(f"Response: {response.text}")

    if response.status_code != 200:
        print("Upload failed!")
        sys.exit(1)

    print("Upload successful!")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python upload_jar.py <path_to_encrypted_jar>")
        sys.exit(1)

    jar_path = sys.argv[1]
    if not os.path.isfile(jar_path):
        print(f"Error: file not found: {jar_path}")
        sys.exit(1)

    upload_jar(jar_path)
