package de.deepamehta.service;

import de.deepamehta.Configuration;
import de.deepamehta.ConfigurationConstants;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.service.db.DatabaseProviderFactory;
import de.deepamehta.service.web.DeepaMehtaServlet;
import de.deepamehta.topics.LiveTopic;

import java.sql.SQLException;
import java.util.Properties;



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
	private Properties conf;



	// ***************************
	// *** Private Constructor ***
	// ***************************



	private ApplicationServiceInstance(String name) throws DeepaMehtaException {
		this(name, "../config/dm.properties");
	}

	/**
	 * @param	configFile	path to <CODE>install/config/dm.properties</CODE> file,
	 *						can be absolute or relative to the servlet engines working directory
	 */
	private ApplicationServiceInstance(String name, String configFile) throws DeepaMehtaException {
		String port = null;
		try {
			conf = new Configuration(name, configFile);
			//
			this.name = conf.getProperty(ConfigurationConstants.Instance.DM_INSTANCE);
			port = conf.getProperty(ConfigurationConstants.Instance.DM_PORT);
			this.port = Integer.parseInt(port);
			this.cmClass = conf.getProperty(ConfigurationConstants.Instance.DM_COPRORATE_MEMORY_CLASS);
			// error check 2
			if (!cmClass.equals("RelationalCorporateMemory")) {
				throw new DeepaMehtaException("Corporate Memory implementation \"" +
					cmClass + "\" not supported");
			}
		} catch (NumberFormatException e) {
			throw new DeepaMehtaException("Service \"" + name + "\" has " +
				"invalid port \"" + port + "\"", e);
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
			return lookup((String) null);
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
	public CorporateMemory createCorporateMemory() throws DeepaMehtaException {
		CorporateMemory cm;		// returned object
        //
        String errText = "error while establishing access to corporate memory";
		try {
            // --- establish access to corporate memory ---
			// ### implementing class is hardcoded for now ("RelationalCorporateMemory")
			cm = new RelationalCorporateMemory(DatabaseProviderFactory.getProvider(conf));
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
				" -- Probably the driver is too old" : "";
			throw new DeepaMehtaException(errText + " (" + e + ")" + hint, e);
		} catch (ClassNotFoundException e) {
			throw new DeepaMehtaException(errText + ": " + "the driver class can't be found (" +
                e.getMessage() + ")", e);
		} catch (DeepaMehtaException e) {
			throw e;
		} catch (Throwable e) {
			throw new DeepaMehtaException(errText, e);
		}
		//
		return cm;
	}

	public String getConfigurationProperty(String property) {
		return conf.getProperty(property);
	}
}
