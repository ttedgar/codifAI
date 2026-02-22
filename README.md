# CodifAI — Coding Challenge Platform

A modern fullstack application for creating, sharing, and solving JavaScript coding challenges with **AI-powered challenge generation**. All code solutions are executed and validated in real-time via Judge0.

---

## 🚀 Start

### Prerequisites
Only **Docker & Docker Compose** required.

### Setup & Run

```bash
# 1. Clone the repository
git clone https://github.com/ttedgar/codifai.git
cd codifai
```
```bash
# 2. Copy configuration template and add your Gemini API key
cp .env.example .env
# Edit .env and add your GEMINI_API_KEY from https://aistudio.google.com
```
```bash
# 3. Start everything
docker-compose up
```
```bash
# 4. Open in browser
# Frontend: http://localhost:3000
# Backend API & Docs: http://localhost:8080/swagger-ui.html
```

---

## 📋 What You Get

Once running, you can:

### 👤 **User Registration & Authentication**
- Create accounts with email/password
- Login with JWT-based sessions
- No external auth required (Google, GitHub, etc.)

### 🧩 **Challenge Management**
- **Browse challenges** — filtered by difficulty (Easy/Medium/Hard)
- **View challenge details** — description, starter code, sample tests
- **Create challenges manually** — write description, starter code, unit tests
- **Generate challenges with AI** — describe what you want, Gemini creates it automatically

### 💻 **Code Execution & Testing**
- Write solutions in browser code editor
- Submit code instantly
- Judge0 compiles and runs tests
- See real-time test results (pass/fail, execution time, memory usage)
- Get detailed error messages (compile errors, runtime errors, wrong output)

---

## 🔧 Technology Stack

| Component | Technology |
|-----------|-----------|
| **Frontend** | React 19 + TypeScript + TailwindCSS + Vite + Nginx |
| **Backend** | Java 17 + Spring Boot 3.4.x + Spring Security (JWT) + Spring Data JPA |
| **Database** | PostgreSQL 15 |
| **Code Execution** | Judge0 1.13.1 (Node.js 20 JavaScript only) |
| **AI Generation** | Google Gemini 2.5 Flash API |
| **Containerization** | Docker & Docker Compose v3.8 |

---

## 📁 Project Structure

```
codifai/
├── backend/                          # Java/Spring Boot backend
│   ├── src/main/java/
│   │   └── com/edi/backend/
│   │       ├── controller/           # REST endpoints
│   │       ├── service/              # Business logic
│   │       ├── repository/           # Database layer
│   │       ├── security/             # JWT authentication
│   │       ├── config/               # Spring configuration
│   │       └── dto/                  # Request/response DTOs
│   ├── src/main/resources/
│   │   └── application.properties    # Backend configuration
│   └── Dockerfile                    # Backend container definition
│
├── frontend/                         # React/TypeScript frontend
│   ├── src/
│   │   ├── pages/                    # Page components
│   │   ├── components/               # Reusable UI components
│   │   ├── api/                      # Auto-generated API client
│   │   └── App.tsx                   # Main app component
│   ├── Dockerfile                    # Frontend container (Nginx)
│   ├── nginx.conf                    # Nginx configuration (SPA routing)
│   ├── vite.config.ts                # Vite bundler config
│   └── package.json                  # Dependencies
│
├── docker-compose.yml                # All services orchestration
├── .env.example                      # Configuration template
├── .env                              # Your local secrets (don't commit)
├── .gitignore                        # Git ignore rules
├── DOCKER_SETUP.md                   # Advanced setup & troubleshooting
└── README.md                         # This file
```

---

## 🌐 Accessing the Application

Once `docker-compose up` is running:

| Service | URL | Purpose |
|---------|-----|---------|
| **Frontend** | `http://localhost:3000` | User interface |
| **Backend API** | `http://localhost:8080` | REST API endpoints |
| **API Documentation** | `http://localhost:8080/swagger-ui.html` | Interactive API docs |
| **Database** | `localhost:5435` | PostgreSQL (for debugging) |

---

## 🧪 Feature Walkthrough

