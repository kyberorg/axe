FROM amazoncorretto:11 as builder

COPY target/yalsee.jar yalsee.jar
RUN java -Djarmode=layertools -jar yalsee.jar extract

FROM quay.io/kyberorg/yalsee-base:distroless-jdk-11 as runner

WORKDIR /app
COPY --from=builder  dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
RUN true # see https://github.com/moby/moby/issues/37965
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

HEALTHCHECK CMD ["/app/healthcheck", "/"]

EXPOSE 8080
