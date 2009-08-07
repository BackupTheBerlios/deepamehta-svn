package de.deepamehta.topics;

import de.deepamehta.Association;
import de.deepamehta.BaseTopic;
import de.deepamehta.Commands;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.PresentableTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.CorporateTopicMap;
import de.deepamehta.service.Session;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;



/**
 * A workspace.
 * <p>
 * The custom behavoir of a <code>WorkspaceTopic</code> is creating the corresponding
 * topicmap and default chattopic once a new workspace is created.
 * Furthermore the corresponding topicmap (and chattopic) is renamed once
 * this <code>WorkspaceTopic</code> is renamed.
 * <p>
 * <hr>
 * Last change: 14.9.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class WorkspaceTopic extends LiveTopic {



	// *****************
	// *** Constants ***
	// *****************



	// commands

	private static final String CMD_JOIN_WORKSPACE = "joinWorkspace";
	private static final String ICON_JOIN_WORKSPACE = "subscribe.gif";
	//
	static final String CMD_LEAVE_WORKSPACE = "leaveWorkspace";
	static final String ICON_LEAVE_WORKSPACE = "unsubscribe.gif";
	//
	static final String CMD_ASSIGN_TOPIC_TYPE = "assignTopicType";
	static final String ICON_ASSIGN_TOPIC_TYPE = "assignTopicType.gif";
	//
	static final String CMD_ASSIGN_ASSOC_TYPE = "assignAssocType";
	static final String ICON_ASSIGN_ASSOC_TYPE = "assignAssociationType.gif";



	// *******************
	// *** Constructor ***
	// *******************



	public WorkspaceTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		System.out.println(">>> WorkspaceTopic.evoke(): " + this + " -- creating topicmap and chat");
		// Note: no directives are added by evoke(), instead the corporate memory is manipulated directly
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		// --- create topicmap ---
		String mapID = cm.getNewTopicID();
		cm.createTopic(mapID, 1, TOPICTYPE_TOPICMAP, 1, "");		// ### create live topic instead ### version=1
		// --- associate workspace with its topicmap ---
		String assocID = cm.getNewAssociationID();
		cm.createAssociation(assocID, 1, SEMANTIC_WORKSPACE_TOPICMAP, 1, getID(), 1, mapID, 1);		// ### version=1
		// --- create chat board---
		String chatId = cm.getNewTopicID();
		cm.createTopic(chatId, 1, TOPICTYPE_CHAT_BOARD, 1, "Chats");			// ### hardcoded
		cm.createViewTopic(mapID, 1, VIEWMODE_USE, chatId, 1, 30, 70, false);	// ### geometry
		// --- create message board ---
		String boardID = cm.getNewTopicID();
		cm.createTopic(boardID, 1, TOPICTYPE_MESSAGE_BOARD, 1, "Forum");		// ### hardcoded
		cm.createViewTopic(mapID, 1, VIEWMODE_USE, boardID, 1, 100, 50, false);	// ### geometry
		// --- set default properties ---
		setProperty(PROPERTY_PUBLIC, SWITCH_OFF);
		setProperty(PROPERTY_DEFAULT_WORKSPACE, SWITCH_OFF);
		// --- join current user ---
		joinUser(session.getUserID(), REVEAL_MEMBERSHIP_NONE, session, directives);
		//
		return directives;
	}



	// ------------------------------------------
	// --- Reacting upon dedicated situations ---
	// ------------------------------------------



	public CorporateDirectives nameChanged(String name, String topicmapID, Session session) {
		// >>> compare to UserTopic.nameChanged()
		// >>> compare to TopicTypeTopic.nameChanged()
		//
		CorporateDirectives directives = super.nameChanged(name, topicmapID, session);
		// rename workspace topicmap
		BaseTopic workspaceTopicmap = as.getWorkspaceTopicmap(getID(), directives);
		if (workspaceTopicmap != null) {
			directives.add(as.setTopicProperty(workspaceTopicmap, PROPERTY_NAME, name, topicmapID, session));
		}
		// rename chat
		BaseTopic chatboard = as.getChatboard(this, directives);
		if (chatboard != null) {
			directives.add(as.setTopicProperty(chatboard, PROPERTY_NAME, name + " Chats", topicmapID, session));	// ### hardcoded
		}
		// rename message board
		BaseTopic messageboard = as.getMessageboard(this, directives);
		if (messageboard != null) {
			directives.add(as.setTopicProperty(messageboard, PROPERTY_NAME, name + " Forum", topicmapID, session));	// ### hardcoded
		}
		//
		return directives;
	}



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		int editorContext = as.editorContext(topicmapID);
		//
		commands.addNavigationCommands(this, editorContext, session);
		commands.addSeparator();
		// --- "Join"/"Leave" ---
		boolean isPublic = getProperty(PROPERTY_PUBLIC).equals(SWITCH_ON);
		boolean isMember = as.isMemberOf(session.getUserID(), getID());
		int joinState = isPublic && !isMember && !session.isDemo() ? COMMAND_STATE_DEFAULT : COMMAND_STATE_DISABLED;
		int leaveState = isMember ? COMMAND_STATE_DEFAULT : COMMAND_STATE_DISABLED;
		commands.addCommand(as.string(ITEM_JOIN_WORKSPACE), CMD_JOIN_WORKSPACE,
			FILESERVER_IMAGES_PATH, ICON_JOIN_WORKSPACE, joinState);
		commands.addCommand(as.string(ITEM_LEAVE_WORKSPACE), CMD_LEAVE_WORKSPACE,
			FILESERVER_IMAGES_PATH, ICON_LEAVE_WORKSPACE, leaveState);
		commands.addSeparator();
		// --- "Assign Topic Type" / "Assign Association Type" ---
		Commands topicTypesGroup = commands.addCommandGroup(as.string(ITEM_ASSIGN_TOPIC_TYPE),
			FILESERVER_IMAGES_PATH, ICON_ASSIGN_TOPIC_TYPE);
		if (!session.isDemo()) {
			commands.addTopicTypeCommands(topicTypesGroup, CMD_ASSIGN_TOPIC_TYPE,
				PERMISSION_CREATE, false, session, directives);
		}
		//
		Commands assocTypesGroup = commands.addCommandGroup(as.string(ITEM_ASSIGN_ASSOC_TYPE),
			FILESERVER_IMAGES_PATH, ICON_ASSIGN_ASSOC_TYPE);
		if (!session.isDemo()) {
			commands.addAssocTypeCommands(assocTypesGroup, CMD_ASSIGN_ASSOC_TYPE,
				PERMISSION_CREATE, false, session, directives);
		}
		//
		// --- standard topic commands ---
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command, Session session,
													String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		if (cmd.equals(CMD_JOIN_WORKSPACE)) {
			joinUser(session.getUserID(), REVEAL_MEMBERSHIP_USER, session, directives);
		} else if (cmd.equals(CMD_LEAVE_WORKSPACE)) {
			leaveUser(session.getUserID(), topicmapID, session, directives);
		} else if (cmd.equals(CMD_ASSIGN_TOPIC_TYPE)) {
			String typeID = st.nextToken();
			assignType(typeID, directives);
		} else if (cmd.equals(CMD_ASSIGN_ASSOC_TYPE)) {
			String typeID = st.nextToken();
			assignType(typeID, directives);
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		return directives;
	}
		


	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#setTopicData
	 */
	public CorporateDirectives propertiesChanged(Hashtable newData, Hashtable oldData,
											String topicmapID, String viewmode, Session session) {
		// >>> compare to TopicMapTopic.propertiesChanged() and
		// >>> UserTopic.propertiesChanged()
		CorporateDirectives directives = super.propertiesChanged(newData, oldData,
			topicmapID, viewmode, session);
		Enumeration e = newData.keys();
		String prop;
		while (e.hasMoreElements()) {
			prop = (String) e.nextElement();
			if (prop.equals(PROPERTY_ICON)) {
				// --- "Icon" changed ---
				String oldIcon = (String) oldData.get(PROPERTY_ICON);
				String newIcon = (String) newData.get(PROPERTY_ICON);
				if (!newIcon.equals(oldIcon)) {
					System.out.println("> \"" + prop + "\" property has been changed " +
						"-- send DIRECTIVE_SET_EDITOR_ICON (queued)");
					// cause the client to show the changed icon
					CorporateDirectives iconDirective = new CorporateDirectives();
					iconDirective.add(DIRECTIVE_SET_EDITOR_ICON,
						as.getWorkspaceTopicmap(getID()).getID(), newIcon);	// ### consider as.getWorkspaceTopicmap(2)
					// Note: the DIRECTIVE_SET_EDITOR_ICON must be _queued_, because the
					// icon upload must be completed _before_ the icon can be shown
					directives.add(DIRECTIVE_QUEUE_DIRECTIVES, iconDirective);
				}
			} else {
				System.out.println(">>> \"" + prop + "\" property change causes no " +
					"special action");
			}
		}
		//
		return directives;
	}



	// -----------------------------
	// --- Handling Associations ---
	// -----------------------------



	/**
	 * Workspaces are reacting if a membership association is connected.
	 */
	public String associationAllowed(String assocTypeID, String relTopicID, int relTopicPos, CorporateDirectives directives) {
		if (assocTypeID.equals(SEMANTIC_MEMBERSHIP)) {
			return as.getMembershipType(getID(), directives);
		}
		return super.associationAllowed(assocTypeID, relTopicID, relTopicPos, directives);
	}

	public void associated(String assocTypeID, String relTopicID, Session session, CorporateDirectives directives) {
		if (as.getLiveTopic(relTopicID, 1).getType().equals(TOPICTYPE_USER) &&
				as.type(assocTypeID, 1).hasSupertype(SEMANTIC_MEMBERSHIP)) {
			// join
			String userID = relTopicID;
			// --- open workspace ---
			boolean isCurrentUser = userID.equals(session.getUserID());
			if (isCurrentUser) {
				BaseTopic workspace = as.getWorkspaceTopicmap(getID(), directives);
				if (workspace != null) {
					CorporateTopicMap topicmap = new CorporateTopicMap(as, workspace.getID(), 1);
					topicmap.createLiveTopicmap(session, directives);
					//
					directives.add(DIRECTIVE_SHOW_WORKSPACE, workspace, topicmap, new Integer(EDITOR_CONTEXT_WORKGROUP));
				}
			}
			// --- transfer workspace prefernces to user ---
			directives.add(transferPreferences(userID));
		}
	}

	public void associationRemoved(String assocTypeID, String relTopicID, Session session, CorporateDirectives directives) {
		if (as.getLiveTopic(relTopicID, 1).getType().equals(TOPICTYPE_USER) &&
				as.type(assocTypeID, 1).hasSupertype(SEMANTIC_MEMBERSHIP)) {
			// leave
			String userID = relTopicID;
			// --- close workspace ---
			boolean isCurrentUser = userID.equals(session.getUserID());
			if (isCurrentUser) {
				BaseTopic workspace = as.getWorkspaceTopicmap(getID(), directives);
				if (workspace != null) {
					directives.add(DIRECTIVE_CLOSE_EDITOR, workspace.getID());
				}
			}
		}
	}



	// *********************
	// *** Public Method ***
	// *********************



	/**
	 * Joins the specified user to this workspace.
	 * <p>
	 * An association of type <code>ASSOCTYPE_MEMBERSHIP</code> (or one of its subtypes) is
	 * created between the user and this workspace.
	 * <p>
	 * References checked: 24.3.2008 (2.0b8)
	 *
	 * @param	membershipRevealMode	REVEAL_MEMBERSHIP_NONE<br>
	 *									REVEAL_MEMBERSHIP_USER<br>
	 *									REVEAL_MEMBERSHIP_WORKSPACE
	 *
	 * @see		#evoke
	 * @see		#executeCommand
	 * @see		UserTopic#evoke
	 * @see		de.deepamehta.kompetenzstern.assocs.KompetenzsternMembership#propertiesChanged
	 */
	public void joinUser(String userID, int membershipRevealMode, Session session, CorporateDirectives directives) {
		// ---  association ---
		String assocType = as.getMembershipType(getID(), directives);
		if (membershipRevealMode == REVEAL_MEMBERSHIP_NONE) {
			// create membership
			as.createLiveAssociation(as.getNewAssociationID(), assocType, userID, getID(), session, directives);
		} else {
			// build directives to create membership
			revealMembership(userID, assocType, membershipRevealMode, directives);
		}
		// Note: two more actions are performed in the associated() hook (see above), which is triggered
		// as response to the newly created membership association:
		// 1) add the workspace to the users GUI (provided the user in question is the current user)
		// 2) transfer the workspaces preferences to the user
	}

	/**
	 * Specified user leaves this workspace.
	 * <p>
	 * The membership association between the user and this workspace is deleted.
	 * <p>
	 * References checked: 24.3.2008 (2.0b8)
	 * 
	 * @param	topicmapID		the topicmap from which the origin command was issued
	 *
	 * @see		#executeCommand
	 * @see		de.deepamehta.kompetenzstern.assocs.KompetenzsternMembership#propertiesChanged
	 */
	public void leaveUser(String userID, String topicmapID, Session session, CorporateDirectives directives) {
		// Note: also subtypes of association type "at-membership" are respected
		Vector subtypes = as.type(SEMANTIC_MEMBERSHIP, 1).getSubtypeIDs();
		Association assoc = cm.getAssociation(subtypes, userID, getID());
		// error check ### should not happen anymore
		if (assoc == null) {
			throw new DeepaMehtaException("user \"" + userID + "\" is not a member of workspace \"" + getName() + "\"");
		}
		//
		directives.add(DIRECTIVE_HIDE_ASSOCIATION, assoc.getID(), Boolean.TRUE, topicmapID);	// die=TRUE
		// Note: one more action is performed in the associationRemoved() hook (see above), which is triggered
		// as response to the deleted membership association:
		// 1) remove the workspace from the users GUI (provided the user in question is the current user)
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Builds the directives to create and reveal a membership association between a user and this workspace.
	 *
	 * @param	userID		the ID of the user
	 * @param	assocType	the ID of the association type. <code>ASSOCTYPE_MEMBERSHIP</code> or one of its subtypes.
	 *
	 * @see		#joinUser
	 */
	private void revealMembership(String userID, String assocType, int membershipRevealMode,
																CorporateDirectives directives) {
		if (membershipRevealMode == REVEAL_MEMBERSHIP_USER) {
			PresentableTopic user = new PresentableTopic(as.getLiveTopic(userID, 1), getID());
			PresentableAssociation assoc = as.createPresentableAssociation(
				assocType, userID, 1, getID(), getVersion(), false);
			directives.add(DIRECTIVE_SHOW_TOPIC, user);
			directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);
			directives.add(DIRECTIVE_SELECT_ASSOCIATION, assoc.getID());
		} else if (membershipRevealMode == REVEAL_MEMBERSHIP_WORKSPACE) {
			PresentableTopic workspace = new PresentableTopic(this, userID);
			PresentableAssociation assoc = as.createPresentableAssociation(
				assocType, userID, 1, getID(), getVersion(), false);
			directives.add(DIRECTIVE_SHOW_TOPIC, workspace);
			directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);
			// ### directives.add(DIRECTIVE_SELECT_ASSOCIATION, assoc.getID());
		} else {
			throw new DeepaMehtaException("unexpected membership reveal mode: " + membershipRevealMode);
		}
	}

	/**
	 * Transfers workspace preferences as defaults for the specified user.
	 * <p>
	 * References checked: 2.4.2003 (2.0a18-pre8)
	 *
	 * @see		#joinUser
	 */
	private CorporateDirectives transferPreferences(String userID) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		// ### compare to UserTopic.evoke()
		Vector prefs = as.getPreferences(getID());
		System.out.println(">>> transfer " + prefs.size() + " workspace preferences of " + this + " to user \"" + userID + "\"");
		as.setUserPreferences(userID, prefs, directives);
		//
		return directives;
	}

	/**
	 * Assigns the specified type to this workspace.
	 * 
	 * @see		#executeCommand
	 */
	private void assignType(String typeID, CorporateDirectives directives) {
		PresentableTopic type = new PresentableTopic(as.getLiveTopic(typeID, 1), getID());
		PresentableAssociation assoc = as.createPresentableAssociation(
			SEMANTIC_WORKSPACE_TYPES, getID(), getVersion(), typeID, 1, false);
		directives.add(DIRECTIVE_SHOW_TOPIC, type);
		directives.add(DIRECTIVE_SHOW_ASSOCIATION, assoc, Boolean.TRUE);
	}
}
