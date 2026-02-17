# CodifAI — Coding Challenge Platform

A modern fullstack application for creating, sharing, and solving JavaScript coding challenges with **AI-powered challenge generation**. All code solutions are executed and validated in real-time via Judge0.

Perfect for:
- **Coding interview preparation**
- **Team skill assessments**
- **Learning platforms**
- **Competitive programming**

---

## 🚀 Quick Start (5 minutes)

### Prerequisites
Only **Docker & Docker Compose** required. Nothing else needed on your machine.

### Setup & Run

```bash
# 1. Clone the repository
git clone https://github.com/your-repo/codifai.git
cd codifai

# 2. Copy configuration template and add your Gemini API key
cp .env.example .env
# Edit .env and add your GEMINI_API_KEY from https://aistudio.google.com

# 3. Start everything
docker-compose up

# 4. Open in browser
# Frontend: http://localhost:3000
# Backend API: http://localhost:8080
```

That's it! All services (database, backend, frontend, code executor) start automatically.

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

### ⭐ **Community Features**
- Rate challenges (1-5 stars)
- Leave comments on challenges
- Track your progress (solved/unsolved)
- Earn XP for solving challenges
- View leaderboard

---

## 🔧 Technology Stack

| Component | Technology |
|-----------|-----------|
| **Frontend** | React 19 + TypeScript + TailwindCSS + Vite |
| **Backend** | Java 17 + Spring Boot 3.4 + Spring Security (JWT) |
| **Database** | PostgreSQL |
| **Code Execution** | Judge0 (Node.js 20 JavaScript execution) |
| **AI Generation** | Google Gemini 2.5 Flash API |
| **Containerization** | Docker & Docker Compose |

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
├── CLAUDE.md                         # Project specifications
├── DOCKER_SETUP.md                   # Advanced Docker docs
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

## ⚙️ Environment Configuration

### `.env.example` (in git, template)
```env
GEMINI_API_KEY=your_gemini_api_key_here
JWT_SECRET=uaVgGWDqWzLjhRMhTE2Ok533xHYL51gqBH6TUs804e2
POSTGRES_PASSWORD=codifai123
JUDGE0_BASE_URL=http://judge0-server:2358
```

### `.env` (NOT in git, your local secrets)
Create this by copying `.env.example`:
```bash
cp .env.example .env
```

Then edit and add:
- **GEMINI_API_KEY** — Required for AI challenge generation. Get free key from [Google AI Studio](https://aistudio.google.com)
- **JWT_SECRET** — Optional. Defaults to safe value if not provided
- Other values — Typically no changes needed

**⚠️ Never commit `.env` to git!** It contains secrets.

---

## 🧪 Feature Walkthrough

### 1. Register & Login
```
Click "Sign In" → Register new account
Enter email, username, password → Account created
```

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

### 5. Rate & Comment
```
After solving or viewing challenge
Click ⭐ to rate (1-5 stars)
Write comment → Share feedback with other users
```

---

## 🛑 Stopping the Application

```bash
# Stop all services (data persists)
docker-compose down

# Stop and remove all data (fresh start)
docker-compose down -v
```

---

## 🔍 Troubleshooting

### Application won't start
```bash
# Check all services are healthy
docker-compose ps

# View logs
docker-compose logs -f

# Rebuild from scratch
docker-compose down -v
docker-compose up --build
```

### API returning 403 errors
- Check `.env` file has valid `GEMINI_API_KEY`
- Restart backend: `docker-compose restart backend`

### Code submission times out
- Judge0 needs time to execute. Wait 30 seconds max.
- Check logs: `docker-compose logs judge0-server`

### Database errors on first run
- PostgreSQL takes ~10s to initialize
- All services have healthchecks, will auto-retry
- If stuck, run: `docker-compose down -v && docker-compose up --build`

For more help, see [DOCKER_SETUP.md](./DOCKER_SETUP.md)

---

## 👨‍💻 Development

### Backend Development
```bash
# Backend runs at http://localhost:8080
# Auto-reload on code changes (if running locally)
# API docs: http://localhost:8080/swagger-ui.html
```

### Frontend Development
```bash
# Frontend runs at http://localhost:3000
# Hot reload included with Vite
```

### Running Backend Locally (Optional)
```bash
# In backend/ directory:
./gradlew bootRun

# Build JAR (used by Docker):
./gradlew build -x test
```

### Running Frontend Locally (Optional)
```bash
# In frontend/ directory:
npm install
npm run dev
# Runs at http://localhost:5173
```

See [DOCKER_SETUP.md](./DOCKER_SETUP.md) for advanced development setup.

---

## 📊 Architecture Overview

```
┌─────────────────────────────────────┐
│       User's Browser                │
│   Frontend (React + TypeScript)     │
│     http://localhost:3000           │
└────────────┬────────────────────────┘
             │ HTTP REST API
┌────────────▼────────────────────────┐
│   Backend (Java Spring Boot)        │
│   http://localhost:8080             │
│  ┌──────────────────────────────┐   │
│  │ JWT Authentication           │   │
│  │ Challenge Management         │   │
│  │ User Ratings & Comments      │   │
│  │ Code Submission Handler      │   │
│  └─────────┬────────────────────┘   │
├────────────┼───────────┬─────────────┤
│            │           │             │
│  PostgreSQL │    Judge0 API   Gemini API
│  (Database) │  (Code Executor)  (AI)
│            │           │             │
└────────────┴───────────┴─────────────┘
```

---

## 🔐 Security Features

- **JWT Authentication** — Stateless, secure token-based auth
- **Password Hashing** — BCrypt with salt
- **CORS Enabled** — Frontend/backend communication secured
- **SQL Injection Protection** — JPA parameterized queries
- **Environment Secrets** — Sensitive config in `.env`, not in code
- **No Hardcoded Keys** — All secrets externalized

---

## 📝 API Endpoints (Examples)

```bash
# Authentication
POST /api/auth/register       # Create account
POST /api/auth/login          # Login, get JWT token

# Challenges
GET /api/challenges           # List all challenges (paginated)
GET /api/challenges/{id}      # Get challenge details
POST /api/challenges          # Create challenge
POST /api/challenges/generate # AI-generate challenge

# Submissions
POST /api/submissions         # Submit code solution
GET /api/submissions/{id}     # Get submission results

# Ratings
POST /api/ratings             # Rate a challenge
GET /api/ratings?challengeId=1

# Comments
POST /api/comments            # Post comment
GET /api/comments?challengeId=1
```

Full API documentation at: `http://localhost:8080/swagger-ui.html`

---

## 🎯 Next Steps

1. **Run the app** → `docker-compose up`
2. **Register** → Create your account
3. **Try a challenge** → Solve an existing one
4. **Generate with AI** → Create a challenge using Gemini
5. **Check the leaderboard** → See who's solving challenges

---

## 📚 Resources

- [Docker Compose Docs](https://docs.docker.com/compose/)
- [Judge0 API](https://judge0.com/)
- [Google Gemini API](https://aistudio.google.com)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [React Docs](https://react.dev)

---

## 📄 License

[Your License Here]

---

**Built with ❤️ for developers who love coding challenges**
