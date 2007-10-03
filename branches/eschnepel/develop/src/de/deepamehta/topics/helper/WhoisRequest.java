package de.deepamehta.topics.helper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;



/**
 * This class is used for sending requests on whois servers.
 */
public class WhoisRequest {

	// - - - - - - - - - - - - - - - - - - - - - Vars
	
	/**
	 * holds the search term and the results
	 */	
	private StringBuffer query;		// holds the search term and the results.
	/**
	 * holds the query domain
	 */	
	private String targetDomain;	// holds the query domain.
	/**
	 * used to catch input 1 line at a time
	 */	
	private String feed;			// used to catch input 1 line at a time.
	/**
	 * the TCP socket
	 */	
	private Socket socket;			// the TCP socket. 
	/**
	 * reads data in from the socket
	 */	
	private BufferedReader in;		// reads data in from the socket.
	/**
	 * writes data to the socket
	 */	
	private DataOutputStream out;	// writes data to the socket.
	/**
	 * whois server address
	 */	
	private String whoisServer;		// whois server address
		
	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - Whois(String)
	/**
	 * constructor
	 *
	 * @param _whoisServer Whois server URL
	 */
		
	/* *******************************************************
	 * PRE:		
	 * IN:		java.lang.String. The query string.
	 * OUT:		
	 * THROWS:	
	 ********************************************************/
	
	public WhoisRequest(String _whoisServer) {
		this.whoisServer=_whoisServer;
	}
	
	/**
	 * sends request to whois server
	 *
	 * @param _targetDomain domain
	 * @return information from the whois server
	 */

	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - String whois()
	/* *******************************************************
	 * PRE:		
	 * IN:		
	 * OUT:		java.lang.String. The whois daemon reply. With \n's.
	 * THROWS:	IOexception. Socket could not be opened.
	 ********************************************************/
	
	public String whois(String _targetDomain) throws IOException{

		this.targetDomain = _targetDomain;

	/* Initialize some variables. */

		feed = new String("");
		query = new StringBuffer("");

	/* Open a new socket. */

		//socket = new Socket("whois.internic.net",43);
		socket = new Socket(whoisServer,43);

	/* Attatch a BufferedReader to the socket to read in data. */

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	/* Attatch a DataOutputStream to the socket to write data out. */

		out = new DataOutputStream(socket.getOutputStream());

	/* After the connection, send the target query. */

		out.writeBytes(targetDomain+"\n");

	/* Read in the response line by line and then return it to the calling program. */

		while((feed = in.readLine())!=null){
			query.append(feed+"\n");
		}
		return (query.toString());
	}	
}
