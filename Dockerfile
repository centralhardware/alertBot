FROM gradle:jdk24-graal as gradle

COPY ./ ./

RUN gradle installDist

FROM openjdk:24-slim

WORKDIR /znatokiBot

COPY --from=gradle /home/gradle/build/install/alertBOt .

RUN apt-get update && apt-get install -y fonts-dejavu fontconfig && apt-get clean && rm -rf /var/lib/apt/lists/*

CMD ["bin/alertBOt"]
