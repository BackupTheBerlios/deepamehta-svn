------------------
--- Workspaces ---
------------------



---
--- Workspace "Kompetenzstern"
---
INSERT INTO Topic VALUES ('tt-workspace', 1, 1, 't-ks-workspace', 'Kompetenzstern');
INSERT INTO TopicProp VALUES ('t-ks-workspace', 1, 'Name', 'Kompetenzstern');
INSERT INTO TopicProp VALUES ('t-ks-workspace', 1, 'Public', 'on');
INSERT INTO TopicProp VALUES ('t-ks-workspace', 1, 'Default', 'off');
-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-ks-workmap', 'Kompetenzstern');
INSERT INTO TopicProp VALUES ('t-ks-workmap', 1, 'Name', 'Kompetenzstern');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-629', '', 't-ks-workspace', 1, 't-ks-workmap', 1);
-- chat
INSERT INTO Topic VALUES ('tt-chatboard', 1, 1, 't-ks-chat', 'Kompetenzstern Chats');
INSERT INTO TopicProp VALUES ('t-ks-chat', 1, 'Name', 'Kompetenzstern Chats');
INSERT INTO ViewTopic VALUES ('t-ks-workmap', 1, 't-ks-chat', 1, 100, 50);
-- forum
INSERT INTO Topic VALUES ('tt-messageboard', 1, 1, 't-ks-forum', 'Kompetenzstern Forum');
INSERT INTO TopicProp VALUES ('t-ks-forum', 1, 'Name', 'Kompetenzstern Forum');
INSERT INTO ViewTopic VALUES ('t-ks-workmap', 1, 't-ks-forum', 1, 120, 100);
-- assign types to workspace
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-630', '', 't-ks-workspace', 1, 'tt-kompetenzstern', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-631', '', 't-ks-workspace', 1, 'tt-bewertungsgegenstand', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-641', '', 't-ks-workspace', 1, 'tt-bewertungsebene', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-632', '', 't-ks-workspace', 1, 'tt-bewertungsskala', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-633', '', 't-ks-workspace', 1, 'tt-kriterium', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-714', '', 't-ks-workspace', 1, 'tt-relateddocument', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-718', '', 't-ks-workspace', 1, 'tt-exporteddocument', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-662', '', 't-ks-workspace', 1, 'at-kompetenzstern', 1);
INSERT INTO AssociationProp VALUES ('a-630', 1, 'Access Permission', 'create in workspace');
INSERT INTO AssociationProp VALUES ('a-631', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-641', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-632', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-633', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-714', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-718', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-662', 1, 'Access Permission', 'view');
--- set workspace preferences
INSERT INTO Association VALUES ('at-preference', 1, 1, 'a-654', '', 't-ks-workspace', 1, 't-businesscheck', 1);
INSERT INTO Association VALUES ('at-preference', 1, 1, 'a-655', '', 't-ks-workspace', 1, 't-kmu', 1);
INSERT INTO Association VALUES ('at-preference', 1, 1, 'a-656', '', 't-ks-workspace', 1, 't-skala1', 1);
--- set default "Export Format" preference
INSERT INTO Association VALUES ('at-preference', 1, 1, 'a-717', '', 't-ks-workspace', 1, 't-pdf', 1);



