---
--- This patch updates CM 2.16 to 2.17
--- Apply this patch if you want to update DeepaMehta 2.0b7 to 2.0b8-rc4 while keeping your content
---


---------------------------------------------------
--- Extended Access Control: Define 2 new roles ---
---------------------------------------------------

-- create properties "Editor" and "Publisher"
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

-- set "Publisher" property for all existing memberships (to emulate 2.0b7 behavoir)
INSERT INTO AssociationProp SELECT ID, 1, 'Publisher', 'on' FROM Association WHERE TypeID = 'at-membership';



--------------------------------
--- Rearrange standard types ---
--------------------------------

-- delete "City", "Country" and "Appointment" assignments from workspace "DeepaMehta"
DELETE FROM Association WHERE ID='a-324';
DELETE FROM AssociationProp WHERE AssociationID='a-324';
DELETE FROM ViewAssociation WHERE AssociationID='a-324';
DELETE FROM Association WHERE ID='a-325';
DELETE FROM AssociationProp WHERE AssociationID='a-325';
DELETE FROM ViewAssociation WHERE AssociationID='a-325';
DELETE FROM Association WHERE ID='a-186';
DELETE FROM AssociationProp WHERE AssociationID='a-186';
DELETE FROM ViewAssociation WHERE AssociationID='a-186';



---------------------------------
--- New Application: Calendar ---
---------------------------------

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
INSERT INTO TopicProp VALUES ('tt-calendar-search', 1, 'Icon', 'calendar-search.gif');
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
--- create topic type "Appointment" ---
---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-event', 'Appointment');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Name', 'Appointment');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Plural Name', 'Appointments');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Description', '<html><head></head><body><p>An <i>Appointment</i> is ...</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Description Query', 'What is an "Appointment"?');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Icon', 'appointment.gif');
-- INSERT INTO TopicProp VALUES ('tt-event', 1, 'Creation Icon', 'createKompetenzstern.gif');
-- INSERT INTO TopicProp VALUES ('tt-event', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Custom Implementation', 'de.deepamehta.topics.AppointmentTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-98', '', 'tt-generic', 1, 'tt-event', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-event-search', 'Appointment Search');
INSERT INTO TopicProp VALUES ('tt-event-search', 1, 'Name', 'Appointment Search');
INSERT INTO TopicProp VALUES ('tt-event-search', 1, 'Icon', 'appointmentcontainer.gif');
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
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-323', 'Attendee', 'tt-event', 1, 'tt-person', 1);
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Name', 'Attendee');
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Web Form', 'Related Topic Selector');
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Ordinal Number', '150');
-- create relation to "Location"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-338', '', 'tt-event', 1, 'tt-location', 1);
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Name', '');
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Cardinality', 'one');
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Web Form', 'Related Topic Selector');
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Ordinal Number', '150');

---
--- create topic type "Event" ---
---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-alldayevent', 'Event');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Name', 'Event');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Plural Name', 'Events');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Description', '<html><head></head><body><p>An <i>Event</i> is ...</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Description Query', 'What is an "Event"?');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Icon', 'event.png');
-- INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Creation Icon', 'createKompetenzstern.gif');
-- INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Custom Implementation', 'de.deepamehta.topics.EventTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-324', '', 'tt-generic', 1, 'tt-alldayevent', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-alldayevent-search', 'Event Search');
INSERT INTO TopicProp VALUES ('tt-alldayevent-search', 1, 'Name', 'Event Search');
INSERT INTO TopicProp VALUES ('tt-alldayevent-search', 1, 'Icon', 'event-search.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-325', '', 'tt-topiccontainer', 1, 'tt-alldayevent-search', 1);
-- assign search type to type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-330', '', 'tt-alldayevent-search', 1, 'tt-alldayevent', 1);
-- assign properties to topic type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-331', '', 'tt-alldayevent', 1, 'pp-begindate', 1);
INSERT INTO AssociationProp VALUES ('a-331', 1, 'Ordinal Number', '110');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-332', '', 'tt-alldayevent', 1, 'pp-enddate', 1);
INSERT INTO AssociationProp VALUES ('a-332', 1, 'Ordinal Number', '130');

