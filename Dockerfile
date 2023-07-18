FROM amazoncorretto:11
WORKDIR /usr/src/app

COPY . .

RUN ./gradlew assemble

EXPOSE 8080

CMD ["./gradlew", "run"]
