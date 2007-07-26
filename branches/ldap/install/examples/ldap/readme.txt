Dateien in diesem Verzeichnis:

ldap.sql
* erzeugt einen "LDAP"-Workspace und zurgehörige DM-Kommnikationstools
* die LDAP-Typen werden dem erzeugten Workspace zugwiesen (TypeAccess)

build.xml
* packt die TopicType und Container-Implementierungen in ein Archiv



Installation der LDAP Beispiele:


1. Einen LDAP Server installieren und mit Testdaten bestücken. 

dafür empfiehlt sich die Installation einer lokalen OpenLDAP-Instanz, siehe:
# http://www.openldap.org/doc/admin23/quickstart.html

in der Konfigurations-Datei slapd.conf folgende Anpassungen vornehmen:
# suffix "dc=deepamehta,dc=de"
# rootdn "cn=admin,dc=deepamehta,dc=de"
# rootpw secret

vor dem Start des Dienstes noch die Testdaten hinzufügen
und dann den OpenLDAP Server starten:
# slapadd -l install/examples/ldap/deepamehta.ldif

Für die Bearbeitung der Daten und einen ersten Verbindungstest 
emfehlen sich Programme wie:
# http://www.jxplorer.org/
# http://directory.apache.org/studio/ldap-browser-plugin.html

Für den Zugriff auf das Verzeichnis kann der hinterlegte rootdn
oder einer der angelegten Benutzer verwendet werden.


2. Datenbank-Patch einspielen

# ant patchdb -Dpatch=install/examples/ldap/ldap.sql


3. Bibliothek mit den Custom-Implementierungen erstellen

Archiv erzeugen (install/examples/ldap/LDAPTopics.jar):
# ant -f install/examples/ldap/build.xml

weiterhin ist u.a. noch MessageBoard erforderlich, also vorher:
# ant -f install/examples/messageboard/build.xml jar

Anmerkung: die Klassen in den Archiven sind in den Topic-Definitionen mit der
Eigenschaft CustomImplementation zugewiesen.


4. Die Archive müssen in den Classpath aufgenommen werden, dafür in der Datei
config.xml den Abschnitt "DeepaMehta Applications Include" prüfen, z.B.:
# <pathelement location="${examples}/ldap/LDAPTopics.jar"/>


5. Nach dem DeepaMehta-Start stehen in der Suche die "LDAP Users" und "LDAP
Groups"-Container für eine Abfrage des Verzeichnisses zur Verfügung. Um eine
Suche auf einem LDAP-Server auszuführen, können die Einstellungen der
DataSource "LDAP-Source" in der "LDAP-Directory Map" angepasst werden,
voreingestellt ist ein Zugriff auf die Beispiel-Datenbank. Ein guter
Einstiegspunkt ist der LDAP-Workspace, in dem alle Topics für einen Überlick
zusammengetragen sind.


Anmerkungen zur aktuellen bzw. jetzt wieder lauffähigen Implementierung:
Die Rückgabe von getNameAttribute() der ElementContainerTopic-Abbleitung wird für den
DataConsumerTopic.revealTopicTypes()-Aufruf als Eigenschaft genutzt.