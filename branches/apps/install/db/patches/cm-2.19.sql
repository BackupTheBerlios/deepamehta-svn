---
--- This patch updates CM 2.18 to 2.19
--- Apply this patch if you want to update DeepaMehta rev346-20080910 to DeepaMehta 2.0b8 while keeping your content
---



----------------------------
--- Update Email Feature ---
----------------------------



--- remove supertype of "Recipient" and "Sender" ---
DELETE FROM Association WHERE ID='a-327';
DELETE FROM AssociationProp WHERE AssociationID='a-327';
DELETE FROM ViewAssociation WHERE AssociationID='a-327';
DELETE FROM Association WHERE ID='a-329';
DELETE FROM AssociationProp WHERE AssociationID='a-329';
DELETE FROM ViewAssociation WHERE AssociationID='a-329';


--- create property "Recipient Type" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-recipienttype', 'Recipient Type');
INSERT INTO TopicProp VALUES ('pp-recipienttype', 1, 'Name', 'Recipient Type');
INSERT INTO TopicProp VALUES ('pp-recipienttype', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-recipienttype', 1, 'Default Value', 'To');
-- assign property to "Recipient"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-347', '', 'at-recipient', 1, 'pp-recipienttype', 1);
INSERT INTO AssociationProp VALUES ('a-347', 1, 'Ordinal Number', '50');
-- create property values "To", "Cc", "Bcc"
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-recipienttype-to', 'To');
INSERT INTO TopicProp VALUES ('t-recipienttype-to', 1, 'Name', 'To');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-recipienttype-cc', 'Cc');
INSERT INTO TopicProp VALUES ('t-recipienttype-cc', 1, 'Name', 'Cc');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-recipienttype-bcc', 'Bcc');
INSERT INTO TopicProp VALUES ('t-recipienttype-bcc', 1, 'Name', 'Bcc');
-- assign property values to "Recipient Type"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-344', '', 'pp-recipienttype', 1, 't-recipienttype-to', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-345', '', 'pp-recipienttype', 1, 't-recipienttype-cc', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-346', '', 'pp-recipienttype', 1, 't-recipienttype-bcc', 1);
INSERT INTO AssociationProp VALUES ('a-344', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-345', 1, 'Ordinal Number', '2');
INSERT INTO AssociationProp VALUES ('a-346', 1, 'Ordinal Number', '3');


--- set custom implementation for "Person Search", "Institution", and "Institution Search" ---
INSERT INTO TopicProp VALUES ('tt-personcontainer', 1, 'Custom Implementation', 'de.deepamehta.topics.PersonSearchTopic');
INSERT INTO TopicProp VALUES ('tt-institution', 1, 'Custom Implementation', 'de.deepamehta.topics.InstitutionTopic');
INSERT INTO TopicProp VALUES ('tt-institutioncontainer', 1, 'Custom Implementation', 'de.deepamehta.topics.InstitutionSearchTopic');



-----------------------
--- Version Control ---
-----------------------



-- change version labels
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b8'         WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b8'   WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';

-- update DB content version
UPDATE KeyGenerator SET NextKey=19 WHERE Relation='DB-Content Version';
