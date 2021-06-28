FROM adoptopenjdk:11-jre-openj9 as builder

COPY target/yalsee.jar yalsee.jar
RUN java -Djarmode=layertools -jar yalsee.jar extract

FROM adoptopenjdk:11-jdk-openj9 as runner
WORKDIR /app
COPY --from=builder  dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

COPY ./docker-entrypoint.sh ./

RUN sh -c 'chmod +x ./docker-entrypoint.sh'
ENTRYPOINT ["./docker-entrypoint.sh"]

EXPOSE 8080
