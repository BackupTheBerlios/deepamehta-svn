package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.PresentableTopic;
import de.deepamehta.PresentableAssociation;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.DeepaMehtaServiceUtils;
import de.deepamehta.service.ApplicationService;
//
import java.util.*;



/**
 * ### No custom implementation for the moment
 */
public class WebsiteTopic extends LiveTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public WebsiteTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}
	                
	                
	                
	// ***************
	// *** Methods ***
	// ***************



	// ----------------------
	// --- Defining Hooks ---
	// ----------------------



	/* ### protected CorporateDirectives evoke(Session session, String topicmapID,
																String viewmode) {
		CorporateDirectives directives = super.evoke(session, topicmapID, viewmode);
		//
		return directives;
	} */



	// -----------------------
	// --- Private Methods ---
	// -----------------------



}
