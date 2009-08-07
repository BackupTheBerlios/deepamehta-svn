
DeepaMehta 2.0b8
================


R E A D M E


--- Inhalt ---

Voraussetzungen
Kurzanleitung
  - Inbetriebnahme unter Windows
  - Inbetriebnahme unter Linux
  - Inbetriebnahme unter Mac OS X
Installation
  - Schritt 1: Auspacken
  - Schritt 2: Installieren
  - Schritt 3: Deployen der Webfrontends
  - Vorhandene Installation updaten
  - Nachträgliches Installieren der Beispielanwendungen
Starten & Beenden
  - Einzelplatz-Anwendung
  - Client/Server-Anwendung
  - Webfrontends
Administration
  - Setzen des root Passworts
  - Weitere Instanzen einrichten
  - Standard-Instanz festlegen
  - Instanz löschen
  - Kontrollieren von Tomcat
  - Die Datenbank zurücksetzen
  - DeepaMehta Deinstallieren
Wie geht's weiter?



Voraussetzungen
===============

Software (notwendig)

    => Java Standard Edition, Runtime Environment (JRE, Versionen 1.4 oder 5 oder 6)
       http://www.java.com/

Software (optional)

    Wenn die DeepaMehta Webfrontends benutzt werden sollen,
    muß folgende Software separat installiert werden:

    => Java Standard Edition, Development Kit (JDK, Versionen 1.4 oder 5 oder 6)
       http://java.sun.com/

    => MySQL (Version 4 oder 5)
       http://www.mysql.com/

    => Tomcat (ab Version 4)
       http://tomcat.apache.org/

Hardware (minimal)

    => Prozessor: 800 MHz
    => Arbeitsspeicher: 256 MB
    => Festplatte: 20 MB frei + Platz für Deine DeepaMehta Inhalte



Kurzanleitung
=============

* Inbetriebnahme unter Windows
* Inbetriebnahme unter Linux
* Inbetriebnahme unter Mac OS X

Diese Kurzanleitung enthält alle Informationen, die benötigt werden, um DeepaMehta so schnell und einfach wie möglich in Betrieb zu nehmen. Nach dem Auspacken kann DeepaMehta sofort per Doppelklick gestartet werden. Beim ersten Programmstart wird automatisch eine minimale Standardinstallation ausgeführt. Es sind keine Terminal-Eingaben notwendig.

Wenn Beispielanwendungen oder Webfrontends installiert werden sollen, führe eine manuelle (Terminal-basierte) Installation durch, die im anschließenden Abschnitt "Installation" beschrieben ist. Es ist auch möglich, zunächst die automatische Installation durchzuführen, und die Beispielanwendungen und Webfrontends nachträglich zu installieren.


Inbetriebnahme unter Windows
----------------------------

1) Auspacken: deepamehta-2.0b8.zip

    Hinweis: Benutze zum Auspacken einen Entpacker, z.B. WinRAR.
    Als Zielort wähle z.B. C:\Programme\.
    
    Beim Auspacken wird am Zielort ein Verzeichnis "deepamehta" angelegt.

2) Starten: doppelklicke die "run"-Datei

    Hinweis: Benutze den Windows Explorer um das "deepamehta"-Verzeichnis anzuzeigen.
    Die "run"-Datei erscheint möglicherweise als "run.bat".

    Es wird eine automatische Standardinstallation durchgeführt und DeepaMehta dann gestartet.
    Sobald der Login-Dialog erscheint, gebe "root" ein und drücke 2x Return.
    Der DeepaMehta Desktop erscheint.

3) Beenden: Schließe das DeepaMehta-Fenster.

    Zum erneuten Starten doppelklicke wieder die "run"-Datei.

4) Wie geht's weiter?

    Dokumentations- und Support-Quellen sind am Ende dieses READMEs genannt.


Inbetriebnahme unter Linux
--------------------------

1) Auspacken:

    unzip deepamehta-2.0b8.zip

    DeepaMehta kann z.B. in Deinem Homedirectory installiert werden.
    Beim Auspacken wird ein Verzeichnis "deepamehta" angelegt.

