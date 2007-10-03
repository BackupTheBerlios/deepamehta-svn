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
 * Last functional change: 16.3.2004 (2.0b3-pre1)<BR>
 * Last documentation update: 25.1.2003 (2.0a17-pre7)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class ArtistTopic extends DataConsumerTopic implements Artfacts {



	// *******************
	// *** Constructor ***
	// *******************



	public ArtistTopic(BaseTopic topic, ApplicationService as) {
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
			registerRelationN("People", ASSOCTYPE_ASSOCIATION, "Artwork", "ArtistID", "ID", TOPICTYPE_ARTWORK, "Artwork", "Title");
			registerRelationN("People", ASSOCTYPE_ASSOCIATION, "ArtistPromotion", "PeopleID", "InstitutionID", TOPICTYPE_GALLERY, "Institution", "Name");
			registerRelationN("People", ASSOCTYPE_ASSOCIATION, "ExhibitionParticipation", "PeopleID", "ExhibitionID", TOPICTYPE_EXHIBITION, "Exhibition", "Title");
		}
		//
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public static Hashtable makeProperties(Hashtable elementData) {
		// "Name" -> "First Name"
		String name = (String) elementData.get("Name");
		if (name != null) {
			elementData.remove("Name");
			elementData.put(PROPERTY_FIRST_NAME, name);
		}
		// "Surname" -> "Name" (labeled "Last Name")
		name = (String) elementData.get("Surname");
		if (name != null) {
			elementData.remove("Surname");
			elementData.put(PROPERTY_NAME, name);
		}
		// "BirthLocation" -> "Birth Location"
		String birthLoc = (String) elementData.get("BirthLocation");
		if (birthLoc != null) {
			elementData.remove("BirthLocation");
			elementData.put(PROPERTY_BIRTH_LOCATION, birthLoc);
		}
		// "BirthYear", "BirthMonth", "BirthDay" -> "Birthday"
		String birthYear = (String) elementData.get("BirthYear");
		String birthMonth = (String) elementData.get("BirthMonth");
		String birthDay = (String) elementData.get("BirthDay");
		if (birthYear != null) {
			if (birthYear.equals("0")) {
				birthYear = VALUE_NOT_SET;
			}
			elementData.remove("BirthYear");
		} else {
			birthYear = VALUE_NOT_SET;
		}
		if (birthMonth != null) {
			birthMonth = birthMonth.equals("0") ? VALUE_NOT_SET : DeepaMehtaUtils.align(birthMonth);
			elementData.remove("BirthMonth");
		} else {
			birthMonth = VALUE_NOT_SET;
		}
		if (birthDay != null) {
			birthDay = birthDay.equals("0") ? VALUE_NOT_SET : DeepaMehtaUtils.align(birthDay);
			elementData.remove("BirthDay");
		} else {
			birthDay = VALUE_NOT_SET;
		}
		//
		elementData.put(PROPERTY_BIRTHDAY, birthYear + DATE_SEPARATOR + birthMonth + DATE_SEPARATOR + birthDay);
		//
		return elementData;
	}

	public static void propertyLabel(PropertyDefinition propertyDef, ApplicationService as, Session session) {
		String propName = propertyDef.getPropertyName();
		if (propName.equals(PROPERTY_NAME)) {
			propertyDef.setPropertyLabel("Last Name");
		}
	}
}
