## For Developers

### Common info

* I use [BugSnag](https://app.bugsnag.com/yalsee/yalsee/errors) for capturing bugs
* Minimal Java level is `17`
* Application needs `postgres` and optionally `redis` (sessions and robot list)

### How to start coding

* Git clone ``` git clone repoUrl ```
* Import into an IDE as **Maven** project
* Select all autodetected Spring facets
* Use ``` Application.main() ``` to run application in IDE

### About: local profile

To develop with locally running dockerized Postgres database and Redis use `local` profile.

Profile can be activated in IDEA or by setting env `SPRING_PROFILES_ACTIVE` to `local`

Before very first run you must prepare pgadmin directory with correct ownership
```shell
cd docker/yalsee-local-run
mkdir pgadmin
chown 5050:5050 pgadmin
```

Containers with Postgres/Redis and their admin interfaces can be started from `docker/yalsee-local-run` directory by running:

```shell script
docker-compose up -d
``` 

Containers can be stopped from `docker/yalsee-local-run` directory by running:

```shell script
docker-compose down
```

Use Telegram local Bot `@yls_local_bot` and its token (currently can be requested
from [kyberorg](mailto:alex@kyberorg.io)) for local run

### About:debugging and JMX interface

Docker Stack at Dev Server aka `Koda` has both Remote JVM Debug (`tcp/8000`) and JMX interface (`tcp/8888`) enabled, but
to prevent misuse direct access to them is denied.

#### JMX
JMX interface configured without any security options and therefore cannot be used without SSH tunnel.

#### SSH Tunnel (unrestricted Internet access)

When you have SSH access to `Koda` and connection is unrestricted, you should establish SSH Tunnel to `Koda`.

So connection schema is: `localhost:8000 -> Koda@SSH -> Koda's localhost:8000`

```shell
ssh -v -N -L 8888:koda.kyberorg.io:8888 koda.kyberorg.io && 
ssh -v -N -L 8000:koda.kyberorg.io:8000 koda.kyberorg.io
```

#### SSH Tunnel via JumpHost (restricted Internet access)

When internet access restricted you can use our jump host to access `Koda` server. It has SSH over HTTPS port, so there
are chances to establish connection. So connection schema is: `Local PC (with IDE) -> Sabaton (aka JumpHost) -> Koda`

* to use Remote JVM debugging run:

```shell
ssh -v -N sabaton -J sabaton -L 8888:koda.kyberorg.io:8888 &&
ssh -v -N sabaton -J sabaton -L 8000:koda.kyberorg.io:8000 
```

#### Remote debugging

After SSH tunnel established connect to:

```
localhost:8000
```

or use IDEA configuration at `.run/Remove Debugging [Dev].run.xml`

#### JMX

To connect to JMX run use following without SSL and username/password:

```
localhost:8888
```

### Logging

To adjust logging we use ENV variables or `-D` vars:

* Use `LOG_LEVEL_CORE` or `-Dlog.level.core` for setting overall log level
* Use `LOG_LEVEL_APP` or `-Dlog.level.app` for setting application (package: `io.kyberorg.yalsee`) log level
* Use `LOG_LEVEL_SPRING` or `-Dlog.level.spring` for setting Spring (package: `org.springframework`) log level
* Use `LOG_LEVEL_DB` or `-Dlog.level.db` for setting Database SQL (package: `org.hibernate`) log level

### About: Tests

* Test URL defined by `-Dtest.url` property. Should be with protocol.
* Browsers defined by `-Dtest.browsers`. Supported browsers: Chrome, Firefox, Safari, IE, Edge.

Can add several browsers like `chrome,firefox`

* Report dir can be changed using `-Dtest.reportdir` property
* Local server port can be changed using `-Dport` property

## For Ops
### How to Deploy app

* Docker image: `kio.ee/yalsee/yalsee`
* H2 (default profile) or Postgres database (local profile) needed to run.
* Redis for storing sessions and stuff like this.
* I use [BugSnag](https://app.bugsnag.com/yalsee/yalsee/errors) for capturing bugs. So token from Bugsnag needed.

#### Docker Swarm

EnvVars:

* SPRING_PROFILES_ACTIVE: `dev,qa or prod`
* DB_HOST: `hostname or service (container) name`
* DB_NAME: `database name`
* DB_USER: `username for database`
* DB_PASSWORD or DB_PASSWORD_FILE: `password for db user`
* TELEGRAM_TOKEN or TELEGRAM_TOKEN_FILE: `token for telegram bot`
* BUGSNAG_TOKEN or BUGSNAG_TOKEN_FILE: `token for BugSnag`
* DELETE_TOKEN or DELETE_TOKEN_FILE: `temporary master token for deleting links` (needed until auth story introduced)
* REDIS_HOST: `redis hostname/ip or container name`
* REDIS_PASSWORD or REDIS_PASSWORD_FILE: `password for connecting to redis`

Optional EnvVars:

* LOG_LEVEL_APP: `see Logging part`
* PORT: `start server at port other than 8080 `
* SERVER_URL: `https://yals.ee` (most likely don't needed as regulated by Spring profile)
* SHORT_DOMAIN: `yls.ee` (most likely don't needed as regulated by Spring profile)
* TELEGRAM_ENABLED `true/false` (most likely don't needed as regulated by Spring profile)
* DEV_MODE `true/false` (most likely don't needed as regulated by Spring profile)
* JAVA_DEBUG_PORT: `port for remote debugging` (this is internal port, need to expose it to connect from outside word)
* JAVA_JMX_PORT: `port for remote JMX` (this is internal port, need to expose it to connect from outside word)
* REDIRECT_PAGE_BYPASS_SYMBOL: `Special symbol, which should be added to ident to bypass (skip) redirect page` (most
  likely don't needed as regulated by Spring profile)
* REDIRECT_PAGE_TIMEOUT: `Timeouts in second after which redirect page makes actual redirect` (most likely don't needed
  as regulated by Spring profile)
* SESSION_TIMEOUT `Timeout in seconds after which current session ends` (most likely don't needed as regulated by Spring
  profile)
* REDIS_PORT: `redis port` (needed if redis runs on port other than 6379)
* REDIS_DB: `0-15` (custom database, redis supports numeric db from 0 to 15, default 0)
* REDIS_TIMEOUT_MILLIS: `Redis connection timeout in milliseconds` (most likely don't needed as regulated by Spring
  profile)
* REDIS_ENABLED: `true/false` (most likely don't needed as regulated by Spring profile)

Ports:

* host port: `select yourself`
* container port: `8080 (or PORT)`

Secrets:

* yalsee_db_password: `database password`
* yalsee_telegram_token: `telegram bot token`

Volumes:

* yalsee_dumps: should be mounted as `/opt/dumps` (volume to store heap dumps, when app crashed)

#### About Telegram Bots

| Stage | Bot Name       | Human Readable Name | 
|-------|----------------|---------------------|
| PROD  | yalsee_bot     | Yalsee Bot          |
| QA    | yls_demo_bot   | Yls Demo Bot        | 
| Dev   | yls_dev_bot    | Yls Dev Bot         | 
| Local | yls_local_bot  | Yls Local Bot       |

##### Bot description

* Name: Yls Dev Bot
* Description: Dev.Yals.ee Link Shortener Bot
* About: Makes long links short
* BotPic: to be done
* Commands: yalsee - https://longLink.tld description

### Release notes
There following sections:

* `:star: Features` - new stuff in application
* `:hammer: Improvements` - already existing stuff which got better
* `:shield: Security`
* `:lady_beetle: Bug Fixes`
* `:test_tube: Testing`
* `:computer: Ops` - Operations and non-coding stuff
* `:package: Dependencies Updates`
* `:broom: Cleanup/Refactoring`
* `:notebook_with_decorative_cover: Dokumentation`
  