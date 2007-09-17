Mailman Integration

Ein Mailman List Topic (de.deepamehta.topics.MailmanTopic) in der Mailman Map ermöglicht die
Angabe eine Übersichtsseite wie https://lists.berlios.de/pipermail/deepamehta-devel/
Mit der Aktion Get Messages wird die von dem Mailman Webinterface erzeugte Seite nach Archiven 
durchsucht, die temporär als Datei gespeichtert und dann für den Import verarbeitet werden.
Für jede Message in einem Archiv wird ein Message Topic angelegt. In diesem
werden die Daten wie Name, Betreff und Inhalt der Mailman Message gespeichert.

Bugs:
 * es werden alle importierten Messages direkt angezeigt (leider habe ich keine
   Directive bzw. Methode gefunden, die "nur speichert"). Wahrscheinlich muss der 
   as.cm direkt verwendet werden, siehe entspr. TODOs im Quelltext
 * Prozess nicht von GUI entkoppelt (steht während des Importes)
 * die Assign Aktionen für die Message Assoziationen müssen noch entfernt werden

Features:
 * die Relation zwischen den Nachrichten über die referenceIds fehlt leider noch,
   die Assoziation muss über einen eigenen Typen (Thread) geschehen
 * die Namensattribute müssen noch mit der Custom Implementation gemappt werden (z.B. Subject)
 * die Email-Adressen und Namen verwenden, um Personen und Emails zuzuordnen
 * auf eine Message antworten ;-)
 
Anmerkungen bzw. was ist noch zu machen:
 * Achtung, die Messages werden jedesmal komplett geladen, Updates 
   werden noch nicht unterstützt. Für die Umsetzung könnten die Archive bzw. deren
   Metadaten als Topics angelegt und evtl. beim nächsten Aufruf mit dem HTTP Header
   verglichen werden.
 * derzeitig ist noch keine Authentifizierung implementiert
 * die Anzahl der Archive kann nur fest angegeben werden (derzeitig 1)



Installation der Mailman Beispiele:

1. Datenbank-Patch einspielen

# ant patchdb -Dpatch=install/examples/mailman/mailman.sql

2. Bibliothek mit den Custom-Implementierungen erstellen

Archiv erzeugen (install/examples/mailman/MailmanArchiveTopics.jar):
# ant -f install/examples/mailman/build.xml

3. Das Archive muß in den Classpath aufgenommen werden, dafür in der Datei
config.xml den Abschnitt "DeepaMehta Applications Include" prüfen, z.B.:
<pathelement location="${examples}/mailman/MailmanArchiveTopics.jar"/>
Weiterhin werden folgende Bibliotheken benötigt:
<pathelement location="${libpath}/commons-logging-1.1.jar"/>
<pathelement location="${libpath}/commons-codec-1.3.jar"/>
<pathelement location="${libpath}/commons-httpclient-3.0.1.jar"/>
