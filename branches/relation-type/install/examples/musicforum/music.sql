---------------------------
--- Application "Music" ---
---------------------------



-- workspace "Music"
INSERT INTO Topic VALUES ('tt-workspace', 1, 1, 't-musicworkgroup', 'Music');
INSERT INTO TopicProp VALUES ('t-musicworkgroup', 1, 'Name', 'Music');
INSERT INTO TopicProp VALUES ('t-musicworkgroup', 1, 'Public', 'on');
INSERT INTO TopicProp VALUES ('t-musicworkgroup', 1, 'Default', 'off');
-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-musicworkspace', 'Music');
INSERT INTO TopicProp VALUES ('t-musicworkspace', 1, 'Name', 'Music');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-mus-1', '', 't-musicworkgroup', 1, 't-musicworkspace', 1);
-- chat
INSERT INTO Topic VALUES ('tt-chatboard', 1, 1, 't-musicchat', 'Music Chats');
INSERT INTO TopicProp VALUES ('t-musicchat', 1, 'Name', 'Music Chats');
INSERT INTO ViewTopic VALUES ('t-musicworkspace', 1, 't-musicchat', 1, 200, 100);
-- forum
INSERT INTO Topic VALUES ('tt-messageboard', 1, 1, 't-musicforum', 'Music Forum');
INSERT INTO TopicProp VALUES ('t-musicforum', 1, 'Name', 'Music Forum');
INSERT INTO ViewTopic VALUES ('t-musicworkspace', 1, 't-musicforum', 1, 200, 50);

-- webpage
INSERT INTO Topic VALUES ('tt-webpage', 1, 1, 't-musicwebpage', 'www.jrimixes.de');
INSERT INTO TopicProp VALUES ('t-musicwebpage', 1, 'Name', 'www.jrimixes.de');
INSERT INTO TopicProp VALUES ('t-musicwebpage', 1, 'URL', 'http://www.jrimixes.de/');
-- website
INSERT INTO Topic VALUES ('tt-website', 1, 1, 't-musicwebsite', 'www.jrimixes.de');
INSERT INTO TopicProp VALUES ('t-musicwebsite', 1, 'Name', 'www.jrimixes.de');
-- domain
INSERT INTO Topic VALUES ('tt-internetdomain', 1, 1, 't-musicdomain', 'jrimixes.de');
INSERT INTO TopicProp VALUES ('t-musicdomain', 1, 'Name', 'jrimixes.de');
-- associate forum with webpage
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-mus-2', '', 't-musicforum', 1, 't-musicwebpage', 1);
-- associate webpage with website
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-mus-3', '', 't-musicwebpage', 1, 't-musicwebsite', 1);
-- associate website with domain
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-mus-4', '', 't-musicwebsite', 1, 't-musicdomain', 1);
