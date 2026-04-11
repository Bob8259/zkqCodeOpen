This documentation of the server. 
URL: localhost:6839
Connect to the server via WebSocket **or** plain HTTP GET.

WebSocket endpoint: `ws://localhost:6839/zkq`
HTTP GET endpoint:  `http://localhost:6839/zkq`


---

# Server API Documentation

This document outlines the JSON-based command structure for simulating touch interactions and performing file system operations.

## 0. Response Format

Most commands return a JSON response to indicate the result of the operation.

### Success Response
```json
{
  "status": "success",
  "data": "..." 
}
```

### Error Response
```json
{
  "status": "error",
  "message": "..."
}
```

*Note: Currently, `touch_action` commands do not return a response.*

---

## 1. Touch Simulation Commands

All touch simulation commands are **JSON strings**. Each command must include an `actionType` of `"touch_action"` and a `subAction` field.

### Common Parameters

| Parameter | Type | Description |
| --- | --- | --- |
| **actionType** | String | Always `"touch_action"`. |
| **subAction** | String | Specifies the action (e.g., `"touchdown"`, `"touchmove"`, `"touchup"`). |
| **x, y** | Float | Screen coordinates. |
| **id** | Int | Pointer identifier for multi-touch. |

---

### Basic Touch Commands

#### **Touch Down**

Simulates a finger pressing the screen.

* **Syntax:**
```json
{ "actionType": "touch_action", "subAction": "touchdown", "x": Float, "y": Float, "id": Int }

```


* **Example:**
```json
{ "actionType": "touch_action", "subAction": "touchdown", "x": 500.0, "y": 500.0, "id": 1 }

```



#### **Touch Move**

Moves an active pointer to a new location.

* **Syntax:**
```json
{ "actionType": "touch_action", "subAction": "touchmove", "x": Float, "y": Float, "id": Int }
```


* **Example:**
```json
{ "actionType": "touch_action", "subAction": "touchmove", "x": 600.0, "y": 600.0, "id": 1 }
```



#### **Touch Up**

Lifts an active pointer from the screen.

* **Syntax:**
```json
{ "actionType": "touch_action", "subAction": "touchup", "id": Int }

```


* **Example:**
```json
{ "actionType": "touch_action", "subAction": "touchup", "id": 1 }

```



---

---

## 2. File Actions

Commands used for interacting with the device file system. These use `actionType: "file_action"`.

### Read File

```json
{
  "actionType": "file_action",
  "subAction": "read",
  "path": "/data/user/0/com.coc.zkqserver/files/test.txt"
}

```

### Write File

```json
{
  "actionType": "file_action",
  "subAction": "write",
  "path": "/data/user/0/com.coc.zkqserver/files/new_file.txt",
  "content": "Hello, this is some content to write."
}

```

### Create File

```json
{
  "actionType": "file_action",
  "subAction": "create",
  "path": "/data/user/0/com.coc.zkqserver/files/empty_file.txt"
}

```

### Delete File

```json
{
  "actionType": "file_action",
  "subAction": "delete",
  "path": "/data/user/0/com.coc.zkqserver/files/old_file.txt"
}

```

### Check File Existence

```json
{
  "actionType": "file_action",
  "subAction": "check_exists",
  "path": "/data/user/0/com.coc.zkqserver/files/my_document.txt"
}

```

### Copy File

```json
{
  "actionType": "file_action",
  "subAction": "copy",
  "path": "/data/user/0/com.coc.zkqserver/files/source.txt",
  "destPath": "/data/user/0/com.coc.zkqserver/files/destination.txt"
}

```

### Rename File

```json
{
  "actionType": "file_action",
  "subAction": "rename",
  "path": "/data/user/0/com.coc.zkqserver/files/old_name.txt",
  "newPath": "/data/user/0/com.coc.zkqserver/files/new_name.txt"
}
```
---

## 3. Server Test

Used to test the connection to the server.

### Connection Test

* **Request:**
```json
{
  "actionType": "connection_test"
}
```

* **Response:**
```json
{
  "status": "success",
  "data": "connected" 
}
```

---

## 4. HTTP GET Interface

All commands available over WebSocket are also accessible via plain HTTP GET requests. Parameters that would normally be JSON fields are passed as **URL query parameters**. The response body is the same JSON format as WebSocket.

**Base URL:** `http://localhost:6839/zkq`

All values must be URL-encoded when they contain special characters (e.g. `/` in file paths should be encoded as `%2F`, or the whole path passed URL-encoded).

### Connection Test

```
GET /zkq?actionType=connection_test
```

### Touch Actions

```
GET /zkq?actionType=touch_action&subAction=touchdown&x=500.0&y=500.0&id=1
GET /zkq?actionType=touch_action&subAction=touchmove&x=600.0&y=600.0&id=1
GET /zkq?actionType=touch_action&subAction=touchup&id=1
```

