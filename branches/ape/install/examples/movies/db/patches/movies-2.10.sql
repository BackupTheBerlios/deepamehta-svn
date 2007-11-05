UPDATE TopicProp SET PropValue='de.deepamehta.movies.topics.MovieTopic' WHERE TopicID='tt-movie' AND PropName='Custom Implementation';
UPDATE TopicProp SET PropValue='de.deepamehta.movies.topics.MovieContainerTopic' WHERE TopicID='tt-moviecontainer' AND PropName='Custom Implementation';
UPDATE TopicProp SET PropValue='de.deepamehta.movies.topics.ActorTopic' WHERE TopicID='tt-actor' AND PropName='Custom Implementation';
UPDATE TopicProp SET PropValue='de.deepamehta.movies.topics.ActorContainerTopic' WHERE TopicID='tt-actorcontainer' AND PropName='Custom Implementation';

INSERT INTO AssociationProp VALUES ('a-508', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-507', 1, 'Ordinal Number', '120');
INSERT INTO AssociationProp VALUES ('a-509', 1, 'Ordinal Number', '130');
