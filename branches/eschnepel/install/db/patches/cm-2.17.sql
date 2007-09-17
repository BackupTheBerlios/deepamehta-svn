---
--- This patch updates CM 2.16 to 2.17
--- Apply this patch if you want to update DeepaMehta 2.0b7 to 2.0b8 while keeping your content
---


---
--- New "Membership" Properties: "Editor" and "Publisher"
---
-- create properties
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-editor', 'Editor');
INSERT INTO TopicProp VALUES ('pp-editor', 1, 'Name', 'Editor');
INSERT INTO TopicProp VALUES ('pp-editor', 1, 'Visualization', 'Switch');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-publisher', 'Publisher');
INSERT INTO TopicProp VALUES ('pp-publisher', 1, 'Name', 'Publisher');
INSERT INTO TopicProp VALUES ('pp-publisher', 1, 'Visualization', 'Switch');
-- assign properties to association type "Membership"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-93', '', 'at-membership', 1, 'pp-editor', 1);
INSERT INTO AssociationProp VALUES ('a-93', 1, 'Ordinal Number', '10');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-94', '', 'at-membership', 1, 'pp-publisher', 1);
INSERT INTO AssociationProp VALUES ('a-94', 1, 'Ordinal Number', '20');



-----------------------------
--- New Feature: Calendar ---
-----------------------------

