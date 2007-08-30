------------------
--- Datasource ---
------------------



--- SQL Datasource ---
-- "Movies" datasource
INSERT INTO Topic VALUES ('tt-datasource', 1, 1, 't-moviesdb', 'Movies (MySQL-DB)');
INSERT INTO TopicProp VALUES ('t-moviesdb', 1, 'Name', 'Movies (MySQL-DB)');
INSERT INTO TopicProp VALUES ('t-moviesdb', 1, 'URL', 'jdbc:hsqldb:../client/db/hsqldb-intern/Movies');
INSERT INTO TopicProp VALUES ('t-moviesdb', 1, 'Driver', 'org.hsqldb.jdbcDriver');
INSERT INTO TopicProp VALUES ('t-moviesdb', 1, 'Idle Elementtype', 'Movie');
-- Associate types with datasource
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-514', '', 'tt-movie', 1, 't-moviesdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-515', '', 'tt-moviecontainer', 1, 't-moviesdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-516', '', 'tt-actor', 1, 't-moviesdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-517', '', 'tt-actorcontainer', 1, 't-moviesdb', 1);
