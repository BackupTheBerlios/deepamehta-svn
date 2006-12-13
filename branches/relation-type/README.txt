
DeepaMehta 2.0b7 
================

README

Joerg Richter
25.6.2006

www.deepamehta.de




 --- CONTENTS ---

  Requirements
  Installation
    - Step 1: Unzip
    - Step 2: Configure
    - Step 3: Install
  Running
    - Single-Place
    - Networked
    - Web Frontends
  Administration
    - Set the root password
    - Configure instances
    - Control the servlet engine
    - Reset the database
    - Update
    - Deinstall




Requirements
============

Software

  * Java 1.4 Standard Edition
      http://java.sun.com/
  * MySQL 4
      http://www.mysql.com/
  * Apache Ant 1.6
      http://ant.apache.org/
  * Optional: Apache Tomcat or another servlet engine
      http://tomcat.apache.org/

Hardware

  * CPU: 800 MHz / Memory: 256MB
  * Disk space: 12 MB + your data



Installation
============

The DeepaMehta distribution consists of the file deepamehta-2.0b7.zip

Follow the instructions of this section if no older version of DeepaMehta is installed on
your machine. If an older version of DeepaMehta is already installed, follow the
instructions of the section "Administration / Update".


Step 1: Unzip
-------------

1) Create the DeepaMehta home directory at a suitable location, e.g.

   * Windows:     C:\Applications\DeepaMehta-2.0b7
   * Mac OS X:    /Macintosh HD/Applications/DeepaMehta-2.0b7
   * Linux:       /usr/local/deepamehta-2.0b7

2) Unzip "deepamehta-2.0b7.zip" to a the just created directory.
   The structure of the DeepaMehta installation looks like this:

   README.txt     this text
   install/       the software to run DeepaMehta
   develop/       you can delete this folder if you do no DeepaMehta development
   libs/          3rd party libraries
   build.xml      buildfile for ant
   config.xml     configurations read by the buildfile


Step 2: Configure 
-----------------

1) At the very minimum configuration effort you must add the directory /<Ant>/bin to your
   environment, so you can start the ant tool from a console window.

   * Windows:            set the PATH environment variable
   * Linux / Mac OS X:   create a symbolic link in /usr/bin

   To test the ant installation type "ant -version". A short message about the ant version
   should appear.

2) Optional: change the default DeepaMehta configuration by editing the file "config.xml"
   with a text editor.

   a) You are strongly advised to change the password for the DeepaMehta database user by
      setting the value of the "db.password" property (section "Database").
      *** Retaining the default password is very insecure ***

         <!-- Database -->
         <property name="db.password" value="dm"/>

      If you change the password here, you must set it in install/client/dms.rc too.
      The password is part of the connection parameters of the "default" instance.

   b) If you want use the example web applications you must set the four "web.*"
      properties to point to your servlet engine installation

         <!-- Servlet Engine -->
         <property name="web.deploy.dir"  value="/usr/local/tomcat/webapps"/>
         <property name="web.lib.dir"     value="/usr/local/tomcat/shared/lib"/>
         <property name="web.classes.dir" value="/usr/local/tomcat/shared/classes"/>
         <property name="web.servlet.lib" value="/usr/local/tomcat/common/lib/servlet-api.jar"/>

   c) If you want deploy 3rd party DeepaMehta applications, e.g. "Kiezatlas" add them to
      the "dmapps" path element.

         <!-- DeepaMehta Applications -->
         <property name="appspath" location="/Users/fred/My DeepaMehta Applications"/>
         <path id="dmapps">
            ...
            <pathelement location="${appspath}/Kiez-Atlas/kiezatlas.jar"/>
         </path>


Step 3: Install
---------------

*** IMPORTANT ***
all ant calls must be performed from the DeepaMehta home directory.

=> Create the DeepaMehta database by executing the ant target "install".

    cd DeepaMehta
    ant install

You will be asked for the MySQL root user password. The DeepaMehta database and the
database user are created. 

Another database that is part of the "Movies" example application is also
created. Type your MySQL root user password again.

Finally you're asked weather to install the DeepaMehta example web applications.



Running
=======


Single-Place
------------

The monolithic DeepaMehta application is suitable for single-place environments.
Client and server are integrated into a single application and communicating via direct
method calls. No TCP port is opened.

=> Start the monolithic DeepaMehta application by executing the "dm" target.

    cd DeepaMehta
    ant dm

Login as "root", no password is required.

You can access a specific DeepaMehta instance by setting the "dm.instance" property at the
command line

    ant -Ddm.instance=test dm

or you can set the "dm.instance" property permanently (in config.xml). If no instance is
specified the "default" instance will be accessed. DeepaMehta instances are declared in
dms.rc (see the section "Administration / Configure instances").


Networked
---------

Client and server are communicating via TCP sockets.
The server opens one dedicated port (7557 by default).

=> Start the DeepaMehta server by executing the "dms" target.

    cd DeepaMehta
    ant dms

