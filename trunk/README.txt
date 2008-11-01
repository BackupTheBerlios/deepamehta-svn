
DeepaMehta 2.0b8
================


R E A D M E


--- CONTENTS ---

Requirements
Brief instruction
  - Windows set up
  - Linux set up
  - Mac OS X set up
Installation
  - Step 1: Unzip
  - Step 2: Install
  - Step 3: Deployment of web frontends
  - Update an existing installation
  - Additional installation of example applications
Starting & Quitting
  - Single-place
  - Networked
  - Web frontends
Administration
  - Set the root password
  - Configure additional instances
  - Determine standard instance
  - Delete an instance
  - Control Tomcat
  - Reset the database
  - Uninstall
What's next?



Requirements
============

Software (essential)

    => Java Standard Edition, Runtime Environment (JRE, Version 1.4 or 5 or 6)
       http://www.java.com/

Software (optional)

    If you plan to use the DeepaMehta web frontends,
    the following software needs to be installed separatly:

    => Java Standard Edition, Development Kit (JDK, Version 1.4 oder 5 oder 6)
       http://java.sun.com/

    => MySQL (Version 4 or 5)
       http://www.mysql.com/

    => Tomcat (Version >=4)
       http://tomcat.apache.org/

Hardware (minimum)

    => CPU: 800 MHz
    => Memory: 256 MB
    => Disk space: 20 MB free + your data entered via DeepaMehta



Brief instruction
=================

* Windows set up
* Linux set up
* Mac OS X set up

This brief instruction contains all neccessary information to install DeepaMehta quickly and easily. After unzipping DeepaMehta can be started instantly by double-clicking. No command line entries are nessessary. At initial startup a minimum standard installation will be executed automatically.

If you want to install any of the examples or web frontends, please configure the installation manually (terminal-based) as described in the following section "Installation". You can as well perform the automatic installation first and supplement it by the example applications and web frontends afterwards.


Installation on Windows PC
--------------------------

1) Unzip: "deepamehta-2.0b8.zip"

    Hint: Use an application for unzipping, e.g. WinRAR,
    choose e.g. C:\Applications\ as destination for the directory "deepamehta" which will be created.
    

2) Starting: double-click the "run"-file

    Hint: Use your Windows Explorer to view the "deepamehta"-directory.
    The "run"-file will possibly be shown as "run.bat".

    Automatically a standard installation will be performed and DeepaMehta will be startet.
    As soon as you see the login-dialog, enter "root" and hit "Return" 2 times.
    No password is required. The DeepaMehta desktop will appear.

3) Quit: Close the DeepaMehta window.

    To start Deepemehta again double-click the "run"-file.

4) What's next?

    Sources for documentation or support can be found at the end of this README-file.


Installation on Linux PC
------------------------

1) Unzip:

    unzip "deepamehta-2.0b8.zip"

    You can install DeepaMehta e.g. in your Home-directory.
    A "deepamehta"-directory will be created during unzipping.

2) Starting:

    cd deepamehta
    ./run.sh

    Automatically a standard installation will be performed and DeepaMehta will be startet.
    As soon as you see the login-dialog, enter "root" and hit "Return" 2 times.
    No password is required. The DeepaMehta desktop will appear.

3) Quit: Close the DeepaMehta window.

    To start DeepaMehta again enter "./run.sh" from within your "deepamehta"-directory.

4) What's next?

    Sources for documentation or support can be found at the end of this README-file.


Installation on Mac OS X
------------------------

1) Unzip:

    Copy the "deepamehta-2.0b8.zip"-file into your Applications-folder and double-click it
    to unzip it. A "deepamehta"-directory will be created during unzipping.

2) Starting:

    Open the "deepamehta"-directory and double-click the file "run.command".

    Automatically a standard installation will be performed and DeepaMehta will be startet.
    As soon as you see the login-dialog, enter "root" and hit "Return" 2 times.
    The DeepaMehta desktop will occur.

3) Quit: Close the DeepaMehta window.

    To start Deepemehta again double-click the "run.command"-file again.

4) What's next?

    Sources for documentation or support can be found at the end of this README-file.



Installation
============

* Step 1: Unzip
* Step 2: Install
* Step 3: Deployment of the web frontends
* Update an existing installation
* Additional installation of the example applications

Most of the processes that are dealt with in this and the following chapters can be executed by entering a "run"-command in your terminal window.

IMPORTANT: the "run"-command must be executed from within your DeepaMehta-home directory. The DeepaMehta-home directory is the directory "deepamehta", which will be created upon unzipping. Use the "cd"-command to change into the DeepaMehta-home directory.

