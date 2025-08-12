// Accesibilidad global: helper para focus visible y skip links
import React, { useEffect } from 'react';

export const A11yHelper: React.FC = () => {
  useEffect(() => {
    // Skip link focus
    const skipLink = document.getElementById('skip-to-content');
    if (skipLink) {
      skipLink.addEventListener('click', (e) => {
        const main = document.querySelector('main');
        if (main) {
          (main as HTMLElement).tabIndex = -1;
          (main as HTMLElement).focus();
        }
      });
    }
    // Focus visible polyfill (for keyboard nav)
    function handleKeyDown(e: KeyboardEvent) {
      if (e.key === 'Tab') {
        document.body.classList.add('user-is-tabbing');
      }
    }
    function handleMouseDown() {
      document.body.classList.remove('user-is-tabbing');
    }
    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('mousedown', handleMouseDown);
    return () => {
      window.removeEventListener('keydown', handleKeyDown);
      window.removeEventListener('mousedown', handleMouseDown);
    };
  }, []);
  return null;
};
