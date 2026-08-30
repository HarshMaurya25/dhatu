# DHĀTU — Ancient Indian Metallurgy
### An Interactive Digital Archive

---

## Project Overview

DHĀTU is a three-page educational research archive. The frontend is served **directly by the Spring Boot application** (Thymeleaf / static resources), so all API calls use `window.location.origin` — no environment variable or hardcoded URL is ever needed.

---

## Pages

| Page | File | Purpose |
|---|---|---|
| Home | `index.html` | Historical introduction, metallurgy overview, CTA to Research Assistant |
| Research Assistant | `chat.html` | Streaming RAG chatbot — real-time response display |
| Archive Administration | `admin.html` | PDF upload for expanding the knowledge base |

---

## Project Structure

```
dhatu/
├── index.html              ← Home page
├── chat.html               ← Research Assistant (streaming chat)
├── admin.html              ← Archive Administration (PDF upload)
├── README.md
│
└── src/
    ├── styles/
    │   ├── shared.css      ← Design tokens, nav, footer (all pages)
    │   ├── home.css        ← Hero, about, metallurgy grid, CTA
    │   ├── chat.css        ← Chat panel, streaming messages, sidebar
    │   └── admin.css       ← Upload form, code display, status
    │
    ├── js/
    │   └── shared.js       ← Nav, hamburger, language, scroll animations
    │
    └── api/
        ├── config.js       ← API origin (window.location.origin) + endpoint builders
        ├── chatApi.js      ← Streaming fetch with AbortController
        └── adminApi.js     ← Multipart PDF upload
```

---

## Deployment

Place all files in your Spring Boot `src/main/resources/static/` directory (or `src/main/resources/templates/` if using Thymeleaf).

Spring Boot serves the frontend at `http://localhost:8080/` and the API at `http://localhost:8080/ai/...`.

Because the frontend and backend share the same origin, **no CORS configuration is needed** and no URL environment variable is required. `config.js` uses `window.location.origin` which automatically resolves to the correct host in every environment.

---

## Backend API Contract

### Chat — Research Assistant

```
Controller: ChatBotController
GET /ai/bot/ask

Parameters:
  question  (String)  — the user's question text (URL-encoded)
  name      (String)  — user's name / session identifier

Response: Flux<String> — streamed plain-text chunks
```

Frontend calls `response.body.getReader()` and appends each chunk to a single message bubble as it arrives. The user sees the answer being typed in real time.

### Admin — PDF Upload

```
Controller: DataInputController
POST /ai/admin/upload

Query parameter:
  code  (String)  — 6-digit administrator access code

Body: multipart/form-data
  Field name: pdf   ← MUST match @RequestParam MultipartFile pdf

Response: Boolean (accepted: 202)
```

> **Critical:** The FormData field name is `pdf`, matching the Spring `@RequestParam MultipartFile pdf`. Using any other name (e.g. `file`) will cause the backend to reject the request.
>
> **Do NOT set `Content-Type` manually on the fetch call.** The browser sets `multipart/form-data; boundary=...` automatically. Setting it manually breaks the boundary delimiter.

---

## Static File Setup (Spring Boot)

```
src/main/resources/
└── static/
    ├── index.html
    ├── chat.html
    ├── admin.html
    └── src/
        ├── styles/
        │   ├── shared.css
        │   ├── home.css
        │   ├── chat.css
        │   └── admin.css
        └── js/
            ├── shared.js
            └── api/
                ├── config.js
                ├── chatApi.js
                └── adminApi.js
```

Spring Boot will serve these at `http://localhost:8080/index.html` etc. automatically.

---

## API Summary

| Page | Call | Endpoint | Params / Body |
|---|---|---|---|
| chat.html | GET (streamed) | `/ai/bot/ask` | `?question=...&name=...` |
| admin.html | POST | `/ai/admin/upload` | `?code=...` + `FormData { pdf: <File> }` |

---

## Checklist

- [x] `index.html` — Home
- [x] `chat.html` — Research Assistant  
- [x] `admin.html` — Archive Administration
- [x] Shared CSS (`shared.css`) across all pages
- [x] Centralised API config (`config.js`) using `window.location.origin`
- [x] Streaming chat (`chatApi.js`) — `response.body.getReader()` + `TextDecoder`
- [x] AbortController stop/cancel button
- [x] Admin upload (`adminApi.js`) — FormData field `pdf`, no manual Content-Type
- [x] Correct `?question=` and `?name=` params matching `ChatBotController`
- [x] Correct `?code=` param and `pdf` field matching `DataInputController`
- [x] Voice-to-text (Web Speech API)
- [x] Username persisted in `localStorage`
- [x] Mobile responsive (320px – 1440px+)
- [x] Accessible: ARIA labels, roles, focus styles, reduced-motion
- [x] No hardcoded URLs anywhere — `window.location.origin` only

---

*DHĀTU — Ancient Indian Metallurgy. An educational research archive.*
