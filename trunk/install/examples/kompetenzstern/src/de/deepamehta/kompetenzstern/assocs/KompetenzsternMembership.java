package de.deepamehta.kompetenzstern.assocs;

import de.deepamehta.BaseAssociation;
import de.deepamehta.assocs.LiveAssociation;
import de.deepamehta.kompetenzstern.KS;
import de.deepamehta.kompetenzstern.topics.KompetenzsternTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.WorkspaceTopic;

import java.util.Hashtable;



/**
 * Part of {@link KompetenzsternTopic Kompetenzstern} application.
 * <p>
 * <hr>
 * Last functional change: 24.3.2008 (2.0b8)<br>
 * Last documentation update: 29.3.2003 (2.0a18-pre8)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class KompetenzsternMembership extends LiveAssociation implements KS {



	// ********************
	// *** Constructors ***
	// ********************



	/**
	 * @see		de.deepamehta.service.ApplicationService#createLiveAssociation
	 */
	public KompetenzsternMembership(BaseAssociation assoc, ApplicationService as) {
		super(assoc, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public CorporateDirectives propertiesChanged(Hashtable newProps, Hashtable oldProps,
										String topicmapID, String viewmode, Session session) {
		CorporateDirectives directives = super.propertiesChanged(newProps, oldProps, topicmapID, viewmode, session);
		// --- "Template Builder" ---
		String prop = (String) newProps.get(PROPERTY_TEMPLATE_BUILDER);
		if (prop != null) {
			System.out.println(">>> \"" + PROPERTY_TEMPLATE_BUILDER + "\" property has been changed to \"" + prop + "\"");
			String userID = getTopicID1();
			WorkspaceTopic workspace = (WorkspaceTopic) as.getLiveTopic(WORKSPACE_TEMPLATE_BUILDER, 1);
			if (prop.equals(SWITCH_ON)) {
				workspace.joinUser(userID, REVEAL_MEMBERSHIP_NONE, session, directives);
			} else {
				workspace.leaveUser(userID, topicmapID, session, directives);
			}
		}
		//
		return directives;
	}
}
