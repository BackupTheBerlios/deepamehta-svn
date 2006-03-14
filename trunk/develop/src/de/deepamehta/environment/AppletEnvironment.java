/**
 * 
 */
package de.deepamehta.environment;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;

import javax.swing.JApplet;
import javax.swing.ListModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Jdk14Logger;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.environment.application.ApplicationSpecification;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.environment.instance.UnknownInstanceException;
import de.deepamehta.environment.plugin.PluginSpecification;

/**
 * MISSDOC No documentation for type AppletEnvironment
 * @author vwegert
 *
 */
public class AppletEnvironment extends AbstractEnvironment implements DeepaMehtaConstants {

	private static final String DEFAULT_INSTANCE_ID = "default";
	
	private static Log logger = LogFactory.getLog(AppletEnvironment.class);
	
	private JApplet parent;
	private InstanceConfiguration config;

	// ----------------------------------------
	// --- Initialization and Configuration ---
	// ----------------------------------------
	
	AppletEnvironment(JApplet parent) {
		super();
		this.parent = parent;
	}

	public void initialize() throws EnvironmentException {
        initializeLogger();
        logJavaDetails();
        setupInstanceConfiguration();
	}
	
    /**
     * Initializes the logging framework.
     */
    private void initializeLogger() {
    	// We know that we have a Jdk14Logger, so...
    	// FIXME Initializing the applet logger doesn't work.
//    	if (parent.getParameter("LogLevel") != null) {
//    		try {
//    			((Jdk14Logger)logger).getLogger().getParent().setLevel(Level.parse(parent.getParameter("LogLevel")));
//    		} catch (IllegalArgumentException e) {
//        		((Jdk14Logger)logger).getLogger().setLevel(Level.INFO);
//        		logger.warn(parent.getParameter("LogLevel") + " isn't a valid log level - falling back to the default INFO.");
//    		}
//    	} else {
//    		((Jdk14Logger)logger).getLogger().setLevel(Level.INFO);
//    	}
    	logger.info("DeepaMehta starting...");
    }
    
    /**
     * Creates a virtual instance configuration that will be used by the applet.
     */
    private void setupInstanceConfiguration() {
    	this.config = new InstanceConfiguration();
    	this.config.setId(DEFAULT_INSTANCE_ID);
    	this.config.setDescription("default applet configuration");
    	this.config.setInstanceTypeClient();
    	this.config.setClientHost(this.parent.getCodeBase().getHost());
    	if (parent.getParameter("PORT") != null) {
    		this.config.setClientPort(Integer.parseInt(this.parent.getParameter("PORT")));
    	} else {
    		this.config.setClientPort(DEFAULT_PORT);
    	}
    }

	// -------------------------
	// --- Instance Handling ---
	// -------------------------

	
	public String getInstanceName() {
		return DEFAULT_INSTANCE_ID;
	}
	
	public InstanceConfiguration getInstanceConfiguration() throws UnknownInstanceException {
		return this.config;
	}

	public Enumeration getInstances() {
		Hashtable t = new Hashtable();
		t.put(DEFAULT_INSTANCE_ID, this.config);
		return t.keys();
	}

	public InstanceConfiguration getInstance(String id) throws UnknownInstanceException {
		if (id.equals(DEFAULT_INSTANCE_ID)) {
			return this.config;
		} else {
			throw new UnknownInstanceException("The applet environment only supports the default instance configuration.");
		}
	}

	public InstanceConfiguration getInstance(int index) {
		if (index == 0) {
			return this.config;
		} else {
			return null;
		}
	}
	
	public void addInstance(InstanceConfiguration instance) {
		throw new UnsupportedFunctionException("This environment implementation does not support instance file editing.");
	}

	public void removeInstance(String id) {
		throw new UnsupportedFunctionException("This environment implementation does not support instance file editing.");
	}

	public TableModel getInstanceTableModel() {
		throw new UnsupportedFunctionException("This environment implementation does not support instance file editing.");
	}

	public void saveInstances() {
		throw new UnsupportedFunctionException("This environment implementation does not support instance file editing.");
	}

	// ----------------------------
	// --- Application Handling ---
	// ----------------------------

	public Enumeration getApplications() {
		throw new UnsupportedFunctionException("This environment implementation does not support application handling.");
	}

	public ApplicationSpecification getApplication(String id) {
		throw new UnsupportedFunctionException("This environment implementation does not support application handling.");
	}

	public ListModel getApplicationListModel() {
		throw new UnsupportedFunctionException("This environment implementation does not support application handling.");
	}

	// -----------------------
	// --- Plugin Handling ---
	// -----------------------

	public Enumeration getPlugins() {
		throw new UnsupportedFunctionException("This environment implementation does not support plugin handling.");
	}

	public PluginSpecification getPlugin(String id) {
		throw new UnsupportedFunctionException("This environment implementation does not support plugin handling.");
	}

	public ListModel getPluginListModel() {
		throw new UnsupportedFunctionException("This environment implementation does not support plugin handling.");
	}

}
