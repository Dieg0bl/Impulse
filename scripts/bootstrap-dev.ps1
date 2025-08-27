Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"
Push-Location "frontend"
npm pkg set scripts.prepare="husky"
npm pkg set scripts.lint="eslint ."
npm pkg set scripts.test="jest"
npm pkg set scripts.'test:watch'="jest --watch"
npm pkg set scripts.'check:actions'="actionlint -color"
npm pkg set scripts.check="npm run lint && npm test"
npm i -D jest @types/jest babel-jest @babel/core @babel/preset-env @babel/preset-react @babel/preset-typescript @testing-library/react @testing-library/jest-dom eslint @typescript-eslint/parser @typescript-eslint/eslint-plugin eslint-plugin-import eslint-plugin-jest eslint-plugin-testing-library eslint-plugin-react husky lint-staged identity-obj-proxy @actionlint/cli markdownlint-cli2
npx husky init
Pop-Location
