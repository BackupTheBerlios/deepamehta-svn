package de.deepamehta.webfrontend;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;

import java.io.Serializable;



/**
 * A bean-like data container for passing data from the front-controler (servlet)
 * to the presentation layer (JSP engine).
 */
public class Topic implements Serializable {

	public String id, name, typeID, typeName, icon;

	Topic(BaseTopic topic, ApplicationService as) {
		id = topic.getID();
		name = topic.getName();
		typeID = topic.getType();
		typeName = as.type(typeID, 1).getName();
		icon = as.getLiveTopic(id, 1).getIconfile();
	}
}