IMPORTANT for Windows-users:

=> whenever you are asked to enter "./run.sh" Windows-users only have to enter "run".

Hint: To open a command-line window choose "Execute..." from the "Start"-menue, enter "cmd" and press "OK".


Step 1: Unzip
-------------

The DeepaMehta distribution consists of the file "deepamehta-2.0b8.zip".
Unzip it into your applications directory, e.g.:

    * Windows:     C:\Applications\
    * Linux:       /home/you/
    * Mac OS X:    /Macintosh HD/Applications/

During unzipping the directory "deepamehta" will be created.


Step 2: Installation
--------------------

To start the DeepaMehta installation enter the following command:

    ./run.sh install

First of all the DeepaMehta installation will be set up, in respect of 4 aspects:
- Will the DeepaMehta web frontends be needed? (Tomcat required)
- Which database shall be used by DeepaMehta? (the provided HSQL or MySQL)?
- Which network port shall be accepted by the DeepaMehta server for client connections?
- Which example applications shall be installed?

Therfore a few questions will be asked. The standard answers are provided in squared brackets and can be chosen by hitting the "Return" key.


=> Web frontends

First of all you will be asked, if you want to use the DeepaMehta web frontends. If "Yes" is your answer, you must state where your Tomcat home-directory is located at.

    [input] Do you want to install the web frontends (Tomcat must already be installed)? (y, [n])

    [input] Please enter the home directory of your Tomcat installation. [/usr/local/tomcat]

=> Database

Then you will be asked into which database the DeepaMehta application shall put your data. If the provided HSQL-database shall be used, simply hit "Return".

    [echo] Please select the DeepaMehta instance to be configured:
    [echo]
    [echo] * hsqldb-intern (Recommended for just using DeepaMehta.)
    [echo] * mysql4 (Required for use with web frontends. MySQL 4 must already be installed.)
    [echo] * mysql5 (Required for use with web frontends. MySQL 5 must already be installed.)
    [echo]
    [input] Currently set [hsqldb-intern]

IMPORTANT: If the DeepaMehta web frontends and the graphical DeepaMehta user interface shall be used simultaneously on your computer, MySQL must be used as a database. MySQL is not provided with DeepaMehta and must be installed separately.

Now you will be asked for the name of the database which is going to be created. Enter your desired name for the database or simply hit "Return".

    [input] Please enter the name of the database to be created: [DeepaMehta]

=> Network port

Then you will be asked for the network port which shall be used by the DeepaMehta server to accept client connections. This setting is important for client-server operation (see "Networked" in section "Starting & Quitting"), especially if several DeepaMehta instances are in use (see "Setting up additional instances" in section "Administration"). At the moment simply hit "Return".

    [input] Network port for this instance (when served by the DeepaMehta server): [7557]

=> Example applications

Now you will be asked which of the provided example applications you would like to install. If you do not want to deal with the example applications at the moment, simply hit "Return". DeepaMehta is fully functionable without all of the example applications. They can be installed later as described in section "Additional installation of example applications".

    [input] Do you want to install the example application 'kompetenzstern'
            (Balanced Scorecard editor and report generator)? (y, [n])
    [input] Do you want to install the example application 'messageboard'
            (Graphical forum application and web frontend)? (y, [n])
    [input] Do you want to install the example application 'ldap'
            (LDAP-Client for browsing users and groups)? (y, [n])
    [input] Do you want to install the example application 'movies'
            (Demonstration of accessing external datasources)? (y, [n])

If you are about to install the "movies" example now, you will be asked for the kind of database you want to use for it.

    [echo] Please select the datasource to be used for the 'movies' example:
    [echo]
    [echo] * hsqldb-intern
    [echo] * mysql4
    [echo] * mysql5
    [echo] * xml
    [echo]
    [input] currently set (default) [hsqldb-intern]

If you selected "hsqldb-intern" you will be asked to enter a name for your movies database now.

    [input] Please enter the name of your database: [Movies]

If, at the beginning, you decided that you want to use the DeepaMehta web frontends as well (with Tomcat), you will now be asked, which of the provided web frontends you would like to install:

    [input] Do you want to install the example application 'dm-browser'
            (Generic web frontend demo 1)? (y, [n])
    [input] Do you want to install the example application 'dm-search'
            (Generic web frontend demo 2)? (y, [n])
    [input] Do you want to install the example application 'dm-topicmapviewer'
            (Generic web based topicmap viewer)? (y, [n])
    [input] Do you want to install the example application 'dm-web'
            (Generic web frontend demo 3, recommendend)? (y, [n])