---
--- Workspace "KS Builder"
---
INSERT INTO Topic VALUES ('tt-workspace', 1, 1, 't-ks-templateworkspace', 'KS Builder');
INSERT INTO TopicProp VALUES ('t-ks-templateworkspace', 1, 'Name', 'KS Builder');
INSERT INTO TopicProp VALUES ('t-ks-templateworkspace', 1, 'Public', 'off');
INSERT INTO TopicProp VALUES ('t-ks-templateworkspace', 1, 'Default', 'off');
-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-templatebuilderworkspace', 'KS Builder');
INSERT INTO TopicProp VALUES ('t-templatebuilderworkspace', 1, 'Name', 'KS Builder');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-759', '', 't-ks-templateworkspace', 1, 't-templatebuilderworkspace', 1);
-- chat
INSERT INTO Topic VALUES ('tt-chatboard', 1, 1, 't-templatebuilderchat', 'KS Builder Chats');
INSERT INTO TopicProp VALUES ('t-templatebuilderchat', 1, 'Name', 'KS Builder Chats');
INSERT INTO ViewTopic VALUES ('t-templatebuilderworkspace', 1, 't-templatebuilderchat', 1, 600, 50);
-- forum
INSERT INTO Topic VALUES ('tt-messageboard', 1, 1, 't-templatebuilderforum', 'KS Builder Forum');
INSERT INTO TopicProp VALUES ('t-templatebuilderforum', 1, 'Name', 'KS Builder Forum');
INSERT INTO ViewTopic VALUES ('t-templatebuilderworkspace', 1, 't-templatebuilderforum', 1, 600, 100);
-- assign types to workspace
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-640', '', 't-ks-templateworkspace', 1, 'tt-kompetenzsterntemplate', 1);
INSERT INTO AssociationProp VALUES ('a-640', 1, 'Access Permission', 'create in workspace');
-- put template "Business Check" into workspace
INSERT INTO ViewTopic VALUES ('t-templatebuilderworkspace', 1, 't-businesscheck', 1, 100, 80);



-------------------
--- Topic Types ---
-------------------



--- "Kompetenzstern" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-kompetenzstern', 'Kompetenzstern');
-- set properties
INSERT INTO TopicProp VALUES ('tt-kompetenzstern', 1, 'Name', 'Kompetenzstern');
INSERT INTO TopicProp VALUES ('tt-kompetenzstern', 1, 'Plural Name', 'Kompetenzsterne');
INSERT INTO TopicProp VALUES ('tt-kompetenzstern', 1, 'Description', '<HTML><BODY>Die Hilfe zu <I>Kompetenzstern</I></BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-kompetenzstern', 1, 'Description Query', 'Was ist ein Kompetenzstern?');
INSERT INTO TopicProp VALUES ('tt-kompetenzstern', 1, 'Icon', 'Kompetenzstern.gif');
INSERT INTO TopicProp VALUES ('tt-kompetenzstern', 1, 'Creation Icon', 'createKompetenzstern.gif');
INSERT INTO TopicProp VALUES ('tt-kompetenzstern', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-kompetenzstern', 1, 'Custom Implementation', 'de.deepamehta.kompetenzstern.topics.KompetenzsternTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-701', '', 'tt-kompetenzstern', 1, 'pp-company', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-702', '', 'tt-kompetenzstern', 1, 'pp-ks-date', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-703', '', 'tt-kompetenzstern', 1, 'pp-ks-author', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-704', '', 'tt-kompetenzstern', 1, 'pp-abstract', 1);
INSERT INTO AssociationProp VALUES ('a-701', 1, 'Ordinal Number', '210');
INSERT INTO AssociationProp VALUES ('a-702', 1, 'Ordinal Number', '220');
INSERT INTO AssociationProp VALUES ('a-703', 1, 'Ordinal Number', '230');
INSERT INTO AssociationProp VALUES ('a-704', 1, 'Ordinal Number', '240');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-600', '', 'tt-topicmap', 1, 'tt-kompetenzstern', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-kompetenzsterncontainer', 'Kompetenzstern Search');
-- set properties of container type
INSERT INTO TopicProp VALUES ('tt-kompetenzsterncontainer', 1, 'Name', 'Kompetenzstern Search');
INSERT INTO TopicProp VALUES ('tt-kompetenzsterncontainer', 1, 'Icon', 'KompetenzsternContainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-601', '', 'tt-topiccontainer', 1, 'tt-kompetenzsterncontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-602', '', 'tt-kompetenzsterncontainer', 1, 'tt-kompetenzstern', 1);

