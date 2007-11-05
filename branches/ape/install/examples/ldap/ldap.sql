------------------------
--- "LDAP" workspace ---
------------------------



INSERT INTO Topic VALUES ('tt-workspace', 1, 1, 't-ldapgroup', 'LDAP');
INSERT INTO TopicProp VALUES ('t-ldapgroup', 1, 'Name', 'LDAP');
INSERT INTO TopicProp VALUES ('t-ldapgroup', 1, 'Public', 'on');
INSERT INTO TopicProp VALUES ('t-ldapgroup', 1, 'Default', 'off');
-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-ldapworkspace', 'LDAP');
INSERT INTO TopicProp VALUES ('t-ldapworkspace', 1, 'Name', 'LDAP');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-ad-1', '', 't-ldapgroup', 1, 't-ldapworkspace', 1);
-- chat
INSERT INTO Topic VALUES ('tt-chatboard', 1, 1, 't-ldapchat', 'LDAP Chats');
INSERT INTO TopicProp VALUES ('t-ldapchat', 1, 'Name', 'LDAP Chats');
INSERT INTO ViewTopic VALUES ('t-ldapworkspace', 1, 't-ldapchat', 1, 80, 40);
-- forum
INSERT INTO Topic VALUES ('tt-messageboard', 1, 1, 't-ldapforum', 'LDAP Forum');
INSERT INTO TopicProp VALUES ('t-ldapforum', 1, 'Name', 'LDAP Forum');
INSERT INTO ViewTopic VALUES ('t-ldapworkspace', 1, 't-ldapforum', 1, 100, 100);
-- assign types
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-ad-2', '', 't-ldapgroup', 1, 'tt-ldapuser', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-ad-3', '', 't-ldapgroup', 1, 'tt-ldapgroup', 1);
INSERT INTO AssociationProp VALUES ('a-ad-2', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-ad-3', 1, 'Access Permission', 'create');



-------------------
--- Topic Types ---
-------------------



INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-ldapuser', 'LDAP User');
INSERT INTO TopicProp VALUES ('tt-ldapuser', 1, 'Name', 'LDAP User');
INSERT INTO TopicProp VALUES ('tt-ldapuser', 1, 'Plural Name', 'LDAP Users');
INSERT INTO TopicProp VALUES ('tt-ldapuser', 1, 'Icon', 'ldapuser.gif');
INSERT INTO TopicProp VALUES ('tt-ldapuser', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-ldapuser', 1, 'Custom Implementation', 'de.deepamehta.topics.LdapUserTopic');

INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-ldapgroup', 'LDAP Group');
INSERT INTO TopicProp VALUES ('tt-ldapgroup', 1, 'Name', 'LDAP Group');
INSERT INTO TopicProp VALUES ('tt-ldapgroup', 1, 'Plural Name', 'LDAP Groups');
INSERT INTO TopicProp VALUES ('tt-ldapgroup', 1, 'Icon', 'ldapgroup.gif');
INSERT INTO TopicProp VALUES ('tt-ldapgroup', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-ldapgroup', 1, 'Custom Implementation', 'de.deepamehta.topics.LdapGroupTopic');

-- container types
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-ldapusercontainer', 'LDAP User Search');
INSERT INTO TopicProp VALUES ('tt-ldapusercontainer', 1, 'Name', 'LDAP User Search');
INSERT INTO TopicProp VALUES ('tt-ldapusercontainer', 1, 'Icon', 'ldapusercontainer.gif');
INSERT INTO TopicProp VALUES ('tt-ldapusercontainer', 1, 'Custom Implementation', 'de.deepamehta.topics.LdapUserContainerTopic');

INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-ldapgroupcontainer', 'LDAP Group Search');
INSERT INTO TopicProp VALUES ('tt-ldapgroupcontainer', 1, 'Name', 'LDAP Group Search');
INSERT INTO TopicProp VALUES ('tt-ldapgroupcontainer', 1, 'Icon', 'ldapgroupcontainer.gif');
INSERT INTO TopicProp VALUES ('tt-ldapgroupcontainer', 1, 'Custom Implementation', 'de.deepamehta.topics.LdapGroupContainerTopic');

-- property associations
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-membergroup', '', 'tt-ldapgroup', 1, 'pp-member', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-descriptiongroup', '', 'tt-ldapgroup', 1, 'pp-description', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-descriptiongroupcontainer', '', 'tt-ldapgroupcontainer', 1, 'pp-description', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-descriptionuser', '', 'tt-ldapuser', 1, 'pp-description', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-descriptionusercontainer', '', 'tt-ldapusercontainer', 1, 'pp-description', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-namegroup', '', 'tt-ldapgroup', 1, 'pp-namesmall', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-namegroupcontainer', '', 'tt-ldapgroupcontainer', 1, 'pp-namesmall', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-nameuser', '', 'tt-ldapuser', 1, 'pp-namesmall', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-nameusercontainer', '', 'tt-ldapusercontainer', 1, 'pp-namesmall', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-principaluser', '', 'tt-ldapuser', 1, 'pp-userprincipalname', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-principalusercontainer', '', 'tt-ldapusercontainer', 1, 'pp-userprincipalname', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-memberofuser', '', 'tt-ldapuser', 1, 'pp-memberof', 1);

-- associations between document types and container types
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-ad-user-usercontassoc', '', 'tt-ldapusercontainer', 1, 'tt-ldapuser', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-ad-group-groupcontassoc', '', 'tt-ldapgroupcontainer', 1, 'tt-ldapgroup', 1);

-- associations to "ActiveDirectory" datasource
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-ad-userassoc', '', 'tt-ldapuser', 1, 't-activedirectory', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-ad-groupassoc', '', 'tt-ldapgroup', 1, 't-activedirectory', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-ad-usercontassoc', '', 'tt-ldapusercontainer', 1, 't-activedirectory', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-ad-groupcontassoc', '', 'tt-ldapgroupcontainer', 1, 't-activedirectory', 1);



