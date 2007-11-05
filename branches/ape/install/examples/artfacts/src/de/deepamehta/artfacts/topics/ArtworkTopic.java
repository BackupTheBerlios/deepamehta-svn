package de.deepamehta.artfacts.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.TopicInitException;
import de.deepamehta.artfacts.Artfacts;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;
import de.deepamehta.topics.DataConsumerTopic;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;



/**
 * Part of the "Artfacts" example.
 * <P>
 * <HR>
 * Last functional change: 25.3.2003 (2.0a18-pre7)<BR>
 * Last documentation update: 25.1.2003 (2.0a17-pre7)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class ArtworkTopic extends DataConsumerTopic implements Artfacts {



	// *******************
	// *** Constructor ***
	// *******************



	public ArtworkTopic(BaseTopic topic, ApplicationService as) {
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
			registerRelation1(ASSOCTYPE_ASSOCIATION, "People", "ArtistID", TOPICTYPE_ARTIST, "People", "Surname");
			registerRelationN("Artwork", ASSOCTYPE_ASSOCIATION, "ArtworkPromotion", "ArtworkID", "InstitutionID", TOPICTYPE_GALLERY, "Institution", "Name");
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
		// set "Description"
		try {
			String id = (String) elementData.get("ID");
			String caption = (String) elementData.get("Caption");
			String imagefile = "http://" + InetAddress.getLocalHost().getHostAddress() + "/Artfacts/thumbs/" + id + ".jpg";				// ### hardcoded
			// ### String imagefile = "http://www.deepamehta.de/Artfacts/thumbs/" + id + ".jpg";				// ### hardcoded
			System.out.println(">>> image URL=\"" + imagefile + "\"");
			elementData.put(PROPERTY_DESCRIPTION, "<img src=\"" + imagefile + "\"><P>" + caption);		// ### <html>
		} catch (UnknownHostException e) {
			System.out.println("*** ArtworkTopic.makeProperties(): " + e);
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



	// -----------------------------
	// --- Handling Topic Detail ---
	// -----------------------------



	/* ### public Detail getDetail() {
		String id = getProperty(PROPERTY_ID);
		String title = getProperty(PROPERTY_NAME);
		String imagefile = "http://www.deepamehta.de/Artfacts/thumbs/" + id + ".jpg";	// ### hardcoded
		System.out.println(">>> ArtworkTopic.getDetail(): imagefile=\"" + imagefile + "\"");
		Detail detail = new Detail(DETAIL_TOPIC, DETAIL_CONTENT_IMAGE, imagefile,
        	Boolean.FALSE, title, "??");	// ### param2 is not used ### command?
		//
		return detail;
	} */
}
