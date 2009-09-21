--- ./run.sh patchdb -Dpatch=install/apps/dm-importer/src/importwizard.sql
--- is the way to apply this patch to your current deepamehta instance on a LINUX machine
--- Import Wizard 0.9a, written by Malte Reißig, Berlin, 03.06.2009
--- and with a copyleft notice right here
---- last assocId is currently named to a-importer-27

--- Importer ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-importwizard', 'Import Wizard');
INSERT INTO TopicProp VALUES ('tt-importwizard', 1, 'Name', 'Import Wizard');
INSERT INTO TopicProp VALUES ('tt-importwizard', 1, 'Plural Name', 'Import Wizards');
INSERT INTO TopicProp VALUES ('tt-importwizard', 1, 'Description', '<html><body><p>An <i>Import Wizard</i> enables you to import Character Seperated Value (CSV) Document Topics.</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-importwizard', 1, 'Description Query', 'What is an Import Wizard?');
INSERT INTO TopicProp VALUES ('tt-importwizard', 1, 'Icon', 'application.gif');
-- INSERT INTO TopicProp VALUES ('tt-importwizard', 1, 'Hidden Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-importwizard', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-importwizard', 1, 'Custom Implementation', 'de.importer.deepamehta.topics.ImporterTopic');

--- Association Result ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-importresult', 'Import Result');
INSERT INTO TopicProp VALUES ('at-importresult', 1, 'Name', 'Import Result');
INSERT INTO TopicProp VALUES ('at-importresult', 1, 'Plural Name', 'Import Results');
INSERT INTO TopicProp VALUES ('at-importresult', 1, 'Description', '<html><body><p>An <i>Import Result</i> is an Edge to all topics imported by an Importer Topic');
INSERT INTO TopicProp VALUES ('at-importresult', 1, 'Description Query', 'What is an Import Result ??');
INSERT INTO TopicProp VALUES ('at-importresult', 1, 'Color', '#a3c5eb');

-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-0', '', 'tt-importwizard', 1, 'pp-importer-seperator', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-1', '', 'tt-importwizard', 1, 'pp-importer-tempdata', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-2', '', 'tt-importwizard', 1, 'pp-importer-encoding', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-3', '', 'tt-importwizard', 1, 'pp-importer-step', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-4', '', 'tt-importwizard', 1, 'pp-importer-targettypeId', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-5', '', 'tt-importwizard', 1, 'pp-importer-log', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-6', '', 'tt-importwizard', 1, 'pp-importer-delimiter', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-7', '', 'tt-importwizard', 1, 'pp-importer-targettype', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-29', '', 'tt-importwizard', 1, 'pp-importer-propertynames', 1);
--- INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-30', '', 'tt-importwizard', 1, 'pp-importer-importfile', 1);
INSERT INTO AssociationProp VALUES ('a-importer-0', 1, 'Ordinal Number', '125');
INSERT INTO AssociationProp VALUES ('a-importer-1', 1, 'Ordinal Number', '353');
INSERT INTO AssociationProp VALUES ('a-importer-2', 1, 'Ordinal Number', '129');
INSERT INTO AssociationProp VALUES ('a-importer-3', 1, 'Ordinal Number', '124');
INSERT INTO AssociationProp VALUES ('a-importer-4', 1, 'Ordinal Number', '351');
INSERT INTO AssociationProp VALUES ('a-importer-5', 1, 'Ordinal Number', '352');
INSERT INTO AssociationProp VALUES ('a-importer-6', 1, 'Ordinal Number', '356');
INSERT INTO AssociationProp VALUES ('a-importer-7', 1, 'Ordinal Number', '123');
INSERT INTO AssociationProp VALUES ('a-importer-29', 1, 'Ordinal Number', '124');
--- INSERT INTO AssociationProp VALUES ('a-importer-30', 1, 'Ordinal Number', '349');
-- patchworking patch ;)
INSERT INTO AssociationProp VALUES ('a-importer-23', 1, 'Ordinal Number', '357');

-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-importer-24', '', 'tt-document', 1, 'tt-importwizard', 1);

