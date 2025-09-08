self.addEventListener('install', (event: any) => {
  event.waitUntil(
    caches.open('impulse-static-v1').then(cache => cache.addAll([
      '/',
      '/index.html'
    ]))
  )
})

self.addEventListener('fetch', (event: any) => {
  event.respondWith(
    caches.match(event.request).then(resp => resp || fetch(event.request))
  )
})
