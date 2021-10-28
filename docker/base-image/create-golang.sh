docker pull golang:1.17.2
docker build -t quay.io/kyberorg/golang:1.17.2 -f Dockerfile.golang .
docker push quay.io/kyberorg/golang:1.17.2

