import type { Config } from "jest";

const config: Config = {
  rootDir: '.',
  testEnvironment: 'jsdom',
  setupFiles: ['<rootDir>/src/test/jest-env-shim.js'],
  setupFilesAfterEnv: ['<rootDir>/src/test/test-setup.ts'],
  transform: {
    '^.+\\.(t|j)sx?$': ['babel-jest', {}],
  },
  transformIgnorePatterns: [
    '/node_modules/(?!(@?react|@?unpkg|nanoid|uuid)/)'
  ],
  moduleNameMapper: {
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy',
    '^@/(.*)$': '<rootDir>/src/$1',
  },
  testRegex: '.*\\.gdpr\\.test\\.(t|j)sx?$',
  // transformIgnorePatterns: ['node_modules/(?!(nanoid|uuid)/)'],
};
export default config;
