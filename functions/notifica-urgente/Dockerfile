# ---------------------------------------------------------------------------
# Build e verificacao da funcao dentro do Docker (garante Java 21 + Maven
# identicos ao CI, independente da versao de Java instalada na maquina local).
#
# NOTA: no runtime, o deploy usa Cloud Functions gen2 (`gcloud functions deploy`),
# que constroi a imagem final via buildpacks a partir de `target/deployment`.
# Este Dockerfile serve para compilar, testar e empacotar de forma reprodutivel.
# ---------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache de dependencias (reaproveita camada enquanto o pom nao muda)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Compila, testa e empacota (gera target/deployment para o gcloud)
COPY src ./src
RUN mvn -B clean package
