CI for backend

This folder contains CI-related helpers.

Workflow (.github/workflows/ci.yml) will run `mvn -B -V -e -DskipTests=false clean verify` in the `backend` folder.

Artifacts uploaded on failure/success:
- junit-reports -> backend/target/surefire-reports
- jacoco-report -> backend/target/site/jacoco

Notes:
- The project uses JDK 21 in CI.
- If modules fail to compile, run `mvn -B -V -e -DskipTests=false clean verify` locally in the `backend` directory to get full logs.
