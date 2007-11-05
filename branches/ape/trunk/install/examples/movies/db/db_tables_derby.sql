
CREATE TABLE Movie (
    ID INT PRIMARY KEY NOT NULL,
    Title VARCHAR(100),
    Originaltitle VARCHAR(100),
    GattungID INT,
    Country VARCHAR(20),
    YearOfProduction VARCHAR(10),
    Duration CHAR(4)
);

CREATE TABLE ACTOR (
    ID INT PRIMARY KEY NOT NULL,
    Name VARCHAR(40)
);

CREATE TABLE Mitwirkung (
    MovieID INT NOT NULL,
    ActorID INT NOT NULL
);

CREATE INDEX IDX_Mitwirkung_MovieID ON Mitwirkung(MovieID);
CREATE INDEX IDX_Mitwirkung_ActorID ON Mitwirkung(ActorID);

CREATE TABLE Gattung (
    ID INT PRIMARY KEY NOT NULL,
    Name VARCHAR(40)
);

