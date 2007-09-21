package de.deepamehta.client;

import de.deepamehta.Detail;
import de.deepamehta.Directives;

import java.util.Hashtable;



/**
 * The remote interface by which the clients access the application service.
 * <P>
 * ### Why public _is_ required?
 * <P>
 * <HR>
 * Last functional change: 27.2.2005 (2.0b6)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface ApplicationService {



	// ----------------------------------
	// --- Core Service (synchronous) ---
	// ----------------------------------



	// --- Session Management ---

	/**
	 * Tries to login with specified username and password.
	 *
	 * @return			in case of successful login client directives for building the
	 *					main GUI, <CODE>null</CODE> otherwise.
	 */
	Directives login(String username, String password);
	Directives startDemo(String demoMapID);
	void logout();

	// --- Topics / Associations ---

	Directives executeTopicCommand(String topicmapID, String viewmode, String topicID, int version, String command);
	Directives executeAssocCommand(String topicmapID, String viewmode, String assocID, int version, String command);
	//
	Directives executeChainedTopicCommand(String topicmapID, String viewmode, String topicID, int version, String command, String result);
	Directives executeChainedAssocCommand(String topicmapID, String viewmode, String assocID, int version, String command, String result);
	//
	Directives setTopicProperties(String topicmapID, String viewmode, String topicID, int version, Hashtable newData);
	Directives setAssocProperties(String topicmapID, String viewmode, String assocID, int version, Hashtable newData);
	//
	Directives processTopicDetail(String topicmapID, String viewmode, String topicID, int version, Detail detail);
	Directives processAssocDetail(String topicmapID, String viewmode, String assocID, int version, Detail detail);

	// --- Views --- 

	Directives setGeometry(String topicmapID, String viewmode, Hashtable topics);
	void setTranslation(String topicmapID, String viewmode, int tx, int ty);



	// --------------------
	// --- Type Service ---
	// --------------------



	PresentationType getTopicType(String typeID);
	PresentationType getAssociationType(String typeID);



	// -------------------------
	// --- Messaging Service ---
	// -------------------------



	// ### void sendMessage(String message);



	// -----------------------------
	// --- File Transfer Service ---
	// -----------------------------



	void downloadFile(String filename, int filetype, long lastModified);
	void uploadFile(String filename, int filetype);
	void processMessage(String message);
}
