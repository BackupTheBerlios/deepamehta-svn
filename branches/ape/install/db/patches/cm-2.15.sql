---
--- This patch updates CM 2.14 to 2.15
--- Apply this patch if you want to update DeepaMehta 2.0b5 to 2.0b6 while keeping your content
---



-- reorder Association Type's "Plural Name"
UPDATE AssociationProp SET PropValue='110' WHERE AssociationID='a-241' AND PropName='Ordinal Number';

-- dimm "Type Access" color
UPDATE TopicProp SET PropValue='#00E0FF' WHERE TopicID='at-uses' AND PropName='Color';

--- *** UPDATE DATA DEFINITION *** ---
ALTER TABLE       Topic CHANGE Name Name CHAR(255) NOT NULL;
ALTER TABLE Association CHANGE Name Name CHAR(255) NOT NULL;



---
--- Update standard installation
---
UPDATE TopicProp SET PropValue='DeepaMehta 2.0b6'       WHERE TopicID='t-deepamehtainstallation' AND PropName='Client Name';
UPDATE TopicProp SET PropValue='DeepaMehtaServer 2.0b6' WHERE TopicID='t-deepamehtainstallation' AND PropName='Server Name';

---
--- Update DB content version
---
UPDATE KeyGenerator SET NextKey=15 WHERE Relation='DB-Content Version';
