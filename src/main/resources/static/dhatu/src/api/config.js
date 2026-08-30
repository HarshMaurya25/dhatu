/* ═══════════════════════════════════════════════════════════════════
   DHĀTU — API CONFIGURATION
   
   Since the frontend is served by the same Spring Boot application
   (via Thymeleaf / static resources), the API origin is always the
   same as the page origin — no environment variable needed.

   window.location.origin covers all environments automatically:
     Development:  http://localhost:8080
     Production:   https://yourdomain.com
═══════════════════════════════════════════════════════════════════ */

// Always same origin as the frontend — Spring Boot serves both.
const API_BASE_URL = window.location.origin;

export { API_BASE_URL };

/* ── ENDPOINT BUILDERS ───────────────────────────────────────────── */
// Matches actual controllers:
//   ChatBotController  → GET /ai/bot/ask?question=...&name=...
//   DataInputController → POST /ai/admin/upload?code=...  field: pdf

export const endpoints = {
  /**
   * @param {string} question  - User's question text
   * @param {string} username  - Display name / session identifier
   */
  ask: (question, username) => {
    const p = new URLSearchParams({ question, name: username });
    return `${API_BASE_URL}/ai/bot/ask?${p}`;
  },

  /**
   * @param {string} code - 6-digit admin access code
   */
  upload: (code) => `${API_BASE_URL}/ai/admin/upload?code=${encodeURIComponent(code)}`,
};
