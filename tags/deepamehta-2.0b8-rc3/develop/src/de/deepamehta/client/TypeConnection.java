package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;



/**
 * <P>
 * <HR>
 * Last functional change: 16.8.2007 (2.0b8)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
class TypeConnection implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private DataInputStream in;
	private DataOutputStream out;
	private PresentationService ps;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		DeepaMehtaClient#createLoginGUI
	 */
	TypeConnection(String host, int port, int sessionID, PresentationService ps)
																throws IOException {
		Socket sock = new Socket(host, port);
		//
		System.out.println("> client side socket timeout was " + sock.getSoTimeout() + " ms -- timeout is now disabled");
		sock.setSoTimeout(0);	// disable timeout ### does it help to avoid broken connections?
		//
		this.in = new DataInputStream(sock.getInputStream());
		this.out = new DataOutputStream(sock.getOutputStream());
		this.ps = ps;
		//
		out.write(CONNECTION_TYPE);
		out.writeInt(sessionID);
	}



	// ***************
	// *** Methods ***
	// ***************



	PresentationType getTopicType(String typeID) throws DeepaMehtaException {
		try {
			if (LOG_TYPES) {
				System.out.print(">>> requesting topic type \"" + typeID + "\" ... ");
			}
			//
			out.write(TYPE_REQUEST_TOPIC_TYPE);
			out.writeUTF(typeID);
			PresentationType type = new PresentationType(in, ps);
			//
			if (LOG_TYPES) {
				System.out.println("\"" + type.getName() + "\"");
			}
			//
			return type;
		} catch (IOException e) {
			throw new DeepaMehtaException("I/O error: " + e);
		}
	}

	PresentationType getAssociationType(String typeID) throws DeepaMehtaException {
		try {
			if (LOG_TYPES) {
				System.out.print(">>> requesting association type \"" + typeID + "\" ... ");
			}
			//
			out.write(TYPE_REQUEST_ASSOC_TYPE);
			out.writeUTF(typeID);
			PresentationType type = new PresentationType(in, ps);
			//
			if (LOG_TYPES) {
				System.out.println("\"" + type.getName() + "\"");
			}
			//
			return type;
		} catch (IOException e) {
			throw new DeepaMehtaException("I/O error: " + e);
		}
	}

	// ---

	/**
	 * @see		DeepaMehtaClient#saveGeometry
	 */
	void logout() {
		try {
			out.write(TYPE_REQUEST_LOGOUT);
		} catch (IOException e) {
			System.out.println("*** TypeConnection.logout(): " + e);
		}
	}
}