---
--- create topic type "Location" ---
---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-location', 'Location');
INSERT INTO TopicProp VALUES ('tt-location', 1, 'Name', 'Location');
INSERT INTO TopicProp VALUES ('tt-location', 1, 'Plural Name', 'Locations');
INSERT INTO TopicProp VALUES ('tt-location', 1, 'Description', '<html><head></head><body><p>A <i>Location</i> is ...</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-location', 1, 'Description Query', 'What is a "Location"?');
INSERT INTO TopicProp VALUES ('tt-location', 1, 'Icon', 'location.png');
-- INSERT INTO TopicProp VALUES ('tt-location', 1, 'Creation Icon', 'createKompetenzstern.gif');
-- INSERT INTO TopicProp VALUES ('tt-location', 1, 'Unique Topic Names', 'on');
-- INSERT INTO TopicProp VALUES ('tt-location', 1, 'Custom Implementation', 'de.deepamehta.topics.EventTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-334', '', 'tt-generic', 1, 'tt-location', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-location-search', 'Location Search');
INSERT INTO TopicProp VALUES ('tt-location-search', 1, 'Name', 'Location Search');
-- INSERT INTO TopicProp VALUES ('tt-location-search', 1, 'Icon', 'event-search.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-335', '', 'tt-topiccontainer', 1, 'tt-location-search', 1);
-- assign search type to type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-336', '', 'tt-location-search', 1, 'tt-location', 1);
-- create relation to "Address"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-337', '', 'tt-location', 1, 'tt-address', 1);
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Name', '');
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Cardinality', 'one');
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Web Form', 'Related Topic Selector');
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Ordinal Number', '150');

---
--- assign topic types to workspace "DeepaMehta"
---
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-114', '', 't-corporategroup', 1, 'tt-calendar', 1);
INSERT INTO AssociationProp VALUES ('a-114', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-114', 1, 'Ordinal Number', '50');
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-115', '', 't-corporategroup', 1, 'tt-event', 1);
INSERT INTO AssociationProp VALUES ('a-115', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-115', 1, 'Ordinal Number', '55');
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-333', '', 't-corporategroup', 1, 'tt-alldayevent', 1);
INSERT INTO AssociationProp VALUES ('a-333', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-333', 1, 'Ordinal Number', '60');



-----------------------------------------
--- Redefine Topic Type "Data Source" ---
-----------------------------------------

-- create 3 new properties "Database Type", "Username", "Password"
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-dbtype', 'Database Type');
INSERT INTO TopicProp VALUES ('pp-dbtype', 1, 'Name', 'Database Type');
INSERT INTO TopicProp VALUES ('pp-dbtype', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-dbuser', 'Username');
INSERT INTO TopicProp VALUES ('pp-dbuser', 1, 'Name', 'Username');
INSERT INTO TopicProp VALUES ('pp-dbuser', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-dbpassword', 'Password');
INSERT INTO TopicProp VALUES ('pp-dbpassword', 1, 'Name', 'Password');
INSERT INTO TopicProp VALUES ('pp-dbpassword', 1, 'Visualization', 'Input Field');

-- remove "Driver" property
DELETE FROM Association WHERE ID='a-21';
DELETE FROM AssociationProp WHERE AssociationID='a-21';
DELETE FROM ViewAssociation WHERE AssociationID='a-21';

-- assign 3 new properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-21', '', 'tt-datasource', 1, 'pp-dbtype', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-21a', '', 'tt-datasource', 1, 'pp-dbuser', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-21b', '', 'tt-datasource', 1, 'pp-dbpassword', 1);
INSERT INTO AssociationProp VALUES ('a-21', 1, 'Ordinal Number', '105');
INSERT INTO AssociationProp VALUES ('a-21a', 1, 'Ordinal Number', '120');
INSERT INTO AssociationProp VALUES ('a-21b', 1, 'Ordinal Number', '125');



--------------------------------------
--- Fixing some Typos in whois.sql ---
--------------------------------------

UPDATE Association SET TopicID1='tt-whoistopic' WHERE TopicID1='tt-whoisTopic';

DELETE FROM TopicProp where topicid='tt-whois6';
INSERT INTO TopicProp VALUES ('tt-whois6', 1, 'Name', 'Whois Server 6');
INSERT INTO TopicProp VALUES ('tt-whois6', 1, 'Server', 'whois.nic.as');
INSERT INTO TopicProp VALUES ('tt-whois6', 1, 'Domains', 'as');

INSERT INTO TopicProp VALUES ('tt-whois18', 1, 'Server', 'whois.nic.fr');
INSERT INTO TopicProp VALUES ('tt-whois18', 1, 'Domains', 'fr');



-------------------------
--- Set new eye icons ---
-------------------------



UPDATE TopicProp SET PropValue='eye.gif' WHERE TopicID='tt-topicmap' AND PropName='Icon';
UPDATE TopicProp SET PropValue='eye-with-wand.gif' WHERE TopicID='tt-topicmap' AND PropName='Creation Icon';
UPDATE TopicProp SET PropValue='eye-in-ton.gif' WHERE TopicID='tt-topicmapcontainer' AND PropName='Icon';



------------------------------------
--- Update standard installation ---
------------------------------------

-- set new icon for "DeepaMehta" installation
DELETE FROM TopicProp                                     WHERE TopicID='t-deepamehtainstallation' AND PropName='Corporate Icon';
INSERT INTO TopicProp VALUES ('t-deepamehtainstallation', 1, 'Customer Icon', 'deepamehta-logo-tiny.png');



-----------------------
--- Version Control ---
-----------------------

-- change version labels
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b8'         WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b8'   WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';

-- update DB content version
UPDATE KeyGenerator SET NextKey=17 WHERE Relation='DB-Content Version';



--------------------------------------
--- *** UPDATE DATA DEFINITION *** ---
--------------------------------------

-- set primary key for "TopicProp" and "AssociationProp" tables
ALTER TABLE             TopicProp
     CHANGE             PropName PropName CHAR(255) NOT NULL,
     DROP   INDEX       TopicID,
     ADD    PRIMARY KEY (TopicID, TopicVersion, PropName)
;
ALTER TABLE             AssociationProp
     CHANGE             PropName PropName CHAR(255) NOT NULL,
     DROP   INDEX       AssociationID,
     ADD    PRIMARY KEY (AssociationID, AssociationVersion, PropName)
;



SELECT *
FROM TopicProp
JOIN (
    SELECT TopicID, TopicVersion, PropName
    FROM TopicProp GROUP BY TopicID, TopicVersion, PropName
    HAVING COUNT(*) > 1 ) tp
USING (TopicID, TopicVersion, PropName);

SELECT *
FROM AssociationProp
JOIN (
    SELECT AssociationID, AssociationVersion, PropName
    FROM AssociationProp GROUP BY AssociationID, AssociationVersion, PropName
    HAVING COUNT(*) > 1 ) ap
USING (AssociationID, AssociationVersion, PropName);
