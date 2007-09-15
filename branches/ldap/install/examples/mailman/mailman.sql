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
-- ListMessage Topic
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-listmessage', 'List Message');
INSERT INTO TopicProp VALUES ('tt-listmessage', 1, 'Name', 'List Message');
INSERT INTO TopicProp VALUES ('tt-listmessage', 1, 'Plural Name', 'List Messages');
INSERT INTO TopicProp VALUES ('tt-listmessage', 1, 'Icon', 'message.gif');
INSERT INTO TopicProp VALUES ('tt-listmessage', 1, 'Unique Topic Names', 'on');
-- ListMessage Association
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-listmessageassoc', 'inlist');
INSERT INTO TopicProp VALUES ('at-listmessageassoc', 1, 'Name', 'In List');
INSERT INTO TopicProp VALUES ('at-listmessageassoc', 1, 'Color', '#0000FF');

------------------
--- Properties ---
------------------
-- Mailman List Topic Properties
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-mailmanurl', 'url');
INSERT INTO TopicProp VALUES ('pp-mailmanurl', 1, 'Name', 'URL');
INSERT INTO TopicProp VALUES ('pp-mailmanurl', 1, 'Visualization', 'Input Field');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-mailmanlisturl', '', 'tt-mailmanlist', 1, 'pp-mailmanurl', 1);
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-mailmanusername', 'username');
INSERT INTO TopicProp VALUES ('pp-mailmanusername', 1, 'Name', 'Username');
INSERT INTO TopicProp VALUES ('pp-mailmanusername', 1, 'Visualization', 'Input Field');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-mailmanlistusername', '', 'tt-mailmanlist', 1, 'pp-mailmanusername', 1);
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-mailmanpassword', 'password');
INSERT INTO TopicProp VALUES ('pp-mailmanpassword', 1, 'Name', 'Password');
INSERT INTO TopicProp VALUES ('pp-mailmanpassword', 1, 'Visualization', 'Input Field');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-mailmanlistpassword', '', 'tt-mailmanlist', 1, 'pp-mailmanpassword', 1);
-- Message Topic Properties
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-listmessagecontent', 'content');
INSERT INTO TopicProp VALUES ('pp-listmessagecontent', 1, 'Name', 'Content');
INSERT INTO TopicProp VALUES ('pp-listmessagecontent', 1, 'Visualization', 'Text Editor');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-listmessagecontent', '', 'tt-listmessage', 1, 'pp-listmessagecontent', 1);
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-listmessagesubject', 'subject');
INSERT INTO TopicProp VALUES ('pp-listmessagesubject', 1, 'Name', 'Subject');
INSERT INTO TopicProp VALUES ('pp-listmessagesubject', 1, 'Visualization', 'Input Field');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-listmessagesubject', '', 'tt-listmessage', 1, 'pp-listmessagesubject', 1);

--INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-listmessage', '');
--INSERT INTO TopicProp VALUES ('pp-listmessage', 1, 'Name', '');
--INSERT INTO TopicProp VALUES ('pp-listmessage', 1, 'Visualization', 'Input Field');
--INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-mailman-listmessage', '', 'tt-listmessage', 1, 'pp-listmessage', 1);

---------------------
--- Example Topic ---
---------------------
INSERT INTO Topic VALUES ('tt-mailmanlist', 1, 1, 't-deepamehtadevelopermailmanlist', 'DeepaMehta Developer List');
INSERT INTO TopicProp VALUES ('t-deepamehtadevelopermailmanlist', 1, 'Name', 'DeepaMehta Developer List');
INSERT INTO TopicProp VALUES ('t-deepamehtadevelopermailmanlist', 1, 'url', 'https://lists.berlios.de/pipermail/deepamehta-devel/');

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
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'pp-mailmanurl', 1, 550, 50);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-mailmanlisturl', 1);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'pp-mailmanpassword', 1, 600, 50);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-mailmanlistpassword', 1);
-- Message Topic
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'tt-listmessage', 1, 400, 100);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'pp-listmessagecontent', 1, 400, 50);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-listmessagecontent', 1);
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'pp-listmessagesubject', 1, 350, 50);
INSERT INTO ViewAssociation VALUES ('t-mailmanmap', 1, 'a-mailman-listmessagesubject', 1);
-- ListMessage Association
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 'at-listmessageassoc', 1, 500, 100);
-- Example List
INSERT INTO ViewTopic VALUES ('t-mailmanmap', 1, 't-deepamehtadevelopermailmanlist', 1, 100, 100);

