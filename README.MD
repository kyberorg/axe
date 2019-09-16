[![Build Status](https://ci.yadev.eu/buildStatus/icon?job=YalsGH%2Fmaster)](https://ci.yadev.eu/job/YalsGH/job/master/)

# Yet another link shortener

Just a simple link shortener like many others. Made for friends.

## Release Notes

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
### How to Deploy app ?

* Our docker image: `yadev/yals`

* We need MySQL database to run

#### Docker Swarm

EnvVars: 

* SPRING_PROFILES_ACTIVE: `dev,qa or prod`

* DB_HOST: `hostname or service (container) name`

* DB_NAME: `database name`

* DB_USER: `username for database`

* YALS_DB_PASSWORD or YALS_DB_PASSWORD_FILE: `password for db user`

* TELEGRAM_TOKEN or TELEGRAM_TOKEN_FILE: `token for telegram bot`

Optional EnvVars: 

* LOG_LEVEL_APP: `see Logging part`

* PORT: `start server at port other than 8080 `

* SERVER_URL: `https://yals.tld` (most likely don't needed as regulated by Spring profile)

Ports: 

* host port: `select yourself`

* container port: `8080 (or PORT)`

Secrets:

* yals_db_password: `database password`

* yals_telegram_token: `telegram bot token`

## For Developers
### How to start coding ?

* Git clone ``` git clone repoUrl ```

* Import into IDE as **Maven** project

* Select all autodetected Spring facets

* Use ``` YalsApplication.main() ``` as start point

### About: local profile
To develop with locally running dockerized MySQL database use `local` profile.

Profile can activated in IDEA or by setting env `SPRING_PROFILES_ACTIVE` to `local`

MySQL can be started from `docker/localMySQL` directory by running:

```
docker-compose up -d
``` 

MySQL can be stopped from `docker/localMySQL` directory by running:

```
docker-compose down
```

Use `@yals_local_bot` and its token (currently can be requested from [kyberorg](mailto:kyberorg@yadev.eu)) for local run

### Logging
To adjust logging we use ENV variables or `-D` vars: 

* Use `LOG_LEVEL_CORE` or `-Dlog.level.core` for setting overall log level
* Use `LOG_LEVEL_APP` or `-Dlog.level.app` for setting application (package: `ee.yals`) log level
* Use `LOG_LEVEL_SPRING` or `-Dlog.level.spring` for setting Spring (package: `org.springframework`) log level
* Use `LOG_LEVEL_DB` or `-Dlog.level.db` for setting Database SQL (package: `org.hibernate`) log level

### Trunk build status

[![Build Status](https://ci.yadev.eu/buildStatus/icon?job=YalsGH%2Ftrunk)](https://ci.yadev.eu/job/YalsGH/job/trunk/)
[![codecov](https://codecov.io/gh/yadevee/yals/branch/trunk/graph/badge.svg)](https://codecov.io/gh/yadevee/yals)
