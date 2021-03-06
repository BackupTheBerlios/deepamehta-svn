package de.deepamehta.topics;

import de.deepamehta.Topic;
import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaUtils;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.ApplicationService;
//
import java.util.*;



/**
 * A chat between logged users which are members of the same workspace.
 * <P>
 * <HR>
 * Last functional change: 24.8.2006 (2.0b8)<BR>
 * Last documentation update: 27.5.2001 (2.0a11-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class ChatTopic extends LiveTopic {



	// *************
	// *** Field ***
	// *************



	/**
	 * The workspace this <CODE>ChatTopic</CODE> belongs to.
	 * <P>
	 * <TABLE>
	 * <TR><TD><B>Initialized by</B></TD></TR>
	 * <TR><TD>{@link #initWorkspace}</TD></TR>
	 * <TR><TD><B>Accessed by</B></TD></TR>
	 * <TR><TD>{@link #executeCommand}</TD></TR>
	 * </TABLE>
	 */
	private BaseTopic workspace;

	/**
	 * The workspace of the workgroup this <CODE>ChatTopic</CODE> belongs to.
	 * <P>
	 * <TABLE>
	 * <TR><TD><B>Initialized by</B></TD></TR>
	 * <TR><TD>{@link #initWorkspace}</TD></TR>
	 * <TR><TD><B>Accessed by</B></TD></TR>
	 * <TR><TD>{@link #executeCommand}</TD></TR>
	 * </TABLE>
	 */
	// ### private BaseTopic chatBoard;



	// *******************
	// *** Constructor ***
	// *******************



	public ChatTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives init(int initLevel, Session session) throws TopicInitException {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		if (initLevel == INITLEVEL_1) {
			initWorkspace(directives);
			if (LOG_TOPIC_INIT) {
				System.out.println(">>> ChatTopic.init(" + initLevel + "): " + this + " belongs to " + workspace);
			}
		}
		//
		return directives;
	}

	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		// Note: triggering super evoke must perform before the props are set
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		// set props
		Hashtable props = new Hashtable();
		props.put(PROPERTY_DATE, DeepaMehtaUtils.getDate());
		props.put(PROPERTY_BEGIN, DeepaMehtaUtils.getTime(false));	// withSecs=false
		// Note: the application service must be used in order to trigger the topic naming behavoir
		directives.add(as.setTopicProperties(getID(), 1, props, topicmapID, true, session));	// triggerPropertiesChangedHook=true
		// notify workspace members
		Vector sessions = activeSessions(session);
		String text = "User \"" + session.getUserName() + "\" invites you to a \"" + workspace.getName() +
			"\" chat. " + (sessions.size() + 1) + " members are online right now.";
		//
		CorporateDirectives notification = new CorporateDirectives();
		notification.add(DIRECTIVE_SHOW_MESSAGE, text, new Integer(NOTIFICATION_DEFAULT));
		as.broadcastSessions(notification, sessions.elements(), false);
		//
		text = "You started a \"" + workspace.getName() + "\" chat. Besides you " + sessions.size() +
			" members are online right now.";
		directives.add(DIRECTIVE_SHOW_MESSAGE, text, new Integer(NOTIFICATION_DEFAULT));
		//
		return directives;
	}



	// -------------------------
	// --- Handling Commands ---
	// -------------------------



	/**
	 * Here is all active behavoir of ChatTopic class.
	 * <P>
	 * "Your Remark" property is added to "Chat Flow" property and then
	 * <CODE>DIRECTIVE_SHOW_TOPIC_PROPERTIES</CODE> directive is broadcasted to all related
	 * users. 
	 * If <CODE>ChatTopic</CODE> belongs to Corporate
	 * workspace, directive is broadcasted to all existing client sessions, otherwise
	 * it is broadcasted to the client sessions that belong to given workspace.
	 * <P>
	 * Property "Your Remark" is cleaned only for current client session, for other
	 * client sessions it is left unchanged.
	 */
	public CorporateDirectives executeCommand(String command, Session session,
													String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		if (command.equals(CMD_SUBMIT_FORM)) {
			String newEntry = getProperty(PROPERTY_YOUR_REMARK);
			if (!newEntry.equals("")) {
				Hashtable props = new Hashtable();
				props.put(PROPERTY_CHAT_FLOW, appendInput(newEntry, session));
				directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(getVersion()));
				// broadcast message via related client sessions
				as.broadcastSessions(directives, activeSessions(session).elements(), false);
				//
				props.put(PROPERTY_YOUR_REMARK, "");
				directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(getVersion()));
			}
		} else {
			directives.add(super.executeCommand(command, session, topicmapID, viewmode));
		}
		//
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public String getNameProperty() {
		return null;
	}

	/**
	 * @param	props		contains only changed properties
	 *
	 * @see		de.deepamehta.service.ApplicationService#setTopicData
	 */
	public String getTopicName(Hashtable props, Hashtable oldProps) {
		String date = (String) props.get(PROPERTY_DATE);
		String time = (String) props.get(PROPERTY_BEGIN);
		//
		if (date == null && time == null) {
			return null;
		}
		// ### either date or time is null when re-evoked trough retyping
		String name = date + "   " + time;
		System.out.println(">>> ChatTopic.getTopicName(): set name \"" + name + "\"");
		return name;
	}

	public Vector disabledProperties(Session session) {
		// Note: the default implementation is not called here
		Vector props = new Vector();
		props.addElement(PROPERTY_CHAT_FLOW);
		// ### disable "Your Remark" if not workspace member
		props.addElement(PROPERTY_DATE);
		props.addElement(PROPERTY_BEGIN);
		return props;
	}



	// *********************
	// *** Public Method ***
	// *********************



	public BaseTopic getChatBoard() {
		return as.getRelatedTopic(getID(), SEMANTIC_CHAT, TOPICTYPE_CHAT_BOARD, 1, false);
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Initializes <CODE>workspace</CODE> variable.
	 *
	 * @see		#init
	 */
	private void initWorkspace(CorporateDirectives directives) throws TopicInitException {
		BaseTopic owner = as.getTopicmapOwner(getChatBoard().getID());
		// ### error check
		if (!owner.getType().equals(TOPICTYPE_WORKSPACE)) {
			throw new TopicInitException("a chat baord is not functional in personal workspace");
		}
		//
		this.workspace = owner;
		// ### this.chatBoard = as.getWorkspace(workspace.getID(), directives);
	}

	private Vector activeSessions(Session session) {
		Vector sessions = as.activeWorkspaceSessions(workspace.getID());
		sessions.remove(session);
		return sessions;
	}

	private String appendInput(String text, Session session) {
		String chatFlow = getProperty(PROPERTY_CHAT_FLOW);
		//
		int pos = chatFlow.indexOf("</body>");
		// error check
		if (pos == -1) {
			throw new DeepaMehtaException("no &lt;/body> tag found");
		}
		//
		chatFlow = chatFlow.substring(0, pos) +
			"<p><font color=\"#A8A898\">" + session.getUserName() + "</font>&nbsp;&nbsp;&nbsp;" + text + "</p>" +
			chatFlow.substring(pos);
		//
		return chatFlow;
	}
}
