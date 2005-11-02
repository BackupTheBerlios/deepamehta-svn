package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.ApplicationService;
//
import java.io.File;
import java.util.*;



/**
 * An <CODE>ApplicationTopic</CODE> represents an application that is installed at client side.
 * <P>
 * The default behavoir of an <CODE>ApplicationTopic</CODE> causes the client to launch it.
 * <P>
 * <HR>
 * Last functional change: 12.4.2003 (2.0a18-pre9)<BR>
 * Last documentation update: 7.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class ApplicationTopic extends FileTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public ApplicationTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	/**
	 * The default behavoir of a <CODE>ApplicationTopic</CODE> causes the client to
	 * launch it locally.
	 *
	 * @see		de.deepamehta.service.ApplicationService#performTopicAction
	 */
	public CorporateDirectives executeCommand(String command, Session session,
													String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		if (command.equals(CMD_DEFAULT) || command.equals(CMD_SUBMIT_FORM)) {
			String application = getProperty(PROPERTY_FILE);
			// error check
			if (application.equals("")) {
				System.out.println("*** ApplicationTopic.executeCommand(): there is " +
					"no file assigned to " + this + " -- application can't be launched");
				return directives;
			}
			// adding DIRECTIVE_LAUNCH_APPLICATION causes the client to launch this application
			directives.add(DIRECTIVE_LAUNCH_APPLICATION, application);
			return directives;
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
	}

	public CorporateDirectives executeChainedCommand(String command,
								String result, String topicmapID, String viewmode,
								Session session) {
		// ### compare to DocumentTopic
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, ":");
		String cmd = st.nextToken();
		if (cmd.equals(CMD_ASSIGN_FILE)) {
			// the chained directive is only performed if the filechoosing has
			// performed at client side
			if (!result.equals("")) {
				// --- build changed topic properties ---
				// Note: the result of a CMD_ASSIGN_FILE contains the
				// absolute path of the (client side) selected file
				Hashtable props = new Hashtable();
				props.put(PROPERTY_FILE, result);
				// ---
				// cause the client to display the changed topic property
				directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
			}
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
			propDef.setPropertyLabel("Application");
		}
	}
}
