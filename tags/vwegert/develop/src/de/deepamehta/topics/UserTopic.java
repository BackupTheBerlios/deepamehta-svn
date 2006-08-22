package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.Commands;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.CorporateTopicMap;
import de.deepamehta.service.ApplicationService;
//
import java.util.*;



/**
 * A user who can login into DeepaMehta.
 * <P>
 * The active behavoir of a <CODE>UserTopic</CODE> is creating the users personal
 * workspace and its personal MIME Configuration once a new user is created.
 * Furthermore the users personal workspace is renamed once this <CODE>UserTopic</CODE>
 * is renamed.
 * <P>
 * <HR>
 * Last functional change: 3.8.2004 (2.0b3)<BR>
 * Last documentation update: 29.11.2000 (2.0a7)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class UserTopic extends PersonTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public UserTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives evoke(Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		//
		String mapID = createPersonalWorkspace();
		if (mapID != null) {
			//
			createConfigurationMap(mapID);
			//
			// join user to system-wide default workspace
			BaseTopic ws = as.getDefaultWorkspace(directives);
			if (ws != null) {
				WorkspaceTopic defaultWorkspace = (WorkspaceTopic) as.getLiveTopic(ws);
				defaultWorkspace.joinUser(getID(), REVEAL_MEMBERSHIP_WORKSPACE, true, session, directives);
			}
			//
			// transfer installation preferences as defaults for new user
			String instID = as.getActiveInstallation().getID();
			// ### compare to WorkspaceTopic.transferPreferences()
			Vector prefs = as.getPreferences(instID);
			System.out.println(">>> transfer " + prefs.size() + " installation preferences of " +
				instID + " to user \"" + getID() + "\"");
			as.setUserPreferences(getID(), prefs, directives);
		}
		//
		return directives;
	}



	// ------------------------------------------
	// --- Reacting upon dedicated situations ---
	// ------------------------------------------



	public CorporateDirectives nameChanged(String name, String topicmapID, String viewmode) {
		// >>> compare to WorkspaceTopic.nameChanged()
		//
		// change name of this user
		CorporateDirectives directives = super.nameChanged(name, topicmapID, viewmode);
		// change name of users personal workspace
		BaseTopic workspace = as.getWorkspace(getID(), directives);
		if (workspace != null) {
			directives.add(as.getLiveTopic(workspace).nameChanged(name, topicmapID, viewmode));
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
		//
		int editorContext = as.editorContext(topicmapID);
		commands.addNavigationCommands(this, editorContext, session);
		//
		// --- "Set Standard Workspace" ---
		String userID = getID();
		Vector workspaces = as.getWorkgroups(userID);
		// ### compare to as.getUsersDefaultWorkspace()
		BaseTopic standardWorkspace = as.getRelatedTopic(userID, SEMANTIC_PREFERENCE, TOPICTYPE_WORKSPACE, 2, true);	// emptyAllowed=true
		//
		commands.addSeparator();
		Commands workspacesGroup = commands.addCommandGroup(DeepaMehtaMessages.getString("UserTopic.SetWorkspace"),
			FILESERVER_ICONS_PATH, "workgroup.gif");
		// CMD_ASSIGN_TOPIC is handled by LiveTopic, 3 parameters are required, the 3rd is added by addTopicCommands()
		String command = CMD_ASSIGN_TOPIC + ":" + SEMANTIC_PREFERENCE + ":" + CARDINALITY_ONE;
		commands.addTopicCommands(workspacesGroup, workspaces, command, COMMAND_STATE_RADIOBUTTON,
			standardWorkspace, null, session, directives);	// title=null
		//
		// --- standard topic commands ---
		commands.addStandardCommands(this, editorContext, viewmode, session, directives);
		//
		return commands;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#setTopicData
	 */
	public CorporateDirectives propertiesChanged(Hashtable newData, Hashtable oldData,
											String topicmapID, String viewmode, Session session) {
		// ### compare to TopicMapTopic.propertiesChanged() and WorkspaceTopic.propertiesChanged()
		CorporateDirectives directives = super.propertiesChanged(newData, oldData,
			topicmapID, viewmode, session);
		//
		Enumeration e = newData.keys();
		while (e.hasMoreElements()) {
			String prop = (String) e.nextElement();
			if (prop.equals(PROPERTY_ICON)) {
				// icon
				String oldIcon = (String) oldData.get(PROPERTY_ICON);
				String newIcon = (String) newData.get(PROPERTY_ICON);
				if (!newIcon.equals(oldIcon)) {
					System.out.println("> \"" + prop + "\" property has been changed " +
						"-- send DIRECTIVE_SET_EDITOR_ICON (queued)");
					// cause the client to show the changed icon
					CorporateDirectives iconDirective = new CorporateDirectives();
					iconDirective.add(DIRECTIVE_SET_EDITOR_ICON,
						as.getWorkspace(getID()).getID(), newIcon);
					// Note: the DIRECTIVE_SET_TOPIC_ICON must be _queued_, because the
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

	public String getNameProperty() {
		return PROPERTY_USERNAME;
	}

	public static void propertyLabel(PropertyDefinition propertyDef, ApplicationService as, Session session) {
		String propName = propertyDef.getPropertyName();
		if (propName.equals(PROPERTY_NAME)) {
			propertyDef.setPropertyLabel("Last Name");
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	// ### this 2 methods should build directives instead of manipulating the corporate memory directly

	/**
	 * @see		#evoke
	 *
	 * @return	ID of the created personal workspace
	 */
	private String createPersonalWorkspace() {
		// Note: this user might already have a workspace, because this
		// topic might been of type tt-user before through retyping
		if (as.getWorkspace(getID()) != null) {
			System.out.println(">>> User \"" + getName() + "\" has already a workspace");
			return null;
		}
		// --- create users personal workspace ---
		String mapID = cm.getNewTopicID();
		//
		System.out.println(">>> Creating personal workspace for user \"" + getName() + "\" ... " + mapID);
		// create personal workspace with name corresponding to this users name
		// Note: this user may have already a name if this topic was of type tt-user before through retyping
		cm.createTopic(mapID, 1, TOPICTYPE_TOPICMAP, 1, getName());		// ### create live topic instead 
		// --- associate user with its personal workspace ---
		String assocID = cm.getNewAssociationID();
		cm.createAssociation(assocID, 1, SEMANTIC_WORKSPACE, 1, getID(), 1, mapID, 1);
		//
		return mapID;
	}

	/**
	 * Creates users MIME Configuration.
	 *
	 * @see		#evoke
	 */
	private void createConfigurationMap(String personalmapID) {
		String confmapID = cm.getNewTopicID();
		//
		System.out.println(">>> Creating \"MIME Configuration\" for user \"" + getName() + "\" ... " + confmapID);
		// create generic MIME Configuration
		cm.createTopic(confmapID, 1, TOPICTYPE_TOPICMAP, 1, MIME_CONF_MAPNAME);
		cm.setTopicData(confmapID, 1, PROPERTY_NAME, MIME_CONF_MAPNAME);	// ### must set name prop
		new CorporateTopicMap(as, "t-genericconfmap", 1).personalize(confmapID);
		// put MIME Configuration in users personal workspace
		cm.createViewTopic(personalmapID, 1, VIEWMODE_USE, confmapID, 1, 600, 30, false);
		// associate user with MIME Configuration
		String assocID = cm.getNewAssociationID();
		cm.createAssociation(assocID, 1, SEMANTIC_CONFIGURATION_MAP, 1, getID(), 1, confmapID, 1);
	}
}
