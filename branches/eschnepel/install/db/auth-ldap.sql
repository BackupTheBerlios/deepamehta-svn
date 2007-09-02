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
