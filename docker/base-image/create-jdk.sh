docker login kio.ee
docker pull kio.ee/lib/golang:1.18.3-alpine
docker pull kio.ee/lib/eclipse-temurin:17-jdk-alpine
docker build -t kio.ee/yalsee/base:alpine-jdk-17 -f Dockerfile.jdk .
docker push kio.ee/yalsee/base:alpine-jdk-17
docker logout kio.ee