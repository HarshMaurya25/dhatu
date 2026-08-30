# DHĀTU

A full-stack web application and research archive on ancient Indian metallurgy, built with Spring Boot, Spring AI, pgvector, and a multilingual frontend.

## What is this?

DHĀTU is an interactive archive that documents the metallurgical history of ancient India (such as Wootz steel, zinc distillation at Zawar, and the Iron Pillar of Delhi). It includes:
1. **Archive Website**: Informational pages covering materials, historical context, and techniques.
2. **AI Research Chatbot**: A RAG-powered assistant that answers questions based on indexed reference papers using Spring AI and PostgreSQL pgvector.
3. **Admin Upload**: An admin page protected by a passkey to upload new PDF documents into the vector store.
4. **Multilingual Support**: Supports Hindi (default), English, Marathi, and French without needing page reloads.

---

## System Architecture & Flow

### 1. ETL Ingestion Pipeline
When documents (e.g., PDF research papers) are uploaded via the Admin Portal, they pass through an automated ETL (Extract, Transform, Load) pipeline before being stored in pgvector:

![ETL Pipeline](documentation/ETL%20PIPELINE.png)

1. **Upload**: PDF document is uploaded via `/ai/admin/upload`.
2. **Document Reader (`PagePdfDocumentReader`)**: Parses the raw PDF into structured page-level document objects.
3. **Document Transformer**: Cleans, refines, and formats document text and metadata.
4. **Document Splitter (`TokenTextSplitter`)**: Chunks the document into optimized token windows.
5. **Vector Embedding**: Uses Google Gemini (`gemini-embedding-001`) to generate 768-dimensional dense vector embeddings.
6. **Vector Store**: Persists chunks and embeddings into PostgreSQL `pgvector` with HNSW indexing for fast similarity search.

---

### 2. RAG Query Flow
When a user asks a question in the Research Chatbot, the retrieval and generation pipeline handles contextual enrichment and response streaming:

![RAG Flow](documentation/RAG%20FLOW.png)

1. **Question**: User submits a natural language question via `/ai/bot/ask`.
2. **Chat History Advisor**: Loads and attaches conversational history from PostgreSQL for conversational context.
3. **Rewrite Query Transformer**: The LLM rewrites the incoming query based on past history to form an independent, context-rich search prompt.
4. **Vector Search & Embedding**: The rewritten query is embedded and matched against PostgreSQL `pgvector` using cosine similarity to retrieve the top-$k$ relevant document chunks.
5. **Chat Client & LLM**: The user query and retrieved reference context are injected into the system prompt and sent to the LLM (`gpt-oss-120b`).
6. **Output**: The answer is streamed back reactively to the frontend as a `Flux<String>`.

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
├── documentation/                    # Architecture diagrams (ETL & RAG flow)
│   ├── ETL PIPELINE.png
│   └── RAG FLOW.png
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
