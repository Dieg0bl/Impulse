module.exports = {
  root: true,
  parser: "@typescript-eslint/parser",
  parserOptions: {
    ecmaVersion: "latest",
    sourceType: "module",
    ecmaFeatures: { jsx: true },
    project: false
  },
  plugins: ["@typescript-eslint", "import", "jest", "testing-library", "react"],
  env: { es2022: true, node: true, browser: true, jest: true },
  extends: [
    "eslint:recommended",
    "plugin:@typescript-eslint/recommended",
    "plugin:import/recommended",
    "plugin:import/typescript",
    "plugin:jest/recommended",
    "plugin:testing-library/react",
    "plugin:react/recommended"
  ],
  settings: { react: { version: "detect" } },
  rules: {
    "import/no-unresolved": "error",
    "import/no-extraneous-dependencies": ["error", { "devDependencies": ["**/*.test.*", "**/tests/**", "**/jest.*"] }],
    "react/react-in-jsx-scope": "off"
  },
  ignorePatterns: ["dist", "build", "coverage", "node_modules"]
};
