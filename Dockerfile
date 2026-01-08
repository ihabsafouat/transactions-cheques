# =========================================================
# 1) BUILD STAGE — Maven 3.9 + JDK 17
# =========================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copier uniquement le pom pour profiter du cache Docker
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests dependency:go-offline

# Copier le code
COPY src ./src

# Build (tests désactivés pour l’instant)
RUN --mount=type=cache,target=/root/.m2 mvn -B -DskipTests clean package


# =========================================================
# 2) RUNTIME STAGE — JRE 17 (léger, sécurisé)
# =========================================================
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Utilisateur non-root (bonne pratique sécurité)
RUN useradd -r -u 10001 appuser
USER 10001

COPY --from=build /app/target/*.jar /app/app.jar

ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=3s --start-period=20s --retries=5 \
  CMD wget -qO- http://127.0.0.1:${SERVER_PORT}/actuator/health | grep -q "UP" || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=$SERVER_PORT -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app/app.jar"]

