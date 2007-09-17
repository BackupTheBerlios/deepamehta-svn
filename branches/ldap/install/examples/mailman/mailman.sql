-------------------------
--- Mailman workspace ---
-------------------------
INSERT INTO Topic VALUES ('tt-workspace', 1, 1, 't-mailmanworkspace', 'Mailman');
INSERT INTO TopicProp VALUES ('t-ldapworkspace', 1, 'Name', 'Mailman');
INSERT INTO TopicProp VALUES ('t-ldapworkspace', 1, 'Public', 'on');
INSERT INTO TopicProp VALUES ('t-ldapworkspace', 1, 'Default', 'off');
INSERT INTO Association VALUES ('at-membership', 1, 1, 'a-mailman-rootusermailmanworkspace', '', 't-rootuser', 1, 't-mailmanworkspace', 1);

-- TODO was ist die "workgroupmap"? bzw. wozu wird der Workspace dieser hinzugefügt?
INSERT INTO ViewTopic VALUES ('t-workgroupmap', 1, 't-mailmanworkspace', 1, 200, 200);

-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-mailmantopicmap', 'Mailman');
INSERT INTO TopicProp VALUES ('t-mailmantopicmap', 1, 'Name', 'Mailman');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-mailman-workspacetopicmap', '', 't-mailmanworkspace', 1, 't-mailmantopicmap', 1);

-- chat
INSERT INTO Topic VALUES ('tt-chatboard', 1, 1, 't-mailmanchat', 'Mailman Chats');
INSERT INTO TopicProp VALUES ('t-mailmanchat', 1, 'Name', 'Mailman Chats');
INSERT INTO ViewTopic VALUES ('t-mailmantopicmap', 1, 't-mailmanchat', 1, 400, 50);

-- forum
INSERT INTO Topic VALUES ('tt-messageboard', 1, 1, 't-mailmanforum', 'Mailman Forum');
INSERT INTO TopicProp VALUES ('t-mailmanforum', 1, 'Name', 'Mailman Forum');
INSERT INTO ViewTopic VALUES ('t-mailmantopicmap', 1, 't-mailmanforum', 1, 400, 100);

-------------------
--- Topic Types ---
-------------------

-- Mailman List Topic
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-mailmanlist', 'Mailman List');
INSERT INTO TopicProp VALUES ('tt-mailmanlist', 1, 'Name', 'Mailman List');
INSERT INTO TopicProp VALUES ('tt-mailmanlist', 1, 'Plural Name', 'Mailman Lists');
INSERT INTO TopicProp VALUES ('tt-mailmanlist', 1, 'Icon', 'category.gif');
INSERT INTO TopicProp VALUES ('tt-mailmanlist', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-mailmanlist', 1, 'Custom Implementation', 'de.deepamehta.topics.MailmanTopic');

INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-mailmanlistcontainer', 'Mailman List Search');
INSERT INTO TopicProp VALUES ('tt-mailmanlistcontainer', 1, 'Name', 'Mailman List Search');
INSERT INTO TopicProp VALUES ('tt-mailmanlistcontainer', 1, 'Icon', 'websearchcontainer.gif');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-mailman-mailmanlisttopiccontainer', '', 'tt-topiccontainer', 1, 'tt-mailmanlistcontainer', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-mailman-mailmanlistcontainer', '', 'tt-mailmanlistcontainer', 1, 'tt-mailmanlist', 1);

-- List Message Topic
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-listmessage', 'List Message');
INSERT INTO TopicProp VALUES ('tt-listmessage', 1, 'Name', 'List Message');
INSERT INTO TopicProp VALUES ('tt-listmessage', 1, 'Plural Name', 'List Messages');
INSERT INTO TopicProp VALUES ('tt-listmessage', 1, 'Icon', 'message.gif');
INSERT INTO TopicProp VALUES ('tt-listmessage', 1, 'Unique Topic Names', 'off');
INSERT INTO TopicProp VALUES ('tt-listmessage', 1, 'Custom Implementation', 'de.deepamehta.topics.ListMessageTopic');

INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-listmessagecontainer', 'List Message Search');
INSERT INTO TopicProp VALUES ('tt-listmessagecontainer', 1, 'Name', 'List Message Search');
INSERT INTO TopicProp VALUES ('tt-listmessagecontainer', 1, 'Icon', 'websearchcontainer.gif');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-mailman-listmessagetopiccontainer', '', 'tt-topiccontainer', 1, 'tt-listmessagecontainer', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-mailman-listmessagecontainer', '', 'tt-listmessagecontainer', 1, 'tt-listmessage', 1);

