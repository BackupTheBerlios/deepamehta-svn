package de.deepamehta;

import java.util.Hashtable;



/**
 * Describes a simplified form of a Topic Map
 * according to the ISO 13250 Topic Maps standard.
 * <P>
 * <HR>
 * Last functional change: 30.3.2003 (2.0a18-pre8)<BR>
 * Last documentation update: 9.6.2001 (2.0a11-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface TopicMap {

	public Hashtable getTopics();
	public Hashtable getAssociations();

	public Topic getTopic(String id);
	public Association getAssociation(String id);
	
	public void addTopic(Topic topic);
	public void addAssociation(Association association);

	public void deleteTopic(String id);
	public void deleteAssociation(String id);

	public void changeTopicName(String id, String name);
	public void changeTopicType(String id, String type);
	public void changeAssociationType(String id, String type);

	public boolean topicExists(String id);
	public boolean associationExists(String id);
}
