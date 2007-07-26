----------------------
--- LDAP workspace ---
----------------------

INSERT INTO Topic VALUES ('tt-workspace', 1, 1, 't-ldapworkspace', 'LDAP');
INSERT INTO TopicProp VALUES ('t-ldapworkspace', 1, 'Name', 'LDAP');
INSERT INTO TopicProp VALUES ('t-ldapworkspace', 1, 'Public', 'on');
INSERT INTO TopicProp VALUES ('t-ldapworkspace', 1, 'Default', 'off');
INSERT INTO ViewTopic VALUES ('t-workgroupmap', 1, 't-ldapworkspace', 1, 100, 100);
INSERT INTO Association VALUES ('at-membership', 1, 1, 'a-ad-rootuserldapworkspace', '', 't-rootuser', 1, 't-ldapworkspace', 1);

-- assign types
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-ad-ldapworkspaceuser', '', 't-ldapworkspace', 1, 'tt-ldapuser', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-ad-ldapworkspacegroup', '', 't-ldapworkspace', 1, 'tt-ldapgroup', 1);
INSERT INTO AssociationProp VALUES ('a-ad-ldapworkspaceuser', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-ad-ldapworkspacegroup', 1, 'Access Permission', 'create');

-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-ldaptopicmap', 'LDAP');
INSERT INTO TopicProp VALUES ('t-ldaptopicmap', 1, 'Name', 'LDAP');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-ad-1', '', 't-ldapworkspace', 1, 't-ldaptopicmap', 1);

-- chat
INSERT INTO Topic VALUES ('tt-chatboard', 1, 1, 't-ldapchat', 'LDAP Chats');
INSERT INTO TopicProp VALUES ('t-ldapchat', 1, 'Name', 'LDAP Chats');
INSERT INTO ViewTopic VALUES ('t-ldaptopicmap', 1, 't-ldapchat', 1, 200, 100);

-- forum
INSERT INTO Topic VALUES ('tt-messageboard', 1, 1, 't-ldapforum', 'LDAP Forum');
INSERT INTO TopicProp VALUES ('t-ldapforum', 1, 'Name', 'LDAP Forum');
INSERT INTO ViewTopic VALUES ('t-ldaptopicmap', 1, 't-ldapforum', 1, 200, 200);

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
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-cngroup', '', 'tt-ldapgroup', 1, 'pp-cn', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-descriptiongroup', '', 'tt-ldapgroup', 1, 'pp-description', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-descriptionuser', '', 'tt-ldapuser', 1, 'pp-description', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-givennameuser', '', 'tt-ldapuser', 1, 'pp-givenname', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-membergroup', '', 'tt-ldapgroup', 1, 'pp-member', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-memberofuser', '', 'tt-ldapuser', 1, 'pp-memberof', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-ougroup', '', 'tt-ldapgroup', 1, 'pp-ou', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-ougroupcontainer', '', 'tt-ldapgroupcontainer', 1, 'pp-ou', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-snuser', '', 'tt-ldapuser', 1, 'pp-sn', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-uiduser', '', 'tt-ldapuser', 1, 'pp-uid', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ad-uidusercontainer', '', 'tt-ldapusercontainer', 1, 'pp-uid', 1);

-- associations between document types and container types
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-ad-user-usercontassoc', '', 'tt-ldapusercontainer', 1, 'tt-ldapuser', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-ad-group-groupcontassoc', '', 'tt-ldapgroupcontainer', 1, 'tt-ldapgroup', 1);

