---
--- This patch updates CM 2.7 to 2.8
--- Apply this patch if you want to update DeepaMehta 2.0a18 to 2.0b1 while keeping your content
---

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
