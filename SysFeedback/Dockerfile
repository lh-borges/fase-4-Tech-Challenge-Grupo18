# ---- Build ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Baixa dependências primeiro (aproveita cache de camada enquanto o pom não muda)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

# Compila e empacota
COPY src ./src
RUN ./mvnw -B -q clean package -DskipTests

# ---- Runtime ----
FROM eclipse-temurin:21-jre
WORKDIR /app

# Fuso horário fixo para manter datas consistentes (Cloud Run roda em UTC por padrão)
ENV TZ=America/Sao_Paulo

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
