# README

## Prerequisites

- JDK 11
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
$env:DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/budget'
$env:DATASOURCE_USERNAME = 'postgres'
$env:DATASOURCE_PASSWORD = 'postgres'
$env:DATASOURCE_PASSWORD = 'postgres'
$env:CORS_ALLOWED_ORIGINS = 'http://localhost:9000'
java -jar "-Dspring.profiles.active=prod" ./target/budget-0.0.1-SNAPSHOT.jar

./mvnw spring-boot:run
```

## Generate Database Schema

<https://docs.liquibase.com/start/install/liquibase-windows.html>

```
liquibase "--url=jdbc:h2:./liquibase;DB_CLOSE_ON_EXIT=FALSE" "--username=sa" "--password=" "--driver=org.h2.Driver" "--changelog-file=dbchangelog.xml" generate-changelog
```

## Release

### Prerequisites

To create the jar:

- JDK 11
- Maven

To create the image:

- Docker
- An image repository (e.g.: <https://hub.docker.com/>)

If you deploy it on <https://fly.io>, the command line tool:

- Flyctl <https://fly.io/docs/hands-on/install-flyctl/>

### Start Docker

### One time activities

```
# creates the fly app + db
flyctl launch --image vitalegi/budget-be:0.1

# replace placeholders with proper values generated at the previous step
flyctl secrets set "DATASOURCE_URL=jdbc:postgresql://insert-here-database-address.internal:5432/database-name"
flyctl secrets set "DATASOURCE_USERNAME=database-username"
flyctl secrets set "DATASOURCE_PASSWORD=database-password"
flyctl secrets set "CORS_ALLOWED_ORIGINS=origins"
```

### Build Image

```
$env:M2_HOME = 'C:\a\software\apache-maven-3.8.7-java11'
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-11.0.16.1'
$env:PATH = $env:M2_HOME + '\bin;' + $env:JAVA_HOME + '\bin;' + $env:PATH

# create jar
./mvnw clean package

# build image
docker build -t vitalegi/budget-be:0.3 .
```

### Publish image

```
docker push vitalegi/budget-be:0.3
```

### Install image

```
flyctl deploy --image vitalegi/budget-be:0.3
```

### Complete deploy script

```
$VERSION='0.2'
$env:M2_HOME = 'C:\a\software\apache-maven-3.8.7-java11'
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-11.0.16.1'
$env:PATH = $env:M2_HOME + '\bin;' + $env:JAVA_HOME + '\bin;' + $env:PATH

mvn versions:set "-DgenerateBackupPoms=false" "-DnewVersion=${VERSION}"
./mvnw clean package
docker build -t vitalegi/budget-be:${VERSION} .
docker push vitalegi/budget-be:${VERSION}
flyctl deploy --image vitalegi/budget-be:${VERSION}
```

### Autoscaling

```
flyctl autoscale set min=0 max=1
```
