#          IngAusOfxFix V#1.01 03 Aug 2016 README.md file.

ING Australia OFX Fix for Windows (using JavaFX) or Linux (using OpenJFX)

This README.md is formatted for github markdown and is most easily read using a web browser
to view https://github.com/goodvibes2/IngAusOfxFixWin/blob/master/README.md.

The last known IngAusOfxFix stable series is the 1.0x series.

Please see:
  - ChangeLog.txt for release details
  - LICENSE.txt for GPLv3 license details

## Table of Contents ##

  - [Overview](#Overview)
  - [Features](#Features)
  - [Dependencies](#Dependencies)
  - [Running](#Running)
  - [Internationalization](#Internationalization)
  - [Building and Installing](#Building and Installing)
  - [Supported Platforms](#Supported Platforms)
  - [Known Issues](#Known Issues)

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

This application is written in Java version 8 using JavaFX (or OpenJFX) for
the graphical user interface. Java versions before 8 cannot be used with this
application as they do not support JavaFX.

The IngAusOfxFix project is _not_ part of the GnuCash project.

If you need help, please email chris.good@ozemail.com.au and I will help
if I can and have time available.
Please read all of this document before asking for help.

<a name="Features"></a>
## Features ##

Features include

- Available for both GNU/Linux and Microsoft Windows.
IngAusOfxFix has been tested in GNU/Linux Ubuntu 16.04 and Windows 10.
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

### Precompiled binaries ###

```
 https://github.com/goodvibes2/IngAusOfxFixWin/blob/master/dist/IngAusOfxFix.jar
 or
 https://github.com/goodvibes2/IngAusOfxFixLinux/blob/master/dist/IngAusOfxFix.jar
```
Being Java bytecode built from the same Java source files, either of the above
should work in either GNU/Linux or Windows.

To download IngAusOfxFix.jar

Paste either of the above URL's into a web browser,
**Right** click on the **Raw** button, **Save target as**,
select the required location.

I suggest
```
GNU/Linux              /home/[USER_NAME]/IngAusOfxFix/IngAusOfxFix.jar
Windows   C:\Users\[USER_NAME]\Documents\IngAusOfxFix\IngAusOfxFix.jar
```

<a name="Dependencies"></a>
## Dependencies ##

There are 2 ways to use this application

  1. Download the prebuilt IngAusOfxFix.jar from this project

     This application comes with no warranty and you should think about the security
     implications of using software downloaded from the internet. You are trusting
     my good nature and the codebase from which this is built!
     This code has not been security audited.

  OR

  2. Download the project source from github, check the code for security and
     build your own copy of IngAusOfxFix.jar.

### To download the prebuilt IngAusOfxFix.jar from github

**Note** There are 2 versions of IngAusOfxFix on github
  ```
    https://github.com/goodvibes2/IngAusOfxFixWin
  ```
  which is the project for Microsoft Windows using
  Oracle Java 8, and netbeans IDE 8.0

  AND
  ```
    https://github.com/goodvibes2/IngAusOfxFixLinux
  ```
  which is the project for GNU/Linux Ubuntu 16.04 using
  Java OpenJDK 8, OpenJFX, and netbeans IDE 8.1

The java source files in both the above projects should be identical
and the dist/IngAusOfxFix.jar files in both, being Java bytecode, should
work in both GNU/Linux and Windows. The differences between these projects
are only in the netbeans project files used for building the project.
This is so as to make it easy to download (or clone) the project, set up the
dependencies, and then be able to open the project in netbeans IDE, and
build it without any further setup.

Paste either of the following URL's into a web browser
```
https://github.com/goodvibes2/IngAusOfxFixWin/blob/master/dist/IngAusOfxFix.jar
 or
https://github.com/goodvibes2/IngAusOfxFixLinux/blob/master/dist/IngAusOfxFix.jar
```
**Right** click on the **Raw** button, **Save target as**,
  select the required location.

I suggest
```
GNU/Linux   /home/[USER_NAME]/IngAusOfxFix/IngAusOfxFix.jar
Windows     C:\Users\[USER_NAME]\Documents\IngAusOfxFix\IngAusOfxFix.jar
```

### Dependencies for using prebuilt backupGnuCash.jar ###

(See [Building and Installing](#Building and Installing) below if you wish to build from source)

If you wish to download and use the prebuilt IngAusOfxFix.jar from
this github project, the following packages are required to be installed

#### GNU/Linux ####
These instructions are for Ubuntu 16.04 but should be similar for other
Gnu/Linux flavours/versions.

##### Java #####
IngAusOfxFix uses Java version 8 (or later) and JavaFX.
These can be either the open **or** Oracle versions.

See also [Known Issues](#Known Issues).

###### Open Java ######
Openjdk (http://openjdk.java.net)
Install **openjfx** (which will also install **openjdk-8-jre** if not already
installed). E.g
```
        sudo apt-get install openjfx
```
Openjfx is available for Ubuntu from the wily (15.10) or xenial (16.04)
**universe** repository, but not for previous Ubuntu versions.

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

See also [Known Issues](#Known Issues).

<a name="Running"></a>
## Running ##

### GNU/Linux ###
  To run the project from the command line, type the following
```
    java -jar "[PathTo]/IngAusOfxFix.jar" &
```
E.g.
```
    java -jar /home/[USER_NAME]/IngAusOfxFix/IngAusOfxFix.jar &
```
**Ubuntu** To set up a IngAusOfxFix.desktop file so it can be started from the Unity
Dash

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
Comment=Backup GnuCash
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
  chmod +x ~/Desktop
```

### Windows ###
Create a shortcut on your desktop

Right click on the desktop,
New, Shortcut,
Browse to and select your IngAusOfxFix.jar file
or just type in the full filestring E.g
```
C:\Users\[USER_NAME]\Documents\IngAusOfxFix\IngAusOfxFix.jar
```
Name the shortcut **IngAusOfxFix**.

<a name="Internationalization"></a>
## Internationalization ##
--------------------

IngAusOfxFix is currently English only.


<a name="Building and Installing"></a>
## Building and Installing ##
---------------------

### **Note** This project was developed and tested using ###

**GNU/Linux**
```
      Ubuntu 16.04 xenial
      openjdk version 1.8.0_91
      openjfx 8u60-b27-4
      SceneBuilder (Gluon) 8.2.0 
      netbeans IDE 8.1
```
**Windows**
```
      Windows 10 64-bit
      Oracle 8 jdk (1.8.0_92) which includes JavaFX
      SceneBuilder (Oracle) 2.0
      netbeans IDE 8.1
```
If you wish to build the IngAusOfxFix.jar from source, you'll need

### GNU/Linux ###
**Note** These instructions are for Ubuntu 16.04 but should be similar for other
Gnu/Linux flavours/versions.

#### Java ####
IngAusOfxFix uses Java version 8 and JavaFX (or OpenJFX).
These can be EITHER the open OR Oracle versions.

##### Openjdk #####
You'll need the Java Development Kit (jdk) and OpenJFX. E.g.
```
sudo apt-get install openjdk-8-jdk openjfx
```
OR
##### Oracle Java 8 jdk (includes JavaFX) #####
Download and install Oracle Java SE 8 Development Kit from
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html.

**Note** You can download a package which includes both the netbeans IDE and
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
#### Netbeans IDE
If you haven't already installed netbeans as part of the Oracle combined
jdk and netbeans
```
sudo apt-get install netbeans
```

### Windows ###

#### Java ####
IngAusOfxFix uses Java version 8 and JavaFX.
    Openjdk and OpenJFX are NOT available for Windows, so use Oracle versions.

##### Oracle Java 8 jdk (includes JavaFX) #####
Download and install Oracle Java SE 8 Development Kit from
http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html.

**Note** You can download a package which includes both the netbeans IDE and
the jdk from http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html.

#### SceneBuilder ####
SceneBuilder is the gui tool for modifying the user interface, details
of which are held in IngAusOfxFix.fxml.
You only need to install SceneBuilder if you wish to modify the user
interface.

SceneBuilder is no longer available from Oracle.

Download from http://gluonhq.com/open-source/scene-builder.

#### Netbeans IDE ####
If you haven't already installed netbeans as part of an Oracle combined
jdk and netbeans

Download and install from https://netbeans.org/downloads/.

#### To download the source files and netbeans project ####

**Note** There are 2 versions of IngAusOfxFix on github
```
https://github.com/goodvibes2/IngAusOfxFixWin
```
which is the project for Microsoft Windows using
Oracle Java 8, and netbeans IDE 8.0
    and
```
https://github.com/goodvibes2/IngAusOfxFixLinux
```
which is the project for GNU/Linux Ubuntu 16.04 using
Java OpenJDK 8, OpenJFX, and netbeans IDE 8.1.

The java source files in both the above projects should be identical
and the dist/IngAusOfxFix.jar files in both should work in both GNU/Linux
or Windows. The differences between these projects are only in the netbeans
project files used for building the project. This is so as to make it easy
to download (or clone) the project, set up the dependencies, and then be
able to open the project in netbeans, and be able to build it without any
further setup.

There are 2 main ways to download the IngAusOfxFix netbeans project from github

1) If you already have a github account and git installed, you can clone

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
OR

2) Open the required Linux or Windows URL from above in a web browser,
click on the green **Clone or download** button,
click on **Download ZIP**.
Extract all files from the zip, retaining directories, to
```
  C:\Users\[USER_NAME]\Documents\NetBeansProjects
```
I suggest after extracting, rename folder
```
  C:\Users\[USER_NAME]\Documents\NetBeansProjects\IngAusOfxFixWin-master
  to
  C:\Users\[USER_NAME]\Documents\NetBeansProjects\IngAusOfxFix
```
    
<a name="Supported Platforms"></a>
## Supported Platforms ##

IngAusOfxFix 1.1x is known to work with the following operating systems

- GNU/Linux             -- x86
- Windows               -- x86

IngAusOfxFix can probably be made to work on any platform for which GnuCash
does, so long as Java 8 (open or Oracle) and JavaFX or openJFX are available.

<a name="Known Issues"></a>
## Known Issues ##

1) Java 1.8.0_72 (8u72) or later is required due to bug
   https://bugs.openjdk.java.net/browse/JDK-8136838 as the value of
   ComboBox.getValue() was not correct in previous versions.

   As of 15 Jul 2016, the current Java version on Windows is 1.8.0_92 and
   on Ubuntu 16.04 is 1.8.0_91. 
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
