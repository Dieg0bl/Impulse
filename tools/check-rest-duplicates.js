#!/usr/bin/env node
const fs = require('fs');
const path = require('path');
function walk(d){return fs.readdirSync(d).flatMap(f=>{const p=path.join(d,f);const s=fs.statSync(p);return s.isDirectory()?walk(p):[p];});}
const base='backend/src/main/java';
if(!fs.existsSync(base)){console.error('backend src no encontrado');process.exit(0);} 
const files=walk(base).filter(f=>f.endsWith('.java'));
const patterns=[
  [/@GetMapping\(\s*value\s*=\s*"([^"]+)"/g,'GET'],
  [/@PostMapping\(\s*value\s*=\s*"([^"]+)"/g,'POST'],
  [/@PutMapping\(\s*value\s*=\s*"([^"]+)"/g,'PUT'],
  [/@DeleteMapping\(\s*value\s*=\s*"([^"]+)"/g,'DELETE'],
  [/@PatchMapping\(\s*value\s*=\s*"([^"]+)"/g,'PATCH'],
  [/@RequestMapping\(\s*value\s*=\s*"([^"]+)"[^)]*RequestMethod\.GET/g,'GET'],
  [/@RequestMapping\(\s*value\s*=\s*"([^"]+)"[^)]*RequestMethod\.POST/g,'POST'],
  [/@RequestMapping\(\s*value\s*=\s*"([^"]+)"[^)]*RequestMethod\.PUT/g,'PUT'],
  [/@RequestMapping\(\s*value\s*=\s*"([^"]+)"[^)]*RequestMethod\.DELETE/g,'DELETE'],
  [/@RequestMapping\(\s*value\s*=\s*"([^"]+)"[^)]*RequestMethod\.PATCH/g,'PATCH']
];
let dup=false; const seen=new Map();
for(const f of files){
  const txt=fs.readFileSync(f,'utf8');
  const classBaseM=txt.match(/@RequestMapping\(\s*value\s*=\s*"([^"]+)"/);
  const classBase=classBaseM?classBaseM[1].replace(/\/$/,''):'';
  for(const [rg,method] of patterns){
    rg.lastIndex=0; let m; while((m=rg.exec(txt))){
      const full=(classBase+'/'+m[1]).replace(/\/+/g,'/').replace(/\/$/,'');
      const key=method+' '+full;
      if(seen.has(key)){console.error('❌ Ruta duplicada:',key);console.error('  - '+seen.get(key));console.error('  - '+f);dup=true;} else seen.set(key,f);
    }}
}
if(dup) process.exit(1); else console.log('✅ Rutas REST sin duplicados ('+seen.size+')');
