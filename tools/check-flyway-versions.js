#!/usr/bin/env node
// Cross-platform Flyway migration version uniqueness check
const fs = require('fs');
const path = require('path');

const MIGRATIONS_DIR = path.join(process.cwd(), 'backend', 'src', 'main', 'resources', 'db', 'migration');
let dup = false;
const seen = new Map();
if(!fs.existsSync(MIGRATIONS_DIR)){
  console.log('Skip Flyway (no dir)');
  process.exit(0);
}
for(const file of fs.readdirSync(MIGRATIONS_DIR)){
  const m = file.match(/^V(\d+)__.*\.sql$/);
  if(m){
    const v = m[1];
    if(seen.has(v)){
      console.error(`❌ Versión duplicada: V${v} (${file} y ${seen.get(v)})`);
      dup = true;
    } else {
      seen.set(v, file);
    }
  }
}
if(dup){
  process.exit(1);
} else {
  console.log('✅ Flyway OK (sin duplicados)');
}