-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-importwizard-search', 'Import Wizard-Search');
INSERT INTO TopicProp VALUES ('tt-importwizard-search', 1, 'Name', 'Import Wizard-Search');
-- INSERT INTO TopicProp VALUES ('tt-importwizard-search', 1, 'Icon', 'container.gif');

-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-importer-8', '', 'tt-topiccontainer', 1, 'tt-importwizard-search', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-importer-9', '', 'tt-importwizard-search', 1, 'tt-importwizard', 1);

------------------
--- Properties ---
------------------

--- "Seperated By  " (Importer) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-importer-seperator', 'Seperated by');
INSERT INTO TopicProp VALUES ('pp-importer-seperator', 1, 'Name', 'Seperated by');
INSERT INTO TopicProp VALUES ('pp-importer-seperator', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-importer-seperator', 1, 'Default Value', 'Tabulator');

--- "File to be used " (Importer) --- (file chooser doesn't work) used supertype tt-document instead
--- INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-importer-importfile', 'File to be used');
--- INSERT INTO TopicProp VALUES ('pp-importer-importfile', 1, 'Name', 'File to be used');
--- INSERT INTO TopicProp VALUES ('pp-importer-importfile', 1, 'Visualization', 'File Chooser');

--- "Column names  " (Importer) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-importer-propertynames', 'Property Names');
INSERT INTO TopicProp VALUES ('pp-importer-propertynames', 1, 'Name', 'Property Names');
INSERT INTO TopicProp VALUES ('pp-importer-propertynames', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-importer-propertynames', 1, 'Default Value', 'Advanced');

--- "Temp Data" (Importer) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-importer-tempdata', 'Temp Data');
INSERT INTO TopicProp VALUES ('pp-importer-tempdata', 1, 'Name', 'Temp Data');
INSERT INTO TopicProp VALUES ('pp-importer-tempdata', 1, 'Visualization', 'Multiline Input Field');

--- "Importer File Encoding (Importer) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-importer-encoding', 'File Encoding');
INSERT INTO TopicProp VALUES ('pp-importer-encoding', 1, 'Name', 'File Encoding');
INSERT INTO TopicProp VALUES ('pp-importer-encoding', 1, 'Visualization', 'Option Menu');
INSERT INTO TopicProp VALUES ('pp-importer-encoding', 1, 'Default Value', 'UTF8');

--- "Importer Status" (Importer) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-importer-step', 'Step');
INSERT INTO TopicProp VALUES ('pp-importer-step', 1, 'Name', 'Step');
INSERT INTO TopicProp VALUES ('pp-importer-step', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-importer-step', 1, 'Default Value', '1');

--- "Importer TargetTypeId" (Importer) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-importer-targettype', 'TypeId');
INSERT INTO TopicProp VALUES ('pp-importer-targettype', 1, 'Name', 'TypeId');
INSERT INTO TopicProp VALUES ('pp-importer-targettype', 1, 'Visualization', 'Input Field');

--- "Importer Log" (Importer) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-importer-log', 'Log');
INSERT INTO TopicProp VALUES ('pp-importer-log', 1, 'Name', 'Log');
INSERT INTO TopicProp VALUES ('pp-importer-log', 1, 'Visualization', 'Multiline Input Field');

--- "Importer Text Delimiter" (Importer) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-importer-delimiter', 'Text Delimiter');
INSERT INTO TopicProp VALUES ('pp-importer-delimiter', 1, 'Name', 'Text Delimiter');
INSERT INTO TopicProp VALUES ('pp-importer-delimiter', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-importer-delimiter', 1, 'Default Value', 'Quotation Marks');

--- "Importer Property Values" ---
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-tab', 'Tabulator');
INSERT INTO TopicProp VALUES ('pv-importer-tab', 1, 'Name', 'Tabulator');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-comma', 'Comma');
INSERT INTO TopicProp VALUES ('pv-importer-comma', 1, 'Name', 'Comma');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-colon', 'Semicolon');
INSERT INTO TopicProp VALUES ('pv-importer-colon', 1, 'Name', 'Semicolon');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-quote', 'Quotation Marks');
INSERT INTO TopicProp VALUES ('pv-importer-quote', 1, 'Name', 'Quotation Marks');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-enc-utf8', 'UTF8');
INSERT INTO TopicProp VALUES ('pv-importer-enc-utf8', 1, 'Name', 'UTF8');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-enc-latin1', 'Latin1');
INSERT INTO TopicProp VALUES ('pv-importer-enc-latin1', 1, 'Name', 'Latin1');

--- Property Names for switching in topics.executeCommand() between different wizard modes

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-pnames-auto', 'Autosuggest');
INSERT INTO TopicProp VALUES ('pv-importer-pnames-auto', 1, 'Name', 'Autosuggest');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-pnames-firstrow', 'First row contains property names');
INSERT INTO TopicProp VALUES ('pv-importer-pnames-firstrow', 1, 'Name', 'First row contains property names');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-pnames-advanced', 'Advanced');
INSERT INTO TopicProp VALUES ('pv-importer-pnames-advanced', 1, 'Name', 'Advanced');


--- Internal State property

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-step1', '1');
INSERT INTO TopicProp VALUES ('pv-importer-step1', 1, 'Name', '1');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-step2', '2');
INSERT INTO TopicProp VALUES ('pv-importer-step2', 1, 'Name', '2');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 'pv-importer-step3', '3');
INSERT INTO TopicProp VALUES ('pv-importer-step3', 1, 'Name', '3');

--- Assign Contants to Property Limit Updates ---


INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-10', '', 'pp-importer-seperator', 1, 'pv-importer-comma', 1);
INSERT INTO AssociationProp VALUES ('a-importer-10', 1, 'Ordinal Number', '2');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-11', '', 'pp-importer-delimiter', 1, 'pv-importer-colon', 1);
INSERT INTO AssociationProp VALUES ('a-importer-11', 1, 'Ordinal Number', '2');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-12', '', 'pp-importer-delimiter', 1, 'pv-importer-quote', 1);
INSERT INTO AssociationProp VALUES ('a-importer-12', 1, 'Ordinal Number', '1');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-13', '', 'pp-importer-encoding', 1, 'pv-importer-enc-utf8', 1);
INSERT INTO AssociationProp VALUES ('a-importer-13', 1, 'Ordinal Number', '1');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-14', '', 'pp-importer-encoding', 1, 'pv-importer-enc-latin1', 1);
INSERT INTO AssociationProp VALUES ('a-importer-14', 1, 'Ordinal Number', '2');
//
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-26', '', 'pp-importer-propertynames', 1, 'pv-importer-pnames-auto', 1);
INSERT INTO AssociationProp VALUES ('a-importer-26', 1, 'Ordinal Number', '1');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-27', '', 'pp-importer-propertynames', 1, 'pv-importer-pnames-firstrow', 1);
INSERT INTO AssociationProp VALUES ('a-importer-27', 1, 'Ordinal Number', '2');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-28', '', 'pp-importer-propertynames', 1, 'pv-importer-pnames-advanced', 1);
INSERT INTO AssociationProp VALUES ('a-importer-28', 1, 'Ordinal Number', '2');
//
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-15', '', 'pp-importer-step', 1, 'pv-importer-step1', 1);
INSERT INTO AssociationProp VALUES ('a-importer-15', 1, 'Ordinal Number', '1');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-16', '', 'pp-importer-step', 1, 'pv-importer-step2', 1);
INSERT INTO AssociationProp VALUES ('a-importer-16', 1, 'Ordinal Number', '2');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-17', '', 'pp-importer-step', 1, 'pv-importer-step3', 1);
INSERT INTO AssociationProp VALUES ('a-importer-17', 1, 'Ordinal Number', '3');

-- assign type to user
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-importer-18', '', 't-rootuser', 1, 'tt-importwizard', 1);
INSERT INTO AssociationProp VALUES ('a-importer-18', 1, 'Access Permission', 'create');

-- assign type to workspace "Administration"
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-importer-20', '', 't-administrationgroup', 1, 'tt-importwizard', 1);
INSERT INTO AssociationProp VALUES ('a-importer-21', 1, 'Access Permission', 'create');


--- what was wrong ? ---
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-importer-22', '', 'pp-importer-seperator', 1, 'pv-importer-tab', 1);
INSERT INTO AssociationProp VALUES ('a-importer-22', 1, 'Ordinal Number', '1');
