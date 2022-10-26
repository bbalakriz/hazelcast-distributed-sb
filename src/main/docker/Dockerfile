FROM maven:3.6.3-openjdk-11 as builder
WORKDIR /project
COPY . /project/
RUN mvn package -DskipTests -B

FROM adoptopenjdk:11-jre-hotspot
COPY --from=builder /project/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]