docker pull gcr.io/distroless/java17-debian11:debug-nonroot
docker build -t quay.io/kyberorg/yalsee-base:distroless-jdk-17 -f Dockerfile.jdk .
docker push quay.io/kyberorg/yalsee-base:distroless-jdk-17
