#!/bin/sh
export CATALINA_HOME=/usr/local/tomcat
export JAVA_HOME=/usr

cd install/client
$CATALINA_HOME/bin/startup.sh
