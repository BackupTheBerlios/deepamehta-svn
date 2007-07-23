@ECHO OFF

rem IMPORTANT
rem you must extend the search path by MySQL's "bin" directory
rem set PATH=%PATH%;\path\to\mysql\bin

rem this batchfile must be run from the directory where it is installed (install\examples\movies\bin)
rem this is the case when this batchfile is just double clicked

echo --- creating example database "Movies" (MySQL root user password required) ...

mysql -u root -p mysql < ..\db\createdb.sql

echo database \"Movies\" and database user created
echo Note: the Movies SQL console is accessed by the "install\examples\movies\bin\mdb" command

mysql -u movies -pmovies Movies < ..\db\db_tables_mysql.sql
mysql -u movies -pmovies Movies < ..\db\db_inserts.sql

echo default "Movies" content created

echo example database "Movies" created

pause