2) Starten:

    cd deepamehta
    ./run.sh

    Es wird eine automatische Standardinstallation durchgeführt und DeepaMehta dann gestartet.
    Sobald der Login-Dialog erscheint, gebe "root" ein und drücke 2x Return.
    Der DeepaMehta Desktop erscheint.

3) Beenden: Schließe das DeepaMehta-Fenster.

    Zum erneuten Starten gebe wieder ./run.sh ein während Du im DeepaMehta-Verzeichnis stehst.

4) Wie geht's weiter?

    Dokumentations- und Support-Quellen sind am Ende dieses READMEs genannt.


Inbetriebnahme unter Mac OS X
-----------------------------

1) Auspacken:

    Kopiere die Datei deepamehta-2.0b8.zip in Deinen Anwendungs-Ordner und doppelklicke sie
    um sie auszupacken. Beim Auspacken wird ein Ordner "deepamehta" angelegt.

2) Starten:

    Öffne den "deepamehta" Ordner und doppelklicke die Datei "run.command".

    Es wird eine automatische Standardinstallation durchgeführt und DeepaMehta dann gestartet.
    Sobald der Login-Dialog erscheint, gebe "root" ein und drücke 2x Return.
    Der DeepaMehta Desktop erscheint.

3) Beenden: Schließe das DeepaMehta-Fenster.

    Zum erneuten Starten doppelklicke wieder die Datei "run.command".

4) Wie geht's weiter?

    Dokumentations- und Support-Quellen sind am Ende dieses READMEs genannt.



Installation
============

* Schritt 1: Auspacken
* Schritt 2: Installieren
* Schritt 3: Deployen der Webfrontends
* Vorhandene Installation updaten
* Nachträgliches Installieren der Beispielanwendungen

Die meisten Vorgänge, die in diesem und den folgenden Abschnitten behandelt werden, werden durch Eingabe des "run"-Kommandos von einem Terminal-Fenster aus durchgeführt.

WICHTIG: das "run"-Kommando muß vom DeepaMehta-Homeverzeichnis aus ausgeführt werden. Das DeepaMehta-Homeverzeichnis ist das Verzeichnis "deepamehta", das beim Auspacken angelegt wird. Benutze das "cd"-Kommando um in dieses Verzeichnis zu wechseln.

WICHTIG für Windows-Anwender:

=> immer, wenn in dieser Anleitung die Eingabe von "./run.sh" erwähnt wird, müssen Windows-Anwender lediglich "run" eingeben.

Hinweis: Zum Öffnen eines Terminal-Fensters ("Eingabeaufforderung") wähle "Ausführen..." aus dem "Start"-Menu, gebe "cmd" in das Eingabefeld ein und drücke dann "OK".


Schritt 1: Auspacken
--------------------

Die DeepaMehta-Distribution besteht aus der Datei deepamehta-2.0b8.zip
Packe diese Datei in Deinem Verzeichnis für Anwendungen aus, z.B.:

    * Windows:     C:\Programme\
    * Linux:       /home/you/
    * Mac OS X:    /Macintosh HD/Applications/

Beim Auspacken wird ein Verzeichnis "deepamehta" angelegt.


Schritt 2: Installieren
-----------------------

Zum Starten der DeepaMehta-Installation gebe folgendes Kommando ein:

    ./run.sh install

Zunächst wird die DeepaMehta-Installation konfiguriert, hinsichtlich 4 Aspekten:
- Sollen die DeepaMehta Webfrontends benutzt werden (erfordert Tomcat)?
- Welche Datenbank soll DeepaMehta benutzen (das mitgelieferte HSQL oder MySQL)?
- Auf welchem Netzwerkport soll der DeepaMehta-Server Client-Verbindungen annehmen?
- Welche Beispielanwendungen sollen installiert werden?

Dazu werden ein paar Fragen gestellt, wobei die Standard-Antwort, die einfach durch Drücken von Return ausgelöst wird, in eckigen Klammern angegeben ist.

