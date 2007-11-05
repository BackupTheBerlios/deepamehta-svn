package de.deepamehta.service.web;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.TopicInitException;
import de.deepamehta.client.DeepaMehtaClient;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.topics.LiveTopic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



/**
 * A socket connection to synchronize one application service instance with another one
 * served by a DeepaMehta server.
 * <P>
 * <HR>
 * Last functional change: 16.8.2007 (2.0b8)<BR>
 * Last documentation update: 22.9.2004 (2.0b3)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
class ExternalConnection implements DeepaMehtaConstants, Runnable {



	// **************
	// *** Fields ***
	// **************



	private ApplicationService as;

	private DataInputStream in;
	private DataOutputStream out;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @param	as		the application service instance to be synchronized.
	 *
	 * @see		DeepaMehtaServlet#init
	 */
	ExternalConnection(String host, int port, ApplicationService as) throws IOException {
		Socket sock = new Socket(host, port);
		//
		System.out.println("> server side socket timeout was " + sock.getSoTimeout() + " ms -- timeout is now disabled");
		sock.setSoTimeout(0);	// disable timeout ### does it help to avoid broken connections?
		//
		this.as = as;
		this.in = new DataInputStream(sock.getInputStream());
		this.out = new DataOutputStream(sock.getOutputStream());
		//
		out.write(CONNECTION_EXTERNAL);
		//
		new Thread(this).start();
	}



	// ******************************************************
	// *** Implementation of Interface java.lang.Runnable ***
	// ******************************************************



	public synchronized void run() {
		int connectionState;
		try {
			while ((connectionState = processNotification(in.read())) == STATE_OK) {
			}
		} catch (Exception e) {
			System.out.println("*** ExternalConnection.run(): " + e + " -- drop external connection");
			connectionState = STATE_ERROR;
		}
		// reporting
		if (connectionState == STATE_TERMINATE) {
			System.out.println(">>> [] external connection dropped properly <<<");
		} else {
			System.out.println(">>> [] external connection dropped due to failure <<<");
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		DeepaMehtaClient#saveGeometry
	 */
	void logout() {
		try {
			out.write(EXTERNAL_REQUEST_LOGOUT);
		} catch (IOException e) {
			System.out.println("*** ExternalConnection.logout(): " + e);
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Reads one message from this <CODE>ExternalConnection</CODE> and processes it.
	 *
	 * @see		#run
	 */
	private int processNotification(int notificationType) throws DeepaMehtaException, IOException {
		switch (notificationType) {
		case -1:
			System.out.println("*** ExternalConnection.processNotification(): " +
				"server has gone away -- drop external connection");
			return STATE_ERROR;
		case EXTERNAL_REQUEST_REINIT:
			String topicID = in.readUTF();
			System.out.println(">>> ExternalConnection.processNotification(): topic \"" + topicID + "\" has changed");
			// reload
			BaseTopic t = as.cm.getTopic(topicID, 1);
			LiveTopic topic = as.getLiveTopic(topicID, 1);
			topic.setName(t.getName());
			topic.setType(t.getType());
			// reinit
			try {
				as.initTopic(topicID, 1);
			} catch (TopicInitException e) {
				System.out.println("*** ExternalConnection.processNotification(): EXTERNAL_REQUEST_REINIT: " + e.getMessage());
			}
			//
			return STATE_OK;
		default:
			throw new DeepaMehtaException("unexpected notification type: " + notificationType);
		}
	}
}
