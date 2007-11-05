package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;

import java.awt.Container;
import java.awt.Graphics;
import java.io.IOException;

import javax.swing.JApplet;



/**
 * The DeepaMehta client.<BR>
 * The client can run as applet as well as application.
 * <P>
 * <HR>
 * Last functional change: 2.4.2004 (2.0b3-pre2)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class DeepaMehtaClient extends JApplet implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



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



	public static void main(String[] args) {
		String host = args.length > 0 ? args[0] : SERVER_DEFAULT_HOST;	// server host
		int port = DEFAULT_PORT;
		// does host contain port number?
		int pos = host.indexOf(":");
		if (pos != -1) {
			port = Integer.parseInt(host.substring(pos + 1));
			host = host.substring(0, pos);
		}
		//
		new DeepaMehtaClient().initApplication(host, port);
	}

	/**
	 * Application specific initialization.
	 */
	private void initApplication(String host, int port) {
		// >>> compare to init()
		// >>> compare to DeepaMehta.initApplication()
		try {
			// --- create presentation service ---
			this.ps = new PresentationService();
			//
			System.out.println("\n--- DeepaMehtaClient " + CLIENT_VERSION + " runs as " +
				"application on \"" + ps.hostAddress + "\" (" + ps.platform + ") ---");
			//
			ps.initialize();
			// ### Note: the client name is unknown at this point
			ps.createMainWindow("DeepaMehta " + CLIENT_VERSION);
			// --- create application service ---
			ps.setService(new SocketService(host, port, ps));	// throws DME, IO
            // --- create login GUI ---
			ps.cp.add(ps.createLoginGUI());
			ps.mainWindow.setTitle(ps.getClientName());
		} catch (DeepaMehtaException e) {
			// Note: if SocketService() throws DeepaMehtaException installationProps
			// IS initialized ### not nice
			System.out.println("*** " + errText + " (" + e.getMessage() + ")");
			ps.cp.add(ps.createErrorGUI(e, host, port));
			ps.mainWindow.setTitle(ps.getClientName());
		} catch (IOException e) {
			// Note: if SocketService() throws IOException installationProps is
			// NOT initialized ### not nice
			System.out.println("*** " + errText + " (" + e + ")");
			ps.cp.add(ps.createErrorGUI(e, host, port));
		} finally {
			// ### ps.mainWindow.pack();
			ps.mainWindow.setVisible(true);
		}
	}



	// ----------------------
	// --- Applet methods ---
	// ----------------------



	/**
	 * Applet specific initialization.
	 */
	public void init() {
		// ### compare to initApplication()
		// ### compare to DeepaMehta.init()
		// --- create presentation service ---
		this.ps = new PresentationService();
		// reporting
		System.out.println("\n--- DeepaMehtaClient " + CLIENT_VERSION + " runs as " +
			"applet on \"" + ps.hostAddress + "\" (" + ps.platform + ") ---");
		//
		ps.initialize();
		//
		Container cp = getContentPane();
		String host = getCodeBase().getHost();
		int port = DEFAULT_PORT;
		String p = getParameter("PORT");
		if (p != null) {
			port = Integer.parseInt(p);
		}
		//
		try {
			String demoMapID = getParameter("DEMO_MAP");
			ps.setDemoMap(demoMapID);
			ps.setApplet(this);
			ps.setService(new SocketService(host, port, ps));	// throws DME, IO
			if (demoMapID != null) {
				cp.add(ps.createStartDemoGUI());
			} else {
				cp.add(ps.createLoginGUI());
			}
		} catch (DeepaMehtaException e) {
			System.out.println("*** " + errText + " (" + e.getMessage() + ")");
			cp.add(ps.createErrorGUI(e, host, port));
		} catch (IOException e) {
			System.out.println("*** " + errText + " (" + e + ")");
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
