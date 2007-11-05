----------------------------
--- Workspace "Artfacts" ---
----------------------------



INSERT INTO Topic VALUES ('tt-workspace', 1, 1, 't-artfactsworkgroup', 'Artfacts');
INSERT INTO TopicProp VALUES ('t-artfactsworkgroup', 1, 'Name', 'Artfacts');
INSERT INTO TopicProp VALUES ('t-artfactsworkgroup', 1, 'Public', 'on');
INSERT INTO TopicProp VALUES ('t-artfactsworkgroup', 1, 'Default', 'off');
-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-artfactsworkspace', 'Artfacts');
INSERT INTO TopicProp VALUES ('t-artfactsworkspace', 1, 'Name', 'Artfacts');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-af-1', '', 't-artfactsworkgroup', 1, 't-artfactsworkspace', 1);
-- chat
INSERT INTO Topic VALUES ('tt-chatboard', 1, 1, 't-artfactschat', 'Artfacts Chats');
INSERT INTO TopicProp VALUES ('t-artfactschat', 1, 'Name', 'Artfacts Chats');
INSERT INTO ViewTopic VALUES ('t-artfactsworkspace', 1, 't-artfactschat', 1, 200, 100);
-- forum
INSERT INTO Topic VALUES ('tt-messageboard', 1, 1, 't-artfactsforum', 'Artfacts Forum');
INSERT INTO TopicProp VALUES ('t-artfactsforum', 1, 'Name', 'Artfacts Forum');
INSERT INTO ViewTopic VALUES ('t-artfactsworkspace', 1, 't-artfactsforum', 1, 200, 50);
-- assign topic types
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-af-5', '', 't-artfactsworkgroup', 1, 'tt-af-artwork', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-af-13', '', 't-artfactsworkgroup', 1, 'tt-af-artist', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-af-20', '', 't-artfactsworkgroup', 1, 'tt-af-gallery', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-af-28', '', 't-artfactsworkgroup', 1, 'tt-af-exhibition', 1);
INSERT INTO AssociationProp VALUES ('a-af-5', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-af-13', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-af-20', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-af-28', 1, 'Access Permission', 'view');



------------------
--- Datasource ---
------------------



-- "Artfacts" datasource
INSERT INTO Topic VALUES ('tt-datasource', 1, 1, 't-af-artfactsdb', 'Artfacts');
INSERT INTO TopicProp VALUES ('t-af-artfactsdb', 1, 'Name', 'Artfacts');
INSERT INTO TopicProp VALUES ('t-af-artfactsdb', 1, 'URL', 'jdbc:mysql://127.0.0.1/Artfacts?user=artfacts&password=artfacts&useUnicode=true&characterEncoding=latin1');
INSERT INTO TopicProp VALUES ('t-af-artfactsdb', 1, 'Driver', 'org.gjt.mm.mysql.Driver');
INSERT INTO TopicProp VALUES ('t-af-artfactsdb', 1, 'Idle Elementtype', 'Artwork');
-- Associate types with datasource
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-af-6', '', 'tt-af-artwork', 1, 't-af-artfactsdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-af-7', '', 'tt-af-artworkcontainer', 1, 't-af-artfactsdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-af-14', '', 'tt-af-artist', 1, 't-af-artfactsdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-af-15', '', 'tt-af-artistcontainer', 1, 't-af-artfactsdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-af-21', '', 'tt-af-gallery', 1, 't-af-artfactsdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-af-22', '', 'tt-af-gallerycontainer', 1, 't-af-artfactsdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-af-29', '', 'tt-af-exhibition', 1, 't-af-artfactsdb', 1);
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-af-30', '', 'tt-af-exhibitioncontainer', 1, 't-af-artfactsdb', 1);



-------------------
--- Topic Types ---
-------------------



--- "Artwork" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-af-artwork', 'Artwork');
INSERT INTO TopicProp VALUES ('tt-af-artwork', 1, 'Name', 'Artwork');
INSERT INTO TopicProp VALUES ('tt-af-artwork', 1, 'Plural Name', 'Artworks');
INSERT INTO TopicProp VALUES ('tt-af-artwork', 1, 'Icon', 'artwork.gif');
INSERT INTO TopicProp VALUES ('tt-af-artwork', 1, 'Custom Implementation', 'de.deepamehta.artfacts.topics.ArtworkTopic');
-- assign properties
-- INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-af-9', '', 'tt-af-artwork', 1, 'pp-af-artworktitle', 1);
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-af-16', '', 'tt-generic', 1, 'tt-af-artwork', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-af-artworkcontainer', 'Artwork Search');
INSERT INTO TopicProp VALUES ('tt-af-artworkcontainer', 1, 'Name', 'Artwork Search');
INSERT INTO TopicProp VALUES ('tt-af-artworkcontainer', 1, 'Icon', 'artwork-search.gif');
INSERT INTO TopicProp VALUES ('tt-af-artworkcontainer', 1, 'Custom Implementation', 'de.deepamehta.artfacts.topics.ArtworkSearchTopic');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-af-3', '', 'tt-elementcontainer', 1, 'tt-af-artworkcontainer', 1);
-- assign properties to search type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-af-8', '', 'tt-af-artworkcontainer', 1, 'pp-af-artworktitle', 1);
-- assign type to search type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-af-4', '', 'tt-af-artworkcontainer', 1, 'tt-af-artwork', 1);

