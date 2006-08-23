-- DeepaMehta Database creation script for use with MySQL.
-- must run as MySQL root user.


CREATE DATABASE DeepaMehta;
GRANT ALL PRIVILEGES ON DeepaMehta.* TO dm@localhost IDENTIFIED BY 'dm' WITH GRANT OPTION;
GRANT ALL PRIVILEGES ON DeepaMehta.* TO dm@"%" IDENTIFIED BY 'dm' WITH GRANT OPTION;
