package de.deepamehta.service;

import de.deepamehta.DeepaMehtaConstants;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;



/**
 * A server console window.<BR>
 * If the window is closed the server shutdown.<BR>
 * Existing client connections are listed in the window.
 * <P>
 * <HR>
 * Last functional change: 13.8.2002 (2.0a15)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class ServerConsole extends JFrame implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private JTextArea conArea = new JTextArea(15, 40);
	// ### private JTextArea errArea = new JTextArea(15, 40);

	private Session[] sessions;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * References checked: 13.1.2002 (2.0a14-pre6)
     *
     * @see		DeepaMehtaServer#main
     */
	ServerConsole(ApplicationService as) {
		super(as.getInstallationName());
		this.sessions = as.getSessions();
		//
		Container cp = getContentPane();
		JTabbedPane tp = new JTabbedPane();
		tp.addTab("Sessions", conArea);
		// ### tp.addTab("Errors", errArea);
		cp.add(tp);
		//
		updateSessions();
		// adds a listener to the application frame
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeApplication(e);
			}
		});
		//
		pack();
		setSize(CONSOLE_WIDTH, CONSOLE_HEIGHT);
		setVisible(true);
	}



	// ***************
	// *** Methods ***
	// ***************



	// Note: must be synchronized because sessions[] is shared by different threads
	// (InteractionConnection)
	synchronized void updateSessions() {
		StringBuffer s = new StringBuffer();
		Session session;
		int sessionCount = 0;
		for (int i = 0; i < MAX_CLIENTS; i++) {
			session = sessions[i];
			if (session != null) {
				s.append("[" + i + "] \"" + session.getHostname() + "\" (" +
					session.getAddress() + ") " + (session.loggedIn() ? "\"" +
					session.getUserName() + "\" (" + (session.isDemo() ? "Demo" :
					session.getUserID()) + ")" : "noch nicht eingelogt" /* ### "not yet logged in" */) + "\n");
				sessionCount++;
			}
		}
		/* conArea.setText((sessionCount == 0 ? "no " : sessionCount + " ") + "session" +
			(sessionCount == 1 ? "" : "s") + "\n\n" + s.toString()); ### */
		conArea.setText((sessionCount == 0 ? "Keine " : sessionCount + " ") + "Session" +
			(sessionCount == 1 ? "" : "s") + "\n\n" + s.toString());
	}



	private void closeApplication(WindowEvent e) {
		System.out.println("--- DeepaMehtaServer shutdown ---");
		System.exit(0);
	}
}
