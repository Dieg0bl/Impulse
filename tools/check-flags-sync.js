#!/usr/bin/env node
const fs=require('fs');
function be(){if(!fs.existsSync('backend/src/main/resources/application-flags.yml'))return[];return fs.readFileSync('backend/src/main/resources/application-flags.yml','utf8').split(/\r?\n/).reduce((a,l)=>{const s=l.match(/^([a-z0-9_]+):\s*$/i);if(s){a._s=s[1];}else{const k=l.match(/^\s{2}([a-z0-9_]+):\s*(true|false)\s*$/i);if(k&&a._s)a.push(a._s+'.'+k[1]);}return a;},[]);} 
function fe(){if(!fs.existsSync('frontend/src/config/flags.ts'))return[];return [...fs.readFileSync('frontend/src/config/flags.ts','utf8').matchAll(/'([a-z0-9_.]+)'\s*:/gi)].map(m=>m[1]);}
const beSet=new Set(be()); const feSet=new Set(fe());
const onlyBe=[...beSet].filter(k=>!feSet.has(k)); const onlyFe=[...feSet].filter(k=>!beSet.has(k));
if(onlyBe.length||onlyFe.length){console.error('❌ Flags desincronizados'); if(onlyBe.length) console.error('  Faltan en FE:', onlyBe.join(', ')); if(onlyFe.length) console.error('  Faltan en BE:', onlyFe.join(', ')); process.exit(1);} else console.log('✅ Flags FE/BE sincronizados ('+beSet.size+')');