=> Webfrontends

Als erstes wirst Du gefragt, ob Du auch die DeepaMehta Webfrontends benutzen möchtest. Wenn Ja, mußt Du angeben, wo das Tomcat Home-Verzeichnis ist.

    [input] Do you want to install the web frontends (Tomcat must already be installed)? (y, [n])

    [input] Please enter the home directory of your Tomcat installation. [/usr/local/tomcat]

=> Datenbank

Dann wirst Du gefragt, in welcher Datenbank DeepaMehta seine Daten ablegen soll. Wenn die mitgelieferte HSQL-Datenbank benutzt werden soll, drücke einfach Return.

    [echo] Please select the DeepaMehta instance to be configured:
    [echo]
    [echo] * hsqldb-intern (Recommended for just using DeepaMehta.)
    [echo] * mysql4 (Required for use with web frontends. MySQL 4 must already be installed.)
    [echo] * mysql5 (Required for use with web frontends. MySQL 5 must already be installed.)
    [echo]
    [input] Currently set [hsqldb-intern]

WICHTIG: Wenn DeepaMehta Webfrontends und die grafische DeepaMehta-Oberfläche gleichzeitig auf einer Maschine benutzt werden sollen, muß als Datenbank MySQL benutzt werden. MySQL wird nicht mit DeepaMehta mitgeliefert und muß separat installiert werden.

Dann wirst Du nach dem Namen der anzulegenden Datenbank gefragt. Gebe den Namen der anzulegenden Datenbank ein, oder drücke einfach Return.

    [input] Please enter the name of the database to be created: [DeepaMehta]

=> Netzwerkport

Dann wirst Du nach dem Netzwerkport gefragt, auf dem der DeepaMehta-Server Client-Verbindungen annehmen soll. Diese Einstellung ist für den Client-Server-Betrieb relevant (siehe "Client/Server-Anwendung" im Abschnitt "Starten & Beenden"), besonders dann, wenn mehrere DeepaMehta-Instanzen im Einsatz sind (siehe "Weitere Instanzen einrichten" im Abschnitt "Administration"). Im Moment drücke einfach Return.

    [input] Network port for this instance (when served by the DeepaMehta server): [7557]

=> Beispielanwendungen

Dann wirst Du gefragt, welche der mitgelieferten Beispielanwendungen Du installieren möchtest. Wenn Du Dich jetzt nicht mit den Beispielanwendungen befassen möchtest, überspringe die Fragen mit Return. DeepaMehta ist auch ohne Beispielanwendungen voll nutzbar. Das spätere Installieren der Beispielanwendungen wird weiter unten in "Nachträgliches Installieren der Beispielanwendungen" beschrieben.

    [input] Do you want to install the example application 'kompetenzstern'
            (Balanced Scorecard editor and report generator)? (y, [n])
    [input] Do you want to install the example application 'messageboard'
            (Graphical forum application and web frontend)? (y, [n])
    [input] Do you want to install the example application 'ldap'
            (LDAP-Client for browsing users and groups)? (y, [n])
    [input] Do you want to install the example application 'movies'
            (Demonstration of accessing external datasources)? (y, [n])

Sofern Du das "movies" Beispiel installieren möchtest, wirst Du jetzt nach der Art der Datenquelle gefragt, die für das movies-Beispiel angelegt werden soll.

    [echo] Please select the datasource to be used for the 'movies' example:
    [echo]
    [echo] * hsqldb-intern
    [echo] * mysql4
    [echo] * mysql5
    [echo] * xml
    [echo]
    [input] currently set (default) [hsqldb-intern]

Sofern Du "hsqldb-intern" ausgewählt hast, wirst Du jetzt nach dem Namen der anzulegenden Beispiel-Film-Datenbank gefragt.

    [input] Please enter the name of your database: [Movies]

