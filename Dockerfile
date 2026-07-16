FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/ShqipBot-1.0.0-jar-with-dependencies.jar /app/bot.jar

# 🔥 KJO E MBAN BOT-IN GJALLË 24/7
ENTRYPOINT ["sh", "-c", "while true; do java -jar bot.jar; echo 'Bot crashed, restarting...'; sleep 2; done"]