[![Build Status](https://travis-ci.org/chriskearney/eyeballs.svg?branch=master)](https://travis-ci.org/chriskearney/eyeballs)
# eyeballs

A motion detection application built in Java, designed to run primarily on the Raspberry Pi, but also supports Mac OS X, Linux and Windows.  It runs "headless" with a built-in HTTP server to view motion events.  Optionally, you may configure Eyeballs to automatically upload Motion Events to a remote file service, current support exists for both SFTP and Dropbox.

##Build
Download this version of [bridj-0.7-20140918.jar](https://github.com/chriskearney/eyeballs/blob/master/bridj-0.7-20140918.jar) that properly supports the ARM architecture (Raspberry Pi Support).  You will then need to install the jar file into your local Maven repository.  Eyeballs requires Java 8.

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
```
##Configure
Before you can run eyeballs on the Raspberry Pi/Raspian, you will need to tweak a USB setting (applicable if you are using a USB webcam).  Insert dwc_otg.fiq_fsm_mask=0x3 some where on the kernel command line:
```
#insert: dwc_otg.fiq_fsm_mask=0x3
$ sudo vi /boot/cmdline.txt
```
Enable the Camera (if using official Raspberry Pi Camera)
```
# Run the raspberry pi configuration program, and enable the Camera.
$ raspbi-config
```
Add "sudo modprobe bcm2835-v4l2" to /etc/rc.local
```
#add sudo modprobe bcm2835-v4l2
# sudo vi /etc/rc.local 
```
