FROM java:8-jre
VOLUME /tmp
MAINTAINER Alexander Muravya (aka kyberorg) <kyberorg@yadev.ee>
ADD ./target/yals.jar /app/
#COPY ./COMMIT /app/
#COPY ./TAG /app/
COPY ./docker-entrypoint.sh /
RUN sh -c 'chmod +x ./docker-entrypoint.sh'
RUN sh -c 'apt-get update && apt-get install -y netcat'
ENTRYPOINT ./docker-entrypoint.sh
EXPOSE 8080

