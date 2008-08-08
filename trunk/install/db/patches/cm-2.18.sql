---
--- This patch updates CM 2.17 to 2.18
--- Apply this patch if you want to update DeepaMehta 2.0b8-rc4 to 2.0b8 while keeping your content
---

-- create property "Result"
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-result', 'Result');
INSERT INTO TopicProp VALUES ('pp-result', 1, 'Name', 'Result');
INSERT INTO TopicProp VALUES ('pp-result', 1, 'Visualization', 'Text Editor');
-- assign to topic type "Search"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-339', '', 'tt-container', 1, 'pp-result', 1);
INSERT INTO AssociationProp VALUES ('a-339', 1, 'Ordinal Number', '200');
