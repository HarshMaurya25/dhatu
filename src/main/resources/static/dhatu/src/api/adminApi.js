/* ═══════════════════════════════════════════════════════════════════
   DHĀTU — ADMIN API

   Endpoint: POST /ai/admin/upload?code=XXXXXX
   Body:     multipart/form-data  —  field name: "pdf"

   Matches DataInputController:
     @RequestParam MultipartFile pdf
     @RequestParam("code") String code

   CRITICAL: Do NOT set Content-Type manually on the fetch call.
   The browser sets "multipart/form-data; boundary=..." automatically.
   Setting it manually breaks the boundary and the server rejects it.
═══════════════════════════════════════════════════════════════════ */
import { endpoints } from './config.js';

/**
 * Upload a PDF to the RAG knowledge base.
 *
 * @param {string} code - 6-digit admin access code (sent as ?code=)
 * @param {File}   file - The PDF File object from the file input
 * @returns {Promise<{ok:boolean, error?:string}>}
 */
export async function uploadPdf(code, file) {
  const formData = new FormData();
  // Field name MUST be "pdf" — matches @RequestParam MultipartFile pdf
  formData.append('pdf', file);

  // Do NOT pass any headers object — let the browser set Content-Type + boundary
  try {
    const res = await fetch(endpoints.upload(code), {
      method: 'POST',
      body: formData,
    });

    // Map HTTP status codes to typed error keys
    if (res.status === 401 || res.status === 403) return { ok: false, error: 'invalid-code' };
    if (res.status === 415)                        return { ok: false, error: 'unsupported-file' };
    if (res.status === 413)                        return { ok: false, error: 'file-too-large' };
    if (!res.ok)                                   return { ok: false, error: 'upload-failed' };

    return { ok: true };
  } catch (err) {
    console.error('[adminApi] upload error:', err);
    return { ok: false, error: 'network' };
  }
}

export const UPLOAD_ERROR_MESSAGES = {
  'invalid-code':    'The access code is incorrect. Please verify and try again.',
  'unsupported-file':'The server rejected the file format. Only PDF documents are accepted.',
  'file-too-large':  'The document is too large. Please reduce its size and try again.',
  'upload-failed':   'The upload failed on the server. Please try again shortly.',
  'network':         'Unable to reach the archive server. Check your connection and ensure the backend is running.',
};
