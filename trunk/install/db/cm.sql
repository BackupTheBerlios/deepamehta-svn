
-- Set up the initial corporate memory 2.17 for use with DeepaMehta 2.0b8
-- Works with all database types (MySQL, HSQL, Derby, and Oracle)



-------------------
--- Topic Types ---
-------------------



--- "Topic" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-generic', 'Topic');
INSERT INTO TopicProp VALUES ('tt-generic', 1, 'Name', 'Topic');
INSERT INTO TopicProp VALUES ('tt-generic', 1, 'Plural Name', 'Topics');
INSERT INTO TopicProp VALUES ('tt-generic', 1, 'Icon', 'generic.gif');
INSERT INTO TopicProp VALUES ('tt-generic', 1, 'Unique Topic Names', 'on');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-295', '', 'tt-generic', 1, 'pp-name', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-11', '', 'tt-generic', 1, 'pp-info', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-4', '', 'tt-generic', 1, 'pp-icon', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-91', '', 'tt-generic', 1, 'pp-owner', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-254', '', 'tt-generic', 1, 'pp-geometrylocked', 1);
INSERT INTO AssociationProp VALUES ('a-295', 1, 'Ordinal Number', '100');
INSERT INTO AssociationProp VALUES ('a-11', 1, 'Ordinal Number', '200');
INSERT INTO AssociationProp VALUES ('a-4', 1, 'Ordinal Number', '300');
INSERT INTO AssociationProp VALUES ('a-91', 1, 'Ordinal Number', '400');
INSERT INTO AssociationProp VALUES ('a-254', 1, 'Ordinal Number', '500');
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-genericcontainer', 'Topic Search');
INSERT INTO TopicProp VALUES ('tt-genericcontainer', 1, 'Name', 'Topic Search');
INSERT INTO TopicProp VALUES ('tt-genericcontainer', 1, 'Icon', 'genericcontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-31', '', 'tt-topiccontainer', 1, 'tt-genericcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-61', '', 'tt-genericcontainer', 1, 'tt-generic', 1);

--- "Topic Type" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-topictype', 'Topic Type');
INSERT INTO TopicProp VALUES ('tt-topictype', 1, 'Name', 'Topic Type');
INSERT INTO TopicProp VALUES ('tt-topictype', 1, 'Plural Name', 'Topic Types');
INSERT INTO TopicProp VALUES ('tt-topictype', 1, 'Description', '<HTML><BODY>A <I>Topic Type</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-topictype', 1, 'Icon', 'topictype.gif');
INSERT INTO TopicProp VALUES ('tt-topictype', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-topictype', 1, 'Custom Implementation', 'de.deepamehta.topics.TopicTypeTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-59', '', 'tt-topictype', 1, 'pp-pluraltypename', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-284', '', 'tt-topictype', 1, 'pp-descriptionquery', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-283', '', 'tt-topictype', 1, 'pp-creationicon', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-13', '', 'tt-topictype', 1, 'pp-color', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-255', '', 'tt-topictype', 1, 'pp-disabled', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-249', '', 'tt-topictype', 1, 'pp-hideinstancenames', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-pp-unique', '', 'tt-topictype', 1, 'pp-unique', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-12', '', 'tt-topictype', 1, 'pp-implementingclass', 1);
INSERT INTO AssociationProp VALUES ('a-59',  1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-284', 1, 'Ordinal Number', '210');
INSERT INTO AssociationProp VALUES ('a-283', 1, 'Ordinal Number', '310');
INSERT INTO AssociationProp VALUES ('a-13',  1, 'Ordinal Number', '320');
INSERT INTO AssociationProp VALUES ('a-255', 1, 'Ordinal Number', '330');
INSERT INTO AssociationProp VALUES ('a-249', 1, 'Ordinal Number', '340');
INSERT INTO AssociationProp VALUES ('a-pp-unique', 1, 'Ordinal Number', '350');
INSERT INTO AssociationProp VALUES ('a-12',  1, 'Ordinal Number', '360');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-5', '', 'tt-generic', 1, 'tt-topictype', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-topictypecontainer', 'Topic Type Search');
INSERT INTO TopicProp VALUES ('tt-topictypecontainer', 1, 'Name', 'Topic Type Search');
INSERT INTO TopicProp VALUES ('tt-topictypecontainer', 1, 'Icon', 'topictypecontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-32', '', 'tt-topiccontainer', 1, 'tt-topictypecontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-62', '', 'tt-topictypecontainer', 1, 'tt-topictype', 1);

--- "Association Type" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-assoctype', 'Association Type');
INSERT INTO TopicProp VALUES ('tt-assoctype', 1, 'Name', 'Association Type');
INSERT INTO TopicProp VALUES ('tt-assoctype', 1, 'Plural Name', 'Association Types');
INSERT INTO TopicProp VALUES ('tt-assoctype', 1, 'Description', '<HTML><BODY>A <I>Association Type</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-assoctype', 1, 'Icon', 'associationtype.gif');
INSERT INTO TopicProp VALUES ('tt-assoctype', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-assoctype', 1, 'Custom Implementation', 'de.deepamehta.topics.AssociationTypeTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-241', '', 'tt-assoctype', 1, 'pp-pluraltypename', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-15', '', 'tt-assoctype', 1, 'pp-descriptionquery', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-14', '', 'tt-assoctype', 1, 'pp-color', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-258', '', 'tt-assoctype', 1, 'pp-disabled', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-242', '', 'tt-assoctype', 1, 'pp-implementingclass', 1);
INSERT INTO AssociationProp VALUES ('a-241', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-15', 1, 'Ordinal Number', '220');
INSERT INTO AssociationProp VALUES ('a-14', 1, 'Ordinal Number', '230');
INSERT INTO AssociationProp VALUES ('a-258', 1, 'Ordinal Number', '320');
INSERT INTO AssociationProp VALUES ('a-242', 1, 'Ordinal Number', '350');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-256', '', 'tt-generic', 1, 'tt-assoctype', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-assoctypecontainer', 'Association Type Search');
INSERT INTO TopicProp VALUES ('tt-assoctypecontainer', 1, 'Name', 'Association Type Search');
INSERT INTO TopicProp VALUES ('tt-assoctypecontainer', 1, 'Icon', 'associationtypecontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-33', '', 'tt-topiccontainer', 1, 'tt-assoctypecontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-63', '', 'tt-assoctypecontainer', 1, 'tt-assoctype', 1);

--- "Property" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-property', 'Property');
INSERT INTO TopicProp VALUES ('tt-property', 1, 'Name', 'Property');
INSERT INTO TopicProp VALUES ('tt-property', 1, 'Plural Name', 'Properties');
INSERT INTO TopicProp VALUES ('tt-property', 1, 'Description', '<HTML><BODY>A <I>Property</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-property', 1, 'Icon', 'property.gif');
INSERT INTO TopicProp VALUES ('tt-property', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-property', 1, 'Custom Implementation', 'de.deepamehta.topics.PropertyTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-16', '', 'tt-property', 1, 'pp-visualization', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-92', '', 'tt-property', 1, 'pp-defaultvalue', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-285', '', 'tt-property', 1, 'pp-editicon', 1);
INSERT INTO AssociationProp VALUES ('a-16', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-92', 1, 'Ordinal Number', '120');
INSERT INTO AssociationProp VALUES ('a-285', 1, 'Ordinal Number', '310');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-257', '', 'tt-generic', 1, 'tt-property', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-propertycontainer', 'Property Search');
INSERT INTO TopicProp VALUES ('tt-propertycontainer', 1, 'Name', 'Property Search');
INSERT INTO TopicProp VALUES ('tt-propertycontainer', 1, 'Icon', 'propertycontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-34', '', 'tt-topiccontainer', 1, 'tt-propertycontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-64', '', 'tt-propertycontainer', 1, 'tt-property', 1);

--- "Property Value" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-constant', 'Property Value');
INSERT INTO TopicProp VALUES ('tt-constant', 1, 'Name', 'Property Value');
INSERT INTO TopicProp VALUES ('tt-constant', 1, 'Plural Name', 'Property Values');
INSERT INTO TopicProp VALUES ('tt-constant', 1, 'Description', '<HTML><BODY>A <I>Property Value</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-constant', 1, 'Icon', 'constant.gif');
INSERT INTO TopicProp VALUES ('tt-constant', 1, 'Unique Topic Names', 'on');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-17', '', 'tt-generic', 1, 'tt-constant', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-constantcontainer', 'Property Value Search');
INSERT INTO TopicProp VALUES ('tt-constantcontainer', 1, 'Name', 'Property Value Search');
INSERT INTO TopicProp VALUES ('tt-constantcontainer', 1, 'Icon', 'constantcontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-35', '', 'tt-topiccontainer', 1, 'tt-constantcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-65', '', 'tt-constantcontainer', 1, 'tt-constant', 1);

--- "Search" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-container', 'Search');
INSERT INTO TopicProp VALUES ('tt-container', 1, 'Name', 'Search');
INSERT INTO TopicProp VALUES ('tt-container', 1, 'Description', '<HTML><BODY>A <I>Search</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-container', 1, 'Icon', 'container.gif');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-18', '', 'tt-container', 1, 'pp-elementcount', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-19', '', 'tt-container', 1, 'pp-queryelements', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-45', '', 'tt-container', 1, 'pp-relatedtopicid', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-173', '', 'tt-container', 1, 'pp-assoctypeid', 1);

--- "Data Source" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-datasource', 'Data Source');
INSERT INTO TopicProp VALUES ('tt-datasource', 1, 'Name', 'Data Source');
INSERT INTO TopicProp VALUES ('tt-datasource', 1, 'Plural Name', 'Data Sources');
INSERT INTO TopicProp VALUES ('tt-datasource', 1, 'Description', '<HTML><BODY>A <I>Data Source</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-datasource', 1, 'Icon', 'datasource.gif');
INSERT INTO TopicProp VALUES ('tt-datasource', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-datasource', 1, 'Custom Implementation', 'de.deepamehta.topics.DataSourceTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-20', '', 'tt-datasource', 1, 'pp-url', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-21', '', 'tt-datasource', 1, 'pp-dbtype', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-21a', '', 'tt-datasource', 1, 'pp-dbuser', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-21b', '', 'tt-datasource', 1, 'pp-dbpassword', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-134', '', 'tt-datasource', 1, 'pp-idleelementtype', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-22', '', 'tt-datasource', 1, 'pp-entities', 1);
INSERT INTO AssociationProp VALUES ('a-20', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-21', 1, 'Ordinal Number', '105');
INSERT INTO AssociationProp VALUES ('a-21a', 1, 'Ordinal Number', '120');
INSERT INTO AssociationProp VALUES ('a-21b', 1, 'Ordinal Number', '125');
INSERT INTO AssociationProp VALUES ('a-134', 1, 'Ordinal Number', '130');
INSERT INTO AssociationProp VALUES ('a-22', 1, 'Ordinal Number', '140');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-47', '', 'tt-generic', 1, 'tt-datasource', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-datasourcecontainer', 'Data Source Search');
INSERT INTO TopicProp VALUES ('tt-datasourcecontainer', 1, 'Name', 'Data Source Search');
INSERT INTO TopicProp VALUES ('tt-datasourcecontainer', 1, 'Icon', 'datasourcecontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-36', '', 'tt-topiccontainer', 1, 'tt-datasourcecontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-66', '', 'tt-datasourcecontainer', 1, 'tt-datasource', 1);

--- "Webpage" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-webpage', 'Webpage');
INSERT INTO TopicProp VALUES ('tt-webpage', 1, 'Name', 'Webpage');
INSERT INTO TopicProp VALUES ('tt-webpage', 1, 'Plural Name', 'Webpages');
INSERT INTO TopicProp VALUES ('tt-webpage', 1, 'Description', '<HTML><BODY>A <I>Webpage</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-webpage', 1, 'Icon', 'webpage.gif');
INSERT INTO TopicProp VALUES ('tt-webpage', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-webpage', 1, 'Custom Implementation', 'de.deepamehta.topics.WebpageTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-23', '', 'tt-webpage', 1, 'pp-url', 1);
-- INSERT INTO AssociationProp VALUES ('a-23', 1, 'Ordinal Number', '1');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-46', '', 'tt-generic', 1, 'tt-webpage', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-webpagecontainer', 'Webpage Search');
INSERT INTO TopicProp VALUES ('tt-webpagecontainer', 1, 'Name', 'Webpage Search');
INSERT INTO TopicProp VALUES ('tt-webpagecontainer', 1, 'Icon', 'webpagecontainer.gif');
-- assign properties to container type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-10', '', 'tt-webpagecontainer', 1, 'pp-url', 1);
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-37', '', 'tt-topiccontainer', 1, 'tt-webpagecontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-67', '', 'tt-webpagecontainer', 1, 'tt-webpage', 1);

--- "Website" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-website', 'Website');
INSERT INTO TopicProp VALUES ('tt-website', 1, 'Name', 'Website');
INSERT INTO TopicProp VALUES ('tt-website', 1, 'Plural Name', 'Websites');
INSERT INTO TopicProp VALUES ('tt-website', 1, 'Description', '<HTML><BODY>A <I>Website</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-website', 1, 'Icon', 'website.gif');
INSERT INTO TopicProp VALUES ('tt-website', 1, 'Unique Topic Names', 'on');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-211', '', 'tt-generic', 1, 'tt-website', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-websitecontainer', 'Website Search');
INSERT INTO TopicProp VALUES ('tt-websitecontainer', 1, 'Name', 'Website Search');
INSERT INTO TopicProp VALUES ('tt-websitecontainer', 1, 'Icon', 'websitecontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-213', '', 'tt-topiccontainer', 1, 'tt-websitecontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-215', '', 'tt-websitecontainer', 1, 'tt-website', 1);

--- "Internet Domain" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-internetdomain', 'Internet Domain');
INSERT INTO TopicProp VALUES ('tt-internetdomain', 1, 'Name', 'Internet Domain');
INSERT INTO TopicProp VALUES ('tt-internetdomain', 1, 'Plural Name', 'Internet Domains');
INSERT INTO TopicProp VALUES ('tt-internetdomain', 1, 'Description', '<HTML><BODY>An <I>Internet Domain</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-internetdomain', 1, 'Icon', 'internetdomain.gif');
INSERT INTO TopicProp VALUES ('tt-internetdomain', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-internetdomain', 1, 'Custom Implementation', 'de.deepamehta.topics.InternetDomainTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-217', '', 'tt-internetdomain', 1, 'pp-domaininfo', 1);
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-212', '', 'tt-generic', 1, 'tt-internetdomain', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-internetdomaincontainer', 'Internet Domain Search');
INSERT INTO TopicProp VALUES ('tt-internetdomaincontainer', 1, 'Name', 'Internet Domain Search');
INSERT INTO TopicProp VALUES ('tt-internetdomaincontainer', 1, 'Icon', 'internetdomaincontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-214', '', 'tt-topiccontainer', 1, 'tt-internetdomaincontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-216', '', 'tt-internetdomaincontainer', 1, 'tt-internetdomain', 1);

