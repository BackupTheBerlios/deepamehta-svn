package de.deepamehta.service;

import de.deepamehta.DeepaMehtaConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;



/**
 * ### External call-interface.
 * <P>
 * ### External applications can establish a connection to the DeepaMehta server to
 * let them process directives.
 * <P>
 * <HR>
 * Last functional change: 23.9.2004 (2.0b3)<BR>
 * Last documentation update: 23.9.2004 (2.0b3)<BR>
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



	ExternalConnection(ApplicationService as, DataInputStream in, DataOutputStream out) {
		this.as = as;
		this.in = in;
		this.out = out;
		//
		new Thread(this).start();
	}



	// ******************************************************
	// *** Implementation of Interface java.lang.Runnable ***
	// ******************************************************



	public void run() {
		int connectionState;
		try {
			int dirType;
			do {
				dirType = in.read();
				connectionState = processRequest(dirType);
			} while (connectionState == STATE_OK);
		} catch (IOException e) {
			System.out.println("*** ExternalConnection.run(): " + e + " -- drop external connection");
			connectionState = STATE_ERROR;
		}
		// reporting
		if (connectionState == STATE_TERMINATE) {
			System.out.println("> [] External connection dropped properly");
		} else {
			System.out.println("*** [] External connection dropped due to failure");
		}
	}



	// ***********************
	// *** Package Private ***
	// ***********************



	/**
	 * @see		DeepaMehtaServer#broadcastTypeChange
	 */
	void sendUpdateNotification(String topicID) {
		try {
			out.write(EXTERNAL_REQUEST_REINIT);
			out.writeUTF(topicID);
		} catch (IOException e) {
			System.out.println("*** ExternalConnection.sendUpdateNotification(): " + e);
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Dispatches to one of the {@link #processShowProperties processXXX() methods}
	 * according to the specified command.
	 *
	 * @return	The connection state,
	 *						see {@link #STATE_OK STATE_XXX constants}
	 * @see		#run
	 */
	private int processRequest(int request) {
		// ### try {
		switch (request) {
		case -1:
			System.out.println("*** ExternalConnection.processRequest(): " +
				"client has gone away -- drop external connection");
			break;
		case EXTERNAL_REQUEST_LOGOUT:
			logout();
			return STATE_TERMINATE;
		default:
			System.out.println("*** ExternalConnection.processRequest(): request " +
				"type (" + request + ") -- drop external connection");
		}
		/* ### } catch (IOException e) {
			System.out.println("*** ExternalConnection.processRequest(): " + e +
				" -- drop external connection");
		} */
		return STATE_ERROR;
	}

	private void logout() {
		System.out.println("[] EXTERNAL_REQUEST_LOGOUT");
	}
}
