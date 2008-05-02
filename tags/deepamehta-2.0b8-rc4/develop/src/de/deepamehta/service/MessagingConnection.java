package de.deepamehta.service;

import de.deepamehta.DeepaMehtaConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;



/**
 * For every connected client the server maintains a socket connection for purpose of
 * messaging.
 * <P>
 * A <CODE>MessagingConnection</CODE> performs <I>asynchronous</I> communication
 * (push protocol). To represent the message types there are the
 * {@link #MESSAGE_TEXT MESSAGE_XXX constants} defined in
 * {@link de.deepamehta.DeepaMehtaConstants}.
 * <P>
 * Besides this connection for every connected client the server maintains 2 further
 * connections simultanously: the
 * {@link InteractionConnection} (used for <I>synchronous</I> communication) and the
 * {@link FileserverConnection} (used for background file transfers).
 * <P>
 * <HR>
 * Last functional change: 27.2.2005 (2.0b6)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class MessagingConnection implements DeepaMehtaConstants, Runnable {



	// **************
	// *** Fields ***
	// **************



	private Session session;
	private ApplicationService as;
	//
	private DataInputStream in;
	private DataOutputStream out;



	// *******************
	// *** Constructor ***
	// *******************



	MessagingConnection(DataInputStream in, DataOutputStream out) {
		this.in = in;
		this.out = out;
	}



	// ******************************************************
	// *** Implementation of Interface java.lang.Runnable ***
	// ******************************************************



	public void run() {
		int connectionState;
		try {
			while ((connectionState = executeCommand(in.read())) == STATE_OK) {
			}
		} catch (IOException e) {
			System.out.println("*** MessagingConnection.run(): " + e + " -- drop messaging connection");
			connectionState = STATE_ERROR;
		}
		// reporting
		if (connectionState == STATE_TERMINATE) {
			System.out.println("> [" + session.getUserName() + "] messaging connection dropped properly");
		} else {
			System.out.println("*** [" + session.getUserName() + "] messaging connection dropped due to failure");
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	// --- sendDirectives (2 forms) ---

	/**
	 * @see		FileserverConnection#performUpload
	 */
	void sendDirectives(CorporateDirectives directives) {
		sendDirectives(directives, null, null, null);
	}

	/**
	 * Note: a <CODE>MessagingConnection</CODE> is used by different threads concurrently
	 * (e.g. PersonalWeb's {@link de.deepamehta.topics.personalweb.FetchThread} and
	 * {@link de.deepamehta.topics.personalweb.ParseThread}) and thus must be
	 * synchronized.
	 *
	 * @see		#sendDirectives(CorporateDirectives directives)
	 * @see		DeepaMehtaServer#importTopicmap
	 * @see		DeepaMehtaServer#importCM
	 * @see		ApplicationService#broadcast
	 * @see		ApplicationService#broadcastSessions
	 * @see		ExternalConnection#sendDirectivesAsynchronously
	 * @see		de.deepamehta.topics.personalweb.PersonalWeb#revealLink
	 * @see		de.deepamehta.topics.personalweb.PersonalWeb#setWebpageTopicName
	 * @see		de.deepamehta.topics.personalweb.PersonalWeb#sendNotification
	 */
	public synchronized void sendDirectives(CorporateDirectives directives,
							ApplicationService as, String topicmapID, String viewmode) {
		try {
			out.write(MESSAGE_DIRECTIVES);
			//
			directives.updateCorporateMemory(as, session, topicmapID, viewmode);
			directives.write(out);
		} catch (IOException e) {
			System.out.println("*** MessagingConnection.sendDirectives(): " + e);
		}
	}

	// ---

	/**
	 * @see		DeepaMehtaServer#setConnection(int sessionID, MessagingConnection con)
	 */
	void setClient(Session session, ApplicationService as) {
		this.session = session;
		this.as = as;
		//
		new Thread(this).start();
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Dispatches to one of the {@link #performLogout performXXX() methods} according to
	 * the specified command.
	 *
	 * @param	command		the ID of the command to execute.
	 *
	 * @return	The connection state, see {@link #STATE_OK STATE_XXX constants}
	 *
	 * @see		#run
	 */
	private int executeCommand(int command) {
		// ### try {
		switch (command) {
		case -1:
			System.out.println("*** MessagingConnection.processCommand(): " +
				"client has gone away -- drop messaging connection");
			return STATE_ERROR;
		/* ### case MESSAGE_TEXT:
			String message = in.readUTF();
			processMessage(message);
			return STATE_OK; */
		case REQUEST_LOGOUT:
			performLogout();
			return STATE_TERMINATE;
		default:
			System.out.println("*** MessagingConnection.processCommand(): " +
				"unexpected command ID: " + command + " -- drop messaging connection");
			return STATE_ERROR;
		}
		/* ### } catch (IOException e) {
			System.out.println("*** MessagingConnection.executeCommand(): " + e +
				" -- drop messaging connection");
			return STATE_ERROR;
		} */
	}

	// ---

	/* ### private void processMessage(String message) {
		System.out.println("[" + session.getUserName() + "] MESSAGE_TEXT \"" +
			message + "\"");
		as.processMessage(message, session);
	} */

	private void performLogout() {
		System.out.println("[" + session.getUserName() + "] REQUEST_LOGOUT");
	}
}
