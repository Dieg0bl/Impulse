/// <reference types="vite/client" />
/// <reference types="vite-plugin-pwa/react" />

interface ImportMetaEnv {
  readonly VITE_API_BASE: string;
  readonly VITE_DEMO_EMAIL: string;
  readonly VITE_DEMO_PASSWORD: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
