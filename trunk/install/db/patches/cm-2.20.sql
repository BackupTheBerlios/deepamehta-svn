INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-form-helptext', 'Helptext');

INSERT INTO TopicProp VALUES ('at-form-helptext', 1, 'Name', 'Helptext');
INSERT INTO TopicProp VALUES ('at-form-helptext', 1, 'Plural Name', 'Helptexts');
INSERT INTO TopicProp VALUES ('at-form-helptext', 1, 'Description', '<html><body><p>A <i>Helptext</i> is used for setting a Note-Topic in relation with a Property-Topic. Once set in relation, the Description-Text of the Note-Topic is rendered as a Hint in the form above the label and the input field of the associated Property.</p></body></html>');
INSERT INTO TopicProp VALUES ('at-form-helptext', 1, 'Description Query', 'What is a Helptext association?');
INSERT INTO TopicProp VALUES ('at-form-helptext', 1, 'Color', '#3f4da6');

INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-form-helptext-use', '', 't-administrationgroup', 1, 'at-form-helptext', 1);

UPDATE KeyGenerator SET NextKey=20 WHERE Relation='DB-Content Version';




