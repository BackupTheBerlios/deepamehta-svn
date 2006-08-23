---
--- This patch updates CM 2.10 to 2.11
--- Apply this patch if you want to update DeepaMehta 2.0b3-pre1 to 2.0b3-pre2 while keeping your content
---

---
--- Extend topic type "Person"
---
--- New property "Gender" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-gender', 'Gender');
INSERT INTO TopicProp VALUES ('pp-gender', 1, 'Name', 'Gender');
INSERT INTO TopicProp VALUES ('pp-gender', 1, 'Visualization', 'Option Buttons');
-- property values
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-male', 'Male');
INSERT INTO TopicProp VALUES ('t-male', 1, 'Name', 'Male');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-female', 'Female');
INSERT INTO TopicProp VALUES ('t-female', 1, 'Name', 'Female');
-- assign property values
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-145', '', 'pp-gender', 1, 't-male', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-146', '', 'pp-gender', 1, 't-female', 1);
INSERT INTO AssociationProp VALUES ('a-145', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-146', 1, 'Ordinal Number', '2');
-- assign property to "Person"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-147', '', 'tt-person', 1, 'pp-gender', 1);
INSERT INTO AssociationProp VALUES ('a-147', 1, 'Ordinal Number', '105');



---
--- Update standard installation
---
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b3-pre2'       WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b3-pre2' WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';

---
--- Update DB content version
---
UPDATE KeyGenerator SET NextKey=11 WHERE Relation='DB-Content Version';