-------------------------
--- Association Types ---
-------------------------



INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-ldapmember', 'LDAP member');
INSERT INTO TopicProp VALUES ('at-ldapmember', 1, 'Name', 'LDAP member');
INSERT INTO TopicProp VALUES ('at-ldapmember', 1, 'Color', '#FFFF00');

INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-ldapmemberof', 'LDAP member of');
INSERT INTO TopicProp VALUES ('at-ldapmemberof', 1, 'Name', 'LDAP member of');
INSERT INTO TopicProp VALUES ('at-ldapmemberof', 1, 'Color', '#FFAA00');



------------------
--- Properties ---
------------------



INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-member', 'member');
INSERT INTO TopicProp VALUES ('pp-member', 1, 'Name', 'member');
INSERT INTO TopicProp VALUES ('pp-member', 1, 'Visualization', 'Multiline Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-memberof', 'memberOf');
INSERT INTO TopicProp VALUES ('pp-memberof', 1, 'Name', 'memberOf');
INSERT INTO TopicProp VALUES ('pp-memberof', 1, 'Visualization', 'Multiline Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-namesmall', 'name');
INSERT INTO TopicProp VALUES ('pp-namesmall', 1, 'Name', 'name');
INSERT INTO TopicProp VALUES ('pp-namesmall', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-description', 'description');
INSERT INTO TopicProp VALUES ('pp-description', 1, 'Name', 'description');
INSERT INTO TopicProp VALUES ('pp-description', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-userprincipalname', 'userPrincipalName');
INSERT INTO TopicProp VALUES ('pp-userprincipalname', 1, 'Name', 'userPrincipalName');
INSERT INTO TopicProp VALUES ('pp-userprincipalname', 1, 'Visualization', 'Input Field');



------------------
--- Datasource ---
------------------



INSERT INTO Topic VALUES ('tt-datasource', 1, 1, 't-activedirectory', 'ActiveDirectory (LDAP-Source)');
INSERT INTO TopicProp VALUES ('t-activedirectory', 1, 'Name', 'ActiveDirectory (LDAP-Source)');
INSERT INTO TopicProp VALUES ('t-activedirectory', 1, 'URL', 'ldap://192.168.251.145:389?login=olaf@staps2000.local&password=olaf&baseDN=DC=staps2000,DC=local&searchScope=SUBTREE_SCOPE');



--------------------
--- Example View ---
--------------------



INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-activedirectorymap', 'ActiveDirectory');
INSERT INTO TopicProp VALUES ('t-activedirectorymap', 1, 'Name', 'ActiveDirectory');
INSERT INTO ViewTopic VALUES ('t-ldapworkspace', 1, 't-activedirectorymap', 1, 300, 100);
