docker pull golang:1.17.5-bullseye
docker build -t quay.io/kyberorg/golang:1.17.5 -f Dockerfile.golang .
docker push quay.io/kyberorg/golang:1.17.5

