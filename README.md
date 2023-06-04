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
$env:DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/cosucce'
$env:DATASOURCE_USERNAME = 'postgres'
$env:DATASOURCE_PASSWORD = 'postgres'
$env:CORS_ALLOWED_ORIGINS = 'http://localhost:9000'
$env:TELEGRAM_TOKEN = 'token'
java -jar "-Dspring.profiles.active=prod" ./target/cosucce-0.0.1-SNAPSHOT.jar

./mvnw spring-boot:run
```

## Generate Database Schema

<https://docs.liquibase.com/start/install/liquibase-windows.html>

```
liquibase "--url=jdbc:h2:./liquibase;DB_CLOSE_ON_EXIT=FALSE" "--username=sa" "--password=" "--driver=org.h2.Driver" "--changelog-file=dbchangelog.xml" generate-changelog
```

## Check differences between 2 databases

```
$oldDb="./db/00002/liquibase"
liquibase "--changelog-file=dbchangelog.xml" "--url=jdbc:h2:${oldDb};DB_CLOSE_ON_EXIT=FALSE" "--username=sa" "--password=" "--referenceUrl=jdbc:h2:./liquibase;DB_CLOSE_ON_EXIT=FALSE" "--referenceUsername=sa" "--referencePassword=" diff-changelog
```

## OpenApi definition

| Description  | Local                                                       | Prod                                                                     |
|--------------|-------------------------------------------------------------|--------------------------------------------------------------------------|
| OpenApi WEB  | [/swagger-ui/](http://localhost:8080/swagger-ui/index.html) | [/swagger-ui/](https://purple-breeze-8455.fly.dev/swagger-ui/index.html) |
| OpenApi JSON | [/v3/api-docs](http://localhost:8080/v3/api-docs)           | [/v3/api-docs](https://purple-breeze-8455.fly.dev/v3/api-docs)           |
| OpenApi YAML | [/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml) | [/v3/api-docs.yaml](https://purple-breeze-8455.fly.dev/v3/api-docs.yaml) |

## Maven upgrade

```
# update parent
mvn versions:update-parent "-DparentVersion=(2.7.0,3.0.0)" "-DgenerateBackupPoms=false"

# update dependencies
mvn versions:use-latest-versions "-Dincludes=org.springdoc:springdoc-openapi-ui,com.tngtech.archunit:archunit-junit5" "-DgenerateBackupPoms=false"

# build and check that application works
mvn clean package
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
flyctl launch --image vitalegi/cosucce-be:0.1

# replace placeholders with proper values generated at the previous step
flyctl secrets set "DATASOURCE_URL=jdbc:postgresql://insert-here-database-address.internal:5432/database-name"
flyctl secrets set "DATASOURCE_USERNAME=database-username"
flyctl secrets set "DATASOURCE_PASSWORD=database-password"
flyctl secrets set "CORS_ALLOWED_ORIGINS=origins"
flyctl secrets set "TELEGRAM_TOKEN=telegram token"
```

### Build Image

```
$env:M2_HOME = 'C:\a\software\apache-maven-3.8.7-java11'
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-11.0.16.1'
$env:PATH = $env:M2_HOME + '\bin;' + $env:JAVA_HOME + '\bin;' + $env:PATH

# create jar
./mvnw clean package

# build image
docker build -t vitalegi/cosucce-be:0.3 .
```

### Publish image

```
docker push vitalegi/cosucce-be:0.3
```

### Install image

```
flyctl deploy --image vitalegi/cosucce-be:0.3
```

### Complete deploy script

```
$VERSION='0.19'
$env:M2_HOME = 'C:\a\software\apache-maven-3.8.7-java11'
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-11.0.16.1'
$env:PATH = $env:M2_HOME + '\bin;' + $env:JAVA_HOME + '\bin;' + $env:PATH

mvn versions:set "-DgenerateBackupPoms=false" "-DnewVersion=${VERSION}"
./mvnw clean package
docker build -t vitalegi/cosucce-be:${VERSION} .
docker push vitalegi/cosucce-be:${VERSION}
git add pom.xml
git add README.md
git commit -m "release v${VERSION}"
flyctl deploy --image vitalegi/cosucce-be:${VERSION}
```

### Autoscaling

```
flyctl autoscale set min=1 max=1
```
