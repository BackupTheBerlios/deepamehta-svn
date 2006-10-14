

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

-- write comments in "description" of WebBuilderLogin and AuthentificationSource
INSERT INTO TopicProp VALUES ('t-useraccounts', 1, 'Description', 'This topic must be associated with one of the Login topics. The association must be of type "association" and directed from AuthentificationSource to Login. If there is no such association, the CorporateMemory will be used for authentification.');
INSERT INTO TopicProp VALUES ('t-wblogin', 1, 'Description', 'This topic must be associated with a Datasource which provides a connection to a running WebBuilder system. The association must be of type "association" and directed from WebBuilderLogin to DataSource.');




-- "Domain Name" property
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-domainname', 'Domain Name');
INSERT INTO TopicProp VALUES ('pp-domainname', 1, 'Name', 'Domain Name');

-- "ActiveDirectoryLogin" topic type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-adlogin', 'ActiveDirectoryLogin');
INSERT INTO TopicProp VALUES ('tt-adlogin', 1, 'Name', 'ActiveDirectoryLogin');
INSERT INTO TopicProp VALUES ('tt-adlogin', 1, 'Unique Topic Names', 'on');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-166', '', 'tt-login', 1, 'tt-adlogin', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-167', '', 'tt-adlogin', 1, 'pp-url', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-168', '', 'tt-adlogin', 1, 'pp-domainname', 1);

-- "ActiveDirectoryLogin Search"
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-adlogincontainer', 'ActiveDirectoryLogin Search');
INSERT INTO TopicProp VALUES ('tt-adlogincontainer', 1, 'Name', 'ActiveDirectoryLogin Search');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-169', '', 'tt-topiccontainer', 1, 'tt-adlogincontainer', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-170', '', 'tt-adlogin', 1, 'tt-adlogincontainer', 1);

-- "ActiveDirectoryLogin" topic
INSERT INTO Topic VALUES ('tt-adlogin', 1, 1, 't-adlogin', 'ActiveDirectory auf Onyx');
INSERT INTO TopicProp VALUES ('t-adlogin', 1, 'Name', 'ActiveDirectory auf Onyx');
INSERT INTO TopicProp VALUES ('t-adlogin', 1, 'URL', 'ldap://192.168.251.145:389');
INSERT INTO TopicProp VALUES ('t-adlogin', 1, 'Domain Name', 'staps2000.local');

INSERT INTO ViewTopic VALUES ('t-directoriesmap', 1, 't-adlogin', 1, 300, 200);
