package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.Hashtable;
import java.util.StringTokenizer;



/**
 * An <code>ApplicationTopic</code> represents an application that is installed at client side.
 * <p>
 * The default behavoir of an <code>ApplicationTopic</code> causes the client to launch it.
 * <p>
 * <hr>
 * Last change: 11.9.2007 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
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
	 * The default behavoir of a <code>ApplicationTopic</code> causes the client to
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

	public CorporateDirectives executeChainedCommand(String command, String result, String topicmapID, String viewmode,
																										Session session) {
		// ### compare to DocumentTopic
		// ### compare to ImageTopic
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		if (cmd.equals(CMD_ASSIGN_FILE)) {
			CorporateDirectives directives = new CorporateDirectives();
			// the chained directive is only performed if the filechoosing has performed at client side
			if (!result.equals("")) {
				// --- show new property value ---
				// Note: the result of a DIRECTIVE_CHOOSE_FILE contains the absolute path of the (client side) selected file
				Hashtable props = new Hashtable();
				props.put(PROPERTY_FILE, result);
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
