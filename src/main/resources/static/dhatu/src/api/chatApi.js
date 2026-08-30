/* ═══════════════════════════════════════════════════════════════════
   DHĀTU — CHAT API
   
   Endpoint: GET /ai/bot/ask
   Parameters:
     question  — the user's question string
     name      — username / session identifier

   Response is a Flux<String> streamed from the Spring backend.
   We read it with response.body.getReader() and decode each chunk
   as it arrives, appending to a single assistant message element.
═══════════════════════════════════════════════════════════════════ */
import { endpoints } from './config.js';

/**
 * Stream a chat response from the Spring AI / RAG backend.
 *
 * @param {string}   question   - The user's question
 * @param {string}   username   - Name/identifier sent as `name` param
 * @param {object}   cbs        - UI callbacks (all called from async context)
 * @param {Function} cbs.onChunk   - (chunk: string, accumulated: string) → void
 * @param {Function} cbs.onDone    - (fullText: string) → void
 * @param {Function} cbs.onError   - (message: string) → void
 * @returns {AbortController}    Caller calls .abort() to cancel streaming
 */
export function streamChatResponse(question, username, { onChunk, onDone, onError }) {
  const ctrl    = new AbortController();
  const { signal } = ctrl;

  // 30-second hard timeout — abort if the backend stalls
  const TIMEOUT_MS = 30_000;
  const timer = setTimeout(() => ctrl.abort('timeout'), TIMEOUT_MS);

  (async () => {
    let accumulated = '';
    try {
      const url = endpoints.ask(question, username);
      const res = await fetch(url, { signal });

      if (!res.ok) {
        const body = await res.text().catch(() => '');
        throw new Error(`HTTP ${res.status}${body ? ': ' + body : ''}`);
      }

      // Spring's Flux<String> streams the body as plain text chunks
      const reader  = res.body.getReader();
      const decoder = new TextDecoder('utf-8');

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        const chunk = decoder.decode(value, { stream: true });
        accumulated += chunk;
        onChunk(chunk, accumulated);
      }

      // Flush any buffered tail bytes
      const tail = decoder.decode();
      if (tail) {
        accumulated += tail;
        onChunk(tail, accumulated);
      }

      clearTimeout(timer);
      onDone(accumulated);

    } catch (err) {
      clearTimeout(timer);
      if (err.name === 'AbortError') {
        // Could be user-triggered (Stop button) or our timeout
        onError('The archive took too long to respond. Please try again.');
      } else {
        console.error('[chatApi] stream error:', err);
        onError('Unable to reach the archive at this moment. Please check your connection.');
      }
    }
  })();

  return ctrl;
}