--- "Email Address" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-emailaddress', 'Email Address');
INSERT INTO TopicProp VALUES ('tt-emailaddress', 1, 'Name', 'Email Address');
INSERT INTO TopicProp VALUES ('tt-emailaddress', 1, 'Plural Name', 'Email Addresses');
INSERT INTO TopicProp VALUES ('tt-emailaddress', 1, 'Description', '<HTML><BODY>An <I>Email Address</I> is ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-emailaddress', 1, 'Icon', 'emailtopicmap.gif');
INSERT INTO TopicProp VALUES ('tt-emailaddress', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-emailaddress', 1, 'Custom Implementation', 'de.deepamehta.topics.EmailAddressTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-402', '', 'tt-emailaddress', 1, 'pp-emailaddress', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-403', '', 'tt-emailaddress', 1, 'pp-mailboxurl', 1);
INSERT INTO AssociationProp VALUES ('a-402', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-403', 1, 'Ordinal Number', '2');
-- super type
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-emailaddresscontainer', 'Email Address Search');
INSERT INTO TopicProp VALUES ('tt-emailaddresscontainer', 1, 'Name', 'Email Address Search');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-299', '', 'tt-topiccontainer', 1, 'tt-emailaddresscontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-401', '', 'tt-emailaddresscontainer', 1, 'tt-emailaddress', 1);

--- "Person" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-person', 'Person');
INSERT INTO TopicProp VALUES ('tt-person', 1, 'Name', 'Person');
INSERT INTO TopicProp VALUES ('tt-person', 1, 'Plural Name', 'Persons');
INSERT INTO TopicProp VALUES ('tt-person', 1, 'Description', '<HTML><BODY>A <I>Person</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-person', 1, 'Icon', 'person.gif');
INSERT INTO TopicProp VALUES ('tt-person', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-person', 1, 'Custom Implementation', 'de.deepamehta.topics.PersonTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-296', '', 'tt-person', 1, 'pp-forename', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-147', '', 'tt-person', 1, 'pp-gender', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-30', '', 'tt-person', 1, 'pp-birthday', 1);
INSERT INTO AssociationProp VALUES ('a-296', 1, 'Ordinal Number', '90');
INSERT INTO AssociationProp VALUES ('a-147', 1, 'Ordinal Number', '105');
INSERT INTO AssociationProp VALUES ('a-30', 1, 'Ordinal Number', '110');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-137', '', 'tt-generic', 1, 'tt-person', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-personcontainer', 'Person Search');
INSERT INTO TopicProp VALUES ('tt-personcontainer', 1, 'Name', 'Person Search');
INSERT INTO TopicProp VALUES ('tt-personcontainer', 1, 'Icon', 'personcontainer.gif');
INSERT INTO TopicProp VALUES ('tt-personcontainer', 1, 'Custom Implementation', 'de.deepamehta.topics.PersonSearchTopic');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-38', '', 'tt-topiccontainer', 1, 'tt-personcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-68', '', 'tt-personcontainer', 1, 'tt-person', 1);
-- relation to "Address"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-308', '', 'tt-person', 1, 'tt-address', 1);
INSERT INTO AssociationProp VALUES ('a-308', 1, 'Cardinality', 'one');
INSERT INTO AssociationProp VALUES ('a-308', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-308', 1, 'Web Info', 'Deeply Related Info');
INSERT INTO AssociationProp VALUES ('a-308', 1, 'Web Form', 'Deeply Related Form');
INSERT INTO AssociationProp VALUES ('a-308', 1, 'Ordinal Number', '140');
-- relation to "Email Address"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-404', '', 'tt-person', 1, 'tt-emailaddress', 1);
INSERT INTO AssociationProp VALUES ('a-404', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-404', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-404', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-404', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-404', 1, 'Ordinal Number', '170');
-- relation to "Phone Number"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-107', '', 'tt-person', 1, 'tt-phonenumber', 1);
INSERT INTO AssociationProp VALUES ('a-107', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-107', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-107', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-107', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-107', 1, 'Ordinal Number', '160');
-- relation to "Fax Number"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-108', '', 'tt-person', 1, 'tt-faxnumber', 1);
INSERT INTO AssociationProp VALUES ('a-108', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-108', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-108', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-108', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-108', 1, 'Ordinal Number', '165');
-- relation to "Mobile Number"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-55', '', 'tt-person', 1, 'tt-personphone', 1);
INSERT INTO AssociationProp VALUES ('a-55', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-55', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-55', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-55', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-55', 1, 'Ordinal Number', '168');
-- relation to "Webpage"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-416', '', 'tt-person', 1, 'tt-webpage', 1);
INSERT INTO AssociationProp VALUES ('a-416', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-416', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-416', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-416', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-416', 1, 'Ordinal Number', '150');

--- "Institution" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-institution', 'Institution');
-- properties
INSERT INTO TopicProp VALUES ('tt-institution', 1, 'Name', 'Institution');
INSERT INTO TopicProp VALUES ('tt-institution', 1, 'Plural Name', 'Institutions');
INSERT INTO TopicProp VALUES ('tt-institution', 1, 'Icon', 'institution.gif');
INSERT INTO TopicProp VALUES ('tt-institution', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-institution', 1, 'Custom Implementation', 'de.deepamehta.topics.InstitutionTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-316', '', 'tt-generic', 1, 'tt-institution', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-institutioncontainer', 'Institution Search');
INSERT INTO TopicProp VALUES ('tt-institutioncontainer', 1, 'Name', 'Institution Search');
INSERT INTO TopicProp VALUES ('tt-institutioncontainer', 1, 'Icon', 'institution-search.gif');
INSERT INTO TopicProp VALUES ('tt-institutioncontainer', 1, 'Custom Implementation', 'de.deepamehta.topics.InstitutionSearchTopic');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-317', '', 'tt-topiccontainer', 1, 'tt-institutioncontainer', 1);
-- assign topic type to its container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-318', '', 'tt-institutioncontainer', 1, 'tt-institution', 1);
-- relation to "Address"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-319', '', 'tt-institution', 1, 'tt-address', 1);
INSERT INTO AssociationProp VALUES ('a-319', 1, 'Cardinality', 'one');
INSERT INTO AssociationProp VALUES ('a-319', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-319', 1, 'Web Info', 'Deeply Related Info');
INSERT INTO AssociationProp VALUES ('a-319', 1, 'Web Form', 'Deeply Related Form');
INSERT INTO AssociationProp VALUES ('a-319', 1, 'Ordinal Number', '140');
-- relation to "Email Address"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-406', '', 'tt-institution', 1, 'tt-emailaddress', 1);
INSERT INTO AssociationProp VALUES ('a-406', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-406', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-406', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-406', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-406', 1, 'Ordinal Number', '170');
-- relation to "Phone Number"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-163', '', 'tt-institution', 1, 'tt-phonenumber', 1);
INSERT INTO AssociationProp VALUES ('a-163', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-163', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-163', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-163', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-163', 1, 'Ordinal Number', '160');
-- relation to "Fax Number"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-106', '', 'tt-institution', 1, 'tt-faxnumber', 1);
INSERT INTO AssociationProp VALUES ('a-106', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-106', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-106', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-106', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-106', 1, 'Ordinal Number', '165');
-- relation to "Webpage"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-417', '', 'tt-institution', 1, 'tt-webpage', 1);
INSERT INTO AssociationProp VALUES ('a-417', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-417', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-417', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-417', 1, 'Web Form', 'Related Form');
INSERT INTO AssociationProp VALUES ('a-417', 1, 'Ordinal Number', '150');

--- "Address" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-address', 'Address');
INSERT INTO TopicProp VALUES ('tt-address', 1, 'Name', 'Address');
INSERT INTO TopicProp VALUES ('tt-address', 1, 'Plural Name', 'Addresses');
INSERT INTO TopicProp VALUES ('tt-address', 1, 'Icon', 'address.gif');
INSERT INTO TopicProp VALUES ('tt-address', 1, 'Unique Topic Names', 'off');
INSERT INTO TopicProp VALUES ('tt-address', 1, 'Custom Implementation', 'de.deepamehta.topics.AddressTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-305', '', 'tt-address', 1, 'pp-street', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-306', '', 'tt-address', 1, 'pp-postalcode', 1);
INSERT INTO AssociationProp VALUES ('a-305', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-306', 1, 'Ordinal Number', '2');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-302', '', 'tt-generic', 1, 'tt-address', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-addresscontainer', 'Address Search');
INSERT INTO TopicProp VALUES ('tt-addresscontainer', 1, 'Name', 'Address Search');
INSERT INTO TopicProp VALUES ('tt-addresscontainer', 1, 'Icon', 'address-search.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-303', '', 'tt-topiccontainer', 1, 'tt-addresscontainer', 1);
-- assign topic type to its container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-304', '', 'tt-addresscontainer', 1, 'tt-address', 1);
--- Define Relations ---
-- "City"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-307', '', 'tt-address', 1, 'tt-city', 1);
INSERT INTO AssociationProp VALUES ('a-307', 1, 'Cardinality', 'one');
INSERT INTO AssociationProp VALUES ('a-307', 1, 'Association Type ID', 'at-association');

--- "Phone Number" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-phonenumber', 'Phone Number');
INSERT INTO TopicProp VALUES ('tt-phonenumber', 1, 'Name', 'Phone Number');
INSERT INTO TopicProp VALUES ('tt-phonenumber', 1, 'Plural Name', 'Phone Numbers');
INSERT INTO TopicProp VALUES ('tt-phonenumber', 1, 'Icon', 'phone.gif');
INSERT INTO TopicProp VALUES ('tt-phonenumber', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-phonenumber', 1, 'Custom Implementation', 'de.deepamehta.topics.PhoneNumberTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-48', '', 'tt-generic', 1, 'tt-phonenumber', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-phonenumbercontainer', 'Phone Number Search');
INSERT INTO TopicProp VALUES ('tt-phonenumbercontainer', 1, 'Name', 'Phone Number Search');
INSERT INTO TopicProp VALUES ('tt-phonenumbercontainer', 1, 'Icon', 'phone-search.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-49', '', 'tt-topiccontainer', 1, 'tt-phonenumbercontainer', 1);
-- assign type to search type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-50', '', 'tt-phonenumbercontainer', 1, 'tt-phonenumber', 1);

--- "Fax Number" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-faxnumber', 'Fax Number');
INSERT INTO TopicProp VALUES ('tt-faxnumber', 1, 'Name', 'Fax Number');
INSERT INTO TopicProp VALUES ('tt-faxnumber', 1, 'Plural Name', 'Fax Numbers');
INSERT INTO TopicProp VALUES ('tt-faxnumber', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-faxnumber', 1, 'Custom Implementation', 'de.deepamehta.topics.FaxNumberTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-103', '', 'tt-phonenumber', 1, 'tt-faxnumber', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-faxnumbersearch', 'Fax Number Search');
INSERT INTO TopicProp VALUES ('tt-faxnumbersearch', 1, 'Name', 'Fax Number Search');
INSERT INTO TopicProp VALUES ('tt-faxnumbersearch', 1, 'Icon', 'phone-search.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-104', '', 'tt-topiccontainer', 1, 'tt-faxnumbersearch', 1);
-- assign type to search type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-105', '', 'tt-faxnumbersearch', 1, 'tt-faxnumber', 1);

--- "Mobile Number" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-personphone', 'Mobile Number');
INSERT INTO TopicProp VALUES ('tt-personphone', 1, 'Name', 'Mobile Number');
INSERT INTO TopicProp VALUES ('tt-personphone', 1, 'Plural Name', 'Mobile Numbers');
INSERT INTO TopicProp VALUES ('tt-personphone', 1, 'Icon', 'mobile.gif');
INSERT INTO TopicProp VALUES ('tt-personphone', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-personphone', 1, 'Custom Implementation', 'de.deepamehta.topics.MobileNumberTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-100', '', 'tt-phonenumber', 1, 'tt-personphone', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-personphonesearch', 'Mobile Number Search');
INSERT INTO TopicProp VALUES ('tt-personphonesearch', 1, 'Name', 'Mobile Number Search');
INSERT INTO TopicProp VALUES ('tt-personphonesearch', 1, 'Icon', 'mobile-search.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-101', '', 'tt-topiccontainer', 1, 'tt-personphonesearch', 1);
-- assign type to search type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-102', '', 'tt-personphonesearch', 1, 'tt-personphone', 1);

--- "City" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-city', 'City');
-- properties
INSERT INTO TopicProp VALUES ('tt-city', 1, 'Name', 'City');
INSERT INTO TopicProp VALUES ('tt-city', 1, 'Plural Name', 'Cities');
INSERT INTO TopicProp VALUES ('tt-city', 1, 'Icon', 'city.gif');
INSERT INTO TopicProp VALUES ('tt-city', 1, 'Unique Topic Names', 'on');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-309', '', 'tt-generic', 1, 'tt-city', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-citycontainer', 'City Search');
INSERT INTO TopicProp VALUES ('tt-citycontainer', 1, 'Name', 'City Search');
INSERT INTO TopicProp VALUES ('tt-citycontainer', 1, 'Icon', 'city-search.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-310', '', 'tt-topiccontainer', 1, 'tt-citycontainer', 1);
-- assign topic type to its container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-311', '', 'tt-citycontainer', 1, 'tt-city', 1);
--- Define Relations ---
-- "Country"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-312', '', 'tt-city', 1, 'tt-country', 1);
INSERT INTO AssociationProp VALUES ('a-312', 1, 'Cardinality', 'one');
INSERT INTO AssociationProp VALUES ('a-312', 1, 'Association Type ID', 'at-association');

--- "Country" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-country', 'Country');
INSERT INTO TopicProp VALUES ('tt-country', 1, 'Name', 'Country');
INSERT INTO TopicProp VALUES ('tt-country', 1, 'Plural Name', 'Countries');
INSERT INTO TopicProp VALUES ('tt-country', 1, 'Icon', 'country.gif');
INSERT INTO TopicProp VALUES ('tt-country', 1, 'Unique Topic Names', 'on');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-313', '', 'tt-generic', 1, 'tt-country', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-countrycontainer', 'Country Search');
INSERT INTO TopicProp VALUES ('tt-countrycontainer', 1, 'Name', 'Country Search');
INSERT INTO TopicProp VALUES ('tt-countrycontainer', 1, 'Icon', 'country-search.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-314', '', 'tt-topiccontainer', 1, 'tt-countrycontainer', 1);
-- assign topic type to its container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-315', '', 'tt-countrycontainer', 1, 'tt-country', 1);

--- "Image" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-image', 'Image');
INSERT INTO TopicProp VALUES ('tt-image', 1, 'Name', 'Image');
INSERT INTO TopicProp VALUES ('tt-image', 1, 'Plural Name', 'Images');
INSERT INTO TopicProp VALUES ('tt-image', 1, 'Icon', 'image.gif');
INSERT INTO TopicProp VALUES ('tt-image', 1, 'Custom Implementation', 'de.deepamehta.topics.ImageTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-56', '', 'tt-image', 1, 'pp-width', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-72', '', 'tt-image', 1, 'pp-height', 1);
INSERT INTO AssociationProp VALUES ('a-56', 1, 'Ordinal Number', '20');
INSERT INTO AssociationProp VALUES ('a-72', 1, 'Ordinal Number', '30');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-27', '', 'tt-file', 1, 'tt-image', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-imagecontainer', 'Image Search');
INSERT INTO TopicProp VALUES ('tt-imagecontainer', 1, 'Name', 'Image Search');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-29', '', 'tt-topiccontainer', 1, 'tt-imagecontainer', 1);
-- assign topic type to its container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-42', '', 'tt-imagecontainer', 1, 'tt-image', 1);

--- "User" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-user', 'User');
INSERT INTO TopicProp VALUES ('tt-user', 1, 'Name', 'User');
INSERT INTO TopicProp VALUES ('tt-user', 1, 'Plural Name', 'Users');
INSERT INTO TopicProp VALUES ('tt-user', 1, 'Description', 'A "User" is a person who can login to DeepaMehta.\n\nA "User" can be assigned to workgroups by "membership" associations (direction is from user to group).\n\nA "User" can made be an administrator by assigning them to the "Administration" group. Administrators can create users and groups and can publish to the corporate space');
INSERT INTO TopicProp VALUES ('tt-user', 1, 'Icon', 'user.gif');
INSERT INTO TopicProp VALUES ('tt-user', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-user', 1, 'Custom Implementation', 'de.deepamehta.topics.UserTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-298', '', 'tt-user', 1, 'pp-username', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-24', '', 'tt-user', 1, 'pp-password', 1);
INSERT INTO AssociationProp VALUES ('a-298', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-24', 1, 'Ordinal Number', '2');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-9', '', 'tt-person', 1, 'tt-user', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-usercontainer', 'User Search');
INSERT INTO TopicProp VALUES ('tt-usercontainer', 1, 'Name', 'User Search');
INSERT INTO TopicProp VALUES ('tt-usercontainer', 1, 'Icon', 'usercontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-39', '', 'tt-topiccontainer', 1, 'tt-usercontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-69', '', 'tt-usercontainer', 1, 'tt-user', 1);

--- "Workspace" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-workspace', 'Workspace');
INSERT INTO TopicProp VALUES ('tt-workspace', 1, 'Name', 'Workspace');
INSERT INTO TopicProp VALUES ('tt-workspace', 1, 'Plural Name', 'Workspaces');
INSERT INTO TopicProp VALUES ('tt-workspace', 1, 'Icon', 'workgroup.gif');
INSERT INTO TopicProp VALUES ('tt-workspace', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-workspace', 1, 'Custom Implementation', 'de.deepamehta.topics.WorkspaceTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-400', '', 'tt-workspace', 1, 'pp-subscribable', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-247', '', 'tt-workspace', 1, 'pp-corporateworkgroup', 1);
INSERT INTO AssociationProp VALUES ('a-400', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-247', 1, 'Ordinal Number', '120');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-138', '', 'tt-generic', 1, 'tt-workspace', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-workgroupcontainer', 'Workspace Search');
INSERT INTO TopicProp VALUES ('tt-workgroupcontainer', 1, 'Name', 'Workspace Search');
INSERT INTO TopicProp VALUES ('tt-workgroupcontainer', 1, 'Icon', 'workgroupcontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-40', '', 'tt-topiccontainer', 1, 'tt-workgroupcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-70', '', 'tt-workgroupcontainer', 1, 'tt-workspace', 1);

--- "Topic Map" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-topicmap', 'Topic Map');
INSERT INTO TopicProp VALUES ('tt-topicmap', 1, 'Name', 'Topic Map');
INSERT INTO TopicProp VALUES ('tt-topicmap', 1, 'Plural Name', 'Topicmaps');
INSERT INTO TopicProp VALUES ('tt-topicmap', 1, 'Description', '<HTML><BODY>A <I>Topic Map</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-topicmap', 1, 'Icon', 'eye.gif');
INSERT INTO TopicProp VALUES ('tt-topicmap', 1, 'Creation Icon', 'eye-with-wand.gif');
INSERT INTO TopicProp VALUES ('tt-topicmap', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-topicmap', 1, 'Custom Implementation', 'de.deepamehta.topics.TopicMapTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-248', '', 'tt-topicmap', 1, 'pp-backgroundimage', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-25', '', 'tt-topicmap', 1, 'pp-bgcolor', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-140', '', 'tt-topicmap', 1, 'pp-translationUse', 1);
INSERT INTO AssociationProp VALUES ('a-248', 1, 'Ordinal Number', '310');
INSERT INTO AssociationProp VALUES ('a-25', 1, 'Ordinal Number', '320');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-135', '', 'tt-generic', 1, 'tt-topicmap', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-topicmapcontainer', 'Topic Map Search');
INSERT INTO TopicProp VALUES ('tt-topicmapcontainer', 1, 'Name', 'Topic Map Search');
INSERT INTO TopicProp VALUES ('tt-topicmapcontainer', 1, 'Icon', 'eye-in-ton.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-41', '', 'tt-topiccontainer', 1, 'tt-topicmapcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-71', '', 'tt-topicmapcontainer', 1, 'tt-topicmap', 1);

--- "File" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-file', 'File');
INSERT INTO TopicProp VALUES ('tt-file', 1, 'Name', 'File');
INSERT INTO TopicProp VALUES ('tt-file', 1, 'Icon', 'file.gif');
INSERT INTO TopicProp VALUES ('tt-file', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-file', 1, 'Custom Implementation', 'de.deepamehta.topics.FileTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-26', '', 'tt-file', 1, 'pp-file', 1);
INSERT INTO AssociationProp VALUES ('a-26', 1, 'Ordinal Number', '10');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-136', '', 'tt-generic', 1, 'tt-file', 1);

--- "Document" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-document', 'Document');
INSERT INTO TopicProp VALUES ('tt-document', 1, 'Name', 'Document');
INSERT INTO TopicProp VALUES ('tt-document', 1, 'Plural Name', 'Documents');
INSERT INTO TopicProp VALUES ('tt-document', 1, 'Description', '<HTML><BODY>A <I>Document</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-document', 1, 'Icon', 'document.gif');
INSERT INTO TopicProp VALUES ('tt-document', 1, 'Custom Implementation', 'de.deepamehta.topics.DocumentTopic');
INSERT INTO TopicProp VALUES ('tt-document', 1, 'Unique Topic Names', 'on');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-1', '', 'tt-file', 1, 'tt-document', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-documentcontainer', 'Document Search');
INSERT INTO TopicProp VALUES ('tt-documentcontainer', 1, 'Name', 'Document Search');
INSERT INTO TopicProp VALUES ('tt-documentcontainer', 1, 'Icon', 'documentcontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-43', '', 'tt-topiccontainer', 1, 'tt-documentcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-73', '', 'tt-documentcontainer', 1, 'tt-document', 1);

--- "Document Type" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-documenttype', 'Document Type');
INSERT INTO TopicProp VALUES ('tt-documenttype', 1, 'Name', 'Document Type');
INSERT INTO TopicProp VALUES ('tt-documenttype', 1, 'Plural Name', 'Document Types');
INSERT INTO TopicProp VALUES ('tt-documenttype', 1, 'Description', '<HTML><BODY>A <I>Document Type</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-documenttype', 1, 'Icon', 'documenttype.gif');
INSERT INTO TopicProp VALUES ('tt-documenttype', 1, 'Unique Topic Names', 'on');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-118', '', 'tt-generic', 1, 'tt-documenttype', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-documenttypecontainer', 'Document Type Search');
INSERT INTO TopicProp VALUES ('tt-documenttypecontainer', 1, 'Name', 'Document Type Search');
INSERT INTO TopicProp VALUES ('tt-documenttypecontainer', 1, 'Icon', 'documenttypecontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-88', '', 'tt-topiccontainer', 1, 'tt-documenttypecontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-75', '', 'tt-documenttypecontainer', 1, 'tt-documenttype', 1);

--- "MIME Type" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-mimetype', 'MIME Type');
INSERT INTO TopicProp VALUES ('tt-mimetype', 1, 'Name', 'MIME Type');
INSERT INTO TopicProp VALUES ('tt-mimetype', 1, 'Plural Name', 'MIME Types');
INSERT INTO TopicProp VALUES ('tt-mimetype', 1, 'Description', '<HTML><BODY>A <I>MIME Type</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-mimetype', 1, 'Icon', 'mimetype.gif');
INSERT INTO TopicProp VALUES ('tt-mimetype', 1, 'Unique Topic Names', 'on');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-119', '', 'tt-generic', 1, 'tt-mimetype', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-mimetypecontainer', 'MIME Type Search');
INSERT INTO TopicProp VALUES ('tt-mimetypecontainer', 1, 'Name', 'MIME Type Search');
INSERT INTO TopicProp VALUES ('tt-mimetypecontainer', 1, 'Icon', 'mimetypecontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-89', '', 'tt-topiccontainer', 1, 'tt-mimetypecontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-60', '', 'tt-mimetypecontainer', 1, 'tt-mimetype', 1);

--- "Application" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-application', 'Application');
INSERT INTO TopicProp VALUES ('tt-application', 1, 'Name', 'Application');
INSERT INTO TopicProp VALUES ('tt-application', 1, 'Plural Name', 'Applications');
INSERT INTO TopicProp VALUES ('tt-application', 1, 'Description', '<HTML><BODY>An <I>Application</I> is a ...</BODY></HTML>');
INSERT INTO TopicProp VALUES ('tt-application', 1, 'Icon', 'application.gif');
INSERT INTO TopicProp VALUES ('tt-application', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-application', 1, 'Custom Implementation', 'de.deepamehta.topics.ApplicationTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-2', '', 'tt-file', 1, 'tt-application', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-applicationcontainer', 'Application Search');
INSERT INTO TopicProp VALUES ('tt-applicationcontainer', 1, 'Name', 'Application Search');
INSERT INTO TopicProp VALUES ('tt-applicationcontainer', 1, 'Icon', 'applicationcontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-44', '', 'tt-topiccontainer', 1, 'tt-applicationcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-74', '', 'tt-applicationcontainer', 1, 'tt-application', 1);

--- "TopicContainer" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-topiccontainer', 'TopicContainer');
INSERT INTO TopicProp VALUES ('tt-topiccontainer', 1, 'Name', 'TopicContainer');
INSERT INTO TopicProp VALUES ('tt-topiccontainer', 1, 'Custom Implementation', 'de.deepamehta.topics.TopicContainerTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-28', '', 'tt-topiccontainer', 1, 'pp-topicname', 1);
INSERT INTO AssociationProp VALUES ('a-28', 1, 'Ordinal Number', '100');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-6', '', 'tt-container', 1, 'tt-topiccontainer', 1);

--- "ElementContainer" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-elementcontainer', 'ElementContainer');
INSERT INTO TopicProp VALUES ('tt-elementcontainer', 1, 'Name', 'ElementContainer');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-7', '', 'tt-container', 1, 'tt-elementcontainer', 1);

--- "Authentification Source" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-authentificationsource', 'Authentification Source');
INSERT INTO TopicProp VALUES ('tt-authentificationsource', 1, 'Name', 'Authentification Source');
INSERT INTO TopicProp VALUES ('tt-authentificationsource', 1, 'Plural Name', 'Authentification Sources');
INSERT INTO TopicProp VALUES ('tt-authentificationsource', 1, 'Icon', 'authentificationsource.gif');
INSERT INTO TopicProp VALUES ('tt-authentificationsource', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-authentificationsource', 1, 'Custom Implementation', 'de.deepamehta.topics.AuthentificationSourceTopic');
-- assign properties
-- ### INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-48', '', 'tt-authentificationsource', 1, 'pp-profileelementtype', 1);
-- ### INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-49', '', 'tt-authentificationsource', 1, 'pp-usernameattr', 1);
-- ### INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-50', '', 'tt-authentificationsource', 1, 'pp-passwordattr', 1);
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-150', '', 'tt-generic', 1, 'tt-authentificationsource', 1);
-- ### INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-15', '', 'tt-datasource', 1, 'tt-authentificationsource', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-authentificationsourcecontainer', 'Authentification Source Search');
INSERT INTO TopicProp VALUES ('tt-authentificationsourcecontainer', 1, 'Name', 'Authentification Source Search');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-90', '', 'tt-topiccontainer', 1, 'tt-authentificationsourcecontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-87', '', 'tt-authentificationsourcecontainer', 1, 'tt-authentificationsource', 1);

--- "Chat Board" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-chatboard', 'Chat Board');
INSERT INTO TopicProp VALUES ('tt-chatboard', 1, 'Name', 'Chat Board');
INSERT INTO TopicProp VALUES ('tt-chatboard', 1, 'Plural Name', 'Chat Boards');
INSERT INTO TopicProp VALUES ('tt-chatboard', 1, 'Icon', 'chat.gif');
INSERT INTO TopicProp VALUES ('tt-chatboard', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-chatboard', 1, 'Custom Implementation', 'de.deepamehta.topics.ChatBoardTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-120', '', 'tt-topicmap', 1, 'tt-chatboard', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-chatboardcontainer', 'Chat Board Search');
INSERT INTO TopicProp VALUES ('tt-chatboardcontainer', 1, 'Name', 'Chat Board Search');
INSERT INTO TopicProp VALUES ('tt-chatboardcontainer', 1, 'Icon', 'chat-search.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-121', '', 'tt-topiccontainer', 1, 'tt-chatboardcontainer', 1);
-- assign topic type to its container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-117', '', 'tt-chatboardcontainer', 1, 'tt-chatboard', 1);

--- "Chat" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-chat', 'Chat');
INSERT INTO TopicProp VALUES ('tt-chat', 1, 'Name', 'Chat');
INSERT INTO TopicProp VALUES ('tt-chat', 1, 'Plural Name', 'Chats');
INSERT INTO TopicProp VALUES ('tt-chat', 1, 'Icon', 'chat.gif');
INSERT INTO TopicProp VALUES ('tt-chat', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-chat', 1, 'Custom Implementation', 'de.deepamehta.topics.ChatTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-320', '', 'tt-chat', 1, 'pp-date', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-321', '', 'tt-chat', 1, 'pp-starttime', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-300', '', 'tt-chat', 1, 'pp-chatarea', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-301', '', 'tt-chat', 1, 'pp-userentry', 1);
INSERT INTO AssociationProp VALUES ('a-320', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-321', 1, 'Ordinal Number', '2');
INSERT INTO AssociationProp VALUES ('a-300', 1, 'Ordinal Number', '3');
INSERT INTO AssociationProp VALUES ('a-301', 1, 'Ordinal Number', '4');
-- super type
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-chatcontainer', 'Chat Search');
INSERT INTO TopicProp VALUES ('tt-chatcontainer', 1, 'Name', 'Chat Search');
INSERT INTO TopicProp VALUES ('tt-chatcontainer', 1, 'Icon', 'chat-search.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-122', '', 'tt-topiccontainer', 1, 'tt-chatcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-123', '', 'tt-chatcontainer', 1, 'tt-chat', 1);

--- "Message Board" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-messageboard', 'Message Board');
INSERT INTO TopicProp VALUES ('tt-messageboard', 1, 'Name', 'Message Board');
INSERT INTO TopicProp VALUES ('tt-messageboard', 1, 'Plural Name', 'Message Boards');
INSERT INTO TopicProp VALUES ('tt-messageboard', 1, 'Icon', 'messageboard.gif');
INSERT INTO TopicProp VALUES ('tt-messageboard', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-messageboard', 1, 'Custom Implementation', 'de.deepamehta.messageboard.topics.MessageBoardTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-859', '', 'tt-topicmap', 1, 'tt-messageboard', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-messageboardcontainer', 'Message Board Search');
INSERT INTO TopicProp VALUES ('tt-messageboardcontainer', 1, 'Name', 'Message Board Search');
INSERT INTO TopicProp VALUES ('tt-messageboardcontainer', 1, 'Icon', 'messageboard-search.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-860', '', 'tt-topiccontainer', 1, 'tt-messageboardcontainer', 1);
-- assign topic type to its container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-861', '', 'tt-messageboardcontainer', 1, 'tt-messageboard', 1);

--- "Message" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-message', 'Message');
INSERT INTO TopicProp VALUES ('tt-message', 1, 'Name', 'Message');
INSERT INTO TopicProp VALUES ('tt-message', 1, 'Plural Name', 'Messages');
INSERT INTO TopicProp VALUES ('tt-message', 1, 'Icon', 'message.gif');
INSERT INTO TopicProp VALUES ('tt-message', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-message', 1, 'Custom Implementation', 'de.deepamehta.messageboard.topics.MessageTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-865', '', 'tt-message', 1, 'pp-from', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-866', '', 'tt-message', 1, 'pp-date', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-869', '', 'tt-message', 1, 'pp-lastreplydate', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-870', '', 'tt-message', 1, 'pp-lastreplytime', 1);
INSERT INTO AssociationProp VALUES ('a-865', 1, 'Ordinal Number', '210');
INSERT INTO AssociationProp VALUES ('a-866', 1, 'Ordinal Number', '220');
INSERT INTO AssociationProp VALUES ('a-869', 1, 'Ordinal Number', '230');
INSERT INTO AssociationProp VALUES ('a-870', 1, 'Ordinal Number', '240');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-862', '', 'tt-generic', 1, 'tt-message', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-messagecontainer', 'Message Search');
INSERT INTO TopicProp VALUES ('tt-messagecontainer', 1, 'Name', 'Message Search');
INSERT INTO TopicProp VALUES ('tt-messagecontainer', 1, 'Icon', 'message-search.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-863', '', 'tt-topiccontainer', 1, 'tt-messagecontainer', 1);
-- assign topic type to its container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-864', '', 'tt-messagecontainer', 1, 'tt-message', 1);

--- "Appointment" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-appointment', 'Appointment');
INSERT INTO TopicProp VALUES ('tt-appointment', 1, 'Name', 'Appointment');
INSERT INTO TopicProp VALUES ('tt-appointment', 1, 'Plural Name', 'Appointments');
INSERT INTO TopicProp VALUES ('tt-appointment', 1, 'Icon', 'appointment.gif');
INSERT INTO TopicProp VALUES ('tt-appointment', 1, 'Unique Topic Names', 'on');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-294', '', 'tt-appointment', 1, 'pp-date', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-attr_ap4', '', 'tt-appointment', 1, 'pp-starttime', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-attr_ap5', '', 'tt-appointment', 1, 'pp-duration', 1);
INSERT INTO AssociationProp VALUES ('a-294', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-attr_ap4', 1, 'Ordinal Number', '120');
INSERT INTO AssociationProp VALUES ('a-attr_ap5', 1, 'Ordinal Number', '130');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-126', '', 'tt-generic', 1, 'tt-appointment', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-appointmentcontainer', 'Appointment Search');
INSERT INTO TopicProp VALUES ('tt-appointmentcontainer', 1, 'Name', 'Appointment Search');
INSERT INTO TopicProp VALUES ('tt-appointmentcontainer', 1, 'Icon', 'appointmentcontainer.gif');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-124', '', 'tt-topiccontainer', 1, 'tt-appointmentcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-125', '', 'tt-appointmentcontainer', 1, 'tt-appointment', 1);

--- "Export Format" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-exportformat', 'Export Format');
INSERT INTO TopicProp VALUES ('tt-exportformat', 1, 'Name', 'Export Format');
INSERT INTO TopicProp VALUES ('tt-exportformat', 1, 'Plural Name', 'Export Formats');
INSERT INTO TopicProp VALUES ('tt-exportformat', 1, 'Icon', 'document.gif');
INSERT INTO TopicProp VALUES ('tt-exportformat', 1, 'Unique Topic Names', 'on');
--- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-250', '', 'tt-generic', 1, 'tt-exportformat', 1);
--- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-exportformatcontainer', 'Export Format Search');
INSERT INTO TopicProp VALUES ('tt-exportformatcontainer', 1, 'Name', 'Export Format Search');
--- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-251', '', 'tt-topiccontainer', 1, 'tt-exportformatcontainer', 1);
--- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-252', '', 'tt-exportformatcontainer', 1, 'tt-exportformat', 1);

--- "Installation" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-installation', 'Installation');
INSERT INTO TopicProp VALUES ('tt-installation', 1, 'Name', 'Installation');
INSERT INTO TopicProp VALUES ('tt-installation', 1, 'Plural Name', 'Installations');
INSERT INTO TopicProp VALUES ('tt-installation', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-installation', 1, 'Custom Implementation', 'de.deepamehta.topics.InstallationTopic');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-276', '', 'tt-installation', 1, 'pp-active', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-275', '', 'tt-installation', 1, 'pp-clientname', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-274', '', 'tt-installation', 1, 'pp-servicename', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-279', '', 'tt-installation', 1, 'pp-language', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-277', '', 'tt-installation', 1, 'pp-serviceicon', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-278', '', 'tt-installation', 1, 'pp-customericon', 1);
INSERT INTO AssociationProp VALUES ('a-276', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-275', 1, 'Ordinal Number', '210');
INSERT INTO AssociationProp VALUES ('a-274', 1, 'Ordinal Number', '220');
INSERT INTO AssociationProp VALUES ('a-279', 1, 'Ordinal Number', '230');
INSERT INTO AssociationProp VALUES ('a-277', 1, 'Ordinal Number', '310');
INSERT INTO AssociationProp VALUES ('a-278', 1, 'Ordinal Number', '320');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-271', '', 'tt-generic', 1, 'tt-installation', 1);
-- container type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-installationcontainer', 'Installation Search');
INSERT INTO TopicProp VALUES ('tt-installationcontainer', 1, 'Name', 'Installation Search');
-- derive container type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-272', '', 'tt-topiccontainer', 1, 'tt-installationcontainer', 1);
-- assign type to container type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-273', '', 'tt-installationcontainer', 1, 'tt-installation', 1);

--- "CM Import/Export" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-cmimportexport', 'CM Import/Export');
INSERT INTO TopicProp VALUES ('tt-cmimportexport', 1, 'Name', 'CM Import/Export');
INSERT INTO TopicProp VALUES ('tt-cmimportexport', 1, 'Description', 'The CM Import/Export is used for importing/exporting of corporate memory from/to external archive file.');
INSERT INTO TopicProp VALUES ('tt-cmimportexport', 1, 'Icon', 'cmimportexport.gif');
INSERT INTO TopicProp VALUES ('tt-cmimportexport', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-cmimportexport', 1, 'Custom Implementation', 'de.deepamehta.topics.CMImportExportTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-142', '', 'tt-generic', 1, 'tt-cmimportexport', 1);

--- "Calendar" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-calendar', 'Calendar');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Name', 'Calendar');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Plural Name', 'Calendars');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Description', '<html><head></head><body><p>A <i>Calendar</i> is ...</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Description Query', 'What is a "Calendar"?');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Icon', 'calendar.png');
-- INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Creation Icon', 'createKompetenzstern.gif');
-- INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-calendar', 1, 'Custom Implementation', 'de.deepamehta.topics.CalendarTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-95', '', 'tt-generic', 1, 'tt-calendar', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-calendar-search', 'Calendar Search');
INSERT INTO TopicProp VALUES ('tt-calendar-search', 1, 'Name', 'Calendar Search');
INSERT INTO TopicProp VALUES ('tt-calendar-search', 1, 'Icon', 'calendar-search.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-96', '', 'tt-topiccontainer', 1, 'tt-calendar-search', 1);
-- assign search type to type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-97', '', 'tt-calendar-search', 1, 'tt-calendar', 1);

--- "Appointment" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-event', 'Appointment');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Name', 'Appointment');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Plural Name', 'Appointments');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Description', '<html><head></head><body><p>An <i>Appointment</i> is ...</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Description Query', 'What is an "Appointment"?');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Icon', 'appointment.gif');
-- INSERT INTO TopicProp VALUES ('tt-event', 1, 'Creation Icon', 'createKompetenzstern.gif');
-- INSERT INTO TopicProp VALUES ('tt-event', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-event', 1, 'Custom Implementation', 'de.deepamehta.topics.AppointmentTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-98', '', 'tt-generic', 1, 'tt-event', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-event-search', 'Appointment Search');
INSERT INTO TopicProp VALUES ('tt-event-search', 1, 'Name', 'Appointment Search');
INSERT INTO TopicProp VALUES ('tt-event-search', 1, 'Icon', 'appointmentcontainer.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-99', '', 'tt-topiccontainer', 1, 'tt-event-search', 1);
-- assign search type to type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-109', '', 'tt-event-search', 1, 'tt-event', 1);

--- "Event" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-alldayevent', 'Event');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Name', 'Event');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Plural Name', 'Events');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Description', '<html><head></head><body><p>An <i>Event</i> is ...</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Description Query', 'What is an "Event"?');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Icon', 'event.png');
-- INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Creation Icon', 'createKompetenzstern.gif');
-- INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-alldayevent', 1, 'Custom Implementation', 'de.deepamehta.topics.EventTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-324', '', 'tt-generic', 1, 'tt-alldayevent', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-alldayevent-search', 'Event Search');
INSERT INTO TopicProp VALUES ('tt-alldayevent-search', 1, 'Name', 'Event Search');
INSERT INTO TopicProp VALUES ('tt-alldayevent-search', 1, 'Icon', 'event-search.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-325', '', 'tt-topiccontainer', 1, 'tt-alldayevent-search', 1);
-- assign search type to type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-330', '', 'tt-alldayevent-search', 1, 'tt-alldayevent', 1);
-- assign properties to topic type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-331', '', 'tt-alldayevent', 1, 'pp-begindate', 1);
INSERT INTO AssociationProp VALUES ('a-331', 1, 'Ordinal Number', '110');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-332', '', 'tt-alldayevent', 1, 'pp-enddate', 1);
INSERT INTO AssociationProp VALUES ('a-332', 1, 'Ordinal Number', '130');

