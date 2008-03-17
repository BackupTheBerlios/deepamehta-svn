@echo off

rem %~dp0 is expanded pathname of the current script under NT
set ANT_HOME=%~dp0ant

call "%ANT_HOME%\bin\ant.bat" %*
