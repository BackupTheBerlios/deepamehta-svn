-- DeepaMehta database model for use with MySQL.
-- This script just creates the tables.

CREATE TABLE Topic (
    TypeID CHAR(40) NOT NULL,
    TypeVersion INT NOT NULL,
    Version INT NOT NULL,
    ID CHAR(40) NOT NULL,
    Name CHAR(255) NOT NULL,
	PRIMARY KEY (ID, Version),
    INDEX (TypeID, TypeVersion, Name)
);

CREATE TABLE Association (
    TypeID CHAR(40) NOT NULL,
    TypeVersion INT NOT NULL,
    Version INT NOT NULL,
    ID CHAR(40) NOT NULL,
    Name CHAR(255) NOT NULL,
    TopicID1 CHAR(40) NOT NULL,
    TopicVersion1 INT NOT NULL,
    TopicID2 CHAR(40) NOT NULL,
    TopicVersion2 INT NOT NULL,
	PRIMARY KEY (ID, Version),
	INDEX (ID, Version, TypeID, TypeVersion),
    INDEX (TopicID1, TopicVersion1),
    INDEX (TopicID2, TopicVersion2)
);

CREATE TABLE ViewTopic (
    ViewTopicID CHAR(40) NOT NULL,
	ViewTopicVersion INT NOT NULL,
    TopicID CHAR(40) NOT NULL,
    TopicVersion INT NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    INDEX (ViewTopicID, ViewTopicVersion),
	INDEX (TopicID)
);

CREATE TABLE ViewAssociation (
    ViewTopicID CHAR(40) NOT NULL,
	ViewTopicVersion INT NOT NULL,
    AssociationID CHAR(40) NOT NULL,
    AssociationVersion INT NOT NULL,
    INDEX (ViewTopicID, ViewTopicVersion)
);

CREATE TABLE TopicProp (
    TopicID CHAR(40) NOT NULL,
    TopicVersion INT NOT NULL,
    PropName CHAR(255) NOT NULL,
    PropValue MEDIUMTEXT,
    PRIMARY KEY (TopicID, TopicVersion, PropName)
);

CREATE TABLE AssociationProp (
    AssociationID CHAR(40) NOT NULL,
    AssociationVersion INT NOT NULL,
    PropName CHAR(255) NOT NULL,
    PropValue MEDIUMTEXT,
    PRIMARY KEY (AssociationID, AssociationVersion, PropName)
);

CREATE TABLE KeyGenerator (
    Relation CHAR(24) PRIMARY KEY NOT NULL,
    NextKey INT NOT NULL
);
