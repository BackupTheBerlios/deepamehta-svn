package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.Session;



/**
 * ### This is the entry point for authentification. There is only one topic of this type in
 * the system which is associated to exactly one LoginTopic.
 * <p>
 * The actual authentification procedure will be managed by the LoginCheck object which
 * is represented by the LoginTopic.
 * <p>
 * The association is from the authentification source topic to the login topic (type
 * {@link #SEMANTIC_AUTHENTIFICATION_SOURCE}).
 */
public class AuthentificationSourceTopic extends LiveTopic {

	public AuthentificationSourceTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}

	/**
	 * ### If the login check by the LoginCheck object succeeded, a lookup is performed to
	 * determine if the user is already represented by a user object.
	 *
	 * @see		de.deepamehta.service.InteractionConnection#login
	 */
	public BaseTopic loginCheck(String username, String password, Session session) {
		try {
			// Check if the user is a registered DeepaMehta user
			if (loginCheck(username, password)) {
				BaseTopic user = cm.getTopic(TOPICTYPE_USER, username, 1);
				return user;
			}
		} catch (Exception e) {
			System.out.println("*** AuthentificationSourceTopic.loginCheck(3): " +
				"login check not performed (" + e + ")");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Retrieves the responsible LoginCheck object from ApplicationService and passes
	 * the loginCheck to that.
	 *
	 * @return true if loginCheck succeeded, false otherwise.
	 */
	private boolean loginCheck(String username, String password) {
		return as.getLoginCheck().loginCheck(username, password);
	}
}
