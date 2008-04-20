package de.deepamehta.service;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.util.DeepaMehtaUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Hashtable;



/**
 * For every connected client the server maintains a socket connection to handle user
 * interaction.
 * <p>
 * An <code>InteractionConnection</code> performs <i>synchronous</i> communication between
 * client and server (request/reply protocol). To represent the request types there are
 * the {@link InteractionConnection#REQUEST_LOGIN REQUEST_XXX constants} defined in
 * {@link de.deepamehta.DeepaMehtaConstants}.
 * <p>
 * Besides this connection for every connected client the server maintains 2 further
 * connections simultanously: the {@link MessagingConnection} (used for <i>asynchronous</i> communication)
 * and the {@link FileserverConnection} (used for background file transfers).
 * <p>
 * <hr>
 * Last functional change: 17.4.2008 (2.0b8)<br>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
final class InteractionConnection implements DeepaMehtaConstants, Runnable {



	// **************
	// *** Fields ***
	// **************



	private Session session;
	private int sessionID;

	private ApplicationService as;
	private DataInputStream in;
	private DataOutputStream out;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		DeepaMehtaServer#runServer
	 */
	InteractionConnection(int sessionID, DataInputStream in, DataOutputStream out, ApplicationService as) {
		this.sessionID = sessionID;
		this.in = in;
		this.out = out;
		this.as = as;
	}



	// ******************************************************
	// *** Implementation of interface java.lang.Runnable ***
	// ******************************************************



	public void run() {
		int connectionState;
		try {
			while ((connectionState = handleRequest(in.read())) == STATE_OK) {
				if (LOG_MEM_STAT) {
					DeepaMehtaUtils.memoryStatus();
				}
			}
		} catch (IOException e) {
			System.out.println("*** InteractionConnection.run(): " + e +
				" -- drop interaction connection");
			connectionState = STATE_ERROR;
		}
		// reporting
		if (connectionState == STATE_TERMINATE) {
			System.out.println("> [" + session.getUserName() + "] interaction connection dropped " +
				"properly");
		} else {
			System.out.println("*** [" + session.getUserName() + "] interaction connection dropped " +
				"due to failure");
		}
		//
		as.removeSession(session);
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		DeepaMehtaServer#setConnection(int sessionID, InteractionConnection con)
	 */
	void setClient(Session session) {
		this.session = session;
		//
		new Thread(this).start();
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Request Handler.
	 *
	 * @param	request		The ID of the request to handle,
	 *						see {@link #REQUEST_LOGIN REQUEST_XXX constants}
	 *
	 * @return	The connection state,
	 *						see {@link #STATE_OK STATE_XXX constants}
	 *
	 * @see		#run
	 */
	private int handleRequest(int request) {
		try {
			switch (request) {
			case -1:
				System.out.println("*** InteractionConnection.handleRequest(): " +
					"client has gone away -- drop interaction connection");
				return STATE_ERROR;
			// --- session control requests ---
			case REQUEST_LOGIN:
				login();
				return STATE_OK;
			case REQUEST_LOGOUT:
				logout();
				return STATE_TERMINATE;
			// --- topic requests ---
			case REQUEST_EXEC_TOPIC_COMMAND:
				executeTopicCommand();
				return STATE_OK;
			case REQUEST_EXEC_TOPIC_COMMAND_CHAINED:
				executeChainedTopicCommand();
				return STATE_OK;
			case REQUEST_PROCESS_TOPIC_DETAIL:
				processTopicDetail();
				return STATE_OK;
			case REQUEST_SET_TOPIC_DATA:
				setTopicData();
				return STATE_OK;
			// --- association requests ---
			case REQUEST_EXEC_ASSOC_COMMAND:
				executeAssociationCommand();
				return STATE_OK;
			case REQUEST_EXEC_ASSOC_COMMAND_CHAINED:
				executeChainedAssociationCommand();
				return STATE_OK;
			case REQUEST_PROCESS_ASSOC_DETAIL:
				processAssociationDetail();
				return STATE_OK;
			case REQUEST_SET_ASSOC_DATA:
				setAssocData();
				return STATE_OK;
			// --- view requests ---
			case REQUEST_SET_GEOMETRY:
				setGeometry();
				return STATE_OK;
			case REQUEST_SET_TRANSLATION:
				setTranslation();
				return STATE_OK;
			default:
				System.out.println("*** InteractionConnection.handleRequest(): " +
					"unexpected request type: " + request + " -- drop interaction " +
					"connection");
				return STATE_ERROR;
			}
		} catch (IOException e) {
			System.out.println("*** error while processing a request of type " +
				request + ": " + e + " -- drop interaction connection");
			e.printStackTrace();
			return STATE_ERROR;
		} catch (DeepaMehtaException e) {
			System.out.println("*** error while processing a request of type " +
				request + " -- drop interaction connection");
			e.printStackTrace();
			return STATE_ERROR;
		}
	}



	// --------------------------------
	// --- Session Control Requests ---
	// --------------------------------



	private void login() throws IOException {
		CorporateDirectives directives = new CorporateDirectives();
		//
		boolean success;
		int loginMode = in.read();
		switch (loginMode) {
		case LOGIN_USER:
			String username = in.readUTF();
			String password = in.readUTF();
			BaseTopic userTopic = as.tryLogin(username, password, session);
			if (userTopic == null) {
				// login failed
				out.write(0);
				success = false;
			} else {
				// login successfull
				out.write(1);
				as.startSession(userTopic, session, directives);
				success = true;
			}
			break;
		case LOGIN_DEMO:
			String demoMapID = in.readUTF();
			as.startDemo(demoMapID, session, directives);
			success = true;
			break;
		default:
			throw new DeepaMehtaException("unexpected login mode: " + loginMode);
		}
		// --- send directives ---
		if (success) {
			directives.updateCorporateMemory(as, session, null, null);
			directives.write(out);
		}
	}

	// ---

	private void logout() throws IOException {
		log("REQUEST_LOGOUT");
	}



	// ------------------------------------
	// --- Topic / Association Requests ---
	// ------------------------------------



	private void executeTopicCommand() throws IOException {
		// read request parameter
		String topicmapID = in.readUTF();
		String viewMode = in.readUTF();
		String topicID = in.readUTF();
		int version = in.readInt();
		String command = in.readUTF();
		// log request
		log("REQUEST_EXEC_TOPIC_COMMAND " + topicID + ":" + version + " \"" + command + "\"");
		// --- handle request ---
		CorporateDirectives directives;
		directives = as.executeTopicCommand(topicID, version, command,
			topicmapID, viewMode, session);
		directives.updateCorporateMemory(as, session, topicmapID, viewMode);
		directives.write(out);
	}

	private void executeAssociationCommand() throws IOException {
		// read request parameter
		String topicmapID = in.readUTF();
		String viewMode = in.readUTF();
		String assocID = in.readUTF();
		int version = in.readInt();
		String command = in.readUTF();
		// log request
		log("REQUEST_EXEC_ASSOC_COMMAND " + assocID + ":" + version + " \"" + command + "\"");
		// --- handle request ---
		CorporateDirectives directives;
		directives = as.executeAssociationCommand(assocID, version, command,
			topicmapID, viewMode, session);
		directives.updateCorporateMemory(as, session, topicmapID, viewMode);
		directives.write(out);
	}

	// ---

	private void executeChainedTopicCommand() throws IOException {
		// read request parameter
		String topicmapID = in.readUTF();
		String viewMode = in.readUTF();
		String topicID = in.readUTF();
		int version = in.readInt();
		String command = in.readUTF();
		String result = in.readUTF();
		// log request
		log("REQUEST_EXEC_TOPIC_COMMAND_CHAINED " + topicID + ":" + version + " \"" + command + "\"");
		// --- handle request ---
		CorporateDirectives directives = as.executeChainedTopicCommand(topicID,
			version, command, result, topicmapID, viewMode, session);
		directives.updateCorporateMemory(as, session, topicmapID, viewMode);
		directives.write(out);
	}

	private void executeChainedAssociationCommand() throws IOException {
		// read request parameter
		String topicmapID = in.readUTF();
		String viewMode = in.readUTF();
		String assocID = in.readUTF();
		int version = in.readInt();
		String command = in.readUTF();
		String result = in.readUTF();
		// --- log request ---
		log("REQUEST_EXEC_ASSOC_COMMAND_CHAINED " + assocID + ":" + version + " \"" + command + "\"");
		// --- handle request ---
		CorporateDirectives directives = as.executeChainedAssociationCommand(assocID,
			version, command, result, topicmapID, viewMode, session);
		directives.updateCorporateMemory(as, session, topicmapID, viewMode);
		directives.write(out);
	}

	// ---

	private void processTopicDetail() throws IOException {
		// read request parameter
		String topicmapID = in.readUTF();
		String viewmode = in.readUTF();
		String topicID = in.readUTF();
		int version = in.readInt();
		CorporateDetail detail = new CorporateDetail(in, as);
		// --- log request ---
		log("REQUEST_PROCESS_TOPIC_DETAIL " + topicID + ":" + version + " \"" + detail.getCommand() + "\"");
		// --- handle request ---
		CorporateDirectives directives = as.processTopicDetail(topicID, version,
			detail, session, topicmapID, viewmode);
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		directives.write(out);
	}

	private void processAssociationDetail() throws IOException {
		// read request parameter
		String topicmapID = in.readUTF();
		String viewmode = in.readUTF();
		String assocID = in.readUTF();
		int version = in.readInt();
		CorporateDetail detail = new CorporateDetail(in, as);
		// --- log request ---
		log("REQUEST_PROCESS_ASSOC_DETAIL " + assocID + ":" + version + " \"" + detail.getCommand() + "\"");
		// --- handle request ---
		CorporateDirectives directives = as.processAssociationDetail(assocID, version,
			detail, session, topicmapID, viewmode);
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		directives.write(out);
	}

	// ---

	private void setTopicData() throws IOException, DeepaMehtaException {
		// read request parameter
		String topicmapID = in.readUTF();
		String viewmode = in.readUTF();
		String topicID = in.readUTF();
		int version = in.readInt();
		Hashtable newData = DeepaMehtaUtils.readHashtable(in);
		// --- log request ---
		log("REQUEST_SET_TOPIC_DATA \"" + topicID + ":" + version + "\"");
		// --- handle request ---
		CorporateDirectives directives = as.setTopicProperties(topicID, version, newData, topicmapID, session);
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		directives.write(out);
	}

	private void setAssocData() throws IOException {
		// read request parameter
		String topicmapID = in.readUTF();
		String viewmode = in.readUTF();
		String assocID = in.readUTF();
		int version = in.readInt();
		Hashtable newData = DeepaMehtaUtils.readHashtable(in);
		// --- log request ---
		log("REQUEST_SET_ASSOC_DATA \"" + assocID + ":" + version + "\"");
		// --- handle request ---
		CorporateDirectives directives = as.setAssocProperties(assocID, version, newData,
			topicmapID, viewmode, session);
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		directives.write(out);
	}



	// ---------------------
	// --- View Requests ---
	// ---------------------



	private void setGeometry() throws IOException {
		// >>> compare to InteractionConnection.setGeometry()
		// read request parameter
		String topicmapID = in.readUTF();
		String viewmode = in.readUTF();
		int topicCount = in.readInt();
		// --- log request ---
		log("REQUEST_SET_GEOMETRY \"" + topicmapID + ":" + viewmode + "\" (" + topicCount + " topics)");
		// --- handle request ---
		CorporateDirectives directives = new CorporateDirectives();
		String topicID;
		int x, y;
		for (int topic = 0; topic < topicCount; topic++) {
			topicID = in.readUTF();
			x = in.readInt();
			y = in.readInt();
			directives.add(as.moveTopic(topicmapID, 1, topicID, x, y, true, session));	// triggerMovedHook=true
		}
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		directives.write(out);
	}

	private void setTranslation() throws IOException {
		// read request parameter
		String topicmapID = in.readUTF();
		String viewmode = in.readUTF();
		int tx = in.readInt();
		int ty = in.readInt();
		// --- log request ---
		log("REQUEST_SET_TRANSLATION \"" + topicmapID + ":" + viewmode + "\" (" + tx + ", " + ty + ")");
		// --- handle request ---
		as.updateViewTranslation(topicmapID, 1, viewmode, tx, ty);
		// Note: there is no result
	}

	// ---

	// ### copied
	private void log(String text) {
		if (LOG_REQUESTS) {
			System.out.println(DeepaMehtaUtils.getDate() + " " + DeepaMehtaUtils.getTime(true) + " [" +
				session.getUserName() + "] " + text);	// withSecs=true
		}
	}
}
