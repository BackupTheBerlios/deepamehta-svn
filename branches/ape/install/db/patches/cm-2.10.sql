---
--- This patch updates CM 2.9 to 2.10
--- Apply this patch if you want to update DeepaMehta 2.0b2 to 2.0b3-pre1 while keeping your content
---



--- New property "Web Alias" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-webalias', 'Web Alias');
INSERT INTO TopicProp VALUES ('pp-webalias', 1, 'Name', 'Web Alias');
INSERT INTO TopicProp VALUES ('pp-webalias', 1, 'Visualization', 'Input Field');

--- Extend topic type "Address"
INSERT INTO TopicProp VALUES ('tt-address', 1, 'Custom Implementation', 'de.deepamehta.topics.AddressTopic');



---
--- Update topic type "Person" ---
---
-- rename "Forename" property into "First Name"
UPDATE Topic SET Name='First Name' WHERE ID='pp-forename';
UPDATE TopicProp SET PropValue='First Name' WHERE TopicID='pp-forename' AND PropName='Name';
-- update values
UPDATE TopicProp SET PropName='First Name' WHERE PropName='Forename';
UPDATE TopicProp SET PropName='Name' WHERE PropName='Surname';
-- delete association to "Surname"
DELETE FROM Association WHERE ID='a-297';
DELETE FROM AssociationProp WHERE AssociationID='a-297';
DELETE FROM ViewAssociation WHERE AssociationID='a-297';



---
--- Update topic types "Person" and "Institution" ---
---
-- new relation to "Webpage"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-416', '', 'tt-person', 1, 'tt-webpage', 1);
INSERT INTO AssociationProp VALUES ('a-416', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-416', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-416', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-416', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-416', 1, 'Ordinal Number', '150');
-- new relation to "Webpage"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-417', '', 'tt-institution', 1, 'tt-webpage', 1);
INSERT INTO AssociationProp VALUES ('a-417', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-417', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-417', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-417', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-417', 1, 'Ordinal Number', '150');