### 1. Register & Login
```
Click "Sign In" → Register new account
Enter email, username, password → Account created
```

#### Default Admin Account
```
Email:    admin@admin.admin
Username: admin
Password: admin
```
This admin account is automatically created on first startup and can delete challenges.

### 2. Browse Challenges
```
Home page → See all challenges
Filter by difficulty → Easy/Medium/Hard
Click challenge → View details, starter code, sample tests
```

### 3. Generate AI Challenge
```
"Generate a new AI challenge" form
Example prompt: "Create a challenge about array manipulation"
Select difficulty → AI generates challenge with tests
```

### 4. Solve Challenge
```
Click "Open" on any challenge
Edit code in browser editor
Click "Submit" → Code sent to Judge0
See results: ✅ All tests passed OR ❌ Test failures
```

## 🛑 Stopping the Application

```bash
# Stop all services (data persists)
docker-compose down
```

```bash
# Stop and remove all data (fresh start)
docker-compose down -v
```

---

## 👨‍💻 Development

### Using Docker Compose (Recommended)
All services start together with one command:
```bash
docker-compose up
```
See [DOCKER_SETUP.md](./DOCKER_SETUP.md) for advanced setup & local development.

### Manual Local Development (Optional)

**Backend:**
```bash
cd backend
./gradlew bootRun
# Runs at http://localhost:8080
```

**Frontend:**
```bash
cd frontend
npm install
npm run dev
# Runs at http://localhost:5173
```

---

## 📊 Architecture Overview

```
                      ┌─────────────────────────────────────────────────────────┐
                      │             User's Browser (Web)                        │
                      │    Frontend (React 19 + TypeScript + TailwindCSS)       │
                      │           Served by Nginx on port 3000                  │
                      │            http://localhost:3000                        │
                      └────────────────────────┬────────────────────────────────┘
                                               │
                                               │ HTTP/REST API (JSON)
                                               │
              ┌────────────────────────────────▼───────────────────────────────┐
              │          Backend (Java 17 + Spring Boot 3.4.x)                 │
              │               http://localhost:8080                            │
              │                                                                │
              │  ┌──────────────────────────────────────────────────────────┐  │
              │  │           Spring Controllers & Services                  │  │
              │  │  • JWT Authentication & Security                         │  │
              │  │  • Challenge CRUD Operations                             │  │
              │  │  • Code Submission & Execution Handler                   │  │
              │  │  • AI Challenge Generator (Gemini Integration)           │  │
              │  │  • Ratings & Comments Management                         │  │
              │  └──────────────────────────────────────────────────────────┘  │
              │                                                                │
              └──────────────────┬─────────────────────┬───────────┬───────────┘
                                 │                     │           │
                ┌────────────────▼──────┐   ┌──────────▼────┐   ┌──▼──────────────┐
                │  PostgreSQL 15        │   │  Judge0 1.13  │   │  Gemini API     │
                │  (Relational DB)      │   │  (Code Exec)  │   │  (AI Challenge) │
                │                       │   │               │   │                 │
                │ • Users               │   │ • Judge0 API  │   │ • LLM Model:    │
                │ • Challenges          │   │ • Redis Queue │   │   2.5 Flash     │
                │ • Submissions         │   │ • Node.js 20  │   │                 │
                │ • Ratings             │   │   Runtime     │   │ • Generates:    │
                │ • Comments            │   │   (JS Only)   │   │   Description   │
                │ • Execution Logs      │   │               │   │   Tests         │
                └───────────────────────┘   └───────────────┘   └─────────────────┘
```

---

## 📝 API Endpoints Overview

Full interactive API documentation available at: **`http://localhost:8080/swagger-ui.html`**

Key endpoints:
```
POST   /api/auth/register
POST   /api/auth/login
GET    /api/challenges
GET    /api/challenges/{id}
POST   /api/challenges
POST   /api/challenges/generate
POST   /api/submissions
GET    /api/submissions/{id}
```

## 📚 Resources

- [Docker Compose Docs](https://docs.docker.com/compose/)
- [Judge0 API](https://judge0.com/)
- [Google Gemini API](https://aistudio.google.com)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Docs](https://react.dev)

---