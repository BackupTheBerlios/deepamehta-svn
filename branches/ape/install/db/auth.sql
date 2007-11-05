

-- "Login" topic type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-login', 'Login');
INSERT INTO TopicProp VALUES ('tt-login', 1, 'Name', 'Login');
INSERT INTO TopicProp VALUES ('tt-login', 1, 'Icon', 'login.gif');
INSERT INTO TopicProp VALUES ('tt-login', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-login', 1, 'Custom Implementation', 'de.deepamehta.topics.LoginTopic');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-152', '', 'tt-generic', 1, 'tt-login', 1);

-- "Login Search"
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-logincontainer', 'Login Search');
INSERT INTO TopicProp VALUES ('tt-logincontainer', 1, 'Name', 'Login Search');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-153', '', 'tt-topiccontainer', 1, 'tt-logincontainer', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-154', '', 'tt-login', 1, 'tt-logincontainer', 1);
