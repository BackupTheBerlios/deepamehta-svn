package de.deepamehta.topics;

import java.util.Hashtable;

import de.deepamehta.BaseTopic;
import de.deepamehta.service.ApplicationService;

/**
 * 
 */
@SuppressWarnings("serial")
public class ListMessageTopic extends LiveTopic {

	/* types and properties */
	private static final String PROPERTY_SUBJECT = "Subject";

	private static final String PROPERTY_FROMMAIL = "From Mail";

	public ListMessageTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}

	@Override
	public String getNameProperty() {
		return null;
	}

	@Override
	public String getTopicName(Hashtable props, Hashtable oldProps) {
		// ### Note: a non-null value is only returned if topic name actually
		// changes
		// ### probably the code tend to be too complicated and we should always
		// return the name
		String forename = (String) props.get(PROPERTY_FROMMAIL);
		String surname = (String) props.get(PROPERTY_SUBJECT);
		String oldfore = (String) oldProps.get(PROPERTY_FROMMAIL);
		String oldsur = (String) oldProps.get(PROPERTY_SUBJECT);
		System.out.println("RETURN NAME: " + forename + " " + surname);
		boolean f = forename != null;
		boolean s = surname != null;
		boolean of = oldfore != null;
		boolean os = oldsur != null;
		if (f && s) {
			System.out.println("RETURN NAME: " + forename + " " + surname);
			return forename + " " + surname;
		} else if (f) {
			System.out.println("RETURN NAME: " + forename + (os ? " " + oldsur : ""));
			return forename + (os ? " " + oldsur : "");
		} else if (s) {
			System.out.println("RETURN NAME: " + (of ? oldfore + " " : "") + surname);
			return (of ? oldfore + " " : "") + surname;
		} else {
			System.out.println("RETURN NAME: NULL");
			return null;
		}
	}

	@Override
	protected String topicName(Hashtable elementData, String fieldname) {
		return super.topicName(elementData, fieldname);
	}
}
