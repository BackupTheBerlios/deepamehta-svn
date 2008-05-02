-- Creates "Artfacts" example database
-- For use with MySQL, must run as MySQL root user

CREATE DATABASE Artfacts;
GRANT ALL PRIVILEGES ON Artfacts.* TO artfacts@localhost IDENTIFIED BY 'artfacts' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON Artfacts.* TO artfacts@"%" IDENTIFIED BY 'artfacts' WITH GRANT OPTION;
