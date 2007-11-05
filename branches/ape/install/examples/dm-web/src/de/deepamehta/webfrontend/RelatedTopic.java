package de.deepamehta.webfrontend;

import de.deepamehta.BaseAssociation;
import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;

import java.io.Serializable;



/**
 * A bean-like data container for passing data from the front-controler (servlet)
 * to the presentation layer (JSP engine).
 */
public class RelatedTopic extends Topic implements Serializable {

	public String assocID, assocName, assocTypeID, assocTypeName;

	RelatedTopic(BaseTopic topic, BaseAssociation assoc, ApplicationService as) {
		super(topic, as);
		assocID = assoc.getID();
		assocName = assoc.getName();
		assocTypeID = assoc.getType();
		assocTypeName = as.type(assocTypeID, 1).getName();
	}
}