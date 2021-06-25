## For Developers

### Common info

* I use [BugSnag](https://app.bugsnag.com/yalsee/yalsee/errors) for capturing bugs

### How to start coding

* Git clone ``` git clone repoUrl ```
* Import into an IDE as **Maven** project
* Select all autodetected Spring facets
* Use ``` Application.main() ``` to run application in IDE

### About: local profile
To develop with locally running dockerized MySQL database use `local` profile.

Profile can be activated in IDEA or by setting env `SPRING_PROFILES_ACTIVE` to `local`

MySQL can be started from `docker/localMySQL` directory by running:

```shell script
docker-compose up -d
``` 

MySQL can be stopped from `docker/localMySQL` directory by running:

```shell script
docker-compose down
```

Use Telegram local Bot `@yls_local_bot` and its token (currently can be requested from [kyberorg](mailto:root@kyberorg.io)) for local run

### Logging
To adjust logging we use ENV variables or `-D` vars:

* Use `LOG_LEVEL_CORE` or `-Dlog.level.core` for setting overall log level
* Use `LOG_LEVEL_APP` or `-Dlog.level.app` for setting application (package: `io.kyberorg.yalsee`) log level
* Use `LOG_LEVEL_SPRING` or `-Dlog.level.spring` for setting Spring (package: `org.springframework`) log level
* Use `LOG_LEVEL_DB` or `-Dlog.level.db` for setting Database SQL (package: `org.hibernate`) log level

### About: Tests
* Test URL is defined by `-Dtest.url` property. Should be with protocol.
* Browsers are defined by `-Dtest.browsers`. Supported browsers: Chrome, Firefox, Safari, IE, Edge.

Can add several browsers like `chrome,firefox`

* Report dir can be changed using `-Dtest.reportdir` property
* Local server port can be changed using `-Dport` property

## For Ops
### How to Deploy app

* Docker image: `kyberorg/yalsee`
* H2 (default profile) or MySQL database (local profile) needed to run.
* I use [BugSnag](https://app.bugsnag.com/yalsee/yalsee/errors) for capturing bugs. So token from Bugsnag needed.

#### Docker Swarm

EnvVars:

* SPRING_PROFILES_ACTIVE: `dev,demo or prod`
* DB_HOST: `hostname or service (container) name`
* DB_NAME: `database name`
* DB_USER: `username for database`
* DB_PASSWORD or DB_PASSWORD_FILE: `password for db user`
* TELEGRAM_TOKEN or TELEGRAM_TOKEN_FILE: `token for telegram bot`
* BUGSNAG_TOKEN or BUGSNAG_TOKEN_FILE: `token for BugSnag`
* DELETE_TOKEN or DELETE_TOKEN_FILE: `temporary master token for deleting links` (needed until auth story introduced)

Optional EnvVars:

* LOG_LEVEL_APP: `see Logging part`
* PORT: `start server at port other than 8080 `
* SERVER_URL: `https://yals.ee` (most likely don't needed as regulated by Spring profile)
* SHORT_DOMAIN: `yls.ee` (most likely don't needed as regulated by Spring profile)
* TELEGRAM_ENABLED `true/false` (most likely don't needed as regulated by Spring profile)
* DEV_MODE `true/false` (most likely don't needed as regulated by Spring profile)
* JAVA_DEBUG_PORT: `port for remote debugging` (this is internal port, need to expose it to connect from outside word)
* JAVA_JMX_PORT: `port for remote JMX` (this is internal port, need to expose it to connect from outside word)
* APM_ENV: `environment in APM` (if not set - APM configuration won't be applied)
* APM_SERVER: `https://my.apm.endpoint` (if not set - APM configuration won't be applied)
* APM_TOKEN or APM_TOKEN_FILE: `token for Elastic APM` (if APM server requires token)
* REDIRECT_PAGE_BYPASS_SYMBOL: `Special symbol, which should be added to ident to bypass (skip) redirect page` (most likely don't needed as regulated by Spring profile)
* REDIRECT_PAGE_TIMEOUT: `Timeout in second after which redirect page makes actual redirect` (most likely don't needed as regulated by Spring profile)

Ports:

* host port: `select yourself`
* container port: `8080 (or PORT)`

Secrets:

* yals_db_password: `database password`
* yals_telegram_token: `telegram bot token`

Volumes:

* yals_dumps: should be mounted as `/opt/dumps` (volume to store heap dumps, when app crashed)

#### About Telegram Bots
| Stage     | Bot Name       | Human Readable Name | 
|-----------|----------------|---------------------|
| PROD      | yalsee_bot     | Yalsee Bot          |
| Demo      | yls_demo_bot   | Yls Demo Bot        | 
| Dev       | yls_dev_bot    | Yls Dev Bot         | 
| Local     | yls_local_bot  | Yls Local Bot       |

##### Bot description 
* Name: Yls Dev Bot
* Description: Dev.Yals.ee Link Shortener Bot
* About: Makes long links short
* Botpic: to be done
* Commands: yalsee - https://longLink.tld description 