---
--- create topic type "Calendar" ---
---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-calendar', 'Calendar');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Name', 'Calendar');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Plural Name', 'Calendars');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Description', '<html><head></head><body><p>A <i>Calendar</i> is ...</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Description Query', 'What is a "Calendar"?');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Icon', 'calendar.png');
-- INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Creation Icon', 'createKompetenzstern.gif');
-- INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Custom Implementation', 'de.deepamehta.topics.CalendarTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-95', '', 'tt-generic', 1, 'tt-calendar', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-calendar-search', 'Calendar Search');
INSERT INTO TopicProp VALUES ('tt-calendar-search', 1, 'Name', 'Calendar Search');
-- INSERT INTO TopicProp VALUES ('tt-calendar-search', 1, 'Icon', 'KompetenzsternContainer.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-96', '', 'tt-topiccontainer', 1, 'tt-calendar-search', 1);
-- assign search type to type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-97', '', 'tt-calendar-search', 1, 'tt-calendar', 1);
-- create properties
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-displaydate', 'Display Date');
INSERT INTO TopicProp VALUES ('pp-displaydate', 1, 'Name', 'Display Date');
INSERT INTO TopicProp VALUES ('pp-displaydate', 1, 'Visualization', 'Date Chooser');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-displaymode', 'Display Mode');
INSERT INTO TopicProp VALUES ('pp-displaymode', 1, 'Name', 'Display Mode');
INSERT INTO TopicProp VALUES ('pp-displaymode', 1, 'Visualization', 'Options Menu');
-- create property values
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-day', 'Day');
INSERT INTO TopicProp VALUES ('t-day', 1, 'Name', 'Day');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-week', 'Week');
INSERT INTO TopicProp VALUES ('t-week', 1, 'Name', 'Week');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-month', 'Month');
INSERT INTO TopicProp VALUES ('t-month', 1, 'Name', 'Month');
-- assign property values to property
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-157', '', 'pp-displaymode', 1, 't-day', 1);
INSERT INTO AssociationProp VALUES ('a-157', 1, 'Ordinal Number', '1');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-162', '', 'pp-displaymode', 1, 't-week', 1);
INSERT INTO AssociationProp VALUES ('a-162', 1, 'Ordinal Number', '2');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-187', '', 'pp-displaymode', 1, 't-month', 1);
INSERT INTO AssociationProp VALUES ('a-187', 1, 'Ordinal Number', '3');
-- assign properties to topic type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-141', '', 'tt-calendar', 1, 'pp-displaydate', 1);
INSERT INTO AssociationProp VALUES ('a-141', 1, 'Ordinal Number', '220');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-148', '', 'tt-calendar', 1, 'pp-displaymode', 1);
INSERT INTO AssociationProp VALUES ('a-148', 1, 'Ordinal Number', '210');
-- create relation to "Person"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-190', '', 'tt-calendar', 1, 'tt-person', 1);
INSERT INTO AssociationProp VALUES ('a-190', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-190', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-190', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-190', 1, 'Web Form', 'Related Topic Selector');
INSERT INTO AssociationProp VALUES ('a-190', 1, 'Ordinal Number', '150');

---
--- create topic type "Event" ---
---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-event', 'Event');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Name', 'Event');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Plural Name', 'Events');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Description', '<html><head></head><body><p>An <i>Event</i> is ...</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Description Query', 'What is an "Event"?');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Icon', 'event.png');
-- INSERT INTO TopicProp VALUES ('tt-event', 1, 'Creation Icon', 'createKompetenzstern.gif');
-- INSERT INTO TopicProp VALUES ('tt-event', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Custom Implementation', 'de.deepamehta.topics.EventTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-98', '', 'tt-generic', 1, 'tt-event', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-event-search', 'Event Search');
INSERT INTO TopicProp VALUES ('tt-event-search', 1, 'Name', 'Event Search');
-- INSERT INTO TopicProp VALUES ('tt-event-search', 1, 'Icon', 'KompetenzsternContainer.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-99', '', 'tt-topiccontainer', 1, 'tt-event-search', 1);
-- assign search type to type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-109', '', 'tt-event-search', 1, 'tt-event', 1);
-- create properties
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-begindate', 'Begin Date');
INSERT INTO TopicProp VALUES ('pp-begindate', 1, 'Name', 'Begin Date');
INSERT INTO TopicProp VALUES ('pp-begindate', 1, 'Visualization', 'Date Chooser');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-begintime', 'Begin Time');
INSERT INTO TopicProp VALUES ('pp-begintime', 1, 'Name', 'Begin Time');
INSERT INTO TopicProp VALUES ('pp-begintime', 1, 'Visualization', 'Time Chooser');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-enddate', 'End Date');
INSERT INTO TopicProp VALUES ('pp-enddate', 1, 'Name', 'End Date');
INSERT INTO TopicProp VALUES ('pp-enddate', 1, 'Visualization', 'Date Chooser');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-endtime', 'End Time');
INSERT INTO TopicProp VALUES ('pp-endtime', 1, 'Name', 'End Time');
INSERT INTO TopicProp VALUES ('pp-endtime', 1, 'Visualization', 'Time Chooser');
-- assign properties to topic type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-110', '', 'tt-event', 1, 'pp-begindate', 1);
INSERT INTO AssociationProp VALUES ('a-110', 1, 'Ordinal Number', '110');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-111', '', 'tt-event', 1, 'pp-begintime', 1);
INSERT INTO AssociationProp VALUES ('a-111', 1, 'Ordinal Number', '120');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-112', '', 'tt-event', 1, 'pp-enddate', 1);
INSERT INTO AssociationProp VALUES ('a-112', 1, 'Ordinal Number', '130');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-113', '', 'tt-event', 1, 'pp-endtime', 1);
INSERT INTO AssociationProp VALUES ('a-113', 1, 'Ordinal Number', '140');
-- create relation to "Person"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-246', 'Attendee', 'tt-event', 1, 'tt-person', 1);
INSERT INTO AssociationProp VALUES ('a-246', 1, 'Name', 'Attendee');
INSERT INTO AssociationProp VALUES ('a-246', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-246', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-246', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-246', 1, 'Web Form', 'Related Topic Selector');
INSERT INTO AssociationProp VALUES ('a-246', 1, 'Ordinal Number', '150');

---
--- assign topic types to workspace "DeepaMehta"
---
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-114', '', 't-corporategroup', 1, 'tt-calendar', 1);
INSERT INTO AssociationProp VALUES ('a-114', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-114', 1, 'Ordinal Number', '50');
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-115', '', 't-corporategroup', 1, 'tt-event', 1);
INSERT INTO AssociationProp VALUES ('a-115', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-115', 1, 'Ordinal Number', '55');

---
--- delete "City" and "Country" assignments from workspace "DeepaMehta"
---
DELETE FROM Association WHERE ID='a-324';
DELETE FROM AssociationProp WHERE AssociationID='a-324';
DELETE FROM ViewAssociation WHERE AssociationID='a-324';
DELETE FROM Association WHERE ID='a-325';
DELETE FROM AssociationProp WHERE AssociationID='a-325';
DELETE FROM ViewAssociation WHERE AssociationID='a-325';

-- fixing a Typo in whois.sql
UPDATE Association SET TopicID1='tt-whoistopic' WHERE TopicID1='tt-whoisTopic';


---
--- Update standard installation
---
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b8'         WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b8'   WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';
DELETE FROM TopicProp                                     WHERE TopicID='t-deepamehtainstallation' AND PropName='Corporate Icon';
INSERT INTO TopicProp VALUES ('t-deepamehtainstallation', 1, 'Customer Icon', 'deepamehta-logo-tiny.png');



--- *** UPDATE DATA DEFINITION *** ---
ALTER TABLE       TopicProp CHANGE PropName PropName CHAR(255) NOT NULL;
ALTER TABLE AssociationProp CHANGE PropName PropName CHAR(255) NOT NULL;
