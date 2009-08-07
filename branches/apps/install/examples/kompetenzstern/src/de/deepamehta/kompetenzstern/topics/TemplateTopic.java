package de.deepamehta.kompetenzstern.topics;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.Commands;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.kompetenzstern.KS;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.TopicMapTopic;

import java.util.StringTokenizer;
import java.util.Vector;



/**
 * Part of {@link KompetenzsternTopic Kompetenzstern} application.
 * <P>
 * <HR>
 * Last functional change: 11.9.2007 (2.0b8)<BR>
 * Last documentation update: 11.11.2001 (2.0a13-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class TemplateTopic extends TopicMapTopic implements KS {



	// *******************
	// *** Constructor ***
	// *******************



	public TemplateTopic(BaseTopic topic, ApplicationService as) {
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
		// ### not if evoked through personalization
		cm.createViewTopic(getID(), 1, VIEWMODE_USE, getID(), 1, 300, 80, false);
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
       	// --- "Kriterium erzeugen", "Bewertungsebene erzeugen" und "Kompetenzstern-Typ festlegen" ---
		if (topicmapID.equals(getID())) {		// ### was (editorContext == EDITOR_CONTEXT_VIEW)
        	commands.addCommand(ITEM_ASSIGN_NEW_CRITERION, CMD_ASSIGN_NEW_CRITERION, FILESERVER_IMAGES_PATH, ICON_ASSIGN_NEW_CRITERION);
        	commands.addCommand(ITEM_ASSIGN_NEW_BEWERTUNGSEBENE, CMD_ASSIGN_NEW_BEWERTUNGSEBENE, FILESERVER_IMAGES_PATH, ICON_ASSIGN_NEW_BEWERTUNGSEBENE);
        	//
			Commands group = commands.addCommandGroup(ITEM_ASSIGN_COMPETENCE_STAR_TYPE, FILESERVER_ICONS_PATH, ICON_SHOW_COMPETENCE_STAR);
			Vector typeIDs = as.type(TOPICTYPE_KOMPETENZSTERN, 1).getSubtypeIDs();
			commands.addTopicCommands(group, typeIDs, CMD_ASSIGN_COMPETENCE_STAR_TYPE, COMMAND_STATE_RADIOBUTTON,
				getKompetenzsternTypeID(), null, session, directives);	// title=null
        } else {
			commands.addNavigationCommands(this, editorContext, session);	// EDITOR_CONTEXT_VIEW only
		}
		// --- publish ---
		if (editorContext == EDITOR_CONTEXT_PERSONAL) {
			commands.addPublishCommand(getID(), session, directives);
		}
		// --- export ---
   	    commands.addSeparator();
		commands.addExportCommand(session, directives);
		// --- standard commands ---
		/* ### if (!session.getUserPreferences().showSidebar) {
			// ### commands.addRenameTopicCommand(this);
			commands.addSeparator();
			commands.addTopicPropertyCommands(this, session);	// ### return value
		} */
		//
		int cmdState = getID().equals(TEMPLATE_STANDARD) ? COMMAND_STATE_DISABLED : COMMAND_STATE_DEFAULT;
		if (editorContext == EDITOR_CONTEXT_VIEW && !topicmapID.equals(getID())) {
			commands.addSeparator();
			commands.addHideTopicCommand(session);
			commands.addRetypeTopicCommand(this, session, directives);
			commands.addDeleteTopicCommand(session, cmdState);
		}
		// --- delete ---
		if (editorContext == EDITOR_CONTEXT_PERSONAL) {
			// Note: the standard template can't be deleted ### can't happen anymore
			commands.addSeparator();
			commands.addDeleteTopicCommand(session, cmdState);
		}
		//
        return commands;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#showViewMenu
	 */
	/* ### public CorporateCommands viewCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		//
		int editorContext = as.editorContext(topicmapID);
		if (editorContext == EDITOR_CONTEXT_PERSONAL) {
			commands.addWorkspaceTopicTypeCommands(session, directives);
			// ### commands.addImportCommand();
		} else if (editorContext == EDITOR_CONTEXT_VIEW) {
			commands.addSearchByTopictypeCommand(viewmode, session, directives);
			commands.addSeparator();
			commands.addCreateCommands(viewmode, session, directives);
			commands.addSeparator();
			//
			commands.addHideAllCommands(topicmapID, viewmode, session);
			commands.addCloseCommand(session);
			commands.addSeparator();
			//
			commands.addPublishCommand(getID(), session, directives);
			commands.addSeparator();
			//
			commands.addExportCommand(session, directives);
			commands.addSeparator();
			//
			if (!session.getUserPreferences().showSidebar) {
				commands.addTopicPropertyCommands(this, session);
				commands.addSeparator();
			}
			//
			commands.addHelpCommand(this, session);
		}
		//
		return commands;
	} */

	/**
	 * @see		de.deepamehta.service.CorporateCommands#addWorkspaceTopicTypeCommands(Vector types, Session session, CorporateDirectives directives)
	 */
	/* ### public static CorporateCommands workspaceCommands(TypeTopic type, ApplicationService as,
													Session session, CorporateDirectives directives)
													throws DeepaMehtaException {
		// --- add "Create Kompetenzstern-Template" command ---
		// Note: only the "admin" user can create Kompetenzstern-Templates ###
		if (as.hasRole(session.getUserID(), WORKSPACE_KOMPETENZSTERN, PROPERTY_TEMPLATE_BUILDER)) {
			return LiveTopic.workspaceCommands(type, as, session, directives);
		}
		return new CorporateCommands(as);
	} */



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
        String cmd = st.nextToken();
		if (cmd.equals(CMD_ASSIGN_NEW_CRITERION)) {
			createChildTopic(TOPICTYPE_BEWERTUNGS_KRITERIUM, SEMANTIC_STERN_COMPOSITION, session, directives);
		} else if (cmd.equals(CMD_ASSIGN_NEW_BEWERTUNGSEBENE)) {
			createChildTopic(TOPICTYPE_BEWERTUNGS_EBENE, SEMANTIC_STERN_COMPOSITION, session, directives);
		} else if (cmd.equals(CMD_ASSIGN_COMPETENCE_STAR_TYPE)) {
			String starTypeID = st.nextToken();
			setKompetenzsternTypeID(starTypeID, directives);
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		//
		return directives;
	}



	// ***********************
	// *** Utility Methods ***
	// ***********************



	Vector getKriterien() throws DeepaMehtaException {
		return as.getRelatedTopics(getID(), SEMANTIC_STERN_COMPOSITION, TOPICTYPE_BEWERTUNGS_KRITERIUM,
			2, true, false);	// ordered=true, emptyAllowed=false
	}

	public String getKompetenzsternTypeID() throws DeepaMehtaException {
		BaseTopic type = as.getRelatedTopic(getID(), SEMANTIC_STERN_TYPE, 2, true);		// emptyAllowed=true
		// default type
		if (type == null) {
			return TOPICTYPE_KOMPETENZSTERN;
		}
		// error check 1
		if (!type.getType().equals(TOPICTYPE_TOPICTYPE)) {
			throw new DeepaMehtaException("association doesn't lead to a topic type, but to a \"" + type.getType() + "\"");
		}
		// error check 2
		if (!as.type(type.getID(), 1).hasSupertype(TOPICTYPE_KOMPETENZSTERN)) {
			throw new DeepaMehtaException("association doesn't lead to a Kompetenzstern subtype, but to a \"" + type.getType() + "\"");
		}
		//
		return type.getID();
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	private void setKompetenzsternTypeID(String starTypeID, CorporateDirectives directives) {
		// ### compare to KompetenzsternTopic.setPreference()
		// ### compare to TopicMapTopic.setExportFormat()
		// remove existing setting
		BaseAssociation assoc = cm.getAssociation(SEMANTIC_STERN_TYPE, getID(), getKompetenzsternTypeID());
		if (assoc != null) {
			as.deleteAssociation(assoc);
		} else {
			System.out.println("*** TemplateTopic.setKompetenzsternTypeID(): Kompetenzstern-Typ of " + this +
				" not set -- now set to \"" + starTypeID + "\"");
		}
		// add new preference
		cm.createAssociation(as.getNewAssociationID(), 1, SEMANTIC_STERN_TYPE, 1, getID(), 1, starTypeID, 1);
	}
}
