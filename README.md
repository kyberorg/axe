# Yalsee - the link shortener
Simple link shortener [yals.ee](https://yals.ee), which produce links
like [https://yls.ee/Cxwycs](https://yls.ee/Cxwycs)

Docker image: `kio.ee/kyberorg/yalsee`

### Note on Anti-scam redirect page
Since release `3.0.5` by default when user opens short link instead of redirect appears redirect page with destination
link. This was done intentionally to prevent scam, phishing and malware activities.

To bypass this page just add manually `+` symbol to end of your short link. For
example: [https://yls.ee/Cxwycs+](https://yls.ee/Cxwycs+)

## Release Notes

Please see [Release Page](https://github.com/kyberorg/yalsee/releases)

## Development and Tech info
See [CONTRIBUTING.md](CONTRIBUTING.md)

## About: Git Branches, Tags and Releases
| Branch    | Docker Tag     | Deploy Destination |
|-----------|----------------|--------------------|
| trunk     | trunk          | PROD               |
| (PR)      | qa             | qa                 |
| (tag)     | (tag name)     | -                  |
| any other | dev/custom tag | dev                | 

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
