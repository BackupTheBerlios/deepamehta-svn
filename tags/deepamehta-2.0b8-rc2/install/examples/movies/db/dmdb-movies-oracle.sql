------------------
--- Datasource ---
------------------



--- SQL Datasource ---
-- "Movies" datasource
INSERT INTO Topic VALUES ('tt-datasource', 1, 1, 't-moviesdb', 'Movies (MySQL-DB)');
INSERT INTO TopicProp VALUES ('t-moviesdb', 1, 'Name', 'Movies (MySQL-DB)');
INSERT INTO TopicProp VALUES ('t-moviesdb', 1, 'URL', 'jdbc:oracle:thin:dm/dm@localhost:1521:orcl');
INSERT INTO TopicProp VALUES ('t-moviesdb', 1, 'Database Type', 'oracle');
INSERT INTO TopicProp VALUES ('t-moviesdb', 1, 'Idle Elementtype', 'Movie');
-- Associate types with datasource
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-514', '', 'tt-movie', 1, 't-moviesdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-515', '', 'tt-moviecontainer', 1, 't-moviesdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-516', '', 'tt-actor', 1, 't-moviesdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-517', '', 'tt-actorcontainer', 1, 't-moviesdb', 1);

--- XML Datasource --- ### can't be used together with SQL example
-- The "Movies and Actors" example map
-- INSERT INTO Topic VALUES ('tt-project', 1, 1, 't-moviesxmlmap', 'Movies and Actors (XML-Source)');
-- "Movies" datasource
-- INSERT INTO Topic VALUES ('tt-datasource', 1, 1, 't-moviesxmlsource', 'Movies (XML-File)');
-- INSERT INTO TopicProp VALUES ('t-moviesxmlsource', 1, 'URL', 'xml:../../examples/Movies.xml');
-- INSERT INTO TopicProp VALUES ('t-moviesxmlsource', 1, 'Entities', 'Movie Actor Association');
-- Associate types with datasource
-- INSERT INTO Association VALUES ('at-association', 1, 1, 'a-518', '', 'tt-movie', 1, 't-moviesxmlsource', 1);
-- INSERT INTO Association VALUES ('at-association', 1, 1, 'a-519', '', 'tt-moviecontainer', 1, 't-moviesxmlsource', 1);
-- INSERT INTO Association VALUES ('at-association', 1, 1, 'a-520', '', 'tt-actor', 1, 't-moviesxmlsource', 1);
-- INSERT INTO Association VALUES ('at-association', 1, 1, 'a-521', '', 'tt-actorcontainer', 1, 't-moviesxmlsource', 1);
