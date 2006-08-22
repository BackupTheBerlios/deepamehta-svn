--- "Patch Email" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-recipient', 'Recipient');
INSERT INTO TopicProp VALUES ('at-recipient', 1, 'Name', 'Recipient');
INSERT INTO TopicProp VALUES ('at-recipient', 1, 'Plural Name', 'Recipients');
INSERT INTO TopicProp VALUES ('at-recipient', 1, 'Color', '#E14589');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-327', '', 'at-generic', 1, 'at-recipient', 1);

--- "Patch Email" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-sender', 'Sender');
INSERT INTO TopicProp VALUES ('at-sender', 1, 'Name', 'Sender');
-- INSERT INTO TopicProp VALUES ('at-sender', 1, 'Plural Name', 'Senders');
INSERT INTO TopicProp VALUES ('at-sender', 1, 'Color', '#4589E1');
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-329', '', 'at-generic', 1, 'at-sender', 1);
