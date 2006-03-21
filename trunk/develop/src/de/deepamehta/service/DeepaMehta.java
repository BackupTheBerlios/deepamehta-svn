package de.deepamehta.service;

import java.awt.Container;

import javax.swing.JApplet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.client.PresentationDirectives;
import de.deepamehta.client.PresentationService;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentFactory;
import de.deepamehta.environment.instance.InstanceConfiguration;



/**
 * DeepaMehta as monolithic application (the server is integrated).
 * <P>
 * <HR>
 * Last functional change: 5.8.2002 (2.0a15-pre11)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class DeepaMehta implements ApplicationServiceHost, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************

	
	private static Log logger = LogFactory.getLog(DeepaMehta.class);

	private Environment env;
	private InstanceConfiguration instanceConfig;
	private PresentationService ps;
	private ApplicationService as;

	private static final String commInfo = "direct method calls (embedded service)";
	private static final String errText = "DeepaMehta can't run";



	// ********************************************************************************
	// *** Implementation of interface de.deepamehta.service.ApplicationServiceHost ***
	// ********************************************************************************



	public String getCommInfo() {
		return commInfo;
	}

	public void sendDirectives(Session session, CorporateDirectives directives,
							   ApplicationService as, String topicmapID, String viewmode) {
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		// ### the monolitih application knows topicmapID, viewmode, the client not!!!
		ps.processDirectives(new PresentationDirectives(directives, ps) /* ###, topicmapID, viewmode */);
	}

	public void broadcastChangeNotification(String topicID) {
		// ### do nothing
	}



	// ****************************
	// *** Application specific ***
	// ****************************



	public static void main(String[] args) {
		new DeepaMehta().initApplication(args);
	}

	/**
	 * Application specific initialization.
	 * @param args command line arguments
	 */
	private void initApplication(String[] args) {
		// >>> compare to init()
		// >>> compare to DeepaMehtaClient.initApplication()
		// >>> compare to DeepaMehtaServer.main()
		// >>> compare to DeepaMehtaServer.runServer()
		try {
			// initialize environment
			this.env = EnvironmentFactory.getMonolithicEnvironment(args);
			logger.info("Running as monolithic application.");
			
			// find out which instance to start
			this.instanceConfig = this.env.getInstanceConfiguration();
			logger.info("Starting instance " + this.instanceConfig.getId() + "...");
			
			// create presentation service 
			this.ps = new PresentationService();
			// reporting
			this.ps.initialize();
			
			// create application service
			ApplicationService aslocal = ApplicationService.create(this, this.instanceConfig);
			
			// ### Note: the client name is unknown at this point
			
			this.ps.createMainWindow("DeepaMehta " + CLIENT_VERSION);
			aslocal.setGraphicsContext(this.ps.mainWindow);
			this.ps.installationProps = aslocal.getInstallationProps();
			
			// create session
			Session session = aslocal.createSession(aslocal.getNewSessionID(), "localhost", this.ps.hostAddress);
			
			this.ps.setService(new EmbeddedService(session, aslocal, this.ps));
			this.ps.cp.add(this.ps.createLoginGUI());
			this.ps.mainWindow.setTitle(this.ps.getClientName());
			// ### ps.mainWindow.pack();
			this.ps.mainWindow.setVisible(true);
		} catch (DeepaMehtaException e) {
			logger.error("Caught unhandled DeepaMehta exception at top level.", e);
		} catch (Throwable e) {
			logger.error("Caught other unhandled exception at top level.", e);
		}
	}
}
