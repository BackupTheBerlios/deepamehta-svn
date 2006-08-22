package de.deepamehta.topics;

import de.deepamehta.BaseTopic;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.service.Session;
import de.deepamehta.service.CorporateDirectives;
import de.deepamehta.service.ApplicationService;
//
import java.io.File;
import java.util.*;



/**
 * <P>
 * <HR>
 * Last functional change: 11.3.2004 (2.0b3-pre1)<BR>
 * Last documentation update: 7.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
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
	 * The default action of a <CODE>DocumentTopic</CODE> causes the client to open it by
	 * use of a locally installed application.
	 * <P>
	 * If the local copy of the document differs from the corporate document the client is
	 * caused to download the document from corporate repository to its local repository.
	 *
	 * @see		de.deepamehta.service.ApplicationService#performTopicAction
	 */
	public CorporateDirectives executeCommand(String command, Session session,
													String topicmapID, String viewmode) {
		if (command.equals(CMD_DEFAULT) || command.equals(CMD_SUBMIT_FORM)) {
			// create directives
			CorporateDirectives directives = new CorporateDirectives();
			try {
				String filename = getProperty(PROPERTY_FILE);
				// access file in corporate document repository
				File file = new File(FILESERVER_DOCUMENTS_PATH + filename);
				Long lastModified = new Long(file.lastModified());
				// Note: if the file is missing in corporate document repository
				// lastModified is 0 and the DIRECTIVE_DOWNLOAD_FILE is added
				// anyway, thus the client can detect the file is missing and can
				// report this condition to the user (and can avoid to queue a
				// download request)
				directives.add(DIRECTIVE_DOWNLOAD_FILE, filename, lastModified,
					new Integer(FILE_DOCUMENT));
				// build the directives to be queued for opening this document
				CorporateDirectives openDirective = new CorporateDirectives();
				String openCommand = as.openCommand(session.getUserID(), filename);
				openDirective.add(DIRECTIVE_OPEN_FILE, openCommand, filename);
				//
				directives.add(DIRECTIVE_QUEUE_DIRECTIVES, openDirective);
			} catch (DeepaMehtaException e) {
				directives.add(DIRECTIVE_SHOW_MESSAGE, e.getMessage(),
					new Integer(NOTIFICATION_WARNING));
			}
			return directives;
		}
		return super.executeCommand(command, session, topicmapID, viewmode);
	}

	public CorporateDirectives executeChainedCommand(String command,
								String result, String topicmapID, String viewmode,
								Session session) {
		// ### compare to ApplicationTopic
		CorporateDirectives directives = new CorporateDirectives();
		//
		StringTokenizer st = new StringTokenizer(command, ":");
		String cmd = st.nextToken();
		if (cmd.equals(CMD_ASSIGN_FILE)) {	// ### to be dropped
			// the chained action is only performed if the filechoosing has performed at
			// client side
			if (!result.equals("")) {
				// --- build changed topic properties ---
				// Note: the result of a CMD_ASSIGN_FILE contains the absolute
				// path of the (client side) selected file
				String path = result;
				String filename = new File(path).getName();
				Hashtable props = new Hashtable();
				props.put(PROPERTY_FILE, filename);
				// cause the client to copy the selected file into its document repository
				directives.add(DIRECTIVE_COPY_FILE, path, new Integer(FILE_DOCUMENT));
				// cause the client to display the changed topic property
				directives.add(DIRECTIVE_SHOW_TOPIC_PROPERTIES, getID(), props, new Integer(1));
			}
		} else {
			return super.executeChainedCommand(command, result, topicmapID, viewmode, session);
		}
		//
		return directives;
	}

	/**
	 * @see		de.deepamehta.service.ApplicationService#addPublishAction
	 */
	public CorporateDirectives published() {
		CorporateDirectives directives = super.published();
		// access file in corporate document repository
		String filename = getProperty(PROPERTY_FILE);
		if (filename.equals("")) {
			return directives;
		}
		File file = new File(FILESERVER_DOCUMENTS_PATH + filename);
		long lastModified = file.lastModified();
		// reporting
		if (LOG_FILESERVER) {
			// check if document already exists in corporate document repository
			if (lastModified != 0) {
				System.out.println("> DocumentTopic.published(): file \"" + filename +
					"\" already in corporate document repository -- upload required " +
					"only if client-side version is newer than " +
					new Date(lastModified) + " (" + lastModified + ")");
			} else {
				System.out.println("> DocumentTopic.published(): file \"" + filename +
					"\" not yet in corporate document repository -- upload required");
			}
		}
		// add directive
		directives.add(DIRECTIVE_UPLOAD_FILE, filename, new Long(lastModified),
			new Integer(FILE_DOCUMENT));
		//
		return directives;
	}
}
