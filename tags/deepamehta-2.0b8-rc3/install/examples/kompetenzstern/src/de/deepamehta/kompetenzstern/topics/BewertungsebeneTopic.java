package de.deepamehta.kompetenzstern.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.kompetenzstern.KS;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDetail;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.LiveTopic;
import de.deepamehta.topics.TypeTopic;

import java.util.StringTokenizer;
import java.util.Vector;



/**
 * Part of {@link KompetenzsternTopic Kompetenzstern} application.
 * <P>
 * <HR>
 * Last functional change: 3.2.2008 (2.0b8)<BR>
 * Last documentation update: 15.10.2001 (2.0a13-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class BewertungsebeneTopic extends LiveTopic implements KS {



	// *******************
	// *** Constructor ***
	// *******************



	public BewertungsebeneTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	public CorporateCommands contextCommands(String topicmapID, String viewmode,
								Session session, CorporateDirectives directives) {
		CorporateCommands commands = new CorporateCommands(as);
		//
		boolean hasRelatedDocuments = !getRelatedDocuments().isEmpty();
		boolean insideTemplate = as.getLiveTopic(topicmapID, 1).getType().equals(
			TOPICTYPE_KOMPETENZSTERN_TEMPLATE);
		//
		/* ### if (insideTemplate) {
			commands.addRenameTopicCommand(this);
			KompetenzsternTopic.addSetOrderCommand(this, commands, directives, as);
			commands.addSeparator();
			moreHiddenProps.addElement(PROPERTY_DESCRIPTION);
		} */
		// ### compare to KriteriumTopic.contextCommands()
		/* ### if (!session.getUserPreferences().showSidebar) {
			Vector moreHiddenProps = new Vector();
			if (insideTemplate) {
				moreHiddenProps.addElement(PROPERTY_DESCRIPTION);
			} else {
				moreHiddenProps.addElement(PROPERTY_NAME);
			}
			if (commands.addTopicPropertyCommands(this, moreHiddenProps, session)) {
				commands.addSeparator();
			}
		} */
		//
		if (insideTemplate) {
			commands.addDeleteTopicCommand(this, session);
		} else {
			KompetenzsternTopic.addDocumentCommands(hasRelatedDocuments, commands);
		}
		//
		return commands;
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeCommand(String command, Session session,
															String topicmapID, String viewmode) {
		// ### compare to KriteriumTopic
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
        String cmd = st.nextToken();
		if (KompetenzsternTopic.executeOrderCommand(this, command, directives,
        	as)) {		// do nothing
		} else if (KompetenzsternTopic.executeDocumentCommand(this, command, session, directives)) {
			// do nothing
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		//
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public static Vector hiddenProperties(TypeTopic type) {
		Vector props = new Vector();
		props.addElement(PROPERTY_ICON);
		return props;
	}

	/**
	 * @see		de.deepamehta.topics.TypeTopic#makeTypeDefinition
	 */
	/* ### public static void propertyLabel(PropertyDefinition propDef, Session session) {
		String propName = propDef.getPropertyName();
		if (propName.equals(PROPERTY_DESCRIPTION)) {
			propDef.setPropertyLabel(PROPERTY_BESCHREIBUNG);
		}
	} */



	// -----------------------------
	// --- Handling Topic Detail ---
	// -----------------------------



	/**
	 * @see		de.deepamehta.service.ApplicationService#processTopicDetail
	 */
	public CorporateDirectives processDetailHook(CorporateDetail detail,
							Session session, String topicmapID, String viewmode) {
		String command = detail.getCommand();
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_SET_ORDER)) {
			String assocID = st.nextToken();
			String order = (String) detail.getParam1();
			cm.setAssociationData(assocID, 1, PROPERTY_ORDINAL_NUMBER, order);
			return new CorporateDirectives();
		} else {
			return super.processDetailHook(detail, session, topicmapID, viewmode);
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * ### there is a copy in KriteriumTopic
	 */
    private Vector getRelatedDocuments() {
        return as.getRelatedTopics(getID(),
			SEMANTIC_RELATED_DOCUMENT, TOPICTYPE_RELATED_DOCUMENT, 2, false, true);
    }
}
