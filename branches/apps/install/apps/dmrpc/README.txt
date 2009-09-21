==========================
DeepaMehta JSON RPC Webapp 
==========================

by Malte Reißig (mre@deepamehta.de)

11.09.2009

Prerequesites are an installed servlet container like tomcat on your webserver


Change your current working directory to DeepaMehta HOME where to you'll find the "run" script

1) ./run.sh config // and enter e.g. tomcat locationpath into the configuration wizard

2) ./run.sh build -f install/apps/dmrpc/build.xml

3) ./run.sh deploy -f install/apps/dmrpc/build.xml

If you configured the right location of your tomcat installation, it'll now contain the desired JSON RPC webapp.. 

NOTE: To make sure that the JSON RPC webapp serves the correct deepamehta server instance you possibly need to configure the desired service-instance name in the dmrpc/config/default/web.xml file at the pointed out location. This configuration error is recognized if a ServletInitException is thrown.

NOTE2: Since Tomcat 6 the webapps must have their libraries self-contained (in their WEB-INF DIR)

