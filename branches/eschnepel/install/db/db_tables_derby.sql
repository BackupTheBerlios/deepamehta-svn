-- DeepaMehta database model for use with MySQL.
-- This script just creates the tables.

CREATE TABLE Topic (
    TypeID VARCHAR(40) NOT NULL,
    TypeVersion INT NOT NULL,
    Version INT NOT NULL,
    ID VARCHAR(40) NOT NULL,
    Name VARCHAR(255) NOT NULL,
	PRIMARY KEY (ID, Version)
);
CREATE INDEX IDX_TopicType ON Topic (
	TypeID, TypeVersion, Name
);

CREATE TABLE Association (
    TypeID VARCHAR(40) NOT NULL,
    TypeVersion INT NOT NULL,
    Version INT NOT NULL,
    ID VARCHAR(40) NOT NULL,
    Name VARCHAR(255) NOT NULL,
    TopicID1 VARCHAR(40) NOT NULL,
    TopicVersion1 INT NOT NULL,
    TopicID2 VARCHAR(40) NOT NULL,
    TopicVersion2 INT NOT NULL,
	PRIMARY KEY (ID, Version)
);
CREATE INDEX IDX_AssociationType ON Association (
	ID, Version, TypeID, TypeVersion
);
CREATE INDEX IDX_TopicAssociation1 ON Association (
    TopicID1, TopicVersion1
);
CREATE INDEX IDX_TopicAssociation2 ON Association (
    TopicID2, TopicVersion2
);

CREATE TABLE ViewTopic (
    ViewTopicID VARCHAR(40) NOT NULL,
	ViewTopicVersion INT NOT NULL,
    TopicID VARCHAR(40) NOT NULL,
    TopicVersion INT NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL
);
CREATE INDEX IDX_ViewTopic ON ViewTopic (
    ViewTopicID, ViewTopicVersion
);
CREATE INDEX IDX_ViewTopicRef ON ViewTopic (
	TopicID
);

CREATE TABLE ViewAssociation (
    ViewTopicID VARCHAR(40) NOT NULL,
	ViewTopicVersion INT NOT NULL,
    AssociationID VARCHAR(40) NOT NULL,
    AssociationVersion INT NOT NULL
);
CREATE INDEX IDX_ViewAssociation ON ViewAssociation (
	ViewTopicID, ViewTopicVersion
);

CREATE TABLE TopicProp (
    TopicID VARCHAR(40) NOT NULL,
    TopicVersion INT NOT NULL,
    PropName VARCHAR(40) NOT NULL,
    PropValue VARCHAR(4096)
);
CREATE INDEX IDX_TopicProp ON TopicProp (
    TopicID, TopicVersion, PropName
);

CREATE TABLE AssociationProp (
    AssociationID VARCHAR(40) NOT NULL,
    AssociationVersion INT NOT NULL,
    PropName VARCHAR(40) NOT NULL,
    PropValue VARCHAR(4096)
);
CREATE INDEX IDX_AssociationProp ON AssociationProp (
    AssociationID, AssociationVersion, PropName
);

CREATE TABLE KeyGenerator (
    Relation VARCHAR(24) NOT NULL PRIMARY KEY,
    NextKey INT NOT NULL
);
