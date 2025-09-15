/// <reference lib="webworker" />

// This is the "Offline page" service worker

declare const self: ServiceWorkerGlobalScope & {
  __WB_MANIFEST: any;
};

// Workbox manifest placeholder

self.addEventListener("install", (event: any) => {
  event.waitUntil(
    caches.open("impulse-static-v1").then((cache) => cache.addAll(["/", "/index.html"])),
  );
});

self.addEventListener("fetch", (event: any) => {
  event.respondWith(caches.match(event.request).then((resp) => resp || fetch(event.request)));
});

export {};
