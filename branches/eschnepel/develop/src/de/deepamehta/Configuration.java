package de.deepamehta;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class Configuration extends Properties {

	private static final long serialVersionUID = 1L;

	public Configuration(String configFile) {
		this(null, configFile);
	}

	public Configuration(String name, String configFile) {
		try {
			loadProperties(configFile);
			putAll(System.getProperties());
			if (null != name) {
				setProperty(ConfigurationConstants.Instance.DM_INSTANCE, name);
			} else {
				name = getProperty(ConfigurationConstants.Instance.DM_INSTANCE);
			}
			resolveReferences();
			loadProperties(getProperty(ConfigurationConstants.Instance.DM_INSTANCE_PROPERTY_FILE));
			resolveReferences();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadProperties(String configFile) throws IOException,
			FileNotFoundException {
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
							System.out.println("unable to resolve " + var
									+ "! maybe later...");
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