--- "Artist" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-af-artist', 'Artist');
INSERT INTO TopicProp VALUES ('tt-af-artist', 1, 'Name', 'Artist');
INSERT INTO TopicProp VALUES ('tt-af-artist', 1, 'Plural Name', 'Artists');
INSERT INTO TopicProp VALUES ('tt-af-artist', 1, 'Icon', 'artist.gif');
INSERT INTO TopicProp VALUES ('tt-af-artist', 1, 'Custom Implementation', 'de.deepamehta.artfacts.topics.ArtistTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-af-9', '', 'tt-af-artist', 1, 'pp-af-birthlocation', 1);
INSERT INTO AssociationProp VALUES('a-af-9', 1, 'Ordinal Number', '140');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-af-2', '', 'tt-person', 1, 'tt-af-artist', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-af-artistcontainer', 'Artist Search');
INSERT INTO TopicProp VALUES ('tt-af-artistcontainer', 1, 'Name', 'Artist Search');
INSERT INTO TopicProp VALUES ('tt-af-artistcontainer', 1, 'Icon', 'artist-search.gif');
INSERT INTO TopicProp VALUES ('tt-af-artistcontainer', 1, 'Custom Implementation', 'de.deepamehta.artfacts.topics.ArtistSearchTopic');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-af-10', '', 'tt-elementcontainer', 1, 'tt-af-artistcontainer', 1);
-- assign properties to search type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-af-11', '', 'tt-af-artistcontainer', 1, 'pp-surname2', 1);
-- assign type to search type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-af-12', '', 'tt-af-artistcontainer', 1, 'tt-af-artist', 1);

