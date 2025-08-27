// Minimal jsdom-like globals for tests that import modules which touch DOM at module scope.
// Don't override Jest's jsdom globals; only provide localStorage if missing.
if (typeof global.localStorage === 'undefined') {
  global.localStorage = {
    _data: {},
    getItem(k) { return this._data[k] || null; },
    setItem(k, v) { this._data[k] = String(v); },
    removeItem(k) { delete this._data[k]; }
  };
}
