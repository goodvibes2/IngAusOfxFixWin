#          IngAusOfxFix V#2.00 28 Jul 2019 README.md file.

ING Australia OFX Fix for Windows (using JavaFX) or Linux (using OpenJFX)

This README.md is formatted for github markdown and is most easily read using a web browser
to view https://github.com/goodvibes2/IngAusOfxFixWin/blob/master/README.md.

The last known IngAusOfxFix stable series is

|Java Version | IngAusOfxFix Stable Series  |
|---          | ---                         |
| 8           | 1.01                        |
| 11          | 2.00                        |

Please see:
  - ChangeLog.txt for release details
  - LICENSE.txt for GPLv3 license details

## Table of Contents ##

  - [Overview](#Overview)
  - [Features](#Features)
  - [Selecting IngAusOfxFix Version](#IngAusOfxFixVersion)
  - [Selecting Precompiled IngAusOfxFix or Compile Yourself](#PreCompiledOrCompile)
  - [Dependencies](#Dependencies)
  - [Running](#Running)
  - [Internationalization](#Internationalization)
  - [Building and Installing](#BuildingAndInstalling)
  - [Supported Platforms](#SupportedPlatforms)
  - [Known Issues](#KnownIssues)

![Image of IngAusOfxFix](https://github.com/goodvibes2/IngAusOfxFixWin/blob/master/IngAusOfxFix.PNG)

<a name="Overview"></a>
## Overview ##

IngAusOfxFix is an application to automate the repetitive task of editing
the file of bank transactions downloaded in OFX format from bank ING Australia
so that they may be imported into GnuCash.

This application corrects 2 problems in the downloaded .ofx file:
  1. Add missing BANKACCTFROM xml entity before BANKTRANLIST.
     This is needed because GnuCash doesn't find any transactions to
     import without this.
  2. Ensure Financial Institution Transaction ID (FITID) is unique for
     every transaction by appending "." + DTPOSTED + "." + TRNAMT to the
     input file FITID.
     This is needed because FITID's are supposed to be unique for every
     transaction within each bank account and GnuCash will treat
     transactions with duplicate FITID's as already imported.

This application is written in Java using JavaFX (or OpenJFX) for
the graphical user interface. Java versions before 8 cannot be used with this
application as they do not support JavaFX.

The IngAusOfxFix project is _not_ part of the GnuCash project.

If you need help, please email goodchris96@gmail.com and I will help
if I can and have time available.
Please read all of this document before asking for help.

<a name="Features"></a>
## Features ##

Features include

- Available for both GNU/Linux and Microsoft Windows.
IngAusOfxFix has been tested in GNU/Linux Ubuntu 16.04 & 18.04 and Windows 10.
GnuCash is also available for Mac OS/X and IngAusOfxFix may work
but this has not (yet) been tested.

- An easy-to-use interface.
After downloading a .ofx file from the ING website, you just start IngAusOfxFix,
select the saved bank account configuration using the Bank Account Name
combobox, select any different options if required (such as the date range),
select the downloaded .ofx file and click the Start button. IngAusOfxFix
processes the input file and creates a new corrected file with the name of the
original file but with the filename suffixed with 'New' before the .ofx
extension. For example
```
  Original file: MyFile.ofx
  New file:      MyFileNew.ofx
```
Then just import the new file into GnuCash.

- Running this app on a file that has already been fixed will not cause any
problems as it checks that
  1. the BANKACCTFROM xml entity is missing before adding
     it.
  2. the FITID has not already been fixed by looking for a "." in it.

- One-time setup (per bank account) of
  1. **Bank Account Name**.

     The configuration details for up to 100 bank accounts may defined and
     saved, and they will be automatically loaded when this app starts.

     The most often used bank account can be flagged as the default, and it will
     be the bank account showing in the Bank Account Name combobox each time
     this app starts.
     The default bank account shows in **bold** font in the Bank Account Name
     combobox dropdown list.

     To make a new bank account the default:
     First select or add the new default bank account, then check (tick) the
     Default checkbox. It is not permitted to uncheck the Default checkbox.

     To add a new bank account:
     Enter the new bank account name in the Bank Account combobox, then press
     ENTER before leaving the combobox, then change the other fields
     (Bank Id, Account No, Account Type and Ofx Directory).

     It is not necessary to press ENTER if using JavaFX or OpenJFX version 8u92,
     but it doesn't cause any problems if you do.
     Use the Save Settings button to save the settings for all bank accounts.
     
     To delete the bank account settings for the current bank account shown in
     the Bank Account combobox, click the **Delete** button.
     The settings for the last remaining bank account and the default bank
     account cannot be deleted. To delete the default bank account, first make
     another bank account the default.

  2. **Bank Id**.

  3. **Account No**.
     This is your bank account number.
     **Note** the first time you import transactions for a particular bank
     account, GnuCash asks you to select which GnuCash bank account matches
     this bank account number, and automatically uses the same GnuCash bank
     account next time a file with the same Account No is imported. So select
     the correct GnuCash account the first time. It is possible, but fiddly, to
     correct if you get it wrong as it currently (GnuCash 2.6.13) involves
     either restoring your GnuCash data file, or manually editing it, or using
     a perl script to edit it (see Bayes at
     http://wiki.gnucash.org/wiki/Published_tools).

  4. **Account Type**, as defined by the OFX specification:
     ```
          CHECKING, CREDITLINE, MONEYMRKT or SAVINGS
     ```

  5. **OFX Directory**.
     The directory you store your downloaded OFX data files for this bank
     account. This can either be typed in or selected using the **Browse**
     button.

  After valid entry of account name, account id, account no, account type and
  OFX directory, the **Save Settings** button will be enabled, which when
  clicked, will save these entries, and the name of the default bank
  account, for all bank accounts, in file
  ```
    GNU/Linux: /home/[USER_NAME]/.IngAusOfxFix/defaultProperties
    Windows: C:\Users\USER_NAME]\.IngAusOfxFix/defaultProperties
  ```
  The next time IngAusOfxFix is started, the saved settings will be
  automatically loaded from the defaultProperties file, and the details for the
  default bank account shown.

- The **Input OFX File** can either be typed or selected using the **Browse**
  button.
  Once an Input OFX File has been selected, the file modification date and
  time is display below it, so the user can check it approximately matches
  the date and time expected.

- Transaction selection by date range.
  After the Input OFX File is entered or selected, the input file is read
  (but not processed), and the date range of transactions in the file is shown
  in the **Date From** and **Date To** screen fields.
  To restrict the range of transactions written to the output file, modify
  the Date From and/or Date To screen fields as required before using the
  **Start** button.

  This may be useful in the situation where you already have imported 
  transactions into GnuCash with the default FITID as supplied by the bank. As
  there is a limited range of date selection criteria when downloading the
  transactions in OFX format from the bank website, limiting the date range of
  transactions output from IngAusOfxFix will stop GnuCash importing transactions
  which it doesn't recognise as duplicates because the FITID's are not the same.

<a name="Home Page"></a>
### Home Page ###
None

<a name="IngAusOfxFixVersion"></a>
## Selecting IngAusOfxFix Version ##

IngAusOfxFix V1.x runs in Java 8.

IngAusOfxFix V2.x runs in Java 11.

IngAusOfxFix versions 1.x and 2.x have the same functionality. Currently (Sep
2019), the only difference is the version of Java they run in. Note that as Java
8 is no longer being developed, future IngAusOfxFix enhancements may not be
included in V1.x.

**Definition**

**JRE**
Java Runtime Environment. Simplified - All the files needed to run a Java
program.

If you already have a Java 8 JRE installed, you can run IngAusOfxFix V1.x.
You should run IngAusOfxFix V2.x if you do not have a Java 8 JRE already
installed.

**Windows**: You can download and install a Java 8 JRE if you wish. A JRE is not
included with Windows.

**GNU/Linux**
  You may wish to use the JRE that is available in the repositories
  for the version of GNU/Linux you are using. For example:

**Ubuntu 16.04 (or Mint 18)**
  Only has Java 8 available so if you already have Java 8 installed, you can
  save some disk space by using the latest IngAusOfxFix V1.x. You can still use
  IngAusOfxFix V2.x or later if you wish.

**Ubuntu 18.04 (or Mint 19)**
  Has both Java 8 and Java 11 and you can set the system to use either 8 or 11.
  If you have another application that requires Java 8 and you have limited
  disk space, then you could use IngAusOfxFix V1.x, otherwise you should use
  V2.x or later.

<a name="DowngradeUbuntu18.04ToJava8"></a>
## Downgrade Ubuntu 18.04 Java 11 to Java 8 ##

If you already have a JRE installed, by default Ubuntu 18.04 will have updated
it to Java 11. To set your Ubuntu 18.04 Java 11 system back to Java 8:
  - Set the *java* command to Java 8
  ```
    sudo update-alternatives --config java
  ```
  and enter the line number for Java 8.
  Check by running
  ```
    java -version
  ```
  - Downgrade the openjfx packages to Java 8
  ```
    sudo apt install openjfx=8u161-b12-1ubuntu2 \
      libopenjfx-java=8u161-b12-1ubuntu2 \
      libopenjfx-jni=8u161-b12-1ubuntu2
  ```
  - To stop the openjfx packages from being automatically updated to the latest
  java 11 packages next update
  ```
    apt-mark hold openjfx libopenjfx-jni libopenjfx-java
  ```
  Note: holding packages at a particular release level may cause
  incompatibilities with other packages so it is preferable not to do so.

  To undo setting a Ubuntu 18.04 system to Java 8
  - Set the *java* command to java 11
  ```
    sudo update-alternatives --config java
  ```
  and enter the line number for Java 11 (auto mode).
  - Unhold the openjfx packages from being automatically updated to the
  latest Java 11 packages next update
  ```
    apt-mark unhold openjfx libopenjfx-jni libopenjfx-java
  ```
  - Update the packages
  ```
    sudo apt-get update
    sudo apt-get upgrade
  ```

<a name="PreCompiledOrCompile"></a>
## Selecting to Use Precompiled IngAusOfxFix or Compile Yourself ##

There are 2 ways to use this application

  1. Use the Precompiled Binaries (programs) attached to this GitHub project -
     Download the prebuilt IngAusOfxFix.jar (Java 8) or Runtime Image (Java
     11).

     This application comes with no warranty and you should think about the
     security implications of using software downloaded from the internet. You
     are trusting my good nature and the codebase from which this is built!
     This code has not been security audited.

  OR

  2. Download the project source from GitHub, check the code for security and
     build your own binary.

### Precompiled binaries ###

#### IngAusOfxFix V1.x for Java 8 ####

To run **IngAusOfxFix V1.x**, you need to have a Java 8 JRE already installed
and you just need to download IngAusOfxFix.jar from GitHub.

**To download IngAusOfxFix.jar**

Copy and Paste one of the following URL's into a web browser
```
  https://github.com/goodvibes2/IngAusOfxFixWin/releases
  or
  https://github.com/goodvibes2/IngAusOfxFixLinux/releases
```

IngAusOfxFix has been written so that the same source files will work in both
GNU/Linux and Windows. Being Java bytecode built from the same Java source files,
IngAusOfxFix.jar from either IngAusOfxFixWin or IngAusOfxFixLinux should work in
both GNU/Linux and Windows.

Find the latest V1.x Release, find the Assets section and click on
IngAusOfxFix.jar to download it.

Usually it downloads to your *Downloads* folder.
Move the downloaded IngAusOfxFix.jar from your *Downloads* folder to a more
appropriate folder.

I suggest
```
GNU/Linux              /home/[USERNAME]/IngAusOfxFix/IngAusOfxFix.jar
Windows   C:\Users\[USERNAME]\Documents\IngAusOfxFix\IngAusOfxFix.jar
```

#### IngAusOfxFix V2.x for Java 11 ####

Java modular applications were introduced in Java 9. IngAusOfxFix V2.x is a
modular Java 11 application.

It is **not** possible to run a modular java app from a .jar file even if you
have an appropriate JRE already installed.

To run IngAusOfxFix V2.x, you do NOT need a Java Runtime Environment (JRE)
already installed as the app is distributed as a Runtime Image which includes
the required JRE and all the files needed.

Disadvantages of a Java Runtime Image:
  - Download is larger as it includes a JRE.
  - Runtime Images work only on the platform (Windows or GNU/Linux in this case)
  for which they are created.

Advantages:
  - Totally independent of any other installed JRE.

**To download a IngAusOfxFix V2.x Runtime Image (includes a Java 11 JRE)**

Copy and Paste one of the following URL's into a web browser depending on your
target platform (Windows or Linux)
```
  https://github.com/goodvibes2/IngAusOfxFixWin/releases
  or
  https://github.com/goodvibes2/IngAusOfxFixLinux/releases
```

Find the latest V2.x Release, then find the Assets section. To download the
Runtime Image archive, click on
```
GNU/Linux       IngAusOfxFix_rel2.nn.tar.gz
Windows         IngAusOfxFix_rel2.nn.zip
```
where 2.nn is the required release.

Usually a web browser downloads to your *Downloads* folder.
Move the downloaded archive file from your *Downloads* folder to a more
appropriate folder.

I suggest the following folders
```
GNU/Linux              /home/[USERNAME]/IngAusOfxFix/v2.nn
Windows   C:\Users\[USERNAME]\Documents\IngAusOfxFix\v2.nn
```
where 2.nn is the required release.

Unpack the Runtime Image

**GNU/Linux**
```
cd /home/[USERNAME]/IngAusOfxFix/v2.nn
tar zxf IngAusOfxFix_rel2.nn.tar.gz
```
where 2.nn is the required release.

**Windows**
Use 7-Zip in a command prompt window to unpack all the files from the
Runtime Image archive to the current directory
```
C:
cd \Users\[USERNAME]\Documents\IngAusOfxFix\v2.nn
"C:\Program Files\7-Zip\7z.exe" x IngAusOfxFix_rel2.nn.zip
```
where 2.nn is the required release.

<a name="Dependencies"></a>
## Dependencies ##

### Dependencies for using prebuilt IngAusOfxFix.jar V1.x in Java 8 ###

(See [Building and Installing](#BuildingAndInstalling) below if you wish to build from source)

If you wish to download and use the prebuilt IngAusOfxFix.jar from
this github project, the following packages need to be installed

#### GNU/Linux ####
These instructions are for Ubuntu 18.04 but should be similar for other
Gnu/Linux flavours/versions.

##### Java #####
IngAusOfxFix V1.x uses Java version 8 and JavaFX.
These can be either the open **or** Oracle versions.

See also [Known Issues](#KnownIssues).

###### Open Java ######
Openjdk (http://openjdk.java.net)
Install **openjfx** (which will also install **openjdk-8-jre** if not already
installed). E.g
```
        sudo apt-get install openjfx
```

See [Downgrade Ubuntu 18.04 Java 11 to Java 8](#DowngradeUbuntu18.04ToJava8)

Openjfx is available for Ubuntu from the wily (15.10), xenial (16.04) or
bionic (18.04) **universe** repository, but not for previous Ubuntu versions.

If openjfx is not available from your distribution's repositories, try
https://wiki.openjdk.java.net/display/OpenJFX/Main
or
http://chriswhocodes.com/.

###### Oracle Java ######
**Note** Oracle Java 8 includes JavaFX.

Install Oracle Java SE 8 RunTime Environment (jre) from
http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html

#### Windows ####
All the dependencies are available for Windows XP (I think) 7, 8, and 10.

##### Java #####
Prebuilt openjfx is not available (as far as I can tell as at 31 May 2016)
for Windows, so use Oracle Java 8 (which includes JavaFX) from
http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html.

See also [Known Issues](#KnownIssues).

### Dependencies for using prebuilt IngAusOfxFix V2.x Runtime Image (Java 11) ###

(See [Building and Installing](#BuildingAndInstalling) below if you wish to
build from source)

If you wish to download and use the prebuilt IngAusOfxFix V2.x Runtime Image
from this GitHub project, the following packages need to be installed

#### GNU/Linux & Windows ####

##### Java #####

As the Prebuilt IngAusOfxFix Runtime Image V2.x includes a Java 11 RTE and
openjfx, it is **not** neccessary to install java or openjfx separately.


<a name="Running"></a>
## Running ##

### Running IngAusOfxFix V1.x for Java 8 (IngAusOfxFix.jar) ###

### GNU/Linux ###
  To run the project from the command line, type the following
```
    java -jar "[PathTo]/IngAusOfxFix.jar" &
```
E.g.
```
    java -jar /home/[USER_NAME]/IngAusOfxFix/IngAusOfxFix.jar &
```
**Ubuntu** To set up an ingausofxfix.desktop file so it can be started from the
Unity Dash

create either (or both)
```
/usr/share/applications/ingausofxfix.desktop
or 
/home/[USER_NAME]/.local/share/applications/ingausofxfix.desktop
```
containing
```
[Desktop Entry]
Name=IngAusOfxFix
Comment=ING Aus OFX Fix
Exec=java -jar /home/[USER_NAME]/IngAusOfxFix/IngAusOfxFix.jar
Icon=gnucash-icon
Terminal=false
Type=Application
Categories=Office;Finance;
```
Ensure the **Exec=** line above points to where you put **IngAusOfxFix.jar**.

You can also create a shortcut on your **Desktop** by copying ingausofxfix.desktop
to ~/Desktop. Ensure it has execute permissions or you will get error
**Untrusted Application Launcher**. E.g.
```
  cp /usr/share/applications/ingausofxfix.desktop ~/Desktop
  chmod +x ~/Desktop/ingausofxfix.desktop
```

### Windows ###
Create a shortcut on your desktop

Right click on the desktop,
New, Shortcut,
Type in the full command
```
"C:\Program Files (x86)\Common Files\Oracle\Java\javapath\javaw.exe" -jar C:\Users\[USER_NAME]\Documents\IngAusOfxFix\IngAusOfxFix.jar
```
Name the shortcut **IngAusOfxFix**.

If you wish to see stdout or stderr for debugging, use **java.exe** instead of
**javaw.exe** in the command above. Javaw.exe is intended for gui (window)
applications (like IngAusOfxFix) and does not show a console window.

### Running IngAusOfxFix V2.x for Java 11 (Runtime Image) ###

#### GNU/Linux ####

  To run the app from the command line, type the following
```
  ~/IngAusOfxFix/v2.nn/dist/jlink/IngAusOfxFixJ11/bin/IngAusOfxFix &
```
where 2.nn is the required release.
E.g.
```
  ~/IngAusOfxFix/v2.00/dist/jlink/IngAusOfxFixJ11/bin/IngAusOfxFix &
```
**Ubuntu** To set up an ingausofxfix.desktop file so it can be started from the
Unity Dash or *Gnome Applications overview*

create either (or both)
```
/usr/share/applications/ingausofxfix.desktop
or 
/home/[USERNAME]/.local/share/applications/ingausofxfix.desktop
```
containing
```
[Desktop Entry]
Name=IngAusOfxFix
Comment=ING Aus OFX Fix
Exec=/home/[UserName]/IngAusOfxFix/v2.nn/dist/jlink/IngAusOfxFixJ11/bin/IngAusOfxFix
Icon=gnucash-icon
Terminal=false
Type=Application
Categories=Office;Finance;
```
where 2.nn is the required release.

You can also create a shortcut on your **Desktop** by copying ingausofxfix.desktop
to ~/Desktop. Ensure it has execute permissions or you will get error
**Untrusted Application Launcher**. E.g.
```
  cp /usr/share/applications/ingausofxfix.desktop ~/Desktop
  chmod +x ~/Desktop/ingausofxfix.desktop
```

#### Windows ####

Create a shortcut on your desktop

Right click on the desktop,
New, Shortcut,
Browse to and select your IngAusOfxFix.bat file
or just type in the full filestring E.g
```
C:\Users\[USERNAME]\Documents\IngAusOfxFix\v2.nn\dist\jlink\IngAusOfxFixJ11\bin\IngAusOfxFix.bat
```
where 2.nn is the required release.
Name the shortcut **IngAusOfxFix**.


<a name="Internationalization"></a>
## Internationalization ##
--------------------

IngAusOfxFix is currently English only.


<a name="BuildingAndInstalling"></a>
## Building and Installing ##
---------------------

There are 2 versions of IngAusOfxFix on GitHub
- https://github.com/goodvibes2/IngAusOfxFixWin
  which is the NetBeans IDE 8.2 project for Microsoft Windows using
  Oracle Java 8 (Includes JavaFX) or Oracle Java 11 and Gluon JavaFX 11.0.2
- https://github.com/goodvibes2/IngAusOfxFixLinux
  which is the NetBeans IDE 8.1 project for GNU/Linux Ubuntu 18.04 using
  Java OpenJDK 8 (and OpenJFX 8) or Oracle Java 11 and Gluon OpenJFX 11.0.2

The java source files in both the above projects should be identical
and the dist/IngAusOfxFix.jar files in both, being Java bytecode, should
work in both GNU/Linux and Windows with a Java 8 JRE. The differences between
these projects are only in the NetBeans project files used for building the
project. This is so as to make it easy to download (or clone) the project, set up
the dependencies, and then be able to open the project in NetBeans IDE, and build
it without any further setup.

### **Note** This project was developed and tested using ###

**IngAusOfxFix V1.x for Java 8**

**GNU/Linux**
```
      Ubuntu 18.04.2 bionic
      openjdk version 1.8.0_191
      openjfx 8u161-b12-1ubuntu2
      SceneBuilder (Gluon) 8.2.0
      NetBeans IDE 8.1
```
**Windows**
```
      Windows 10 64-bit
      Oracle 8 jdk (1.8.0_191) which includes JavaFX
      SceneBuilder (Gluon) 8.5.0
      NetBeans IDE 8.2
```

**IngAusOfxFix V2.x for Java 11**

**GNU/Linux**
```
      Ubuntu 18.04.2 bionic
      Oracle jdk 11.0.4
      Gluon openjfx 11.0.2
      Gluon jfx jmods 11.0.2
      SceneBuilder (Gluon) 8.2.0
      Apache NetBeans IDE 11.0
```
**Windows**

```
      Windows 10 64-bit
      Oracle jdk (11.0.4)
      Gluon openjfx 11.0.2
      Gluon JavaFX jmods 11.0.2
      SceneBuilder (Gluon) 8.5.0
      Apache NetBeans IDE 11.0
```

### Build the V1.x IngAusOfxFix.jar from source ###
You will need

### GNU/Linux ###
**Note** These instructions are for Ubuntu 18.04 but should be similar for other
Gnu/Linux flavours/versions.

#### Java ####
IngAusOfxFix uses Java version 8 and JavaFX (or OpenJFX).
These can be EITHER the open OR Oracle versions.

##### Openjdk #####
You'll need the Java Development Kit (jdk) and OpenJFX. E.g.
```
sudo apt-get install openjdk-8-jdk openjfx
```

Ubuntu 18.04 includes Java 8 and Java 11.
IngAusOfxFix V1.x needs Java 8, so set the default Java version to 8. See
[Downgrade Ubuntu 18.04 Java 11 to Java 8](#DowngradeUbuntu18.04ToJava8)

OR
##### Oracle Java 8 jdk (includes JavaFX) #####
Download and install Oracle Java SE 8 Development Kit from
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html.

**Note** You can download a package which includes both the NetBeans IDE and
the jdk from http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html.

#### SceneBuilder ####
SceneBuilder is the gui tool for modifying the user interface, details
of which are held in IngAusOfxFix.fxml.
You only need to install SceneBuilder if you wish to modify the user
interface.

SceneBuilder is NOT available in Ubuntu repositories, and is no longer
    available from Oracle.

Download the .deb from http://gluonhq.com/open-source/scene-builder

E.g. Linux 64 bit: scenebuilder-8.2.0_x64_64.deb

Install
```
sudo dpkg -i scenebuilder-8.2.0_x64_64.deb
```
#### NetBeans IDE
If you haven't already installed NetBeans as part of the Oracle combined
jdk and NetBeans
```
sudo apt-get install netbeans
```

### Windows ###

#### Java ####
IngAusOfxFix V1.x uses Java version 8 and JavaFX.
    Openjdk and OpenJFX are NOT available for Windows, so use Oracle versions.

##### Oracle Java 8 jdk (includes JavaFX) #####
Download and install Oracle Java SE 8 Development Kit from
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html.

**Note** You can download a package which includes both the NetBeans IDE and
the jdk from http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html.

#### SceneBuilder ####
SceneBuilder is the gui tool for modifying the user interface, details
of which are held in IngAusOfxFix.fxml.
You only need to install SceneBuilder if you wish to modify the user
interface.

SceneBuilder is no longer available from Oracle.

Download from http://gluonhq.com/open-source/scene-builder.

#### NetBeans IDE ####
If you haven't already installed NetBeans as part of an Oracle combined
jdk and NetBeans

Download and install from https://netbeans.org/downloads/.

### Build the V2.x Runtime Image from source ###

Follow the instructions at https://openjfx.io/openjfx-docs/#introduction
including the instructions for using JavaFX and NetBeans to build a
Modular app from the IDE (NetBeans).

**Note** The instructions on the above webpage are for Java 12 but IngAusOfxFix
V2.x was built for Java 11 as that was the version available in the Ubuntu 18.04
repositories. Substitute Java 11 for Java 12 in the above webpage instructions.

As a Modular Java 11 app is run from a Runtime Image (which includes its
own JRE), and the Ubuntu jdk 11.0.4 is broken when used for a JavaFX Modular
app (so we need to use the Oracle jdk), the Ubuntu jdk is actually irrelevant,
and this app could probably have been built using Java 12 (or later).

You will need

#### GNU/Linux ####
**Note** These instructions are for Ubuntu 18.04 but should be similar for other
GNU/Linux flavours/versions.

##### Java JDK #####
IngAusOfxFix V2.x uses Oracle Java jdk version 11

Download jdk-11.0.4_linux-x64_bin.tar.gz from Oracle http://jdk.java.net/11/.
The .gz contains base directory jdk-11.0.4.

Extract (assuming the download is in ~/Downloads)
```
  cd $HOME
  mkdir java
  cd java
  tar zxf ~/Downloads/jdk-11.0.4_linux-x64_bin.tar.gz
```
This will create $HOME/java/jdk-11.0.4/...

###### openjfx ######
Download JavaFX Linux SDK (openjfx-11.0.2_linux-x64_bin-sdk.zip)
from https://gluonhq.com/products/javafx/. The .zip contains base directory
javafx-sdk-11.0.2.

Extract (assuming download is in ~/Downloads)
```
cd $HOME/java
unzip ~/Downloads/openjfx-11.0.2_linux-x64_bin-sdk.zip
```
This will create $HOME/java/javafx-sdk-11.0.2/...

##### jfx jmods #####
Download JavaFX Linux jmods (openjfx-11.0.2_linux-x64_bin-jmods.zip)
from https://gluonhq.com/products/javafx/. The .zip contains base directory
javafx-jmods-11.0.2.

Extract (assuming download is in ~/Downloads)
```
cd $HOME/java
unzip ~/Downloads/openjfx-11.0.2_linux-x64_bin-jmods.zip
```
This will create $HOME/java/javafx-jmods-11.0.2/...

##### SceneBuilder #####
SceneBuilder is the gui tool for modifying the user interface, details
of which are held in IngAusOfxFix.fxml.
You only need to install SceneBuilder if you wish to modify the user
interface.

SceneBuilder is NOT available in Ubuntu repositories, and is no longer
    available from Oracle.

Download the .deb from http://gluonhq.com/open-source/scene-builder

E.g. Linux 64 bit: scenebuilder-8.2.0_x64_64.deb

Install
```
sudo dpkg -i scenebuilder-8.2.0_x64_64.deb
```

##### NetBeans IDE #####
Ubuntu 18.04 repositories contains NetBeans 10.0 but we need Apache NetBeans 11.
```
cd ~/java
wget https://www-us.apache.org/dist/incubator/netbeans/incubating-netbeans/incubating-11.0/incubating-netbeans-11.0-bin.zip
unzip incubating-netbeans-11.0-bin.zip
sudo mv netbeans/ /opt/
```
The script to run NetBeans is /opt/netbeans/bin/netbeans

Create NetBeans Desktop Launcher
```
  sudo nano /usr/share/applications/netbeans11.desktop
```
with contents
```
[Desktop Entry]
Name=NetBeans 11 IDE
Comment=NetBeans 11 IDE
Type=Application
Encoding=UTF-8
Exec=/opt/netbeans/bin/netbeans
Icon=/opt/netbeans/nb/netbeans.png
Categories=GNOME;Application;Development;
Terminal=false
StartupNotify=true
```

#### Windows ####

##### Java #####
IngAusOfxFix V2.x uses Java JDK version 11 and openjfx.

###### Oracle Java JDK 11 ######
Openjdk 11 is only available from Oracle. You will need a free Oracle account
to download the JDK.

Download Oracle Java SE 11.0.4 Java Development Kit
(jdk-11.0.4_windows-x64_bin.exe) from
https://www.oracle.com/technetwork/java/javase/downloads/index.html.

Double click on the downloaded .exe to install (by default) to
C:\Program Files\Java\jdk-11.04.

###### Openjfx ######
Download JavaFX Windows SDK (openjfx-11.0.2_windows-x64_bin-sdk.zip)
from https://gluonhq.com/products/javafx.
The .zip contains base directory javafx-sdk-11.0.2.

Use 7-Zip in a command prompt window to unpack all the files from the zip
archive to the current directory
```
C:
cd "\Program Files\Java"
"C:\Program Files\7-Zip\7z.exe" x openjfx-11.0.2_windows-x64_bin-sdk.zip
```
This will create C:\Program Files\Java\javafx-sdk-11.0.2\\...

###### jfx jmods ######
Download JavaFX Windows jmods (openjfx-11.0.2_windows-x64_bin-jmods.zip)
from https://gluonhq.com/products/javafx
The .zip contains base directory javafx-jmods-11.0.2.

Use 7-Zip in a command prompt window to unpack all the files from the zip
archive to the current directory
```
C:
cd "\Program Files\Java"
"C:\Program Files\7-Zip\7z.exe" x openjfx-11.0.2_windows-x64_bin-jmods.zip
```
This will create C:\Program Files\Java\javafx-jmods-11.0.2\\...

##### SceneBuilder #####
SceneBuilder is the gui tool for modifying the user interface, details
of which are held in IngAusOfxFix.fxml.
You only need to install SceneBuilder if you wish to modify the user
interface.

SceneBuilder is no longer available from Oracle.

Download from http://gluonhq.com/open-source/scene-builder.

##### NetBeans IDE #####
Download NetBeans 11.0 LTS Binary (incubating-netbeans-11.0-bin.zip)
from https://netbeans.apache.org/download/. The zip archive files all start with
folder *netbeans*.

As you may have permissions problems extracting directly to C:\Program Files,
extract to your Downloads folder, then use File Explorer to create folder
C:\Program Files\Netbeans11.0 and move the extracted *netbeans* folder to
C:\Program Files\Netbeans11.0.

Create a desktop shortcut pointing to
  C:\Program Files\Netbeans11.0\netbeans\bin\netbeans64.exe

#### To download the source files and NetBeans project ####

There are 2 versions of IngAusOfxFix on github
- https://github.com/goodvibes2/IngAusOfxFixWin
  which is the project for Microsoft Windows
- https://github.com/goodvibes2/IngAusOfxFixLinux
  which is the project for GNU/Linux

The java source files in both the above projects should be identical
and the dist/IngAusOfxFix.jar files in both should work in both GNU/Linux
or Windows. The differences between these projects are only in the NetBeans
project files used for building the project. This is so as to make it easy
to download (or clone) the project, set up the dependencies, and then be
able to open the project in NetBeans, and be able to build it without any
further setup.

There are 2 main ways to download the IngAusOfxFix NetBeans project from github

1) **Clone** (if you already have a github account and git installed)

   **GNU/Linux** At the command line
   ```
      cd
      mkdir NetBeansProjects
      cd NetBeansProjects
      git clone https://github.com/goodvibes2/IngAusOfxFixLinux IngAusOfxFix
   ```
   **Windows** In a **git** shell
   ```
      cd ~/Documents
      mdkir NetBeansProjects
      cd NetBeansProjects
      git clone https://github.com/goodvibes2/IngAusOfxFixWin IngAusOfxFix
   ```

##### IngAusOfxFix V1.x Java 8 Extra Task #####
   As the master branch has now been updated to Java 11, you need to reset back to
   the last commit before Java 11 was merged into master. To create a new branch
   (called say java8)
   ```
     cd IngAusOfxFix
   GNU/Linux:
     git checkout -b java8 85ba487d6cba6fda081dbaf2fb154273332b3215
   Windows:
     git checkout -b java8 27495fbb59774ca56f73849148f9e7a35b585425
   ```

OR

2) **Download the source code archive for the required release**

   Copy and Paste one of the following URL's into a web browser depending on your
   target platform (Windows or Linux)
   ```
     https://github.com/goodvibes2/IngAusOfxFixWin/releases
    or
     https://github.com/goodvibes2/IngAusOfxFixLinux/releases
   ```

   Find the latest V1.x or V2.x Release as required, then find the Assets section.
   Click on
   ```
   GNU/Linux       Source code (tar.gz)
   Windows         Source code (zip)
   ```
   This will download source code archive file
   ```
   GNU/Linux       IngAusOfxFixLinux-v.nn.tar.gz
   Windows         IngAusOfxFixWin-v.nn.zip
   ```
   where v.nn is the required release.

   Usually a web browser downloads to your *Downloads* folder.
   Move the downloaded archive file from your *Downloads* folder to a more
   appropriate folder.

   I suggest the following folders
   ```
     GNU/Linux              /home/[USERNAME]/NetBeansProjects
     Windows   C:\Users\[USERNAME]\Documents\NetBeansProjects
   ```

   Unpack the source code and project files

   **GNU/Linux**
   ```
     cd /home/[USERNAME]/NetBeansProjects
     tar zxf IngAusOfxFixLinux-v.nn.tar.gz
   ```
   where v.nn is the required release.

   **Windows**
   Use 7-Zip in a command prompt window to unpack all the files from the
   source code archive to the current directory
   ```
   C:
   cd \Users\[USERNAME]\Documents\NetBeansProjects
   "C:\Program Files\7-Zip\7z.exe" x IngAusOfxFixWin-v.n.n.zip
   ```
   where v.nn is the required release.

After extracting, before you open the project in NetBeans, you should edit the
following files to ensure the paths match your project
```
  build.xml
  nbproject/private/private.properties
  nbproject/build-impl.xml
  nbproject/project.properties
  nbproject/project.xml
```

<a name="SupportedPlatforms"></a>
## Supported Platforms ##

IngAusOfxFix is known to work with the following operating systems

- GNU/Linux             -- x86
- Windows               -- x86

IngAusOfxFix can probably be made to work on any platform where GnuCash
does, so long as the [Dependencies](#Dependencies) are available.

<a name="KnownIssues"></a>
## Known Issues ##

1) Java 1.8.0_72 (8u72) or later is required due to bug
   https://bugs.openjdk.java.net/browse/JDK-8136838 as the value of
   ComboBox.getValue() was not correct in previous versions.

   As of 14 Sep 2019, the current Java version on Windows is 1.8.0_221,
   Ubuntu 16.04 is 1.8.0_222 and Ubuntu 18.04 is 1.8.0_222.
   Ubuntu 16.04 openjfx is version 8u60-b27-4 which works so long as when
   adding a new bank account, ENTER is pressed after typing a new bank account
   name into the Bank Account combobox. I.e. Press ENTER before leaving the
   combobox.

2) Any new bank account added to the Bank Account combobox is added to the end
   of the combobox dropdown list, rather than in it's sorted position. The bank
   account settings are sorted before they are saved to the defaultProperties
   file, so the combobox dropdown list will be sorted next time the program is
   started. This is because of the following bug in in Java 1.8.0_92
     https://bugs.openjdk.java.net/browse/JDK-8087838.
   The use of a SortedList for the combobox can be re-instated after the above
   bug is fixed. See also
   http://stackoverflow.com/questions/38342046/how-to-use-a-sortedlist-with-javafx-editable-combobox-strange-onaction-events


I hope you find IngAusOfxFix useful.

Thank you.