---
--- Update Collection of "Phone.." topic types ---
---
INSERT INTO TopicProp VALUES ('tt-phonenumber', 1, 'Custom Implementation', 'de.deepamehta.topics.PhoneNumberTopic');
INSERT INTO TopicProp VALUES ('tt-personphone', 1, 'Custom Implementation', 'de.deepamehta.topics.MobileNumberTopic');
--- rename topic type "Person Phone" to "Mobile Number"
UPDATE Topic SET Name='Mobile Number' WHERE ID='tt-personphone';
UPDATE TopicProp SET PropValue='Mobile Number' WHERE TopicID='tt-personphone' AND PropName='Name';
UPDATE TopicProp SET PropValue='Mobile Numbers' WHERE TopicID='tt-personphone' AND PropName='Plural Name';
-- rename search type
UPDATE Topic SET Name='Mobile Number Search' WHERE ID='tt-personphonesearch';
UPDATE TopicProp SET PropValue='Mobile Number Search' WHERE TopicID='tt-personphonesearch' AND PropName='Name';
---
--- new Topic Type "Fax Number" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-faxnumber', 'Fax Number');
INSERT INTO TopicProp VALUES ('tt-faxnumber', 1, 'Name', 'Fax Number');
INSERT INTO TopicProp VALUES ('tt-faxnumber', 1, 'Plural Name', 'Fax Numbers');
INSERT INTO TopicProp VALUES ('tt-faxnumber', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-faxnumber', 1, 'Custom Implementation', 'de.deepamehta.topics.FaxNumberTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-103', '', 'tt-phonenumber', 1, 'tt-faxnumber', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-faxnumbersearch', 'Fax Number Search');
INSERT INTO TopicProp VALUES ('tt-faxnumbersearch', 1, 'Name', 'Fax Number Search');
INSERT INTO TopicProp VALUES ('tt-faxnumbersearch', 1, 'Icon', 'phone-search.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-104', '', 'tt-topiccontainer', 1, 'tt-faxnumbersearch', 1);
-- assign type to search type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-105', '', 'tt-faxnumbersearch', 1, 'tt-faxnumber', 1);
---
--- new relations
-- "Institution" -> "Fax Number"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-106', '', 'tt-institution', 1, 'tt-faxnumber', 1);
INSERT INTO AssociationProp VALUES ('a-106', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-106', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-106', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-106', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-106', 1, 'Ordinal Number', '165');
-- "Person" -> "Phone Number"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-107', '', 'tt-person', 1, 'tt-phonenumber', 1);
INSERT INTO AssociationProp VALUES ('a-107', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-107', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-107', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-107', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-107', 1, 'Ordinal Number', '160');
-- "Person" -> "Fax Number"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-108', '', 'tt-person', 1, 'tt-faxnumber', 1);
INSERT INTO AssociationProp VALUES ('a-108', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-108', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-108', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-108', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-108', 1, 'Ordinal Number', '165');
---
--- delete "Kind" property
DELETE FROM Topic WHERE ID='pp-personphonekind';
DELETE FROM TopicProp WHERE TopicID='pp-personphonekind';
DELETE FROM ViewTopic WHERE TopicID='pp-personphonekind';
-- delete association
DELETE FROM Association WHERE ID='a-157';
DELETE FROM AssociationProp WHERE AssociationID='a-157';
DELETE FROM ViewAssociation WHERE AssociationID='a-157';
--- delete property values
DELETE FROM Topic WHERE ID='t-home';
DELETE FROM TopicProp WHERE TopicID='t-home';
DELETE FROM ViewTopic WHERE TopicID='t-home';
DELETE FROM Topic WHERE ID='t-work';
DELETE FROM TopicProp WHERE TopicID='t-work';
DELETE FROM ViewTopic WHERE TopicID='t-work';
DELETE FROM Topic WHERE ID='t-homefax';
DELETE FROM TopicProp WHERE TopicID='t-homefax';
DELETE FROM ViewTopic WHERE TopicID='t-homefax';
DELETE FROM Topic WHERE ID='t-workfax';
DELETE FROM TopicProp WHERE TopicID='t-workfax';
DELETE FROM ViewTopic WHERE TopicID='t-workfax';
DELETE FROM Topic WHERE ID='t-mobile';
DELETE FROM TopicProp WHERE TopicID='t-mobile';
DELETE FROM ViewTopic WHERE TopicID='t-mobile';
-- delete associations
DELETE FROM Association WHERE ID='a-58';
DELETE FROM AssociationProp WHERE AssociationID='a-58';
DELETE FROM ViewAssociation WHERE AssociationID='a-58';
DELETE FROM Association WHERE ID='a-149';
DELETE FROM AssociationProp WHERE AssociationID='a-149';
DELETE FROM ViewAssociation WHERE AssociationID='a-149';
DELETE FROM Association WHERE ID='a-155';
DELETE FROM AssociationProp WHERE AssociationID='a-155';
DELETE FROM ViewAssociation WHERE AssociationID='a-155';
DELETE FROM Association WHERE ID='a-156';
DELETE FROM AssociationProp WHERE AssociationID='a-156';
DELETE FROM ViewAssociation WHERE AssociationID='a-156';
DELETE FROM Association WHERE ID='a-151';
DELETE FROM AssociationProp WHERE AssociationID='a-151';
DELETE FROM ViewAssociation WHERE AssociationID='a-151';