--- "Bewertungsgegenstand" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-bewertungsgegenstand', 'Bewertungsgegenstand');
-- set properties
INSERT INTO TopicProp VALUES ('tt-bewertungsgegenstand', 1, 'Name', 'Bewertungsgegenstand');
INSERT INTO TopicProp VALUES ('tt-bewertungsgegenstand', 1, 'Plural Name', 'Bewertungsgegenstände');
INSERT INTO TopicProp VALUES ('tt-bewertungsgegenstand', 1, 'Description', '<HTML><BODY>Die Hilfe zu <I>Bewertungsgegenstand</I></BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-bewertungsgegenstand', 1, 'Description Query', 'Was ist ein Bewertungsgegenstand?');
INSERT INTO TopicProp VALUES ('tt-bewertungsgegenstand', 1, 'Icon', 'BewertungsGegenstand.gif');
INSERT INTO TopicProp VALUES ('tt-bewertungsgegenstand', 1, 'Unique Topic Names', 'on');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-623', '', 'tt-generic', 1, 'tt-bewertungsgegenstand', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-bewertungsgegenstandcontainer', 'Bewertungsgegenstand Search');
-- set properties of container type
INSERT INTO TopicProp VALUES ('tt-bewertungsgegenstandcontainer', 1, 'Name', 'Bewertungsgegenstand Search');
INSERT INTO TopicProp VALUES ('tt-bewertungsgegenstandcontainer', 1, 'Icon', 'BewertungsGegenstandContainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-624', '', 'tt-topiccontainer', 1, 'tt-bewertungsgegenstandcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-625', '', 'tt-bewertungsgegenstandcontainer', 1, 'tt-bewertungsgegenstand', 1);

--- "Bewertungsskala" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-bewertungsskala', 'Bewertungsskala');
-- set properties
INSERT INTO TopicProp VALUES ('tt-bewertungsskala', 1, 'Name', 'Bewertungsskala');
INSERT INTO TopicProp VALUES ('tt-bewertungsskala', 1, 'Plural Name', 'Bewertungsskalen');
INSERT INTO TopicProp VALUES ('tt-bewertungsskala', 1, 'Description', '<HTML><BODY>Die Hilfe zu <I>Bewertungsskala</I></BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-bewertungsskala', 1, 'Description Query', 'Was ist eine Bewertungsskala?');
INSERT INTO TopicProp VALUES ('tt-bewertungsskala', 1, 'Icon', 'BewertungsSkala.gif');
INSERT INTO TopicProp VALUES ('tt-bewertungsskala', 1, 'Unique Topic Names', 'on');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-626', '', 'tt-generic', 1, 'tt-bewertungsskala', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-bewertungsskalacontainer', 'Bewertungsskala Search');
-- set properties of container type
INSERT INTO TopicProp VALUES ('tt-bewertungsskalacontainer', 1, 'Name', 'Bewertungsskala Search');
INSERT INTO TopicProp VALUES ('tt-bewertungsskalacontainer', 1, 'Icon', 'BewertungsSkalaContainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-627', '', 'tt-topiccontainer', 1, 'tt-bewertungsskalacontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-628', '', 'tt-bewertungsskalacontainer', 1, 'tt-bewertungsskala', 1);