-- List Message inlist Mailman List Association Topic and Relation
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-listmessageassociation', 'In List');
INSERT INTO TopicProp VALUES ('at-listmessageassociation', 1, 'Name', 'In List');
INSERT INTO TopicProp VALUES ('at-listmessageassociation', 1, 'Color', '#FFBF00');
INSERT INTO Association VALUES ('at-relation', 1, 1, 'at-listmessagerelation', '', 'tt-mailmanlist', 1, 'tt-listmessage', 1);
INSERT INTO AssociationProp VALUES ('at-listmessagerelation', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('at-listmessagerelation', 1, 'Association Type ID', 'at-listmessageassociation');
INSERT INTO AssociationProp VALUES ('at-listmessagerelation', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('at-listmessagerelation', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('at-listmessagerelation', 1, 'Ordinal Number', '0');

-- List Message inReplyTo List Message Association Topic and Relation
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-listmessageinreplyto', 'In Reply To');
INSERT INTO TopicProp VALUES ('at-listmessageinreplyto', 1, 'Name', 'In Reply To');
INSERT INTO TopicProp VALUES ('at-listmessageinreplyto', 1, 'Color', '#00007F');
INSERT INTO Association VALUES ('at-relation', 1, 1, 'at-listmessageinreplytorelation', '', 'tt-listmessage', 1, 'tt-listmessage', 1);
INSERT INTO AssociationProp VALUES ('at-listmessageinreplytorelation', 1, 'Cardinality', 'one');
INSERT INTO AssociationProp VALUES ('at-listmessageinreplytorelation', 1, 'Association Type ID', 'at-listmessageinreplyto');
INSERT INTO AssociationProp VALUES ('at-listmessageinreplytorelation', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('at-listmessageinreplytorelation', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('at-listmessageinreplytorelation', 1, 'Ordinal Number', '1');

-- List Message inReplyTo List Message Association Topic and Relation
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-listmessagereference', 'Reference');
INSERT INTO TopicProp VALUES ('at-listmessagereference', 1, 'Name', 'Reference');
INSERT INTO TopicProp VALUES ('at-listmessagereference', 1, 'Color', '#9F0000');
INSERT INTO Association VALUES ('at-relation', 1, 1, 'at-listmessagereferencerelation', '', 'tt-listmessage', 1, 'tt-listmessage', 1);
INSERT INTO AssociationProp VALUES ('at-listmessagereferencerelation', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('at-listmessagereferencerelation', 1, 'Association Type ID', 'at-listmessagereference');
INSERT INTO AssociationProp VALUES ('at-listmessagereferencerelation', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('at-listmessagereferencerelation', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('at-listmessagereferencerelation', 1, 'Ordinal Number', '2');

------------------
--- Properties ---
------------------

-- Mailman List Topic Properties
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-mailman-mailmanlistuses', '', 't-corporategroup', 1, 'tt-mailmanlist', 1);
INSERT INTO AssociationProp VALUES ('a-mailman-mailmanlistuses', 1, 'Access Permission', 'create');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-mailman-mailmanlisttopic', '', 'tt-generic', 1, 'tt-mailmanlist', 1);

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-mailmanurl', 'URL');
INSERT INTO TopicProp VALUES ('pp-mailmanurl', 1, 'Name', 'URL');
INSERT INTO TopicProp VALUES ('pp-mailmanurl', 1, 'Visualization', 'Input Field');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-mailmanlisturl', '', 'tt-mailmanlist', 1, 'pp-mailmanurl', 1);

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-mailmanusername', 'Username');
INSERT INTO TopicProp VALUES ('pp-mailmanusername', 1, 'Name', 'Username');
INSERT INTO TopicProp VALUES ('pp-mailmanusername', 1, 'Visualization', 'Input Field');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-mailmanlistusername', '', 'tt-mailmanlist', 1, 'pp-mailmanusername', 1);

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-mailmanpassword', 'Password');
INSERT INTO TopicProp VALUES ('pp-mailmanpassword', 1, 'Name', 'Password');
INSERT INTO TopicProp VALUES ('pp-mailmanpassword', 1, 'Visualization', 'Input Field');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-mailmanlistpassword', '', 'tt-mailmanlist', 1, 'pp-mailmanpassword', 1);

-- List Message Topic Properties
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-mailman-listmessageuses', '', 't-corporategroup', 1, 'tt-listmessage', 1);
INSERT INTO AssociationProp VALUES ('a-mailman-listmessageuses', 1, 'Access Permission', 'create');
--INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-mailman-listmessagetopic', '', 'tt-generic', 1, 'tt-listmessage', 1);

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-listmessagesubject', 'Subject');
INSERT INTO TopicProp VALUES ('pp-listmessagesubject', 1, 'Name', 'Subject');
INSERT INTO TopicProp VALUES ('pp-listmessagesubject', 1, 'Visualization', 'Input Field');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-listmessagesubject', '', 'tt-listmessage', 1, 'pp-listmessagesubject', 1);

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-listmessagedate', 'Date');
INSERT INTO TopicProp VALUES ('pp-listmessagedate', 1, 'Name', 'Date');
INSERT INTO TopicProp VALUES ('pp-listmessagedate', 1, 'Visualization', 'Date Chooser');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-listmessagedate', '', 'tt-listmessage', 1, 'pp-listmessagedate', 1);

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-listmessagetime', 'Time');
INSERT INTO TopicProp VALUES ('pp-listmessagetime', 1, 'Name', 'Time');
INSERT INTO TopicProp VALUES ('pp-listmessagetime', 1, 'Visualization', 'Time Chooser');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-listmessagetime', '', 'tt-listmessage', 1, 'pp-listmessagetime', 1);

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-listmessagecontent', 'Content');
INSERT INTO TopicProp VALUES ('pp-listmessagecontent', 1, 'Name', 'Content');
INSERT INTO TopicProp VALUES ('pp-listmessagecontent', 1, 'Visualization', 'Text Editor');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-listmessagecontent', '', 'tt-listmessage', 1, 'pp-listmessagecontent', 1);


--INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-listmessage', '');
--INSERT INTO TopicProp VALUES ('pp-listmessage', 1, 'Name', '');
--INSERT INTO TopicProp VALUES ('pp-listmessage', 1, 'Visualization', 'Input Field');
--INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-listmessage', '', 'tt-listmessage', 1, 'pp-listmessage', 1);

---------------------
--- Example Topic ---
---------------------
INSERT INTO Topic VALUES ('tt-mailmanlist', 1, 1, 't-deepamehtadevelopermailmanlist', 'DeepaMehta Developer List');
INSERT INTO TopicProp VALUES ('t-deepamehtadevelopermailmanlist', 1, 'Name', 'DeepaMehta Developer List');
INSERT INTO TopicProp VALUES ('t-deepamehtadevelopermailmanlist', 1, 'URL', 'https://lists.berlios.de/pipermail/deepamehta-devel/');

--------------------
--- Workpad View ---
--------------------
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-mailmanmap', 'Mailman Map');
INSERT INTO TopicProp VALUES ('t-mailmanmap', 1, 'Name', 'Mailman Map');
INSERT INTO ViewTopic VALUES ('t-mailmantopicmap', 1, 't-mailmanmap', 1, 100, 100);

-- Mailman List Topic
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'tt-mailmanlist', 1, 550, 100);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'pp-mailmanusername', 1, 500, 50);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-mailmanlistusername', 1);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'pp-mailmanurl', 1, 565, 50);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-mailmanlisturl', 1);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'pp-mailmanpassword', 1, 600, 50);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-mailmanlistpassword', 1);

-- List Message Topic
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'tt-listmessage', 1, 400, 100);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'pp-listmessagesubject', 1, 350, 50);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-listmessagesubject', 1);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'pp-listmessagedate', 1, 400, 50);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-listmessagedate', 1);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'pp-listmessagecontent', 1, 450, 50);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-listmessagecontent', 1);

-- List Message Association
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'at-listmessageassociation', 1, 500, 125);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'at-listmessagerelation', 1);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'at-listmessageinreplyto', 1, 300, 100);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'at-listmessageinreplytorelation', 1);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'at-listmessagereference', 1, 300, 150);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'at-listmessageinreferencerelation', 1);

-- Container Associations
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'tt-listmessagecontainer', 1, 400, 150);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-listmessagecontainer', 1);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'tt-mailmanlistcontainer', 1, 550, 150);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-mailmanlistcontainer', 1);

-- Example List
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 't-deepamehtadevelopermailmanlist', 1, 100, 100);
