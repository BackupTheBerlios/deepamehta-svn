------------------
--- Datasource ---
------------------



--- XML Datasource ---
-- The "Movies and Actors" example map
INSERT INTO Topic VALUES ('tt-project', 1, 1, 't-moviesxmlmap', 'Movies and Actors (XML-Source)');
-- "Movies" datasource
INSERT INTO Topic VALUES ('tt-datasource', 1, 1, 't-moviesxmlsource', 'Movies (XML-File)');
INSERT INTO TopicProp VALUES ('t-moviesxmlsource', 1, 'URL', 'xml:../../examples/Movies.xml');
INSERT INTO TopicProp VALUES ('t-moviesxmlsource', 1, 'Entities', 'Movie Actor Association');
-- Associate types with datasource
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-518', '', 'tt-movie', 1, 't-moviesxmlsource', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-519', '', 'tt-moviecontainer', 1, 't-moviesxmlsource', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-520', '', 'tt-actor', 1, 't-moviesxmlsource', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-521', '', 'tt-actorcontainer', 1, 't-moviesxmlsource', 1);
