# README

<https://medium.com/comsystoreply/authentication-with-firebase-auth-and-spring-security-fcb2c1dc96d>

## Prerequisites

- JDK 18
- Maven

```
$env:M2_HOME = 'C:\a\software\apache-maven-3.8.7-java11'
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-11.0.16.1'
$env:PATH = $env:M2_HOME + '\bin;' + $env:JAVA_HOME + '\bin;' + $env:PATH
```

## Compile

```bash
mvn clean package
```

## Run

```bash
mvn spring-boot:run

./mvnw spring-boot:run
```

## Generate Database Schema

<https://docs.liquibase.com/start/install/liquibase-windows.html>

```
liquibase "--url=jdbc:h2:./liquibase;DB_CLOSE_ON_EXIT=FALSE" "--username=sa" "--password=" "--driver=org.h2.Driver" "--changelog-file=dbchangelog.xml" generate-changelog
```
