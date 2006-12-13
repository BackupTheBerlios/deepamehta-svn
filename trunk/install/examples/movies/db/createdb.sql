# DeepaMehta's "Movies & Actors" example database creation script for use with MySQL.
# must run as MySQL root user.


CREATE DATABASE Movies;
GRANT ALL PRIVILEGES ON Movies.* TO movies@localhost IDENTIFIED BY 'movies' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON Movies.* TO movies@"%" IDENTIFIED BY 'movies' WITH GRANT OPTION;
