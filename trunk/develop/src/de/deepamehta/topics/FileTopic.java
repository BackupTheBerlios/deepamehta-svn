package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.util.StringTokenizer;



/**
 * A file.
 * <p>
 * The data definition of a <code>FileTopic</code> comprises one <code>File</code> field
 * to hold the filename resp. path of the file. As GUI extension to this field a
 * <code>FileTopic</code> adds a button (labeled "Choose...") to bring up the filechooser
 * dialog (see {@link #buttonCommand}). The command fired by that button (<code>CMD_ASSIGN_FILE</code>)
 * is handled by this class (see {@link #executeCommand}). The chained command is handled by the
 * <code>FileTopic</code>'s subclasses -- a generic <code>FileTopic</code> doesn't know
 * what to to with the selected file.
 * <p>
 * <hr>
 * Last change: 11.9.2007 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public abstract class FileTopic extends LiveTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public FileTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	// --------------------------
	// --- Providing Commands ---
	// --------------------------



	public static void buttonCommand(PropertyDefinition propDef, ApplicationService as, Session session) {
		String propName = propDef.getPropertyName();
		if (propName.equals(PROPERTY_FILE)) {
			propDef.setActionButton(as.string(BUTTON_ASSIGN_FILE), CMD_ASSIGN_FILE);
		}
	}



	// --------------------------
	// --- Executing Commands ---
	// --------------------------



	/**
	 * Note: the chained command is provided by the FileTopic's subclasses.
	 *
	 * @see		de.deepamehta.service.ApplicationService#performTopicAction
	 */
	public CorporateDirectives executeCommand(String command, Session session,
													String topicmapID, String viewmode) {
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		if (cmd.equals(CMD_ASSIGN_FILE)) {
			directives.add(DIRECTIVE_CHOOSE_FILE);
		} else {
			return super.executeCommand(command, session, topicmapID, viewmode);
		}
		//
		return directives;
	}
}
