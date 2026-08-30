# DHĀTU

A full-stack web application and research archive on ancient Indian metallurgy, built with Spring Boot, Spring AI, pgvector, and a multilingual frontend.

## What is this?

DHĀTU is an interactive archive that documents the metallurgical history of ancient India (such as Wootz steel, zinc distillation at Zawar, and the Iron Pillar of Delhi). It includes:
1. **Archive Website**: Informational pages covering materials, historical context, and techniques.
2. **AI Research Chatbot**: A RAG-powered assistant that answers questions based on indexed reference papers using Spring AI and PostgreSQL pgvector.
3. **Admin Upload**: An admin page protected by a passkey to upload new PDF documents into the vector store.
4. **Multilingual Support**: Supports Hindi (default), English, Marathi, and French without needing page reloads.

---

## Tech Stack

- **Backend**: Java 21, Spring Boot 4.1.1, Spring WebMVC, Spring WebFlux, Spring Data JPA
- **AI & RAG**: Spring AI 2.0.1
  - Embeddings: Google Gemini (`gemini-embedding-001`, 768 dimensions)
  - Chat LLM: Groq / OpenAI compatible endpoint (`gpt-oss-120b`)
  - Vector Store: PostgreSQL with `pgvector` extension (HNSW index, cosine distance)
- **Frontend**: HTML5, CSS, Vanilla JavaScript, Thymeleaf
- **Database / Infra**: Docker Compose (PostgreSQL 16 with pgvector + Adminer)

---

## Project Structure

```
dhatu/
├── compose.yaml                      # Docker compose for postgres (pgvector) and adminer
├── Dockerfile                        # Docker buildfile for the Spring Boot app
├── pom.xml                           # Dependencies & build setup
├── src/main/java/com/project/dhatu/
│   ├── DhatuApplication.java         # Main Spring Boot entry point
│   ├── config/                       # CORS, VectorStore, and tokenizer configs
│   ├── controller/
│   │   ├── ChatBotController.java    # /ai/bot/ask endpoint (reactive flux stream)
│   │   └── DataInputController.java  # /ai/admin/upload endpoint
│   └── service/                      # Chatbot logic, PDF parsing, text chunking
├── src/main/resources/
│   ├── application.yaml              # Main configuration (server port: 8090)
│   ├── application-ai.yaml           # Spring AI model & vector store config
│   ├── application-database.yaml     # Datasource & JPA configuration
│   └── static/                       # Frontend HTML, CSS, and JS
│       ├── index.html                # Archive homepage
│       ├── chat.html                 # Chatbot assistant page
│       └── admin.html                # Admin PDF upload page
└── README.md
```

---

## Getting Started

### Prerequisites
- Java 21
- Maven 3.8+ (or use `./mvnw`)
- Docker & Docker Compose

### 1. Clone the repo
```bash
git clone https://github.com/your-username/dhatu.git
cd dhatu
```

### 2. Set environment variables
Create a `.env` file or export these variables in your terminal:

```bash
# AI API Keys
export API_KEY="your_groq_or_openai_api_key"
export GEMINI_API_KEY="your_gemini_api_key"

# Database Configuration
export URL_DATABASE="jdbc:postgresql://localhost:5455/ai"
export PASSWORD_DATABASE="ai"
```

### 3. Start PostgreSQL + pgvector
```bash
docker compose up -d
```
- PostgreSQL runs on `localhost:5455` (user: `ai`, password: `ai`, db: `ai`).
- Adminer UI is available on `http://localhost:8080`.

### 4. Run the Spring Boot application
```bash
# On Linux / macOS
./mvnw spring-boot:run

# On Windows
mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8090`.

### 5. Application Pages
- **Home**: `http://localhost:8090/` (or `http://localhost:8090/index.html`)
- **Research Chatbot**: `http://localhost:8090/chat.html`
- **Admin Document Upload**: `http://localhost:8090/admin.html` (Default admin code: `202610`)

---

## API Endpoints

### 1. Chatbot Query (Streaming)
- **`GET /ai/bot/ask`**
- **Query Params**:
  - `question` (string, required): User question
  - `name` (string, optional): User name
- **Response**: `Flux<String>` text stream

### 2. Admin PDF Upload
- **`POST /ai/admin/upload`**
- **Query Params**:
  - `code` (string, required): 6-digit admin passkey
- **Body**: `multipart/form-data` with `pdf` file
- **Response**: `202 Accepted` (`true`/`false`)

---

## Frontend Details

- **Deployment URL**: The frontend uses `window.location.origin` for all backend calls, so it works across local and deployed environments without hardcoded URLs.
- **Name Prompt**: When visiting `chat.html`, users are prompted for their name if not already set in `localStorage`.
- **Language Switcher**: Built-in translations for Hindi, English, Marathi, and French stored in client-side state.

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
