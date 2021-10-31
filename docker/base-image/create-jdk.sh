docker pull gcr.io/distroless/java-debian11:11-debug-nonroot
docker build -t quay.io/kyberorg/yalsee-base:distroless-jdk-11 -f Dockerfile.jdk .
docker push quay.io/kyberorg/yalsee-base:distroless-jdk-11
