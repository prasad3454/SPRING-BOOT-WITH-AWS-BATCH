FROM openjdk:17
RUN mkdir /app
ADD target/SpringBatch-With-AWSBatch-0.0.1-SNAPSHOT.jar /app/SpringBatch-With-AWSBatch-0.0.1-SNAPSHOT.jar
WORKDIR /app
ENTRYPOINT ["java", "-jar", "SpringBatch-With-AWSBatch-0.0.1-SNAPSHOT.jar"]
