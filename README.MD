[![Build Status](https://ci.yadev.eu/buildStatus/icon?job=YalsGH%2Fmaster)](https://ci.yadev.eu/job/YalsGH/job/master/)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/d6a6b92ffc1841e49cef33eadf8aa201)](https://www.codacy.com/gh/yadevee/yals)

# Yet another link shortener

Just a simple link shortener like many others. Made for friends.

## Release Notes

### Version 2.7.3 (16/06/2020)
Super long links bug fixed

* Fixed a bug when super long URL with a lot params cannot be saved due to limitations (#207)

### Version 2.7.2 (08/06/2020)
Ops fixes

* Fixed illegal access warning during a startup in Java 11 + added some useful Java 11 params (#196)

* Fixed app restarts caused by DB connection aborted (#197)

* Startup banner replaced and now contains app name and version (#198)

* Fixing home-view.css loading error (#201)

### Version 2.7.1 (13/05/2020)
Bug fixes

* Bug fixed. EKSS Links can be used (#139) 

* Adding missing image for 404 page (#184)

* Fixing IDN URL tests (#186)

### Version 2.7 (11/05/2020)
Vaadin UI

* User interface re-written in Vaadin 14 framework. (#55)

### Version 2.6 (30/09/19)
QR Code

* Added QR code with short link (#134)

* Added REST API Endpoint, which generates QR code from ident and optional size: 
`/api/qrCode/{ident}/{size}`

### Version 2.5.1 (25/09/19)
Updates and better error handling

* Software updates (#120) 

* Replying to API calls only by JSON and respect Accept Header (#130)

### Version 2.5 (24/09/19)
Bug Fixes

* Links without http:// prefix are supported (#50)

* Links from Russian Wikipedia are supported (#92)

* Application moved to Spring Boot 2 (#101)

* IDN aka URLs with non-latin symbols are supported (#102)

* Application can correctly handle Database disconnects at runtime (#104)

* Removed double slash in git commit link (#105)

* Footer no more flaky (#106)

* Telegram auto config working stable (#108)

* Link counter shows without space after 1000 links saved (#122) 

### Version 2.4 (12/04/18)
Telegram Bot

* Telegram Bot (#80)

* Better logs (#84)

### Version 2.3.1 (05/02/18)
Mattermost multiple params support and fixes

* Mattermost Bug: query with all spaces leaded to ":warning: Server Error" (#68)

* Multiple param support (#69)

* :warning: replaced with  :information_source: in Usage message (#70)

### Version 2.3 (31/01/18)
Mattermost integration

* Mattermost endpoint (#65)

### Version 2.2 (29/12/17)
Mobile-friendly site

* Tag footer no longer hides content on small screens (#34)

* Error box rewritten from static row to modal (#33)

* Internal: Move selenide selectors to separate class (#27)

### Version 2.1 (22/12/17)
Banners and copy to clipboard

* Banner about public access (#21)

* Banner: "N overall links saved" (#22)

* Copy to clipboard feature (#26)

### Version 2.0.2 (19/12/17)
Fix pack

* Docker ready Git-feature implementation (#1)

* Review technologies at humans.txt (#9)

* URL Args must be cleaned (#18)

### Version 2.0.1 (27/11/17)
Small fix

* Removed vertical scroll bar below (#14)

### Version 2.0 (11/07/17)
Second stable version

* Project was rewritten to _Spring Boot_ stack

* Makes short link from long one

### Version 1.0 and less (01/04/16)

Can be found from [here](https://github.com/yadevee/yals-play/blob/trunk/README.md#yet-another-link-shortener)

## For Ops
### How to Deploy app

* Our docker image: `yadev/yals`

* We need H2 (default profile) or MySQL database (local profile) to run

* We use [BugSnag](https://app.bugsnag.com/yadev/yals/errors) for capturing bugs. So token from Bugsnag needed.
#### Docker Swarm

EnvVars: 

* SPRING_PROFILES_ACTIVE: `dev,qa or prod`

* DB_HOST: `hostname or service (container) name`

* DB_NAME: `database name`

* DB_USER: `username for database`

* YALS_DB_PASSWORD or YALS_DB_PASSWORD_FILE: `password for db user`

* TELEGRAM_TOKEN or TELEGRAM_TOKEN_FILE: `token for telegram bot`

* BUGSNAG_TOKEN or BUGSNAG_TOKEN_FILE: `token for BugSnag`

Optional EnvVars: 

* LOG_LEVEL_APP: `see Logging part`

* PORT: `start server at port other than 8080 `

* SERVER_URL: `https://yals.tld` (most likely don't needed as regulated by Spring profile)

* OLD_UI: `https://old.yals.eu` (most likely remains as is)

* TELEGRAM_ENABLED `true/false` (most likely don't needed as regulated by Spring profile)

* DEV_MODE `true/false` (most likely don't needed as regulated by Spring profile)

* JAVA_DEBUG_PORT: `port for remote debugging` (this is internal port, need to expose it to connect from outside word)

Ports: 

* host port: `select yourself`

* container port: `8080 (or PORT)`

Secrets:

* yals_db_password: `database password`

* yals_telegram_token: `telegram bot token`

Volumes:

* yals_dumps: should be mounted as `/opt/dumps` (volume to store heap dumps, when app crashed)

## For Developers
### Common info

* We use [slack](https://app.slack.com/client/TRVM22544/CRW52SCLQ)

* We use [BugSnag](https://app.bugsnag.com/yadev/yals/errors)

### How to start coding

* Git clone ``` git clone repoUrl ```

* Import into an IDE as **Maven** project

* Select all autodetected Spring facets

* Use ``` YalsApplication.main() ``` to run application in IDE

### About: local profile
To develop with locally running dockerized MySQL database use `local` profile.

Profile can activated in IDEA or by setting env `SPRING_PROFILES_ACTIVE` to `local`

MySQL can be started from `docker/localMySQL` directory by running:

```shell script
docker-compose up -d
``` 

MySQL can be stopped from `docker/localMySQL` directory by running:

```shell script
docker-compose down
```

Use `@yals_local_bot` and its token (currently can be requested from [kyberorg](mailto:kyberorg@yadev.eu)) for local run

### Logging
To adjust logging we use ENV variables or `-D` vars: 

* Use `LOG_LEVEL_CORE` or `-Dlog.level.core` for setting overall log level

* Use `LOG_LEVEL_APP` or `-Dlog.level.app` for setting application (package: `ee.yals`) log level

* Use `LOG_LEVEL_SPRING` or `-Dlog.level.spring` for setting Spring (package: `org.springframework`) log level

* Use `LOG_LEVEL_DB` or `-Dlog.level.db` for setting Database SQL (package: `org.hibernate`) log level

### About: Tests
* Test URL is defined bu `-Dtest.url` property. Should be with protocol.
  
* Browsers are defined by `-Dtest.browsers`. Supported browsers: Chrome, Firefox, Safari, IE, Edge. 

Can add several browsers like `chrome,firefox`

* Report dir can be changed using `-Dtest.reportdir` property

* Local server port can be changed using `-Dport` property

## About: Git Branches, Tags and Releases
| Branch    | Docker Tag     | Deploy Destination  |
|-----------|----------------|---------------------|
| trunk     | latest         | qa/demo             |
| (tag)     | (tag name)     | PROD                |
| any other | dev/custom tag | dev                 | 

### Trunk
Considered as default branch.
Should always be stable. 

### Trunk build status
[![Build Status](https://ci.yadev.eu/buildStatus/icon?job=YalsGH%2Ftrunk)](https://ci.yadev.eu/job/YalsGH/job/trunk/)
[![codecov](https://codecov.io/gh/yadevee/yals/branch/trunk/graph/badge.svg)](https://codecov.io/gh/yadevee/yals)

### Tags
Reserved for releases only. Will be deployed to Production Server.

### Other branches aka features
* Always start from trunk branch.
* Uses `dev` docker tag, unless custom (or branch named) tag provided.
* Deploy destination = dev server
 
