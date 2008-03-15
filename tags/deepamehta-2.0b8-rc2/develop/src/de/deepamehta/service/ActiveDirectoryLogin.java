package de.deepamehta.service;

import de.deepamehta.topics.LiveTopic;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;



/**
 * Implementation of LoginCheck for WebBuilder systems.
 * The LiveTopic representing this LoginCheck must have the following properties:
 * <ul>
 * <li> <b>URL:</b>         A valid LDAP URL specifying host and port, e.g. "ldap://192.168.251.145:389"
 * <li> <b>Domain Name:</b> The name of the domain configured in the ActiveDirectory, e.g. "staps2000.local"
 * </ul>
 */
public class ActiveDirectoryLogin implements LoginCheck {

	/**
	 * The LiveTopic representing this LoginCheck.
	 * Further property queries will be directed to this topic.
	 */
	LiveTopic topic;
	
	/**
	 * Just stores the reference to the LiveTopic
	 * representing this LoginCheck.
	 */
	public ActiveDirectoryLogin(LiveTopic topic) {
		this.topic = topic;
	}
	
	/**
	 * Implementation of LoginCheck.
	 * Note that empty passwords are not accepted
	 * since they provide an anonymous login
	 * to the ActiveDirectory.
	 */
	public boolean loginCheck(String username, String password) {
		String url = topic.getProperty("URL");
		String domain = topic.getProperty("Domain Name");
		if (password != null && password.length() > 0) {
			try {
				return null != createInitialContext(url, username + "@" + domain,
					password);
			} catch(NamingException e) {
			}
		}
		return false;
	}
	
	/**
	 *  Creates an initial DirContext.
	 */
	private DirContext createInitialContext(String url, String login, String password)
	throws NamingException
	{
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, login);
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		return new InitialDirContext(env);
	}
}