--- "Location" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-location', 'Location');
INSERT INTO TopicProp VALUES ('tt-location', 1, 'Name', 'Location');
INSERT INTO TopicProp VALUES ('tt-location', 1, 'Plural Name', 'Locations');
INSERT INTO TopicProp VALUES ('tt-location', 1, 'Description', '<html><head></head><body><p>A <i>Location</i> is ...</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-location', 1, 'Description Query', 'What is a "Location"?');
INSERT INTO TopicProp VALUES ('tt-location', 1, 'Icon', 'location.png');
-- INSERT INTO TopicProp VALUES ('tt-location', 1, 'Creation Icon', 'createKompetenzstern.gif');
-- INSERT INTO TopicProp VALUES ('tt-location', 1, 'Unique Topic Names', 'on');
-- INSERT INTO TopicProp VALUES ('tt-location', 1, 'Custom Implementation', 'de.deepamehta.topics.EventTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-334', '', 'tt-generic', 1, 'tt-location', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-location-search', 'Location Search');
INSERT INTO TopicProp VALUES ('tt-location-search', 1, 'Name', 'Location Search');
-- INSERT INTO TopicProp VALUES ('tt-location-search', 1, 'Icon', 'event-search.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-335', '', 'tt-topiccontainer', 1, 'tt-location-search', 1);
-- assign search type to type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-336', '', 'tt-location-search', 1, 'tt-location', 1);
-- create relation to "Address"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-337', '', 'tt-location', 1, 'tt-address', 1);
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Name', '');
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Cardinality', 'one');
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Web Form', 'Related Topic Selector');
INSERT INTO AssociationProp VALUES ('a-337', 1, 'Ordinal Number', '150');

--- "Recipient List" ---
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-recipientlist', 'Recipient List');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Name', 'Recipient List');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Plural Name', 'Recipient Lists');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Description', '<html><head></head><body><p>A <i>Recipient List</i> is ...</p></body></html>');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Description Query', 'What is a "Recipient List"?');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Icon', 'authentificationsource.gif');
-- INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Creation Icon', 'createKompetenzstern.gif');
-- INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Unique Topic Names', 'on');
INSERT INTO TopicProp VALUES ('tt-recipientlist', 1, 'Custom Implementation', 'de.deepamehta.topics.RecipientListTopic');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-340', '', 'tt-generic', 1, 'tt-recipientlist', 1);
-- search type
INSERT INTO Topic VALUES ('tt-topictype', 1, 1, 'tt-recipientlist-search', 'Recipient List Search');
INSERT INTO TopicProp VALUES ('tt-recipientlist-search', 1, 'Name', 'Recipient List Search');
-- INSERT INTO TopicProp VALUES ('tt-recipientlist-search', 1, 'Icon', 'event-search.gif');
-- derive search type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-341', '', 'tt-topiccontainer', 1, 'tt-recipientlist-search', 1);
-- assign search type to type
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-342', '', 'tt-recipientlist-search', 1, 'tt-recipientlist', 1);

