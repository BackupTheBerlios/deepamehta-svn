-- "WebBuilderLogin" topic type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-wblogin', 'WebBuilderLogin');
INSERT INTO TopicProp VALUES ('tt-wblogin', 1, 'Name', 'WebBuilderLogin');
INSERT INTO TopicProp VALUES ('tt-wblogin', 1, 'Unique Topic Names', 'on');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-158', '', 'tt-login', 1, 'tt-wblogin', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-159', '', 'tt-wblogin', 1, 'pp-profileelementtype', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-160', '', 'tt-wblogin', 1, 'pp-usernameattr', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-161', '', 'tt-wblogin', 1, 'pp-passwordattr', 1);

-- "WebBuilderLogin Search"
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-wblogincontainer', 'WebBuilderLogin Search');
INSERT INTO TopicProp VALUES ('tt-wblogincontainer', 1, 'Name', 'WebBuilderLogin Search');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-164', '', 'tt-topiccontainer', 1, 'tt-wblogincontainer', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-165', '', 'tt-wblogin', 1, 'tt-wblogincontainer', 1);

-- "WebBuilderLogin" topic
INSERT INTO Topic VALUES ('tt-wblogin', 1, 1, 't-wblogin', 'Login auf Production');
INSERT INTO TopicProp VALUES ('t-wblogin', 1, 'Name', 'Login auf Production');
INSERT INTO TopicProp VALUES ('t-wblogin', 1, 'Profile Elementtype', 'user_profile');
INSERT INTO TopicProp VALUES ('t-wblogin', 1, 'Username Attribute', 'login');
INSERT INTO TopicProp VALUES ('t-wblogin', 1, 'Password Attribute', 'password');

INSERT INTO ViewTopic VALUES ('t-directoriesmap', 1, 't-wblogin', 1, 300, 150);

-- create new DataSourceTopic for sample WebBuilder system
INSERT INTO Topic VALUES ('tt-datasource', 1, 1, 't-xbuilderdb', 'XBuilder auf Production');
INSERT INTO TopicProp VALUES ('t-xbuilderdb', 1, 'Name', 'XBuilder auf Production');
INSERT INTO TopicProp VALUES ('t-xbuilderdb', 1, 'Driver', 'oracle.jdbc.driver.OracleDriver');
INSERT INTO TopicProp VALUES ('t-xbuilderdb', 1, 'URL', 'jdbc:oracle:thin:www1/www1@production:1521:stapstst');

-- associate DataSourceTopic and WebBuilderLogin
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-172', '', 't-wblogin', 1, 't-xbuilderdb', 1);

-- make DataSourceTopic and association visible
INSERT INTO ViewTopic VALUES ('t-directoriesmap', 1, 't-xbuilderdb', 1, 400, 150);
INSERT INTO ViewAssociation VALUES ('t-directoriesmap', 1, 'a-172', 1);

-- write comments in "description" of WebBuilderLogin
INSERT INTO TopicProp VALUES ('t-wblogin', 1, 'Description', 'This topic must be associated with a Datasource which provides a connection to a running WebBuilder system. The association must be of type "association" and directed from WebBuilderLogin to DataSource.');

