FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM tomcat:9-jdk11
RUN rm -rf /usr/local/tomcat/webapps/ROOT
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war
VOLUME /data
EXPOSE 8080
CMD ["catalina.sh", "run"]
