console.log('Hola mundo');
console.log('Script funcionando');

setTimeout(() => {
  console.log('Timeout funcionando');
  process.exit(0);
}, 2000);
