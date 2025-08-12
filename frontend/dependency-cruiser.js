module.exports = {
  forbidden: [
    { name: 'no-circular', severity: 'error', comment: 'No cycles allowed', from: {}, to: { circular: true } },
    { name: 'no-cross-layer', severity: 'error', comment: 'No layer violations',
      from: { path: '^src/pages' }, to: { path: '^src/services' } }
    // Añadir más reglas según la estructura
  ],
  options: { doNotFollow: { path: 'node_modules' }, exclude: 'node_modules|test|e2e' }
};
