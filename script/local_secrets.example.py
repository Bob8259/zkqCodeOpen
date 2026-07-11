# Local secrets for script/*.py — do not commit the real local_secrets.py.
# Copy this file to local_secrets.py and replace the placeholders.

# ChaCha20 encryption key (hex-encoded)
KEY_HEX = "REPLACE_ME"

# Ed25519 private key for JAR signing (hex-encoded)
PRIVATE_KEY_HEX = "REPLACE_ME"

# Hot-update upload endpoint base URL
BASE_URL = "https://example.com/"

# Admin password for hot-update upload API
ADMIN_PASSWORD = "REPLACE_ME"
