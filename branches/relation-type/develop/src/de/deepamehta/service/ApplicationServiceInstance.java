package de.deepamehta.service;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.topics.LiveTopic;
//
import java.sql.SQLException;
import java.util.*;
import java.io.*;



/**
 * Service as read from <CODE>dms.rc</CODE>.
 * <P>
 * <HR>
 * Last functional change: 15.12.2002 (2.0a17-pre3)<BR>
 * Last documentation update: 2.1.2002 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class ApplicationServiceInstance implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	public String name;
	public int port;
	public String cmClass;
	public String cmDriverClass;
	public String cmURL;



	// ***************************
	// *** Private Constructor ***
	// ***************************



	private ApplicationServiceInstance(String name) throws DeepaMehtaException {
		this(name, "dms.rc");
	}

	/**
	 * @param	configFile	path to <CODE>dms.rc</CODE> file, can be absolute or relative to the servlet engines
	 *						working directory
	 */
	private ApplicationServiceInstance(String name, String configFile) throws DeepaMehtaException {
		String port = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(configFile));
			String line;
			// loop through comments
			while (!reader.readLine().startsWith("-----")) {
			}
			//
			boolean found = false;
			StringTokenizer st = null;
			String token;
			// loop throuh listed server instances
			while ((line = reader.readLine()) != null) {
				st = new StringTokenizer(line);
				token = st.nextToken();
				if (token.equals(name)) {
					found = true;
					break;
				}
			}
			// error check 1
			if (!found) {
				throw new DeepaMehtaException("Service \"" + name + "\" not listed in dms.rc");
			}
			// parse the found service
			port = st.nextToken();
			this.name = name;
			this.port = Integer.parseInt(port);
			this.cmClass = st.nextToken();
			this.cmDriverClass = st.nextToken();
			this.cmURL = st.nextToken();
			// error check 2
			if (!cmClass.equals("RelationalCorporateMemory")) {
				throw new DeepaMehtaException("Corporate Memory implementation \"" +
					cmClass + "\" not supported");
			}
		} catch (NumberFormatException e) {
			throw new DeepaMehtaException("Service \"" + name + "\" has " +
				"invalid port \"" + port + "\"");
		} catch (IOException e) {
			throw new DeepaMehtaException("error while reading " + e.getMessage());
		}
	}



	// ***************
	// *** Methods ***
	// ***************



	// --- lookup (3 forms) ---

	/**
	 * References checked: 17.5.2002 (2.0a15-pre2)
	 *
	 * @see		DeepaMehtaServer#main
	 * @see		DeepaMehta#initApplication
	 */
	public static ApplicationServiceInstance lookup(String[] args) throws DeepaMehtaException {
		if (args.length == 0) {
			return lookup("default");
		} else if (args.length == 1) {
			return lookup(args[0]);
		} else {
			throw new DeepaMehtaException("Too many parameters\n>>> Usage: dms|dm [service]\n>>> see dms.rc");
		}
	}

	/**
	 * References checked: 17.5.2002 (2.0a15-pre2)
	 *
	 * @see		DeepaMehta#init
	 */
	public static ApplicationServiceInstance lookup(String name) throws DeepaMehtaException {
		return new ApplicationServiceInstance(name);
	}

	/**
	 * References checked: 17.5.2002 (2.0a15-pre2)
	 *
	 * @see		DeepaMehtaServlet#init
	 */
	public static ApplicationServiceInstance lookup(String name, String path) throws DeepaMehtaException {
		return new ApplicationServiceInstance(name, path);
	}

	// ---

	/**
     * Establishes access to {@link CorporateMemory corporate memory} for this application
     * service.
     * <P>
	 * References checked: 26.1.2005 (2.0b5)
	 *
     * @throws	DeepaMehtaException if one of this errors occurrs
     *			<UL>
     *				<LI>the driver class can't be found
     *				<LI>the version of the corporate memory content doesn't match with the application service
     *			</UL>
     *
	 * @see		ApplicationService#create
	 */
	CorporateMemory createCorporateMemory() throws DeepaMehtaException {
		CorporateMemory cm;		// returned object
        //
        String errText = "error while establishing access to corporate memory";
		try {
            // --- establish access to corporate memory ---
			// ### implementing class is hardcoded for now ("RelationalCorporateMemory")
			cm = new RelationalCorporateMemory(cmDriverClass, cmURL);
            // error check 1: standard topics compatibility ### move to LCM.create
            int kernelVersion = LiveTopic.kernelTopicsVersion;
            if (kernelVersion != REQUIRED_STANDARD_TOPICS) {
                throw new DeepaMehtaException("DeepaMehta Service " + SERVER_VERSION +
                    " requires standard topics version " + REQUIRED_STANDARD_TOPICS +
                    " but version " + kernelVersion + " is installed");
            }
            // --- get content version ---
            int cmModel = cm.getModelVersion();		// throws DME
            int cmContent = cm.getContentVersion();	// throws DME
            //
            System.out.println(">    model/content version: " + cmModel + "." + cmContent);
            // error check 2: corporate memory compatibility
            if (cmModel != REQUIRED_DB_MODEL || cmContent != REQUIRED_DB_CONTENT) {
                throw new DeepaMehtaException("DeepaMehta Service " + SERVER_VERSION +
                    " requires corporate memory " + REQUIRED_DB_MODEL + "." +
                    REQUIRED_DB_CONTENT + " but " + cmModel + "." + cmContent +
                    " is installed");
            }
		} catch (SQLException e) {	// ### not tested
			String hint = e.getMessage().indexOf("Bad Handshake") != -1 ?
				" -- Probably the driver is too old" +
				(cmDriverClass.equals("org.gjt.mm.mysql.Driver") ?
				".\n*** Get a new driver from www.gjt.org/download/. " +
				"Look for \"MM MySQL - JDBC Compliant Driver For MySQL\"." : "") : "";
			throw new DeepaMehtaException(errText + " (" + e + ")" + hint);
		} catch (ClassNotFoundException e) {
			throw new DeepaMehtaException(errText + ": " + "the driver class can't be found (" +
                e.getMessage() + ")");
		} catch (Throwable e) {
			throw new DeepaMehtaException(errText + " (" + e + ")");
		}
		//
		return cm;
	}
}
