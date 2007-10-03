
CREATE TABLE Movie (
    ID INT PRIMARY KEY NOT NULL,
    Title VARCHAR(100),
    Originaltitle VARCHAR(100),
    GattungID INT,
    Country VARCHAR(20),
    YearOfProduction VARCHAR(10),
    Duration CHAR(4)
);

CREATE TABLE Actor (
    ID INT PRIMARY KEY NOT NULL,
    Name VARCHAR(40)
);

CREATE TABLE Mitwirkung (
    MovieID INT NOT NULL,
    ActorID INT NOT NULL,
    INDEX(MovieID),
    INDEX(ActorID)
);

CREATE TABLE Gattung (
    ID INT PRIMARY KEY NOT NULL,
    Name VARCHAR(40)
);

