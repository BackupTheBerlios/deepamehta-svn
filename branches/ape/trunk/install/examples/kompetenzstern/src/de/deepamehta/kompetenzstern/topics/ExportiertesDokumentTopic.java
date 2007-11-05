package de.deepamehta.kompetenzstern.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateCommands;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.DocumentTopic;



/**
 * Part of {@link KompetenzsternTopic Kompetenzstern} application.
 * <P>
 * A document resulted from export of a <I>Kompetenzstern</I> or <I>Kompetenzstern-Template</I>.
 * <P>
 * <HR>
 * Last functional change: 10.6.2003 (2.0b1)<BR>
 * Last documentation update: 12.11.2001 (2.0a13-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class ExportiertesDokumentTopic extends DocumentTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public ExportiertesDokumentTopic(BaseTopic topic, ApplicationService as) {
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
		commands.addDeleteTopicCommand(this, session);
		return commands;
	}
}
