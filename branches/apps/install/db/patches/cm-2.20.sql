---
--- This patch updates CM 2.19 to 2.20
--- Apply this patch if you want to update DeepaMehta rev346-20080910 to DeepaMehta 2.0b8 while keeping your content
---



----------------------------
--- Lobo Browser Feature ---
----------------------------

--- "Lobo Browser" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-browser', 'Lobo Browser');
INSERT INTO TopicProp VALUES ('pp-browser', 1, 'Visualization', 'Lobo Browser');

INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-511', '', 'tt-webpage', 1, 'pp-browser', 1);
INSERT INTO AssociationProp VALUES ('a-511', 1, 'Ordinal Number', '200');

UPDATE TopicProp SET PropValue='applications-internet.png' WHERE TopicID='tt-webpage' AND PropName='Icon';

-- change googley key name needs to adapt as.getGoogleKey then.. 
-- UPDATE TopicProp SET PropValue='Yahoo Search API Key' WHERE TopicID='pp-googlekey' AND PropName='Name';
-- UPDATE Topic SET Name='Yahoo Search API UpdateKey' WHERE ID='pp-googlekey';



-----------------------
--- Version Control ---
-----------------------

-- change version labels
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b8e'         WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b8e'   WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';

-- update DB content version
UPDATE KeyGenerator SET NextKey=19 WHERE Relation='DB-Content Version';
