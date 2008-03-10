
DeepaMehta 2.0b8
================


R E A D M E


--- Inhalt ---

Voraussetzungen
Inbetriebnahme, Kurzanleitung
Installation
  - Schritt 1: Auspacken
  - Schritt 2: Installieren
  - Schritt 3: Deployen der Webanwendungen
Starten & Beenden
  - Einzelplatz-Anwendung
  - Client/Server-Anwendung
  - Webanwendungen
Administration
  - Setzen des root Passworts
  - Weitere Instanzen einrichten
  - Standard-Instanz festlegen
  - Instanz löschen
  - Kontrollieren von Tomcat
  - Die Datenbank zurücksetzen
  - Vorhandene Installation updaten
  - Deinstallieren
Wie geht's weiter?



Voraussetzungen
===============

Notwendig:
- Java Standard Edition (Versionen 1.4 oder 5 oder 6)

Optional:
- MySQL (Version 4 oder 5) -- kann als Alternative zum mitgelieferten HSQL benutzt werden.
- Tomcat (ab Version 4) -- wird für die DeepaMehta Beispiel-Webanwendungen benötigt.



Inbetriebnahme, Kurzanleitung
=============================

1) Auspacken:
		unzip deepamehta-2.0b8.zip
	Es wird ein Verzeichnis "deepamehta" angelegt.

2) Installieren:
		cd deepamehta
		./run.sh install
	Beantworte alle Fragen mit Return

3) Starten:
		./run.sh
	Es erscheint der DeepaMehta-Login-Dialog.
	Gebe "root" ein und drücke 2x Return.
	Der DeepaMehta Desktop erscheint.

4) Dokumentation lesen:
	Bedienhinweise für die ersten Schritte stehen im Wiki:
		https://www.mindworxs.de/zwiki/ErsteSchritte
	Den Beginn eines Users Guides gibt es hier:
		www.deepamehta.de/docs/usersguide.html

5) Beenden:
	Schließe das DeepaMehta-Fenster.



Installation
============

* Schritt 1: Auspacken
* Schritt 2: Installieren
* Schritt 3: Deployen der Webanwendungen


Schritt 1: Auspacken
--------------------

Die DeepaMehta-Distribution besteht aus der Datei deepamehta-2.0b8.zip
Packe diese Datei in Deinem Verzeichnis für Anwendungen aus, z.B.:

	* Windows:     C:\Program Files\
	* Mac OS X:    /Macintosh HD/Applications/
	* Linux:       /usr/local/

Beim Auspacken wird ein Verzeichnis "deepamehta" angelegt.


Schritt 2: Installieren
-----------------------

Zum Starten der DeepaMehta-Installation gebe folgendes Kommando ein:

	./run.sh install

Zunächst wird die DeepaMehta-Installation konfiguriert, hinsichtlich 3 Aspekten:
- Sollen die DeepaMehta Beispiel-Webanwendungen benutzt werden (erfordert Tomcat)?
- Welche Datenbank soll DeepaMehta benutzen (das mitgelieferte HSQL oder MySQL)?
- Welche Beispiel-Anwendungen sollen installiert werden?

Dir werden ein paar Fragen gestellt, wobei die Standard-Antwort, die einfach durch Drücken von Return ausgelöst wird, in eckigen Klammern angegeben ist.

Als erstes wirst Du gefragt, ob Du auch die DeepaMehta Beispiel-Webanwendungen benutzen möchtest. Wenn Ja, mußt Du angeben, wo das Tomcat Home-Verzeichnis ist.

	[input] Do you want to install the example web applications (Tomcat must already be installed)? (y, [n])

	[input] Please enter the home directory of your Tomcat installation. [/usr/local/tomcat]

