---
--- This patch updates CM 2.16 to 2.17
--- Apply this patch if you want to update DeepaMehta 2.0b7 to 2.0b8 while keeping your content
---


---
--- New "Membership" Properties: "Editor" and "Publisher"
---
-- create properties
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-editor', 'Editor');
INSERT INTO TopicProp VALUES ('pp-editor', 1, 'Name', 'Editor');
INSERT INTO TopicProp VALUES ('pp-editor', 1, 'Visualization', 'Switch');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-publisher', 'Publisher');
INSERT INTO TopicProp VALUES ('pp-publisher', 1, 'Name', 'Publisher');
INSERT INTO TopicProp VALUES ('pp-publisher', 1, 'Visualization', 'Switch');
-- assign properties to association type "Membership"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-93', '', 'at-membership', 1, 'pp-editor', 1);
INSERT INTO AssociationProp VALUES ('a-93', 1, 'Ordinal Number', '10');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-94', '', 'at-membership', 1, 'pp-publisher', 1);
INSERT INTO AssociationProp VALUES ('a-94', 1, 'Ordinal Number', '20');



--- *** UPDATE DATA DEFINITION *** ---
ALTER TABLE       TopicProp CHANGE PropName PropName CHAR(255) NOT NULL;
ALTER TABLE AssociationProp CHANGE PropName PropName CHAR(255) NOT NULL;