You can serve a specific DeepaMehta instance by setting the "dm.instance" property at the
command line

    ant -Ddm.instance=test dms

or you can set the "dm.instance" property permanently (in config.xml). If no instance is
specified the "default" instance will be served. DeepaMehta instances are declared in
dms.rc (see the section "Administration / Configure instances").

=> Start the DeepaMehta client application by executing the "dmc" target.

    cd DeepaMehta
    ant dmc

Login as "root", no password is required.

You can connect to a specific DeepaMehta server by setting the "dm.host" and "dm.port"
properties at the command line

    ant -Ddm.host=www.site.com -Ddm.port=7580 dmc

or you can set the properties permanently (in config.xml). If no host is specified
"localhost" will be used. If no port is specified the default port (7557) will be used.

=> Start the DeepaMehta client applet resp. the signed client applet by browsing to

    /<DeepaMehta>/install/client/index.html
    /<DeepaMehta>/install/client/plugin.html

In any case the client applet expects the DeepaMehta server to run at the host the applet
originates from. The port can be specified inside the HTML pages by the applet parameter
"PORT". If no port is specified the default port (7557) will be used.


Web Frontends
-------------

DeepaMehta includes 5 example web applications.

=> Start an example web application by browsing to the respective URL:

    http://localhost:8080/dm-browser/controller
    http://localhost:8080/dm-search/controller
    http://localhost:8080/messageboard/controller
    http://localhost:8080/musicforum/controller
    http://localhost:8080/kompetenzstern/controller

Note: the web applications are installed while the standard DeepaMehta installation
(see section "Installation" above).

If your servlet engine is already running it must be restarted. The servlet engine must
be running. (see section "Administration / Control the servlet engine" below)



Administration
==============


Set the root password
---------------------

1) Start the DeepaMehta client and login as "root", no password is required.
2) Select the "Administration" workspace
3) Open the "Users and Groups" view by double-clicking it
4) Select the "root" user
5) Type in the password in the appropriate field


Configure instances
-------------------

For different purposes you can define several DeepaMehta instances (e.g. one for "real"
content, another for testing while development). Every DeepaMehta instance is associated
with a specific corporate memory. Every corporate memory can be realized by a specific
DBMS. To configure DeepaMehta instances edit the file /<DeepaMehta>/install/client/dms.rc
with a text editor and restart the DeepaMehta server resp. the monolithic DeepaMehta
application resp. the servlet engine.

If no instance is specified the "default" is used. The default instance represents the
MySQL database as installed in section "Installation".


Control the servlet engine
--------------------------

* Linux / Mac OS X / Tomcat:

  The following commands are located in /<DeepaMehta>/install/server/bin/
  - start_tomcat    starts the Tomcat servlet engine
  - stop_tomcat     stops the Tomcat servlet engine
  - log_tomcat      starts Tomcat logging,
                    do this in a separate window and leave this window open all the time
* Windows:

  There are no scripts to control a servlet engine provided so far.

*** IMPORTANT ***
the servlet engine must be started from the directory /<DeepaMehta>/install/client/
otherwise the DeepaMehta servlet can't find the required files.


Reset the database
------------------

The "reset" target resets the DeepaMehta database to its initial content.

    cd DeepaMehta
    ant reset

To reset a specific DeepaMehta instance you can set 3 properties at the command line

    ant -Ddb.name=DeepaMehtaTest -Ddb.userid=dmtest -Ddb.password=dmtest reset

or you can set the 3 properties permanently (in config.xml).
If no instance is specified the "default" instance will be reset.


Update
------

1) Replace the DeepaMehta installation directory /<DeepaMehta>/

2) Install all the patches, starting from the
   version of your current DeepaMehta installation.

       Your version          install these patches to update to 2.0b7
       -------------------------------------------------------------------
       2.0a18                cm-2.8.sql, music-2.8.sql
       2.0b1                 cm-2.9.sql
       2.0b2                 cm-2.10.sql, movies-2.10.sql, ks-2.10.sql, artfacts-2.10.sql
       2.0b3-pre1            cm-2.11.sql
       2.0b3-pre2            cm-2.12.sql
       2.0b3                 cm-2.13.sql
       2.0b4                 cm-2.14.sql
       2.0b5                 cm-2.15.sql
       2.0b6                 cm-2.16.sql

   A patch is installed by executing "patchdb" target and setting the "patch" property
   at the command line.

       ant -Dpatch=install/db/patches/cm-2.16.sql patchdb

   To patch a specific DeepaMehta instance you can set 3 properties at the command line

       ant -Dpatch=install/db/patches/cm-2.16.sql -Ddb.name=DeepaMehtaTest -Ddb.userid=dmtest -Ddb.password=dmtest patchdb


Deinstall
---------

=> Delete the DeepaMehta database and user by executing the "dropdb" target.

    cd DeepaMehta
    ant dropdb

The DeepaMehta installation directory and the deployed web applications must be removed
manually.
