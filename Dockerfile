FROM adoptopenjdk/openjdk11:jre-11.0.10_9-alpine

ADD /target/homework-*.jar /usr/local/

WORKDIR /usr/local/

EXPOSE 8080

CMD java -jar homework-*.jar
