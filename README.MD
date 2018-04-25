[![Build Status](https://travis-ci.org/yadevee/yals.svg?branch=master)](https://travis-ci.org/yadevee/yals)

# Yet another link shortener

Just a simple link shortener like many others. Made for friends.

## For Developers
### How to start coding ?

* Git clone ``` git clone repoUrl ```

* Import into IDE as **Maven** project

* Select all autodetected Spring facets

* Use ``` YalsApplication.main() ``` as start point

### Trunk build status

[![Build Status](https://travis-ci.org/yadevee/yals.svg?branch=trunk)](https://travis-ci.org/yadevee/yals)
[![codecov](https://codecov.io/gh/yadevee/yals/branch/trunk/graph/badge.svg)](https://codecov.io/gh/yadevee/yals)

### How run application ?
* Build with Maven with H2 profile enabled

``` mvn -P h2 -Dskip.tests clean package```

* Run JAR with following options:

``` java 
   -Ddb.type=H2 
   -Ddb.driver=org.h2.Driver
   -Ddb.url=jdbc:h2:mem:test
```

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

## Version 1.0 and less (01/04/16)

Can be found from [here](https://github.com/yadevee/yals-play/blob/trunk/README.md#yet-another-link-shortener)
