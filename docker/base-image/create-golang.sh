docker login kio.ee
docker pull golang:1.18.2-bullseye
docker build -t kio.ee/kyberorg/golang:1.18.2 -f Dockerfile.golang .
docker push kio.ee/kyberorg/golang:1.18.2
docker logout kio.ee
