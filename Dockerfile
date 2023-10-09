FROM gradle:8.2-jdk17-alpine as builder
COPY . .
RUN ./gradlew --no-daemon bootJar
RUN java -Djarmode=layertools -jar build/libs/mancala-1.0.0.jar extract --destination /extract/

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /extract/dependencies/ ./
COPY --from=builder /extract/spring-boot-loader/ ./
COPY --from=builder /extract/snapshot-dependencies/ ./
COPY --from=builder /extract/application/ ./
CMD java org.springframework.boot.loader.JarLauncher