-- create relation from "Email" to "Recipient List"

INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-343', '', 'tt-email', 1, 'tt-recipientlist', 1);
INSERT INTO AssociationProp VALUES ('a-343', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-343', 1, 'Association Type ID', 'at-recipient');
INSERT INTO AssociationProp VALUES ('a-343', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-343', 1, 'Web Form', 'Related Topic Selector');
INSERT INTO AssociationProp VALUES ('a-343', 1, 'Ordinal Number', '35');



-------------------------
--- Association Types ---
-------------------------



--- "Association" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-generic', 'Association');
INSERT INTO TopicProp VALUES ('at-generic', 1, 'Name', 'Association');
INSERT INTO TopicProp VALUES ('at-generic', 1, 'Plural Name', 'Associations');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-328', '', 'at-generic', 1, 'pp-name', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-238', '', 'at-generic', 1, 'pp-info', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-236', '', 'at-generic', 1, 'pp-ordinalnumber', 1);
INSERT INTO AssociationProp VALUES ('a-328', 1, 'Ordinal Number', '100');
INSERT INTO AssociationProp VALUES ('a-238', 1, 'Ordinal Number', '200');
INSERT INTO AssociationProp VALUES ('a-236', 1, 'Ordinal Number', '500');

--- "Assignment" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-association', 'Assignment');
INSERT INTO TopicProp VALUES ('at-association', 1, 'Name', 'Assignment');
INSERT INTO TopicProp VALUES ('at-association', 1, 'Plural Name', 'Assignments');
INSERT INTO TopicProp VALUES ('at-association', 1, 'Color', '#FF00FF');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-233', '', 'at-generic', 1, 'at-association', 1);

--- "Derivation" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-derivation', 'Derivation');
INSERT INTO TopicProp VALUES ('at-derivation', 1, 'Name', 'Derivation');
INSERT INTO TopicProp VALUES ('at-derivation', 1, 'Plural Name', 'Derivations');
INSERT INTO TopicProp VALUES ('at-derivation', 1, 'Color', '#0000FF');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-235', '', 'at-generic', 1, 'at-derivation', 1);

--- "Aggregation" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-aggregation', 'Aggregation');
INSERT INTO TopicProp VALUES ('at-aggregation', 1, 'Name', 'Aggregation');
INSERT INTO TopicProp VALUES ('at-aggregation', 1, 'Plural Name', 'Aggregations');
INSERT INTO TopicProp VALUES ('at-aggregation', 1, 'Color', '#FF0000');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-232', '', 'at-generic', 1, 'at-aggregation', 1);

--- "Composition" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-composition', 'Composition');
INSERT INTO TopicProp VALUES ('at-composition', 1, 'Name', 'Composition');
INSERT INTO TopicProp VALUES ('at-composition', 1, 'Plural Name', 'Compositions');
INSERT INTO TopicProp VALUES ('at-composition', 1, 'Color', '#00FF00');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-234', '', 'at-generic', 1, 'at-composition', 1);

--- "Relation" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-relation', 'Relation');
INSERT INTO TopicProp VALUES ('at-relation', 1, 'Name', 'Relation');
INSERT INTO TopicProp VALUES ('at-relation', 1, 'Plural Name', 'Relations');
INSERT INTO TopicProp VALUES ('at-relation', 1, 'Color', '#A000A0');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-227', '', 'at-relation', 1, 'pp-cardinality', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-290', '', 'at-relation', 1, 'pp-associationtypeid', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-413', '', 'at-relation', 1, 'pp-webinfo', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-414', '', 'at-relation', 1, 'pp-webform', 1);
INSERT INTO AssociationProp VALUES ('a-227', 1, 'Ordinal Number', '110');
INSERT INTO AssociationProp VALUES ('a-290', 1, 'Ordinal Number', '120');
INSERT INTO AssociationProp VALUES ('a-413', 1, 'Ordinal Number', '210');
INSERT INTO AssociationProp VALUES ('a-414', 1, 'Ordinal Number', '220');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-218', '', 'at-generic', 1, 'at-relation', 1);

--- "Membership" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-membership', 'Membership');
INSERT INTO TopicProp VALUES ('at-membership', 1, 'Name', 'Membership');
INSERT INTO TopicProp VALUES ('at-membership', 1, 'Plural Name', 'Memberships');
INSERT INTO TopicProp VALUES ('at-membership', 1, 'Color', '#FF8000');
-- super type

--- "Publish Permission" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-publishpermission', 'Publish Permission');
INSERT INTO TopicProp VALUES ('at-publishpermission', 1, 'Name', 'Publish Permission');
-- super type

--- "Publishing" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-publishing', 'Publishing');
INSERT INTO TopicProp VALUES ('at-publishing', 1, 'Name', 'Publishing');
INSERT INTO TopicProp VALUES ('at-publishing', 1, 'Color', '#FF00FF');
-- super type

--- "Dating" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-dating', 'Dating');
INSERT INTO TopicProp VALUES ('at-dating', 1, 'Name', 'Dating');
INSERT INTO TopicProp VALUES ('at-dating', 1, 'Plural Name', 'Datings');
INSERT INTO TopicProp VALUES ('at-dating', 1, 'Color', '#FF8040');

--- "view in use" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-viewinuse', 'view in use');
INSERT INTO TopicProp VALUES ('at-viewinuse', 1, 'Name', 'view in use');

--- "Type Access" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-uses', 'Type Access');
INSERT INTO TopicProp VALUES ('at-uses', 1, 'Name', 'Type Access');
INSERT INTO TopicProp VALUES ('at-uses', 1, 'Color', '#00E0FF');
-- assign properties
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-239', '', 'at-uses', 1, 'pp-createpermission', 1);
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-237', '', 'at-generic', 1, 'at-uses', 1);

--- "Group Leader" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-groupleader', 'Group Leader');
INSERT INTO TopicProp VALUES ('at-groupleader', 1, 'Name', 'Group Leader');
-- super type

--- "Search Result" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-navigation', 'Search Result');
INSERT INTO TopicProp VALUES ('at-navigation', 1, 'Name', 'Search Result');
-- super type

--- "Preference" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-preference', 'Preference');
INSERT INTO TopicProp VALUES ('at-preference', 1, 'Name', 'Preference');
INSERT INTO TopicProp VALUES ('at-preference', 1, 'Plural Name', 'Preferences');
INSERT INTO TopicProp VALUES ('at-preference', 1, 'Color', '#A00000');

--- "Google Result" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-googleresult', 'Google Result');
INSERT INTO TopicProp VALUES ('at-googleresult', 1, 'Name', 'Google Result');
INSERT INTO TopicProp VALUES ('at-googleresult', 1, 'Plural Name', 'Google Results');
INSERT INTO TopicProp VALUES ('at-googleresult', 1, 'Color', '#A000A0');
-- super type
INSERT INTO Association VALUES ('at-derivation', 1, 1, 'a-174', '', 'at-association', 1, 'at-googleresult', 1);

--- "Recipient" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-recipient', 'Recipient');
INSERT INTO TopicProp VALUES ('at-recipient', 1, 'Name', 'Recipient');
INSERT INTO TopicProp VALUES ('at-recipient', 1, 'Plural Name', 'Recipients');
INSERT INTO TopicProp VALUES ('at-recipient', 1, 'Color', '#E14589');

--- "Sender" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-sender', 'Sender');
INSERT INTO TopicProp VALUES ('at-sender', 1, 'Name', 'Sender');
INSERT INTO TopicProp VALUES ('at-sender', 1, 'Plural Name', 'Senders');
INSERT INTO TopicProp VALUES ('at-sender', 1, 'Color', '#4589E1');

--- "Attachment" ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-attachment', 'Attachment');
INSERT INTO TopicProp VALUES ('at-attachment', 1, 'Name', 'Attachment');
INSERT INTO TopicProp VALUES ('at-attachment', 1, 'Plural Name', 'Attachments');
INSERT INTO TopicProp VALUES ('at-attachment', 1, 'Color', '#408000');

--- "Helptext" (for Webforms) ---
INSERT INTO Topic VALUES ('tt-assoctype', 1, 1, 'at-form-helptext', 'Helptext');
INSERT INTO TopicProp VALUES ('at-form-helptext', 1, 'Name', 'Helptext');
INSERT INTO TopicProp VALUES ('at-form-helptext', 1, 'Plural Name', 'Helptexts');
INSERT INTO TopicProp VALUES ('at-form-helptext', 1, 'Description', '<html><body><p>A <i>Helptext</i>-Edge is used for setting a Note-Topic in relation with a Property-Topic. Once set, the Description-Text of the Note-Topic is rendered as a Hint in the form above the label and the input field of the associated Property.</p></body></html>');
INSERT INTO TopicProp VALUES ('at-form-helptext', 1, 'Description Query', 'What is a Helptext association?');
INSERT INTO TopicProp VALUES ('at-form-helptext', 1, 'Color', '#3f4da6');
--- Assignment of the "Helptext"-Edge to the "Administration"-Workspace ---
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-form-helptext-use', '', 't-administrationgroup', 1, 'at-form-helptext', 1);



------------------
--- Properties ---
------------------



--- "Name" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-name', 'Name');
INSERT INTO TopicProp VALUES ('pp-name', 1, 'Visualization', 'Input Field');
INSERT INTO TopicProp VALUES ('pp-name', 1, 'Edit Icon', 'rename.gif');

--- "Description" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-info', 'Description');
INSERT INTO TopicProp VALUES ('pp-info', 1, 'Visualization', 'Text Editor');
INSERT INTO TopicProp VALUES ('pp-info', 1, 'Edit Icon', 'editdescription.gif');

--- "Description" (single line) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-infosingle', 'Description');
INSERT INTO TopicProp VALUES ('pp-infosingle', 1, 'Visualization', 'Input Field');

--- "Locked Geometry" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-geometrylocked', 'Locked Geometry');
INSERT INTO TopicProp VALUES ('pp-geometrylocked', 1, 'Visualization', 'hidden');

--- "Owner ID" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-owner', 'Owner ID');
INSERT INTO TopicProp VALUES ('pp-owner', 1, 'Name', 'Owner ID');
INSERT INTO TopicProp VALUES ('pp-owner', 1, 'Visualization', 'hidden');

--- "Icon" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-icon', 'Icon');
INSERT INTO TopicProp VALUES ('pp-icon', 1, 'Visualization', 'Input Field');
INSERT INTO TopicProp VALUES ('pp-icon', 1, 'Edit Icon', 'editicon.gif');

--- "Color" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-color', 'Color');
INSERT INTO TopicProp VALUES ('pp-color', 1, 'Visualization', 'Color Chooser');
INSERT INTO TopicProp VALUES ('pp-color', 1, 'Edit Icon', 'editcolor.gif');

--- "Creation Icon" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-creationicon', 'Creation Icon');
INSERT INTO TopicProp VALUES ('pp-creationicon', 1, 'Visualization', 'Input Field');
INSERT INTO TopicProp VALUES ('pp-creationicon', 1, 'Edit Icon', 'editicon.gif');

--- "Description Query" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-descriptionquery', 'Description Query');
INSERT INTO TopicProp VALUES ('pp-descriptionquery', 1, 'Visualization', 'Input Field');
INSERT INTO TopicProp VALUES ('pp-descriptionquery', 1, 'Edit Icon', 'editdescription.gif');

--- "Plural Name" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-pluraltypename', 'Plural Name');
INSERT INTO TopicProp VALUES ('pp-pluraltypename', 1, 'Visualization', 'Input Field');

--- "Hidden Topic Names" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-hideinstancenames', 'Hidden Topic Names');
INSERT INTO TopicProp VALUES ('pp-hideinstancenames', 1, 'Visualization', 'Switch');
INSERT INTO TopicProp VALUES ('pp-hideinstancenames', 1, 'Edit Icon', 'sethiddentopicnames.gif');

--- "Unique Topic Names" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-unique', 'Unique Topic Names');
INSERT INTO TopicProp VALUES ('pp-unique', 1, 'Visualization', 'Switch');

--- "Disabled" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-disabled', 'Disabled');
INSERT INTO TopicProp VALUES ('pp-disabled', 1, 'Visualization', 'Switch');
INSERT INTO TopicProp VALUES ('pp-disabled', 1, 'Edit Icon', 'setdisabled.gif');

--- "Custom Implementation" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-implementingclass', 'Custom Implementation');
INSERT INTO TopicProp VALUES ('pp-implementingclass', 1, 'Visualization', 'Input Field');
INSERT INTO TopicProp VALUES ('pp-implementingclass', 1, 'Edit Icon', 'editdescription.gif');

--- "Ordinal Number" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-ordinalnumber', 'Ordinal Number');
INSERT INTO TopicProp VALUES ('pp-ordinalnumber', 1, 'Visualization', 'Input Field');

--- "Access Permission" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-createpermission', 'Access Permission');
INSERT INTO TopicProp VALUES ('pp-createpermission', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-createpermission', 1, 'Default Value', 'view');

--- "Background Color" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-bgcolor', 'Background Color');
INSERT INTO TopicProp VALUES ('pp-bgcolor', 1, 'Visualization', 'Color Chooser');

--- "Background Image" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-backgroundimage', 'Background Image');
INSERT INTO TopicProp VALUES ('pp-backgroundimage', 1, 'Visualization', 'Input Field');

--- "Translation" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-translationUse', 'Translation');
INSERT INTO TopicProp VALUES ('pp-translationUse', 1, 'Visualization', 'hidden');

--- "Visualization" --- (Property)
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-visualization', 'Visualization');
INSERT INTO TopicProp VALUES ('pp-visualization', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-visualization', 1, 'Default Value', 'Input Field');
INSERT INTO TopicProp VALUES ('pp-visualization', 1, 'Edit Icon', 'setvisualization.gif');

--- "Default Value" --- (Property)
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-defaultvalue', 'Default Value');
INSERT INTO TopicProp VALUES ('pp-defaultvalue', 1, 'Name', 'Default Value');
INSERT INTO TopicProp VALUES ('pp-defaultvalue', 1, 'Visualization', 'Input Field');

--- "Edit Icon" --- (Property)
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-editicon', 'Edit Icon');
INSERT INTO TopicProp VALUES ('pp-editicon', 1, 'Visualization', 'Input Field');
INSERT INTO TopicProp VALUES ('pp-editicon', 1, 'Edit Icon', 'editicon.gif');

--- "Public" (Workspace) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-subscribable', 'Public');
INSERT INTO TopicProp VALUES ('pp-subscribable', 1, 'Visualization', 'Switch');
INSERT INTO TopicProp VALUES ('pp-subscribable', 1, 'Edit Icon', 'setsubscribable.gif');

--- "Default" (Workspace) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-corporateworkgroup', 'Default');
INSERT INTO TopicProp VALUES ('pp-corporateworkgroup', 1, 'Visualization', 'Switch');
INSERT INTO TopicProp VALUES ('pp-corporateworkgroup', 1, 'Edit Icon', 'setcorporateworkgroup.gif');

--- "ElementCount" (Search) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-elementcount', 'ElementCount');
INSERT INTO TopicProp VALUES ('pp-elementcount', 1, 'Visualization', 'hidden');

--- "QueryElements" (Search) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-queryelements', 'QueryElements');
INSERT INTO TopicProp VALUES ('pp-queryelements', 1, 'Visualization', 'hidden');

--- "RelatedTopicID" (Search) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-relatedtopicid', 'RelatedTopicID');
INSERT INTO TopicProp VALUES ('pp-relatedtopicid', 1, 'Visualization', 'hidden');

--- "AssociationTypeID" (Search) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-assoctypeid', 'AssociationTypeID');
INSERT INTO TopicProp VALUES ('pp-assoctypeid', 1, 'Name', 'AssociationTypeID');
INSERT INTO TopicProp VALUES ('pp-assoctypeid', 1, 'Visualization', 'hidden');

-- "Email Address"
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-emailaddress', 'Email Address');
INSERT INTO TopicProp VALUES ('pp-emailaddress', 1, 'Name', 'Email Address');
INSERT INTO TopicProp VALUES ('pp-emailaddress', 1, 'Visualization', 'Input Field');

-- "Mailbox URL"
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-mailboxurl', 'Mailbox URL');
INSERT INTO TopicProp VALUES ('pp-mailboxurl', 1, 'Name', 'Mailbox URL');
INSERT INTO TopicProp VALUES ('pp-mailboxurl', 1, 'Visualization', 'Input Field');

--- "URL" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-url', 'URL');
INSERT INTO TopicProp VALUES ('pp-url', 1, 'Visualization', 'Input Field');

--- "Domain Information" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-domaininfo', 'Domain Information');
INSERT INTO TopicProp VALUES ('pp-domaininfo', 1, 'Visualization', 'Multiline Input Field');

--- "Server" (Whois) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-wit_server', 'Server');
INSERT INTO TopicProp VALUES ('pp-wit_server', 1, 'Name', 'Server');

--- "Domains" (Whois) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-wit_domains', 'Domains');
INSERT INTO TopicProp VALUES ('pp-wit_domains', 1, 'Name', 'Domains');

--- "Database Type" (Data Source) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-dbtype', 'Database Type');
INSERT INTO TopicProp VALUES ('pp-dbtype', 1, 'Name', 'Database Type');
INSERT INTO TopicProp VALUES ('pp-dbtype', 1, 'Visualization', 'Input Field');

--- "Username" (Data Source) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-dbuser', 'Username');
INSERT INTO TopicProp VALUES ('pp-dbuser', 1, 'Name', 'Username');
INSERT INTO TopicProp VALUES ('pp-dbuser', 1, 'Visualization', 'Input Field');

--- "Password" (Data Source) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-dbpassword', 'Password');
INSERT INTO TopicProp VALUES ('pp-dbpassword', 1, 'Name', 'Password');
INSERT INTO TopicProp VALUES ('pp-dbpassword', 1, 'Visualization', 'Input Field');

--- "Entities" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-entities', 'Entities');
INSERT INTO TopicProp VALUES ('pp-entities', 1, 'Visualization', 'Input Field');

--- "Idle Elementtype" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-idleelementtype', 'Idle Elementtype');

--- "Profile Elementtype" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-profileelementtype', 'Profile Elementtype');

--- "Username Attribute" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-usernameattr', 'Username Attribute');
INSERT INTO TopicProp VALUES ('pp-usernameattr', 1, 'Name', 'Username Attribute');

--- "Password Attribute" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-passwordattr', 'Password Attribute');

--- "Username" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-username', 'Username');
INSERT INTO TopicProp VALUES ('pp-username', 1, 'Name', 'Username');
INSERT INTO TopicProp VALUES ('pp-username', 1, 'Visualization', 'Input Field');

--- "Password" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-password', 'Password');
INSERT INTO TopicProp VALUES ('pp-password', 1, 'Visualization', 'Password Field');

--- "File" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-file', 'File');
INSERT INTO TopicProp VALUES ('pp-file', 1, 'Visualization', 'File Chooser');

--- "Search" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-topicname', 'Search');
INSERT INTO TopicProp VALUES ('pp-topicname', 1, 'Visualization', 'Input Field');

--- "Chat Flow" (Chat) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-chatarea', 'Chat Flow');
INSERT INTO TopicProp VALUES ('pp-chatarea', 1, 'Visualization', 'Text Editor');

--- "Your Remark" (Chat) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-userentry', 'Your Remark');
INSERT INTO TopicProp VALUES ('pp-userentry', 1, 'Visualization', 'Input Field');

--- "Last Reply Date" (Message) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-lastreplydate', 'Last Reply Date');
INSERT INTO TopicProp VALUES ('pp-lastreplydate', 1, 'Name', 'Last Reply Date');
INSERT INTO TopicProp VALUES ('pp-lastreplydate', 1, 'Visualization', 'Date Chooser');

--- "Last Reply Time" (Message) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-lastreplytime', 'Last Reply Time');
INSERT INTO TopicProp VALUES ('pp-lastreplytime', 1, 'Name', 'Last Reply Time');
INSERT INTO TopicProp VALUES ('pp-lastreplytime', 1, 'Visualization', 'Time Chooser');

-- "First Name"
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-forename', 'First Name');
INSERT INTO TopicProp VALUES ('pp-forename', 1, 'Name', 'First Name');
INSERT INTO TopicProp VALUES ('pp-forename', 1, 'Visualization', 'Input Field');

-- "Surname"
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-surname2', 'Surname');
INSERT INTO TopicProp VALUES ('pp-surname2', 1, 'Name', 'Surname');
INSERT INTO TopicProp VALUES ('pp-surname2', 1, 'Visualization', 'Input Field');

--- "Gender" (Person) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-gender', 'Gender');
INSERT INTO TopicProp VALUES ('pp-gender', 1, 'Name', 'Gender');
INSERT INTO TopicProp VALUES ('pp-gender', 1, 'Visualization', 'Option Buttons');
-- property values
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-male', 'Male');
INSERT INTO TopicProp VALUES ('t-male', 1, 'Name', 'Male');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-female', 'Female');
INSERT INTO TopicProp VALUES ('t-female', 1, 'Name', 'Female');
-- assign property values
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-145', '', 'pp-gender', 1, 't-male', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-146', '', 'pp-gender', 1, 't-female', 1);
INSERT INTO AssociationProp VALUES ('a-145', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-146', 1, 'Ordinal Number', '2');

--- "Birthday" (Person) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-birthday', 'Birthday');
INSERT INTO TopicProp VALUES ('pp-birthday', 1, 'Visualization', 'Date Chooser');

--- "Street" (Address) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-street', 'Street');
INSERT INTO TopicProp VALUES ('pp-street', 1, 'Name', 'Street');
INSERT INTO TopicProp VALUES ('pp-street', 1, 'Visualization', 'Input Field');

--- "Postal Code" (Address) ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-postalcode', 'Postal Code');
INSERT INTO TopicProp VALUES ('pp-postalcode', 1, 'Name', 'Postal Code');
INSERT INTO TopicProp VALUES ('pp-postalcode', 1, 'Visualization', 'Input Field');

--- "Start Time" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-starttime', 'Begin');
INSERT INTO TopicProp VALUES ('pp-starttime', 1, 'Visualization', 'Time Chooser');

--- "Duration" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-duration', 'Duration');
INSERT INTO TopicProp VALUES ('pp-duration', 1, 'Visualization', 'Time Chooser');

--- "Language" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-language', 'Language');
INSERT INTO TopicProp VALUES ('pp-language', 1, 'Visualization', 'Options Menu');

--- "Splitted screen" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-splitscreen', 'Splitted screen');
INSERT INTO TopicProp VALUES ('pp-splitscreen', 1, 'Visualization', 'Switch');
INSERT INTO TopicProp VALUES ('pp-splitscreen', 1, 'Edit Icon', 'setsplittedscreen.gif');

--- "Show side panel" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-showsidebar', 'Show side panel');
INSERT INTO TopicProp VALUES ('pp-showsidebar', 1, 'Visualization', 'Switch');
INSERT INTO TopicProp VALUES ('pp-showsidebar', 1, 'Edit Icon', 'setshowsidepanel.gif');

--- "Show build panel" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-showbuildpanel', 'Show build panel');
INSERT INTO TopicProp VALUES ('pp-showbuildpanel', 1, 'Visualization', 'Switch');
INSERT INTO TopicProp VALUES ('pp-showbuildpanel', 1, 'Edit Icon', 'setshowbuildpanel.gif');

--- "Server Name", "Client Name", "Active", "Corporate Icon", "Customer Icon" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-servicename', 'Server Name');
INSERT INTO TopicProp VALUES ('pp-servicename', 1, 'Visualization', 'Input Field');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-clientname', 'Client Name');
INSERT INTO TopicProp VALUES ('pp-clientname', 1, 'Visualization', 'Input Field');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-active', 'Active');
INSERT INTO TopicProp VALUES ('pp-active', 1, 'Visualization', 'Switch');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-serviceicon', 'Corporate Icon');
INSERT INTO TopicProp VALUES ('pp-serviceicon', 1, 'Visualization', 'Input Field');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-customericon', 'Customer Icon');
INSERT INTO TopicProp VALUES ('pp-customericon', 1, 'Visualization', 'Input Field');

--- "Cardinality" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-cardinality', 'Cardinality');
INSERT INTO TopicProp VALUES ('pp-cardinality', 1, 'Visualization', 'Option Buttons');
INSERT INTO TopicProp VALUES ('pp-cardinality', 1, 'Default Value', 'one');
-- assign property values to "Cardinality"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-228', '', 'pp-cardinality', 1, 't-one', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-289', '', 'pp-cardinality', 1, 't-many', 1);
INSERT INTO AssociationProp VALUES ('a-228', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-289', 1, 'Ordinal Number', '2');

--- "Association Type ID" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-associationtypeid', 'Association Type ID');
INSERT INTO TopicProp VALUES ('pp-associationtypeid', 1, 'Visualization', 'Input Field');
INSERT INTO TopicProp VALUES ('pp-associationtypeid', 1, 'Default Value', 'at-association');

--- "Web Alias" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-webalias', 'Web Alias');
INSERT INTO TopicProp VALUES ('pp-webalias', 1, 'Name', 'Web Alias');
INSERT INTO TopicProp VALUES ('pp-webalias', 1, 'Visualization', 'Input Field');

--- "Web Info" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-webinfo', 'Web Info');
INSERT INTO TopicProp VALUES ('pp-webinfo', 1, 'Name', 'Web Info');
INSERT INTO TopicProp VALUES ('pp-webinfo', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-webinfo', 1, 'Default Value', 'Related Topic Name');
-- property values
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-relatedtopicname', 'Related Topic Name');
INSERT INTO TopicProp VALUES ('t-relatedtopicname', 1, 'Name', 'Related Topic Name');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-relatedinfo', 'Related Info');
INSERT INTO TopicProp VALUES ('t-relatedinfo', 1, 'Name', 'Related Info');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-deeplyrelatedinfo', 'Deeply Related Info');
INSERT INTO TopicProp VALUES ('t-deeplyrelatedinfo', 1, 'Name', 'Deeply Related Info');

---
--- "Membership" Properties: "Editor" and "Publisher"
---
-- create properties
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-editor', 'Editor');
INSERT INTO TopicProp VALUES ('pp-editor', 1, 'Name', 'Editor');
INSERT INTO TopicProp VALUES ('pp-editor', 1, 'Visualization', 'Switch');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-publisher', 'Publisher');
INSERT INTO TopicProp VALUES ('pp-publisher', 1, 'Name', 'Publisher');
INSERT INTO TopicProp VALUES ('pp-publisher', 1, 'Visualization', 'Switch');

-- assign property values
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-408', '', 'pp-webinfo', 1, 't-relatedtopicname', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-418', '', 'pp-webinfo', 1, 't-relatedinfo', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-409', '', 'pp-webinfo', 1, 't-deeplyrelatedinfo', 1);
INSERT INTO AssociationProp VALUES ('a-408', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-418', 1, 'Ordinal Number', '2');
INSERT INTO AssociationProp VALUES ('a-409', 1, 'Ordinal Number', '3');
-- assign properties to association type "Membership"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-93', '', 'at-membership', 1, 'pp-editor', 1);
INSERT INTO AssociationProp VALUES ('a-93', 1, 'Ordinal Number', '10');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-94', '', 'at-membership', 1, 'pp-publisher', 1);
INSERT INTO AssociationProp VALUES ('a-94', 1, 'Ordinal Number', '20');

--- Calendar
-- create properties
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-displaydate', 'Display Date');
INSERT INTO TopicProp VALUES ('pp-displaydate', 1, 'Name', 'Display Date');
INSERT INTO TopicProp VALUES ('pp-displaydate', 1, 'Visualization', 'Date Chooser');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-displaymode', 'Display Mode');
INSERT INTO TopicProp VALUES ('pp-displaymode', 1, 'Name', 'Display Mode');
INSERT INTO TopicProp VALUES ('pp-displaymode', 1, 'Visualization', 'Options Menu');
-- create property values
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-day', 'Day');
INSERT INTO TopicProp VALUES ('t-day', 1, 'Name', 'Day');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-week', 'Week');
INSERT INTO TopicProp VALUES ('t-week', 1, 'Name', 'Week');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-month', 'Month');
INSERT INTO TopicProp VALUES ('t-month', 1, 'Name', 'Month');
-- assign property values to property
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-157', '', 'pp-displaymode', 1, 't-day', 1);
INSERT INTO AssociationProp VALUES ('a-157', 1, 'Ordinal Number', '1');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-162', '', 'pp-displaymode', 1, 't-week', 1);
INSERT INTO AssociationProp VALUES ('a-162', 1, 'Ordinal Number', '2');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-187', '', 'pp-displaymode', 1, 't-month', 1);
INSERT INTO AssociationProp VALUES ('a-187', 1, 'Ordinal Number', '3');
-- assign properties to topic type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-141', '', 'tt-calendar', 1, 'pp-displaydate', 1);
INSERT INTO AssociationProp VALUES ('a-141', 1, 'Ordinal Number', '220');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-148', '', 'tt-calendar', 1, 'pp-displaymode', 1);
INSERT INTO AssociationProp VALUES ('a-148', 1, 'Ordinal Number', '210');
-- create relation to "Person"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-190', '', 'tt-calendar', 1, 'tt-person', 1);
INSERT INTO AssociationProp VALUES ('a-190', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-190', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-190', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-190', 1, 'Web Form', 'Related Topic Selector');
INSERT INTO AssociationProp VALUES ('a-190', 1, 'Ordinal Number', '150');

--- Appointment
-- create properties
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-begindate', 'Begin Date');
INSERT INTO TopicProp VALUES ('pp-begindate', 1, 'Name', 'Begin Date');
INSERT INTO TopicProp VALUES ('pp-begindate', 1, 'Visualization', 'Date Chooser');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-begintime', 'Begin Time');
INSERT INTO TopicProp VALUES ('pp-begintime', 1, 'Name', 'Begin Time');
INSERT INTO TopicProp VALUES ('pp-begintime', 1, 'Visualization', 'Time Chooser');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-enddate', 'End Date');
INSERT INTO TopicProp VALUES ('pp-enddate', 1, 'Name', 'End Date');
INSERT INTO TopicProp VALUES ('pp-enddate', 1, 'Visualization', 'Date Chooser');
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-endtime', 'End Time');
INSERT INTO TopicProp VALUES ('pp-endtime', 1, 'Name', 'End Time');
INSERT INTO TopicProp VALUES ('pp-endtime', 1, 'Visualization', 'Time Chooser');
-- assign properties to topic type
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-110', '', 'tt-event', 1, 'pp-begindate', 1);
INSERT INTO AssociationProp VALUES ('a-110', 1, 'Ordinal Number', '110');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-111', '', 'tt-event', 1, 'pp-begintime', 1);
INSERT INTO AssociationProp VALUES ('a-111', 1, 'Ordinal Number', '120');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-112', '', 'tt-event', 1, 'pp-enddate', 1);
INSERT INTO AssociationProp VALUES ('a-112', 1, 'Ordinal Number', '130');
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-113', '', 'tt-event', 1, 'pp-endtime', 1);
INSERT INTO AssociationProp VALUES ('a-113', 1, 'Ordinal Number', '140');
-- create relation to "Person"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-323', 'Attendee', 'tt-event', 1, 'tt-person', 1);
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Name', 'Attendee');
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Cardinality', 'many');
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Web Form', 'Related Topic Selector');
INSERT INTO AssociationProp VALUES ('a-323', 1, 'Ordinal Number', '150');
-- create relation to "Location"
INSERT INTO Association VALUES ('at-relation', 1, 1, 'a-338', '', 'tt-event', 1, 'tt-location', 1);
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Name', '');
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Cardinality', 'one');
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Association Type ID', 'at-association');
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Web Info', 'Related Topic Name');
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Web Form', 'Related Topic Selector');
INSERT INTO AssociationProp VALUES ('a-338', 1, 'Ordinal Number', '150');

--- Search
-- create property "Result"
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-result', 'Result');
INSERT INTO TopicProp VALUES ('pp-result', 1, 'Name', 'Result');
INSERT INTO TopicProp VALUES ('pp-result', 1, 'Visualization', 'Text Editor');
-- assign to topic type "Search"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-339', '', 'tt-container', 1, 'pp-result', 1);
INSERT INTO AssociationProp VALUES ('a-339', 1, 'Ordinal Number', '200');

--- "Web Form" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-webform', 'Web Form');
INSERT INTO TopicProp VALUES ('pp-webform', 1, 'Name', 'Web Form');
INSERT INTO TopicProp VALUES ('pp-webform', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-webform', 1, 'Default Value', 'Related Topic Selector');
-- property values
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-relatedtopicselector', 'Related Topic Selector');
INSERT INTO TopicProp VALUES ('t-relatedtopicselector', 1, 'Name', 'Related Topic Selector');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-relatedform', 'Related Form');
INSERT INTO TopicProp VALUES ('t-relatedform', 1, 'Name', 'Related Form');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-deeplyrelatedform', 'Deeply Related Form');
INSERT INTO TopicProp VALUES ('t-deeplyrelatedform', 1, 'Name', 'Deeply Related Form');
-- assign property values
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-410', '', 'pp-webform', 1, 't-relatedtopicselector', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-412', '', 'pp-webform', 1, 't-relatedform', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-415', '', 'pp-webform', 1, 't-deeplyrelatedform', 1);
INSERT INTO AssociationProp VALUES ('a-410', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-412', 1, 'Ordinal Number', '2');
INSERT INTO AssociationProp VALUES ('a-415', 1, 'Ordinal Number', '3');

--- create property "Recipient Type" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-recipienttype', 'Recipient Type');
INSERT INTO TopicProp VALUES ('pp-recipienttype', 1, 'Name', 'Recipient Type');
INSERT INTO TopicProp VALUES ('pp-recipienttype', 1, 'Visualization', 'Options Menu');
INSERT INTO TopicProp VALUES ('pp-recipienttype', 1, 'Default Value', 'To');
-- assign property to "Recipient"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-347', '', 'at-recipient', 1, 'pp-recipienttype', 1);
INSERT INTO AssociationProp VALUES ('a-347', 1, 'Ordinal Number', '50');
-- create property values "To", "Cc", "Bcc"
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-recipienttype-to', 'To');
INSERT INTO TopicProp VALUES ('t-recipienttype-to', 1, 'Name', 'To');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-recipienttype-cc', 'Cc');
INSERT INTO TopicProp VALUES ('t-recipienttype-cc', 1, 'Name', 'Cc');
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-recipienttype-bcc', 'Bcc');
INSERT INTO TopicProp VALUES ('t-recipienttype-bcc', 1, 'Name', 'Bcc');
-- assign property values to "Recipient Type"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-344', '', 'pp-recipienttype', 1, 't-recipienttype-to', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-345', '', 'pp-recipienttype', 1, 't-recipienttype-cc', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-346', '', 'pp-recipienttype', 1, 't-recipienttype-bcc', 1);
INSERT INTO AssociationProp VALUES ('a-344', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-345', 1, 'Ordinal Number', '2');
INSERT INTO AssociationProp VALUES ('a-346', 1, 'Ordinal Number', '3');



-----------------------
--- Property Values ---
-----------------------



--- "Visualization" ---
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-inputfield', 'Input Field');
INSERT INTO TopicProp VALUES ('t-inputfield', 1, 'Name', 'Input Field');
INSERT INTO TopicProp VALUES ('t-inputfield', 1, 'Icon', 'inputfield.gif');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-multilineinputfield', 'Multiline Input Field');
INSERT INTO TopicProp VALUES ('t-multilineinputfield', 1, 'Name', 'Multiline Input Field');
INSERT INTO TopicProp VALUES ('t-multilineinputfield', 1, 'Icon', 'multilineinputfield.gif');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-texteditor', 'Text Editor');
INSERT INTO TopicProp VALUES ('t-texteditor', 1, 'Name', 'Text Editor');
INSERT INTO TopicProp VALUES ('t-texteditor', 1, 'Icon', 'texteditor.gif');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-optionsmenu', 'Options Menu');
INSERT INTO TopicProp VALUES ('t-optionsmenu', 1, 'Name', 'Options Menu');
INSERT INTO TopicProp VALUES ('t-optionsmenu', 1, 'Icon', 'optionsmenu.gif');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-optionbuttons', 'Option Buttons');
INSERT INTO TopicProp VALUES ('t-optionbuttons', 1, 'Name', 'Option Buttons');
INSERT INTO TopicProp VALUES ('t-optionbuttons', 1, 'Icon', 'optionbuttons.gif');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-switch', 'Switch');
INSERT INTO TopicProp VALUES ('t-switch', 1, 'Name', 'Switch');
INSERT INTO TopicProp VALUES ('t-switch', 1, 'Icon', 'switch.gif');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-passwordfield', 'Password Field');
INSERT INTO TopicProp VALUES ('t-passwordfield', 1, 'Name', 'Password Field');
INSERT INTO TopicProp VALUES ('t-passwordfield', 1, 'Icon', 'passwordfield.gif');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-datechooser', 'Date Chooser');
INSERT INTO TopicProp VALUES ('t-datechooser', 1, 'Name', 'Date Chooser');
INSERT INTO TopicProp VALUES ('t-datechooser', 1, 'Icon', 'appointment.gif');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-timechooser', 'Time Chooser');
INSERT INTO TopicProp VALUES ('t-timechooser', 1, 'Name', 'Time Chooser');
INSERT INTO TopicProp VALUES ('t-timechooser', 1, 'Icon', 'appointment.gif');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-color', 'Color Chooser');
INSERT INTO TopicProp VALUES ('t-color', 1, 'Name', 'Color Chooser');
INSERT INTO TopicProp VALUES ('t-color', 1, 'Icon', 'color.gif');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-file', 'File Chooser');
INSERT INTO TopicProp VALUES ('t-file', 1, 'Name', 'File Chooser');
INSERT INTO TopicProp VALUES ('t-file', 1, 'Icon', 'document.gif');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-hiddenproperty', 'hidden');
INSERT INTO TopicProp VALUES ('t-hiddenproperty', 1, 'Name', 'hidden');
INSERT INTO TopicProp VALUES ('t-hiddenproperty', 1, 'Icon', 'hidden.gif');

--- "Access Permission" ---
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-view', 'view');
INSERT INTO TopicProp VALUES ('t-view', 1, 'Name', 'view');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-create', 'create');
INSERT INTO TopicProp VALUES ('t-create', 1, 'Name', 'create');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-createinworkspace', 'create in workspace');
INSERT INTO TopicProp VALUES ('t-createinworkspace', 1, 'Name', 'create in workspace');

--- "Language" ---
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-english', 'English');
INSERT INTO TopicProp VALUES ('t-english', 1, 'Name', 'English');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-deutsch', 'Deutsch');
INSERT INTO TopicProp VALUES ('t-deutsch', 1, 'Name', 'Deutsch');

--- "Relation" ---
INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-one', 'one');
INSERT INTO TopicProp VALUES ('t-one', 1, 'Name', 'one');

INSERT INTO Topic VALUES ('tt-constant', 1, 1, 't-many', 'many');
INSERT INTO TopicProp VALUES ('t-many', 1, 'Name', 'many');

--- "Width" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-width', 'Width');
INSERT INTO TopicProp VALUES ('pp-width', 1, 'Name', 'Width');
INSERT INTO TopicProp VALUES ('pp-width', 1, 'Visualization', 'Input Field');
--- "Height" ---
INSERT INTO Topic VALUES ('tt-property', 1, 1, 'pp-height', 'Height');
INSERT INTO TopicProp VALUES ('pp-height', 1, 'Name', 'Height');
INSERT INTO TopicProp VALUES ('pp-height', 1, 'Visualization', 'Input Field');



--------------------
--- Associations ---
--------------------



-- assign property values to "Visualization"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-51', '', 'pp-visualization', 1, 't-inputfield', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-52', '', 'pp-visualization', 1, 't-multilineinputfield', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-259', '', 'pp-visualization', 1, 't-texteditor', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-53', '', 'pp-visualization', 1, 't-optionsmenu', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-54', '', 'pp-visualization', 1, 't-optionbuttons', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-240', '', 'pp-visualization', 1, 't-switch', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-127', '', 'pp-visualization', 1, 't-passwordfield', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-292', '', 'pp-visualization', 1, 't-datechooser', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-293', '', 'pp-visualization', 1, 't-timechooser', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-297', '', 'pp-visualization', 1, 't-color', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-291', '', 'pp-visualization', 1, 't-file', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-57', '', 'pp-visualization', 1, 't-hiddenproperty', 1);
INSERT INTO AssociationProp VALUES ('a-51',  1, 'Ordinal Number', '01');
INSERT INTO AssociationProp VALUES ('a-52',  1, 'Ordinal Number', '02');
INSERT INTO AssociationProp VALUES ('a-259', 1, 'Ordinal Number', '03');
INSERT INTO AssociationProp VALUES ('a-53',  1, 'Ordinal Number', '04');
INSERT INTO AssociationProp VALUES ('a-54',  1, 'Ordinal Number', '05');
INSERT INTO AssociationProp VALUES ('a-240', 1, 'Ordinal Number', '06');
INSERT INTO AssociationProp VALUES ('a-127', 1, 'Ordinal Number', '07');
INSERT INTO AssociationProp VALUES ('a-292', 1, 'Ordinal Number', '08');
INSERT INTO AssociationProp VALUES ('a-293', 1, 'Ordinal Number', '09');
INSERT INTO AssociationProp VALUES ('a-297', 1, 'Ordinal Number', '20');
INSERT INTO AssociationProp VALUES ('a-291', 1, 'Ordinal Number', '30');
INSERT INTO AssociationProp VALUES ('a-57',  1, 'Ordinal Number', '40');
-- assign property values to "Access Permission"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-243', '', 'pp-createpermission', 1, 't-view', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-244', '', 'pp-createpermission', 1, 't-create', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-245', '', 'pp-createpermission', 1, 't-createinworkspace', 1);
-- assign property values to "Language"
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-280', '', 'pp-language', 1, 't-english', 1);
INSERT INTO Association VALUES ('at-composition', 1, 1, 'a-281', '', 'pp-language', 1, 't-deutsch', 1);
INSERT INTO AssociationProp VALUES ('a-280', 1, 'Ordinal Number', '1');
INSERT INTO AssociationProp VALUES ('a-281', 1, 'Ordinal Number', '2');



----------------------------------
--- Assign Types To Workspaces ---
----------------------------------



--- "DeepaMehta" ---
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-185', '', 't-corporategroup', 1, 'tt-generic', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-246', '', 't-corporategroup', 1, 'tt-topicmap', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-192', '', 't-corporategroup', 1, 'tt-workspace', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-189', '', 't-corporategroup', 1, 'tt-person', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-188', '', 't-corporategroup', 1, 'tt-document', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-181', '', 't-corporategroup', 1, 'tt-email', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-191', '', 't-corporategroup', 1, 'tt-webpage', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-326', '', 't-corporategroup', 1, 'tt-institution', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-114', '', 't-corporategroup', 1, 'tt-calendar', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-115', '', 't-corporategroup', 1, 'tt-event', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-333', '', 't-corporategroup', 1, 'tt-alldayevent', 1);
--
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-193', '', 't-corporategroup', 1, 'at-generic', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-194', '', 't-corporategroup', 1, 'at-association', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-195', '', 't-corporategroup', 1, 'at-aggregation', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-196', '', 't-corporategroup', 1, 'at-composition', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-197', '', 't-corporategroup', 1, 'at-derivation', 1);
--
INSERT INTO AssociationProp VALUES ('a-185', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-246', 1, 'Access Permission', 'create in workspace');
INSERT INTO AssociationProp VALUES ('a-192', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-189', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-188', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-181', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-191', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-326', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-114', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-115', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-333', 1, 'Access Permission', 'create');
--
INSERT INTO AssociationProp VALUES ('a-193', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-194', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-195', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-196', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-197', 1, 'Access Permission', 'create');
--
INSERT INTO AssociationProp VALUES ('a-185', 1, 'Ordinal Number', '01');
INSERT INTO AssociationProp VALUES ('a-246', 1, 'Ordinal Number', '02');
INSERT INTO AssociationProp VALUES ('a-192', 1, 'Ordinal Number', '03');
INSERT INTO AssociationProp VALUES ('a-189', 1, 'Ordinal Number', '04');
INSERT INTO AssociationProp VALUES ('a-188', 1, 'Ordinal Number', '05');
INSERT INTO AssociationProp VALUES ('a-181', 1, 'Ordinal Number', '07');
INSERT INTO AssociationProp VALUES ('a-191', 1, 'Ordinal Number', '08');
INSERT INTO AssociationProp VALUES ('a-326', 1, 'Ordinal Number', '09');
INSERT INTO AssociationProp VALUES ('a-114', 1, 'Ordinal Number', '50');
INSERT INTO AssociationProp VALUES ('a-115', 1, 'Ordinal Number', '55');
INSERT INTO AssociationProp VALUES ('a-333', 1, 'Ordinal Number', '60');

--- "Type Builder" ---
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-263', '', 't-constructionworkspace', 1, 'tt-topictype', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-264', '', 't-constructionworkspace', 1, 'tt-assoctype', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-265', '', 't-constructionworkspace', 1, 'tt-property', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-266', '', 't-constructionworkspace', 1, 'tt-constant', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-267', '', 't-constructionworkspace', 1, 'tt-datasource', 1);
--
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-205', '', 't-constructionworkspace', 1, 'at-relation', 1);
--
INSERT INTO AssociationProp VALUES ('a-263', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-264', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-265', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-266', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-267', 1, 'Access Permission', 'create');
--
INSERT INTO AssociationProp VALUES ('a-205', 1, 'Access Permission', 'create');

--- "Administration" ---
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-180', '', 't-administrationgroup', 1, 'tt-user', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-230', '', 't-administrationgroup', 1, 'tt-whoistopic', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-282', '', 't-administrationgroup', 1, 'tt-installation', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-260', '', 't-administrationgroup', 1, 'tt-documenttype', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-261', '', 't-administrationgroup', 1, 'tt-mimetype', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-262', '', 't-administrationgroup', 1, 'tt-application', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-208', '', 't-administrationgroup', 1, 'tt-exportformat', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-229', '', 't-administrationgroup', 1, 'tt-message', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-209', '', 't-administrationgroup', 1, 'tt-messageboard', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-210', '', 't-administrationgroup', 1, 'tt-chat', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-116', '', 't-administrationgroup', 1, 'tt-chatboard', 1);
--
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-182', '', 't-administrationgroup', 1, 'at-membership', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-183', '', 't-administrationgroup', 1, 'at-groupleader', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-184', '', 't-administrationgroup', 1, 'at-publishpermission', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-207', '', 't-administrationgroup', 1, 'at-uses', 1);
INSERT INTO Association VALUES ('at-uses', 1, 1, 'a-171', '', 't-administrationgroup', 1, 'at-preference', 1);
--
INSERT INTO AssociationProp VALUES ('a-180', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-230', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-282', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-260', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-261', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-262', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-208', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-229', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-209', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-210', 1, 'Access Permission', 'view');
INSERT INTO AssociationProp VALUES ('a-116', 1, 'Access Permission', 'view');
--
INSERT INTO AssociationProp VALUES ('a-182', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-183', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-184', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-207', 1, 'Access Permission', 'create');
INSERT INTO AssociationProp VALUES ('a-171', 1, 'Access Permission', 'create');



--------------------------
--- Users & Workspaces ---
--------------------------



--- "root" (User) ---
INSERT INTO Topic VALUES ('tt-user', 1, 1, 't-rootuser', 'root');
INSERT INTO TopicProp VALUES ('t-rootuser', 1, 'Username', 'root');
-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-rootuserpersonalmap', 'root');
INSERT INTO TopicProp VALUES ('t-rootuserpersonalmap', 1, 'Name', 'root');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-77', '', 't-rootuser', 1, 't-rootuserpersonalmap', 1);
-- MIME Configuration
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-rootuserconfmap', 'MIME Configuration');
INSERT INTO TopicProp VALUES ('t-rootuserconfmap', 1, 'Name', 'MIME Configuration');
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-80', '', 't-rootuser', 1, 't-rootuserconfmap', 1);
-- memberships
INSERT INTO Association VALUES ('at-membership', 1, 1, 'a-3', '', 't-rootuser', 1, 't-corporategroup', 1);
INSERT INTO Association VALUES ('at-membership', 1, 1, 'a-76', '', 't-rootuser', 1, 't-administrationgroup', 1);
INSERT INTO Association VALUES ('at-membership', 1, 1, 'a-268', '', 't-rootuser', 1, 't-constructionworkspace', 1);

--- "DeepaMehta" (Workspace) ---
INSERT INTO Topic VALUES ('tt-workspace', 1, 1, 't-corporategroup', 'DeepaMehta');
INSERT INTO TopicProp VALUES ('t-corporategroup', 1, 'Name', 'DeepaMehta');
INSERT INTO TopicProp VALUES ('t-corporategroup', 1, 'Public', 'off');
INSERT INTO TopicProp VALUES ('t-corporategroup', 1, 'Default', 'on');
-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-corporatemap', 'DeepaMehta');
INSERT INTO TopicProp VALUES ('t-corporatemap', 1, 'Name', 'DeepaMehta');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-79', '', 't-corporategroup', 1, 't-corporatemap', 1);
-- chat
INSERT INTO Topic VALUES ('tt-chatboard', 1, 1, 't-corparatechat', 'DeepaMehta Chats');
INSERT INTO TopicProp VALUES ('t-corparatechat', 1, 'Name', 'DeepaMehta Chats');
INSERT INTO ViewTopic VALUES ('t-corporatemap', 1, 't-corparatechat', 1, 500, 100);
-- forum
INSERT INTO Topic VALUES ('tt-messageboard', 1, 1, 't-deepamehtaforum', 'DeepaMehta Forum');
INSERT INTO TopicProp VALUES ('t-deepamehtaforum', 1, 'Name', 'DeepaMehta Forum');
INSERT INTO ViewTopic VALUES ('t-corporatemap', 1, 't-deepamehtaforum', 1, 500, 50);
-- example topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-deepamehtaarch', 'DeepaMehta Architecture');
INSERT INTO TopicProp VALUES ('t-deepamehtaarch', 1, 'Name', 'DeepaMehta Architecture');
INSERT INTO TopicProp VALUES ('t-deepamehtaarch', 1, 'Background Image', 'arch.png');
INSERT INTO ViewTopic VALUES ('t-corporatemap', 1, 't-deepamehtaarch', 1, 100, 100);

-- webpage
INSERT INTO Topic VALUES ('tt-webpage', 1, 1, 't-deepamehtawebpage', 'DeepaMehta Homepage');
INSERT INTO TopicProp VALUES ('t-deepamehtawebpage', 1, 'Name', 'DeepaMehta Homepage');
INSERT INTO TopicProp VALUES ('t-deepamehtawebpage', 1, 'URL', 'http://www.deepamehta.de/');
-- website
INSERT INTO Topic VALUES ('tt-website', 1, 1, 't-deepamehtawebsite', 'www.deepamehta.de');
INSERT INTO TopicProp VALUES ('t-deepamehtawebsite', 1, 'Name', 'www.deepamehta.de');
-- domain
INSERT INTO Topic VALUES ('tt-internetdomain', 1, 1, 't-deepamehtadomain', 'deepamehta.de');
INSERT INTO TopicProp VALUES ('t-deepamehtadomain', 1, 'Name', 'deepamehta.de');
-- associate forum with webpage
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-269', '', 't-deepamehtaforum', 1, 't-deepamehtawebpage', 1);
-- associate webpage with website
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-206', '', 't-deepamehtawebpage', 1, 't-deepamehtawebsite', 1);
-- associate website with domain
INSERT INTO Association VALUES ('at-association', 1, 1, 'a-322', '', 't-deepamehtawebsite', 1, 't-deepamehtadomain', 1);

--- "Type Builder" (Workspace) ---
INSERT INTO Topic VALUES ('tt-workspace', 1, 1, 't-constructionworkspace', 'Type Builder');
INSERT INTO TopicProp VALUES ('t-constructionworkspace', 1, 'Name', 'Type Builder');
INSERT INTO TopicProp VALUES ('t-constructionworkspace', 1, 'Public', 'off');
INSERT INTO TopicProp VALUES ('t-constructionworkspace', 1, 'Default', 'off');
-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-constructiontopicmap', 'Type Builder');
INSERT INTO TopicProp VALUES ('t-constructiontopicmap', 1, 'Name', 'Type Builder');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-231', '', 't-constructionworkspace', 1, 't-constructiontopicmap', 1);
-- chat
INSERT INTO Topic VALUES ('tt-chatboard', 1, 1, 't-constructionchatboard', 'Type Builder Chats');
INSERT INTO TopicProp VALUES ('t-constructionchatboard', 1, 'Name', 'Type Builder Chats');
INSERT INTO ViewTopic VALUES ('t-constructiontopicmap', 1, 't-constructionchatboard', 1, 600, 100);
-- forum
INSERT INTO Topic VALUES ('tt-messageboard', 1, 1, 't-constructionforum', 'Type Builder Forum');
INSERT INTO TopicProp VALUES ('t-constructionforum', 1, 'Name', 'Type Builder Forum');
INSERT INTO ViewTopic VALUES ('t-constructiontopicmap', 1, 't-constructionforum', 1, 600, 50);

--- "Administration" (Workspace) ---
INSERT INTO Topic VALUES ('tt-workspace', 1, 1, 't-administrationgroup', 'Administration');
INSERT INTO TopicProp VALUES ('t-administrationgroup', 1, 'Name', 'Administration');
INSERT INTO TopicProp VALUES ('t-administrationgroup', 1, 'Public', 'off');
INSERT INTO TopicProp VALUES ('t-administrationgroup', 1, 'Default', 'off');
-- workspace topicmap
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-administrationgroupmap', 'Administration');
INSERT INTO TopicProp VALUES ('t-administrationgroupmap', 1, 'Name', 'Administration');
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-78', '', 't-administrationgroup', 1, 't-administrationgroupmap', 1);
-- chat
INSERT INTO Topic VALUES ('tt-chatboard', 1, 1, 't-adminchat', 'Administration Chats');
INSERT INTO TopicProp VALUES ('t-adminchat', 1, 'Name', 'Administration Chats');
INSERT INTO ViewTopic VALUES ('t-administrationgroupmap', 1, 't-adminchat', 1, 430, 120);
-- forum
INSERT INTO Topic VALUES ('tt-messageboard', 1, 1, 't-administrationforum', 'Administration Forum');
INSERT INTO TopicProp VALUES ('t-administrationforum', 1, 'Name', 'Administration Forum');
INSERT INTO ViewTopic VALUES ('t-administrationgroupmap', 1, 't-administrationforum', 1, 430, 70);
-- further topics
INSERT INTO ViewTopic VALUES ('t-administrationgroupmap', 1, 't-workgroupmap', 1, 30, 70);
INSERT INTO ViewTopic VALUES ('t-administrationgroupmap', 1, 't-directoriesmap', 1, 30, 120);
INSERT INTO ViewTopic VALUES ('t-administrationgroupmap', 1, 't-cmimportexport', 1, 230, 70);

-- set default "Export Format" preference for "root"
INSERT INTO Association VALUES ('at-preference', 1, 1, 'a-253', '', 't-rootuser', 1, 't-xml', 1);

-- The views for the "Administration" group
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-workgroupmap', 'Users and Groups');
INSERT INTO TopicProp VALUES ('t-workgroupmap', 1, 'Name', 'Users and Groups');
INSERT INTO Topic VALUES ('tt-topicmap', 1, 1, 't-directoriesmap', 'Directory Services');
INSERT INTO TopicProp VALUES ('t-directoriesmap', 1, 'Name', 'Directory Services');
-- the User Authentification Datasource
INSERT INTO Topic VALUES ('tt-authentificationsource', 1, 1, 't-useraccounts', 'User Accounts');
INSERT INTO TopicProp VALUES ('t-useraccounts', 1, 'Name', 'User Accounts');
-- write comments in "description" of AuthentificationSource
INSERT INTO TopicProp VALUES ('t-useraccounts', 1, 'Description', 'This topic must be associated with one of the Login topics. The association must be of type "association" and directed from AuthentificationSource to Login. If there is no such association, the CorporateMemory will be used for authentification.');
-- CM Import/Export
INSERT INTO Topic VALUES ('tt-cmimportexport', 1, 1, 't-cmimportexport', 'CM Import/Export');
INSERT INTO TopicProp VALUES ('t-cmimportexport', 1, 'Name', 'CM Import/Export');

--- Export Formats
INSERT INTO Topic VALUES ('tt-exportformat', 1, 1, 't-xml', 'XML (ISO 13250 Topic Map)');
INSERT INTO Topic VALUES ('tt-exportformat', 1, 1, 't-svg', 'SVG (Scalable Vector Graphics)');
INSERT INTO Topic VALUES ('tt-exportformat', 1, 1, 't-pdf', 'PDF (Portable Document Format)');
INSERT INTO TopicProp VALUES ('t-xml', 1, 'Name', 'XML (ISO 13250 Topic Map)');
INSERT INTO TopicProp VALUES ('t-svg', 1, 'Name', 'SVG (Scalable Vector Graphics)');
INSERT INTO TopicProp VALUES ('t-pdf', 1, 'Name', 'PDF (Portable Document Format)');
--- Document Types
INSERT INTO Topic VALUES ('tt-documenttype', 1, 1, 't-textfile', '.txt');
INSERT INTO Topic VALUES ('tt-documenttype', 1, 1, 't-gifimage', '.gif');
INSERT INTO Topic VALUES ('tt-documenttype', 1, 1, 't-jpgimage', '.jpg');
INSERT INTO Topic VALUES ('tt-documenttype', 1, 1, 't-tiffimage', '.tiff');
INSERT INTO Topic VALUES ('tt-documenttype', 1, 1, 't-mp3audio', '.mp3');
INSERT INTO Topic VALUES ('tt-documenttype', 1, 1, 't-worddocument', '.doc');
INSERT INTO Topic VALUES ('tt-documenttype', 1, 1, 't-exceldocument', '.xls');
INSERT INTO Topic VALUES ('tt-documenttype', 1, 1, 't-powerpointdocument', '.ppt');
INSERT INTO Topic VALUES ('tt-documenttype', 1, 1, 't-pdfdocument', '.pdf');
INSERT INTO TopicProp VALUES ('t-textfile', 1, 'Name', '.txt');
INSERT INTO TopicProp VALUES ('t-gifimage', 1, 'Name', '.gif');
INSERT INTO TopicProp VALUES ('t-jpgimage', 1, 'Name', '.jpg');
INSERT INTO TopicProp VALUES ('t-tiffimage', 1, 'Name', '.tiff');
INSERT INTO TopicProp VALUES ('t-mp3audio', 1, 'Name', '.mp3');
INSERT INTO TopicProp VALUES ('t-worddocument', 1, 'Name', '.doc');
INSERT INTO TopicProp VALUES ('t-exceldocument', 1, 'Name', '.xls');
INSERT INTO TopicProp VALUES ('t-powerpointdocument', 1, 'Name', '.ppt');
INSERT INTO TopicProp VALUES ('t-pdfdocument', 1, 'Name', '.pdf');
--- MIME Types
INSERT INTO Topic VALUES ('tt-mimetype', 1, 1, 't-textplain', 'text/plain');
INSERT INTO Topic VALUES ('tt-mimetype', 1, 1, 't-imagegif', 'image/gif');
INSERT INTO Topic VALUES ('tt-mimetype', 1, 1, 't-imagejpeg', 'image/jpeg');
INSERT INTO Topic VALUES ('tt-mimetype', 1, 1, 't-imagetiff', 'image/tiff');
INSERT INTO Topic VALUES ('tt-mimetype', 1, 1, 't-audioxmpeg', 'audio/x-mpeg');
INSERT INTO Topic VALUES ('tt-mimetype', 1, 1, 't-applicationmsword', 'application/msword');
INSERT INTO Topic VALUES ('tt-mimetype', 1, 1, 't-applicationmsexcel', 'application/msexcel');
INSERT INTO Topic VALUES ('tt-mimetype', 1, 1, 't-applicationmspowerpoint', 'application/mspowerpoint');
INSERT INTO Topic VALUES ('tt-mimetype', 1, 1, 't-applicationadobeacrobat', 'application/adobeacrobat');
INSERT INTO TopicProp VALUES ('t-textplain', 1, 'Name', 'text/plain');
INSERT INTO TopicProp VALUES ('t-imagegif', 1, 'Name', 'image/gif');
INSERT INTO TopicProp VALUES ('t-imagejpeg', 1, 'Name', 'image/jpeg');
INSERT INTO TopicProp VALUES ('t-imagetiff', 1, 'Name', 'image/tiff');
INSERT INTO TopicProp VALUES ('t-audioxmpeg', 1, 'Name', 'audio/x-mpeg');
INSERT INTO TopicProp VALUES ('t-applicationmsword', 1, 'Name', 'application/msword');
INSERT INTO TopicProp VALUES ('t-applicationmsexcel', 1, 'Name', 'application/msexcel');
INSERT INTO TopicProp VALUES ('t-applicationmspowerpoint', 1, 'Name', 'application/mspowerpoint');
INSERT INTO TopicProp VALUES ('t-applicationadobeacrobat', 1, 'Name', 'application/adobeacrobat');
--- Applications
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-supernotetab', 'SuperNotetab');
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-nedit', 'Nedit');
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-bbedit', 'BBEdit');
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-photoshop', 'Photoshop');
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-gimp', 'Gimp');
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-winamp', 'WinAmp');
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-xmms', 'Xmms');
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-microsoftword', 'Word');
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-staroffice', 'Star Office');
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-microsoftexcel', 'Excel');
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-microsoftpowerpoint', 'Powerpoint');
INSERT INTO Topic VALUES ('tt-application', 1, 1, 't-acrobatreader', 'Acrobat Reader');
INSERT INTO TopicProp VALUES ('t-supernotetab', 1, 'Name', 'SuperNotetab');
INSERT INTO TopicProp VALUES ('t-nedit', 1, 'Name', 'Nedit');
INSERT INTO TopicProp VALUES ('t-bbedit', 1, 'Name', 'BBEdit');
INSERT INTO TopicProp VALUES ('t-photoshop', 1, 'Name', 'Photoshop');
INSERT INTO TopicProp VALUES ('t-gimp', 1, 'Name', 'Gimp');
INSERT INTO TopicProp VALUES ('t-winamp', 1, 'Name', 'WinAmp');
INSERT INTO TopicProp VALUES ('t-xmms', 1, 'Name', 'Xmms');
INSERT INTO TopicProp VALUES ('t-microsoftword', 1, 'Name', 'Word');
INSERT INTO TopicProp VALUES ('t-staroffice', 1, 'Name', 'Star Office');
INSERT INTO TopicProp VALUES ('t-microsoftexcel', 1, 'Name', 'Excel');
INSERT INTO TopicProp VALUES ('t-microsoftpowerpoint', 1, 'Name', 'Powerpoint');
INSERT INTO TopicProp VALUES ('t-acrobatreader', 1, 'Name', 'Acrobat Reader');
-- Standard associations between document types and MIME types
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-81', '', 't-textplain', 1, 't-textfile', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-82', '', 't-imagegif', 1, 't-gifimage', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-83', '', 't-imagejpeg', 1, 't-jpgimage', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-84', '', 't-imagetiff', 1, 't-tiffimage', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-85', '', 't-audioxmpeg', 1, 't-mp3audio', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-86', '', 't-applicationmsword', 1, 't-worddocument', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-286', '', 't-applicationmsexcel', 1, 't-exceldocument', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-287', '', 't-applicationmspowerpoint', 1, 't-powerpointdocument', 1);
INSERT INTO Association VALUES ('at-aggregation', 1, 1, 'a-288', '', 't-applicationadobeacrobat', 1, 't-pdfdocument', 1);
---
--- Create standard installation "DeepaMehta"
---
INSERT INTO Topic VALUES ('tt-installation', 1, 1, 't-deepamehtainstallation', 'DeepaMehta');
INSERT INTO TopicProp VALUES ('t-deepamehtainstallation', 1, 'Name', 'DeepaMehta');
INSERT INTO TopicProp VALUES ('t-deepamehtainstallation', 1, 'Server Name', 'DeepaMehtaServer 2.0b8');
INSERT INTO TopicProp VALUES ('t-deepamehtainstallation', 1, 'Client Name', 'DeepaMehta 2.0b8');
INSERT INTO TopicProp VALUES ('t-deepamehtainstallation', 1, 'Active', 'on');
INSERT INTO TopicProp VALUES ('t-deepamehtainstallation', 1, 'Customer Icon', 'deepamehta-logo-tiny.png');
-- INSERT INTO TopicProp VALUES ('t-deepamehtainstallation', 1, 'Corporate Icon', '');
-- assign preferences to installation ("Export Format")
INSERT INTO Association VALUES ('at-preference', 1, 1, 'a-270', '', 't-deepamehtainstallation', 1, 't-xml', 1);



----------------------
--- Standard Views ---
----------------------



INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-textfile', 1, 50, 50);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-gifimage', 1, 50, 90);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-jpgimage', 1, 50, 130);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-tiffimage', 1, 50, 170);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-mp3audio', 1, 50, 210);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-worddocument', 1, 50, 250);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-textplain', 1, 150, 50);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-imagegif', 1, 150, 90);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-imagejpeg', 1, 150, 130);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-imagetiff', 1, 150, 170);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-audioxmpeg', 1, 150, 210);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-applicationmsword', 1, 150, 250);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-supernotetab', 1, 350, 50);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-nedit', 1, 350, 90);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-bbedit', 1, 350, 130);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-photoshop', 1, 350, 170);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-gimp', 1, 350, 210);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-winamp', 1, 350, 250);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-xmms', 1, 350, 290);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-microsoftword', 1, 350, 330);
INSERT INTO ViewTopic VALUES ('t-genericconfmap', 1, 't-staroffice', 1, 350, 370);
INSERT INTO ViewAssociation VALUES ('t-genericconfmap', 1, 'a-81', 1);
INSERT INTO ViewAssociation VALUES ('t-genericconfmap', 1, 'a-82', 1);
INSERT INTO ViewAssociation VALUES ('t-genericconfmap', 1, 'a-83', 1);
INSERT INTO ViewAssociation VALUES ('t-genericconfmap', 1, 'a-84', 1);
INSERT INTO ViewAssociation VALUES ('t-genericconfmap', 1, 'a-85', 1);
INSERT INTO ViewAssociation VALUES ('t-genericconfmap', 1, 'a-86', 1);

INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-textfile', 1, 50, 50);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-gifimage', 1, 50, 90);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-jpgimage', 1, 50, 130);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-tiffimage', 1, 50, 170);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-mp3audio', 1, 50, 210);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-worddocument', 1, 50, 250);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-textplain', 1, 150, 50);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-imagegif', 1, 150, 90);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-imagejpeg', 1, 150, 130);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-imagetiff', 1, 150, 170);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-audioxmpeg', 1, 150, 210);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-applicationmsword', 1, 150, 250);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-supernotetab', 1, 350, 50);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-nedit', 1, 350, 90);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-bbedit', 1, 350, 130);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-photoshop', 1, 350, 170);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-gimp', 1, 350, 210);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-winamp', 1, 350, 250);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-xmms', 1, 350, 290);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-microsoftword', 1, 350, 330);
INSERT INTO ViewTopic VALUES ('t-rootuserconfmap', 1, 't-staroffice', 1, 350, 370);
INSERT INTO ViewAssociation VALUES ('t-rootuserconfmap', 1, 'a-81', 1);
INSERT INTO ViewAssociation VALUES ('t-rootuserconfmap', 1, 'a-82', 1);
INSERT INTO ViewAssociation VALUES ('t-rootuserconfmap', 1, 'a-83', 1);
INSERT INTO ViewAssociation VALUES ('t-rootuserconfmap', 1, 'a-84', 1);
INSERT INTO ViewAssociation VALUES ('t-rootuserconfmap', 1, 'a-85', 1);
INSERT INTO ViewAssociation VALUES ('t-rootuserconfmap', 1, 'a-86', 1);

