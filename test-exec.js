const { exec } = require('child_process');

console.log('Probando exec...');

exec('echo "Hola desde exec"', (error, stdout, stderr) => {
  if (error) {
    console.log('Error:', error.message);
    return;
  }
  console.log('Salida:', stdout);
  console.log('Todo bien con exec');
  
  setTimeout(() => {
    process.exit(0);
  }, 1000);
});
