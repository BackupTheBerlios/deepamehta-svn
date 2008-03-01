
DeepaMehta 2.0b8
================


R E A D M E


--- Inhalt ---

Voraussetzungen
Inbetriebnahme, Kurzanleitung
Installation
  - Schritt 1: Auspacken
  - Schritt 2: Konfigurieren & Installieren
  - Schritt 3: Deployen der Webanwendungen
Starten & Beenden
  - Einzelplatz-Anwendung
  - Client/Server-Anwendung
  - Webanwendungen
Administration
  - Setzen des root Passworts
  - Weitere Instanzen einrichten
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

2) Konfigurieren:
		cd deepamehta
		./run.sh config
	Beantworte alle Fragen mit Return

3) Installieren:
		./run.sh install
	Beantworte die Frage mit Return

4) Starten:
		./run.sh
	Es erscheint der DeepaMehta-Login-Dialog.
	Gebe "root" ein und drücke 2x Return.
	Der DeepaMehta Desktop erscheint.

5) Dokumentation lesen:
	Bedienhinweise für die ersten Schritte stehen im Wiki:
		https://www.mindworxs.de/zwiki/ErsteSchritte
	Den Beginn eines Users Guides gibt es hier:
		www.deepamehta.de/docs/usersguide.html

6) Beenden:
	Schließe das DeepaMehta-Fenster.



Installation
============

* Schritt 1: Auspacken
* Schritt 2: Konfigurieren & Installieren
* Schritt 3: Deployen der Webanwendungen


Schritt 1: Auspacken
--------------------

Die DeepaMehta-Distribution besteht aus der Datei deepamehta-2.0b8.zip
Packe diese Datei in Deinem Verzeichnis für Anwendungen aus, z.B.:

	* Windows:     C:\Program Files\
	* Mac OS X:    /Macintosh HD/Applications/
	* Linux:       /usr/local/

Beim Auspacken wird ein Verzeichnis "deepamehta" angelegt.


Schritt 2: Konfigurieren & Installieren
---------------------------------------

Jetzt wird die DeepaMehta-Installation konfiguriert, hinsichtlich 3 Aspekten:
- Sollen die DeepaMehta Beispiel-Webanwendungen benutzt werden (erfordert Tomcat)?
- Welche Datenbank soll DeepaMehta benutzen (das mitgelieferte HSQL oder MySQL)?
- Welche Beispiel-Anwendungen sollen installiert werden?

Zum Starten der Konfigurierung gebe folgendes Kommando ein:

	./run.sh config

Dir werden ein paar Fragen gestellt, wobei die Standard-Antwort, die einfach durch Drücken von Return ausgelöst wird, in eckigen Klammern angegeben ist.

Als erstes wirst Du gefragt, ob Du auch die DeepaMehta Beispiel-Webanwendungen benutzen möchtest. Wenn Ja, mußt Du angeben, wo das Tomcat Home-Verzeichnis ist.

	[input] Do you want to install the example web applications (Tomcat must already be installed)? (y, [n])

	[input] Please enter the home directory of your Tomcat installation. [/usr/local/tomcat]

Dann wirst Du gefragt, in welcher Datenbank DeepaMehta seine Daten ablegen soll. Wenn die mitgelieferte HSQL-Datenbank benutzt werden soll, drücke einfach Return.

	[echo] Please select one of the following instance configurations:
	[echo]
	[echo] * hsqldb-intern
	[echo] * mysql4
	[echo] * mysql5
	[echo]
	[input] Currently set [hsqldb-intern]

ACHTUNG: Wenn DeepaMehta-Webfrontends und die grafische DeepaMehta-Oberfläche gleichzeitig auf einer Maschine benutzt werden sollen, muß als Datenbank MySQL benutzt werden. MySQL wird nicht mit DeepaMehta mitgeliefert und muß separat installiert werden.

Dann wirst Du nach dem Namen der anzulegenden Datenbank gefragt. Gebe den Namen der anzulegenden Datenbank ein, oder drücke einfach Return.

	[input] Please enter the name of your database: [DeepaMehta]

Dann wirst Du gefragt, welche der mitgelieferten Beispiel-Anwendungen Du installieren möchtest. Die Entscheidung steht Dir frei. Auch ohne Beispiel-Anwendungen ist DeepaMehta nutzbar.

	[input] Shall the example kompetenzstern be processed? ([y], n)
	[input] Shall the example messageboard be processed? ([y], n)
	[input] Shall the example ldap be processed? ([y], n)
	[input] Shall the example movies be processed? ([y], n)

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

