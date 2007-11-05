package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



/**
 * A socket connection to the server used to handle server side messaging.
 * <P>
 * A <CODE>MessagingConnection</CODE> is used for <I>asynchronous</I> communication
 * between server and client (push protocol). To represent the message types there are
 * the {@link #MESSAGE_TEXT MESSAGE_XXX constants} defined
 * in {@link de.deepamehta.DeepaMehtaConstants}.
 * <P>
 * Besides this connection the client deployes 2 further connections to the server
 * simultanously: the {@link InteractionConnection} (used for <I>synchronous</I> communication)
 * and the {@link FileserverConnection} (used for background file transfers).
 * <P>
 * <HR>
 * Last functional change: 16.8.2007 (2.0b8)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
class MessagingConnection implements DeepaMehtaConstants, Runnable {



	// **************
	// *** Fields ***
	// **************



	private DataInputStream in;
	private DataOutputStream out;
	private PresentationService ps;
	// needed for processing messaged (processDirectives())



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		DeepaMehtaClient#createLoginGUI
	 */
	MessagingConnection(String host, int port, int sessionID, PresentationService ps) throws IOException {
		Socket sock = new Socket(host, port);
		//
		System.out.println("> client side socket timeout was " + sock.getSoTimeout() + " ms -- timeout is now disabled");
		sock.setSoTimeout(0);	// disable timeout ### does it help to avoid broken connections?
		//
		this.in = new DataInputStream(sock.getInputStream());
		this.out = new DataOutputStream(sock.getOutputStream());
		this.ps = ps;
		//
		out.write(CONNECTION_MESSAGING);
		out.writeInt(sessionID);
		//
		new Thread(this).start();
	}



	// ******************************************************
	// *** Implementation of Interface java.lang.Runnable ***
	// ******************************************************



	public synchronized void run() {
		int connectionState;
		try {
			while ((connectionState = processMessage(in.read())) == STATE_OK) {
			}
		} catch (Exception e) {
			System.out.println("*** MessagingConnection.run(): " + e + " -- drop " +
				"messaging connection");
			connectionState = STATE_ERROR;
		}
		// reporting
		if (connectionState == STATE_TERMINATE) {
			System.out.println(">>> [] messaging connection dropped properly <<<");
		} else {
			System.out.println(">>> [] messaging connection dropped due to failure <<<");
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		TopicMapEditorControler#checkTopicType
	 * @see		TopicMapEditorControler#checkAssociationType
	 * @see		FileserverConnection#performRequest
	 */
	/* ### void sendMessage(String message) {
		try {
			out.write(MESSAGE_TEXT);
			out.writeUTF(message);
		} catch (IOException e) {
			System.out.println("*** MessagingConnection.sendMessage(): " + e +
				" -- message \"" + message + "\" not send");
		}
	} */

	// ---

	/**
	 * @see		DeepaMehtaClient#createLoginGUI
	 */
	/* ### void setClient(PresentationService ps) {
		this.ps = ps;
	} */

	/**
	 * @see		DeepaMehtaClient#saveGeometry
	 */
	void logout() {
		try {
			out.write(REQUEST_LOGOUT);
		} catch (IOException e) {
			System.out.println("*** MessagingConnection.logout(): " + e);
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Reads one message from this <CODE>MessagingConnection</CODE> and processes it.
	 *
	 * @see		#run
	 */
	private int processMessage(int messageType) throws DeepaMehtaException {
		switch (messageType) {
		case -1:
			System.out.println("*** MessagingConnection.processMessage(): " +
				"server has gone away -- drop messaging connection");
			return STATE_ERROR;
		case MESSAGE_DIRECTIVES:
			ps.processDirectives(new PresentationDirectives(in, ps));		// throws DME
			return STATE_OK;
		default:
			throw new DeepaMehtaException("unexpected message type: " + messageType);
		}
	}
}
