package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.ApplicationService;
//
import java.util.StringTokenizer;



/**
 * A file.
 * <P>
 * The data definition of a <CODE>FileTopic</CODE> comprises one <CODE>File</CODE> field
 * to hold the filename resp. path of the file. As GUI extension to this field a
 * <CODE>FileTopic</CODE> adds a button (labeled "Choose...") to bring up the filechooser
 * dialog (see {@link #buttonCommand}). The command fired by that button (<code>CMD_ASSIGN_FILE</code>)
 * is handled by this class (see {@link #executeCommand}). The chained command is handled by the
 * <CODE>FileTopic</CODE>'s subclasses -- a generic <CODE>FileTopic</CODE> doesn't know
 * what to to with the selected file.
 * <P>
 * <HR>
 * Last functional change: 26.9.2002 (2.0a16-pre4)<BR>
 * Last documentation update: 24.4.2001 (2.0a7-pre6)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
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
		StringTokenizer st = new StringTokenizer(command, ":");
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
