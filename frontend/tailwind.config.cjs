module.exports = {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        'background': 'rgb(var(--color-background) / <alpha-value>)',
        'foreground': 'rgb(var(--color-text-primary) / <alpha-value>)',
        'border': 'rgb(var(--color-border) / <alpha-value>)',
      },
      boxShadow: {
        'medium': '0 4px 6px rgba(0,0,0,0.08)'
      }
    }
  },
  plugins: [],
}
