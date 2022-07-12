docker login kio.ee
docker pull kio.ee/lib/golang:1.18.3-alpine
docker pull kio.ee/lib/eclipse-temurin:17-jre-alpine
docker build -t kio.ee/yalsee/base:alpine-jre-17 -f Dockerfile.jre .
docker push kio.ee/yalsee/base:alpine-jre-17
docker logout kio.ee