Weitere Beispiel-Anwendungen werden angeboten:

	[input] Shall the example dm-browser be processed? (y, [n])
	[input] Shall the example dm-search be processed? (y, [n])
	[input] Shall the example dm-topicmapviewer be processed? (y, [n])
	[input] Shall the example dm-web be processed? (y, [n])

Damit ist die Konfiguration abgeschlossen.

Du wirst gefragt, ob die eigentliche Installation jetzt vorgenommen werden soll. Während der Installation wird die DeepaMehta-Datenbank angelegt und die ausgewählten Beispiel-Anwendungen eingespielt. Um mit der Installation fortzufahren, drücke Return.

	[input] Do you want to install now? ([y], n)

Dir werden nochmal die konfigurierten Datenbank-Angaben gezeigt. Sobald Du Return drückst wird die DeepaMehta-Datenbank angelegt und mit den initialen Inhalten gefüllt.

	[echo] --- DeepaMehta Installation ---
	[echo] You are about to create a database 'DeepaMehta' and an
	[echo] user 'sa' (password '') with corresponding grants.
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

Die DeepaMehta-Einzelplatz-Anwendung ist die einfachste Art DeepaMehta zu starten, und ist ausreichend, wenn nicht über das Netzwerk mit anderen Nutzern gemeinsam gearbeitet werden soll.

=> Zum Starten der DeepaMehta-Einzelplatz-Anwendung gebe folgendes Kommando ein:

	./run.sh

Es erscheint der DeepaMehta-Login-Dialog. Gebe "root" ein und drücke 2x Return (initial hat der DeepaMehta-root-User kein Passwort). Der DeepaMehta Desktop erscheint.

Zum Starten einer bestimmten DeepaMehta-Instanz setze auf der Kommandozeile mittels -D die "dm.instance" Einstellung auf die gewünschte Instanz:

	./run.sh -Ddm.instance=myinstance

Um eine bestimmte Instanz als Standard-Instanz zu benutzen, öffne mit einem Texteditor die Datei deepamehta/install/config/config.properties und setze die "dm.instance" Einstellung dort. Die Standard-Instanz ist diejenige, die benutzt wird, wenn DeepaMehta ohne Angabe einer Instanz gestartet wird. Informationen über DeepaMehta-Instanzen findest Du unten unter "Weitere Instanzen einrichten" (Abschnitt "Administration").

=> Zum Beenden der DeepaMehta-Sitzung schließe das DeepaMehta-Fenster.

Technischer Sicherheitshinweis: die monolithische DeepaMehta-Anwendung integriert den DeepaMehta-Client und -Server in eine einzige Anwendung, wobei über direkte Methoden kommuniziert wird. Es wird kein Netzwerkport geöffnet.


Client/Server-Anwendung
-----------------------

Die DeepaMehta-Client/Server-Anwendung ermöglicht Nutzern über das Netzwerk gemeinsam an Inhalten zu arbeiten.

=> Zum Starten des DeepaMehta-Servers gebe folgendes Kommando ein:

	./run.sh dms

Zum Servieren einer bestimmten DeepaMehta-Instanz setze auf der Kommandozeile mittels -D die "dm.instance" Einstellung auf die gewünschte Instanz:

	./run.sh dms -Ddm.instance=myinstance

Um eine bestimmte Instanz als Standard-Instanz zu benutzen, öffne mit einem Texteditor die Datei deepamehta/install/config/config.properties und setze die "dm.instance" Einstellung dort. Die Standard-Instanz ist diejenige, die benutzt wird, wenn DeepaMehta ohne Angabe einer Instanz gestartet wird. Informationen über DeepaMehta-Instanzen findest Du unten unter "Weitere Instanzen einrichten" (Abschnitt "Administration").

=> Zum Starten der DeepaMehta-Client-Anwendung gebe folgendes Kommando ein:

	./run.sh dmc

Es erscheint der DeepaMehta-Login-Dialog. Gebe "root" ein und drücke 2x Return (initial hat der DeepaMehta-root-User kein Passwort). Der DeepaMehta Desktop erscheint.