Sofern Du eingangs gesagt hast, daß Du auch die DeepaMehta Webfrontends benutzen möchtest (unter Tomcat), wirst Du jetzt gefragt, welche der mitgelieferten Webfrontends Du installieren möchtest:

    [input] Do you want to install the example application 'dm-browser'
            (Generic web frontend demo 1)? (y, [n])
    [input] Do you want to install the example application 'dm-search'
            (Generic web frontend demo 2)? (y, [n])
    [input] Do you want to install the example application 'dm-topicmapviewer'
            (Generic web based topicmap viewer)? (y, [n])
    [input] Do you want to install the example application 'dm-web'
            (Generic web frontend demo 3, recommendend)? (y, [n])

Damit ist die Konfiguration abgeschlossen.

=> Installation

Du wirst gefragt, ob die eigentliche Installation jetzt vorgenommen werden soll. Während der Installation wird die DeepaMehta-Datenbank angelegt und die ausgewählten Beispielanwendungen eingespielt. Um mit der Installation fortzufahren, drücke Return.

    [input] Do you want to initialize now? ([y], n)

Dir werden nochmal die konfigurierten Datenbank-Angaben gezeigt. Sobald Du Return drückst wird die DeepaMehta-Datenbank angelegt und mit den initialen Inhalten gefüllt.

    [echo] --- DeepaMehta Installation ---
    [echo] You are about to create a database 'DeepaMehta' and a database user 'sa' (password '').
    ...
    [input] Continue?  ([y], n)

Sofern Du beim Konfigurieren die Movies-Beispielanwendung ausgewählt hast, und "hsqldb-intern" als Datenquelle gewählt hast, wird jetzt die Movies-Beispiel-Datenbank angelegt.

    [echo] --- DeepaMehta Installation ---
    [echo] You are about to create a database 'Movies' and an
    [echo] user 'sa' (password '') with corresponding grants.
    ...
    [input] Continue?  ([y], n)

Wenn die Installation erfolgreich verlaufen ist, wird "BUILD SUCCESSFUL" angezeigt.


Schritt 3: Deployen der Webfrontends
------------------------------------

Wenn Du beim Konfigurieren gesagt hast, daß Du die DeepaMehta Webfrontends benutzen möchtest, werden diese jetzt unter Tomcat deployt. Zum Deployen gebe folgendes Kommando ein:

    ./run.sh deploy

Wenn die Web-Anwendungen erfolgreich deployt wurden, wird "BUILD SUCCESSFUL" angezeigt.

WICHTIG: wenn beim Deployen Tomcat bereits lief, muß Tomcat jetzt neugestartet werden (auch wenn Tomcats Hot-Deployment Funktion aktiviert ist).


Vorhandene Installation updaten
-------------------------------

Um eine ältere DeepaMehta Version auf DeepaMehta 2.0b8 zu aktualieren, und alle bereits eingegebenen Inhalte zu übernehmen, gehe in 3 Schritten vor:

=> Aktualisieren der Dateien

Packe DeepaMehta 2.0b8 aus, wie oben im Abschnitt "Instalation" in "Schritt 1: Auspacken" beschrieben ist. Achte darauf, daß die alte DeepaMehta Installation dabei nicht überschrieben wird.

Wenn Du bereits eigene Icons, Topicmap-Hintergründe oder Dokumente, in DeepaMehta eingebunden hast, kopiere diese aus den alten Verzeichnissen in die entsprechenden Verzeichnisse der neuen DeepaMehta 2.0b8 Installation:

    deepamehta/install/client/icons/
    deepamehta/install/client/backgrounds/
    deepamehta/install/client/documents/

=> Konfigurieren der Datenbank

Jetzt muß die alte Datenbank dem neuen DeepaMehta bekannt gemacht werden. Gebe dazu folgendes Kommando ein:

    ./run.sh config

Dir werden dann ein paar Fragen gestellt. Der Ablauf entspricht im Prinzip dem, der oben im Abschnitt "Instalation" im Punkt "Schritt 2: Installieren" beschrieben ist. Die Frage nach den Webfrontends kannst Du an dieser Stelle einfach mit Return beantworten.

