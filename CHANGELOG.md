### Milestone 3.1 (20/07/2021)
RESTify API

:warning: there are breaking changes in API.

* API Endpoints were changes in order to fit with REST API template ([#347](https://github.com/kyberorg/yalsee/issue/347)).

  * `GET /api/link/{ident}` -> `GET /api/links/{ident}`
  * `POST/PUT /api/store` -> `POST /api/links`
  * `GET /qrcode/{ident}` -> `GET /qr/{ident}`
  * `GET /qrcode/{ident}/{size}` -> `GET /qr/{ident}/{size}`


* Added `GET /qr/{ident}/{width}/{height}` endpoint.
* Added API [Dokumentation Page](https://app.swaggerhub.com/apis/kyberorg/Yalsee/1.0.0) ([#347](https://github.com/kyberorg/yalsee/issue/347))
### Milestone 3.0.10 (13/07/2021)
Dependencies Bump

* [Maven Deps] &nbsp;&nbsp; Bump telegrambots from 5.2.0 to 5.3.0 ([#417](https://github.com/kyberorg/yalsee/pull/417))
* [Maven Deps] &nbsp;&nbsp; Bump selenide from 5.22.2 to 5.22.3 ([#420](https://github.com/kyberorg/yalsee/pull/420))
* [Actions Deps] &nbsp; Bump EnricoMi/publish-unit-test-result-action from 1.18 to 1.19 ([#421](https://github.com/kyberorg/yalsee/pull/421))
* [Ops] &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Fail fast: login to docker hub before compiling JAR ([#422](https://github.com/kyberorg/yalsee/pull/422))

### Version 3.0.9 (02/07/2021)
* [Code Cleanup] &nbsp; Code cleanup (#368)
* [Deps Cleanup] &nbsp; Actualized info at pom.xml (#372)
* [Doku Cleanup] &nbsp; Removed dead stuff from README.md (#382)
* [Deps Updates] &nbsp; Dependabot and Dependabot updates (#408) (#411)
* [Security] &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Remediated Security Issues in code (#389)

### Version 3.0.8 (29/06/2021)
* [Internal] &nbsp;&nbsp; Added JMX and fixed remote debugging for Dev (#361)
* [Cleanup] &nbsp; Remove APM Stuff (#362)
* [Cleanup] &nbsp; Cleanup Docker image (#363)
* [Docker] &nbsp;&nbsp;&nbsp; Moved from Fat Jar to Layered Docker image (#364)

### Version 3.0.7 (22/06/2021)
Frontend improvements

* Added word `long` to title (#370)
* Reduced white space on large screens (#369)
* Smaller font size for note text for mobile screens (#371)
* Image at error 503 refers to old yals.eu hostname (#373)
* Submit button no longer disabled on empty input to prevent flaky tests (#367)
* YalseeLayout: common layout for all pages (#355)

### Version 3.0.6 (18/06/2021)
Updates

* Spring Boot 2.4.2 -> 2.5.1 (#356)
* Other dependencies (#356)

### Version 3.0.5 (11/06/2021)
Anti-Scam and Anti-Malware Actions

* Anti-Scam Redirect Page (#353)
* Banning Mechanism (#350)
* tmweb.ru and weebly.com are banned (#350) (#357)

### Version 3.0.4 (18/03/2021)
Delete Links API

* Delete Link API (#344)
* Dependencies updates (#346)

### Version 3.0.3 (09/02/2021)
Small Improvements

* An offline page updated and unified (#301)
* Tests code cleanup (#314)
* Version Dump and pom.xml clean up (#333)
* Smart server waiting before running tests (#303)
* Update Selenium Grid links at UI Tests summary (#323)
* Actuator Endpoints unified (#317)

### Version 3.0.2 (03/02/2021)
SEO and Analytics

* Installed Google Analytics (#298)
* Added Some basic SEO Stuff (#308) 
* Fixed release workflow (#327)

### Version 3.0.1 (27/01/2021)
Security and Ops

* Release-free deploy: `trunk` goes to `PROD` (#309)
* Migrated to GitHub Actions with self-hosted runner (#307) (#320)
* Reduced start-up to 20-25 seconds (#221)
* Implemented Security policy in `SECURITY.md` (#283)
* CodeQL tests code for security issues (#283)
* Fixed long URL input behavior which led to flaky test (#322)  
* CheckStyle Action test code style (#304)  
* Bump APM Agent to `1.20` (#302)
* Updated codacy badge (#311)

### Version 3.0 (13/01/2021)
Yals -> Yalsee

* Project renamed to `yalsee` (#296)
* Yalsee will produce even shorter URL like `https://yls.ee/Cxwycs` (#288)   
* Site URL changed from `https://yals.eu` to `https://yals.ee` (#287)  
* New Telegram Bot: Yalsee Bot `@yalsee_bot` (#296)
* Package name changed from `eu.yals` to `io.kyberorg.yalsee` (#296)

### Version 2.8 (07/01/2021)
Fresh UI

* More clean UI (#228)
* Fancy loading indicator (#271)
* Removed dead link old.yals.eu (#285)
* Fixed Mobile UI issues (#235)

### Version 2.7.6 (04/01/2021)
Version Bump

* Vaadin 14.4.5 (#259)
* Spring Boot 2.4.1 (#259)
* Telegram Bots 5.0.1 (#259) + code adaptation (#270)
* Small deps update (#259)
* JUnit 4 replaced with JUnit 5 (#266)
* Code: warnings fixed and deprecations and unused code removed (#280)
* JUnit 5 test report problem fixed (#278)

### Version 2.7.5 (30/12/2020)
No more leaks

* Application memory leak found and fixed (#264)
* OpenJ9 Java VM tweaks (#264)
* Migration from AppView (App Layout add-on) to Vaadin App layout (#269)
* Site title per each profile (#268)
* Small ops changes (#262)

### Version 2.7.4 (27/11/2020)
Internal Tweaks

* Memory usage decreased by switching to OpenJ9 (#216)
* Got rid of Vaadin Paid Components (#217)
* Dockerfile: JDK for Dev builds, JRE for others (#244)
* Dependencies updates (#238)
* Fixing application tests by moving to Selenide + Selenoid (#224) (#249)
* Added Java Remote Debug for DEV/DEMO (#213)
* Now deploying to K8S with Chuck Norris help (#218)
* Added APM agent for JVM stats (#223) (#242)
* Jenkinsfile updates  (#237) (#240) (#243)
* Logging: Removed extensive logging on debug level (#220)
* Fixed NPE at `ErrorUtils.java` (#212)
* Fixed Jenkins badges at `README.MD` (#246)
* Documented application.properties (#229)

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
* Added REST API Endpoint, which generates QR code from ident and optional size: `/api/qrCode/{ident}/{size}`

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

* Mattermost Bug: query with all spaces led to ":warning: Server Error" (#68)
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

Can be found from [here](https://github.com/kyberorg-archive/yals-play)
