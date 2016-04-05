[![Build Status](https://travis-ci.org/chriskearney/eyeballs.svg?branch=master)](https://travis-ci.org/chriskearney/eyeballs)
# eyeballs

A motion detection application built in Java, designed to run primarily on the Raspberry Pi, but also supports Mac OS X, Linux and Windows.  It runs "headless" with a built-in HTTP server to view motion events.

##Building
Download this version of [bridj-0.7-20140918.jar](https://github.com/chriskearney/eyeballs/blob/master/bridj-0.7-20140918.jar) that properly supports the ARM architecture (Raspberry Pi Support).  You will then need to install the jar file into your local Maven repository.  Also, Java 8 is needed to compile successfully.

```
$ wget https://github.com/chriskearney/eyeballs/blob/master/bridj-0.7-20140918.jar
$ mvn install:install-file -Dfile=./bridj-0.7-20140918.jar -DgroupId=com.nativelibs4java -DartifactId=bridj -Dversion=0.7-20140918 -Dpackaging=jar
$ git clone https://github.com/chriskearney/eyeballs
$ cd eyeballs ; mvn clean install
```
The resulting jar file will be located here:
```
~/.m2/repository/com/comandante/eyeballs/1.0-SNAPSHOT/eyeballs-1.0-SNAPSHOT.jar
```
You can copy this jar file and the eyeballs.yaml (configuration) file to the Raspberry Pi.
```
scp ~/.m2/repository/com/comandante/eyeballs/1.0-SNAPSHOT/eyeballs-1.0-SNAPSHOT.jar pi@xxx.xxx.xxx.xxx:~/
scp eyeballs/eyeballs.yaml pi@xxx.xxx.xxx.xxx:~/
