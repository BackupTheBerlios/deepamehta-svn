--------------------------
--- "Movies" workspace ---
--------------------------



-- workspace
INSERT INTO Topic VALUES ('tt-workspace', 1, 1, 't-examplesgroup', 'Movies');
INSERT INTO TopicProp VALUES ('t-examplesgroup', 1, 'Name', 'Movies');
INSERT INTO TopicProp VALUES ('t-examplesgroup', 1, 'Public', 'on');
INSERT INTO TopicProp VALUES ('t-examplesgroup', 1, 'Default', 'off');
-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-examplesgroupmap', 'Movies');
INSERT INTO TopicProp VALUES ('t-examplesgroupmap', 1, 'Name', 'Movies');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-502', '', 't-examplesgroup', 1, 't-examplesgroupmap', 1);
-- chat
INSERT INTO Topic VALUES ('tt-chatboard', 1, 1, 't-exampleschat', 'Movies Chats');
INSERT INTO TopicProp VALUES ('t-exampleschat', 1, 'Name', 'Movies Chats');
INSERT INTO ViewTopic VALUES ('t-examplesgroupmap', 1, 't-exampleschat', 1, 30, 100);
-- forum
INSERT INTO Topic VALUES ('tt-messageboard', 1, 1, 't-moviesforum', 'Movies Forum');
INSERT INTO TopicProp VALUES ('t-moviesforum', 1, 'Name', 'Movies Forum');
INSERT INTO ViewTopic VALUES ('t-examplesgroupmap', 1, 't-moviesforum', 1, 120, 100);
-- assign types to workspace
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-198', '', 't-examplesgroup', 1, 'tt-movie', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-199', '', 't-examplesgroup', 1, 'tt-actor', 1);
INSERT INTO AssociationProp VALUES ('a-198', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-199', 1, 'Access Permission', 'create');



-------------------
--- Topic Types ---
-------------------



--- "Movie" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-movie', 'Movie');
-- set properties
INSERT INTO TopicProp VALUES ('tt-movie', 1, 'Name', 'Movie');
INSERT INTO TopicProp VALUES ('tt-movie', 1, 'Plural Name', 'Movies');
INSERT INTO TopicProp VALUES ('tt-movie', 1, 'Icon', 'movie.gif');
INSERT INTO TopicProp VALUES ('tt-movie', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-movie', 1, 'Custom Implementation', 'de.deepamehta.movies.topics.MovieTopic');
-- assign properties
-- INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-506', '', 'tt-movie', 1, 'pp-title', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-507', '', 'tt-movie', 1, 'pp-year', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-508', '', 'tt-movie', 1, 'pp-country', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-509', '', 'tt-movie', 1, 'pp-duration', 1);
INSERT INTO AssociationProp VALUES ('a-507', 1, 'Ordinal Number', '120');
INSERT INTO AssociationProp VALUES ('a-508', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-509', 1, 'Ordinal Number', '130');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-506', '', 'tt-generic', 1, 'tt-movie', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-moviecontainer', 'Movie Search');
-- set properties of container type
INSERT INTO TopicProp VALUES ('tt-moviecontainer', 1, 'Name', 'Movie Search');
INSERT INTO TopicProp VALUES ('tt-moviecontainer', 1, 'Icon', 'moviecontainer.gif');
INSERT INTO TopicProp VALUES ('tt-moviecontainer', 1, 'Custom Implementation', 'de.deepamehta.movies.topics.MovieContainerTopic');
-- assign properties to container type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-510', '', 'tt-moviecontainer', 1, 'pp-title', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-511', '', 'tt-moviecontainer', 1, 'pp-year', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-512', '', 'tt-moviecontainer', 1, 'pp-country', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-513', '', 'tt-actorcontainer', 1, 'pp-name', 1);
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-503', '', 'tt-elementcontainer', 1, 'tt-moviecontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-518', '', 'tt-moviecontainer', 1, 'tt-movie', 1);

--- "Actor" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-actor', 'Actor');
-- set properties
INSERT INTO TopicProp VALUES ('tt-actor', 1, 'Name', 'Actor');
INSERT INTO TopicProp VALUES ('tt-actor', 1, 'Plural Name', 'Actors');
INSERT INTO TopicProp VALUES ('tt-actor', 1, 'Icon', 'actor.gif');
INSERT INTO TopicProp VALUES ('tt-actor', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-actor', 1, 'Custom Implementation', 'de.deepamehta.movies.topics.ActorTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-505', '', 'tt-person', 1, 'tt-actor', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-actorcontainer', 'Actor Search');
-- set properties of container type
INSERT INTO TopicProp VALUES ('tt-actorcontainer', 1, 'Name', 'Actor Search');
INSERT INTO TopicProp VALUES ('tt-actorcontainer', 1, 'Icon', 'actorcontainer.gif');
INSERT INTO TopicProp VALUES ('tt-actorcontainer', 1, 'Custom Implementation', 'de.deepamehta.movies.topics.ActorContainerTopic');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-504', '', 'tt-elementcontainer', 1, 'tt-actorcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-519', '', 'tt-actorcontainer', 1, 'tt-actor', 1);



------------------
--- Properties ---
------------------



INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-title', 'Title');
INSERT INTO TopicProp VALUES ('pp-title', 1, 'Name', 'Title');
INSERT INTO TopicProp VALUES ('pp-title', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-year', 'YearOfProduction');
INSERT INTO TopicProp VALUES ('pp-year', 1, 'Name', 'YearOfProduction');
INSERT INTO TopicProp VALUES ('pp-year', 1, 'Visualization', 'Input Field');

INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-country', 'Country');
INSERT INTO TopicProp VALUES ('pp-country', 1, 'Name', 'Country');
INSERT INTO TopicProp VALUES ('pp-country', 1, 'Visualization', 'Input Field');



------------------
--- Datasource ---
------------------

-- This will be done by another script



--------------------
--- Example View ---
--------------------



-- The "Movies and Actors" example map
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-moviesmap', 'Movies and Actors');
INSERT INTO TopicProp VALUES ('t-moviesmap', 1, 'Name', 'Movies and Actors');
INSERT INTO TopicProp VALUES ('t-moviesmap', 1, 'Description', 'This example demonstrates the integration of corporate datasources.\n\nThe "Movies and Actors" example uses a SQL-Datasource.');
-- "Movies" and "Actors" container
INSERT INTO Topic VALUES ('tt-moviecontainer', 1, 1, 't-movies', 'Movies');
INSERT INTO Topic VALUES ('tt-actorcontainer', 1, 1, 't-actors', 'Actors');

INSERT INTO ViewTopic VALUES ('t-moviesmap', 1, 't-movies', 1, 150, 100);
INSERT INTO ViewTopic VALUES ('t-moviesmap', 1, 't-actors', 1, 270, 120);

INSERT INTO ViewTopic VALUES ('t-examplesgroupmap', 1, 't-moviesmap', 1, 100, 50);
