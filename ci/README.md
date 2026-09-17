# CI/CD files

Copy these files into the root of the existing multilingual-pdf-validator repository.

## Included

- Jenkinsfile — Jenkins declarative pipeline
- Dockerfile — Java 17 + Maven test container
- .dockerignore — Docker exclusions
- .gitignore — Java/Maven exclusions
- .github/workflows/maven-ci.yml — GitHub Actions CI

## Jenkins prerequisites

Configure Jenkins global tools with these names:

- JDK17
- Maven3

If your Jenkins agent already provides Java 17 and Maven, remove the `tools`
block from Jenkinsfile.

## Local Docker test

docker build -t multilingual-pdf-validator .
docker run --rm multilingual-pdf-validator

## Recommended flow

Git push -> GitHub Actions -> Maven tests

and/or

Git push -> Jenkins webhook -> Maven tests -> JUnit report
