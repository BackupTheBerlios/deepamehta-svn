package de.deepamehta.service;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.Directives;
import de.deepamehta.PresentableType;
import de.deepamehta.client.PresentationDirectives;
import de.deepamehta.client.PresentationService;
import de.deepamehta.client.PresentationTopic;
import de.deepamehta.client.PresentationTopicMap;
import de.deepamehta.client.PresentationType;

import java.awt.Point;
import java.util.Enumeration;
import java.util.Hashtable;



/**
 * Proxy of the application service which communicates via direct method calls.
 * Presentation service and application service are running inside the same VM.
 * <p>
 * <hr>
 * Last functional change: 17.4.2008 (2.0b8)<br>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public final class EmbeddedService implements de.deepamehta.client.ApplicationService, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	Session session;
	ApplicationService as;
	PresentationService ps;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		DeepaMehta#initApplication
	 * @see		DeepaMehta#init
	 */
	EmbeddedService(Session session, ApplicationService as, PresentationService ps) {
		this.session = session;
		this.as = as;
		this.ps = ps;
	}



	// ******************************************************
	// *** Implementation of interface ApplicationService ***
	// ******************************************************



	// ----------------------------------
	// --- Core Service (synchronous) ---
	// ----------------------------------



	// --- Session Management ---

	public Directives login(String username, String password) {
		// >>> compare to service.InteractionConnection.login()
		CorporateDirectives directives = new CorporateDirectives();
		//
		BaseTopic userTopic = as.tryLogin(username, password, session);
		if (userTopic == null) {
			// login failed
			return null;
		} else {
			// login successfull
			as.startSession(userTopic, session, directives);
			//
			directives.updateCorporateMemory(as, session, null, null);
			return new PresentationDirectives(directives, ps);
		}
	}

	public Directives startDemo(String demoMapID) {
		// ### embedded service can't run as demo for now
		return null;
	}

	public void logout() {
		as.shutdown();
		// >>> compare to service.InteractionConnection.logout()
		if (LOG_REQUESTS) {
			System.out.println("[" + session.getUserName() + "] REQUEST_LOGOUT");
		}
		System.out.println("--- application service stopped ---");
	}

	// ---  ---

	/**
	 * @see		PresentationTopicMap#processTopicCommand
	 */
	public Directives executeTopicCommand(String topicmapID, String viewmode,
										String topicID, int version, String command) {
		// --- log request ---
		if (LOG_REQUESTS) {
			System.out.println("[" + session.getUserName() + "] " +
				"REQUEST_EXEC_TOPIC_COMMAND " + topicID + ":" + version + " \"" +
				command + "\"");
		}
		// --- handle request ---
		CorporateDirectives directives = as.executeTopicCommand(topicID, version,
			command, topicmapID, viewmode, session);
		//
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		return new PresentationDirectives(directives, ps);
	}

	public Directives executeAssocCommand(String topicmapID, String viewmode,
										String assocID, int version, String command) {
		// --- log request ---
		if (LOG_REQUESTS) {
			System.out.println("[" + session.getUserName() + "] " +
				"REQUEST_EXEC_ASSOC_COMMAND " + assocID + ":" + version + " \"" +
				command + "\"");
		}
		// --- handle request ---
		CorporateDirectives directives = as.executeAssociationCommand(assocID, version,
			command, topicmapID, viewmode, session);
		//
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		return new PresentationDirectives(directives, ps);
	}

	// ---

	/**
	 * @see		PresentationTopicMap#processTopicCommand
	 */
	public Directives executeChainedTopicCommand(String topicmapID, String viewmode,
							String topicID, int version, String command, String result) {
		// --- log request ---
		if (LOG_REQUESTS) {
			System.out.println("[" + session.getUserName() + "] " +
				"REQUEST_EXEC_TOPIC_COMMAND_CHAINED " + topicID + ":" + version + " \"" +
				command + "\", result \"" + result + "\"");
		}
		// --- handle request ---
		CorporateDirectives directives = as.executeChainedTopicCommand(topicID,
			version, command, result, topicmapID, viewmode, session);
		//
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		return new PresentationDirectives(directives, ps);
	}

	public Directives executeChainedAssocCommand(String topicmapID, String viewmode,
							String assocID, int version, String command, String result) {
		// --- log request ---
		if (LOG_REQUESTS) {
			System.out.println("[" + session.getUserName() + "] " +
				"REQUEST_EXEC_ASSOC_COMMAND_CHAINED " + assocID + ":" + version + " \"" +
				command + "\", result \"" + result + "\"");
		}
		// --- handle request ---
		CorporateDirectives directives = as.executeChainedAssociationCommand(assocID,
			version, command, result, topicmapID, viewmode, session);
		//
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		return new PresentationDirectives(directives, ps);
	}

	// ---

	public Directives setTopicProperties(String topicmapID, String viewmode,
												String topicID, int version, Hashtable props) {
		// --- log request ---
		if (LOG_REQUESTS) {
			System.out.println("[" + session.getUserName() + "] " +
				"REQUEST_SET_TOPIC_DATA \"" + topicID + ":" + version + "\"");
		}
		// --- handle request ---
		CorporateDirectives directives = as.setTopicProperties(topicID, version, props, topicmapID, session);
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		return new PresentationDirectives(directives, ps);
	}

	public Directives setAssocProperties(String topicmapID, String viewmode,
												String assocID, int version, Hashtable props) {
		// --- log request ---
		if (LOG_REQUESTS) {
			System.out.println("[" + session.getUserName() + "] " +
				"REQUEST_SET_ASSOC_DATA \"" + assocID + ":" + version + "\"");
		}
		// --- handle request ---
		CorporateDirectives directives = as.setAssocProperties(assocID, version, props,
			topicmapID, viewmode, session);
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		return new PresentationDirectives(directives, ps);
	}

	// ---

	public Directives processTopicDetail(String topicmapID, String viewmode,
											String topicID, int version, Detail detail) {
		// >>> compare to service.InteractionConnection.processTopicDetail()
		// --- log request ---
		if (LOG_REQUESTS) {
			System.out.println("[" + session.getUserName() + "] REQUEST_PROCESS_TOPIC_DETAIL " +
				topicID + ":" + version + " \"" + detail.getCommand() + "\"");
		}
		// --- handle request ---
		CorporateDirectives directives = as.processTopicDetail(topicID, version,
			new CorporateDetail(detail, as), session, topicmapID, viewmode);
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		return new PresentationDirectives(directives, ps);
	}

	public Directives processAssocDetail(String topicmapID, String viewmode,
											String assocID, int version, Detail detail) {
		// >>> compare to service.InteractionConnection.processAssociationDetail()
		// --- log request ---
		if (LOG_REQUESTS) {
			System.out.println("[" + session.getUserName() + "] REQUEST_PROCESS_ASSOC_DETAIL " +
				assocID + ":" + version + " \"" + detail.getCommand() + "\"");
		}
		// --- handle request ---
		CorporateDirectives directives = as.processAssociationDetail(assocID, version,
			new CorporateDetail(detail, as), session, topicmapID, viewmode);
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		return new PresentationDirectives(directives, ps);
	}

	// --- Views --- 

	public Directives setGeometry(String topicmapID, String viewmode, Hashtable topics) {
		// >>> compare to InteractionConnection.setGeometry()
		// --- log request ---
		if (LOG_REQUESTS) {
			System.out.println("[" + session.getUserName() + "] REQUEST_SET_GEOMETRY \"" +
				topicmapID + ":" + viewmode + "\" (" + topics.size() + " topics)");
		}
		// --- handle request ---
		CorporateDirectives directives = new CorporateDirectives();
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			PresentationTopic topic = (PresentationTopic) e.nextElement();
			Point p = topic.getGeometry();
			directives.add(as.moveTopic(topicmapID, 1, topic.getID(), p.x, p.y, true, session));	// triggerMovedHook=true
		}
		directives.updateCorporateMemory(as, session, topicmapID, viewmode);
		return new PresentationDirectives(directives, ps);
	}

	public void setTranslation(String topicmapID, String viewmode, int tx, int ty) {
		// >>> compare to service.InteractionConnection.setTranslation()
		// --- log request ---
		if (LOG_REQUESTS) {
			System.out.println("[" + session.getUserName() + "] " +
				"REQUEST_SET_TRANSLATION \"" + topicmapID + ":" + viewmode + "\" (" +
				tx + ", " + ty + ")");
		}
		// --- handle request ---
		as.updateViewTranslation(topicmapID, 1, viewmode, tx, ty);
		// Note: there is no result
	}



	// ----------------------------------
	// --- Type Service (synchronous) ---
	// ----------------------------------



	public PresentationType getTopicType(String typeID) throws DeepaMehtaException {
		if (LOG_TYPES) {
			System.out.print("[" + session.getUserName() + "] TYPE_REQUEST_TOPIC_TYPE" +
				" \"" + typeID + "\" ... ");
		}
		//
		PresentableType type = new PresentableType(as.type(typeID, 1));		// as.type() throws DME
		as.initTypeTopic(type, false, null);
		//
		if (LOG_TYPES) {
			System.out.println("\"" + type.getName() + "\"");
		}
		//
		return new PresentationType(type, ps);
	}

	public PresentationType getAssociationType(String typeID) throws DeepaMehtaException {
		if (LOG_TYPES) {
			System.out.print("[" + session.getUserName() + "] TYPE_REQUEST_ASSOC_TYPE" +
				" \"" + typeID + "\" ... ");
		}
		//
		PresentableType type = new PresentableType(as.type(typeID, 1));		// as.type() throws DME
		as.initTypeTopic(type, false, null);
		//
		if (LOG_TYPES) {
			System.out.println("\"" + type.getName() + "\"");
		}
		//
		return new PresentationType(type, ps);
	}



	// -------------------------
	// --- Messaging Service ---
	// -------------------------



	/* ### public void sendMessage(String message) {
		as.processMessage(message, session);
	} */



	// -----------------------------
	// --- File Transfer Service ---
	// -----------------------------



	public void downloadFile(String filename, int filetype, long lastModified) {
	}

	public void uploadFile(String filename, int filetype) {
	}

	public void processMessage(String message) {
		as.processMessage(message, session);
	}
}