--- "root" Users Personal Workspace content ---

INSERT INTO ViewTopic VALUES ('t-rootuserpersonalmap', 1, 't-rootuserconfmap', 1, 600, 30);

--- "Users and Groups" view content ---

INSERT INTO ViewTopic VALUES ('t-workgroupmap', 1, 't-rootuser', 1, 250, 200);
INSERT INTO ViewTopic VALUES ('t-workgroupmap', 1, 't-corporategroup', 1, 270, 100);
INSERT INTO ViewTopic VALUES ('t-workgroupmap', 1, 't-administrationgroup', 1, 170, 240);
INSERT INTO ViewTopic VALUES ('t-workgroupmap', 1, 't-constructionworkspace', 1, 310, 260);
INSERT INTO ViewAssociation VALUES ('t-workgroupmap', 1, 'a-3', 1);
INSERT INTO ViewAssociation VALUES ('t-workgroupmap', 1, 'a-76', 1);
INSERT INTO ViewAssociation VALUES ('t-workgroupmap', 1, 'a-268', 1);

--- "Directory Services" view content ---

INSERT INTO ViewTopic VALUES ('t-directoriesmap', 1, 't-useraccounts', 1, 100, 150);

---
--- Key Generator
---
--   1 -  500 Kernel
-- 501 - 1000 included examples
-- 600 -  799 Kompetenzstern
INSERT INTO KeyGenerator VALUES ('Topic', 1001);
INSERT INTO KeyGenerator VALUES ('Association', 1001);
-- Not a Key Generator but the (constant) version of the database model
INSERT INTO KeyGenerator VALUES ('DB-Model Version', 2);
-- Not a Key Generator but the (constant) version of the database content
INSERT INTO KeyGenerator VALUES ('DB-Content Version', 20);
