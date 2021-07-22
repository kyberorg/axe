FROM adoptopenjdk:11-jre-openj9 as builder

COPY target/yalsee.jar yalsee.jar
RUN java -Djarmode=layertools -jar yalsee.jar extract

FROM adoptopenjdk:11-jdk-openj9 as runner

# Create user and set ownership and permissions as required
RUN useradd --user-group --create-home --no-log-init --shell /bin/bash yalsee \
    && mkdir /app \
    && chown -R yalsee /app

WORKDIR /app
COPY --from=builder  dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

COPY ./docker-entrypoint.sh ./
RUN sh -c 'chmod +x ./docker-entrypoint.sh'

USER yalsee
ENTRYPOINT ["./docker-entrypoint.sh"]

EXPOSE 8080
