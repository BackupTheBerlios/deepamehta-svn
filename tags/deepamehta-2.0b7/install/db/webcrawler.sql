-------------------
--- Topic Types ---
-------------------



--- "CorporateWeb Settings" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-cwatopic','CorporateWeb Settings');
INSERT INTO TopicProp VALUES ('tt-cwatopic', 1, 'Name', 'CorporateWeb Settings');
INSERT INTO TopicProp VALUES ('tt-cwatopic', 1, 'Icon', 'webpage.gif');
INSERT INTO TopicProp VALUES ('tt-cwatopic', 1, 'Unique Topic Names', 'on');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-130', '', 'tt-cwatopic', 1, 'pp-cwa_www_root_directory', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-131', '', 'tt-cwatopic', 1, 'pp-cwa_baseurl', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-132', '', 'tt-cwatopic', 1, 'pp-smptpserver', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-133', '', 'tt-cwatopic', 1, 'pp-googlekey', 1);
-- ### default property values?
-- super type
-- INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-208', '', 'tt-generic', 1, 'tt-cwatopic', 1);

--- "Webcrawler-Job" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-webcrawlerjob', 'Webcrawler-Job');
INSERT INTO TopicProp VALUES ('tt-webcrawlerjob', 1, 'Name', 'Webcrawler-Job');
INSERT INTO TopicProp VALUES ('tt-webcrawlerjob', 1, 'Plural Name', 'Webcrawler-Jobs');
INSERT INTO TopicProp VALUES ('tt-webcrawlerjob', 1, 'Icon', 'webcrawlerjob.gif');
INSERT INTO TopicProp VALUES ('tt-webcrawlerjob', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-webcrawlerjob', 1, 'Custom Implementation', 'de.deepamehta.topics.WebcrawlerJobTopic');
-- assign properties
-- Note: pp-status is defined in email.sql
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-221', '', 'tt-webcrawlerjob', 1, 'pp-starturl', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-222', '', 'tt-webcrawlerjob', 1, 'pp-status', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-223', '', 'tt-webcrawlerjob', 1, 'pp-settings', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-224', '', 'tt-webcrawlerjob', 1, 'pp-statistics', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-225', '', 'tt-webcrawlerjob', 1, 'pp-downloading', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-226', '', 'tt-webcrawlerjob', 1, 'pp-parsing', 1);
INSERT INTO AssociationProp VALUES ('a-221', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-223', 1, 'Ordinal Number', '2');
INSERT INTO AssociationProp VALUES ('a-222', 1, 'Ordinal Number', '3');
INSERT INTO AssociationProp VALUES ('a-224', 1, 'Ordinal Number', '4');
INSERT INTO AssociationProp VALUES ('a-225', 1, 'Ordinal Number', '5');
INSERT INTO AssociationProp VALUES ('a-226', 1, 'Ordinal Number', '6');
-- super type
-- INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-218', '', 'tt-generic', 1, 'tt-webcrawlerjob', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-webcrawlerjobcontainer', 'Webcrawler-Job Search');
INSERT INTO TopicProp VALUES ('tt-webcrawlerjobcontainer', 1, 'Name', 'Webcrawler-Job Search');
INSERT INTO TopicProp VALUES ('tt-webcrawlerjobcontainer', 1, 'Icon', 'webcrawlerjobcontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-219', '', 'tt-topiccontainer', 1, 'tt-webcrawlerjobcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-220', '', 'tt-webcrawlerjobcontainer', 1, 'tt-webcrawlerjob', 1);



-------------------------
--- Association Types ---
-------------------------



--- "webcrawler" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-webcrawler', 'webcrawler');
INSERT INTO TopicProp VALUES ('at-webcrawler', 1, 'Name', 'webcrawler');
INSERT INTO TopicProp VALUES ('at-webcrawler', 1, 'Color', '#004080');



------------------
--- Properties ---
------------------



-- Webcrawler-Job
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-starturl', 'Start URL');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-settings', 'Settings');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-statistics', 'Statistics');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-downloading', 'Loading');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-parsing', 'Parsing');

--- CorporateWeb Settings
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-cwa_baseurl', 'Base URL');
INSERT INTO TopicProp VALUES ('pp-cwa_baseurl', 1, 'Name', 'Base URL');
INSERT INTO TopicProp VALUES ('pp-cwa_baseurl', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-cwa_www_root_directory', 'Root Directory');
INSERT INTO TopicProp VALUES ('pp-cwa_www_root_directory', 1, 'Name', 'Root Directory');
INSERT INTO TopicProp VALUES ('pp-cwa_www_root_directory', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-smptpserver', 'SMTP Server');
INSERT INTO TopicProp VALUES ('pp-smptpserver', 1, 'Name', 'SMTP Server');
INSERT INTO TopicProp VALUES ('pp-smptpserver', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-googlekey', 'Google Key');
INSERT INTO TopicProp VALUES ('pp-googlekey', 1, 'Name', 'Google Key');
INSERT INTO TopicProp VALUES ('pp-googlekey', 1, 'Visualization', 'Input Field');



--- default instance ---
INSERT INTO Topic VALUES ('tt-cwatopic', 1, 1, 't-corpwebadm', 'CorporateWeb Settings');
INSERT INTO TopicProp VALUES ('t-corpwebadm', 1, 'Name', 'CorporateWeb Settings');
INSERT INTO ViewTopic VALUES ('t-administrationgroupmap', 1, 't-corpwebadm', 1, 230, 120);
