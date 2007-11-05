@echo off
if "%ANT_HOME%"=="" goto setDefaultAntHome

:stripAntHome
if not _%ANT_HOME:~-1%==_\ goto testAntHome
set ANT_HOME=%ANT_HOME:~0,-1%
goto stripAntHome

:testAntHome
if exist %ANT_HOME%/bin/ant.bat goto callAnt

:setDefaultAntHome
rem %~dp0 is expanded pathname of the current script under NT
set ANT_HOME=%~dp0ant

:callAnt
call %ANT_HOME%/bin/ant.bat %%
