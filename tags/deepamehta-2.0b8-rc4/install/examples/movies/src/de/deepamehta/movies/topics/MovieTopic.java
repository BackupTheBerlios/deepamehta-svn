package de.deepamehta.movies.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.DataConsumerTopic;

import java.util.Hashtable;



/**
 * Part of the "Movies" example.
 * <P>
 * <HR>
 * Last functional change: 16.3.2004 (2.0b3-pre1)<BR>
 * Last documentation update: 8.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class MovieTopic extends DataConsumerTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public MovieTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives init(int initLevel, Session session) throws TopicInitException {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		if (initLevel == INITLEVEL_1) {
			if (LOG_TOPIC_INIT) {
				System.out.println(">>> MovieTopic.init(" + initLevel + "): " + this + " -- registers relation");
			}
			registerRelationN("Movie", "at-association", "Mitwirkung", "MovieID", "ActorID", "tt-actor", "Actor", "Name");
		}
		//
		return directives;
	}




	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public static Hashtable makeProperties(Hashtable elementData) {
		// transfer "Title" -> "Name"
		String title = (String) elementData.get("Title");
		if (title != null) {
			elementData.remove("Title");
			elementData.put(PROPERTY_NAME, title);
		}
		//
		return elementData;
	}

	public static void propertyLabel(PropertyDefinition propDef, ApplicationService as, Session session) {
		String propName = propDef.getPropertyName();
		if (propName.equals(PROPERTY_NAME)) {
			propDef.setPropertyLabel("Title");
		}
	}

	/* ### public String getNameProperty() {
		return "Title";
	} */
}
