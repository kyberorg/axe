FROM java:8-jre
VOLUME /tmp
MAINTAINER Alexander Muravya (aka kyberorg) <asm@virtalab.net>
ADD ./target/yals.jar /app/
RUN sh -c 'touch /app/yals.jar'
ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/yals.jar"]
EXPOSE 8080