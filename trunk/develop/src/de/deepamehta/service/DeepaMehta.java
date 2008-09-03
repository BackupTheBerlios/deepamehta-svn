package de.deepamehta.service;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.client.PresentationDirectives;
import de.deepamehta.client.PresentationService;

import java.awt.Container;

import javax.swing.JApplet;



/**
 * The DeepaMehta client as monolithic applet/application (the server is integrated).
 * <P>
 * ### Not yet functional
 * <P>
 * <HR>
 * Last functional change: 7.6.2006 (2.0b7)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class DeepaMehta extends JApplet implements ApplicationServiceHost, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private PresentationService ps;
	// ### private ApplicationService as;

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
		// ### the monolitic application knows topicmapID, viewmode, the client not!!!
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
	 */
	private void initApplication(String[] args) {
		// >>> compare to init()
		// >>> compare to DeepaMehtaClient.initApplication()
		// >>> compare to DeepaMehtaServer.main()
		// >>> compare to DeepaMehtaServer.runServer()
		try {
			// --- create presentation service ---
			this.ps = new PresentationService();
			// reporting
			System.out.println("\n--- DeepaMehta " + CLIENT_VERSION + " runs as " +
				"application on \"" + ps.hostAddress + "\" (" + ps.platform + ") ---");
			//
			ps.initialize();
			// --- create application service ---
			ApplicationServiceInstance instance = ApplicationServiceInstance.lookup(args);
			ApplicationService as = ApplicationService.create(this, instance);		// throws DME
			// ### Note: the client name is unknown at this point
			ps.createMainWindow("DeepaMehta " + CLIENT_VERSION);
			ps.installationProps = as.getInstallationProps();
			// --- create session ---
			Session session = as.createSession(as.getNewSessionID(), "localhost", ps.hostAddress);
			//
			ps.setService(new EmbeddedService(session, as, ps));
			ps.cp.add(ps.createLoginGUI());
			ps.mainWindow.setTitle(ps.getClientName());
			// ### ps.mainWindow.pack();
			ps.mainWindow.setVisible(true);
		} catch (DeepaMehtaException e) {
			System.out.println("*** " + errText + " (" + e.getMessage() + ")");
			e.printStackTrace();
		} catch (Throwable e) {
			System.out.println("*** DeepaMehta.initApplication(): " + e);
			e.printStackTrace();
		}
	}



	// ***********************
	// *** Applet specific ***
	// ***********************



	/**
	 * Applet specific initialization.
	 */
	public void init() {
		// >>> compare to initApplication()
		// >>> compare to DeepaMehtaClient.init()
		//
		Container cp = getContentPane();
		//
		try {
			// --- create presentation service ---
			this.ps = new PresentationService();
			// reporting
			System.out.println("\n--- DeepaMehta " + CLIENT_VERSION + " runs as " +
				"applet on \"" + ps.hostAddress + "\" (" + ps.platform + ") ---");
			//
			ps.initialize();
			// --- create application service ---
			String serviceName = getParameter("SERVICE_NAME");
			String demoMapID = getParameter("DEMO_MAP");
			ApplicationServiceInstance instance = ApplicationServiceInstance.lookup(serviceName);
			ApplicationService as = ApplicationService.create(this, instance);	// throws DME
			ps.installationProps = as.getInstallationProps();
			// --- create session ---
			Session session = as.createSession(as.getNewSessionID(), "localhost", ps.hostAddress);
			//
			ps.setDemoMap(demoMapID);
			ps.setApplet(this);
			ps.setService(new EmbeddedService(null, as, ps));	// ### null
			if (demoMapID != null) {
				cp.add(ps.createStartDemoGUI());
			} else {
				cp.add(ps.createLoginGUI());
			}
		} catch (DeepaMehtaException e) {
			System.out.println("*** " + errText + " (" + e.getMessage() + ")");
			cp.add(ps.createErrorGUI(e));
		}
	}
}
