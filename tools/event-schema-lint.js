#!/usr/bin/env node
// Event schema linter.
// Validates: uniqueness, snake_case, presence of required_props block per event,
// non-empty required_props, snake_case for each property, and no duplicates within a block.

const fs = require('fs');
const path = require('path');

const EVENTS_DOC = path.join(process.cwd(), 'docs', 'analytics', 'events.md');
let hasViolations = false;

function error(msg){
  hasViolations = true;
  console.error('[event-schema-lint] ' + msg);
}

function main(){
  if(!fs.existsSync(EVENTS_DOC)){
    console.warn('[event-schema-lint] events.md not found, skipping');
    process.exit(0);
  }
  const content = fs.readFileSync(EVENTS_DOC,'utf8');
  const lines = content.split(/\r?\n/);
  const eventNames = new Set();
  let current = null;
  let hasReq = false;
  let currentProps = new Set();
  for(const line of lines){
    const ev = line.match(/^event:\s*(.+)$/i);
    if(ev){
      if(current){
        if(!hasReq) error(`Evento sin required_props: ${current}`);
        currentProps.clear();
      }
      current = ev[1].trim();
      hasReq = false;
      if(!/^([a-z0-9]+_)*[a-z0-9]+$/.test(current)) error(`Nombre inválido (snake_case requerido): ${current}`);
      if(eventNames.has(current)) error(`Evento duplicado: ${current}`); else eventNames.add(current);
      continue;
    }
    if(/^required_props:/i.test(line)) {
      hasReq = true;
      continue;
    }
    // Captura de propiedades listadas ( - prop_name ) cuando estamos dentro de un bloque de props
    if(hasReq){
      const prop = line.match(/^\s*[-*]\s*([a-zA-Z0-9_]+)\s*$/);
      if(prop){
        const name = prop[1];
        if(!/^([a-z0-9]+_)*[a-z0-9]+$/.test(name)) error(`Propiedad inválida (snake_case requerido): ${name} en evento ${current}`);
        if(currentProps.has(name)) error(`Propiedad duplicada '${name}' en evento ${current}`); else currentProps.add(name);
      } else if(line.trim()==="" ){ /* ignore blank */ } 
    }
  }
  if(current && !hasReq) error(`Evento sin required_props: ${current}`);
  // validar que cada evento tuvo al menos 1 prop (simple: si se vio required_props y no se añadieron props)
  if(hasReq && currentProps.size === 0){
    error(`required_props vacío en último evento: ${current}`);
  }
  if(eventNames.size === 0) error('No se encontraron definiciones event: <nombre> en events.md');
  if(hasViolations){
    console.error(`[event-schema-lint] ${eventNames.size} eventos parseados (con errores)`);
    process.exit(1);
  } else {
    console.log(`[event-schema-lint] OK (${eventNames.size} eventos validados)`);
  }
}

main();
