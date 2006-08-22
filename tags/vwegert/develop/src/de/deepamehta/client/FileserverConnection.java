package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.FileServer;
//
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultBoundedRangeModel;
import java.net.*;
import java.io.*;
import java.util.*;



/**
 * A socket connection to the server used to handle background file transfers.
 * <P>
 * To represent the fileserver request types there are the
 * {@link FileserverConnection#FS_REQUEST_UPLOAD_FILE FS_REQUEST_XXX constants} defined
 * in {@link de.deepamehta.DeepaMehtaConstants}.
 * <P>
 * Besides this connection the client deployes 2 further connections to the server
 * simultanously: the {@link InteractionConnection} (used for <I>synchronous</I> communication)
 * and the {@link MessagingConnection} (used for <I>asynchronous</I> communication).
 * <P>
 * <HR>
 * Last functional change: 27.2.2005 (2.0b6)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
class FileserverConnection implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	// needed for
	// sending queued messages (ps.sendMessage()),
	// processing queued directives (ps.processDirectives()) and
	// sync modification of downloaded files (ps.setLastModifiedLocally())
	// ### private PresentationService ps;
	private FileServer fileServer;

	private DataInputStream in;
	private DataOutputStream out;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		DeepaMehtaClient#createLoginGUI
	 */
	FileserverConnection(String host, int port, int sessionID, FileServer fileServer) throws IOException {
		Socket sock = new Socket(host, port);
		// ### this.ps = ps;
		this.fileServer = fileServer;
		this.in = new DataInputStream(sock.getInputStream());
		this.out = new DataOutputStream(sock.getOutputStream());
		//
		out.write(CONNECTION_FILESERVER);
		out.writeInt(sessionID);
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * @see		SocketService#downloadFile
	 */
	void downloadFile(String filename, int filetype) throws IOException {
		out.write(FS_REQUEST_DOWNLOAD_FILE);
		out.writeUTF(filename);
		out.write(filetype);
		//
		File file = new File(FileServer.repositoryPath(filetype) + filename);
		fileServer.readFile(file, in);
	}

	/**
	 * @see		SocketService#uploadFile
	 */
	void uploadFile(String filename, int filetype) throws IOException {
		out.write(FS_REQUEST_UPLOAD_FILE);
		out.writeUTF(filename);
		out.write(filetype);
		//
		File file = new File(FileServer.repositoryPath(filetype) + filename);
		fileServer.writeFile(file, out);
	}

	/**
	 * @see		SocketService#processMessage
	 */
	void sendMessage(String message) throws IOException {
		out.write(FS_REQUEST_QUEUE_MESSAGE);
		out.writeUTF(message);
	}

	// ---

	/**
	 * @see		PresentationService#closeApplication
	 */
	void logout() {
		try {
			out.write(FS_REQUEST_LOGOUT);
		} catch (IOException e) {
			System.out.println("*** FileserverConnection.logout(): " + e);
		}
	}
}
