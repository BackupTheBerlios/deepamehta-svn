package de.deepamehta.movies.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.DataConsumerTopic;
import de.deepamehta.topics.TypeTopic;

import java.util.Vector;



/**
 * Part of the "Movies" example.
 * <P>
 * ### Note: not derived from PersonTopic however in corporate memory this derivation does exist.
 * ### Consequence: An actor doesn't have the customized topic behavoir of a person especially the
 * ### naming behavoir
 * <P>
 * <HR>
 * Last functional change: 16.3.2004 (2.0b3-pre1)<BR>
 * Last documentation update: 8.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class ActorTopic extends DataConsumerTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public ActorTopic(BaseTopic topic, ApplicationService as) {
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
				System.out.println(">>> ActorTopic.init(" + initLevel + "): " + this + " -- register relation");
			}
			registerRelationN("Actor", "at-association", "Mitwirkung", "ActorID", "MovieID", "tt-movie", "Movie", "Title");
		}
		//
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public static Vector hiddenProperties(TypeTopic type) {
		Vector props = new Vector();
		props.addElement(PROPERTY_FIRST_NAME);
		return props;
	}
}