--- "Bewertungsebene" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-bewertungsebene', 'Bewertungsebene');
-- set properties
INSERT INTO TopicProp VALUES ('tt-bewertungsebene', 1, 'Name', 'Bewertungsebene');
INSERT INTO TopicProp VALUES ('tt-bewertungsebene', 1, 'Plural Name', 'Bewertungsebenen');
INSERT INTO TopicProp VALUES ('tt-bewertungsebene', 1, 'Description', '<HTML><BODY>Die Hilfe zu <I>Bewertungsebene</I></BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-bewertungsebene', 1, 'Description Query', 'Was ist eine Bewertungsebene?');
INSERT INTO TopicProp VALUES ('tt-bewertungsebene', 1, 'Icon', 'BewertungsEbene.gif');
INSERT INTO TopicProp VALUES ('tt-bewertungsebene', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-bewertungsebene', 1, 'Custom Implementation', 'de.deepamehta.kompetenzstern.topics.BewertungsebeneTopic');
--- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-674', '', 'tt-bewertungsebene', 1, 'pp-ordinalnumberhidden', 1);
--- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-637', '', 'tt-generic', 1, 'tt-bewertungsebene', 1);
--- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-bewertungsebenecontainer', 'Bewertungsebene Search');
--- set properties of container type
INSERT INTO TopicProp VALUES ('tt-bewertungsebenecontainer', 1, 'Name', 'Bewertungsebene Search');
INSERT INTO TopicProp VALUES ('tt-bewertungsebenecontainer', 1, 'Icon', 'BewertungsEbeneContainer.gif');
--- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-638', '', 'tt-topiccontainer', 1, 'tt-bewertungsebenecontainer', 1);
--- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-639', '', 'tt-bewertungsebenecontainer', 1, 'tt-bewertungsebene', 1);

--- "Kriterium" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-kriterium', 'Kriterium');
--- set properties
INSERT INTO TopicProp VALUES ('tt-kriterium', 1, 'Name', 'Kriterium');
INSERT INTO TopicProp VALUES ('tt-kriterium', 1, 'Description', '<HTML><BODY>Die Hilfe zu <I>Kriterium</I></BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-kriterium', 1, 'Description Query', 'Was ist ein Kriterium?');
INSERT INTO TopicProp VALUES ('tt-kriterium', 1, 'Plural Name', 'Kriterien');
INSERT INTO TopicProp VALUES ('tt-kriterium', 1, 'Icon', 'Kriterium.gif');
INSERT INTO TopicProp VALUES ('tt-kriterium', 1, 'Unique Topic Names', 'off');
INSERT INTO TopicProp VALUES ('tt-kriterium', 1, 'Custom Implementation', 'de.deepamehta.kompetenzstern.topics.KriteriumTopic');
--- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-622', '', 'tt-generic', 1, 'tt-kriterium', 1);
--- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-kriteriumcontainer', 'Kriterium Search');
--- set properties of container type
INSERT INTO TopicProp VALUES ('tt-kriteriumcontainer', 1, 'Name', 'Kriterium Search');
INSERT INTO TopicProp VALUES ('tt-kriteriumcontainer', 1, 'Icon', 'KriteriumContainer.gif');
--- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-659', '', 'tt-topiccontainer', 1, 'tt-kriteriumcontainer', 1);
--- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-660', '', 'tt-kriteriumcontainer', 1, 'tt-kriterium', 1);
--- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-661', '', 'tt-kriterium', 1, 'pp-ordinalnumberhidden', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-663', '', 'tt-kriterium', 1, 'pp-help', 1);

