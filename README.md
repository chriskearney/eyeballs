[![Build Status](https://travis-ci.org/chriskearney/eyeballs.svg?branch=master)](https://travis-ci.org/chriskearney/eyeballs)
# eyeballs

A motion detection application built in Java, designed to run primarily on the Raspberry Pi, but also supports Mac OS X, Linux and Windows.  It runs "headless" with a built-in HTTP server to view motion events.  Optionally, you may configure Eyeballs to automatically upload Motion Events to a remote file service, current support exists for both SFTP and Dropbox.

##Download
[eyeballs-1.0-SNAPSHOT.jar](http://ktwit.net/eyeballs-1.0-SNAPSHOT.jar)

[eyeballs.yaml](https://raw.githubusercontent.com/chriskearney/eyeballs/master/eyeballs.yaml)
##Running
I typically use a screen session to run the eyeballs jar file so that I can keep it running in the background. Please note that instead of using sudo, you could change the permissions of the /dev/video0 file to be readable by non root users.  If you are running eyeballs on the raspberry pi, please see the necessary "Raspian Tweaks" section below.
```
sudo java -jar eyeballs-1.0-SNAPSHOT.jar server eyeballs.yaml
```
After eyeballs is running, you can acccess the HTTP interface by navigating to any of the following urls:
* **http://\<raspberry-pi-address\>:4444/image**
* **http://\<raspberry-pi-address\>:4444/view/recent_events/1000**
* **http://\<raspberry-pi-address\>:4444/view/recent_events/image**

##Raspbian Tweaks
Before you can run eyeballs on the Raspberry Pi/Raspian, you will need to tweak a USB setting (applicable if you are using a USB webcam).  Insert dwc_otg.fiq_fsm_mask=0x3 some where on the kernel command line:
```
# insert: dwc_otg.fiq_fsm_mask=0x3
$ sudo vi /boot/cmdline.txt
```
Enable the Camera (if using official Raspberry Pi Camera)
```
# Run the raspberry pi configuration program, and enable the Camera.
$ raspbi-config
```
Add "sudo modprobe bcm2835-v4l2" to /etc/rc.local
```
# add: sudo modprobe bcm2835-v4l2
# sudo vi /etc/rc.local 
```

##Configuration
####Http Basic Authorization
Place in eyeballs.yaml:
```
useAuth: true
username: admin
password: password
```
####SFTP Motion Event Upload
Place in eyeballs.yaml, the sftpDestinationHost must be configured to allow password-less (key-based) ssh logins. 
```
useSftp: true
sftpDestinationHost: 192.168.0.10
sftpUsername: eyeballs
sftpDestinationDirectory: /home/eyeballs/camera
sftpRemotePort: 22
```

####Dropbox Motion Event Upload
You can obtain a Dropbox access token by using a built in class that is included with the eyeballs jar file.  First you need to go to your Dropbox account and create an "app" that will allow eyeballs to upload files to your Dropbox account.

[Create App - Dropbox](https://www.dropbox.com/developers-v1/apps/create)

Choose "Dropbox API App" and select "My app only needs access to files it creates."  Once your App is created, grab both the App Key and App Secret and use them with the following command to obtain a Dropbox Access Token:

```
java -cp ./eyeballs-1.0-SNAPSHOT.jar com.comandante.eyeballs.DropwizardToken <App Key> <App Secret>
```

Use this Access Token in your eyeballs.yaml file:
```
useDropbox: true
dropBoxAccessToken: <dropbox access token>
```

If you wish, you can also have Dropbox act as the backing store for all of the images served via the API/WEB interface.
```
useDropboxPersistence: true
```

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


