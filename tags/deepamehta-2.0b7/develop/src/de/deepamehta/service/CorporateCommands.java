package de.deepamehta.service;

import de.deepamehta.AmbiguousSemanticException;
import de.deepamehta.BaseTopic;
import de.deepamehta.BaseAssociation;
import de.deepamehta.Commands;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaUtils;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.Relation;
import de.deepamehta.assocs.LiveAssociation;
import de.deepamehta.topics.LiveTopic;
import de.deepamehta.topics.TypeTopic;
import de.deepamehta.topics.TopicTypeTopic;
import de.deepamehta.topics.AssociationTypeTopic;
//
import java.util.*;



/**
 * Utility class for building {@link de.deepamehta.Commands topic commands / association commands}.
 * <P>
 * <HR>
 * Last functional change: 17.2.2005 (2.0b5)<BR>
 * Last documentation update: 9.10.2001 (2.0a12)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public final class CorporateCommands extends Commands implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private ApplicationService as;



	// *******************
	// *** Constructor ***
	// *******************



	public CorporateCommands(ApplicationService as) {
		this.as = as;
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * Adds a "Create" command group to this (view) command set.
	 * <P>
	 * References checked: 11.6.2002 (2.0a15-pre7)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#viewCommands
	 */
	public void addCreateCommands(String viewmode, Session session, CorporateDirectives directives) {
		Commands createGroup = addCommandGroup(as.string(ITEM_NEW_TOPIC), FILESERVER_IMAGES_PATH, ICON_NEW_TOPIC);
		if (!session.isDemo()) {
			addTopicTypeCommands(createGroup, CMD_CREATE_TOPIC, PERMISSION_CREATE, false, session, directives);
		}
	}

	// ---

	/**
	 * Adds a "Search by Topic Type" command group to this (view) command set.
	 * <P>
	 * References checked: 16.6.2002 (2.0a15-pre8)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#viewCommands
	 */
	public void addSearchByTopictypeCommand(String viewmode, Session session,
													CorporateDirectives directives) {
		Commands showGroup = addCommandGroup(as.string(ITEM_SEARCH_BY_TOPICTYPE), FILESERVER_IMAGES_PATH, ICON_SEARCH_BY_TOPICTYPE);
		addTopicTypeCommands(showGroup, CMD_SEARCH_BY_TOPICTYPE, PERMISSION_VIEW, false, session, directives);
	}

	// ---

	/**
	 * Adds a "Hide all" command group to this (view) command set.
	 * <P>
	 * References checked: 4.12.2002 (2.0a17-pre2)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#viewCommands
	 */
	public void addHideAllCommands(String topicmapID, String viewmode, Session session) {
		Commands hideGroup = addCommandGroup(as.string(ITEM_HIDE_ALL),
			FILESERVER_IMAGES_PATH, ICON_HIDE_ALL);
		Hashtable types = as.cm.getTopicTypes(topicmapID, 1, viewmode);
		addTypeCommands(hideGroup, types, CMD_HIDE_ALL, null);	// heading=null
	}

	/**
	 * Adds a "Close" command to this (view) command set.
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#viewCommands
	 */
	public void addCloseCommand(Session session) {
		int state = session.isDemo() ? COMMAND_STATE_DISABLED : COMMAND_STATE_DEFAULT;
		addCommand(as.string(ITEM_CLOSE_VIEW), CMD_CLOSE_VIEW, FILESERVER_IMAGES_PATH, ICON_CLOSE_VIEW, state);
	}

	/**
	 * References checked: 28.6.2004 (2.0b3)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#contextCommands
	 * @see		de.deepamehta.topics.TopicMapTopic#viewCommands
	 * @see		de.deepamehta.topics.MessageBoardTopic#viewCommands
	 * @see		de.deepamehta.topics.ChatBoardTopic#viewCommands
	 */
	public void addPublishCommand(String topicmapID, Session session, CorporateDirectives directives) {
		String userID = session.getUserID();
		String workgroupID;
		Commands commandGroup = addCommandGroup(as.string(ITEM_PUBLISH), FILESERVER_IMAGES_PATH, ICON_PUBLISH);
		BaseTopic workgroup = as.getOriginWorkgroup(topicmapID);
		if (workgroup == null) {
			// this topicmap has been created in the users personal workspace originally -- it was never
			// published. Publishing policy is as follows: it can be published to all worspaces the user
			// has joined as well as to the default workspace if the user is an administrator
			//
			// enable publishing to users default workspace first
			BaseTopic defaultWorkspace = as.getUsersDefaultWorkspace(userID, directives);
			String defaultWorkspaceID = null;
			if (defaultWorkspace != null) {
				workgroupID = defaultWorkspace.getID();
				defaultWorkspaceID = workgroupID;
				commandGroup.addCommand(defaultWorkspace.getName(), CMD_PUBLISH + ":" + workgroupID,
					FILESERVER_ICONS_PATH, as.getIconfile(defaultWorkspace));
				commandGroup.addSeparator();	// ### conditional
			}
			// enable publishing to other workspaces the user is a member of
			Vector workgroups = as.getWorkgroups(userID);
			Enumeration e = workgroups.elements();
			while (e.hasMoreElements()) {
				BaseTopic workgroupTopic = (BaseTopic) e.nextElement();
				workgroupID = workgroupTopic.getID();
				// skip default workspace
				if (workgroupID.equals(defaultWorkspaceID)) {
					continue;
				}
				commandGroup.addCommand(workgroupTopic.getName(), CMD_PUBLISH + ":" + workgroupID,
					FILESERVER_ICONS_PATH, as.getIconfile(workgroupTopic));
			}
		} else {
			// this topicmap is a personalized view originating from a shared workspace -- it can be published
			// "back" to its original workspace. Note: also the default workspace is a shared workspace.
			workgroupID = workgroup.getID();
			commandGroup.addCommand(workgroup.getName(), CMD_PUBLISH + ":" + workgroupID,
				FILESERVER_ICONS_PATH, as.getIconfile(workgroup));
		}
	}

	public void addImportCommand(Session session) {
		addCommand(as.string(ITEM_IMPORT_TOPICMAP), CMD_IMPORT_TOPICMAP,
			FILESERVER_IMAGES_PATH, ICON_IMPORT_TOPICMAP);
	}

	/**
	 * References checked: 13.12.2001 (2.0a14-pre4)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#contextCommands
	 * @see		de.deepamehta.topics.TopicMapTopic#viewCommands
	 */
	public void addExportCommand(Session session, CorporateDirectives directives) {
		// --- export command ---
		int state = session.isDemo() ? COMMAND_STATE_DISABLED : COMMAND_STATE_DEFAULT;
		addCommand(as.string(ITEM_EXPORT_TOPICMAP), CMD_EXPORT_TOPICMAP,
			FILESERVER_IMAGES_PATH, ICON_EXPORT_TOPICMAP, state);
		// --- preferences submenu ---
		Commands prefsGroup = addCommandGroup(as.string(ITEM_PREFERENCES_EXPORT), FILESERVER_ICONS_PATH, ICON_PREFERENCES);
		if (!session.isDemo()) {
			BaseTopic exportFormat = null;
			try {
				exportFormat = as.getExportFormat(session.getUserID(), directives);   // DME
			} catch (DeepaMehtaException e) {
				System.out.println("*** TopicMapTopic.addExportCommand(): " + e);
			}
			addTopicCommands(prefsGroup, as.cm.getTopics(TOPICTYPE_EXPORT_FORMAT), CMD_SET_EXPORT_FORMAT,
				COMMAND_STATE_RADIOBUTTON, exportFormat, "Export Format", session, directives);
		}
	}

	// ---

	/**
	 * References checked: 11.6.2002 (2.0a15-pre7)
	 *
	 * @see		de.deepamehta.topics.LiveTopic#contextCommands
	 * @see		de.deepamehta.topics.WorkspaceTopic#contextCommands
	 * @see		de.deepamehta.topics.PropertyTopic#contextCommands
	 * @see		de.deepamehta.topics.TypeTopic#contextCommands
	 */
	public void addNavigationCommands(BaseTopic topic, int editorContext, Session session) {
		if (editorContext == EDITOR_CONTEXT_VIEW) {
			Commands navGroup = addCommandGroup(as.string(ITEM_NAVIGATION),
				FILESERVER_IMAGES_PATH, ICON_NAVIGATION);
			//
			// --- by topic type ---
			Hashtable topicTypes = as.revealTopicTypes(topic.getID(), 1);		// ### version
			if (!addTypeCommands(navGroup, topicTypes, CMD_NAVIGATION_BY_TOPIC, as.string(ITEM_NAVIGATION_BY_TOPIC))) {
				return;
			}
			// --- by association type ---
			Hashtable assocTypes = as.revealAssociationTypes(topic.getID(), 1);	// ### version
			addTypeCommands(navGroup, assocTypes, CMD_NAVIGATION_BY_ASSOCIATION,
				as.string(ITEM_NAVIGATION_BY_ASSOCIATION));
		}
	}

	/**
	 * References checked: 9.6.2002 (2.0a15-pre7)
	 *
	 * @see		de.deepamehta.topics.LiveTopic#contextCommands
	 * @see		de.deepamehta.topics.PropertyTopic#contextCommands
	 * @see		de.deepamehta.topics.TypeTopic#contextCommands
	 */
	public void addStandardCommands(LiveTopic topic, int editorContext, String viewmode,
												Session session, CorporateDirectives directives) {
		addRelationCommands(topic, session, directives);
		// only add rename and property commands if no side panel is shown
		if (editorContext == EDITOR_CONTEXT_VIEW) {
			addSeparator();
			addHideTopicCommand(session);
			addRetypeTopicCommand(topic, session, directives);
			addDeleteTopicCommand(topic, session);
		}
		// --- delete ---
		if (editorContext == EDITOR_CONTEXT_PERSONAL) {
			// e.g. exported documents in personal workspace can be deleted
			if (!isEmpty()) {
				addSeparator();
			}
			addDeleteTopicCommand(topic, session);
		}
	}

	// ---

	/**
	 * Adds a "Retype" command to this (topic) command set.
	 * <P>
	 * References checked: 9.2.2005 (2.0b5)
	 *
	 * @see		#addStandardCommands
	 */
	public void addRetypeTopicCommand(BaseTopic topic, Session session, CorporateDirectives directives) {
		Commands changeGroup = addCommandGroup(as.string(ITEM_CHANGE_TOPIC_TYPE),
			FILESERVER_IMAGES_PATH, ICON_CHANGE_TOPIC_TYPE);
		//
		if (as.retypeTopicIsAllowed(topic.getID(), topic.getVersion(), session)) {
			addTopicTypeCommands(changeGroup, CMD_CHANGE_TOPIC_TYPE, PERMISSION_CREATE, false, session, directives);
			//
			changeGroup.addSeparator();
			// "Create Topic Type ..."
			changeGroup.addCommand(as.string(ITEM_NEW_TOPIC_TYPE), CMD_NEW_TOPIC_TYPE,
				FILESERVER_IMAGES_PATH, ICON_NEW_TOPIC_TYPE);
		}
	}

	/**
	 * Adds a "Retype" command to this (association) command set.
	 * <P>
	 * References checked: 9.2.2005 (2.0b5)
	 *
	 * @see		de.deepamehta.assocs.LiveAssociation#contextCommands
	 */
	public void addRetypeAssociationCommand(BaseAssociation assoc, Session session, CorporateDirectives directives) {
		Commands changeGroup = addCommandGroup(as.string(ITEM_CHANGE_ASSOC_TYPE),
			FILESERVER_IMAGES_PATH, ICON_CHANGE_ASSOC_TYPE);
		//
		if (as.retypeAssociationIsAllowed(assoc.getID(), assoc.getVersion(), session)) {
			addAssocTypeCommands(changeGroup, CMD_CHANGE_ASSOC_TYPE, PERMISSION_CREATE, false, session, directives);
			//
			changeGroup.addSeparator();
			// "Create Association Type ..."
			changeGroup.addCommand(as.string(ITEM_NEW_ASSOC_TYPE), CMD_NEW_ASSOC_TYPE,
				FILESERVER_IMAGES_PATH, ICON_NEW_ASSOC_TYPE);
		}
	}

	// ---

	/**
	 * Adds a "Hide" command to this (topic) command set.
	 * <P>
	 * References checked: 11.10.2001 (2.0a12)<BR>
	 * Documentation updated: 11.10.2001 (2.0a12)
	 *
	 * @see		#addStandardCommands
	 */
	public void addHideTopicCommand(Session session) {
		addCommand(as.string(ITEM_HIDE_TOPIC), CMD_HIDE_TOPIC, FILESERVER_IMAGES_PATH, ICON_HIDE_TOPIC);
	}

	/**
	 * Adds a "Hide" command to this (association) command set.
	 * <P>
	 * References checked: 11.10.2001 (2.0a12)
	 *
	 * @see		de.deepamehta.assocs.LiveAssociation#contextCommands
	 */
	public void addHideAssociationCommand(Session session) {
		addCommand(as.string(ITEM_HIDE_ASSOC), CMD_HIDE_ASSOC, FILESERVER_IMAGES_PATH, ICON_HIDE_ASSOC);
	}

	// --- addDeleteTopicCommand (4 forms) ---

	/**
	 * Adds a "Delete" command to this (topic) command set.
	 * <P>
	 * References checked: 9.2.2005 (2.0b5)
	 *
	 * @see		#addStandardCommands
	 */
	public void addDeleteTopicCommand(BaseTopic topic, Session session) {
		int state = as.deleteTopicIsAllowed(topic, session) ? COMMAND_STATE_DEFAULT : COMMAND_STATE_DISABLED;
		addDeleteTopicCommand(session, state);
	}

	public void addDeleteTopicCommand(Session session, int state) {
		addDeleteTopicCommand(as.string(ITEM_DELETE_TOPIC), state);
	}

	public void addDeleteTopicCommand(String label, int state) {
		addCommand(label, CMD_DELETE_TOPIC, FILESERVER_IMAGES_PATH, ICON_DELETE_TOPIC, state);
	}

	/**
	 * References checked: 17.2.2005 (2.0b5)
	 *
	 * @see		de.deepamehta.topics.ContainerTopic#contextCommands
	 */
	public void addDeleteTopicCommand(String label, String iconPath, String iconfile) {
		addCommand(label, CMD_DELETE_TOPIC, iconPath, iconfile, COMMAND_STATE_DEFAULT);		// ### permission check
	}

	// --- addDeleteAssociationCommand (3 forms) ---

	/**
	 * Adds a "Delete" command to this (association) command set.
	 * <P>
	 * References checked: 9.2.2005 (2.0b5)
	 *
	 * @see		de.deepamehta.assocs.LiveAssociation#contextCommands
	 */
	public void addDeleteAssociationCommand(BaseAssociation assoc, Session session) {
		int state = as.deleteAssociationIsAllowed(assoc, session) ? COMMAND_STATE_DEFAULT : COMMAND_STATE_DISABLED;
		addDeleteAssociationCommand(as.string(ITEM_DELETE_ASSOC), state);
	}

	public void addDeleteAssociationCommand(String label, int state) {
		addCommand(label, CMD_DELETE_ASSOC, FILESERVER_IMAGES_PATH, ICON_DELETE_ASSOC, state);
	}

	/**
	 * References checked: 17.2.2005 (2.0b5)
	 *
	 * @see		de.deepamehta.assocs.LiveAssociation#contextCommands
	 */
	public void addDeleteAssociationCommand(String label, String iconPath, String iconfile) {
		addCommand(label, CMD_DELETE_ASSOC, iconPath, iconfile, COMMAND_STATE_DEFAULT);
	}

	// ---

	/**
	 * @see		#addStandardCommands
	 */
	private void addRelationCommands(BaseTopic topic, Session session, CorporateDirectives directives) {
		// --- add command for every existing relation ---
		Vector relations = as.type(topic).getRelations();
		if (relations.size() > 0) {
			addSeparator();
		}
		Enumeration e = relations.elements();
		while (e.hasMoreElements()) {
			Relation rel = (Relation) e.nextElement();
			addRelationCommand(topic, rel, session, directives);
		}
	}

	private void addRelationCommand(BaseTopic topic, Relation rel, Session session, CorporateDirectives directives) {
		String relName = rel.name;
		String relTopicTypeID = rel.relTopicTypeID;
		String cardinality = rel.cardinality;
		String assocTypeID = rel.assocTypeID;
		//
		TypeTopic relTopicType = as.type(relTopicTypeID, 1);	// ### version=1
		// --- determine label of command group and basis state for commands ---
		String label;	// Note: "label" is not used if "relName" is set
		int state;
		if (cardinality.equals(CARDINALITY_ONE)) {
			label = relTopicType.getName();
			state = COMMAND_STATE_RADIOBUTTON;
		} else if (cardinality.equals(CARDINALITY_MANY)) {
			label = relTopicType.getPluralNaming();
			state = COMMAND_STATE_CHECKBOX;
		} else {
			throw new DeepaMehtaException("unexpected cardinality: \"" + cardinality + "\"");
		}
		// --- create submenu and put all instances of the related topic type
		Commands topicGroup = addCommandGroup(as.string(ITEM_ASSIGN_TOPIC, !relName.equals("") ? relName : label),
			FILESERVER_ICONS_PATH, relTopicType.getIconfile());
		if (!session.isDemo()) {
			if (rel.webForm.equals(WEB_FORM_TOPIC_SELECTOR)) {
				Vector topics = as.cm.getTopics(relTopicTypeID);	// ### can be very big
				Vector selectedTopics = as.getRelatedTopics(topic.getID(), assocTypeID, relTopicTypeID, 2,
					false, true);	// ordered=false, emptyAllowed=true ### relTopicPos=2 hardcoded
				Vector selectedTopicIDs = DeepaMehtaUtils.topicIDs(selectedTopics);
				//
				String cmd = CMD_ASSIGN_TOPIC + ":" + assocTypeID + ":" + cardinality;
				addTopicCommands(topicGroup, topics, cmd, state, selectedTopicIDs, null, session, directives);
				if (topics.size() > 0) {
					topicGroup.addSeparator();
				}
			}
			// add additional "Create ..." command
			String item = as.string(ITEM_ASSIGN_NEW_TOPIC, relTopicType.getName());
			String cmd = CMD_ASSIGN_NEW_TOPIC + ":" + relTopicTypeID + ":" + assocTypeID + ":" + cardinality;
			topicGroup.addCommand(item, cmd, FILESERVER_IMAGES_PATH, ICON_NEW_TOPIC);
		}
	}

	// ---

	public void addHelpCommand(LiveTopic topic, Session session) {
		TypeTopic type = as.type(topic);
		String typeName = type.getName();
		if (typeName.equals("")) {
			return;
		}
		String query = type.getProperty(PROPERTY_TYPE_DESCRIPTION_QUERY);
		if (query.equals("")) {
			query = as.string(ITEM_SHOW_HELP, typeName);
		}
		addCommand(query, CMD_SHOW_HELP + ":" + type.getID(), FILESERVER_IMAGES_PATH, ICON_SHOW_HELP);	// ### encapsulate title
	}

	public void addHelpCommand(LiveAssociation assoc, Session session) {
		AssociationTypeTopic type = as.type(assoc);
		String typeName = type.getName();
		if (typeName.equals("")) {
			return;
		}
		String query = type.getProperty(PROPERTY_TYPE_DESCRIPTION_QUERY);
		if (query.equals("")) {
			query = as.string(ITEM_SHOW_HELP, typeName);
		}
		addCommand(query, CMD_SHOW_HELP + ":" + type.getID(), FILESERVER_IMAGES_PATH, ICON_SHOW_HELP);	// ### encapsulate title
	}

	// ---

	public void addSearchInternetCommand(LiveTopic topic, Session session) {
		addCommand(as.string(ITEM_SEARCH_INTERNET, topic.getName()), CMD_SEARCH_INTERNET, FILESERVER_IMAGES_PATH, ICON_SEARCH_INTERNET);	// ### encapsulate title
	}

	// ---

	public void addOptionsMenuCommands(String propName, String propLabel, String iconfile,
														String propValue, Vector options, Session session) {
		Commands cmdGroup = addCommandGroup(as.string(ITEM_SET_PROPERTY, propLabel),
			FILESERVER_IMAGES_PATH, iconfile);
		Enumeration e = options.elements();
		PresentableTopic option;
		String value, optionIconfile, cmd;
		int cmdState;
		while (e.hasMoreElements()) {
			option = (PresentableTopic) e.nextElement();
			value = option.getName();
			optionIconfile = option.getAppearanceParam();
			cmd = CMD_SET_PROPERTY + ":" + propName + ":" + value;
			cmdState = COMMAND_STATE_RADIOBUTTON + (propValue.equals(value) ?
				COMMAND_STATE_SELECTED : COMMAND_STATE_DEFAULT);
			cmdGroup.addCommand(value, cmd, FILESERVER_ICONS_PATH, optionIconfile,
				cmdState);
		}
	}

	public void addSwitchCommand(String propName, String propLabel, String iconfile,
																	String propValue, Session session) {
		// Note: a switch is represented as 2 radiobuttons
		Commands cmdGroup = addCommandGroup(as.string(ITEM_SET_PROPERTY, propLabel),
			FILESERVER_IMAGES_PATH, iconfile);
		String cmd;
		int cmdState;
		// on
		cmd = CMD_SET_PROPERTY + ":" + propName + ":" + SWITCH_ON;
		cmdState = COMMAND_STATE_RADIOBUTTON + (propValue.equals(SWITCH_ON) ?
				COMMAND_STATE_SELECTED : COMMAND_STATE_DEFAULT);
		cmdGroup.addCommand(SWITCH_ON, cmd, "", "", cmdState);
		// off
		cmd = CMD_SET_PROPERTY + ":" + propName + ":" + SWITCH_OFF;
		cmdState = COMMAND_STATE_RADIOBUTTON + (propValue.equals(SWITCH_OFF) ?
				COMMAND_STATE_SELECTED : COMMAND_STATE_DEFAULT);
		cmdGroup.addCommand(SWITCH_OFF, cmd, "", "", cmdState);
	}

	// --- addTopicCommands (3 forms) ---

	/**
	 * Adds topic commands to the specified command group of this (topic) command set.
	 * <P>
	 * This method is useful for creating preference submenus, e.g. the submenus for
	 * the export preferences or the Kompetenzstern preferences are build by help
	 * of this method.
	 * <P>
	 * References checked: 14.12.2001 (2.0a14-pre4)<BR>
	 * Documentation updated: 9.10.2001 (2.0a12)
	 *
	 * @param	topics			vector of topics (BaseTopic) resp. topic IDs (String)
	 * @param	selectedTopic	currently selected topic, may be <CODE>null</CODE>
	 * @param	title			appears before commands, may be <CODE>null</CODE>
	 *
	 * @see		#addExportCommand
	 */
	public void addTopicCommands(Commands cmdGroup, Vector topics, String command,
								int commandState, BaseTopic selectedTopic, String title,
								Session session, CorporateDirectives directives) {
		String selectedTopicID = selectedTopic != null ? selectedTopic.getID() : null;
		addTopicCommands(cmdGroup, topics, command, commandState, selectedTopicID, title, session, directives);
	}

	public void addTopicCommands(Commands cmdGroup, Vector topics, String command,
								int commandState, String selectedTopicID, String title,
								Session session, CorporateDirectives directives) {
		Vector selectedTopicIDs = new Vector();
		if (selectedTopicID != null) {
			selectedTopicIDs.addElement(selectedTopicID);
		}
		addTopicCommands(cmdGroup, topics, command, commandState, selectedTopicIDs, title, session, directives);
	}

	/**
	 * @param	topics				vector of topics (BaseTopic) resp. topic IDs (String)
	 * @param	selectedTopicIDs	IDs of currently selected topics,
	 *								may be empty but not <CODE>null</CODE>
	 */
	public void addTopicCommands(Commands cmdGroup, Vector topics, String command,
								int commandState, Vector selectedTopicIDs, String title,
								Session session, CorporateDirectives directives) {
		if (title != null) {
			cmdGroup.addCommand(title, "dummy", "", "", COMMAND_STATE_DISABLED);
		}
		// add command for every topic
		Enumeration e = topics.elements();
		while (e.hasMoreElements()) {
			Object o = e.nextElement();
			String topicID = o instanceof String ? (String) o : ((BaseTopic) o).getID();
			LiveTopic topic = as.getLiveTopic(topicID, 1, session, directives);
			String topicName = topic.getName();
			boolean isSelected = selectedTopicIDs.contains(topicID);
			// ### Note: a radio button that is selected can't be selected once again, it is disabled
			// ### boolean disable = commandState == COMMAND_STATE_RADIOBUTTON && isSelected;
			int cmdState = commandState + (isSelected ? COMMAND_STATE_SELECTED : 0);
			// add command
			cmdGroup.addCommand(topicName, command + ":" + topicID, FILESERVER_ICONS_PATH, topic.getIconfile(),
				cmdState);
		}
	}

	// ---

	/**
	 * Adds commands for all topic types which are accessible by the user to the specified command group.
	 * <P>
	 * Used for "Retype", "Create", "Search" and workspace's "Assign Topic Type" commands.
	 * <P>
	 * References checked: 19.7.2003 (2.0b2)
	 *
	 * @see		#addCreateCommands
	 * @see		#addSearchByTopictypeCommand
	 * @see		#addRetypeTopicCommand
	 * @see		de.deepamehta.topics.WorkspaceTopic#contextCommands	false	PERMISSION_CREATE
	 */
	public void addTopicTypeCommands(Commands commandsGroup, String command, String permissionMode,
										boolean usePluralName, Session session, CorporateDirectives directives) {
		String userID = session.getUserID();
		BaseTopic defaultWorkspace = null;
		String defaultWorkspaceID = null;
		Vector types;
		// --- add default types ---
		defaultWorkspace = as.getUsersDefaultWorkspace(userID, directives);
		if (defaultWorkspace != null) {
			defaultWorkspaceID = defaultWorkspace.getID();
			types = as.getTopicTypes(defaultWorkspaceID, permissionMode);
			addTopicTypeCommands(commandsGroup, types, command, usePluralName);
		}
		// --- add user types ---
		types = as.getTopicTypes(userID, permissionMode);
		if (!types.isEmpty()) {
			if (!commandsGroup.isEmpty()) {
				commandsGroup.addSeparator();
			}
			addTopicTypeCommands(commandsGroup, types, command, usePluralName);
		}
		// --- add workgroup types ---
		Vector workgroups = as.getWorkgroups(userID);
		Enumeration e = workgroups.elements();
		boolean first = true;
		while (e.hasMoreElements()) {
			BaseTopic workgroup = (BaseTopic) e.nextElement();
			// skip default workspace
			if (workgroup.getID().equals(defaultWorkspaceID)) {
				continue;
			}
			//
			types = as.getTopicTypes(workgroup.getID(), permissionMode);
			if (!types.isEmpty()) {
				if (first) {
					if (!commandsGroup.isEmpty()) {
						commandsGroup.addSeparator();
					}
					first = false;
				}
				// create sub menu
				Commands cmdGroup = commandsGroup.addCommandGroup(workgroup.getName(),
					FILESERVER_ICONS_PATH, as.getLiveTopic(workgroup).getIconfile());
				addTopicTypeCommands(cmdGroup, types, command, usePluralName);
			}
		}
	}

	/**
	 * Adds commands for all association types which are accessible by the user to the specified command group.
	 * <P>
	 * Used for "Retype" and workspace's "Assign Association Type" commands.
	 * <P>
	 * References checked: 9.2.2005 (2.0b5)
	 *
	 * @see		#addRetypeAssociationCommand
	 * @see		de.deepamehta.topics.WorkspaceTopic#contextCommands
	 */
	public void addAssocTypeCommands(Commands commandsGroup, String command, String permissionMode, boolean usePluralName,
																			Session session, CorporateDirectives directives) {
		String userID = session.getUserID();
		BaseTopic defaultWorkspace = null;
		String defaultWorkspaceID = null;
		Vector types;
		// --- add default types ---
		defaultWorkspace = as.getUsersDefaultWorkspace(userID, directives);
		if (defaultWorkspace != null) {
			defaultWorkspaceID = defaultWorkspace.getID();
			types = as.getAssociationTypes(defaultWorkspaceID, permissionMode);
			addAssocTypeCommands(commandsGroup, types, command, usePluralName);
		}
		// --- add user types ---
		types = as.getAssociationTypes(userID, permissionMode);
		if (!types.isEmpty()) {
			// ### compare to latter
			commandsGroup.addSeparator();
			// add association type commands
			addAssocTypeCommands(commandsGroup, types, command, usePluralName);
		}
		// --- add workgroup types ---
		Vector workgroups = as.getWorkgroups(userID);
		Enumeration e = workgroups.elements();
		boolean first = true;
		while (e.hasMoreElements()) {
			BaseTopic workgroup = (BaseTopic) e.nextElement();
			// skip default workspace
			if (workgroup.getID().equals(defaultWorkspaceID)) {
				continue;
			}
			types = as.getAssociationTypes(workgroup.getID(), permissionMode);
			if (!types.isEmpty()) {
				if (first) {
					if (!commandsGroup.isEmpty()) {
						commandsGroup.addSeparator();
					}
					first = false;
				}
				// create sub menu
				Commands cmdGroup = commandsGroup.addCommandGroup(workgroup.getName(),
					FILESERVER_ICONS_PATH, as.getLiveTopic(workgroup).getIconfile());
				addAssocTypeCommands(cmdGroup, types, command, usePluralName);
			}
		}
	}

	// ---

	// Note: there is another private addWorkspaceTopicTypeCommands(3) method

	/**
	 * References checked: 26.9.2002 (2.0a16-pre4)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#viewCommands
	 */
	public void addWorkspaceTopicTypeCommands(Session session, CorporateDirectives directives) {
		String userID = session.getUserID();
		String defaultWorkspaceID = null;
		Vector types;
		// --- add default types ---
		BaseTopic defaultWorkspace = as.getUsersDefaultWorkspace(userID, directives);
		if (defaultWorkspace != null) {
			defaultWorkspaceID = defaultWorkspace.getID();
			types = as.getTopicTypes(defaultWorkspaceID, PERMISSION_CREATE_IN_WORKSPACE);
			addWorkspaceTopicTypeCommands(types, session, directives);
		}
		// --- add workgroup types ---
		Enumeration e = as.getWorkgroups(userID).elements();
		while (e.hasMoreElements()) {
			String workgroupID = ((BaseTopic) e.nextElement()).getID();
			if (workgroupID.equals(defaultWorkspaceID)) {
				continue;
			}
			types = as.getTopicTypes(workgroupID, PERMISSION_CREATE_IN_WORKSPACE);
			addWorkspaceTopicTypeCommands(types, session, directives);
		}
		// --- add user types ---
		types = as.getTopicTypes(userID, PERMISSION_CREATE_IN_WORKSPACE);
		addWorkspaceTopicTypeCommands(types, session, directives);
	}

	// ---

	/**
	 * Used for "Navigate by Topic", "Navigate by Association" and "Hide all" commands.
	 *
	 * @param	types		the types to add as typeID/count pairs
	 * @param	heading		can be <CODE>null</CODE>
	 *
	 * @return	<CODE>true</CODE> if at least one command has been added
	 *
	 * @see		#addHideAllCommands
	 * @see		#addNavigationCommands
	 */
	private boolean addTypeCommands(Commands cmdGroup, Hashtable types, String command, String heading) {
		boolean added = false;
		//
		Enumeration e = types.keys();
		while (e.hasMoreElements()) {
			String typeID = (String) e.nextElement();
			try {
				TypeTopic type = as.type(typeID, 1);
				if (!type.isSearchType()) {
					if (!added) {
						if (heading != null) {
							if (!cmdGroup.isEmpty()) {
								cmdGroup.addSeparator();
							}
							cmdGroup.addCommand(heading, "dummy", "", "", COMMAND_STATE_DISABLED);
						}
						added = true;
					}
					String count = (String) types.get(typeID);
					cmdGroup.addCommand(type.getPluralNaming() + " (" + count + ")",
						command + ":" + typeID, FILESERVER_ICONS_PATH, type.getIconfile());
				}
			} catch (DeepaMehtaException ex) {
				System.out.println("*** CorporateCommands.addTypeCommands(): command \"" + command + "\" cant be added for type \"" + typeID + "\": " + ex);
			}
		}
		//
		return added;
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @see		#addTopicTypeCommands
	 */
	private void addBuildmodeTopicTypeCommands(Commands cmdGroup, String command,
															boolean usePluralName) {
		addTypeCommand(cmdGroup, TOPICTYPE_TOPICTYPE, command, usePluralName);
		addTypeCommand(cmdGroup, TOPICTYPE_ASSOCTYPE, command, usePluralName);
		addTypeCommand(cmdGroup, TOPICTYPE_PROPERTY, command, usePluralName);
		addTypeCommand(cmdGroup, TOPICTYPE_PROPERTY_VALUE, command, usePluralName);
		addTypeCommand(cmdGroup, "tt-datasource", command, usePluralName);
	}

	// --- addTopicPropertyCommand (2 forms) ---

	/**
	 * @see		#addTopicPropertyCommands
	 */
	/* ### private boolean addTopicPropertyCommand(PropertyDefinition propDef, boolean enabled,
																			LiveTopic topic, Session session) {
		boolean added = false;
		//
		String propName = propDef.getPropertyName();
		String propLabel = propDef.getPropertyLabel();
		String visualization = propDef.getVisualization();
		String editIconfile = propDef.getEditIconfile();
		//
		if (visualization.equals(VISUAL_FIELD)) {
			addTopicPropertyCommand(propName, propLabel, editIconfile, enabled, false, false, session);
			added = true;
		} else if (visualization.equals(VISUAL_AREA)) {
			addTopicPropertyCommand(propName, propLabel, editIconfile, enabled, false, true, session);
			added = true;
		} else if (visualization.equals(VISUAL_TEXT_EDITOR)) {
			addTopicPropertyCommand(propName, propLabel, editIconfile, enabled, true, true, session);
			added = true;
		} else if (visualization.equals(VISUAL_CHOICE) ||
				   visualization.equals(VISUAL_RADIOBUTTONS)) {
			String value = topic.getProperty(propName);
			addOptionsMenuCommands(propName, propLabel, editIconfile, value, propDef.getOptions(), session);
			added = true;
		} else if (visualization.equals(VISUAL_SWITCH)) {
			String value = topic.getProperty(propName);
			addSwitchCommand(propName, propLabel, editIconfile, value, session);
			added = true;
		} else if (visualization.equals(VISUAL_FILE_CHOOSER)) {
			addFileCommand(propName, propLabel, "chooseFile.gif" // ### editIconfile //, session);
			added = true;
		} else {
			// ### System.out.println(">>> no command for property \"" + propName + "\" (" +
				"visualization: \"" + visualization + "\")");
		}
		// Note: hidden properties (VISUAL_HIDDEN) are not respected
		// ### pending: VISUAL_DATE_CHOOSER
		// ### pending: VISUAL_TIME_CHOOSER
		// ### pending: VISUAL_PASSWORD_FIELD
		//
		return added;
	} */

	/**
	 * @see		#addTopicPropertyCommand	(above)
	 */
	/* ### private void addTopicPropertyCommand(String propName, String propLabel, String iconfile,
									boolean editable, boolean styled, boolean multiline, Session session) {
		int item = editable ? ITEM_EDIT_PROPERTY : ITEM_VIEW_PROPERTY;
		String cmd = editable ? CMD_EDIT_TOPIC_PROPERTY : CMD_VIEW_TOPIC_PROPERTY;
		String param = ":" + propName + ":" + propLabel + ":" + styled + ":" + multiline;
		addCommand(as.string(item, propLabel), cmd + param, FILESERVER_IMAGES_PATH, iconfile);
	} */

	// ---

	/**
	 * @see		#addAssocPropertyCommands
	 */
	private void addAssociationPropertyCommand(String propName, String iconfile,
									boolean editable, boolean styled, boolean multiline, Session session) {
		int item = editable ? ITEM_EDIT_PROPERTY : ITEM_VIEW_PROPERTY;
		String cmd = editable ? CMD_EDIT_ASSOC_PROPERTY : CMD_VIEW_ASSOC_PROPERTY;
		String param = ":" + propName + ":" + styled + ":" + multiline;
		addCommand(as.string(item, propName), cmd + param, FILESERVER_IMAGES_PATH, iconfile);
	}

	// ---

	/**
	 * @param	types		types to add as vector of {@link de.deepamehta.BaseTopic} ### or as vector of typeIDs (Strings)
	 *
	 * @see		#addTopicTypeCommands(int groupID, String command, boolean usePluralName,
	 *			Session session, String permissionMode, String viewmode)
	 */
	public void addTopicTypeCommands(Commands cmdGroup, Vector types, String command, boolean usePluralName) {
		// --- add "generic" type first ---
		/* ### boolean add;
		if (types.size() > 0 && types.firstElement() instanceof String) {
			add = types.contains(TOPICTYPE_TOPIC);
		} else {
			add = DeepaMehtaServiceUtils.findIndexByID(types, TOPICTYPE_TOPIC) != -1;
		} */
		if (DeepaMehtaServiceUtils.findIndexByID(types, TOPICTYPE_TOPIC) != -1) {
			addTypeCommand(cmdGroup, TOPICTYPE_TOPIC, command, usePluralName);
		}
		// --- add other types ---
		Enumeration e = types.elements();
		while (e.hasMoreElements()) {
			String typeID = ((BaseTopic) e.nextElement()).getID();
			TopicTypeTopic type = (TopicTypeTopic) as.type(typeID, 1);
			// filter "generic" and container-types
			if (!typeID.equals(TOPICTYPE_TOPIC) && !type.isSearchType()) {
				addTypeCommand(cmdGroup, typeID, command, usePluralName);
			}
		}
	}

	/**
	 * @see		#addAssocTypeCommands(int groupID, String command, boolean usePluralName,
	 *											Session session, String viewmode)
	 */
	private void addAssocTypeCommands(Commands cmdGroup, Vector types, String command, boolean usePluralName) {
		// --- add "generic" type first ---
		if (DeepaMehtaServiceUtils.findIndexByID(types, "at-generic") != -1) {
			addTypeCommand(cmdGroup, "at-generic", command, usePluralName);
		}
		// --- add other types ---
		Enumeration e = types.elements();
		while (e.hasMoreElements()) {
			String typeID = ((BaseTopic) e.nextElement()).getID();
			// ### type = as.type(typeID, 1);
			// filter "generic"
			if (!typeID.equals("at-generic")) {
				addTypeCommand(cmdGroup, typeID, command, usePluralName);
			}
		}
	}

	// ---

	/**
	 * @see		#addWorkspaceTopicTypeCommands(Session session, CorporateDirectives directives)
	 */
	private void addWorkspaceTopicTypeCommands(Vector types, Session session, CorporateDirectives directives) {
		Enumeration e = types.elements();
		while (e.hasMoreElements()) {
			String typeID = ((BaseTopic) e.nextElement()).getID();
			System.out.println(">>> CorporateCommands.addWorkspaceTopicTypeCommands(): typeID=\"" + typeID + "\"");
			TypeTopic type = as.type(typeID, 1);	// ### type version is 1
			CorporateCommands commands;
			// --- trigger workspaceCommands() hook ---
			Class[] paramTypes = {TypeTopic.class, ApplicationService.class, Session.class, CorporateDirectives.class};
			Object[] paramValues = {type, as, session, directives};
			try {
				commands = (CorporateCommands) as.triggerStaticHook(type.getImplementingClass(),
					"workspaceCommands", paramTypes, paramValues, true);	// throwIfNoSuchHookExists=true
			} catch (DeepaMehtaException e2) {
				// Note: the type may have an custom implementation but no static workspaceCommands() hook
				// defined, in this case the default implementation in LiveTopic must be triggered
				commands = LiveTopic.workspaceCommands(type, as, session, directives);
			}
			//
			if (commands != null && !commands.isEmpty()) {
				add(commands);
				addSeparator();
			}
		}
	}

	// ---

	/**
	 * References checked: 7.8.2001 (2.0a11)
	 *
	 * @see		addBuildmodeTopicTypeCommands
	 * @see		addTopicTypeCommands
	 * @see		addAssocTypeCommands
	 */
	private void addTypeCommand(Commands cmdGroup, String typeID, String command,
															boolean usePluralName) {
		TypeTopic type = as.type(typeID, 1);
		String cmd = command + ":" + type.getID();
		cmdGroup.addCommand(usePluralName ? type.getPluralNaming() : type.getName(), cmd,
			FILESERVER_ICONS_PATH, type.getIconfile());
	}
}
