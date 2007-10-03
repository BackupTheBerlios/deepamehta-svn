-- DeepaMehta database model for use with Oracle.
-- This script creates just the tables.

CREATE TABLE topic (
    typeid      VARCHAR2(40) NOT NULL,
    typeversion INT         NOT NULL,
    version     INT         NOT NULL,
    id          VARCHAR2(40) NOT NULL,
    name        VARCHAR2(255) NOT NULL,
    CONSTRAINT pk_topic PRIMARY KEY (id, version)
);
CREATE INDEX i_topic ON topic (typeid, typeversion, name);

-- ----------------------------------------------------------------------------

CREATE TABLE association (
    typeid        VARCHAR2(40) NOT NULL,
    typeversion   INT         NOT NULL,
    version       INT         NOT NULL,
    id            VARCHAR2(40) NOT NULL,
    name          VARCHAR2(255) NOT NULL,
    topicid1      VARCHAR2(40) NOT NULL,
    topicversion1 INT         NOT NULL,
    topicid2      VARCHAR2(40) NOT NULL,
    topicversion2 INT         NOT NULL,
    CONSTRAINT pk_association PRIMARY KEY (id, version)
);
CREATE INDEX i1_association ON association (id, version, typeid, typeversion);
CREATE INDEX i2_association ON association (topicid1, topicversion1);
CREATE INDEX i3_association ON association (topicid2, topicversion2);

-- ----------------------------------------------------------------------------

CREATE TABLE viewtopic (
    viewtopicid      VARCHAR2(40) NOT NULL,
    viewtopicversion INT         NOT NULL,
    topicid          VARCHAR2(40) NOT NULL,
    topicversion     INT         NOT NULL,
    x                INT         NOT NULL,
    y                INT         NOT NULL
);
CREATE INDEX i1_viewtopic ON viewtopic (ViewTopicID, ViewTopicVersion);
CREATE INDEX i2_viewtopic ON viewtopic (TopicID);

-- ----------------------------------------------------------------------------

CREATE TABLE viewassociation (
    viewtopicid        VARCHAR2(40) NOT NULL,
    viewtopicversion   INT         NOT NULL,
    associationid      VARCHAR2(40) NOT NULL,
    associationversion INT         NOT NULL
);
CREATE INDEX i_viewassociation ON viewassociation (viewtopicid, viewtopicversion);

-- ----------------------------------------------------------------------------

CREATE TABLE topicprop (
    topicid      VARCHAR2(40) NOT NULL,
    topicversion INT         NOT NULL,
    propname    VARCHAR2(255) NOT NULL,
    propvalue   VARCHAR2(4000)
);
ALTER TABLE TopicProp ADD PRIMARY KEY (
	TopicID, TopicVersion, PropName
);

-- ----------------------------------------------------------------------------

CREATE TABLE associationprop (
    associationid      VARCHAR2(40) NOT NULL,
    associationversion INT         NOT NULL,
    propname          VARCHAR2(255) NOT NULL,
    propvalue         VARCHAR2(4000)
);
ALTER TABLE AssociationProp ADD PRIMARY KEY (
	AssociationID, AssociationVersion, PropName
);
    
-- ----------------------------------------------------------------------------

CREATE TABLE keygenerator (
    relation VARCHAR2(24) NOT NULL,
    nextkey  INT         NOT NULL,
    CONSTRAINT pk_keygenerator PRIMARY KEY (relation)
);

-- ----------------------------------------------------------------------------
