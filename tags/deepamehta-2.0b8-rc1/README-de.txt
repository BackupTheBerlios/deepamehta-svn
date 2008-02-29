
DeepaMehta 2.0b8
================


R E A D M E


--- Inhalt ---

=> Voraussetzungen
=> Inbetriebnahme, Kurzanleitung
=> Inbetriebnahme




Voraussetzungen
===============

Notwendig:
- Java Standard Edition (Versionen 1.4 oder 5 oder 6)

Optional:
- MySQL (Version 4 oder 5) -- kann als Alternative zum mitgelieferten HSQL benutzt werden.
- Tomcat (ab Version 4) -- wird für die DeepaMehta Beispiel-Webfrontends benötigt.



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
		./run.sh dm
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



Inbetriebnahme
==============

1) Auspacken
2) Konfigurieren
3) Installieren
4) Deployen der Web-Frontends
5) Starten
6) Beenden
7) Wie geht's weiter?
8) Was bleibt offen?


1) Auspacken

Die DeepaMehta-Distribution besteht aus der Datei deepamehta-2.0b8.zip
Packe diese Datei in Deinem Verzeichnis für Anwendungen aus, z.B.:

	* Windows:     C:\Program Files\
	* Mac OS X:    /Macintosh HD/Applications/
	* Linux:       /usr/local/

Beim Auspacken wird ein Verzeichnis "deepamehta" angelegt.


2) Konfigurieren

Jetzt wird die DeepaMehta-Installation konfiguriert.

	./run.sh config

Dir werden ein paar Fragen gestellt, wobei die Standard-Antwort, die einfach durch Drücken von Return ausgelöst wird, in eckigen Klammern angegeben ist.

Als erstes wirst Du gefragt, ob Du auch die DeepaMehta Beispiel-Webfrontends benutzen möchtest. Wenn Ja, mußt Du angeben, wo das Tomcat Home-Verzeichnis ist.

	[input] Do you want to install web applications? (y, [n])

	[input] Please enter the root directory of your application server. [/usr/local/tomcat]

Dann wirst Du gefragt, in welcher Datenbank DeepaMehta seine Daten ablegen soll. Wenn die HSQL-Datenbank benutzt werden soll, drücke einfach Return.

	[echo] Please select one of the following instance configurations:
	[echo]
	[echo] * derby-intern
	[echo] * hsqldb-intern
	[echo] * mysql4
	[echo] * mysql5
	[echo]
	[input] currently set (default) [hsqldb-intern]

Dann wirst Du nach dem Namen der anzulegenden Datenbank gefragt. Gebe den Namen der anzulegenden Datenbank ein, oder drücke einfach Return.

	[input] Please enter the name of your database: [DeepaMehta]

Dann wirst Du gefragt, welche der mitgelieferten Beispiel-Anwendungen Du installieren möchtest. Die Entscheidung steht Dir frei. Auch ohne Beispielanwendungen ist DeepaMehta nutzbar.

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
	[input] Shall the example knoppixforum be processed? (y, [n])
	[input] Shall the example musicforum be processed? (y, [n])

Wenn die Konfiguration erfolgreich beendet wurde, wird "BUILD SUCCESSFUL" angezeigt.


3) Installieren

Zum Anlegen der initialen DeepaMehta-Datenbank gebe folgendes Kommando ein:

	./run.sh install

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

Wenn die Datenbanken erfolgreich angelegt wurden, wird "BUILD SUCCESSFUL" angezeigt.


4) Deployen der Web-Frontends

Wenn Du beim Konfigurieren gesagt hast, daß Du die DeepaMehta Beispiel-Webfrontends benutzen möchtest, gebe zum Deployen folgendes Kommando ein:

	./run.sh deploy


5) Starten

Die monolithische DeepaMehta-Anwendung wird mit folgendem Kommando gestartet:

	./run.sh dm

Es erscheint der DeepaMehta-Login-Dialog. Gebe "root" ein und drücke 2x Return (initial hat der DeepaMehta-root-User kein Passwort). Der DeepaMehta Desktop erscheint.


6) Beenden

Zum Beenden der DeepaMehta-Sitzung drücke das Schließen-Feld des DeepaMehta-Fensters.


7) Wie geht's weiter?

Bedienhinweise für die ersten Schritte stehen im Wiki:
https://www.mindworxs.de/zwiki/ErsteSchritte

Den Beginn eines Users Guides gibt es hier:
www.deepamehta.de/docs/usersguide.html

Für Deine Fragen benutze möglichst das Forum auf der DeepaMehta Website oder abboniere die deepamehta-users Mailingliste. Im Forum befinden sich bereits wichtige Hinweise zur Bedienung. Forum und Mailinglisten sind auf www.deepamehta.de unter "Community" zu erreichen.


8) Was bleibt offen?

Die aktuelle Fassung dieses READMEs läßt viele Themen unbehandelt, z.B. Administrationsaufgaben. Das README der alten DeepaMehta Version 2.0b7 enthält hierzu einige Hinweise, die teilweise auch für DeepaMehta 2.0b8 gelten.

- Ändern des root-Passworts
- DeepaMehta Client-Server Szenario / Das Client-Applet
- Zweck und Funktionalität der mitgelieferten Beispiel-Anwendungen
- Nutzung der DeepaMehta Beispiel-Webfrontends unter Tomcat
- Zurücksetzen der Datenbank in den Ausgangszustand
- Einrichten weiterer DeepaMehta Instanzen
- Vorhandene DeepaMehta Installation updaten
- DeepaMehta deinstallieren



--------------------------------------------------------------------------------------------
Jörg Richter                                                               www.deepamehta.de
24.2.2008
