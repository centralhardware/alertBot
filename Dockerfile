FROM gradle:jdk23-graal as gradle

COPY ./ ./

RUN gradle installDist

FROM openjdk:23-slim

WORKDIR /znatokiBot

COPY --from=gradle /home/gradle/build/install/alertBOt .

RUN apt-get update && apt-get install -y curl fonts-dejavu fontconfig && apt-get clean && rm -rf /var/lib/apt/lists/*
HEALTHCHECK --interval=30s --timeout=5s --start-period=5s --retries=3 \
  CMD curl --fail http://localhost:81/health || exit 1


CMD ["bin/alertBOt"]
