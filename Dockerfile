FROM openjdk:17-jdk

WORKDIR /app

COPY target/seat-type-management-0.0.1-SNAPSHOT.jar seat-type-management.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "seat-type-management.jar"]