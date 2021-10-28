docker pull gcr.io/distroless/java-debian11:11-nonroot
docker build -t quay.io/kyberorg/yalsee-base:distroless-jre-11 -f Dockerfile.jre .
docker push quay.io/kyberorg/yalsee-base:distroless-jre-11
