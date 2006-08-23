---
--- This patch updates CM 2.8 to 2.9
--- Apply this patch if you want to update DeepaMehta 2.0b1 to 2.0b2 while keeping your content
---



--- Define new topic type "Image" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-image', 'Image');
INSERT INTO TopicProp VALUES ('tt-image', 1, 'Name', 'Image');
INSERT INTO TopicProp VALUES ('tt-image', 1, 'Plural Name', 'Images');
INSERT INTO TopicProp VALUES ('tt-image', 1, 'Icon', 'image.gif');
INSERT INTO TopicProp VALUES ('tt-image', 1, 'Property Layout', 'Subtypes first');
INSERT INTO TopicProp VALUES ('tt-image', 1, 'Custom Implementation', 'de.deepamehta.topics.ImageTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-56', '', 'tt-image', 1, 'pp-width', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-72', '', 'tt-image', 1, 'pp-height', 1);
INSERT INTO AssociationProp VALUES ('a-56', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-72', 1, 'Ordinal Number', '2');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-27', '', 'tt-file', 1, 'tt-image', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-imagecontainer', 'Image Search');
INSERT INTO TopicProp VALUES ('tt-imagecontainer', 1, 'Name', 'Image Search');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-29', '', 'tt-topiccontainer', 1, 'tt-imagecontainer', 1);
-- assign topic type to its container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-42', '', 'tt-imagecontainer', 1, 'tt-image', 1);

--- Properties "Width" and "Height" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-width', 'Width');
INSERT INTO TopicProp VALUES ('pp-width', 1, 'Name', 'Width');
INSERT INTO TopicProp VALUES ('pp-width', 1, 'Visualization', 'Input Field');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-height', 'Height');
INSERT INTO TopicProp VALUES ('pp-height', 1, 'Name', 'Height');
INSERT INTO TopicProp VALUES ('pp-height', 1, 'Visualization', 'Input Field');



--- Rename "Construction" workspace to "Type Builder" ---
UPDATE Topic SET Name='Type Builder' WHERE ID='t-constructionworkspace';
UPDATE Topic SET Name='Type Builder' WHERE ID='t-constructiontopicmap';
UPDATE Topic SET Name='Type Builder Chats' WHERE ID='t-constructionchatboard';
UPDATE Topic SET Name='Type Builder Forum' WHERE ID='t-constructionforum';
UPDATE TopicProp SET PropValue='Type Builder' WHERE TopicID='t-constructionworkspace' AND PropName='Name';
UPDATE TopicProp SET PropValue='Type Builder' WHERE TopicID='t-constructiontopicmap' AND PropName='Name';
UPDATE TopicProp SET PropValue='Type Builder Chats' WHERE TopicID='t-constructionchatboard' AND PropName='Name';
UPDATE TopicProp SET PropValue='Type Builder Forum' WHERE TopicID='t-constructionforum' AND PropName='Name';



--- *** Remove Builde Mode *** ---
DELETE FROM ViewTopic WHERE ViewMode='B';
DELETE FROM ViewAssociation WHERE ViewMode='B';
--- *** UPDATE DATA DEFINITION *** ---
ALTER TABLE ViewTopic DROP ViewMode;
ALTER TABLE ViewAssociation DROP ViewMode;



---
--- Update standard installation
---
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b2'       WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b2' WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';

---
--- Update DB content version
---
UPDATE KeyGenerator SET NextKey=9 WHERE Relation='DB-Content Version';
