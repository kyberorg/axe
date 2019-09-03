FROM yasys/db-aware-openjdk:latest
VOLUME /tmp
MAINTAINER Alexander Muravya (aka kyberorg) <kyberorg@yadev.eu>
ADD ./target/yals.jar /app/
COPY ./docker-entrypoint.sh /
RUN sh -c 'chmod +x /docker-entrypoint.sh'
ENTRYPOINT ./docker-entrypoint.sh
EXPOSE 8080