Dann wirst Du nach dem Datenbanktyp gefragt. Gebe "mysql4" oder "mysql5" ein, je nach Version Deiner MySQL-Installation.

    [echo] Please select the DeepaMehta instance to be configured:
    [echo] 
    [echo] * hsqldb-intern (Recommended for just using DeepaMehta.)
    [echo] * mysql4 (Required for use with web frontends. MySQL 4 must already be installed.)
    [echo] * mysql5 (Required for use with web frontends. MySQL 5 must already be installed.)
    [echo] 
    [input] Currently set [hsqldb-intern]

Dann wirst Du nach dem Datenbank-Host, dem Passwort des Datenbank-Rootusers, dem Datenbank-Namen, dem Namen des Datenbank-Benutzers und dem Passwort des Datenbank-Benutzers gefragt. Wenn Du damals eine DeepaMehta Standardinstallation gemacht hast, und in Deiner MySQL-Installation für den Datenbank-Rootuser kein Passwort vergeben wurde, beantworte alle Fragen einfach mit Return.

    [input] Please enter the host of your database: [127.0.0.1]

    [extendedinput] Please enter the database root password:

    [input] Please enter the name of the database to be created: [DeepaMehta]
    
    [input] Please enter the user to use: [dm]

    [input] Please enter the password to use: [dm]

Dann werden Fragen nach dem Netzwerkport und den Beispielanwendungen gestellt, die Du an dieser Stelle einfach mit Return beantworten kannst.

Sobald die Konfiguration abgeschlossen ist, wird "BUILD SUCCESSFUL" angezeigt.

=> Aktualisieren der Datenbank

Jetzt müssen Patches in die alte Datenbank eingespielt werden, um sie auf den Stand von DeepaMehta 2.0b8 zu bringen. Die folgende Tabelle gibt Auskunft darüber, welche Patches eingespielt werden müssen, in Abhängigkeit Deiner alten DeepaMehta Version. Spiele alle Patches ein, beginnend von Deiner alten DeepaMehta Version.

    Deine alte            Spiele diese Patches ein, um auf
    DeepaMehta Version    DeepaMehta 2.0b8 zu aktualisieren
    -------------------------------------------------------
    2.0b3                 cm-2.13.sql
    2.0b4                 cm-2.14.sql
    2.0b5                 cm-2.15.sql
    2.0b6                 cm-2.16.sql
    2.0b7                 cm-2.17.sql
    2.0b8-rc4             cm-2.18.sql
    rev346-20080910       cm-2.19.sql

Wenn Du z.B. derzeit DeepaMehta 2.0b7 installiert hast, spiele die Patches 2.17, 2.18 und 2.19 ein (in dieser Reihenfolge).

Zum Einspielen eines einzelnen Patches gebe folgendes Kommando ein (hier wird der Patch "cm-2.17.sql" eingespielt):

    ./run.sh patchdb -Dpatch=install/db/patches/cm-2.17.sql

Zur Sicherheit wird dann nachgefragt, ob der Patch eingespielt werden soll, und es wird die betroffene Datenbank angezeigt. Um den Patch einzuspielen gebe 'y' ein und drücke Return.

    [echo] You are about to apply the patch install/db/patches/cm-2.17.sql the database DeepaMehta (user 'dm', password 'dm').
    ...
    [input] Continue?  (y, n)

Sobald der Patch eingespielt ist, wird "BUILD SUCCESSFUL" angezeigt. Wiederhole ggf. das Kommando, um weitere Patches einzuspielen.


Nachträgliches Installieren der Beispielanwendungen
---------------------------------------------------

Zum nachträglichen Installieren der Beispielanwendungen gebe folgendes Kommando ein:

    ./run.sh install

Es werden die Fragen gestellt, die oben in "Schritt 2: Installieren" beschrieben sind. Wähle die Beispielanwendungen aus, die Du installieren möchtest.

Wenn Du gefragt wirst "Do you want to initialize now?" anworte 'y' bzw. drücke einfach Return.

    [input] Do you want to initialize now? ([y], n)

