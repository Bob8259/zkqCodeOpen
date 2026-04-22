# ZKQyolo — AI Detector Inference Service for Android

**Version:** 1.05

An Android app that runs a local HTTP server exposing AI detector inference. The app launches a foreground service on startup, making the detection API available to other apps or automation tools on the device via `http://localhost:13462`.

---

## Table of Contents

1. [Quick Start](#quick-start)
2. [Supported Models](#supported-models)
3. [API Reference](#api-reference)
4. [Usage Examples](#usage-examples)

---

## Quick Start

1. Install the APK on the target device.
2. Launch the app once — the foreground service starts automatically.
3. The HTTP server is now listening on **port 13462**.
4. Load a model, then send images for detection.

```
# Verify the service is running
curl http://localhost:13462/status

# Load a model (modelType is required)
curl -X POST http://localhost:13462/load -d '{"modelType":"capital-building-detect"}'

# Run detection on an image
curl -X POST http://localhost:13462/detect \
  --data-binary @screenshot.png
```

You can also start the service without opening the app UI:

```
adb shell am start-foreground-service -n com.coc.zkqyolo/.service.YoloService
```

---

## Supported Models

Models are loaded by passing a `modelType` string to the `/load` endpoint.
All detector models are expected to be bundled in the APK assets directory.

| `modelType` value | Model file | Source |
|---|---|---|
| `"walls-detect"` | `walls_detector.tflite` | Bundled in assets |
| `"numbers"` | `numbers_detector.tflite` | Bundled in assets |
| `"building-detect"` | `my_building_detector.tflite` | Bundled in assets |
| `"capital-building-detect"` | `capital_building_detector.tflite` | Bundled in assets |
| `"remove-obstacle"` | `obstacles_detector.tflite` | Bundled in assets |
| `"clan-war-numbers"` | `clan_war_number_detector.tflite` | Bundled in assets |

> **Note:** `modelType` is required. Omitting it or passing an unrecognized value will result in an error.
> If a model asset has not been packaged under `app/src/main/assets`, `/load` will fail with a missing-asset error.

The detector dynamically reads the model's input/output tensor shapes, so any compatible object-detection TFLite model with output shape `[1, N, 6]` (where each detection is `[x1, y1, x2, y2, score, classIndex]`) will work.

---

## API Reference

Base URL: `http://localhost:13462`

All responses are JSON with `Content-Type: application/json`.

### GET /status

Health check. Returns the server status, version, and current model state.

**Response:**

```json
{
  "status": "running",
  "version": "1.05",
  "modelLoaded": true,
  "modelType": "walls-detect"
}
```

| Field | Type | Description |
|---|---|---|
| `status` | string | Always `"running"` if the server is up. |
| `version` | string | Current server version. |
| `modelLoaded` | boolean | Whether a model is currently loaded in memory. |
| `modelType` | string \| null | The `modelType` that was used to load the current model, or `null` if none. |

---

### POST /load

Load model weights into the TFLite interpreter. If the same model type is already loaded, this is a no-op.

**Request body (JSON):**

```json
{
  "modelType": "walls-detect"
}
```

| Field | Type | Required | Description |
|---|---|---|---|
| `modelType` | string | **Yes** | Model identifier (see [Supported Models](#supported-models)). Must be one of the listed values. |

**Success response:**

```json
{ "success": true }
```

**Error response (400) — missing or invalid `modelType`:**

```json
{ "success": false, "error": "\"modelType\" is required. Valid types: walls-detect, numbers, building-detect, capital-building-detect, remove-obstacle, clan-war-numbers" }
```

**Error response (500) — model failed to load:**

```json
{ "success": false, "error": "..." }
```

---

### POST /detect

Run object detection on a posted image. A model **must** be loaded first via `/load`.

**Request:**

- **Body:** Raw image bytes (PNG, JPEG, etc. — any format decodable by `BitmapFactory`).
- **Content-Type:** `application/octet-stream` (or any; the server reads raw bytes).
- **Content-Length:** Must be set and non-zero.

**Query parameters:**

| Parameter | Type | Default | Description |
|---|---|---|---|
| `threshold` | float | `0.3` | Minimum confidence score to include a detection. |
| `distanceThreshold` | double | `5.0` | Pixel distance below which two detections are considered duplicates (NMS-like filtering). The higher-confidence detection is kept. |

**Example:**

```
POST /detect?threshold=0.5&distanceThreshold=10 HTTP/1.1
Content-Length: 54321

<raw image bytes>
```

**Success response:**

```json
{
  "detections": [
    {
      "x1": 102.5,
      "y1": 200.0,
      "x2": 350.0,
      "y2": 410.3,
      "score": 0.92,
      "classIndex": 0
    }
  ]
}
```

| Field | Type | Description |
|---|---|---|
| `x1`, `y1` | float | Top-left corner of the bounding box (in original image pixel coordinates). |
| `x2`, `y2` | float | Bottom-right corner of the bounding box. |
| `score` | float | Detection confidence (0–1). |
| `classIndex` | int | Predicted class index. |

**Error responses:**

| Status | Condition |
|---|---|
| 400 | No model loaded — call `/load` first. |
| 400 | Empty request body. |
| 400 | Image bytes could not be decoded. |

---

### POST /clear

Unload model weights and free the TFLite interpreter.

**Request body:** *(empty or ignored)*

**Success response:**

```json
{ "success": true }
```

---

## Usage Examples

### Python

```python
import requests

BASE = "http://localhost:13462"

# Check status
r = requests.get(f"{BASE}/status")
print(r.json())

# Load a model
requests.post(f"{BASE}/load", json={"modelType": "capital-building-detect"})

# Detect objects in a screenshot
with open("screenshot.png", "rb") as f:
    r = requests.post(
        f"{BASE}/detect",
        params={"threshold": 0.4, "distanceThreshold": 8},
        data=f.read(),
        headers={"Content-Type": "application/octet-stream"},
    )
print(r.json()["detections"])

# Unload model when done
requests.post(f"{BASE}/clear")
```

### ADB + curl (on-device)

```bash
# Forward port from PC to device
adb forward tcp:13462 tcp:13462

# Now use curl from your PC
curl http://localhost:13462/status
curl -X POST http://localhost:13462/load -d '{"modelType":"numbers"}'
curl -X POST http://localhost:13462/detect \
  -H "Content-Type: application/octet-stream" \
  --data-binary @image.png
```

