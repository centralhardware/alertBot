FROM gradle:jdk21-graal as gradle

COPY ./ ./

RUN gradle fatJar

FROM findepi/graalvm:java21

WORKDIR /znatokiBot

COPY --from=gradle /home/gradle/build/libs/currencyAlert-jvm-1.0-SNAPSHOT.jar .

CMD ["java", "-jar", "currencyAlert-jvm-1.0-SNAPSHOT.jar"]