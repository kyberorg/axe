[![Build Status](https://ci.yadev.eu/buildStatus/icon?job=yals%2Ftrunk)](https://ci.yadev.eu/view/Yalsee/job/yals/job/trunk/)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/8af7847efda742da9dd02d660ad6b5a9)](https://www.codacy.com/gh/kyberorg/yalsee/dashboard)

# Yalsee - the link shortener
Simple link shortener [yals.ee](https://yals.ee), which produce links like [https://yls.ee/Cxwycs](https://yls.ee/Cxwycs)

Docker image: [`kyberorg/yalsee`](https://hub.docker.com/repository/docker/kyberorg/yalsee)

## Release Notes
Moved to [CHANGELOG](CHANGELOG.md).

## Development and Tech info
See [CONTRIBUTING.md](CONTRIBUTING.md)

## About: Git Branches, Tags and Releases
| Branch    | Docker Tag     | Deploy Destination  |
|-----------|----------------|---------------------|
| trunk     | latest         | demo                |
| (tag)     | (tag name)     | PROD                |
| any other | dev/custom tag | dev                 | 

### Trunk
Considered as default branch.
Should always be stable. 

### Tags
Build manually. Deploy Destination selected by user. By design, we use tags for Releases.

### Other branches aka features
* Always start from trunk branch.
* Uses `dev` docker tag, unless custom (or branch named) tag provided.
* Deploy destination = dev server
 
