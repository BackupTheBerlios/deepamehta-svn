---
--- This patch updates CM 2.12 to 2.13
--- Apply this patch if you want to update DeepaMehta 2.0b3 to 2.0b4 while keeping your content
---

---
--- Update standard installation
---
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b4'       WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b4' WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';

---
--- Update DB content version
---
UPDATE KeyGenerator SET NextKey=13 WHERE Relation='DB-Content Version';
