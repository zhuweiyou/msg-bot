FROM openjdk:21-jdk-slim as builder
COPY . ./app
WORKDIR /app
RUN chmod +x ./mvnw
RUN ./mvnw -B clean package -Dmaven.test.skip=true -Dautoconfig.skip

FROM openjdk:21-jdk-slim
COPY --from=builder /app/target/*.jar /app/app.jar
EXPOSE 8080
ENV TZ=Asia/Shanghai
WORKDIR /app
CMD ["java", "-jar", "app.jar"]