Wenn Du gefragt wirst, ob die Datenbank jetzt angelegt werden soll, antworte 'n' bzw. drücke einfach Return.

    [echo] Initialize the DeepaMehta instance 'hsqldb-intern' now...
    ...
    [echo] --- DeepaMehta Installation ---
    [echo] You are about to create a database 'DeepaMehta' and a database user 'sa' (password '').
    ...
    [input] Continue?  (y, [n])

Wenn die Beispielanwendungen erfolgreich installiert wurden, wird "BUILD SUCCESSFUL" angezeigt.



Starten & Beenden
=================

* Einzelplatz-Anwendung
* Client/Server-Anwendung
* Webfrontends


Einzelplatz-Anwendung
---------------------

Die einfachste Art DeepaMehta zu starten ist die Einzelplatz-Anwendung. Diese ist ausreichend, wenn nicht mit anderen Nutzern gemeinsam gearbeitet werden soll (über das Netzwerk).

=> Zum Starten der Einzelplatz-Anwendung gebe folgendes Kommando ein:

    ./run.sh

Es erscheint der DeepaMehta-Login-Dialog. Gebe "root" ein und drücke 2x Return (initial hat der DeepaMehta-root-User kein Passwort). Der DeepaMehta Desktop erscheint.

Zum Starten einer bestimmten DeepaMehta-Instanz setze auf der Kommandozeile mittels -D die "dm.instance" Einstellung auf die gewünschte Instanz:

    ./run.sh -Ddm.instance=myinstance

Informationen über DeepaMehta-Instanzen findest Du unten im Abschnitt "Administration" unter "Weitere Instanzen einrichten" und "Standard-Instanz festlegen".

=> Zum Beenden der DeepaMehta-Sitzung schließe das DeepaMehta-Fenster.

Technischer Sicherheitshinweis: die monolithische DeepaMehta-Anwendung integriert den DeepaMehta-Client und -Server in eine einzige Anwendung, wobei über direkte Methodenaufrufe kommuniziert wird. Es wird kein Netzwerkport geöffnet.


Client/Server-Anwendung
-----------------------

Die DeepaMehta-Client/Server-Anwendung ermöglicht Nutzern über das Netzwerk gemeinsam zu arbeiten.

=> Zum Starten des DeepaMehta-Servers gebe folgendes Kommando ein:

    ./run.sh dms

Zum Servieren einer bestimmten DeepaMehta-Instanz setze auf der Kommandozeile mittels -D die "dm.instance" Einstellung auf die gewünschte Instanz:

    ./run.sh dms -Ddm.instance=myinstance

Informationen über DeepaMehta-Instanzen findest Du unten im Abschnitt "Administration" unter "Weitere Instanzen einrichten" und "Standard-Instanz festlegen".

=> Zum Starten der DeepaMehta-Client-Anwendung und Verbinden mit einem lokalen DeepaMehta-Server gebe folgendes Kommando ein:

    ./run.sh dmc

Zum Verbinden mit einem entfernten DeepaMehta-Server setze auf der Kommandozeile mittels -D die "dm.host" und "dm.port" Einstellungen (wenn kein Servername angegeben wird, wird "localhost" benutzt und wenn kein Port angegeben wird, wird der Standard-Port 7557 benutzt):

    ./run.sh dmc -Ddm.host=www.site.com -Ddm.port=7558

In beiden Fällen erscheint der DeepaMehta-Login-Dialog. Gebe "root" ein und drücke 2x Return (initial hat der DeepaMehta-root-User kein Passwort). Der DeepaMehta Desktop erscheint.

=> Zum Starten des DeepaMehta-Client-Applets resp. des signierten Client-Applets öffne die entsprechende Webseite in Deinem Webbrowser:

    .../deepamehta/install/client/start.html
    .../deepamehta/install/client/start-signed.html

