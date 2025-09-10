# Impulse Frontend (local demo)

Run the frontend dev server (PowerShell):

```powershell
cd C:\Users\Dieg0\Impulse\frontend
# $env:PORT=3001; $env:BROWSER='none'; npm start
npm start
```

Demo credentials (mock):
- Email: demo@impulse.test

Notes:
- The frontend ships a mock backend fallback (in `src/services/mockStore.ts`) so you can exercise create challenge, upload evidence and validator flows without the real backend.
- To integrate with a real backend, set `REACT_APP_API_BASE` environment variable when running.
