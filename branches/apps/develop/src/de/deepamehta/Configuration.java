package de.deepamehta;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Configuration extends Properties {

	private static final long serialVersionUID = 1L;
	private static Configuration globalInstance;
	private static Logger logger = Logger.getLogger("de.deepamehta");

	private String configDir;
	private String dbTypePropertyFile;

	/**
	 * @param	configFile	path to <CODE>install/config/dm.properties</CODE> file,
	 *						can be absolute or relative to the servlet engines working directory
	 */
	public Configuration(String configFile) {
		this(null, configFile);
	}

	/**
	 * @param	configFile	path to <CODE>install/config/dm.properties</CODE> file,
	 *						can be absolute or relative to the servlet engines working directory
	 */
	public Configuration(String name, String configFile) {
		super();
		if (globalInstance == null) {
			globalInstance = this;
		}
		try {
			loadProperties(configFile);
			configDir = new File(configFile).getAbsoluteFile().getParentFile().getAbsolutePath() + "/";
			dbTypePropertyFile = getProperty(ConfigurationConstants.Database.DB_TYPE_PROPERTY_FILE);
			putAll(System.getProperties());		// ### required?
			loadProperties(configDir + "config.properties");
			putAll(System.getProperties());		// ### required?
			if (null != name) {
				setProperty(ConfigurationConstants.Instance.DM_INSTANCE, name);
			} else {
				// loadProperties(getProperty(ConfigurationConstants.Instance.DM_CONFIG_PROPERTY_FILE));
				name = getProperty(ConfigurationConstants.Instance.DM_INSTANCE);		// ### required?
			}
			resolveReferences();
			loadProperties(configDir + getProperty(ConfigurationConstants.Instance.DM_INSTANCE_PROPERTY_FILE));
			loadProperties(configDir + getProperty(ConfigurationConstants.Instance.DM_INSTANCE_CONFIG_PROPERTY_FILE));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error loading configuration file", e);
		}
		resolveReferences();
	}

	public static Configuration getDbConfig(String dbType) {
		Configuration c = (Configuration) globalInstance.clone();
		c.setProperty(ConfigurationConstants.Database.DB_TYPE_PROPERTY_FILE, globalInstance.dbTypePropertyFile);
		c.setProperty(ConfigurationConstants.Database.DB_TYPE, dbType);
		c.resolveReferences();
		try {
			c.loadProperties(globalInstance.configDir + c.getProperty(ConfigurationConstants.Database.DB_TYPE_PROPERTY_FILE));
			c.resolveReferences();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error loading configuration file", e);
		}
		return c;
	}

	public static Configuration getGlobalConfig() {
		return globalInstance;
	}

	private void loadProperties(String configFile) throws IOException, FileNotFoundException {
		logger.info("loading configuration file \"" + configFile + "\"");
		Properties p = new Properties();
		p.load(new FileInputStream(configFile));
		putAll(p);
	}

	private void resolveReferences() {
		boolean allResolved;
		do {
			allResolved = true;
			Enumeration ks = keys();
			while (ks.hasMoreElements()) {
				String key = (String) ks.nextElement();
				StringBuffer val = new StringBuffer((String) get(key));
				int from;
				boolean replaced = false;
				while ((from = val.indexOf("${")) >= 0) {
					int to = val.indexOf("}", from);
					if (to >= 0) {
						String var = val.substring(from + 2, to);
						String rep = (String) get(var);
						if (null == rep) {
							logger.info("unable to resolve " + var + "! maybe later...");
							break;
						}
						val.replace(from, to + 1, rep);
						replaced = true;
					} else {
						throw new IllegalStateException();
					}
				}
				if (replaced) {
					setProperty(key, val.toString());
					allResolved = false;
				}
			}
		} while (!allResolved);
	}
}