--- "Kriterium" subtypes (one per Bewertungsskala) ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-kriterium1', 'Kriterium');
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-kriterium2', 'Kriterium');
-- properties
INSERT INTO TopicProp VALUES ('tt-kriterium1', 1, 'Name', 'Kriterium');
INSERT INTO TopicProp VALUES ('tt-kriterium1', 1, 'Description Query', 'Was ist ein Kriterium?');
INSERT INTO TopicProp VALUES ('tt-kriterium1', 1, 'Unique Topic Names', 'off');
INSERT INTO TopicProp VALUES ('tt-kriterium2', 1, 'Name', 'Kriterium');
INSERT INTO TopicProp VALUES ('tt-kriterium2', 1, 'Description Query', 'Was ist ein Kriterium?');
INSERT INTO TopicProp VALUES ('tt-kriterium2', 1, 'Unique Topic Names', 'off');
-- subtypes
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-603', '', 'tt-kriterium', 1, 'tt-kriterium1', 1);
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-604', '', 'tt-kriterium', 1, 'tt-kriterium2', 1);
-- container types
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-kriterium1container', 'Kriterium Search');
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-kriterium2container', 'Kriterium Search');
INSERT INTO TopicProp VALUES ('tt-kriterium1container', 1, 'Name', 'Kriterium Search');
INSERT INTO TopicProp VALUES ('tt-kriterium2container', 1, 'Name', 'Kriterium Search');
INSERT INTO TopicProp VALUES ('tt-kriterium1container', 1, 'Icon', 'KriteriumContainer.gif');
INSERT INTO TopicProp VALUES ('tt-kriterium2container', 1, 'Icon', 'KriteriumContainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-665', '', 'tt-topiccontainer', 1, 'tt-kriterium1container', 1);
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-667', '', 'tt-topiccontainer', 1, 'tt-kriterium2container', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-666', '', 'tt-kriterium1container', 1, 'tt-kriterium1', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-668', '', 'tt-kriterium2container', 1, 'tt-kriterium2', 1);
-- assign property
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-605', '', 'tt-kriterium1', 1, 'pp-wert1', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-606', '', 'tt-kriterium2', 1, 'pp-wert2', 1);
-- assign Bewertungsskala to "Kriterium" subtypes
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-657', '', 't-skala1', 1, 'tt-kriterium2', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-658', '', 't-skala2', 1, 'tt-kriterium1', 1);
---
--- Bewertungsskala 1
---
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val1-1', '1');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val1-2', '2');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val1-3', '3');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val1-4', '4');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val1-5', '5');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val1-6', '6');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val1-7', '7');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val1-8', '8');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val1-9', '9');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val1-10', '10');
INSERT INTO TopicProp VALUES ('t-val1-1', 1, 'Name', '1');
INSERT INTO TopicProp VALUES ('t-val1-2', 1, 'Name', '2');
INSERT INTO TopicProp VALUES ('t-val1-3', 1, 'Name', '3');
INSERT INTO TopicProp VALUES ('t-val1-4', 1, 'Name', '4');
INSERT INTO TopicProp VALUES ('t-val1-5', 1, 'Name', '5');
INSERT INTO TopicProp VALUES ('t-val1-6', 1, 'Name', '6');
INSERT INTO TopicProp VALUES ('t-val1-7', 1, 'Name', '7');
INSERT INTO TopicProp VALUES ('t-val1-8', 1, 'Name', '8');
INSERT INTO TopicProp VALUES ('t-val1-9', 1, 'Name', '9');
INSERT INTO TopicProp VALUES ('t-val1-10', 1, 'Name', '10');
--- assign constants to property
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-607', '', 'pp-wert1', 1, 't-val1-1', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-608', '', 'pp-wert1', 1, 't-val1-2', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-609', '', 'pp-wert1', 1, 't-val1-3', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-610', '', 'pp-wert1', 1, 't-val1-4', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-611', '', 'pp-wert1', 1, 't-val1-5', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-612', '', 'pp-wert1', 1, 't-val1-6', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-613', '', 'pp-wert1', 1, 't-val1-7', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-614', '', 'pp-wert1', 1, 't-val1-8', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-615', '', 'pp-wert1', 1, 't-val1-9', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-616', '', 'pp-wert1', 1, 't-val1-10', 1);
INSERT INTO AssociationProp VALUES ('a-607', 1, 'Ordinal Number', '01');
INSERT INTO AssociationProp VALUES ('a-608', 1, 'Ordinal Number', '02');
INSERT INTO AssociationProp VALUES ('a-609', 1, 'Ordinal Number', '03');
INSERT INTO AssociationProp VALUES ('a-610', 1, 'Ordinal Number', '04');
INSERT INTO AssociationProp VALUES ('a-611', 1, 'Ordinal Number', '05');
INSERT INTO AssociationProp VALUES ('a-612', 1, 'Ordinal Number', '06');
INSERT INTO AssociationProp VALUES ('a-613', 1, 'Ordinal Number', '07');
INSERT INTO AssociationProp VALUES ('a-614', 1, 'Ordinal Number', '08');
INSERT INTO AssociationProp VALUES ('a-615', 1, 'Ordinal Number', '09');
INSERT INTO AssociationProp VALUES ('a-616', 1, 'Ordinal Number', '10');
---
--- Bewertungsskala 2
---
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val2-1', '-\-');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val2-2', '-');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val2-3', '0');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val2-4', '+');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-val2-5', '++');
INSERT INTO TopicProp VALUES ('t-val2-1', 1, 'Name', '-\-');
INSERT INTO TopicProp VALUES ('t-val2-2', 1, 'Name', '-');
INSERT INTO TopicProp VALUES ('t-val2-3', 1, 'Name', '0');
INSERT INTO TopicProp VALUES ('t-val2-4', 1, 'Name', '+');
INSERT INTO TopicProp VALUES ('t-val2-5', 1, 'Name', '++');
--- assign constants to property
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-617', '', 'pp-wert2', 1, 't-val2-1', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-618', '', 'pp-wert2', 1, 't-val2-2', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-619', '', 'pp-wert2', 1, 't-val2-3', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-620', '', 'pp-wert2', 1, 't-val2-4', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-621', '', 'pp-wert2', 1, 't-val2-5', 1);
INSERT INTO AssociationProp VALUES ('a-617', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-618', 1, 'Ordinal Number', '2');
INSERT INTO AssociationProp VALUES ('a-619', 1, 'Ordinal Number', '3');
INSERT INTO AssociationProp VALUES ('a-620', 1, 'Ordinal Number', '4');
INSERT INTO AssociationProp VALUES ('a-621', 1, 'Ordinal Number', '5');

--- "Bewertung" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-bewertung', 'Bewertung');
--- set properties
INSERT INTO TopicProp VALUES ('tt-bewertung', 1, 'Name', 'Bewertung');
INSERT INTO TopicProp VALUES ('tt-bewertung', 1, 'Disabled', 'on');
INSERT INTO TopicProp VALUES ('tt-bewertung', 1, 'Hidden Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-bewertung', 1, 'Icon', 'blackdot.gif');
INSERT INTO TopicProp VALUES ('tt-bewertung', 1, 'Unique Topic Names', 'on');
--- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-664', '', 'tt-bewertung', 1, 'pp-ordinalnumber', 1);

--- "Template" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-kompetenzsterntemplate', 'Template');
--- set properties
INSERT INTO TopicProp VALUES ('tt-kompetenzsterntemplate', 1, 'Name', 'Template');
INSERT INTO TopicProp VALUES ('tt-kompetenzsterntemplate', 1, 'Plural Name', 'Templates');
INSERT INTO TopicProp VALUES ('tt-kompetenzsterntemplate', 1, 'Description', '<HTML><BODY>Die Hilfe zu <I>Kompetenzstern-Template</I></BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-kompetenzsterntemplate', 1, 'Description Query', 'Was ist ein Template?');
INSERT INTO TopicProp VALUES ('tt-kompetenzsterntemplate', 1, 'Icon', 'KompetenzsternTemplate.gif');
INSERT INTO TopicProp VALUES ('tt-kompetenzsterntemplate', 1, 'Creation Icon', 'createKompetenzsterntemplate.gif');
INSERT INTO TopicProp VALUES ('tt-kompetenzsterntemplate', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-kompetenzsterntemplate', 1, 'Custom Implementation', 'de.deepamehta.kompetenzstern.topics.TemplateTopic');
--- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-634', '', 'tt-generic', 1, 'tt-kompetenzsterntemplate', 1);
--- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-kompetenzsterntemplatecontainer', 'Template Search');
--- set properties of container type
INSERT INTO TopicProp VALUES ('tt-kompetenzsterntemplatecontainer', 1, 'Name', 'Template Search');
INSERT INTO TopicProp VALUES ('tt-kompetenzsterntemplatecontainer', 1, 'Icon', 'KompetenzsternTemplateContainer.gif');
--- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-635', '', 'tt-topiccontainer', 1, 'tt-kompetenzsterntemplatecontainer', 1);
--- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-636', '', 'tt-kompetenzsterntemplatecontainer', 1, 'tt-kompetenzsterntemplate', 1);

--- "Verknüpftes Dokument" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-relateddocument', 'Verknüpftes Dokument');
--- set properties
INSERT INTO TopicProp VALUES ('tt-relateddocument', 1, 'Name', 'Verknüpftes Dokument');
INSERT INTO TopicProp VALUES ('tt-relateddocument', 1, 'Plural Name', 'Verknüpfte Dokumente');
INSERT INTO TopicProp VALUES ('tt-relateddocument', 1, 'Description Query', 'Was ist ein verknüpftes Dokument?');
INSERT INTO TopicProp VALUES ('tt-relateddocument', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-relateddocument', 1, 'Custom Implementation', 'de.deepamehta.kompetenzstern.topics.VerknuepftesDokumentTopic');
--- derive from "Document"
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-671', '', 'tt-document', 1, 'tt-relateddocument', 1);
--- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-relateddocumentcontainer', 'Verknüpfte Dokumente Search');
--- set properties of container type
INSERT INTO TopicProp VALUES ('tt-relateddocumentcontainer', 1, 'Name', 'Verknüpfte Dokumente Search');
--- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-672', '', 'tt-topiccontainer', 1, 'tt-relateddocumentcontainer', 1);
--- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-673', '', 'tt-relateddocumentcontainer', 1, 'tt-relateddocument', 1);

--- "Exportiertes Dokument" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-exporteddocument', 'Exportiertes Dokument');
-- properties
INSERT INTO TopicProp VALUES ('tt-exporteddocument', 1, 'Name', 'Exportiertes Dokument');
INSERT INTO TopicProp VALUES ('tt-exporteddocument', 1, 'Plural Name', 'Exportierte Dokumente');
INSERT INTO TopicProp VALUES ('tt-exporteddocument', 1, 'Description Query', 'Was ist ein exportiertes Dokument?');
INSERT INTO TopicProp VALUES ('tt-exporteddocument', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-exporteddocument', 1, 'Custom Implementation', 'de.deepamehta.kompetenzstern.topics.ExportiertesDokumentTopic');
-- derive from "Document"
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-709', '', 'tt-document', 1, 'tt-exporteddocument', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-exporteddocumentcontainer', 'Exportierte Dokumente Search');
--- set properties of container type
INSERT INTO TopicProp VALUES ('tt-exporteddocumentcontainer', 1, 'Name', 'Exportierte Dokumente Search');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-710', '', 'tt-topiccontainer', 1, 'tt-exporteddocumentcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-711', '', 'tt-exporteddocumentcontainer', 1, 'tt-exporteddocument', 1);



-------------------------
--- Association Types ---
-------------------------



--- "kompetenzstern" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-kompetenzstern', 'Kompetenzstern');
INSERT INTO TopicProp VALUES ('at-kompetenzstern', 1, 'Name', 'Kompetenzstern');
INSERT INTO TopicProp VALUES ('at-kompetenzstern', 1, 'Disabled', 'on');
INSERT INTO TopicProp VALUES ('at-kompetenzstern', 1, 'Color', '#000000');

--- "kompetenzstern membership" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-ksmembership', 'Kompetenzstern Membership');
INSERT INTO TopicProp VALUES ('at-ksmembership', 1, 'Name', 'Kompetenzstern Membership');
INSERT INTO TopicProp VALUES ('at-ksmembership', 1, 'Custom Implementation', 'de.deepamehta.kompetenzstern.assocs.KompetenzsternMembership');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-758', '', 'at-ksmembership', 1, 'pp-templatebuilder', 1);
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-757', '', 'at-membership', 1, 'at-ksmembership', 1);
-- assign membership association type to workspace "Kompetenzstern"
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-756', '', 't-ks-workspace', 1, 'at-ksmembership', 1);



------------------
--- Properties ---
------------------


--- "Firma" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-company', 'Firma');
INSERT INTO TopicProp VALUES ('pp-company', 1, 'Name', 'Firma');
INSERT INTO TopicProp VALUES ('pp-company', 1, 'Visualization', 'Input Field');

--- "Datum" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-ks-date', 'Datum');
INSERT INTO TopicProp VALUES ('pp-ks-date', 1, 'Name', 'Datum');
INSERT INTO TopicProp VALUES ('pp-ks-date', 1, 'Visualization', 'Date Chooser');

--- "Erfasser" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-ks-author', 'Erfasser');
INSERT INTO TopicProp VALUES ('pp-ks-author', 1, 'Name', 'Erfasser');
INSERT INTO TopicProp VALUES ('pp-ks-author', 1, 'Visualization', 'Input Field');

--- "Zusammenfassung" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-abstract', 'Zusammenfassung');
INSERT INTO TopicProp VALUES ('pp-abstract', 1, 'Name', 'Zusammenfassung');
INSERT INTO TopicProp VALUES ('pp-abstract', 1, 'Visualization', 'Text Editor');

--- "Ordinal Number" (Kriterium, Bewertungsebene) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-ordinalnumberhidden', 'Ordinal Number');
INSERT INTO TopicProp VALUES ('pp-ordinalnumberhidden', 1, 'Name', 'Ordinal Number');
INSERT INTO TopicProp VALUES ('pp-ordinalnumberhidden', 1, 'Visualization', 'hidden');

--- "Hilfe" (Kriterium) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-help', 'Hilfe');
INSERT INTO TopicProp VALUES ('pp-help', 1, 'Name', 'Hilfe');
INSERT INTO TopicProp VALUES ('pp-help', 1, 'Visualization', 'Text Editor');
INSERT INTO TopicProp VALUES ('pp-help', 1, 'Edit Icon', 'showHilfe.gif');

--- "Wert" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-wert1', 'Wert');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-wert2', 'Wert');
INSERT INTO TopicProp VALUES ('pp-wert1', 1, 'Name', 'Wert');
INSERT INTO TopicProp VALUES ('pp-wert2', 1, 'Name', 'Wert');
INSERT INTO TopicProp VALUES ('pp-wert1', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-wert2', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-wert1', 1, 'Edit Icon', 'setwert.gif');
INSERT INTO TopicProp VALUES ('pp-wert2', 1, 'Edit Icon', 'setwert.gif');

--- "Template Builder" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-templatebuilder', 'Template Builder');
INSERT INTO TopicProp VALUES ('pp-templatebuilder', 1, 'Name', 'Template Builder');
INSERT INTO TopicProp VALUES ('pp-templatebuilder', 1, 'Visualization', 'Switch');



-----------------------
--- Default Entries ---
-----------------------



--- "Bewertungsgegenstand" ---
INSERT INTO Topic VALUES ('tt-bewertungsgegenstand', 1, 1, 't-startup',   'Startup');
INSERT INTO Topic VALUES ('tt-bewertungsgegenstand', 1, 1, 't-kmu',       'KMU');
INSERT INTO Topic VALUES ('tt-bewertungsgegenstand', 1, 1, 't-produkt',   'Produkt');
INSERT INTO Topic VALUES ('tt-bewertungsgegenstand', 1, 1, 't-projekt',   'Projekt');
INSERT INTO Topic VALUES ('tt-bewertungsgegenstand', 1, 1, 't-portfolio', 'Portfolio');
INSERT INTO TopicProp VALUES ('t-startup', 1,   'Name', 'Startup');
INSERT INTO TopicProp VALUES ('t-kmu', 1,       'Name', 'KMU');
INSERT INTO TopicProp VALUES ('t-produkt', 1,   'Name', 'Produkt');
INSERT INTO TopicProp VALUES ('t-projekt', 1,   'Name', 'Projekt');
INSERT INTO TopicProp VALUES ('t-portfolio', 1, 'Name', 'Portfolio');

--- "Bewertungsskala" ---
INSERT INTO Topic VALUES ('tt-bewertungsskala', 1, 1, 't-skala1', '-\-/++');
INSERT INTO Topic VALUES ('tt-bewertungsskala', 1, 1, 't-skala2', '1/10');
INSERT INTO TopicProp VALUES ('t-skala1', 1, 'Name', '-\-/++');
INSERT INTO TopicProp VALUES ('t-skala2', 1, 'Name', '1/10');