At this point the configuration of the DeepaMehta installation is completed.

=> Installation

You will be asked if you wish to start the installation now. During the installation the DeepaMehta database will be built and the chosen example applications will be stored in it. To continue with the installation simply hit "Return".

    [input] Do you want to initialize now? ([y], n)

Once again your database settings will be shown. As soon as you hit "Return" the DeepaMehta database will be created and the chosen content will be stored in it. 

    [echo] --- DeepaMehta Installation ---
    [echo] You are about to create a database 'DeepaMehta' and a database user 'sa' (password '').
    ...
    [input] Continue?  ([y], n)

If you have chosen the Movies example application in the configuration process and "hsqldb-intern" as your data source, the Movies example database will now be created.

    [echo] --- DeepaMehta Installation ---
    [echo] You are about to create a database 'Movies' and an
    [echo] user 'sa' (password '') with corresponding grants.
    ...
    [input] Continue?  ([y], n)

If the installation was successful, "BUILD SUCCESSFUL" will be displayed.


Step 3: Deployment of the web frontends
---------------------------------------

If you decided to use the DeepaMehta web frontends in the configuration process they will be deployed now under Tomcat. To start deployment please enter the following command:

    ./run.sh deploy

If the deployment was successful, "BUILD SUCCESSFUL" will be displayed.

IMPORTANT: if Tomcat was already running during the deployment process, Tomcat needs to be restarted now (even if Tomcats hot-deployment function is activated).


Update an existing installation
-------------------------------

To update an existing older version of Deepamehta to version 2.0b8 without loosing any of the already stored data please proceed with the following 3 steps:

=> Updating of the files

Unzip "DeepaMehta 2.0b8.zip", as described above in section "Installation", "Step 1: Unzip". Be carefull not to overwrite your existing DeepaMehta installation!

If you already have custom icons, topicmap background images or documents embedded in DeepaMehta, you need to copy them into the corresponding directories of your new DeepaMehta 2.0b8 installation:

    deepamehta/install/client/icons/
    deepamehta/install/client/backgrounds/
    deepamehta/install/client/documents/

=> Configuration of the database

Now your old database needs to be introduced to the new DeepaMehta 2.0b8. Therefor enter the following command:

    ./run.sh config

You will be asked a few questions. The following procedure is about the same as the one described in the above section "Installation" in "Step 2: Install". The question about the web frontends can simply be answered by hitting "Return" at this point.

When asked for the type of database enter "mysql4" or "mysql5", depending on the version of your MySQL installation.

    [echo] Please select the DeepaMehta instance to be configured:
    [echo] 
    [echo] * hsqldb-intern (Recommended for just using DeepaMehta.)
    [echo] * mysql4 (Required for use with web frontends. MySQL 4 must already be installed.)
    [echo] * mysql5 (Required for use with web frontends. MySQL 5 must already be installed.)
    [echo] 
    [input] Currently set [hsqldb-intern]

Then you will be asked for the database host, the database root-user password, the database name, the name of the database user as well as the database user password. If, at that time, you made a DeepaMehta standard installation and did not assign a root-user password to your MySQL-Installation simply answer all questions with "Return".

    [input] Please enter the host of your database: [127.0.0.1]

    [extendedinput] Please enter the database root password:

    [input] Please enter the name of the database to be created: [DeepaMehta]
    
    [input] Please enter the user to use: [dm]

    [input] Please enter the password to use: [dm]

Now questions about the network port and the example applications will be asked, which again can be answered by hitting "Return" at this point.

As soon as the configuration is finished, "BUILD SUCCESSFUL" will be displayed.

=> Update the database

Now patches need to be installed to update your current version of DeepaMehta to DeepaMehta 2.0b8. The following table shows you which are the appropriate patches to update your version of DeepaMehta. Apply all patches, beginning with the one for your current DeepaMehta version.

    Your current          Use these Patches to update to
    DeepaMehta version    DeepaMehta 2.0b8
    ----------------------------------------------------
    2.0b3                 cm-2.13.sql
    2.0b4                 cm-2.14.sql
    2.0b5                 cm-2.15.sql
    2.0b6                 cm-2.16.sql
    2.0b7                 cm-2.17.sql
    2.0b8-rc4             cm-2.18.sql
    rev346-20080910       cm-2.19.sql

If your currently installed version is e.g. DeepaMehta 2.0b7, you have to apply the patches 2.17, 2.18, and 2.19 (in the correct order).

