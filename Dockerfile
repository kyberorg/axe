ARG JAVA_BASE=11-jdk-openj9
FROM adoptopenjdk:${JAVA_BASE}

VOLUME /tmp

COPY ./target/yalsee.jar /app/
COPY ./docker-entrypoint.sh /

RUN sh -c 'chmod +x /docker-entrypoint.sh'
ENTRYPOINT ./docker-entrypoint.sh

EXPOSE 8080
