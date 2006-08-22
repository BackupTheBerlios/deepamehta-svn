---
--- This patch updates CM 2.11 to 2.12
--- Apply this patch if you want to update DeepaMehta 2.0b3-pre2 to 2.0b3 while keeping your content
---


---
--- New Property "Default Value"
---
-- create property
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-defaultvalue', 'Default Value');
INSERT INTO TopicProp VALUES ('pp-defaultvalue', 1, 'Name', 'Default Value');
INSERT INTO TopicProp VALUES ('pp-defaultvalue', 1, 'Visualization', 'Input Field');
-- assign new property to topic type "Property"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-92', '', 'tt-property', 1, 'pp-defaultvalue', 1);
INSERT INTO AssociationProp VALUES ('a-92', 1, 'Ordinal Number', '120');

--- Content
-- Property "Access Permission" (AT "Type Access")
INSERT INTO TopicProp VALUES ('pp-createpermission', 1, 'Default Value', 'view');
-- Property "Visualization" (TT "Property")
INSERT INTO TopicProp VALUES ('pp-visualization', 1, 'Default Value', 'Input Field');
-- 4 Properties (AT "Relation")
INSERT INTO TopicProp VALUES ('pp-cardinality', 1, 'Default Value', 'one');
INSERT INTO TopicProp VALUES ('pp-associationtypeid', 1, 'Default Value', 'at-association');
INSERT INTO TopicProp VALUES ('pp-webinfo', 1, 'Default Value', 'Related Topic Name');
INSERT INTO TopicProp VALUES ('pp-webform', 1, 'Default Value', 'Related Topic Selector');



---
--- Extend topic type "TopicContainer"
---
-- "Search" property
INSERT INTO AssociationProp VALUES ('a-28', 1, 'Ordinal Number', '100');

---
--- Set "Mobile" icons
---
UPDATE TopicProp SET PropValue='mobile.gif'        WHERE TopicID='tt-personphone' AND PropName='Icon';
UPDATE TopicProp SET PropValue='mobile-search.gif' WHERE TopicID='tt-personphonesearch' AND PropName='Icon';

---
--- Change custom implementation of messageboard application
---
UPDATE TopicProp SET PropValue='de.deepamehta.messageboard.topics.MessageTopic' WHERE TopicID='tt-message' AND PropName='Custom Implementation';
UPDATE TopicProp SET PropValue='de.deepamehta.messageboard.topics.MessageBoardTopic' WHERE TopicID='tt-messageboard' AND PropName='Custom Implementation';



---
--- Update standard installation
---
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b3'       WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b3' WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';

---
--- Update DB content version
---
UPDATE KeyGenerator SET NextKey=12 WHERE Relation='DB-Content Version';
