[![Build Status](https://travis-ci.org/yadevee/yals.svg?branch=master)](https://travis-ci.org/yadevee/yals)

# Yet another link shortener

Just a simple link shortener like many others for friends.

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
