#!/usr/bin/env node
// Fails if any TS/TSX/JS/Java file exceeds a configurable max line count (default 800) excluding migration SQL.
const fs = require('fs');
const path = require('path');
const MAX = parseInt(process.env.MAX_FILE_LINES || '800',10);
const exts = new Set(['.ts','.tsx','.js','.java']);
let failed = false;
function walk(dir){
  for(const e of fs.readdirSync(dir)){
    const p = path.join(dir,e);
    const stat = fs.statSync(p);
    if(stat.isDirectory()){
      if(e==='node_modules' || e==='.git' || e==='target') continue;
      walk(p);
    } else {
      const ext = path.extname(e);
      if(exts.has(ext)){
        if(p.includes(path.join('db','migration'))) continue; // ignore migrations
        const lines = fs.readFileSync(p,'utf8').split(/\r?\n/).length;
        if(lines>MAX){
          console.error(`❌ Archivo demasiado largo (${lines} líneas > ${MAX}): ${p}`);
          failed = true;
        }
      }
    }
  }
}
walk(process.cwd());
if(failed){
  console.error('Fallo: archivos exceden tamaño permitido. Refactorizar para cumplir anti-dup.');
  process.exit(1);
} else {
  console.log('✅ Tamaño de archivos dentro de límites');
}
