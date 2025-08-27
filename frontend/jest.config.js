module.exports = {
	testEnvironment: 'jsdom',
	moduleNameMapper: {
		'\\.(css|less|scss|sass)$': 'identity-obj-proxy'
	},
	transform: {
		'^.+\\.(ts|tsx|js|jsx)$': 'babel-jest'
	},
	setupFilesAfterEnv: [
		'<rootDir>/src/setupTests.js'
	]
};
