FROM openjdk:11-jre-slim
VOLUME /tmp

COPY ./target/yals.jar /app/
COPY ./docker-entrypoint.sh /
RUN sh -c 'chmod +x /docker-entrypoint.sh'
RUN sh -c 'apt-get update && apt-get install -y netcat curl jq iproute2 && apt -y autoremove && rm -rf /var/lib/apt/lists/*'

ENTRYPOINT ./docker-entrypoint.sh

#EXPOSE 8080 8000

HEALTHCHECK --start-period=60s --interval=5s --timeout=20s --retries=3 \
   CMD curl --silent --request GET http://127.0.0.1:8080/actuator/health \
                   | jq --exit-status '.status == "UP"' || exit 1
