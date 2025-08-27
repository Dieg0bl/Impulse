# tools/

This directory contains repository quality checks used by CI and PRs.

Key scripts (invokable with `node tools/<script>.js`):

- `check-rest-duplicates.js` — verifies no duplicate REST endpoints in `backend/src/main/java`.
- `check-flyway-versions.js` — verifies Flyway migration versions are unique in `backend/src/main/resources/db/migration`.
- `check-flags-sync.js` — verifies feature flags are synchronized between `backend` and `frontend` definitions.
- `check-file-size.js` — enforces a max file size (lines) for code files.
- `event-schema-lint.js` — validates `docs/analytics/events.md` event schema.

Guidance
1. Node v18+ recommended.
2. Run checks locally from repo root:

```powershell
node tools/check-rest-duplicates.js
node tools/check-flyway-versions.js
node tools/check-flags-sync.js
node tools/check-file-size.js
node tools/event-schema-lint.js
```

If you want a single runner, add a `scripts` entry in the root `package.json` such as:

```json
"scripts": {
  "tools:all": "node tools/check-rest-duplicates.js && node tools/check-flyway-versions.js && node tools/check-flags-sync.js && node tools/check-file-size.js && node tools/event-schema-lint.js"
}
```

If a script is intentionally unimplemented, remove it to avoid confusion.
