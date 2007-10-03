-- disables the ampersand variable expansion
SET DEFINE OFF

-- drop all tables
@@..\db\dropall.sql

-- table definitions
@@..\db\db_tables_oracle.sql

-- table inserts
@@..\db\db_inserts.sql

-- restoring example application (movies.sql)
-- @@..\movies.sql

-- commit changes
commit;

-- exit SQLPlus
-- quit;











