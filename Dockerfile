FROM yasys/db-aware-openjdk:8
VOLUME /tmp

ADD ./target/yals.jar /app/
COPY ./docker-entrypoint.sh /
RUN sh -c 'chmod +x /docker-entrypoint.sh'
ENTRYPOINT ./docker-entrypoint.sh
EXPOSE 8080
HEALTHCHECK --start-period=30s --interval=15s --timeout=10s --retries=3 \
   CMD curl --silent --request GET http://127.0.0.1:8080/actuator/health \
                   | jq --exit-status '.status == "UP"' || exit 1