Dann wirst Du gefragt, in welcher Datenbank DeepaMehta seine Daten ablegen soll. Wenn die mitgelieferte HSQL-Datenbank benutzt werden soll, drücke einfach Return.

	[echo] Please select the DeepaMehta instance to be configured:
	[echo]
	[echo] * hsqldb-intern
		(Recommended for just using the DeepaMehta application. No additional software is required.)
	[echo] * mysql4
		(Recommended for use with the additional web frontends. MySQL 4 must be installed separately.)
	[echo] * mysql5
		(Recommended for use with the additional web frontends. MySQL 5 must be installed separately.)
	[echo]
	[input] Currently set [hsqldb-intern]

WICHTIG: Wenn DeepaMehta-Webfrontends und die grafische DeepaMehta-Oberfläche gleichzeitig auf einer Maschine benutzt werden sollen, muß als Datenbank MySQL benutzt werden. MySQL wird nicht mit DeepaMehta mitgeliefert und muß separat installiert werden.

Dann wirst Du nach dem Namen der anzulegenden Datenbank gefragt. Gebe den Namen der anzulegenden Datenbank ein, oder drücke einfach Return.

	[input] Please enter the name of the database to be created: [DeepaMehta]

Dann wirst Du nach dem Netzwerk-Port gefragt, auf dem der DeepaMehta-Server Client-Verbindungen annehmen soll. Diese Einstellung ist für den Client-Server-Betrieb relevant (siehe "Client/Server-Anwendung" im Abschnitt "Starten & Beenden"), besonders dann, wenn mehrere DeepaMehta-Instanzen im Einsatz sind (siehe "Weitere Instanzen einrichten" im Abschnitt "Administration"). Im Moment drücke einfach Return.

	[input] Network port for this instance (when served by the DeepaMehta server): [7557]

Dann wirst Du gefragt, welche der mitgelieferten Beispiel-Anwendungen Du installieren möchtest. Die Entscheidung steht Dir frei. Auch ohne Beispiel-Anwendungen ist DeepaMehta nutzbar.

	[input] Do you want to install the example application 'kompetenzstern'
			(Balanced Scorecard editor and report generator)? (y, [n])
	[input] Do you want to install the example application 'messageboard'
			(Graphical forum application and web frontend)? (y, [n])
	[input] Do you want to install the example application 'ldap'
			(LDAP-Client for browsing users and groups)? (y, [n])
	[input] Do you want to install the example application 'movies'
			(Demonstration of accessing external datasources)? (y, [n])

Sofern Du das "movies" Beispiel installieren möchtest, wirst Du jetzt nach der Art der Datenquelle gefragt, die für das movies-Beispiel angelegt werden soll.

	[echo] Please select one of the following instance configurations:
	[echo]
	[echo] * derby-intern
	[echo] * hsqldb-intern
	[echo] * mysql4
	[echo] * mysql5
	[echo] * xml
	[echo]
	[input] currently set (default) [hsqldb-intern]

Sofern Du "hsqldb-intern" ausgewählt hast, wirst Du jetzt nach dem Namen der anzulegenden Beispiel-Film-Datenbank gefragt.

	[input] Please enter the name of your database: [Movies]

Sofern Du eingangs gesagt hast, daß Du auch die DeepaMehta Webanwendungen benutzen möchtest (unter Tomcat), wirst Du jetzt gefragt, welche der mitgelieferten Beispiel-Webanwendungen Du installieren möchtest:

	[input] Do you want to install the example application 'dm-browser'
			(Generic web frontend demo 1)? (y, [n])
	[input] Do you want to install the example application 'dm-search'
			(Generic web frontend demo 2)? (y, [n])
	[input] Do you want to install the example application 'dm-topicmapviewer'
			(Generic web based topicmap viewer)? (y, [n])
	[input] Do you want to install the example application 'dm-web'
			(Generic web frontend demo 3, recommendend)? (y, [n])

Damit ist die Konfiguration abgeschlossen.

Du wirst gefragt, ob die eigentliche Installation jetzt vorgenommen werden soll. Während der Installation wird die DeepaMehta-Datenbank angelegt und die ausgewählten Beispiel-Anwendungen eingespielt. Um mit der Installation fortzufahren, drücke Return.

	[input] Do you want to install now? ([y], n)

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


