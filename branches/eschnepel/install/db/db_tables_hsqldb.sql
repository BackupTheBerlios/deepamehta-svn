-- DeepaMehta database model for use with MySQL.
-- This script just creates the tables.

CREATE CACHED TABLE Topic (
    TypeID CHAR(40) NOT NULL,
    TypeVersion INT NOT NULL,
    Version INT NOT NULL,
    ID CHAR(40) NOT NULL,
    Name CHAR(255) NOT NULL,
	PRIMARY KEY (ID, Version)
);
CREATE INDEX IDX_TopicType ON Topic (
	TypeID, TypeVersion, Name
);

CREATE CACHED TABLE Association (
    TypeID CHAR(40) NOT NULL,
    TypeVersion INT NOT NULL,
    Version INT NOT NULL,
    ID CHAR(40) NOT NULL,
    Name CHAR(255) NOT NULL,
    TopicID1 CHAR(40) NOT NULL,
    TopicVersion1 INT NOT NULL,
    TopicID2 CHAR(40) NOT NULL,
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

CREATE CACHED TABLE ViewTopic (
    ViewTopicID CHAR(40) NOT NULL,
	ViewTopicVersion INT NOT NULL,
    TopicID CHAR(40) NOT NULL,
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

CREATE CACHED TABLE ViewAssociation (
    ViewTopicID CHAR(40) NOT NULL,
	ViewTopicVersion INT NOT NULL,
    AssociationID CHAR(40) NOT NULL,
    AssociationVersion INT NOT NULL
);
CREATE INDEX IDX_ViewAssociation ON ViewAssociation (
	ViewTopicID, ViewTopicVersion
);

CREATE CACHED TABLE TopicProp (
    TopicID CHAR(40) NOT NULL,
    TopicVersion INT NOT NULL,
    PropName CHAR(40) NOT NULL,
    PropValue VARCHAR_IGNORECASE
);
CREATE INDEX IDX_TopicProp ON TopicProp (
    TopicID, TopicVersion, PropName
);

CREATE CACHED TABLE AssociationProp (
    AssociationID CHAR(40) NOT NULL,
    AssociationVersion INT NOT NULL,
    PropName CHAR(40) NOT NULL,
    PropValue VARCHAR_IGNORECASE
);
CREATE INDEX IDX_AssociationProp ON AssociationProp (
    AssociationID, AssociationVersion, PropName
);

CREATE TABLE KeyGenerator (
    Relation CHAR(24) NOT NULL PRIMARY KEY,
    NextKey INT NOT NULL
);

SET WRITE_DELAY 0 MILLIS
;
