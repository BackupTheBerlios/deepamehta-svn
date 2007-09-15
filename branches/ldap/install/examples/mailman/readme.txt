Mailman Integration

Ein Mailman List Topic (de.deepamehta.topics.MailmanTopic) in der Mailman Map ermöglicht die
Angabe eine Übersichtsseite wie https://lists.berlios.de/pipermail/deepamehta-devel/
Mit der Aktion Get Messages wird die von dem Mailman Webinterface erzeugte Seite nach Archiven 
durchsucht, die temporär als Datei gespeichtert und dann für den Import verarbeitet werden.
Für jede Message in einem Archiv wird ein Message Topic angelegt. In diesem
werden die Daten wie Name, Betreff und Inhalt der Mailman Message gespeichert.

Bugs:
 * die Assoziationen sind noch ein wenig eigenartig, "What's related?" funktioniert 
   leider nicht wirklich
 * es werden alle importierten Messages direkt angezeigt (leider habe ich keine
   Directive bzw. Methode gefunden, die "nur speichert")
 * der Message Inhalt muss noch in HTML <pre> oder ähnliches
 * Prozess nicht von GUI entkoppelt (steht während des Importes)

Features:
 * die Relation zwischen den Nachrichten über die referenceIds fehlt leider noch,
   für die messageId muss ein eigenes Attribut oder eine Hash-Methode genutzt werden
 * die Email-Adressen und Namen verwenden, um Personen und Emails zuzuordnen
 
Anmerkungen bzw. was ist noch zu machen:
 * derzeitig ist noch keine Authentifizierung implementiert
 * Achtung, die Messages werden jedesmal komplett geladen, Updates 
   werden noch nicht unterstützt
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
<pathelement location="${libpath}/commons-httpclient-3.0.1.jar"/>