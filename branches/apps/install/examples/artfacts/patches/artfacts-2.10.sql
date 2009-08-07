--- Set new ordinal numbers ---
-- "Artist"
INSERT INTO AssociationProp VALUES('a-af-9', 1, 'Ordinal Number', '140');
-- "Gallery"
UPDATE AssociationProp SET PropValue='110' WHERE AssociationID='a-af-33' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='210' WHERE AssociationID='a-af-34' AND PropName='Ordinal Number';
-- "Exhibition"
UPDATE AssociationProp SET PropValue='110' WHERE AssociationID='a-af-31' AND PropName='Ordinal Number';
UPDATE AssociationProp SET PropValue='120' WHERE AssociationID='a-af-32' AND PropName='Ordinal Number';
