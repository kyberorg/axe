FROM java:8-jre
VOLUME /tmp
MAINTAINER Alexander Muravya (aka kyberorg) <kyberorg@yadev.ee>
ADD ./target/yals.jar /app/
COPY ./COMMIT /app/
COPY ./TAG /app/
RUN sh -c 'touch /app/yals.jar'
ENTRYPOINT docker-entrypoint.sh
EXPOSE 8080

