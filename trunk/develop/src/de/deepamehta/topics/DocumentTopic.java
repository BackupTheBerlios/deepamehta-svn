package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.FileServer;
import de.deepamehta.service.ApplicationService;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.Session;

import java.io.File;
import java.util.Hashtable;
import java.util.StringTokenizer;



/**
 * <p>
 * <hr>
 * Last change: 26.1.2009 (2.0b9)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class DocumentTopic extends FileTopic {



	// *******************
	// *** Constructor ***
	// *******************



	public DocumentTopic(BaseTopic topic, ApplicationService as) {
		super(topic, as);
	}



	// **********************
	// *** Defining Hooks ***
	// **********************



	/**
	 * The default action of a <code>DocumentTopic</code> causes the client to open it by
	 * use of a locally installed application.
	 * <p>
	 * If the local copy of the document differs from the corporate document the client is
	 * caused to download the document from corporate repository to its local repository.
	 *
	 * @see		de.deepamehta.service.ApplicationService#performTopicAction
	 */
	public CorporateDirectives executeCommand(String command, Session session, String topicmapID, String viewmode) {
		if (command.equals(CMD_DEFAULT) || command.equals(CMD_SUBMIT_FORM)) {
			// create directives
			CorporateDirectives directives = new CorporateDirectives();
			try {
				String filename = getProperty(PROPERTY_FILE);
				// access file in corporate document repository
				File file = new File(FileServer.repositoryPath(FILE_DOCUMENT) + filename);
				Long lastModified = new Long(file.lastModified());
				// Note: if the file is missing in corporate document repository lastModified is 0 and the
				// DIRECTIVE_DOWNLOAD_FILE is added anyway, thus the client can detect the file is missing
				// and can report this condition to the user (and can avoid to queue a download request)
				directives.add(DIRECTIVE_DOWNLOAD_FILE, filename, lastModified, new Integer(FILE_DOCUMENT));
				// build the directives to be queued for opening this document
				CorporateDirectives openDirective = new CorporateDirectives();
				String openCommand = as.openCommand(session.getUserID(), filename);
				openDirective.add(DIRECTIVE_OPEN_FILE, openCommand, filename);
				//
				directives.add(DIRECTIVE_QUEUE_DIRECTIVES, openDirective);
			} catch (DeepaMehtaException e) {
				directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(), new Integer(NOTIFICATION_WARNING));
			}
			return directives;
		}
		return super.executeCommand(command, session, topicmapID, viewmode);
	}

	public CorporateDirectives executeChainedCommand(String command, String result, String topicmapID, String viewmode,
																										Session session) {
		// ### compare to ImageTopic
		// ### compare to ApplicationTopic
		StringTokenizer st = new StringTokenizer(command, COMMAND_SEPARATOR);
		String cmd = st.nextToken();
		//
		if (cmd.equals(CMD_ASSIGN_FILE)) {
			CorporateDirectives directives = new CorporateDirectives();
			// Note: the result of a DIRECTIVE_CHOOSE_FILE contains the absolute path of the (client side) selected file
			copyAndUpload(result, FILE_DOCUMENT, PROPERTY_FILE, session, directives);
			return directives;
		} else {
			return super.executeChainedCommand(command, result, topicmapID, viewmode, session);
		}
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#addPublishAction
	 */
	public void published(Session session, CorporateDirectives directives) {
		// upload the file to the server
		upload(getProperty(PROPERTY_FILE), FILE_DOCUMENT, session, directives);
	}
}
