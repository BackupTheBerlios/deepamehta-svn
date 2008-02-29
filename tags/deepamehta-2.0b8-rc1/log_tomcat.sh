#!/bin/sh
export CATALINA_HOME=/usr/local/tomcat

tail -f $CATALINA_HOME/logs/catalina.out
