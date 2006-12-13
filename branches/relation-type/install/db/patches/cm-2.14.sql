---
--- This patch updates CM 2.13 to 2.14
--- Apply this patch if you want to update DeepaMehta 2.0b4 to 2.0b5 while keeping your content
---



--- *** UPDATE DATA DEFINITION *** ---
ALTER TABLE       TopicProp CHANGE PropValue PropValue MEDIUMTEXT;
ALTER TABLE AssociationProp CHANGE PropValue PropValue MEDIUMTEXT;



---
--- Update standard installation
---
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b5'       WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b5' WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';

---
--- Update DB content version
---
UPDATE KeyGenerator SET NextKey=14 WHERE Relation='DB-Content Version';
