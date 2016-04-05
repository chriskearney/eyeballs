[![Build Status](https://travis-ci.org/chriskearney/eyeballs.svg?branch=master)](https://travis-ci.org/chriskearney/eyeballs)
# eyeballs

A motion detection application built in Java, designed to run primarily on the Raspberry Pi, but also supports Mac OS X, Linux and Windows.  It runs "headless" with a built-in HTTP server to view motion events.

##Building
Download this version of [bridj-0.7-20140918.jar](https://github.com/chriskearney/eyeballs/blob/master/bridj-0.7-20140918.jar) that properly supports the ARM architecture (Raspberry Pi Support).  You will then need to install the jar file into your local Maven repository.

```
$ wget https://github.com/chriskearney/eyeballs/blob/master/bridj-0.7-20140918.jar
$ mvn install:install-file -Dfile=./bridj-0.7-20140918.jar -DgroupId=com.nativelibs4java -DartifactId=bridj \ 
-Dversion=0.7-20140918 -Dpackaging=jar
$ git clone git@github.com:chriskearney/eyeballs.git
$ cd eyeballs ; mvn clean install
```
