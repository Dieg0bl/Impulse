#!/usr/bin/env node
const fs=require('fs');
function be(){
	const f='backend/src/main/resources/application-flags.yml';
	if(!fs.existsSync(f)) return [];
	return fs.readFileSync(f,'utf8')
		.split(/\r?\n/)
		.map(l=>l.trim())
		.filter(l=>l && !l.startsWith('#'))
		.map(l=>{ const m=l.match(/^([a-z0-9_]+\.[a-z0-9_]+):/); return m?m[1]:null; })
		.filter(Boolean);
}
function fe(){
	const f='frontend/src/config/flags.ts';
	if(!fs.existsSync(f)) return [];
	return [...fs.readFileSync(f,'utf8').matchAll(/'([a-z0-9_.]+)'\s*:/gi)].map(m=>m[1]);
}
const beSet=new Set(be());
const feSet=new Set(fe());
const onlyBe=[...beSet].filter(k=>!feSet.has(k)); const onlyFe=[...feSet].filter(k=>!beSet.has(k));
if(onlyBe.length||onlyFe.length){console.error('❌ Flags desincronizados'); if(onlyBe.length) console.error('  Faltan en FE:', onlyBe.join(', ')); if(onlyFe.length) console.error('  Faltan en BE:', onlyFe.join(', ')); process.exit(1);} else console.log('✅ Flags FE/BE sincronizados ('+beSet.size+')');
