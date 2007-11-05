@ECHO OFF

rem IMPORTANT
rem you must extend the search path by MySQL's "bin" directory
rem set PATH=%PATH%;\path\to\mysql\bin

mysql -u dm -pdm DeepaMehta < %1