### File Actions

```
GET /zkq?actionType=file_action&subAction=read&path=%2Fdata%2Fuser%2F0%2Fcom.coc.zkqserver%2Ffiles%2Ftest.txt

GET /zkq?actionType=file_action&subAction=write&path=%2Fdata%2F...%2Fnew_file.txt&content=Hello%2C%20world.

GET /zkq?actionType=file_action&subAction=create&path=%2Fdata%2F...%2Fempty_file.txt

GET /zkq?actionType=file_action&subAction=delete&path=%2Fdata%2F...%2Fold_file.txt

GET /zkq?actionType=file_action&subAction=check_exists&path=%2Fdata%2F...%2Fmy_document.txt

GET /zkq?actionType=file_action&subAction=copy&path=%2Fdata%2F...%2Fsource.txt&destPath=%2Fdata%2F...%2Fdestination.txt

GET /zkq?actionType=file_action&subAction=rename&path=%2Fdata%2F...%2Fold_name.txt&newPath=%2Fdata%2F...%2Fnew_name.txt
```

### Notes

- The response format is identical to the WebSocket interface (`{"status":"success","data":"..."}` or `{"status":"error","message":"..."}`).
- Touch actions always return `{"status":"success","data":"null"}`.
- Query parameter names map 1-to-1 to the JSON field names described in sections 1–3.

---

## 5. Client Mode (app_process)

When launched via `app_process`, the app runs the embedded WebSocket/HTTP server **and** simultaneously connects as a WebSocket client to `ws://localhost:16839/zkq`. The standard entry point is `com.coc.zkqserver.ShellServer`.

### Launch Command

```sh
CLASSPATH=/data/local/tmp/zkqserver.apk app_process /data/local/tmp com.coc.zkqserver.ShellServer
```

A client-only entry point (no embedded server) is also available at `com.coc.zkqserver.AppProcessClient` if needed.

### Connection Behavior

- On startup, the client attempts to connect to `ws://localhost:16839/zkq`.
- If the connection fails for any reason, it waits **2 seconds** and retries. This loop runs indefinitely until a connection is established.
- Reconnection follows the same retry policy if the server drops the connection.

### Announcement Message

Immediately after the **first** successful connection, the client sends the following message to the server:

```json
{
  "actionType": "client_connected",
  "message": "ZKQserver connected"
}
```

No announcement is sent on subsequent reconnections.

### Receiving Actions from the Server

The server sends JSON text frames to the client over the WebSocket connection. The client reads the `actionType` field and dispatches accordingly. Every handled message produces a JSON response that is sent back to the server over the same connection.

| `actionType` | Returns response? |
| --- | --- |
| `connection_test` | Yes |
| `file_action` | Yes |
| `touch_action` (or any other value) | Yes |

#### connection_test

```json
{ "actionType": "connection_test" }
```

Response:

```json
{ "status": "success", "data": "connected" }
```

#### file_action

The server sends any of the file sub-actions defined in section 2. Examples:

```json
{ "actionType": "file_action", "subAction": "read", "path": "/data/local/tmp/example.txt" }
```

```json
{ "actionType": "file_action", "subAction": "write", "path": "/data/local/tmp/example.txt", "content": "hello" }
```

```json
{ "actionType": "file_action", "subAction": "create", "path": "/data/local/tmp/new.txt" }
```

```json
{ "actionType": "file_action", "subAction": "delete", "path": "/data/local/tmp/old.txt" }
```

```json
{ "actionType": "file_action", "subAction": "check_exists", "path": "/data/local/tmp/file.txt" }
```

```json
{ "actionType": "file_action", "subAction": "copy", "path": "/data/local/tmp/src.txt", "destPath": "/data/local/tmp/dst.txt" }
```

```json
{ "actionType": "file_action", "subAction": "rename", "path": "/data/local/tmp/old.txt", "newPath": "/data/local/tmp/new.txt" }
```

See section 2 for the full field reference and expected responses.

#### touch_action

The server sends any of the touch sub-actions defined in section 1. Examples:

```json
{ "actionType": "touch_action", "subAction": "touchdown", "x": 500.0, "y": 800.0, "id": 1 }
```

```json
{ "actionType": "touch_action", "subAction": "touchmove", "x": 520.0, "y": 820.0, "id": 1 }
```

```json
{ "actionType": "touch_action", "subAction": "touchup", "id": 1 }
```

Response for all touch sub-actions:

```json
{ "status": "success", "data": "null" }
```

See section 1 for the full field reference.

### Response Format

Responses follow the same format as the server API (see section 0):

```json
{ "status": "success", "data": "..." }
```

or

```json
{ "status": "error", "message": "..." }
```
