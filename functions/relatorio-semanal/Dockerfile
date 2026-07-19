# Build e verificacao da funcao dentro do Docker (Java 21 + Maven), independente
# da versao de Java instalada na maquina. O deploy usa Cloud Functions gen2
# (`gcloud functions deploy`), que empacota a partir de target/deployment.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B clean package
