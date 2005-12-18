--- create new visualization "Color Chooser" ---
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-color', 'Color Chooser');
INSERT INTO TopicProp VALUES ('t-color', 1, 'Name', 'Color Chooser');
INSERT INTO TopicProp VALUES ('t-color', 1, 'Icon', 'inputfield.gif');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-297', '', 'pp-visualization', 1, 't-color', 1);
INSERT INTO AssociationProp VALUES ('a-297', 1, 'Ordinal Number', '12');

--- change visualization of property "Color" ---
UPDATE TopicProp SET PropValue = 'Color Chooser' WHERE TopicID = 'pp-color' AND PropName = 'Visualization'; 

--- change visualization of property "Background Color" ---
UPDATE TopicProp SET PropValue = 'Color Chooser' WHERE TopicID = 'pp-bgcolor' AND PropName = 'Visualization';
 
