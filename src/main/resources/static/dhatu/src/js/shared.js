/* ═══════════════════════════════════════════════════════════════════
   DHĀTU — SHARED JS
   Initialised on every page: nav, language, scroll animations, footer
═══════════════════════════════════════════════════════════════════ */
'use strict';

/* ── LANGUAGE ────────────────────────────────────────────────────── */
export const LANG_CODES = { en: 'en', hi: 'hi', mr: 'mr', fr: 'fr' };

export function getLang() {
  return localStorage.getItem('dhatu_lang') || 'en';
}

export function setLang(lang) {
  if (!LANG_CODES[lang]) return;
  localStorage.setItem('dhatu_lang', lang);
  // Sync all selects on the page
  document.querySelectorAll('.lang-select').forEach(el => { el.value = lang; });
}

export function initLangSelects() {
  const current = getLang();
  document.querySelectorAll('.lang-select').forEach(sel => {
    sel.value = current;
    sel.addEventListener('change', e => setLang(e.target.value));
  });
}

/* ── NAV SCROLL SHADOW ───────────────────────────────────────────── */
export function initNav() {
  const nav = document.getElementById('nav');
  if (!nav) return;

  // Scroll shadow
  const onScroll = () => nav.classList.toggle('scrolled', window.scrollY > 40);
  window.addEventListener('scroll', onScroll, { passive: true });
  onScroll();

  // Hamburger
  const ham    = document.getElementById('nav-ham');
  const drawer = document.getElementById('nav-drawer');
  if (ham && drawer) {
    ham.addEventListener('click', () => {
      const open = drawer.classList.toggle('open');
      ham.setAttribute('aria-expanded', String(open));
    });
    // Close on any drawer link
    drawer.querySelectorAll('a').forEach(a => {
      a.addEventListener('click', () => {
        drawer.classList.remove('open');
        ham.setAttribute('aria-expanded', 'false');
      });
    });
    // Close on outside click
    document.addEventListener('click', e => {
      if (!nav.contains(e.target) && !drawer.contains(e.target)) {
        drawer.classList.remove('open');
        ham.setAttribute('aria-expanded', 'false');
      }
    });
  }
}

/* ── SCROLL-TRIGGERED FADE-UPS ───────────────────────────────────── */
export function initScrollAnimations() {
  const obs = new IntersectionObserver(entries => {
    entries.forEach(e => { if (e.isIntersecting) e.target.classList.add('visible'); });
  }, { threshold: 0.08, rootMargin: '0px 0px -36px 0px' });

  document.querySelectorAll('.fade-up').forEach(el => obs.observe(el));
}

/* ── ORNAMENT HELPER ─────────────────────────────────────────────── */
export function orn() {
  return `<div class="orn-wrap" aria-hidden="true">
    <div class="orn">
      <div class="orn-line"></div>
      <span class="orn-glyph">✦ ✦ ✦</span>
      <div class="orn-line"></div>
    </div>
  </div>`;
}

/* ── BOOT ────────────────────────────────────────────────────────── */
document.addEventListener('DOMContentLoaded', () => {
  initNav();
  initLangSelects();
  initScrollAnimations();
});