To input the data of a single patch enter the following command (the patch "cm-2.17.sql" will be used here):

    ./run.sh patchdb -Dpatch=install/db/patches/cm-2.17.sql

For security reasons you will be asked if you really want to apply that spezific patch, and the concerned database will be displayed. To apply the chosen patch enter 'y' and press "Return".

    [echo] You are about to apply the patch install/db/patches/cm-2.17.sql to the database DeepaMehta (user 'dm', password 'dm').
    ...
    [input] Continue?  (y, n)

As soon as the patch is applied "BUILD SUCCESSFUL" will be displayed. Repeat the command to apply additional patches.


Additional installation of example applications
-----------------------------------------------

To install additional example applications enter the following command:

    ./run.sh install

You will be asked the questions which are described above in "Step 2: Install". Choose the example applications you would like to install.

If you are asked "Do you want to initialize now?" answer with 'y' or simply hit "Return".

    [input] Do you want to initialize now? ([y], n)

When you are asked, if the database shall be created now, answer with 'n' or simply hit "Return".

    [echo] Initialize the DeepaMehta instance 'hsqldb-intern' now...
    ...
    [echo] --- DeepaMehta Installation ---
    [echo] You are about to create a database 'DeepaMehta' and a database user 'sa' (password '').
    ...
    [input] Continue?  (y, [n])

When the example applications have been installed successfully, "BUILD SUCCESSFUL" will be displayed.



Starting & Quitting
===================

* Single-place
* Networked
* Web frontends


Single-place
------------

The easiest way to start DeepaMehta is as a single-place application. The single-place application is fully sufficient if you do not plan to get connected to other DeepaMehta users. (networked).

=> To start the single-place application enter the following command:

    ./run.sh

The DeepaMehta login dialog will be displayed. Enter "root" and hit "Return" 2 times (initially the DeepaMehta root-user does not have a password). The DeepaMehta desktop will occur.

To start a specific DeepaMehta instance set the "dm.instance" property with -D at the command line:

    ./run.sh -Ddm.instance=myinstance

Informations about DeepaMehta instances you will find in section "Administration", "Configure additional instances" as well as in "Define standard instance".

=> To quit DeepaMehta simply close the DeepaMehta window.

Technical security advice: the monolithic DeepaMehta application has the DeepaMehta client and the server integrated into one single application, where the communication takes place by direct method calls. No network port is needed.


Networked
---------

The DeepaMehta client/server application enables different users to work together over a network.

=> To start the DeepaMehta server enter the following command:

    ./run.sh dms

To serve a specific DeepaMehta instance set the "dm.instance" properties to the desired instance at he command line:

    ./run.sh dms -Ddm.instance=myinstance

Informations about DeepaMehta instances can be found in section "Administration", "Configure additional instances" and "Determine standard instance".

=> To start the DeepaMehta client application and get connected to a local DeepaMehta server enter the following command:

    ./run.sh dmc

To connect to a specific DeepaMehta server set the "dm.host" and "dm.port" properties at the command line (If no host is specified "localhost" will be used. If no port is specified the default port (7557) will be used):

    ./run.sh dmc -Ddm.host=www.site.com -Ddm.port=7558

The DeepaMehta login dialog will appear. Enter "root" and hit "Return" 2 times (the DeepaMehta root user does not have a password yet). The DeepaMehta desktop will appear.

=> To start the DeepaMehta client applets resp. the signed client applets open the corresponding website in your web browser:

    .../deepamehta/install/client/start.html
    .../deepamehta/install/client/start-signed.html

The client applet expects the DeepaMehta server to run on the same machine as the one the applet was loaded from. The port used by the client applet to contact the server can be edited on the HTML-sites by entering its value in the applet-parameter "PORT". eingestellt werden. If no "Port"-parameter is available, the default port (7557) will be used.

Technical security advice: The DeepaMehta clients communicate with the DeepaMehta server via TCP sockets. The DeepaMehta server opens a dedicated TCP-port (it is port 7557 by default).


Web frontends
-------------

6 different web frontends are provided with DeepaMehta. To start a web frontend enter the corresponding URL into your web browser:

    http://localhost:8080/kompetenzstern/controller
    http://localhost:8080/messageboard/controller
    http://localhost:8080/dm-browser/controller
    http://localhost:8080/dm-search/controller
    http://localhost:8080/dm-topicmapviewer/controller
    http://localhost:8080/dm-web/controller

The web frontends can only be started if DeepaMehta has been configured correspondingly (see Section "Installation", "Step 2: Installation") and the web frontends have been deployed (see Section "Installation", "Step 3: Deployment of web frontends"). Tomcat needs to be started already.



