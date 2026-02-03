FROM node:18-alpine AS frontend-build
WORKDIR /frontend

ARG REACT_APP_API_URL=http://localhost:8080/api
ENV REACT_APP_API_URL=$REACT_APP_API_URL

COPY frontend/package*.json ./
RUN npm install

COPY frontend ./
RUN npm run build

FROM maven:3.9.6-eclipse-temurin-21 AS backend-build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
COPY --from=frontend-build /frontend/build ./src/main/resources/static
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend-build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]