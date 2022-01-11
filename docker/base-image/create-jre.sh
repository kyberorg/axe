docker pull gcr.io/distroless/java17-debian11:nonroot
docker build -t quay.io/kyberorg/yalsee-base:distroless-jre-17 -f Dockerfile.jre .
docker push quay.io/kyberorg/yalsee-base:distroless-jre-17
