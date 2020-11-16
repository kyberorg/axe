ARG JAVA_BASE=11-jdk
FROM openjdk:${JAVA_BASE}
ENV JAVA_VERSION=${JAVA_BASE}

VOLUME /tmp

COPY ./target/yals.jar /app/
COPY ./docker-entrypoint.sh /

RUN sh -c 'chmod +x /docker-entrypoint.sh'
ENTRYPOINT ./docker-entrypoint.sh

EXPOSE 8080
