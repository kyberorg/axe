## For Developers

### Common info

* I use [BugSnag](https://app.bugsnag.com/axe/axe/errors) for capturing bugs
* Minimal Java level is `17`
* Application needs `postgres` (links and other data) and `redis` (sessions and robot list).

### How to start coding

* Git clone ``` git clone repoUrl ```
* Import into an IDE as **Maven** project
* Select all autodetected Spring facets
* Use ``` Application.main() ``` to run application in IDE

### About: default profile

To develop with locally running dockerized Postgres database and Redis use `default` profile.

Profile can be activated in IDEA or by setting env `SPRING_PROFILES_ACTIVE` to `default`

Before very first run you must prepare pgadmin directory with correct ownership
```shell
cd docker/axe-local-run
mkdir pgadmin
chown 5050:5050 pgadmin
```

Containers with Postgres/Redis and their admin interfaces can be started from `docker/axe-local-run` directory by running:

```shell script
docker-compose up -d
``` 

Containers can be stopped from `docker/axe-local-run` directory by running:

```shell script
docker-compose down
```

Use Telegram local Bot `@axe_local_bot` and its token (currently can be requested
from [kyberorg](mailto:alex@kyberorg.io)) for local run

### About:debugging and JMX interface

Docker Stack at Dev Server aka `Koda` has both Remote JVM Debug (`tcp/8000`) and JMX interface (`tcp/8888`) enabled, but
to prevent misuse direct access to them is denied.

#### JMX
JMX interface configured without any security options and therefore cannot be used without SSH tunnel.

#### SSH Configuration
You request Access to `Dev` and SSH Config from [kyberorg](mailto:alex@kyberorg.io)

#### SSH Tunnel (unrestricted Internet access)

When your connection is unrestricted, you should establish SSH Tunnel to `Dev` first.

So connection schema is: `localhost:8000 -> Dev@SSH -> Dev's localhost:8000`

```shell
ssh -v -N -L 8888:dev:8888 dev && 
ssh -v -N -L 8000:dev:8000 dev
```

#### SSH Tunnel via JumpHost (restricted Internet access)

When internet access restricted you can use our jump host to access `Dev` server. It has SSH over HTTPS port, so there
are chances to establish connection. So connection schema is: `Local PC (with IDE) -> Hyppa (aka JumpHost) -> Dev`

* to use Remote JVM debugging run:

```shell
ssh -v -N hyppa -J hyppa -L 8888:dev:8888 &&
ssh -v -N hyppa -J hyppa -L 8000:dev:8000 
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
* Use `LOG_LEVEL_APP` or `-Dlog.level.app` for setting application (package: `pm.axe`) log level
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

* Docker image: `kio.ee/kyberorg/axe`
* H2 (default profile) or Postgres database (local profile) needed to run.
* Redis for storing sessions and stuff like this.
* I use [BugSnag](https://app.bugsnag.com/axe/axe/errors) for capturing bugs. So token from Bugsnag needed.

#### Docker Swarm

EnvVars:

* SPRING_PROFILES_ACTIVE: `dev,qa,poc or prod`
* DB_HOST: `hostname or service (container) name`
* DB_NAME: `database name`
* DB_USER: `username for database`
* DB_PASSWORD or DB_PASSWORD_FILE: `password for db user`
* TELEGRAM_TOKEN or TELEGRAM_TOKEN_FILE: `token for telegram bot`
* BUGSNAG_TOKEN or BUGSNAG_TOKEN_FILE: `token for BugSnag`
* MASTER_TOKEN or MASTER_TOKEN_FILE: `temporary master token for deleting links` (needed until auth story introduced)
* REDIS_HOST: `redis hostname/ip or container name`
* REDIS_PASSWORD or REDIS_PASSWORD_FILE: `password for connecting to redis`
* FACEBOOK_APP_ID: `Facebook Application ID for sharing link to Facebook`
* SERVER_KEY: `Symmetric encryption/decryption key`
* PASSWORD_SALT: `String with Salt added to Password during encryption proccess`
* MAIL_USER: `Axe Mail User (name@axe.pm)`
* MAIL_PASSWORD: `Axe Mail Password`

Optional EnvVars:

* LOG_LEVEL_APP: `see Logging part`
* PORT: `start server at port other than 8080 `
* SERVER_URL: `https://axe.pm` (most likely don't needed as regulated by Spring profile)
* SHORT_DOMAIN: `axe.pm` (most likely don't needed as regulated by Spring profile) - currently same as `SERVER_URL` until app logic rewritten to support premium domain.
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
* MAIL_DEBUG: `true/false` (enables/disables mail (SMTP) debug output. Default: false)
* TELEGRAM_BOT: `Name of telegram bot` (most likely don't needed as regulated by Spring profile)

Ports:

* host port: `select yourself`
* container port: `8080 (or PORT)`

#### About Telegram Bots

| Stage  | Bot Name      | Human Readable Name | 
|--------|---------------|---------------------|
| PROD   | axe_pm_bot    | Axe Bot             |
| QA     | axe_qa_bot    | Axe QA Bot          |
| PoC    | axe_poc_bot   | Axe PoC Bot         |
| Dev    | axe_dev_bot   | Axe Dev Bot         | 
| Local  | axe_local_bot | Axe Local Bot       |

##### Bot description

* Name: Axe Dev Bot
* Description: Axe.PM Shortener Bot
* About: Makes long links short
* BotPic: to be done
* Commands: axe - https://longLink.tld description


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

## About:Artwork
### Banner (at application startup)
* Banner created with [Taag](https://patorjk.com/software/taag)
Settings:
* Font: Doom
* CharOptions: both Full