Schritt 3: Deployen der Webanwendungen
--------------------------------------

Wenn Du beim Konfigurieren gesagt hast, daß Du die DeepaMehta Beispiel-Webanwendungen benutzen möchtest, werden diese jetzt unter Tomcat deployt. Zum Deployen gebe folgendes Kommando ein:

	./run.sh deploy

Wenn die Web-Anwendungen erfolgreich deployt wurden, wird "BUILD SUCCESSFUL" angezeigt.

WICHTIG: wenn beim Deployen Tomcat bereits lief, muß Tomcat jetzt neugestartet werden (auch wenn Tomcats Hot-Deployment Funktion aktiviert ist).



Starten & Beenden
=================

* Einzelplatz-Anwendung
* Client/Server-Anwendung
* Webanwendungen


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


Webanwendungen
--------------

Mit DeepaMehta werden 8 Beispiel-Webanwendungen mitgeliefert. Zum Starten einer Webanwendung gebe die entsprechende URL in Deinen Webbrowser ein:

	http://localhost:8080/kompetenzstern/controller
	http://localhost:8080/messageboard/controller
	http://localhost:8080/dm-browser/controller
	http://localhost:8080/dm-search/controller
	http://localhost:8080/dm-topicmapviewer/controller
	http://localhost:8080/dm-web/controller
	http://localhost:8080/knoppixforum/controller
	http://localhost:8080/musicforum/controller

Die Webanwendungen können nur gestartet werden, wenn DeepaMehta entsprechend konfiguriert wurde (siehe "Installation", Schritt 2: "Konfigurieren") und die Webanwendungen deployt wurden (siehe "Installation", Schritt 4: "Deployen der Webanwendungen"). Außerdem muß Tomcat gestartet sein.



Administration
==============

* Setzen des root Passworts
* Weitere Instanzen einrichten
* Standard-Instanz festlegen
* Instanz löschen
* Kontrollieren von Tomcat
* Die Datenbank zurücksetzen
* Vorhandene Installation updaten
* Deinstallieren


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

Die anderen Fragen (bezgl. den Webanwendungen und den Beispiel-Anwendungen) beantworte einfach mit Return, wodurch die bisherigen Einstellungen für die neue Instanz übernommen werden (Hinweis: diese Einstellungen werden tatsächlich nicht pro DeepaMehta-Instanz gespeichert, sondern sind global).

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

WICHTIG: Tomcat muß aus dem Verzeichnis deepamehta/install/client/ heraus gestartet werden, sonst können die Webanwendungen notwendige Dateien nicht finden. Benutze zum Starten von Tomcat das hier angegebene Kommando, und nicht die Mechanismen Deiner Systemumgebung (z.B. /etc/init.d bei Linux).

=> Zum Stoppen von Tomcat gebe folgendes Kommando ein:

	./run.sh tomcat-stop

=> Zum Anzeigen der Tomcat-Diagnosemeldungen gebe folgendes Kommando ein:

	./run.sh tomcat-log

Tipp: die Tomcat-Diagnosemeldungen können am besten mitgelesen werden, wenn das Kommando in einem separaten Konsolen-Fenster eingegeben wird, und dieses während der ganzen Sitzung offengelassen wird.


Die Datenbank zurücksetzen
--------------------------


Vorhandene Installation updaten
-------------------------------


Deinstallieren
--------------



Wie geht's weiter?
==================

Bedienhinweise für die ersten Schritte stehen im Wiki:
https://www.mindworxs.de/zwiki/ErsteSchritte

Den Beginn eines Users Guides gibt es hier:
www.deepamehta.de/docs/usersguide.html

Für Deine Fragen benutze möglichst das Forum auf der DeepaMehta Website oder abboniere die deepamehta-users Mailingliste. Im Forum befinden sich bereits wichtige Hinweise zur Bedienung. Forum und Mailinglisten sind auf www.deepamehta.de unter "Community" zu erreichen.



------------------------------------------------------------------------------------------------------
Jörg Richter                                                                         www.deepamehta.de
11.3.2008
