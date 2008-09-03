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



----------------------------
--- Update Email Feature ---
----------------------------



--- create topic type "Recipient List" ---

INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-recipientlist', 'Recipient List');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Name', 'Recipient List');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Plural Name', 'Recipient Lists');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Description', '<html><head></head><body><p>A <i>Recipient List</i> is ...</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Description Query', 'What is a "Recipient List"?');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Icon', 'authentificationsource.gif');
-- INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Creation Icon', 'createKompetenzstern.gif');
-- INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Custom Implementation', 'de.deepamehta.topics.RecipientListTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-340', '', 'tt-generic', 1, 'tt-recipientlist', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-recipientlist-search', 'Recipient List Search');
INSERT INTO TopicProp VALUES ('tt-recipientlist-search', 1, 'Name', 'Recipient List Search');
-- INSERT INTO TopicProp VALUES ('tt-recipientlist-search', 1, 'Icon', 'event-search.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-341', '', 'tt-topiccontainer', 1, 'tt-recipientlist-search', 1);
-- assign search type to type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-342', '', 'tt-recipientlist-search', 1, 'tt-recipientlist', 1);

-- remove association types "attachement" and "e-mail addressee"

DELETE FROM Topic WHERE ID='at-attachement';
DELETE FROM TopicProp WHERE TopicID='at-attachement';
DELETE FROM ViewTopic WHERE TopicID='at-attachement';
DELETE FROM Association WHERE TypeID='at-attachement';

DELETE FROM Topic WHERE ID='at-emailaddressee';
DELETE FROM TopicProp WHERE TopicID='at-emailaddressee';
DELETE FROM ViewTopic WHERE TopicID='at-emailaddressee';
DELETE FROM Association WHERE TypeID='at-emailaddressee';

--- create association type: "Attachment" ---

INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-attachment', 'Attachment');
INSERT INTO TopicProp VALUES ('at-attachment', 1, 'Name', 'Attachment');
INSERT INTO TopicProp VALUES ('at-attachment', 1, 'Plural Name', 'Attachments');
INSERT INTO TopicProp VALUES ('at-attachment', 1, 'Color', '#408000');
