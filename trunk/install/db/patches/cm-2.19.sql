---
--- This patch updates CM 2.18 to 2.19
--- Apply this patch if you want to update DeepaMehta rev346-20080910 to 2.0b8 while keeping your content
---



----------------------------
--- Update Email Feature ---
----------------------------



--- set custom implementation for "Person Search" ---
INSERT INTO TopicProp VALUES ('tt-personcontainer', 1, 'Custom Implementation', 'de.deepamehta.topics.PersonSearchTopic');
