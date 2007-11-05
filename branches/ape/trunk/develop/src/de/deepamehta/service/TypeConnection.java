package de.deepamehta.service;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.PresentableType;
import de.deepamehta.util.DeepaMehtaUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;



/**
 * <P>
 * <HR>
 * Last functional change: 16.4.2003 (2.0a18-pre10)<BR>
 * Last documentation update: 2.12.2001 (2.0a14-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
class TypeConnection implements DeepaMehtaConstants, Runnable {



	// **************
	// *** Fields ***
	// **************



	private DataInputStream in;
	private DataOutputStream out;

	private Session session;
	private ApplicationService as;



	// *******************
	// *** Constructor ***
	// *******************



	TypeConnection(DataInputStream in, DataOutputStream out) {
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
			System.out.println("*** TypeConnection.run(): " + e + " -- drop type connection");
			connectionState = STATE_ERROR;
		}
		// reporting
		if (connectionState == STATE_TERMINATE) {
			System.out.println("> [" + session.getUserName() + "] type connection dropped properly");
		} else {
			System.out.println("*** [" + session.getUserName() + "] type connection dropped due to failure");
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		DeepaMehtaServer#setConnection(int sessionID, FileserverConnection con)
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
	 * Dispatches to one of the {@link #performUpload performXXX() methods} according to
	 * the specified command.
	 *
	 * @param	command		The ID of the command to execute,
	 *						see {@link #TYPE_REQUEST_TOPIC_TYPE TYPE_REQUEST_XXX constants}
	 * @return	The connection state,
	 *						see {@link #STATE_OK STATE_XXX constants}
	 * @see		#run
	 */
	private int executeCommand(int command) {
		try {
			switch (command) {
			case -1:
				System.out.println("*** TypeConnection.executeCommand(): " +
					"client has gone away -- drop type connection");
				return STATE_ERROR;
			case TYPE_REQUEST_TOPIC_TYPE:
				performTopicType();
				return STATE_OK;
			case TYPE_REQUEST_ASSOC_TYPE:
				performAssociationType();
				return STATE_OK;
			case TYPE_REQUEST_LOGOUT:
				performLogout();
				return STATE_TERMINATE;
			default:
				System.out.println("*** TypeConnection.executeCommand(): " +
					"unexpected command ID: " + command + " -- drop type connection");
				return STATE_ERROR;
			}
		} catch (IOException e) {
			System.out.println("*** TypeConnection.executeCommand(): " + e +
				" -- drop type connection");
			return STATE_ERROR;
		}
	}

	// ---

	private void performTopicType() throws IOException {
		// read request parameter
		String typeID = in.readUTF();
		// log request
		log("TYPE_REQUEST_TOPIC_TYPE \"" + typeID + "\"");
		// send type ### version
		PresentableType type = new PresentableType(as.type(typeID, 1));
		as.initTypeTopic(type, false, null);
		type.write(out);
	}

	private void performAssociationType() throws IOException {
		// read request parameter
		String typeID = in.readUTF();
		// log request
		log("TYPE_REQUEST_ASSOC_TYPE \"" + typeID + "\"");
		// send type ### version
		PresentableType type = new PresentableType(as.type(typeID, 1));
		as.initTypeTopic(type, false, null);
		type.write(out);
	}

	private void performLogout() {
		log("TYPE_REQUEST_LOGOUT");
	}

	// ---

	// ### copied
	private void log(String text) {
		if (LOG_REQUESTS) {
			System.out.println(DeepaMehtaUtils.getDate() + " " + DeepaMehtaUtils.getTime(true) + " [" +
				session.getUserName() + "] " + text);	// withSecs=true
				// ### session might be null?
		}
	}
}
