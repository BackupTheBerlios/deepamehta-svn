CREATE TABLE movie
(
    id            INT NOT NULL,
    title         VARCHAR(100),
    originaltitle VARCHAR(100),
    gattungid     INT,
    country       VARCHAR(20),
    YearOfProduction          VARCHAR(10),
    duration      VARCHAR(4),
    CONSTRAINT pk_movie PRIMARY KEY (id)
);

CREATE TABLE actor
(
    id   INT NOT NULL,
    name VARCHAR(40),
    CONSTRAINT pk_actor PRIMARY KEY (id)
);

CREATE TABLE mitwirkung
(
    movieid INT NOT NULL,
    actorid INT NOT NULL
);
CREATE INDEX i1_mitwirkung ON mitwirkung (movieid);
CREATE INDEX i2_mitwirkung ON mitwirkung (actorid);

CREATE TABLE gattung
(
    id   INT NOT NULL,
    name VARCHAR(40),
    CONSTRAINT pk_gattung PRIMARY KEY (id)
);

