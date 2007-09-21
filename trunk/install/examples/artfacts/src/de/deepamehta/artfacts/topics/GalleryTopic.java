package de.deepamehta.artfacts.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.TopicInitException;
import de.deepamehta.artfacts.Artfacts;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.DataConsumerTopic;

import java.util.Hashtable;



/**
 * Part of the "Artfacts" example.
 * <P>
 * <HR>
 * Last functional change: 25.3.2003 (2.0a18-pre7)<BR>
 * Last documentation update: 30.1.2003 (2.0a18-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class GalleryTopic extends DataConsumerTopic implements Artfacts {



	// *******************
	// *** Constructor ***
	// *******************



	public GalleryTopic(BaseTopic topic, ApplicationService as) {
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
			registerRelationN("Institution", ASSOCTYPE_ASSOCIATION, "ArtistPromotion", "InstitutionID", "PeopleID", TOPICTYPE_ARTIST, "People", "Surname");
			registerRelationN("Institution", ASSOCTYPE_ASSOCIATION, "ArtworkPromotion", "InstitutionID", "ArtworkID", TOPICTYPE_ARTWORK, "Artwork", "Title");
			registerRelationN("Institution", ASSOCTYPE_ASSOCIATION, "ExhibitionOrganizer", "InstitutionID", "ExhibitionID", TOPICTYPE_EXHIBITION, "Exhibition", "Title");
		}
		//
		return directives;
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	public static Hashtable makeProperties(Hashtable elementData) {
		// "FoundationYear" -> "Foundation Year"
		String year = (String) elementData.get("FoundationYear");
		if (year != null) {
			elementData.remove("FoundationYear");
			if (!year.equals("0")) {
				elementData.put(PROPERTY_FOUNDATION_YEAR, year);
			}
		}
		//
		return elementData;
	}
}
