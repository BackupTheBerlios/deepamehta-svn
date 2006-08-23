---
--- This patch updates CM 2.7 to 2.8
--- Apply this patch if you want to update DeepaMehta 2.0a18 to 2.0b1 while keeping your content
---

INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-205', '', 't-corporategroup', 1, 'at-relation', 1);
INSERT INTO AssociationProp VALUES ('a-205', 1, 'Access Permission', 'create');

INSERT INTO AssociationProp VALUES ('a-185', 1, 'Ordinal Number', '01');
INSERT INTO AssociationProp VALUES ('a-246', 1, 'Ordinal Number', '02');
INSERT INTO AssociationProp VALUES ('a-192', 1, 'Ordinal Number', '03');
INSERT INTO AssociationProp VALUES ('a-189', 1, 'Ordinal Number', '04');
INSERT INTO AssociationProp VALUES ('a-188', 1, 'Ordinal Number', '05');
INSERT INTO AssociationProp VALUES ('a-186', 1, 'Ordinal Number', '06');
INSERT INTO AssociationProp VALUES ('a-181', 1, 'Ordinal Number', '07');
INSERT INTO AssociationProp VALUES ('a-191', 1, 'Ordinal Number', '08');
INSERT INTO AssociationProp VALUES ('a-326', 1, 'Ordinal Number', '09');
INSERT INTO AssociationProp VALUES ('a-324', 1, 'Ordinal Number', '10');
INSERT INTO AssociationProp VALUES ('a-325', 1, 'Ordinal Number', '11');

-- webpage
INSERT INTO Topic VALUES ('tt-webpage', 1, 1, 't-deepamehtawebpage', 'DeepaMehta -- Homepage');
INSERT INTO TopicProp VALUES ('t-deepamehtawebpage', 1, 'Name', 'DeepaMehta -- Homepage');
INSERT INTO TopicProp VALUES ('t-deepamehtawebpage', 1, 'URL', 'http://www.deepamehta.de/');
-- website
INSERT INTO Topic VALUES ('tt-website', 1, 1, 't-deepamehtawebsite', 'www.deepamehta.de');
INSERT INTO TopicProp VALUES ('t-deepamehtawebsite', 1, 'Name', 'www.deepamehta.de');
-- domain
INSERT INTO Topic VALUES ('tt-internetdomain', 1, 1, 't-deepamehtadomain', 'deepamehta.de');
INSERT INTO TopicProp VALUES ('t-deepamehtadomain', 1, 'Name', 'deepamehta.de');
-- associate forum with webpage
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-269', '', 't-deepamehtaforum', 1, 't-deepamehtawebpage', 1);
-- associate webpage with website
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-206', '', 't-deepamehtawebpage', 1, 't-deepamehtawebsite', 1);
-- associate website with domain
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-322', '', 't-deepamehtawebsite', 1, 't-deepamehtadomain', 1);


---
--- Update standard installation
---
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b1'       WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b1' WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';
UPDATE TopicProp SET PropValue='logo.png'               WHERE TopicID='t-deepamehtainstallation' AND PropName='Corporate Icon';

---
--- Update DB content version
---
UPDATE KeyGenerator SET NextKey=8 WHERE Relation='DB-Content Version';
