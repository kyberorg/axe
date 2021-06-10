[![Build Status](https://ci.yadev.eu/buildStatus/icon?job=yals%2Ftrunk)](https://ci.yadev.eu/view/Yalsee/job/yals/job/trunk/)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/8af7847efda742da9dd02d660ad6b5a9)](https://www.codacy.com/gh/kyberorg/yalsee/dashboard)

# Yalsee - the link shortener
Simple link shortener [yals.ee](https://yals.ee), which produce links like [https://yls.ee/Cxwycs](https://yls.ee/Cxwycs)

Docker image: [`kyberorg/yalsee`](https://hub.docker.com/repository/docker/kyberorg/yalsee)

### Note on Anti-phishing redirect page
Since version `3.0.5` by default redirect when user opens short link redirect page with destination link shows.
This was done intentionally to prevent phishing and malware activities. 

To bypass this page just add manually `+` symbol to end of your short link. For example: [https://yls.ee/Cxwycs+](https://yls.ee/Cxwycs+)

## Release Notes
Moved to [CHANGELOG](CHANGELOG.md).

## Development and Tech info
See [CONTRIBUTING.md](CONTRIBUTING.md)

## About: Git Branches, Tags and Releases
| Branch    | Docker Tag     | Deploy Destination  |
|-----------|----------------|---------------------|
| trunk     | trunk          | PROD                |
| (PR)      | RC             | demo                |
| (tag)     | (tag name)     | -                   |
| any other | dev/custom tag | dev                 | 

### Trunk
Considered as default branch.
Should always be stable. 
Deploys to Production

### Tags
Build manually. By design, I use tags for Releases aka Milestones.

### Other branches aka features
* Always start from trunk branch.
* Uses `dev` docker tag, unless custom (or branch named) tag provided.
* Deploy destination = dev server
