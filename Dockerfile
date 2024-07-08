FROM gradle:jdk21-graal as gradle

COPY ./ ./

RUN gradle shadowJar

FROM findepi/graalvm:java21

WORKDIR /znatokiBot

COPY --from=gradle /home/gradle/build/libs/shadow-1.0-SNAPSHOT-all.jar .

RUN apt update; apt install fonts-dejavu fontconfig -y

CMD ["java", "-jar", "shadow-1.0-SNAPSHOT-all.jar"]