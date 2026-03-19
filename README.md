# 💬 Java Chat Application

A multi-user desktop chat application built as a university project.

## Tech Stack

| Layer      | Technology          |
|------------|---------------------|
| Language   | Java 17+            |
| Server     | Java HttpServer     |
| Client UI  | JavaFX 21 (MVC)     |
| HTTP       | Java HttpClient     |
| Database   | H2 (embedded)       |
| JSON       | Gson                |
| Build      | Maven               |

## Project Structure

```
src/main/java/com/chatapp/
├── server/
│   ├── ServerMain.java          ← Start the server here
│   ├── DatabaseManager.java     ← H2 connection + table setup
│   └── handlers/
│       ├── BaseHandler.java
│       ├── RegisterHandler.java
│       ├── LoginHandler.java
│       ├── UsersHandler.java
│       ├── SendMessageHandler.java
│       └── MessagesHandler.java
├── client/
│   ├── MainApp.java             ← Start the client here
│   ├── ApiClient.java           ← All HTTP calls
│   └── controllers/
│       ├── LoginController.java
│       └── ChatController.java
└── model/
    ├── User.java
    └── Message.java

src/main/resources/com/chatapp/client/views/
├── login.fxml
└── chat.fxml
```

## API Endpoints

| Method | Endpoint                        | Description              |
|--------|---------------------------------|--------------------------|
| POST   | `/register`                     | Register new user        |
| POST   | `/login`                        | Login, returns user JSON |
| GET    | `/users`                        | List all users           |
| POST   | `/sendMessage`                  | Send a message           |
| GET    | `/messages?user1=X&user2=Y`     | Get conversation history |

## How to Run

### Prerequisites
- JDK 17 or newer — https://adoptium.net
- IntelliJ IDEA — https://www.jetbrains.com/idea/download

### Step 1 — Load dependencies
Open the project in IntelliJ. When prompted, click **"Load Maven Changes"**.  
Wait for all libraries to download (JavaFX, H2, Gson).

### Step 2 — Start the server
Run `com.chatapp.server.ServerMain`.  
You should see:
```
[DB] Tables ready.
[SERVER] Running on http://localhost:8080
```

### Step 3 — Start the client
In IntelliJ's Maven panel (right side):  
**Plugins → javafx → javafx:run**

Or from terminal:
```bash
mvn javafx:run
```

### Step 4 — Test with two users
- Enable **"Allow parallel run"** in the Run Configuration
- Launch the client twice
- Register Alice in window 1, Bob in window 2
- Select each other in the user list and chat!

## Database

H2 saves automatically to `chatapp_db.mv.db` in the project root.  
To inspect it while the server runs, add to `ServerMain.java`:
```java
org.h2.tools.Server.createWebServer("-webPort", "8082").start();
// then open: http://localhost:8082
// JDBC URL: jdbc:h2:./chatapp_db  |  User: sa  |  Password: (empty)
```

## Possible Improvements

1. **Auto-refresh** — already included (polls every 3 seconds via `ScheduledService`)
2. **Password hashing** — use SHA-256 before storing passwords
3. **Session tokens** — return a UUID token on login; validate on every request
