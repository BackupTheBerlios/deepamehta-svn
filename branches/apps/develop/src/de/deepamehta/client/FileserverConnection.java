package de.deepamehta.client;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.FileServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;



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
 * Last functional change: 16.8.2007 (2.0b8)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
class FileserverConnection implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



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
		//
		System.out.println("> client side socket timeout was " + sock.getSoTimeout() + " ms -- timeout is now disabled");
		sock.setSoTimeout(0);	// disable timeout ### does it help to avoid broken connections?
		//
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
