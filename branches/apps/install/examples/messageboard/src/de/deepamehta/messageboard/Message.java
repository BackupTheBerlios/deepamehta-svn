package de.deepamehta.messageboard;

import de.deepamehta.service.ApplicationService;
import de.deepamehta.util.DeepaMehtaUtils;



/**
 * A bean-like container for passing data from the front-controler (servlet)
 * to the presentation layer (JSP engine).
 * <p>
 * Last change: 5.7.2008<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class Message implements MessageBoard {

	public String id, subject, text, from, date;

	Message(String id, ApplicationService as) {
		this.id = id;
		this.subject = DeepaMehtaUtils.encodeHTMLTags(as.getTopicProperty(id, 1, PROPERTY_NAME));
		// Note: the message text is an HTML property and is stored in quoted form already
		this.text = as.getTopicProperty(id, 1, PROPERTY_DESCRIPTION);
		this.from = DeepaMehtaUtils.encodeHTMLTags(as.getTopicProperty(id, 1, PROPERTY_FROM));
		this.date = as.getTopicProperty(id, 1, PROPERTY_DATE);
	}
}
