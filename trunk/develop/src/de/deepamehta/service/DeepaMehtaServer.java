package de.deepamehta.service;

import java.awt.HeadlessException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaUtils;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.environment.instance.InstanceType;
import de.deepamehta.environment.instance.UnknownInstanceException;



/**
 * A server application which provides the application service via a dedicated
 * TCP port. Client communication performes via TCP sockets.
 * <P>
 * Running client sessions are listed in the server console window. The server is shutdown
 * by closing its console window.
 * <P>
 * <HR>
 * Last functional change: 22.9.2004 (2.0b3)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
final class DeepaMehtaServer implements ApplicationServiceHost, DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************

	private static Log logger = LogFactory.getLog(DeepaMehtaServer.class);
	
	private Environment env;
	private InstanceConfiguration instanceConfig;

	private ApplicationService as;

	private ServerSocket socket;
	private ServerConsole console;
	//
	private Vector externalConnections = new Vector();
	//
	private static final String errText = "DeepaMehtaServer can't run";



	// ***************************
	// *** Private Constructor ***
	// ***************************


	private DeepaMehtaServer() {
		// The server is only instantiated using its static main method.
	}



	// ********************************************************************************
	// *** Implementation of interface de.deepamehta.service.ApplicationServiceHost ***
	// ********************************************************************************



	public String getCommInfo() {
		return "port " + this.instanceConfig.getServerPort();
	}

	public void sendDirectives(Session session, CorporateDirectives directives,
							   ApplicationService as, String topicmapID, String viewmode) {
		MessagingConnection con = (MessagingConnection) session.getCommunication();
		con.sendDirectives(directives, as, topicmapID, viewmode);
	}

	/**
	 * @see		CorporateDirectives#synchronizeTopics
	 */
	public void broadcastChangeNotification(String topicID) {
		System.out.println(">>> DeepaMehtaServer.broadcastChangeNotification(): topicID=\"" + topicID +
			"\", " + externalConnections.size() + " open external connections");
		Enumeration e = externalConnections.elements();
		while (e.hasMoreElements()) {
			ExternalConnection con = (ExternalConnection) e.nextElement();
			con.sendUpdateNotification(topicID);
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	/**
	 * Main method used to start the server process.
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		DeepaMehtaServer dms = new DeepaMehtaServer();
		if (dms.initApplication(args))
			dms.runServer(); // endless loop right now, no external shutdown
		System.exit(1);

	}

	/**
	 * Server-specific initialization.
	 * @param args The command line arguments
	 * @return <code>true</code> if the server is ready to run.
	 */
	private boolean initApplication(String[] args) {
		
		try {
			// initialize environment
			this.env = Environment.getEnvironment(args, InstanceType.SERVER);
			logger.info("Running as standalone server.");
			
			// say hello
			if (LOG_MEM_STAT) {
				DeepaMehtaUtils.memoryStatus();
			}
			
			// retrieve instance configuration
			this.instanceConfig = this.env.guessInstance();
			logger.info("Starting instance " + this.instanceConfig.getId() + "...");
			
			// initialize server socket
			// TODO use server interface too
			this.socket = new ServerSocket(this.instanceConfig.getServerPort());
			
			// initialize application service
			this.as = ApplicationService.create(this, this.instanceConfig);
			
			// --- create server console ---
			try {
				this.console = new ServerConsole(this.as);
				this.as.setGraphicsContext(this.console);
			} catch (HeadlessException e) {
				logger.warn("Note: the graphical server console is not available (the server is running in headless mode). Kill the server process for shutdown.", e);
			} catch (InternalError e) {
				logger.warn("Note: the graphical server console is not available. Kill the server process for shutdown.", e);
			}
			
			return true;
		} catch (UnknownInstanceException e) {
			logger.error("Unable to load the instance specified.", e);
			return false;
		} catch (BindException e) {
			logger.error("The socket is already in use.", e);
			return false;
		} catch (IOException e) {
			logger.error("Unable to initialize server socket.", e);
			return false;
		}
	}

	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @see		#main
	 */
	private void runServer() {
		if (LOG_MEM_STAT) {
			DeepaMehtaUtils.memoryStatus();
		}
		System.out.println("--- DeepaMehtaServer started successfully ---");
		Connection con;
		int sessionID;
		// accepts clients -- endless loop
		while (true) {
			try {
				con = new Connection(socket.accept());
				switch (con.type) {
				case CONNECTION_INTERACTION:
					// get new session ID
					sessionID = as.getNewSessionID();
					// send session ID and server version. the session ID is -1 if the server refuses the connection
					// because the maximum number of connected clients has been reached.
					con.out.writeInt(sessionID);
					con.out.writeUTF(SERVER_VERSION);
					as.writeInstallationProps(con.out);
					//
					if (sessionID != -1) {
						as.createSession(sessionID, con.clientName, con.clientAddress);
						//
						setConnection(sessionID, new InteractionConnection(sessionID, con.in, con.out, as));
					} else {
						System.out.println("*** DeepaMehtaServer.runServer(): there are already " +
							MAX_CLIENTS + " clients connected -- connection refused");
					}
					break;
				case CONNECTION_FILESERVER:
					sessionID = con.in.readInt();
					setConnection(sessionID, new FileserverConnection(con.in, con.out));
					break;
				case CONNECTION_MESSAGING:
					sessionID = con.in.readInt();
					setConnection(sessionID, new MessagingConnection(con.in, con.out));
					break;
				case CONNECTION_TYPE:
					sessionID = con.in.readInt();
					setConnection(sessionID, new TypeConnection(con.in, con.out));
					break;
				case CONNECTION_EXTERNAL:
					externalConnections.addElement(new ExternalConnection(as, con.in, con.out));
					break;
				default:
					throw new DeepaMehtaException("unexpected connection type: " + con.type);
				}
			} catch (Throwable e) {
				System.out.println("*** DeepaMehtaServer.runServer(): " + e);
			}
		}
	}

	// --- setConnection (4 forms) ---

	/**
	 * @see		#runServer
	 */
	private void setConnection(int sessionID, InteractionConnection con) {
		Session session = as.getSession(sessionID);
		// ### session.interactionCon = con;
		//
		con.setClient(session);
	}

	/**
	 * @see		#runServer
	 */
	private void setConnection(int sessionID, FileserverConnection con) {
		Session session = as.getSession(sessionID);
		// ### session.fileserverCon = con;
		//
		con.setClient(session, as);
	}

	/**
	 * @see		#runServer
	 */
	private void setConnection(int sessionID, MessagingConnection con) {
		Session session = as.getSession(sessionID);
		session.setCommunication(con);
		// ### session.messagingCon = con;
		//
		con.setClient(session, as);
	}

	/**
	 * @see		#runServer
	 */
	private void setConnection(int sessionID, TypeConnection con) {
		Session session = as.getSession(sessionID);
		//
		con.setClient(session, as);
	}



	// *********************
	// *** Inner Classes ***
	// *********************



	/**
	 * An upcoming connection to a client.
	 */
	private class Connection {

		DataInputStream in;
		DataOutputStream out;
		int type;
		String clientName;
		String clientAddress;
	
		Connection(Socket sock) throws IOException {
			InetAddress clientAddress = sock.getInetAddress();
			this.in = new DataInputStream(sock.getInputStream());
			this.out = new DataOutputStream(sock.getOutputStream());
			this.type = in.read();
			this.clientName = clientAddress.getHostName();
			this.clientAddress = clientAddress.getHostAddress();
			//
			System.out.println(DeepaMehtaUtils.getDate() + " " + DeepaMehtaUtils.getTime(true) +
				" client connected from " + clientAddress + ", connection type: " + type);
		}
	}
}