Das Client-Applet erwartet, daß der DeepaMehta-Server auf der gleichen Maschine läuft, von der das Applet geladen wurde. Der Port, auf dem das Client-Applet versucht, den Server zu kontaktieren, kann in den HTML-Seiten durch Angabe des Applet-Parameters "PORT" eingestellt werden. Wenn kein "Port"-Parameter vorhanden ist, wird der Standard-Port (7557) benutzt.

Technischer Sicherheitshinweis: Die DeepaMehta-Clients kommunizieren mit dem DeepaMehta-Server über TCP Sockets. Der DeepaMehta-Server öffnet einen dedizierten TCP-Port (standardmäßig ist das Port 7557).


Webfrontends
------------

Mit DeepaMehta werden 6 Webfrontends mitgeliefert. Zum Starten eines Webfrontends gebe die entsprechende URL in Deinen Webbrowser ein:

    http://localhost:8080/kompetenzstern/controller
    http://localhost:8080/messageboard/controller
    http://localhost:8080/dm-browser/controller
    http://localhost:8080/dm-search/controller
    http://localhost:8080/dm-topicmapviewer/controller
    http://localhost:8080/dm-web/controller

Die Webfrontends können nur gestartet werden, wenn DeepaMehta entsprechend konfiguriert wurde (siehe Abschnitt "Installation" den Punkt "Schritt 2: Installieren") und die Webfrontends deployt wurden (siehe ebenddort den Punkt "Schritt 3: Deployen der Webfrontends"). Außerdem muß Tomcat gestartet sein.



Administration
==============

* Setzen des root Passworts
* Weitere Instanzen einrichten
* Standard-Instanz festlegen
* Instanz löschen
* Kontrollieren von Tomcat
* Die Datenbank zurücksetzen
* DeepaMehta Deinstallieren


Setzen des root Passworts
-------------------------

1) Starte DeepaMehta (Einzelplatz- oder Client/Server-Anwendung) und logge Dich als "root" ein.
2) Wähle den Workspace "Administration" aus dem Pulldownmenü.
3) Öffne die Topicmap "Users and Groups" mittels Doppelklick.
4) Klicke den User "root" an.
5) Gebe das Passwort in das entsprechende Feld rechts ein.


Weitere Instanzen einrichten
----------------------------

Für unterschiedliche Anwendungszwecke können unabhängige DeepaMehta-Instanzen angelegt werden (z.B. eine mit "echten" Inhalten und eine mit Testinhalten während der Entwicklung). Jede DeepaMehta-Instanz hat ein separates Corporate Memory (Inhalte-Speicher). Für jedes Corporate Memory kann ein individuelles Datenbanksystems (z.B. HSQL oder MySQL) verwendet werden.

Um eine neue DeepaMehta-Instanz einzurichten gebe folgendes Kommando ein: 

    ./run.sh newinstance

Ales erstes wirst Du gefragt, auf welchen Einstellungen die neue Instanz basieren soll. Wenn z.B. für die neue Instanz die HSQL-Datenbank benutzt werden soll, wähle "hsqldb-intern" aus.

    [echo] Please select the instance configuration the new instance is based on:
    [echo] 
    [echo] * hsqldb-intern
    [echo] * mysql4
    [echo] * mysql5
    [echo] 
    [input] Instance name: [hsqldb-intern]

Dann wirst Du nach einem Namen für die neue Instanz gefragt. Die Empfehlung ist, einen Namen zu verwenden, der den Zweck der Instanz bezeichnet, z.B. "production" oder "test".

    [input] Please enter the new instance name: [hsqldb-intern2]

Dann wird die neue Instanz konfiguriert und installiert, im Prinzip wie oben im Abschnitt "Installation" unter "Schritt 2: Installieren" erklärt ist. Wenn Du nach dem Namen der anzulegenden Datenbank gefragt wirst, werden alle bereits vorhandenen Datenbanken aufgelistet. Gebe einen Datenbanknamen ein, der nicht in der Liste vorhanden ist.

    [input] Please enter the name of your database: [DeepaMehta]

