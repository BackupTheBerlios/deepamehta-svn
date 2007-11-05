package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;



/**
 *
 */
public class LdapGroupTopic extends DataConsumerTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public LdapGroupTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **************
	// *** Method ***
	// **************



	// ----------------------
	// --- Defining Hooks ---
	// ----------------------



	public CorporateDirectives init(int initLevel, Session session)
															throws TopicInitException {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		if (initLevel == INITLEVEL_1) {
			if (LOG_TOPIC_INIT) {
				System.out.println(">>> LdapGroupTopic.init(" + initLevel + "): " + this +
					" -- registers relation");
			}
			registerRelationX("at-ldapmember", "user", "member", "tt-ldapuser", "user",
				"cn");
		}
		//
		return directives;
	}

	/**
	 * Overwritten from DataConsumerTopic
	 */
	protected String createTopicID(String topicTypeName, String id) {
		return "t-" + topicTypeName + "-" + id.hashCode();
	}
}
