FROM kio.ee/hub/library/amazoncorretto:17-alpine as builder

COPY target/yalsee.jar yalsee.jar
RUN java -Djarmode=layertools -jar yalsee.jar extract

FROM kio.ee/yalsee/base:alpine-jre-17 as runner

WORKDIR /app
COPY --from=builder  dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./

# see https://github.com/moby/moby/issues/37965
LABEL maintainer="Aleksandr Muravja <alex@kyberorg.io>"

COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

HEALTHCHECK CMD ["/app/healthcheck", "/"]

EXPOSE 8080
