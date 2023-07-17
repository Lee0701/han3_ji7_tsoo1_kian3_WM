FROM amazoncorretto:11
WORKDIR /usr/src/app

COPY . .

EXPOSE 8080

CMD ["./gradlew", "run"]
