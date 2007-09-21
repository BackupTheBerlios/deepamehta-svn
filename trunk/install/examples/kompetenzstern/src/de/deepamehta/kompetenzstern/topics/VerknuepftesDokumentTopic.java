package de.deepamehta.kompetenzstern.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.kompetenzstern.KS;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.DocumentTopic;
import de.deepamehta.topics.TypeTopic;

import java.util.Vector;



/**
 * Part of {@link KompetenzsternTopic Kompetenzstern} application.
 * <P>
 * A document that is related to a <I>Bewertungskriterium</I> or a
 * <I>Bewertungsebene</I> of a <I>Kompetenzstern</I>.
 * <P>
 * <HR>
 * Last functional change: 10.6.2003 (2.0b1)<BR>
 * Last documentation update: 12.11.2001 (2.0a13-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class VerknuepftesDokumentTopic extends DocumentTopic implements KS {



	// *******************
	// *** Constructor ***
	// *******************



	public VerknuepftesDokumentTopic(BaseTopic topic, ApplicationService as) {
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
		commands.addHideTopicCommand(session);
		commands.addSeparator();
		//
		// ### commands.addRenameTopicCommand(this);
		/* ### if (!session.getUserPreferences().showSidebar) {
			if (commands.addTopicPropertyCommands(this, session)) {
				commands.addSeparator();
			}
		} */
		//
		commands.addDeleteTopicCommand(this, session);
		//
		return commands;
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
	public static void propertyLabel(PropertyDefinition propDef, ApplicationService as, Session session) {
		if (as.getLanguage() == LANGUAGE_GERMAN) {
			String propName = propDef.getPropertyName();
			if (propName.equals(PROPERTY_FILE)) {
				propDef.setPropertyLabel(PROPERTY_DATEI);
			} /* ### else if (propName.equals(PROPERTY_DESCRIPTION)) {
				propDef.setPropertyLabel(PROPERTY_BESCHREIBUNG);
			} */
		}
	}
}
