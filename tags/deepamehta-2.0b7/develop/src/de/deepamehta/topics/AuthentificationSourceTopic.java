package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.TopicInitException;
import de.deepamehta.service.Session;
import de.deepamehta.service.ApplicationService;
//
import java.util.*;



/**
 * ### This is the entry point for authentification. There is only one topic of this type in
 * the system which is associated to exactly one LoginTopic.
 * <P>
 * The actual authentification procedure will be managed by the LoginCheck object which
 * is represented by the LoginTopic.
 * <P>
 * The association is from the authentification source topic to the login topic (type
 * {@link #SEMANTIC_AUTHENTIFICATION_SOURCE}).
 */
public class AuthentificationSourceTopic extends LiveTopic {

	public AuthentificationSourceTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}

	/**
	 * If the login check by the LoginCheck object succeeded, a lookup is performed to
	 * determine if the user is already represented by a user object. ### If the user is
	 * logged in for the first time, a new user object with associated personal map
	 * will be created.
	 *
	 * @see		de.deepamehta.service.InteractionConnection#login
	 */
	public BaseTopic loginCheck(String username, String password, Session session) {
		try {
			if (loginCheck(username, password)) {
				// Check if the user is a registered DeepaMehta user already
				// ### possibly the check isn't sufficient. Must the user exist in the
				// "Users and Groups" view?
				BaseTopic user = cm.getTopic(TOPICTYPE_USER, username, 1);
				/* ### if (user == null) {
					System.out.println(">>> AuthentificationSourceTopic: register new " +
						"DeepaMehta user \"" + username + "\"");
					// register new DeepaMehta user ### also if CM is used for authentification?
					// ### Note: programatically registered users will not appear in the
					// "Users and Groups" view
					user = new BaseTopic(as.getNewTopicID(), 1, TOPICTYPE_USER, 1, username);
					as.createLiveTopic(user, false, session);
					as.evokeLiveTopic(user, session, null, null);
				} */
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
