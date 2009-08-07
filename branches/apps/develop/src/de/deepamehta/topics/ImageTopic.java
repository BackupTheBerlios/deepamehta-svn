package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Detail;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.StringTokenizer;



/**
 * <p>
 * <hr>
 * Last change: 11.9.2007 (2.0b8)<br>
 * J&ouml;rg Richter<BR>
 * jri@deepamehta.de
 */
public class ImageTopic extends FileTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public ImageTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	public CorporateDirectives executeChainedCommand(String command, String result, String topicmapID, String viewmode,
																										Session session) {
		// ### compare to DocumentTopic
		// ### compare to ApplicationTopic
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_ASSIGN_FILE)) {
			CorporateDirectives directives = new CorporateDirectives();
			// Note: the result of a DIRECTIVE_CHOOSE_FILE contains the absolute path of the (client side) selected file
			copyAndUpload(result, FILE_IMAGE, PROPERTY_FILE, session, directives);
			return directives;
		} else {
			return super.executeChainedCommand(command, result, topicmapID, viewmode, session);
		}
	}



	// ---------------------------
	// --- Handling Properties ---
	// ---------------------------



	/**
	 * @see		TypeTopic#makeTypeDefinition
	 */
	public static void propertyLabel(PropertyDefinition propDef, ApplicationService as, Session session) {
		String propName = propDef.getPropertyName();
		if (propName.equals(PROPERTY_FILE)) {
			propDef.setPropertyLabel("Image");
		}
	}



	// -----------------------------
	// --- Handling Topic Detail ---
	// -----------------------------



	public Detail getDetail() {
		String image = getProperty(PROPERTY_FILE);
		String title = getProperty(PROPERTY_NAME);
		String imagefile = as.getCorporateWebBaseURL() + FILESERVER_IMAGES_PATH + image;
		System.out.println(">>> ImageTopic.getDetail(): imagefile=\"" + imagefile + "\"");
		Detail detail = new Detail(DETAIL_TOPIC, DETAIL_CONTENT_IMAGE, imagefile,
        	Boolean.FALSE, title, "??");	// ### param2 is not used ### command?
		//
		return detail;
	}
}
