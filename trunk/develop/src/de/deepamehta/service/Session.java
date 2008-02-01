package de.deepamehta.service;

import de.deepamehta.BaseTopic;
import de.deepamehta.topics.helper.EmailChecker;

import java.util.Enumeration;



/**
 * ### A <CODE>Session</CODE> represents a client who is connected to the server.
 * <P>
 * At instantiation time of a <CODE>Session</CODE> the clients user is not
 * logged in.
 * <P>
 * Note: the same user can login from different client machines at the same time.
 * <P>
 * <HR>
 * Last functional change: 31.1.2008 (2.0b8)<BR>
 * Last documentation update: 13.12.2002 (2.0a17-pre3)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface Session {

	public Object getAttribute(String name);
	Enumeration getAttributeNames();
	int getType();
	int getSessionID();
	String getHostname();
	String getAddress();
	boolean isDemo();
	String getDemoTopicmapID();
	boolean loggedIn();
	String getUserID();
	String getUserName();
	BaseTopic getPersonalWorkspace();
	Object getCommunication();

	public void setAttribute(String name, Object value);
	public void removeAttribute(String name);
	void setDemo(boolean isDemo);
	void setDemoTopicmapID(String demoTopicmapID);
	void setLoggedIn(boolean loggedIn);
	void setUserID(String userID);
	void setUserName(String userName);
	void setPersonalWorkspace(BaseTopic personalWorkspace);
	void setCommunication(Object comm);
	void setEmailChecker(EmailChecker ec);
}
