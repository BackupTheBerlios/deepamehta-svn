package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.LoginCheck;

import java.lang.reflect.Constructor;



/**
 * ### to be dropped
 * <P>
 * Represents a LoginCheck object. Other TopicTypes may be derived from the TopicType
 * which has this Class as "Implementing Class". The necessary properties of the derived
 * TopicTypes depend on the actual implementation of LoginCheck.
 * <P>
 * The Class of the LoginCheck object is derived from the name of the TopicType.
 */
public class LoginTopic extends LiveTopic {

	private static final String SERVER_PACKAGE = "de.deepamehta.service";
	private LoginCheck loginCheck;

	public LoginTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}

	/**
	 * @see AuthentificationSourceTopic#loginCheck
	 */
	public boolean loginCheck(String name, String password) {
		try {
			return getLoginCheck().loginCheck(name, password);
		} catch(Exception e) {
			System.out.println("*** LoginTopic.loginCheck() failed: " + e);
			return false;
		}
	}

	/**
	 * Note: creation of the object is lazy.
	 *
	 * @return		the LoginCheck object.
	 *
	 * @see de.deepamehta.service.ApplicationService#getLoginCheck
	 */
	public LoginCheck getLoginCheck() {
		if (loginCheck == null) {
			loginCheck = createLoginCheck();
		}		
		System.out.println(">>> LoginTopic.getLoginCheck(): using instance of " +
				loginCheck.getClass().getName() + " for authentification.");
		return loginCheck;
	}

	/**
	 * Creates a new instance of a LoginCheck object.
	 * The Class of the LoginCheck object is derived from the
	 * name of the TopicType.
	 */
	private LoginCheck createLoginCheck() {
		String loginClassName = SERVER_PACKAGE + "." + as.typeName(this);
		try {
			Constructor cons = Class.forName(loginClassName).getConstructor(
				new Class[] {LiveTopic.class});
			return (LoginCheck) cons.newInstance(new Object[] {this});
		} catch (Exception e) {
			System.out.println("*** LoginTopic.createLoginCheck(): creation of " +
			"LoginCheck failed: " + e + " -- using ApplicationService");
			return as;
		}		
	}
}
