package de.deepamehta;

import java.io.Serializable;



/**
 * A model for a topic geometry.
 * <p>
 * <hr>
 * Last functional change: 7.4.2007 (2.0b8)<br>
 * Last documentation update: 7.4.2007 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@freenet.de
 */
public class TopicGeometry implements Serializable {

	public String topicID;
	public int x1, y1, x2, y2;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * References checked: 7.4.2007 (2.0b8)
	 *
	 * @see		de.deepamehta.topics.TopicMapTopic#paintTopics
	 */
	public TopicGeometry(String topicID, int x1, int y1, int x2, int y2) {
		this.topicID = topicID;
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
}
