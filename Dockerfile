FROM adoptopenjdk:11-jre-openj9 as builder

COPY target/yalsee.jar yalsee.jar
RUN java -Djarmode=layertools -jar yalsee.jar extract

FROM quay.io/kyberorg/yalsee-base:538-base-image as runner

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
