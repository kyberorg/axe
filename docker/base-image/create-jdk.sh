docker login kio.ee
docker pull gcr.io/distroless/java17-debian11:debug-nonroot
docker build -t kio.ee/kyberorg/yalsee-base:distroless-jdk-17 -f Dockerfile.jdk .
docker push kio.ee/kyberorg/yalsee-base:distroless-jdk-17
docker logout kio.ee