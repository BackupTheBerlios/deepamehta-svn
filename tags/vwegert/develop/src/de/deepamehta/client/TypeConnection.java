package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
//
import java.net.*;
import java.io.*;



/**
 * <P>
 * <HR>
 * Last functional change: 27.10.2002 (2.0a17-pre1)<BR>
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
