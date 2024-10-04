FROM gradle:jdk21-graal as gradle

COPY ./ ./

RUN gradle shadowJar

FROM findepi/graalvm:java21

WORKDIR /znatokiBot

COPY --from=gradle /home/gradle/build/libs/shadow-1.0-SNAPSHOT-all.jar .

RUN apt-get update && apt-get install -y curl fonts-dejavu fontconfig && apt-get clean && rm -rf /var/lib/apt/lists/*
EXPOSE 80
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
  CMD curl --fail http://localhost:80/health || exit 1


CMD ["java", "-jar", "shadow-1.0-SNAPSHOT-all.jar"]