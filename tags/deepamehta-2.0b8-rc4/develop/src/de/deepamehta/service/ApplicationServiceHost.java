package de.deepamehta.service;



/**
 * This interface is implemented by {@link ApplicationService application service} hosting singletons.
 * <P>
 * ### rename to ApplicationServiceProvider
 * <P>
 * <HR>
 * Last functional change: 22.9.2004 (2.0b3)<BR>
 * Last documentation update: 22.9.2004 (2.0b3)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface ApplicationServiceHost {

	String getCommInfo();

	void sendDirectives(Session session, CorporateDirectives directives,
											   ApplicationService as, String topicmapID, String viewmode);
	void broadcastChangeNotification(String topicID);
}
