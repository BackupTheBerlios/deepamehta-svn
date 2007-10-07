package de.deepamehta.artfacts.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.TopicInitException;
import de.deepamehta.artfacts.Artfacts;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.DataConsumerTopic;
import de.deepamehta.util.DeepaMehtaUtils;

import java.util.Hashtable;



/**
 * Part of the "Artfacts" example.
 * <P>
 * <HR>
 * Last functional change: 1.2.2003 (2.0a18-pre1)<BR>
 * Last documentation update: 30.1.2003 (2.0a18-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class ExhibitionTopic extends DataConsumerTopic implements Artfacts {



	// *******************
	// *** Constructor ***
	// *******************



	public ExhibitionTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// ------------------
	// --- Life Cycle ---
	// ------------------



	public CorporateDirectives init(int initLevel, Session session) throws TopicInitException {
		CorporateDirectives directives = super.init(initLevel, session);
		//
		if (initLevel == INITLEVEL_1) {
			registerRelationN("Exhibition", ASSOCTYPE_ASSOCIATION, "ExhibitionOrganizer", "ExhibitionID", "InstitutionID", TOPICTYPE_GALLERY, "Institution", "Name");
			registerRelationN("Exhibition", ASSOCTYPE_ASSOCIATION, "ExhibitionParticipation", "ExhibitionID", "PeopleID", TOPICTYPE_ARTIST, "People", "Surname");
		}
		//
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public static Hashtable makeProperties(Hashtable elementData) {
		// transfer "Title" -> "Name"
		String title = (String) elementData.get("Title");
		if (title != null) {
			elementData.remove("Title");
			elementData.put(PROPERTY_NAME, title);
		}
		// "BeginDate" -> "Begin"
		String beginDate = (String) elementData.get("BeginDate");
		if (beginDate != null) {
			elementData.remove("BeginDate");
			elementData.put(PROPERTY_BEGIN, DeepaMehtaUtils.replace(beginDate, '-', DATE_SEPARATOR));
		}
		// "EndDate" -> "End"
		String endDate = (String) elementData.get("EndDate");
		if (endDate != null) {
			elementData.remove("EndDate");
			elementData.put(PROPERTY_END, DeepaMehtaUtils.replace(endDate, '-', DATE_SEPARATOR));
		}
		//
		return elementData;
	}

	public static void propertyLabel(PropertyDefinition propDef, ApplicationService as, Session session) {
		String propName = propDef.getPropertyName();
		if (propName.equals(PROPERTY_NAME)) {
			propDef.setPropertyLabel(PROPERTY_TITLE);
		}
	}
}
