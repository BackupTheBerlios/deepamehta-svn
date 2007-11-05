package de.deepamehta.service;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.FileServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;



/**
 * For every connected client the server maintains a socket connection for purpose of
 * background file transfers.
 * <P>
 * To represent the fileserver request types there are the
 * {@link FileserverConnection#FS_REQUEST_UPLOAD_FILE FS_REQUEST_XXX constants} defined
 * in {@link de.deepamehta.DeepaMehtaConstants}.
 * <P>
 * ### Besides this connection for every connected client the server maintains 2 further
 * connections simultanously: the
 * {@link InteractionConnection} (used for <I>synchronous</I> communication) and the
 * {@link MessagingConnection} (used for <I>asynchronous</I> communication).
 * <P>
 * <HR>
 * Last functional change: 27.2.2005 (2.0b6)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
class FileserverConnection implements DeepaMehtaConstants, Runnable {



	// **************
	// *** Fields ***
	// **************



	private Session session;
	private DataInputStream in;
	private DataOutputStream out;

	private ApplicationService as;
	private FileServer fileServer;



	// *******************
	// *** Constructor ***
	// *******************



	FileserverConnection(DataInputStream in, DataOutputStream out) {
		this.fileServer = new FileServer();
		this.in = in;
		this.out = out;
	}



	// ******************************************************
	// *** Implementation of Interface java.lang.Runnable ***
	// ******************************************************



	public void run() {
		int connectionState;
		try {
			while ((connectionState = executeCommand(in.read())) == STATE_OK) {
			}
		} catch (IOException e) {
			System.out.println("*** FileserverConnection.run(): " + e +
							   " -- drop fileserver connection");
			connectionState = STATE_ERROR;
		}
		// reporting
		if (connectionState == STATE_TERMINATE) {
			System.out.println("> [" + session.getUserName() + "] fileserver connection " +
				"dropped properly");
		} else {
			System.out.println("*** [" + session.getUserName() + "] fileserver connection " +
				"dropped due to failure");
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		DeepaMehtaServer#setConnection(int sessionID, FileserverConnection con)
	 */
	void setClient(Session session, ApplicationService as) {
		this.session = session;
		this.as = as;
		//
		new Thread(this).start();
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * Dispatches to one of the {@link #performUpload performXXX() methods} according to
	 * the specified command.
	 *
	 * @param	command		The ID of the command to execute,
	 *						see {@link #FS_REQUEST_UPLOAD_FILE FS_REQUEST_XXX constants}
	 * @return	The connection state,
	 *						see {@link #STATE_OK STATE_XXX constants}
	 * @see		#run
	 */
	private int executeCommand(int command) {
		try {
			switch (command) {
			case -1:
				System.out.println("*** FileserverConnection.executeCommand(): " +
					"client has gone away -- drop fileserver connection");
				return STATE_ERROR;
			case FS_REQUEST_UPLOAD_FILE:
				performUpload();
				return STATE_OK;
			case FS_REQUEST_DOWNLOAD_FILE:
				performDownload();
				return STATE_OK;
			case FS_REQUEST_QUEUE_MESSAGE:
				processMessage();
				return STATE_OK;
			case FS_REQUEST_LOGOUT:
				performLogout();
				return STATE_TERMINATE;
			default:
				System.out.println("*** FileserverConnection.executeCommand(): " +
					"unexpected command ID: " + command + " -- drop fileserver connection");
				return STATE_ERROR;
			}
		} catch (IOException e) {
			System.out.println("*** FileserverConnection.executeCommand(): " + e +
				" -- drop fileserver connection");
			return STATE_ERROR;
		}
	}

	private void performUpload() throws IOException {
		// read request parameter
		String filename = in.readUTF();
		int filetype = in.read();
		// log request
		if (LOG_FILESERVER) {
			System.out.println("[" + session.getUserName() + "] FS_REQUEST_UPLOAD_FILE" +
				" \"" + filename + "\" type: " + filetype);
		}
		// create file in corporate document repository resp. corporate icon repository
		File file = new File(FileServer.repositoryPath(filetype) + filename);
		fileServer.readFile(file, in);
		// notify client
		CorporateDirectives directives = new CorporateDirectives();
		directives.add(DIRECTIVE_SET_LAST_MODIFIED, filename,
			new Long(file.lastModified()), new Integer(filetype));
		as.getHostObject().sendDirectives(session, directives, null, null, null);
	}

	private void performDownload() throws IOException {
		// read request parameter
		String filename = in.readUTF();
		int filetype = in.read();
		// log request
		if (LOG_FILESERVER) {
			System.out.println("[" + session.getUserName() + "] FS_REQUEST_DOWNLOAD_FILE" +
				" \"" + filename + "\" type: " + filetype);
		}
		// access file in corporate document repository
		// Presumption: the requested file exists, ensured because the client do not
		// request files they not exist in corporate document repository
		File file = new File(FileServer.repositoryPath(filetype) + filename);
		fileServer.writeFile(file, out);
		// Note: the client is not explicitely notified about completion, it is
		// resposible for setting the file modification date.
		// Compare to client.FileserverConnection.performDownloadRequest()
	}

	private void processMessage() throws IOException {
		String message = in.readUTF();
		System.out.println("[" + session.getUserName() + "] FS_REQUEST_QUEUE_MESSAGE \"" + message + "\"");
		as.processMessage(message, session);
	}

	private void performLogout() {
		System.out.println("[" + session.getUserName() + "] FS_REQUEST_LOGOUT");
	}
}
