FROM yasys/db-aware-openjdk:1.0
VOLUME /tmp
COPY ./target/yals.jar /app/
COPY ./docker-entrypoint.sh /
RUN sh -c 'chmod +x /docker-entrypoint.sh'
ENTRYPOINT ./docker-entrypoint.sh
EXPOSE 8080