=> Zum Starten des DeepaMehta-Client-Applets resp. des signierten Client-Applets öffne die entsprechende Webseite in Deinem Webbrowser:

	.../deepamehta/install/client/start.html
	.../deepamehta/install/client/start-signed.html

Das Client-Applet erwartet, daß der DeepaMehta-Server auf der gleichen Maschine läuft, von der das Applet geladen wurde. Der Port, auf dem das Client-Applet versucht, den Server zu kontaktieren, can in den HTML-Seiten durch Angabe des Applet-Parameters "PORT" eingestellt werden. Wenn kein "Port"-Parameter vorhanden ist, wird der Standard-Port (7557) benutzt.

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

Für unterschiedliche Zwecke können jeweils eigene DeepaMehta-Instanzen angelegt werden (z.B. eine mit "echten" Inhalten und eine mit Testinhalten während der Entwicklung). Jede DeepaMehta-Instanz ist mit einem individuellen Corporate Memory (Inhalte-Speicher) assoziiert. Für jedes Corporate Memory kann ein individuelles Datenbanksystems (z.B. HSQL oder MySQL) verwendet werden.

Um eine neue DeepaMehta-Instanz einzurichten führe die 3 folgenden Schritte aus: 

1) Kopiere aus .../deepamehta/install/config/ die build- und die dm- Dateien für das gewünschte Datenbanksystem und ersetze im Dateinamen den Datenbanktyp ("hsqldb-intern", "mysql4", "mysql5") durch den Namen der neuen DeepaMehta-Instanz. Um z.B. eine neue DeepaMehta-Instanz "myinstance", die in einer HSQL-Datenbank abgelegt wird, einzurichten, führe folgende Kopiervorgänge durch:

	build-hsqldb-intern.properties	=> build-myinstance.properties
	dm-hsqldb-intern.properties		=> dm-myinstance.properties

2) Jetzt muß die neue Instanz konfiguriert werden, im Prinzip wie oben in "Schritt 2: Konfigurieren" (Abschnitt "Installation") erklärt ist:

	./run.sh config

In der Liste der vorhandenen DeepaMehta-Instanzen wird nun die neue Instanz (z.B. "myinstance") aufgelistet. Gebe hier den Namen der neuen Instanz ein und drücke Return.

	[echo] Please select one of the following instance configurations:
	[echo] 
	[echo] * hsqldb-intern
	[echo] * myinstance
	[echo] * mysql4
	[echo] * mysql5
	[echo] 
	[input] currently set (default) [hsqldb-intern]

Jetzt wirst Du gefragt, ob die neue Instanz die Standard-Instanz sein soll. Die Standard-Instanz ist diejenige, die benutzt wird, wenn DeepaMehta ohne Angabe einer Instanz gestartet wird. Zum Starten einer bestimmten DeepaMehta-Instanz siehe oben "Einzelplatz-Anwendung" oder "Client/Server-Anwendung" (Abschnitt "Starten & Beenden").

	[input] Setup myinstance as default instance? ([y], n)

Dann wirst Du wieder nach dem Namen der anzulegenden Datenbank gefragt, wobei alle bereits vorhandenen Datenbanken zuvor aufgelistet werden. Gebe einen Datenbanknamen ein, der nicht in der Liste vorhanden ist, und drücke Return.

	[input] Please enter the name of your database: [DeepaMehta]

Die anderen Fragen, die das Config-Skript stellt (bezgl. den Webanwendungen und den Beispiel-Anwendungen) beantworte einfach mit Return, wodurch die bisherigen Einstellungen beibehalten werden (Hinweis: diese Einstellungen werden tatsächlich nicht pro DeepaMehta-Instanz gespeichert, sondern sind global).

3) Nun wird die neue Instanz installiert, im Prinzip wie oben in "Schritt 3: Installieren" (Abschnitt "Installation") erklärt ist:

	./run.sh install

Die neue DeepaMehta-Instanz ist jetzt eingerichtet. Zum Starten einer bestimmten DeepaMehta-Instanz siehe oben "Einzelplatz-Anwendung" oder "Client/Server-Anwendung" (Abschnitt "Starten & Beenden").


Kontrollieren von Tomcat
------------------------


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



------------------------------------------------------------------------------------------------
Jörg Richter                                                                   www.deepamehta.de
1.3.2008