Administration
==============

* Set the root password
* Configure additional instances
* Determine standard instance
* Delete an instance
* Controll Tomcat
* Reset the database
* Uninstall DeepaMehta


Set the root password
---------------------

1) Start DeepaMehta (Single-place or networked) and log in as "root".
2) Choose the workspace "Administration" from the pulldown menue.
3) Open the topicmap "Users and Groups" with a double-click.
4) Click on the user "root".
5) Enter your desired password on the right side into the "Password" field.


Setting up additional instances
-------------------------------

For some specific needs independent DeepaMehta instances can be created (e.g. one with "real" content and one for testing during development). Each DeepaMehta instance has a separate corporate memory (content-memory). For each corporate memory an individual database system (e.g. HSQL or MySQL) can be used.

To create a new DeepaMehta instance please enter the following command: 

    ./run.sh newinstance

First of all you will be asked to set the configuration for the new instance. If e.g. for the new instance a HSQL-database shall be used, choose "hsqldb-intern".

    [echo] Please select the instance configuration the new instance is based on:
    [echo] 
    [echo] * hsqldb-intern
    [echo] * mysql4
    [echo] * mysql5
    [echo] 
    [input] Instance name: [hsqldb-intern]

Then you will be asked to name the new instance. A good advice is to choose a name that speaks for itself, e.g. "production" or "test".

    [input] Please enter the new instance name: [hsqldb-intern2]

Then the new instance will be configured and installed, like it is explained in the above section "Installation", "Step 2: Install". When you are asked for the name of the database that is going to be created, all existing databases will be listet. Enter a database name, that is not in the list yet.

    [input] Please enter the name of your database: [DeepaMehta]

Simply answer all other questions (concerning the web frontends and the example applications) with "Return", whereby the settings made so far will be transfered to the new instance (Advice: these settings are not stored per single DeepaMehta instance but are stored globally).

Now you will be asked if the new instance shall be installed now. Answer with yes by hitting "Return".

    [input] Do you want to initialize now? ([y], n)

If the instance was set up succesfully "BUILD SUCCESSFUL" will be displayed. Zum Starten einer bestimmten DeepaMehta-Instanz siehe oben den Abschnitt "Starten & Beenden" und den folgenden Punkt "Standard-Instanz festlegen".


Determine a standard instance
-----------------------------

A specific instance can be defined as standard instance. The standard instance is used if DeepaMehta gets started without stating an instance.

To determine an instance enter the following command:

    ./run.sh switchinstance

The names of all existing instances will be listed and the current standard instance will be displayed. Enter the name of the future standard instance and hit "Return".

    [echo] Please select the DeepaMehta instance to activate:
    [echo] 
    [echo] * hsqldb-intern
    [echo] * myinstance
    [echo] 
    [input] Currently set [hsqldb-intern]


Delete an instance
------------------

To delete an instance enter the following command, by setting the "dm.instance" properties at the command line with -D:

    ./run.sh dropdb -Ddm.instance=myinstance

For security reasons you will be asked if you really want to delete that instance. Enter 'y' and hit "Return" to delete that instance.

    [echo] Uninstalling the DeepaMehta instance 'myinstance' now...
    ...
    [echo] You are about to delete the database 'DeepaMehta'.
    ...
    [input] Continue?  (y, n)


Control Tomcat
--------------

=> To start Tomcat enter the following command:

    ./run.sh tomcat-start

IMPORTANT: Tomcat must be started from the directory deepamehta/install/client/, otherwise the web frontends will not find neccessary files. Please use the command stated above to start Tomcat, and not the mechanisms of your system environment (e.g. /etc/init.d under Linux).

=> To stop Tomcat enter the following command:

    ./run.sh tomcat-stop

=> To view the Tomcat-diagnostics enter the following command:

    ./run.sh tomcat-log

Hint: the Tomcat-diagnostics can be read best if you enter the command in a separate terminal window, which should remain open during the whole session.


Reset the database
------------------


Uninstall DeepaMehta
--------------------



What's next?
============

Hints for your first steps:
http://www.deepamehta.de/wiki/en/Quickstart

DeepaMehta User Guide:
http://www.deepamehta.de/wiki/en/User_Guide

For all of your questions please use the forum on the DeepaMehta website or subscribe to the deepamehta-users mailing list. In the forum you can find important hints for using DeepaMehta already. Forum and mailing lists can be found at "Community" on www.deepamehta.de.



------------------------------------------------------------------------------------------------------
Jörg Richter                                                                         www.deepamehta.de
Nov 1, 2008
