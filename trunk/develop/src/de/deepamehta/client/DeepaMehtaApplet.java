package de.deepamehta.client;

import java.awt.Container;
import java.awt.Graphics;
import java.io.IOException;

import javax.swing.JApplet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentException;
import de.deepamehta.environment.EnvironmentFactory;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.environment.instance.UnknownInstanceException;



/**
 * The DeepaMehta client (applet version).
 * <P>
 * <HR>
 * Last functional change: 2.4.2004 (2.0b3-pre2)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class DeepaMehtaApplet extends JApplet implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************

	private static Log logger = LogFactory.getLog(DeepaMehtaApplet.class);
	
	private Environment env;
	private InstanceConfiguration instanceConfig;
	private PresentationService ps;
	//
	private static final String errText = "DeepaMehtaClient can't run";
	private boolean firstPaint = true;



	// ***************
	// *** Methods ***
	// ***************

	/**
	 * Applet specific initialization.
	 */
	public void init() {
		// ### compare to initApplication()
		// ### compare to DeepaMehta.init()
		
		Container cp = getContentPane();
		String host = "";
		int port = 0;
		
		try {
			// initialize environment
			this.env = EnvironmentFactory.getAppletEnvironment(this);
			logger.info("Running as applet only.");
			
			// find out which instance to start
			this.instanceConfig = this.env.getInstanceConfiguration();
			logger.info("Starting instance " + this.instanceConfig.getId() + "...");
			host = this.instanceConfig.getClientHost();
			port = this.instanceConfig.getClientPort();
			
			// create presentation service 
			this.ps = new PresentationService();
			// reporting
			// System.out.println("\n--- DeepaMehtaClient " + CLIENT_VERSION + " runs as " +
			// 	"applet on \"" + ps.hostAddress + "\" (" + ps.platform + ") ---");
			//
			ps.initialize();
			

			String demoMapID = getParameter("DEMO_MAP");
			ps.setDemoMap(demoMapID);
			ps.setApplet(this);
			ps.setService(new SocketService(host, port, ps)); 
			if (demoMapID != null) {
				cp.add(ps.createStartDemoGUI());
			} else {
				cp.add(ps.createLoginGUI());
			}
	    } catch (EnvironmentException e) {
	    	logger.error("An error occurred during initialization of the environment.", e);
        } catch (UnknownInstanceException e) {
            logger.error("Unable to load the instance specified.", e);
        } catch (DeepaMehtaException e) {
            logger.error("Uncaught DeepaMehta exception at top level.", e);
			cp.add(ps.createErrorGUI(e, host, port));
		} catch (IOException e) {
			logger.error("Uncaught I/O exception at top level.", e);
			cp.add(ps.createErrorGUI(e, host, port));
		}
	}

	/* ### public void start() {
		System.out.println(">>> applet started");
	}

	public void stop() {
		System.out.println(">>> applet stopped");
	} */

	public void destroy() {
		System.out.println(">>> applet destroyed");
		//
		ps.close();
		// ### ps.mainWindow.dispose(); ??
	}

	// ---

	public void paint(Graphics g) {
		super.paint(g);
		if (firstPaint) {
			ps.focusUsername();
			firstPaint = false;
		}
	}
}
