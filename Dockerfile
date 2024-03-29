FROM kio.ee/base/go:1.19 as healthcheckBuilder
WORKDIR /go/src/app
COPY cmd/healthcheck.go cmd/healthcheck.go
RUN  GO111MODULE=off CGO_ENABLED=0 go install ./...

FROM kio.ee/base/go:1.19 as entrypointBuilder
WORKDIR /go/src/app
COPY cmd/entrypoint.go cmd/entrypoint.go
RUN  GO111MODULE=off CGO_ENABLED=0 go install ./...

FROM kio.ee/base/java:17-jdk as appBuilder
COPY target/axe.jar axe.jar
RUN java -Djarmode=layertools -jar axe.jar extract

FROM kio.ee/base/java:17-jdk as runner
WORKDIR /app

COPY --from=healthcheckBuilder --chown=appuser:appgroup /go/bin/cmd ./healthcheck
COPY --from=entrypointBuilder --chown=appuser:appgroup /go/bin/cmd ./entrypoint

COPY --from=appBuilder  dependencies/ ./
COPY --from=appBuilder snapshot-dependencies/ ./

# see https://github.com/moby/moby/issues/37965
LABEL maintainer="Aleksandr Muravja <alex@kyberorg.io>"

COPY --from=appBuilder spring-boot-loader/ ./
COPY --from=appBuilder application/ ./

USER appuser
HEALTHCHECK CMD ["/app/healthcheck", "/actuator/health"]
ENTRYPOINT ["/app/entrypoint"]
EXPOSE 8080