---
--- Update association type "Relation" ---
---
-- delete "Strong" property
DELETE FROM Topic WHERE ID='pp-strong';
DELETE FROM TopicProp WHERE TopicID='pp-strong';
DELETE FROM ViewTopic WHERE TopicID='pp-strong';
DELETE FROM Association WHERE ID='a-407';
DELETE FROM AssociationProp WHERE AssociationID='a-407';
DELETE FROM ViewAssociation WHERE AssociationID='a-407';
-- delete values
DELETE FROM AssociationProp WHERE PropName='Strong';
-- update values (Person - Address, Person - Email Address, Institution - Address, Institution - Email Address)
INSERT INTO AssociationProp VALUES ('a-308', 1, 'Web Info', 'Deeply Related Info');
INSERT INTO AssociationProp VALUES ('a-308', 1, 'Web Form', 'Deeply Related Form');
INSERT INTO AssociationProp VALUES ('a-308', 1, 'Ordinal Number', '140');
INSERT INTO AssociationProp VALUES ('a-404', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-404', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-404', 1, 'Ordinal Number', '170');
INSERT INTO AssociationProp VALUES ('a-319', 1, 'Web Info', 'Deeply Related Info');
INSERT INTO AssociationProp VALUES ('a-319', 1, 'Web Form', 'Deeply Related Form');
INSERT INTO AssociationProp VALUES ('a-319', 1, 'Ordinal Number', '140');
INSERT INTO AssociationProp VALUES ('a-406', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-406', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-406', 1, 'Ordinal Number', '170');
-- (Person - Mobile Number, Institution - Phone Number)
INSERT INTO AssociationProp VALUES ('a-55', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-55', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-55', 1, 'Ordinal Number', '168');
INSERT INTO AssociationProp VALUES ('a-163', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-163', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-163', 1, 'Ordinal Number', '160');
-- assign new properties "Web Info" and "Web Form"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-413', '', 'at-relation', 1, 'pp-webinfo', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-414', '', 'at-relation', 1, 'pp-webform', 1);
INSERT INTO AssociationProp VALUES ('a-413', 1, 'Ordinal Number', '210');
INSERT INTO AssociationProp VALUES ('a-414', 1, 'Ordinal Number', '220');
--
-- new property "Web Info" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-webinfo', 'Web Info');
INSERT INTO TopicProp VALUES ('pp-webinfo', 1, 'Name', 'Web Info');
INSERT INTO TopicProp VALUES ('pp-webinfo', 1, 'Visualization', 'Options Menu');
-- property values
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-relatedtopicname', 'Related Topic Name');
INSERT INTO TopicProp VALUES ('t-relatedtopicname', 1, 'Name', 'Related Topic Name');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-relatedinfo', 'Related Info');
INSERT INTO TopicProp VALUES ('t-relatedinfo', 1, 'Name', 'Related Info');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-deeplyrelatedinfo', 'Deeply Related Info');
INSERT INTO TopicProp VALUES ('t-deeplyrelatedinfo', 1, 'Name', 'Deeply Related Info');
-- assign property values
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-408', '', 'pp-webinfo', 1, 't-relatedtopicname', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-418', '', 'pp-webinfo', 1, 't-relatedinfo', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-409', '', 'pp-webinfo', 1, 't-deeplyrelatedinfo', 1);
INSERT INTO AssociationProp VALUES ('a-408', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-418', 1, 'Ordinal Number', '2');
INSERT INTO AssociationProp VALUES ('a-409', 1, 'Ordinal Number', '3');
--
-- new property "Web Form" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-webform', 'Web Form');
INSERT INTO TopicProp VALUES ('pp-webform', 1, 'Name', 'Web Form');
INSERT INTO TopicProp VALUES ('pp-webform', 1, 'Visualization', 'Options Menu');
-- property values
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-relatedtopicselector', 'Related Topic Selector');
INSERT INTO TopicProp VALUES ('t-relatedtopicselector', 1, 'Name', 'Related Topic Selector');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-relatedform', 'Related Form');
INSERT INTO TopicProp VALUES ('t-relatedform', 1, 'Name', 'Related Form');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-deeplyrelatedform', 'Deeply Related Form');
INSERT INTO TopicProp VALUES ('t-deeplyrelatedform', 1, 'Name', 'Deeply Related Form');
-- assign property values
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-410', '', 'pp-webform', 1, 't-relatedtopicselector', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-412', '', 'pp-webform', 1, 't-relatedform', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-415', '', 'pp-webform', 1, 't-deeplyrelatedform', 1);
INSERT INTO AssociationProp VALUES ('a-410', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-412', 1, 'Ordinal Number', '2');
INSERT INTO AssociationProp VALUES ('a-415', 1, 'Ordinal Number', '3');


---
--- Update topic types "Topic Type" and "Association Type" ---
---
-- delete property "Property Layout"
DELETE FROM Topic WHERE ID='pp-propertylayout';
DELETE FROM TopicProp WHERE TopicID='pp-propertylayout';
DELETE FROM ViewTopic WHERE TopicID='pp-propertylayout';
DELETE FROM Topic WHERE ID='t-supertypesfirst';
DELETE FROM TopicProp WHERE TopicID='t-supertypesfirst';
DELETE FROM ViewTopic WHERE TopicID='t-supertypesfirst';
DELETE FROM Topic WHERE ID='t-subtypesfirst';
DELETE FROM TopicProp WHERE TopicID='t-subtypesfirst';
DELETE FROM ViewTopic WHERE TopicID='t-subtypesfirst';
DELETE FROM Association WHERE ID='a-145';
DELETE FROM AssociationProp WHERE AssociationID='a-145';
DELETE FROM ViewAssociation WHERE AssociationID='a-145';
DELETE FROM Association WHERE ID='a-146';
DELETE FROM AssociationProp WHERE AssociationID='a-146';
DELETE FROM ViewAssociation WHERE AssociationID='a-146';
-- remove property from "Topic Type" and from "Association Type"
DELETE FROM Association WHERE ID='a-147';
DELETE FROM AssociationProp WHERE AssociationID='a-147';
DELETE FROM ViewAssociation WHERE AssociationID='a-147';
DELETE FROM Association WHERE ID='a-148';
DELETE FROM AssociationProp WHERE AssociationID='a-148';
DELETE FROM ViewAssociation WHERE AssociationID='a-148';
-- delete values
DELETE FROM TopicProp WHERE PropName='Property Layout';


---
--- Set new ordinal numbers ---
---
-- "Topic"
UPDATE AssociationProp SET PropValue='100' WHERE AssociationID='a-295' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='200' WHERE AssociationID='a-11' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='300' WHERE AssociationID='a-4' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='400' WHERE AssociationID='a-91' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='500' WHERE AssociationID='a-254' AND PropName='Ordinal Number';
-- "Topic Type"
UPDATE AssociationProp SET PropValue='110' WHERE AssociationID='a-59' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='210' WHERE AssociationID='a-284' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='310' WHERE AssociationID='a-283' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='320' WHERE AssociationID='a-13' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='330' WHERE AssociationID='a-255' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='340' WHERE AssociationID='a-249' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='350' WHERE AssociationID='a-pp-unique' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='360' WHERE AssociationID='a-12' AND PropName='Ordinal Number';
-- "Association Type"
UPDATE AssociationProp SET PropValue='210' WHERE AssociationID='a-241' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='220' WHERE AssociationID='a-15' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='230' WHERE AssociationID='a-14' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='320' WHERE AssociationID='a-258' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='350' WHERE AssociationID='a-242' AND PropName='Ordinal Number';
-- "Property"
UPDATE AssociationProp SET PropValue='110' WHERE AssociationID='a-16' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='310' WHERE AssociationID='a-285' AND PropName='Ordinal Number';
-- "Data Source"
UPDATE AssociationProp SET PropValue='110' WHERE AssociationID='a-20' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='120' WHERE AssociationID='a-21' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='130' WHERE AssociationID='a-134' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='140' WHERE AssociationID='a-22' AND PropName='Ordinal Number';
-- "Person"
UPDATE AssociationProp SET PropValue='90' WHERE AssociationID='a-296' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='110' WHERE AssociationID='a-30' AND PropName='Ordinal Number';
-- "Image"
UPDATE AssociationProp SET PropValue='20' WHERE AssociationID='a-56' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='30' WHERE AssociationID='a-72' AND PropName='Ordinal Number';
-- "Topic Map"
UPDATE AssociationProp SET PropValue='310' WHERE AssociationID='a-248' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='320' WHERE AssociationID='a-25' AND PropName='Ordinal Number';
-- "Workspace"
INSERT INTO AssociationProp VALUES ('a-400', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-247', 1, 'Ordinal Number', '120');
-- "File"
INSERT INTO AssociationProp VALUES ('a-26', 1, 'Ordinal Number', '10');
-- "Message"
UPDATE AssociationProp SET PropValue='210' WHERE AssociationID='a-865' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='220' WHERE AssociationID='a-866' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='230' WHERE AssociationID='a-869' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='240' WHERE AssociationID='a-870' AND PropName='Ordinal Number';
-- "Appointment"
UPDATE AssociationProp SET PropValue='110' WHERE AssociationID='a-294' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='120' WHERE AssociationID='a-attr_ap4' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='130' WHERE AssociationID='a-attr_ap5' AND PropName='Ordinal Number';
-- "Installation"
UPDATE AssociationProp SET PropValue='110' WHERE AssociationID='a-276' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='210' WHERE AssociationID='a-275' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='220' WHERE AssociationID='a-274' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='230' WHERE AssociationID='a-279' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='310' WHERE AssociationID='a-277' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='320' WHERE AssociationID='a-278' AND PropName='Ordinal Number';

-- "Association"
UPDATE AssociationProp SET PropValue='100' WHERE AssociationID='a-328' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='200' WHERE AssociationID='a-238' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='500' WHERE AssociationID='a-236' AND PropName='Ordinal Number';
-- "Relation"
UPDATE AssociationProp SET PropValue='110' WHERE AssociationID='a-227' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='120' WHERE AssociationID='a-290' AND PropName='Ordinal Number';



---
--- Update standard installation
---
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b3-pre1'       WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b3-pre1' WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';

---
--- Update DB content version
---
UPDATE KeyGenerator SET NextKey=10 WHERE Relation='DB-Content Version';