-- associations to LDAP datasource
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-ad-userassoc', '', 'tt-ldapuser', 1, 't-ldapdatasource', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-ad-groupassoc', '', 'tt-ldapgroup', 1, 't-ldapdatasource', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-ad-usercontassoc', '', 'tt-ldapusercontainer', 1, 't-ldapdatasource', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-ad-groupcontassoc', '', 'tt-ldapgroupcontainer', 1, 't-ldapdatasource', 1);

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

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-cn', 'cn');
INSERT INTO TopicProp VALUES ('pp-cn', 1, 'Name', 'Common Name');
INSERT INTO TopicProp VALUES ('pp-cn', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-description', 'description');
INSERT INTO TopicProp VALUES ('pp-description', 1, 'Name', 'Description');
INSERT INTO TopicProp VALUES ('pp-description', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-givenname', 'givenName');
INSERT INTO TopicProp VALUES ('pp-givenname', 1, 'Name', 'Given Name');
INSERT INTO TopicProp VALUES ('pp-givenname', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-member', 'member');
INSERT INTO TopicProp VALUES ('pp-member', 1, 'Name', 'Member');
INSERT INTO TopicProp VALUES ('pp-member', 1, 'Visualization', 'Multiline Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-memberof', 'memberOf');
INSERT INTO TopicProp VALUES ('pp-memberof', 1, 'Name', 'Member Of');
INSERT INTO TopicProp VALUES ('pp-memberof', 1, 'Visualization', 'Multiline Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-ou', 'ou');
INSERT INTO TopicProp VALUES ('pp-ou', 1, 'Name', 'Organisational Unit');
INSERT INTO TopicProp VALUES ('pp-ou', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-sn', 'sn');
INSERT INTO TopicProp VALUES ('pp-sn', 1, 'Name', 'Surname');
INSERT INTO TopicProp VALUES ('pp-sn', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-uid', 'uid');
INSERT INTO TopicProp VALUES ('pp-uid', 1, 'Name', 'User Identifier');
INSERT INTO TopicProp VALUES ('pp-uid', 1, 'Visualization', 'Input Field');

------------------
--- Datasource ---
------------------

INSERT INTO Topic VALUES ('tt-datasource', 1, 1, 't-ldapdatasource', 'LDAP-Source');
INSERT INTO TopicProp VALUES ('t-ldapdatasource', 1, 'Name', 'LDAP-Source');
INSERT INTO TopicProp VALUES ('t-ldapdatasource', 1, 'URL', 'ldap://localhost?login=ou=dm,ou=services,dc=deepamehta,dc=de&password=secret&base=dc=deepamehta,dc=de&scope=SUBTREE_SCOPE');
INSERT INTO TopicProp VALUES ('t-ldapdatasource', 1, 'Driver', 'com.sun.jndi.ldap.LdapCtxFactory');
INSERT INTO Topic VALUES ('tt-webpage', 1, 1, 't-openldapwebpage', 'OpenLDAP');
INSERT INTO TopicProp VALUES ('t-openldapwebpage', 1, 'Name', 'OpenLDAP');
INSERT INTO TopicProp VALUES ('t-openldapwebpage', 1, 'URL', 'http://www.openldap.org/');
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-ad-openldapdatasource', '', 't-openldapwebpage', 1, 't-ldapdatasource', 1);

--------------------
--- Workpad View ---
--------------------

INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-ldapmap', 'LDAP-Directory Map');
INSERT INTO TopicProp VALUES ('t-ldapmap', 1, 'Name', 'LDAP-Directory Map');
INSERT INTO ViewTopic VALUES ('t-ldaptopicmap', 1, 't-ldapmap', 1, 300, 100);

INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 't-openldapwebpage', 1, 300, 50);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 't-ldapdatasource', 1, 300, 100);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-groupassoc', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-groupcontassoc', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-openldapdatasource', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-userassoc', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-usercontassoc', 1);

INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'at-ldapmember', 1, 100, 300);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'pp-ou', 1, 100, 150);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'pp-cn', 1, 100, 200);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'pp-member', 1, 100, 250);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'tt-ldapgroup', 1, 225, 200);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'tt-ldapgroupcontainer', 1, 225, 150);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-cngroup', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-group-groupcontassoc', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-membergroup', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-ougroup', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-ougroupcontainer', 1);

INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'at-ldapmemberof', 1, 500, 350);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'pp-givenname', 1, 500, 200);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'pp-sn', 1, 500, 250);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'pp-memberof', 1, 500, 300);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'pp-uid', 1, 500, 150);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'tt-ldapuser', 1, 375, 200);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 'tt-ldapusercontainer', 1, 375, 150);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-givennameuser', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-memberofuser', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-snuser', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-user-usercontassoc', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-uiduser', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-uidusercontainer', 1);

INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 't-ldapworkspace', 1, 300, 250);
INSERT INTO ViewTopic VALUES ('t-ldapmap', 1, 't-rootuser', 1, 300, 300);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-ldapworkspacegroup', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-ldapworkspaceuser', 1);
INSERT INTO ViewAssociation VALUES ('t-ldapmap', 1, 'a-ad-rootuserldapworkspace', 1);
