package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.util.DeepaMehtaUtils;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;



/**
 * A socket connection to the server used to handle user interaction.
 * <P>
 * An <CODE>InteractionConnection</CODE> is used for <I>synchronous</I> communication
 * between client and server (request/reply protocol). To represent the request types
 * there are the {@link InteractionConnection#REQUEST_LOGIN REQUEST_XXX constants}
 * defined in {@link de.deepamehta.DeepaMehtaConstants}.
 * <P>
 * Besides this connection the client deployes 2 further connections to the server
 * simultanously: the {@link MessagingConnection} (used for <I>asynchronous</I>
 * communication) and the {@link FileserverConnection} (used for background file
 * transfers).
 * <P>
 * <HR>
 * Last functional change: 16.8.2007 (2.0b8)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
class InteractionConnection implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private PresentationService ps;
	//
	private int sessionID;
			DataInputStream in;		// ### should be private
			DataOutputStream out;	// ### should be private



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * ### Throws DeepaMehtaException ...
	 *
	 * @see		SocketService#createConnection
	 */
	InteractionConnection(String host, int port, String requiredServerVersion,
												PresentationService ps)
												throws DeepaMehtaException, IOException {
		Socket sock = new Socket(host, port);	// may throw java.net.ConnectException
		//
		System.out.println("> client side socket timeout was " + sock.getSoTimeout() + " ms -- timeout is now disabled");
		sock.setSoTimeout(0);	// disable timeout ### does it help to avoid broken connections?
		//
		this.ps = ps;
		this.in = new DataInputStream(sock.getInputStream());
		this.out = new DataOutputStream(sock.getOutputStream());
		//
		out.write(CONNECTION_INTERACTION);
		//
		this.sessionID = in.readInt();
		String serverVersion = in.readUTF();
		ps.installationProps = DeepaMehtaUtils.readHashtable(in);
		//
		System.out.println("> session ID: " + sessionID);
		System.out.println("> server version: " + serverVersion);
		System.out.println("> installation properties: " + ps.installationProps);
		// error check 1
		if (sessionID == -1) {
			if (host.equals("") || host.equals("localhost")) {
				throw new DeepaMehtaException("There are already " + MAX_CLIENTS +
					" clients connected");
			} else {
				throw new DeepaMehtaException("There are already " + MAX_CLIENTS +
					" clients connected to " + host + ":" + port);
			}
		}
		// error check 2
		if (!serverVersion.equals(requiredServerVersion)) {
			if (host.equals("") || host.equals("localhost")) {
				throw new DeepaMehtaException("DeepaMehtaServer " + serverVersion +
					" is running, but this client requires " + requiredServerVersion);
			} else {
				throw new DeepaMehtaException(host + ":" + port + " runs " +
					"DeepaMehtaServer " + serverVersion + ", but this client " +
					"requires " + requiredServerVersion);
			}
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		PresentationService#createLoginGUI
	 */
	int getSessionID() {
		return sessionID;
	}

	// --- login (2 forms) ---

	/**
	 * Tries to login with specified username and password.
	 * <p>
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @return	### <CODE>true</CODE> if login is successfull, <CODE>false</CODE> if login
	 *			failed
	 *
	 * @see		SocketService#login
	 */
	PresentationDirectives login(String username, String password) throws IOException {
		out.write(REQUEST_LOGIN);
		out.write(LOGIN_USER);
		out.writeUTF(username);
		out.writeUTF(password);
		int success = in.read();
		if (success == 0) {
			return null;
		} else if (success == 1) {
			return new PresentationDirectives(in, ps);
		} else {
			throw new DeepaMehtaException("unexpected server response while login: " + success);
		}
	}

	/**
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @see		SocketService#startDemo
	 */
	PresentationDirectives login(String demoMapID) throws IOException {
		out.write(REQUEST_LOGIN);
		out.write(LOGIN_DEMO);
		out.writeUTF(demoMapID);
		//
		return new PresentationDirectives(in, ps);
	}

	// ---

	/**
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @see		SocketService#logout
	 */
	void logout() throws IOException {
		out.write(REQUEST_LOGOUT);
	}

	// ---

	/**
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @see		SocketService#setTopicProperties
	 */
	PresentationDirectives setTopicData(String topicmapID, String viewmode, String topicID,
											int version, Hashtable newData) throws IOException {
		// write request
		out.write(REQUEST_SET_TOPIC_DATA);
		out.writeUTF(topicmapID);
		out.writeUTF(viewmode);
		out.writeUTF(topicID);
		out.writeInt(version);
		DeepaMehtaUtils.writeHashtable(newData, out);
		// read reply
		return new PresentationDirectives(in, ps);
	}

	/**
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @see		SocketService#setAssocProperties
	 */
	PresentationDirectives setAssociationData(String topicmapID, String viewmode, String assocID,
											int version, Hashtable newData) throws IOException {
		// write request
		out.write(REQUEST_SET_ASSOC_DATA);
		out.writeUTF(topicmapID);
		out.writeUTF(viewmode);
		out.writeUTF(assocID);
		out.writeInt(version);
		DeepaMehtaUtils.writeHashtable(newData, out);
		// read reply
		return new PresentationDirectives(in, ps);
	}

	// ---

	/** 
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @see		SocketService#executeTopicCommand
	 */
	PresentationDirectives executeTopicCommand(String topicmapID, String viewmode,
								String topicID, int version, String command) throws IOException {
		// write request
		out.write(REQUEST_EXEC_TOPIC_COMMAND);
		out.writeUTF(topicmapID);
		out.writeUTF(viewmode);
		out.writeUTF(topicID);
		out.writeInt(version);
		out.writeUTF(command);
		// read reply
		return new PresentationDirectives(in, ps);
	}

	/** 
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @see		SocketService#executeAssocCommand
	 */
	PresentationDirectives executeAssociationCommand(String topicmapID, String viewmode,
								String assocID, int version, String command) throws IOException {
		// write request
		out.write(REQUEST_EXEC_ASSOC_COMMAND);
		out.writeUTF(topicmapID);
		out.writeUTF(viewmode);
		out.writeUTF(assocID);
		out.writeInt(version);
		out.writeUTF(command);
		// read reply
		return new PresentationDirectives(in, ps);
	}

	// ---

	/** 
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @see		SocketService#executeChainedTopicCommand
	 */
	PresentationDirectives executeChainedTopicCommand(String topicmapID,
										String viewmode, String topicID, int version,
										String command, String result) throws IOException {
		// write request
		out.write(REQUEST_EXEC_TOPIC_COMMAND_CHAINED);
		out.writeUTF(topicmapID);
		out.writeUTF(viewmode);
		out.writeUTF(topicID);
		out.writeInt(version);
		out.writeUTF(command);
		out.writeUTF(result);
		// read reply
		return new PresentationDirectives(in, ps);
	}

	/** 
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @see		SocketService#executeChainedAssocCommand
	 */
	PresentationDirectives executeChainedAssociationCommand(String topicmapID,
											String viewmode, String assocID, int version,
											String command, String result) throws IOException {
		// write request
		out.write(REQUEST_EXEC_ASSOC_COMMAND_CHAINED);
		out.writeUTF(topicmapID);
		out.writeUTF(viewmode);
		out.writeUTF(assocID);
		out.writeInt(version);
		out.writeUTF(command);
		out.writeUTF(result);
		// read reply
		return new PresentationDirectives(in, ps);
	}

	// ---

	/**
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @see		SocketService#processTopicDetail
	 */
	PresentationDirectives processTopicDetail(String topicmapID, String viewmode,
								String topicID, int version, Detail detail) throws IOException {
		// write request
		out.write(REQUEST_PROCESS_TOPIC_DETAIL);
		out.writeUTF(topicmapID);
		out.writeUTF(viewmode);
		out.writeUTF(topicID);
		out.writeInt(version);
		detail.write(out);
		// read reply
		return new PresentationDirectives(in, ps);
	}

	/**
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @see		SocketService#processAssocDetail
	 */
	PresentationDirectives processAssociationDetail(String topicmapID, String viewmode,
								String assocID, int version, Detail detail) throws IOException {
		// write request
		out.write(REQUEST_PROCESS_ASSOC_DETAIL);
		out.writeUTF(topicmapID);
		out.writeUTF(viewmode);
		out.writeUTF(assocID);
		out.writeInt(version);
		detail.write(out);
		// read reply
		return new PresentationDirectives(in, ps);
	}

	// ---

	/**
	 * References checked: 14.11.2004 (2.0b3)
	 *
	 * @see		SocketService#setGeometry
	 */
	PresentationDirectives setGeometry(String topicmapID, String viewmode, Hashtable topics) throws IOException {
		// write request
		out.write(REQUEST_SET_GEOMETRY);
		out.writeUTF(topicmapID);
		out.writeUTF(viewmode);
		out.writeInt(topics.size());
		Enumeration e = topics.elements();
		PresentationTopic topic;
		Point p;
		while (e.hasMoreElements()) {
			topic = (PresentationTopic) e.nextElement();
			p = topic.getGeometry();
			out.writeUTF(topic.getID());
			out.writeInt(p.x);
			out.writeInt(p.y);
		}
		// read reply
		return new PresentationDirectives(in, ps);
	}

	/**
	 * References checked: 20.10.2003 (2.0b2)
	 *
	 * @see		SocketService#setTranslation
	 */
	void setTranslation(String topicmapID, String viewmode, int tx, int ty) throws IOException {
		// write request
		out.write(REQUEST_SET_TRANSLATION);
		out.writeUTF(topicmapID);
		out.writeUTF(viewmode);
		out.writeInt(tx);
		out.writeInt(ty);
		// no reply
	}
}
