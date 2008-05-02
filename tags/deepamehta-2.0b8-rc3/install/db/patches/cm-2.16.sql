---
--- This patch updates CM 2.15 to 2.16
--- Apply this patch if you want to update DeepaMehta 2.0b6 to 2.0b7 while keeping your content
---



----------------------------------
--- New Feature: Color Chooser ---
----------------------------------



--- create new visualization "Color Chooser" ---
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-color', 'Color Chooser');
INSERT INTO TopicProp VALUES ('t-color', 1, 'Name', 'Color Chooser');
INSERT INTO TopicProp VALUES ('t-color', 1, 'Icon', 'color.gif');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-297', '', 'pp-visualization', 1, 't-color', 1);
INSERT INTO AssociationProp VALUES ('a-297', 1, 'Ordinal Number', '20');

--- place "File Chooser" and "hidden" behind "Color Chooser"
UPDATE AssociationProp SET PropValue = '30' WHERE AssociationID = 'a-291' AND PropName = 'Ordinal Number'; 
UPDATE AssociationProp SET PropValue = '40' WHERE AssociationID = 'a-57' AND PropName = 'Ordinal Number'; 

--- change visualization of property "Color" ---
UPDATE TopicProp SET PropValue = 'Color Chooser' WHERE TopicID = 'pp-color' AND PropName = 'Visualization'; 

--- change visualization of property "Background Color" ---
UPDATE TopicProp SET PropValue = 'Color Chooser' WHERE TopicID = 'pp-bgcolor' AND PropName = 'Visualization';



------------------------------------
--- Reorganize Association Types ---
------------------------------------



--- Rename association type "Association" (pink) to "Assignment" ---
UPDATE Topic SET Name='Assignment' WHERE ID='at-association';
UPDATE TopicProp SET PropValue='Assignment' WHERE TopicID='at-association' AND PropName='Name';
UPDATE TopicProp SET PropValue='Assignments' WHERE TopicID='at-association' AND PropName='Plural Name';

--- Reroute association type "Relation" from workspace "DeepaMehta" to workspace "Type Builder"
UPDATE Association SET TopicID1='t-constructionworkspace' WHERE ID='a-205';
DELETE FROM ViewAssociation WHERE AssociationID='a-205';



----------------------------
--- Update Email Feature ---
----------------------------

--- New association type: "Recipient" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-recipient', 'Recipient');
INSERT INTO TopicProp VALUES ('at-recipient', 1, 'Name', 'Recipient');
INSERT INTO TopicProp VALUES ('at-recipient', 1, 'Plural Name', 'Recipients');
INSERT INTO TopicProp VALUES ('at-recipient', 1, 'Color', '#E14589');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-327', '', 'at-generic', 1, 'at-recipient', 1);

--- New association type: "Sender" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-sender', 'Sender');
INSERT INTO TopicProp VALUES ('at-sender', 1, 'Name', 'Sender');
INSERT INTO TopicProp VALUES ('at-sender', 1, 'Plural Name', 'Senders');
INSERT INTO TopicProp VALUES ('at-sender', 1, 'Color', '#4589E1');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-329', '', 'at-generic', 1, 'at-sender', 1);

--- Set order of "Email" properties ---
INSERT INTO AssociationProp VALUES ('a-175', 1, 'Ordinal Number', '10');
INSERT INTO AssociationProp VALUES ('a-176', 1, 'Ordinal Number', '20');
INSERT INTO AssociationProp VALUES ('a-177', 1, 'Ordinal Number', '30');
INSERT INTO AssociationProp VALUES ('a-178', 1, 'Ordinal Number', '40');
INSERT INTO AssociationProp VALUES ('a-179', 1, 'Ordinal Number', '50');



---
--- Update standard installation
---
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b7'       WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b7' WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';

---
--- Update DB content version
---
UPDATE KeyGenerator SET NextKey=16 WHERE Relation='DB-Content Version';
