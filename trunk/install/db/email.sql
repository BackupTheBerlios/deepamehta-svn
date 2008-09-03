--- "Email" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-email', 'Email');
-- set properties
INSERT INTO TopicProp VALUES ('tt-email', 1, 'Name', 'Email');
INSERT INTO TopicProp VALUES ('tt-email', 1, 'Icon', 'mail.gif');
INSERT INTO TopicProp VALUES ('tt-email', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-email', 1, 'Custom Implementation', 'de.deepamehta.topics.EmailTopic');
-- super type
-- INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-128', '', 'tt-generic', 1, 'tt-email', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-emailcontainer', 'Email Search');
-- set properties of container type
INSERT INTO TopicProp VALUES ('tt-emailcontainer', 1, 'Name', 'Email Search');
INSERT INTO TopicProp VALUES ('tt-emailcontainer', 1, 'Icon', 'mailcontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-drvmailcont', '', 'tt-topiccontainer', 1, 'tt-emailcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-139', '', 'tt-emailcontainer', 1, 'tt-email', 1);
-- assign properties to container type
-- INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-emcp1', '', 'tt-emailcontainer', 1, 'pp-date', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-200', '', 'tt-emailcontainer', 1, 'pp-from', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-201', '', 'tt-emailcontainer', 1, 'pp-to', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-202', '', 'tt-emailcontainer', 1, 'pp-subject', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-203', '', 'tt-emailcontainer', 1, 'pp-text', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-204', '', 'tt-emailcontainer', 1, 'pp-status', 1);
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-175', '', 'tt-email', 1, 'pp-date', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-176', '', 'tt-email', 1, 'pp-from', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-177', '', 'tt-email', 1, 'pp-to', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-178', '', 'tt-email', 1, 'pp-subject', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-179', '', 'tt-email', 1, 'pp-text', 1);
INSERT INTO AssociationProp VALUES ('a-175', 1, 'Ordinal Number', '10');
INSERT INTO AssociationProp VALUES ('a-176', 1, 'Ordinal Number', '20');
INSERT INTO AssociationProp VALUES ('a-177', 1, 'Ordinal Number', '30');
INSERT INTO AssociationProp VALUES ('a-178', 1, 'Ordinal Number', '40');
INSERT INTO AssociationProp VALUES ('a-179', 1, 'Ordinal Number', '50');



INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-date', 'Date');
INSERT INTO TopicProp VALUES ('pp-date', 1, 'Name', 'Date');
INSERT INTO TopicProp VALUES ('pp-date', 1, 'Visualization', 'Date Chooser');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-from', 'From');
INSERT INTO TopicProp VALUES ('pp-from', 1, 'Name', 'From');
INSERT INTO TopicProp VALUES ('pp-from', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-to', 'To');
INSERT INTO TopicProp VALUES ('pp-to', 1, 'Name', 'To');
INSERT INTO TopicProp VALUES ('pp-to', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-subject', 'Subject');
INSERT INTO TopicProp VALUES ('pp-subject', 1, 'Name', 'Subject');
INSERT INTO TopicProp VALUES ('pp-subject', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-text', 'Text');
INSERT INTO TopicProp VALUES ('pp-text', 1, 'Name', 'Text');
INSERT INTO TopicProp VALUES ('pp-text', 1, 'Visualization', 'Multiline Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-status', 'Status');
INSERT INTO TopicProp VALUES ('pp-status', 1, 'Name', 'Status');
INSERT INTO TopicProp VALUES ('pp-status', 1, 'Visualization', 'Input Field');
