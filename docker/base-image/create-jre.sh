docker login kio.ee
docker pull gcr.io/distroless/java17-debian11:nonroot
docker build -t kio.ee/kyberorg/yalsee-base:distroless-jre-17 -f Dockerfile.jre .
docker push kio.ee/kyberorg/yalsee-base:distroless-jre-17
docker logout kio.ee