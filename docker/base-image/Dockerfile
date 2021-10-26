FROM amazoncorretto:11 as jreBuilder
RUN jlink \
        --add-modules jdk.net,jdk.unsupported,java.sql,java.desktop,java.naming,java.management,java.instrument,java.security.jgss,java.rmi,jdk.management.agent,jdk.crypto.ec,jdk.crypto.cryptoki \
        --verbose \
        --strip-debug \
        --compress 2 \
        --no-header-files \
        --no-man-pages \
        --output /jre

FROM golang:1.17.2 as healthcheckBuilder
WORKDIR /go/src/app

COPY cmd/healthcheck.go cmd/healthcheck.go

RUN  GO111MODULE=off CGO_ENABLED=0 go install ./...

# Set ownership and permissions as required
# 65532 - is nonroot @ distroless. See: https://github.com/GoogleContainerTools/distroless/issues/235
RUN mkdir /app && chown -R 65532:65532 /app

FROM golang:1.17.2 as entrypointBuilder
WORKDIR /go/src/app
COPY cmd/entrypoint.go cmd/entrypoint.go

RUN  GO111MODULE=off CGO_ENABLED=0 go install ./...

FROM gcr.io/distroless/java-debian11:base as runner
COPY --from=jreBuilder /jre /usr/jre
## Networking
COPY --from=jreBuilder /usr/lib/jvm/java-11-amazon-corretto/lib/libsunec.so /usr/jre/lib/libsunec.so
COPY --from=jreBuilder /usr/lib/jvm/java-11-amazon-corretto/lib/security/cacerts /cacerts
COPY --from=jreBuilder /usr/lib/jvm/java-11-amazon-corretto/lib/security/blacklisted.certs  /usr/jre/lib/security/blacklisted.certs
COPY --from=jreBuilder /usr/lib/jvm/java-11-amazon-corretto/lib/security/default.policy /usr/jre/lib/security/default.policy


ENV javax.net.ssl.trustStore /cacerts
ENV javax.net.ssl.trustAnchors /cacerts

## Debug and JMX Support
COPY --from=jreBuilder /usr/lib/jvm/java-11-amazon-corretto/lib/libjdwp.so /usr/jre/lib/libjdwp.so
COPY --from=jreBuilder /usr/lib/jvm/java-11-amazon-corretto/lib/libdt_socket.so /usr/jre/lib/libdt_socket.so

COPY --from=healthcheckBuilder /go/bin/cmd /app/healthcheck
COPY --from=entrypointBuilder /go/bin/cmd /app/entrypoint


HEALTHCHECK --start-period=60s --interval=5s --timeout=20s --retries=3 CMD ["/app/healthcheck"]

USER nonroot
ENTRYPOINT ["/app/entrypoint"]
