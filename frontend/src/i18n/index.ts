import es from './es.json';
import en from './en.json';

export const locales = { es, en };
export type Locale = keyof typeof locales;

export function getLocale(): Locale {
  const lang = localStorage.getItem('lang') || navigator.language.slice(0, 2);
  return lang === 'en' ? 'en' : 'es';
}

export function t(key: string, locale?: Locale): string {
  const l = locale || getLocale();
  const dict = locales[l] || locales['es'];
  const value = key.split('.').reduce((o, k) => (o && o[k] !== undefined ? o[k] : key), dict);
  return typeof value === 'string' ? value : key;
}

export function setLocale(locale: Locale) {
  localStorage.setItem('lang', locale);
  window.location.reload();
}
