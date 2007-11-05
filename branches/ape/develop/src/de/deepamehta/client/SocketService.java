package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.Directives;

import java.io.IOException;
import java.util.Hashtable;



/**
 * Proxy of the application service which communicates via TCP sockets.
 * <P>
 * <HR>
 * Last functional change: 27.2.2005 (2.0b6)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public final class SocketService implements ApplicationService, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private PresentationService ps;
	//
	private String host;
	private int port;
	private int sessionID;

	/**
	 * Initialized by constructor.
	 */
	private InteractionConnection cc;
	private FileserverConnection fileserverCon;
	private MessagingConnection messagingCon;
	private TypeConnection typeCon;

	/**
	 * Set to <CODE>true</CODE> once the latter 3 connections are created.
	 */
	private boolean connectionsCreated;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		DeepaMehtaClient#initApplication
	 * @see		DeepaMehtaClient#init
	 */
	SocketService(String host, int port, PresentationService ps)
							throws IOException, DeepaMehtaException {
		this.host = host;
		this.port = port;
		this.ps = ps;
		//
		createConnection(CONNECTION_INTERACTION);	// throws DME, IO
	}



	// ******************************************************
	// *** Implementation of interface ApplicationService ***
	// ******************************************************



	// ----------------------------------
	// --- Core Service (synchronous) ---
	// ----------------------------------



	// --- Session Management ---

	public Directives login(String username, String password) {
		try {
			Directives directives = cc.login(username, password);
			if (directives != null) {
				createConnection(CONNECTION_FILESERVER);	// throws DME, IO
				createConnection(CONNECTION_MESSAGING);		// throws DME, IO
				createConnection(CONNECTION_TYPE);			// throws DME, IO
				connectionsCreated = true;
			}
			//
			return directives;
		} catch (IOException e) {
			System.out.println("*** SocketService.login(): " + e);
			// ### ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
			return null;
		}
	}

	public Directives startDemo(String demoMapID) {
		try {
			Directives directives = cc.login(demoMapID);
			//
			createConnection(CONNECTION_FILESERVER);	// throws DME, IO
			createConnection(CONNECTION_MESSAGING);		// throws DME, IO
			createConnection(CONNECTION_TYPE);			// throws DME, IO
			connectionsCreated = true;
			//
			return directives;
		} catch (IOException e) {
			System.out.println("*** SocketService.startDemo(): " + e);
			// ### ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
			return null;	// ###
		}
	}

	/**
	 * References checked: 29.12.2001 (2.0a14-pre5)
	 *
	 * @see		PresentationService#closeApplication
	 */
	public void logout() {
		try {
			cc.logout();		// throws IO
			if (connectionsCreated) {
				fileserverCon.logout();
				messagingCon.logout();
				typeCon.logout();
			}
		} catch (IOException e) {
			System.out.println("*** SocketService.logout(): " + e);
			// ### ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
		}
	}

	// ---

	public Directives executeTopicCommand(String topicmapID, String viewmode,
										String topicID, int version, String command) {
		try {
			return cc.executeTopicCommand(topicmapID, viewmode, topicID, version, command);
		} catch (IOException e) {
			System.out.println("*** SocketService.executeTopicCommand(): " + e);
			ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
			return new Directives();	// ###
		}
	}

	public Directives executeAssocCommand(String topicmapID, String viewmode,
										String assocID, int version, String command) {
		try {
			return cc.executeAssociationCommand(topicmapID, viewmode, assocID, version, command);
		} catch (IOException e) {
			System.out.println("*** SocketService.executeAssocCommand(): " + e);
			ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
			return new Directives();	// ###
		}
	}

	// ---

	public Directives executeChainedTopicCommand(String topicmapID, String viewmode,
							String topicID, int version, String command, String result) {
		try {
			return cc.executeChainedTopicCommand(topicmapID, viewmode, topicID, version, command, result);
		} catch (IOException e) {
			System.out.println("*** SocketService.executeChainedTopicCommand(): " + e);
			ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
			return new Directives();	// ###
		}
	}

	public Directives executeChainedAssocCommand(String topicmapID, String viewmode,
							String assocID, int version, String command, String result) {
		try {
			return cc.executeChainedAssociationCommand(topicmapID, viewmode, assocID, version, command, result);
		} catch (IOException e) {
			System.out.println("*** SocketService.executeChainedAssocCommand(): " + e);
			ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
			return new Directives();	// ###
		}
	}

	// ---

	public Directives setTopicProperties(String topicmapID, String viewmode, String topicID,
														int version, Hashtable newData) {
		try {
			return cc.setTopicData(topicmapID, viewmode, topicID, version, newData);
		} catch (IOException e) {
			System.out.println("*** SocketService.setTopicProperties(): " + e);
			ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
			return new Directives();	// ###
		}	
	}

	public Directives setAssocProperties(String topicmapID, String viewmode, String assocID,
														int version, Hashtable newData) {
		try {
			return cc.setAssociationData(topicmapID, viewmode, assocID, version, newData);
		} catch (IOException e) {
			System.out.println("*** SocketService.setAssocProperties(): " + e);
			ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
			return new Directives();	// ###
		}	
	}

	// ---

	public Directives processTopicDetail(String topicmapID, String viewmode,
											String topicID, int version, Detail detail) {
		try {
			return cc.processTopicDetail(topicmapID, viewmode, topicID, version, detail);
		} catch (IOException e) {
			System.out.println("*** SocketService.processTopicDetail(): " + e);
			ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
			return new Directives();	// ###
		}	
	}

	public Directives processAssocDetail(String topicmapID, String viewmode,
											String assocID, int version, Detail detail) {
		try {
			return cc.processAssociationDetail(topicmapID, viewmode, assocID, version, detail);
		} catch (IOException e) {
			System.out.println("*** SocketService.processAssocDetail(): " + e);
			ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
			return new Directives();	// ###
		}	
	}

	// --- Views --- 

	public Directives setGeometry(String topicmapID, String viewmode, Hashtable topics) {
		try {
			return cc.setGeometry(topicmapID, viewmode, topics);
			// no result
		} catch (IOException e) {
			System.out.println("*** SocketService.setGeometry(): " + e);
			ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
			return new Directives();	// ###
		}	
	}

	public void setTranslation(String topicmapID, String viewmode, int tx, int ty) {
		try {
			cc.setTranslation(topicmapID, viewmode, tx, ty);
			// no result
		} catch (IOException e) {
			System.out.println("*** SocketService.setTranslation(): " + e);
			ps.showMessage("Communication Error (" + e.getMessage() + ")", NOTIFICATION_ERROR);
		}	
	}



	// --------------------
	// --- Type Service ---
	// --------------------



	public PresentationType getTopicType(String typeID) throws DeepaMehtaException {
		return typeCon.getTopicType(typeID);
	}

	public PresentationType getAssociationType(String typeID) throws DeepaMehtaException {
		return typeCon.getAssociationType(typeID);
	}



	// -------------------------
	// --- Messaging Service ---
	// -------------------------



	/* ### public void sendMessage(String message) {
		messagingCon.sendMessage(message);
	} */



	// -----------------------------
	// --- File Transfer Service ---
	// -----------------------------



	/**
	 * Presumption: remote file exists, ensured because an download request is not
	 * queued if the remote file doesn't exist.
	 */
	public void downloadFile(String filename, int filetype, long lastModified) {
		try {
			fileserverCon.downloadFile(filename, filetype);
			// Note: the client is resposible for setting the file modification date.
			// Compare to server.FileserverConnection.performDownload()
			ps.setLastModifiedLocally(filename, filetype, lastModified);
		} catch (Exception e) {
			System.out.println("*** SocketService.downloadFile(): " + e +
				" -- \"" + filename + "\" not downloaded");
		}
	}

	/**
	 * Presumption: local file exists, ensured because an upload request is not
	 * queued if the local file doesn't exist.
	 */
	public void uploadFile(String filename, int filetype) {
		try {	
			fileserverCon.uploadFile(filename, filetype);
		} catch (Exception e) {
			System.out.println("*** SocketService.uploadFile(): " + e +
				" -- \"" + filename + "\" not uploaded");
		}
	}

	public void processMessage(String message) {
		try {
			fileserverCon.sendMessage(message);
		} catch (Exception e) {
			System.out.println("*** SocketService.processMessage(): " + e +
				" -- message \"" + message + "\" not send");
		}
	}



	// **********************
	// *** Private Method ***
	// **********************



	/**
	 * @see		#initApplication	CONNECTION_INTERACTION
	 * @see		#init				CONNECTION_INTERACTION
	 * @see		#run		 CONNECTION_FILESERVER, CONNECTION_MESSAGING
	 */
	private void createConnection(int type) throws IOException, DeepaMehtaException {
		switch (type) {
		case CONNECTION_INTERACTION:
			// may throw a DeepaMehtaException
			cc = new InteractionConnection(host, port, REQUIRED_SERVER_VERSION, ps);
			sessionID = cc.getSessionID();
			System.out.println("> interaction connection established (session ID: " + sessionID + ")");
			break;
		case CONNECTION_FILESERVER:
			fileserverCon = new FileserverConnection(host, port, sessionID, ps.getFileServer());
			System.out.println("> fileserver connection established");
			break;
		case CONNECTION_MESSAGING:
			messagingCon = new MessagingConnection(host, port, sessionID, ps);
			System.out.println("> messaging connection established");
			break;
		case CONNECTION_TYPE:
			typeCon = new TypeConnection(host, port, sessionID, ps);
			System.out.println("> type connection established");
			break;
		default:
			throw new DeepaMehtaException("unexpected connection type: " + type);
		}
	}
}
