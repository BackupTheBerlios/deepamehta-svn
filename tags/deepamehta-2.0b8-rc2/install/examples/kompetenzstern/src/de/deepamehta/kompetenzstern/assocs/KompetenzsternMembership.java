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
 * <P>
 * <HR>
 * Last functional change: 10.6.2003 (2.0b1)<BR>
 * Last documentation update: 29.3.2003 (2.0a18-pre8)<BR>
 * J&ouml;rg Richter<BR>
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
				workspace.joinUser(userID, REVEAL_MEMBERSHIP_NONE, true, session, directives);	// createMembership=true
			} else {
				workspace.leaveUser(userID, topicmapID, viewmode, session, directives);
			}
			// ### DOESN'T WORK FOR MORE THAN ONE TEMPLATE BUILDER
			/* String memberID = getTopicID1();
			String workspaceID = as.getWorkspaceTopicmap(memberID, directives).getID();
			System.out.println(">>> role of user \"" + memberID + "\" changed, personal workspace is \"" + workspaceID + "\"");
			if (memberID.equals(session.getUserID())) {
				if (prop.equals(SWITCH_ON)) {
					PresentableTopic template = as.createPresentableTopic(TEMPLATE_STANDARD, 1);	// throws DME ### version=1
					directives.add(DIRECTIVE_SHOW_TOPIC, template, Boolean.FALSE, workspaceID, VIEWMODE_USE);
				} else {
					directives.add(DIRECTIVE_HIDE_TOPIC, TEMPLATE_STANDARD, Boolean.FALSE, workspaceID, VIEWMODE_USE);
				}
			} else {
				if (prop.equals(SWITCH_ON)) {
					as.createViewTopic(workspaceID, 1, VIEWMODE_USE, TEMPLATE_STANDARD, 1, 100, 80, true);	// ### geometry
				} else {
					as.deleteViewTopic(workspaceID, VIEWMODE_USE, TEMPLATE_STANDARD);
				}
			} */
		}
		//
		return directives;
	}
}