--- "Gallery" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-af-gallery', 'Gallery');
INSERT INTO TopicProp VALUES ('tt-af-gallery', 1, 'Name', 'Gallery');
INSERT INTO TopicProp VALUES ('tt-af-gallery', 1, 'Plural Name', 'Galleries');
INSERT INTO TopicProp VALUES ('tt-af-gallery', 1, 'Icon', 'gallery.gif');
INSERT INTO TopicProp VALUES ('tt-af-gallery', 1, 'Custom Implementation', 'de.deepamehta.artfacts.topics.GalleryTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-af-33', '', 'tt-af-gallery', 1, 'pp-af-foundationyear', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-af-34', '', 'tt-af-gallery', 1, 'pp-af-employees', 1);
INSERT INTO AssociationProp VALUES ('a-af-33', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-af-34', 1, 'Ordinal Number', '210');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-af-17', '', 'tt-institution', 1, 'tt-af-gallery', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-af-gallerycontainer', 'Gallery Search');
INSERT INTO TopicProp VALUES ('tt-af-gallerycontainer', 1, 'Name', 'Gallery Search');
INSERT INTO TopicProp VALUES ('tt-af-gallerycontainer', 1, 'Icon', 'gallery-search.gif');
INSERT INTO TopicProp VALUES ('tt-af-gallerycontainer', 1, 'Custom Implementation', 'de.deepamehta.artfacts.topics.GallerySearchTopic');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-af-18', '', 'tt-elementcontainer', 1, 'tt-af-gallerycontainer', 1);
-- assign properties to search type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-af-23', '', 'tt-af-gallerycontainer', 1, 'pp-name', 1);
-- assign type to search type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-af-19', '', 'tt-af-gallerycontainer', 1, 'tt-af-gallery', 1);

--- "Exhibition" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-af-exhibition', 'Exhibition');
INSERT INTO TopicProp VALUES ('tt-af-exhibition', 1, 'Name', 'Exhibition');
INSERT INTO TopicProp VALUES ('tt-af-exhibition', 1, 'Plural Name', 'Exhibitions');
INSERT INTO TopicProp VALUES ('tt-af-exhibition', 1, 'Icon', 'exhibition.gif');
INSERT INTO TopicProp VALUES ('tt-af-exhibition', 1, 'Custom Implementation', 'de.deepamehta.artfacts.topics.ExhibitionTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-af-31', '', 'tt-af-exhibition', 1, 'pp-af-begindate', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-af-32', '', 'tt-af-exhibition', 1, 'pp-af-enddate', 1);
INSERT INTO AssociationProp VALUES ('a-af-31', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-af-32', 1, 'Ordinal Number', '120');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-af-24', '', 'tt-generic', 1, 'tt-af-exhibition', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-af-exhibitioncontainer', 'Exhibition Search');
INSERT INTO TopicProp VALUES ('tt-af-exhibitioncontainer', 1, 'Name', 'Exhibition Search');
INSERT INTO TopicProp VALUES ('tt-af-exhibitioncontainer', 1, 'Icon', 'exhibition-search.gif');
INSERT INTO TopicProp VALUES ('tt-af-exhibitioncontainer', 1, 'Custom Implementation', 'de.deepamehta.artfacts.topics.ExhibitionSearchTopic');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-af-25', '', 'tt-elementcontainer', 1, 'tt-af-exhibitioncontainer', 1);
-- assign properties to search type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-af-26', '', 'tt-af-exhibitioncontainer', 1, 'pp-af-artworktitle', 1);
-- assign type to search type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-af-27', '', 'tt-af-exhibitioncontainer', 1, 'tt-af-exhibition', 1);



------------------
--- Properties ---
------------------



--- "Birth Location" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-af-birthlocation', 'Birth Location');
INSERT INTO TopicProp VALUES ('pp-af-birthlocation', 1, 'Name', 'Birth Location');
INSERT INTO TopicProp VALUES ('pp-af-birthlocation', 1, 'Visualization', 'Input Field');

--- "Employees" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-af-employees', 'Employees');
INSERT INTO TopicProp VALUES ('pp-af-employees', 1, 'Name', 'Employees');
INSERT INTO TopicProp VALUES ('pp-af-employees', 1, 'Visualization', 'Input Field');

--- "Foundation Year" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-af-foundationyear', 'Foundation Year');
INSERT INTO TopicProp VALUES ('pp-af-foundationyear', 1, 'Name', 'Foundation Year');
INSERT INTO TopicProp VALUES ('pp-af-foundationyear', 1, 'Visualization', 'Input Field');

--- "Title" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-af-artworktitle', 'Title');
INSERT INTO TopicProp VALUES ('pp-af-artworktitle', 1, 'Name', 'Title');
INSERT INTO TopicProp VALUES ('pp-af-artworktitle', 1, 'Visualization', 'Input Field');

--- "Begin" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-af-begindate', 'Begin');
INSERT INTO TopicProp VALUES ('pp-af-begindate', 1, 'Name', 'Begin');
INSERT INTO TopicProp VALUES ('pp-af-begindate', 1, 'Visualization', 'Date Chooser');

--- "End" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-af-enddate', 'End');
INSERT INTO TopicProp VALUES ('pp-af-enddate', 1, 'Name', 'End');
INSERT INTO TopicProp VALUES ('pp-af-enddate', 1, 'Visualization', 'Date Chooser');



------------
--- View ---
------------



-- The "Artfacts" example view
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-af-artfactsview', 'Artfacts');
INSERT INTO TopicProp VALUES ('t-af-artfactsview', 1, 'Name', 'Artfacts');
-- prepare searches
INSERT INTO Topic VALUES ('tt-af-artistcontainer', 1, 1, 't-af-artists', 'Artists');
INSERT INTO Topic VALUES ('tt-af-artworkcontainer', 1, 1, 't-af-artworks', 'Artworks');
INSERT INTO Topic VALUES ('tt-af-gallerycontainer', 1, 1, 't-af-galleries', 'Galleries');
INSERT INTO Topic VALUES ('tt-af-exhibitioncontainer', 1, 1, 't-af-exhibitions', 'Exhibitions');
-- place topics in view
INSERT INTO ViewTopic VALUES ('t-af-artfactsview', 1, 't-af-artists', 1, 150, 100);
INSERT INTO ViewTopic VALUES ('t-af-artfactsview', 1, 't-af-artworks', 1, 270, 120);
INSERT INTO ViewTopic VALUES ('t-af-artfactsview', 1, 't-af-galleries', 1, 320, 80);
INSERT INTO ViewTopic VALUES ('t-af-artfactsview', 1, 't-af-exhibitions', 1, 500, 130);
-- place view in workspace
INSERT INTO ViewTopic VALUES ('t-artfactsworkspace', 1, 't-af-artfactsview', 1, 80, 80);
