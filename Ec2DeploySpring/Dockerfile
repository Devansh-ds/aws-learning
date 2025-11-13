FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/spring_sql_aws.jar spring_aws.jar

ENTRYPOINT [ "java", "-jar", "spring_aws.jar" ]