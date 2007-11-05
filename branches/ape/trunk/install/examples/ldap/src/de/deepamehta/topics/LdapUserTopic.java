package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;



/**
 *
 */
public class LdapUserTopic extends DataConsumerTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public LdapUserTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// ----------------------
	// --- Defining Hooks ---
	// ----------------------



	public CorporateDirectives init(int initLevel, Session session)
															throws TopicInitException {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		if (initLevel == INITLEVEL_1) {
			if (LOG_TOPIC_INIT) {
				System.out.println(">>> LdapUserTopic.init(" + initLevel + "): " + this +
					" -- register relation");
			}
			registerRelationX("at-ldapmemberof", "group", "memberOf", "tt-ldapgroup",
				"group", "cn");
		}
		//
		return directives;
	}
	
	/**
	 *  Overwritten from DataConsumerTopic
	 */
	protected String createTopicID(String topicTypeName, String id) {
		return "t-" + topicTypeName + "-" + id.hashCode();
	}
}
