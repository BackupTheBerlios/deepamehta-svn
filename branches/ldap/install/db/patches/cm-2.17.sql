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

--- create topic type "Calendar" ---
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
-- assign properties
-- INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-ka-73', '', 'tt-calendar', 1, 'pp-color', 1);
-- INSERT INTO AssociationProp VALUES ('a-ka-73', 1, 'Ordinal Number', '50');

--- create topic type "Event" ---
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
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-110', '', 'tt-event', 1, 'pp-begindate', 1);
INSERT INTO AssociationProp VALUES ('a-110', 1, 'Ordinal Number', '110');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-111', '', 'tt-event', 1, 'pp-begintime', 1);
INSERT INTO AssociationProp VALUES ('a-111', 1, 'Ordinal Number', '120');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-112', '', 'tt-event', 1, 'pp-enddate', 1);
INSERT INTO AssociationProp VALUES ('a-112', 1, 'Ordinal Number', '130');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-113', '', 'tt-event', 1, 'pp-endtime', 1);
INSERT INTO AssociationProp VALUES ('a-113', 1, 'Ordinal Number', '140');

--- assign topic types to workspace "DeepaMehta"
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-114', '', 't-corporategroup', 1, 'tt-calendar', 1);
INSERT INTO AssociationProp VALUES ('a-114', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-114', 1, 'Ordinal Number', '50');
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-115', '', 't-corporategroup', 1, 'tt-event', 1);
INSERT INTO AssociationProp VALUES ('a-115', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-115', 1, 'Ordinal Number', '55');

--- delete "City" and "Country" assignments from workspace "DeepaMehta"
DELETE FROM Association WHERE ID='a-324';
DELETE FROM AssociationProp WHERE AssociationID='a-324';
DELETE FROM ViewAssociation WHERE AssociationID='a-324';
DELETE FROM Association WHERE ID='a-325';
DELETE FROM AssociationProp WHERE AssociationID='a-325';
DELETE FROM ViewAssociation WHERE AssociationID='a-325';



--- *** UPDATE DATA DEFINITION *** ---
ALTER TABLE       TopicProp CHANGE PropName PropName CHAR(255) NOT NULL;
ALTER TABLE AssociationProp CHANGE PropName PropName CHAR(255) NOT NULL;
