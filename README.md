# 🎬 MovieRama – Backend (Local Development Setup)

This is the backend service for MovieRama — a movie-sharing platform where users can submit and vote on movies. This guide explains how to set up the backend for local development using **Spring Boot**, **Java 21**, **PostgreSQL**, and **Podman**.

---

## ✅ Requirements

- Java 21
- Maven 3.9+
- Podman (with Docker CLI compatibility)
- IntelliJ IDEA (or another IDE)

---

## ⚙️ Getting Started

### 1️⃣ Clone the repository

```bash
  git clone https://github.com/<your-username>/movierama.git
cd movierama
```
### #️⃣ Start the PostgreSQL container
Run the following command to spin up PostgreSQL using Podman:

```bash
  podman compose up
```

ℹ️ *You can inspect or connect to the DB locally using IntelliJ's Database tabl.*

### 3️⃣ Run the backend from IntelliJ (recommended)
- Open the project in IntelliJ.
- Navigate to MovieramaApplication.java.
- Click ▶️ Run (or Debug).

App should start on: http://localhost:8080

### 4️⃣ Access Swagger UI
Once the app starts, access the Swagger UI at:

```bash
  http://localhost:8080/swagger-ui.html
This gives you access to interact with the API directly during development.
```
