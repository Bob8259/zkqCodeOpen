This documentation of the server. 
URL:localhost:6839
Connect to the server via websocket.


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
| **subAction** | String | Specifies the action (e.g., `"touchdown"`, `"touchmove"`, `"pinchin"`). |
| **x, y, x1, y1...** | Float | Screen coordinates. |
| **id** | Int | Pointer identifier for multi-touch. |
| **duration** | Long | Execution time in milliseconds (Optional, default: `150L`). |
| **jitter** | Boolean | Simulates human-like noise (Optional, default: `true`). |

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

Moves an active pointer to a new location over a period of time.

* **Syntax:**
```json
{ "actionType": "touch_action", "subAction": "touchmove", "x": Float, "y": Float, "id": Int, "duration": Long, "jitter": Boolean }

```


* **Example:**
```json
{ "actionType": "touch_action", "subAction": "touchmove", "x": 600.0, "y": 600.0, "id": 1, "duration": 200, "jitter": true }

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

### Gesture Commands (Pinch)

These commands automate multi-finger gestures.

#### **Pinch In**

Simulates two fingers moving from outer positions toward a central point.

* **Logic:** * Finger 1: $(x1, y1) \to (finalX, finalY)$
* Finger 2: $(x2, y2) \to (finalX, finalY)$ 


* **Syntax:**
```json
{ "actionType": "touch_action", "subAction": "pinchin", "x1": Float, "y1": Float, "x2": Float, "y2": Float, "finalX": Float, "finalY": Float, "duration": Long, "jitter": Boolean }

```


* **Example:**
```json
{ "actionType": "touch_action", "subAction": "pinchin", "x1": 100.0, "y1": 100.0, "x2": 900.0, "y2": 900.0, "finalX": 500.0, "finalY": 500.0, "duration": 300 }

```



#### **Pinch Out**

Simulates two fingers starting at a center point and moving outward.

* **Logic:** * Finger 1: $(x1, y1) \to (finalX, finalY)$
* Finger 2: $(x2, y2) \to (finalX, finalY)$
* *Note: $x1, y1, x2, y2$  represent starting points, while  represent target points.*


* **Syntax:**
```json
{ "actionType": "touch_action", "subAction": "pinchout", "x1": Float, "y1": Float, "x2": Float, "y2": Float, "finalX": Float, "finalY": Float, "duration": Long, "jitter": Boolean }

```


* **Example:**
```json
{ "actionType": "touch_action", "subAction": "pinchout", "x1": 500.0, "y1": 500.0, "x2": 500.0, "y2": 500.0, "finalX": 100.0, "finalY": 100.0, "duration": 300 }

```



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

## 3. System Actions

Commands for system-level operations like downloading files or calculating hashes. These use `actionType: "system_action"`.

### Download File

Downloads a file from a URL to a specified path on the device.

* **Syntax:**
```json
{
  "actionType": "system_action",
  "subAction": "download",
  "url": "String",
  "savePath": "String"
}
```

* **Example:**
```json
{
  "actionType": "system_action",
  "subAction": "download",
  "url": "https://example.com/file.zip",
  "savePath": "/data/user/0/com.coc.zkqserver/files/file.zip"
}
```

### SHA256 Hash

Calculates the SHA256 hash of a given input string.

* **Syntax:**
```json
{
  "actionType": "system_action",
  "subAction": "sha256",
  "input": "String"
}
```

* **Example:**
```json
{
  "actionType": "system_action",
  "subAction": "sha256",
  "input": "hello world"
}
```

---

## 4. Database Actions

Commands for interacting with SQLite databases. These use `actionType: "database_action"`.

### Open Database

Opens or creates a database at the specified path.

* **Syntax:**
```json
{
  "actionType": "database_action",
  "subAction": "open",
  "path": "String"
}
```

* **Example:**
```json
{
  "actionType": "database_action",
  "subAction": "open",
  "path": "/data/local/tmp/mydb.db"
}
```

### Execute SQL

Executes a SQL command. If it's a `SELECT` statement, it returns a JSON array of results. Otherwise, it returns `"Success"`.

* **Syntax:**
```json
{
  "actionType": "database_action",
  "subAction": "execute",
  "sql": "String"
}
```

* **Example (Query):**
```json
{
  "actionType": "database_action",
  "subAction": "execute",
  "sql": "SELECT * FROM users"
}
```

* **Example (Update/Insert):**
```json
{
  "actionType": "database_action",
  "subAction": "execute",
  "sql": "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT)"
}
```

### Close Database

Closes the currently open database connection.

* **Syntax:**
```json
{
  "actionType": "database_action",
  "subAction": "close"
}
```

---

## 5. Server Test

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