Die anderen Fragen (bezgl. den Webfrontends und den Beispielanwendungen) beantworte einfach mit Return, wodurch die bisherigen Einstellungen für die neue Instanz übernommen werden (Hinweis: diese Einstellungen werden tatsächlich nicht pro DeepaMehta-Instanz gespeichert, sondern sind global).

Dann wirst Du gefragt, ob die neue Instanz jetzt installiert werden soll. Beantworte die Fragen durch Drücken von Return.

    [input] Do you want to initialize now? ([y], n)

Wenn die Instanz erfolgreich angelegt wurde, wird "BUILD SUCCESSFUL" angezeigt. Zum Starten einer bestimmten DeepaMehta-Instanz siehe oben den Abschnitt "Starten & Beenden" und den folgenden Punkt "Standard-Instanz festlegen".


Standard-Instanz festlegen
--------------------------

Eine bestimmte DeepaMehta-Instanz kann als Standard-Instanz festgelegt werden. Die Standard-Instanz ist diejenige, die benutzt wird, wenn DeepaMehta ohne Angabe einer Instanz gestartet wird.

Zum Festlegen der Standard-Instanz gebe folgendes Kommando ein:

    ./run.sh switchinstance

Es werden die Namen aller bisher eingerichteten Instanzen aufgelistet und angezeigt, welche aktuell die Standard-Instanz ist. Gebe den Namen der Instanz, die als Standard-Instanz festgelegt werden soll, ein und drücke Return.

    [echo] Please select the DeepaMehta instance to activate:
    [echo] 
    [echo] * hsqldb-intern
    [echo] * myinstance
    [echo] 
    [input] Currently set [hsqldb-intern]


Instanz löschen
---------------

Zum Löschen einer Instanz gebe folgendes Kommando ein, wobei mittels -D die "dm.instance" Einstellung auf die zu löschenden Instanz zu setzen ist:

    ./run.sh dropdb -Ddm.instance=myinstance

Zur Sicherheit wird nachgefragt, ob die Instanz tatsächlich gelöscht werden soll. Um die Instanz zu löschen gebe 'y' ein und drücke Return.

    [echo] Uninstalling the DeepaMehta instance 'myinstance' now...
    ...
    [echo] You are about to delete the database 'DeepaMehta'.
    ...
    [input] Continue?  (y, n)


Kontrollieren von Tomcat
------------------------

=> Zum Starten von Tomcat gebe folgendes Kommando ein:

    ./run.sh tomcat-start

WICHTIG: Tomcat muß aus dem Verzeichnis deepamehta/install/client/ heraus gestartet werden, sonst können die Webfrontends notwendige Dateien nicht finden. Benutze zum Starten von Tomcat das hier angegebene Kommando, und nicht die Mechanismen Deiner Systemumgebung (z.B. /etc/init.d bei Linux).

=> Zum Stoppen von Tomcat gebe folgendes Kommando ein:

    ./run.sh tomcat-stop

=> Zum Anzeigen der Tomcat-Diagnosemeldungen gebe folgendes Kommando ein:

    ./run.sh tomcat-log

Tipp: die Tomcat-Diagnosemeldungen können am besten mitgelesen werden, wenn das Kommando in einem separaten Terminal-Fenster eingegeben wird, und dieses während der ganzen Sitzung offengelassen wird.


Die Datenbank zurücksetzen
--------------------------


DeepaMehta Deinstallieren
-------------------------



Wie geht's weiter?
==================

Bedienhinweise für die ersten Schritte (in Englisch):
http://www.deepamehta.de/wiki/en/Quickstart

DeepaMehta User Guide (in Englisch):
http://www.deepamehta.de/wiki/en/User_Guide

Für Deine Fragen benutze möglichst das Forum auf der DeepaMehta Website oder abboniere die deepamehta-users Mailingliste. Im Forum befinden sich bereits wichtige Hinweise zur Bedienung. Forum und Mailinglisten sind auf www.deepamehta.de unter "Community" zu erreichen.



------------------------------------------------------------------------------------------------------
Jörg Richter                                                                         www.deepamehta.de
1.11.2008
