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
 * The DeepaMehta client (standalone application).
 * <P>
 * <HR>
 * Last functional change: 2.4.2004 (2.0b3-pre2)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class DeepaMehtaClient implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************

	private static Log logger = LogFactory.getLog(DeepaMehtaClient.class);
	
	private Environment env;
	private InstanceConfiguration instanceConfig;
	private PresentationService ps;
	//
	private static final String errText = "DeepaMehtaClient can't run";
	private boolean firstPaint = true;



	// ***************
	// *** Methods ***
	// ***************



	// ---------------------------
	// --- Application Methods ---
	// ---------------------------



	/**
	 * The main method to start the client.
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		new DeepaMehtaClient().initApplication(args);
	}

	/**
	 * Client-specific initialization.
	 * @param args The command line arguments.
	 */
	private void initApplication(String[] args ) {

	    // >>> compare to init()
		// >>> compare to DeepaMehta.initApplication()

	    try {
            // initialize environment
            this.env = EnvironmentFactory.getClientEnvironment(args);
            logger.info("Running as client only.");
            
            // find out which instance to start
            this.instanceConfig = this.env.getInstanceConfiguration();
            logger.info("Starting instance " + this.instanceConfig.getId() + "...");
            
            // create presentation service 
            this.ps = new PresentationService();
            this.ps.initialize();
            // ### Note: the client name is unknown at this point
            this.ps.createMainWindow("DeepaMehta " + CLIENT_VERSION);
            
            //	    // TODO re-implement this logic some day
            //		String host = args.length > 0 ? args[0] : SERVER_DEFAULT_HOST;	// server host
            //		int port = DEFAULT_PORT;
            //		// does host contain port number?
            //		int pos = host.indexOf(":");
            //		if (pos != -1) {
            //			port = Integer.parseInt(host.substring(pos + 1));
            //			host = host.substring(0, pos);
            //		}		
            
            
            // create application service 
            logger.debug("Connecting to host " + this.instanceConfig.getClientHost() 
            		+ " port " + this.instanceConfig.getClientPort() + " ...");
            this.ps.setService(new SocketService(this.instanceConfig.getClientHost(), 
            		this.instanceConfig.getClientPort(), this.ps));
            
            // create login GUI
            this.ps.cp.add(this.ps.createLoginGUI());
            this.ps.mainWindow.setTitle(this.ps.getClientName());
            
            
	    } catch (EnvironmentException e) {
	    	logger.error("An error occurred during initialization of the environment.", e);
        } catch (UnknownInstanceException e) {
            logger.error("Unable to load the instance specified.", e);
        } catch (DeepaMehtaException e) {
            logger.error("Uncaught DeepaMehta exception at top level.", e);
			// Note: if SocketService() throws DeepaMehtaException installationProps
			// IS initialized ### not nice
			this.ps.cp.add(this.ps.createErrorGUI(e, this.instanceConfig.getClientHost(), 
					this.instanceConfig.getClientPort()));
			this.ps.mainWindow.setTitle(this.ps.getClientName());
        } catch (IOException e) {
			// Note: if SocketService() throws IOException installationProps is
			// NOT initialized ### not nice
			logger.error("Uncaught I/O exception at top level.", e);
			this.ps.cp.add(this.ps.createErrorGUI(e, this.instanceConfig.getClientHost(), 
					this.instanceConfig.getClientPort()));
		} finally {
			// ### ps.mainWindow.pack();
			this.ps.mainWindow.setVisible(true);
			// FIXME this   should not be run if one of the first two exceptions is caught
		}
	}

}
