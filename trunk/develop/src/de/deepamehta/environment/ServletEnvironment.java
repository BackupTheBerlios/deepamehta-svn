/**
 * 
 */
package de.deepamehta.environment;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.swing.ListModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.deepamehta.environment.application.ApplicationManager;
import de.deepamehta.environment.application.ApplicationSpecification;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.environment.instance.InstanceManager;
import de.deepamehta.environment.instance.UnknownInstanceException;
import de.deepamehta.environment.plugin.PluginManager;
import de.deepamehta.environment.plugin.PluginSpecification;

/**
 * MISSDOC No documentation for type ServletEnvironment
 * @author vwegert
 *
 */
public class ServletEnvironment extends AbstractEnvironment {

    private static final String DEFAULT_INSTANCE_FILE = "instances.xml";
    private static final String DEFAULT_PLUGIN_FILE = "plugins.xml";

    private static Log logger = LogFactory.getLog(ServerEnvironment.class);

    private ServletContext context;
    
    private String instanceFile, pluginFile, loggerConfigFile;

    private PluginManager plugins;
    private InstanceManager instances;
    private ApplicationManager applications;
	
	// ----------------------------------------
	// --- Initialization and Configuration ---
	// ----------------------------------------
	
    ServletEnvironment(ServletContext context) {
		super();
		this.context = context;
		this.plugins = new PluginManager(this);
		this.applications = new ApplicationManager(this);
		this.instances = new InstanceManager(this);
    }

	public void initialize() throws EnvironmentException {
        parseOptions();
        initializeLogger();
        logJavaDetails();

        this.plugins.loadFromFile(this.pluginFile);
        String appPath = this.getHomeDirectory() + getFileSeparator() + "bin" + getFileSeparator() + "apps"; 
        this.applications.scanApplicationPath(appPath);
            
        // FIXME this is definitely the wrong time to load all the applications - MOVE THIS!
        for(Enumeration e = this.getApplications(); e.hasMoreElements() ; ) {
        	ApplicationSpecification spec = this.getApplication((String) e.nextElement());
        	spec.loadImplementations();
        }		

        this.instances.loadFromFile(this.instanceFile);
	}
	
	/**
	 * MISSDOC No documentation for method parseOptione of type ServletEnvironment
	 */
	private void parseOptions() {
		this.loggerConfigFile = context.getInitParameter("logger_config");
		// this parameter may be null - will use the default config then

		this.instanceFile = context.getInitParameter("instance_config");
		if (this.instanceFile == null)
            this.instanceFile = getHomeDirectory() + getFileSeparator() + DEFAULT_INSTANCE_FILE;

		this.pluginFile = context.getInitParameter("plugin_config");
		if (this.pluginFile == null)
            this.pluginFile = getHomeDirectory() + getFileSeparator() + DEFAULT_PLUGIN_FILE;
	}
	
    /**
     * Initializes the logging framework - either using the configuration file specified on the
     * command line or with default settings
     */
    private void initializeLogger() {
        if (this.loggerConfigFile == null) {
            // no logger configuration specified, use default
    		BasicConfigurator.configure();
    		Logger.getRootLogger().setLevel(Level.INFO);
        } else {
    		PropertyConfigurator.configure(this.loggerConfigFile);
            logger.info("Using logger configuration file " + this.loggerConfigFile);
        }
        logger.info("DeepaMehta starting...");
    }

	// ----------------------------
	// --- Runtime Environment  ---
	// ----------------------------

	public String getWorkingDirectory() {
		return System.getProperty("user.dir");
	}

	public String getHomeDirectory() {
		String hd = context.getInitParameter("home");
		if (hd == null) 
			 hd = System.getProperty("user.dir");
		return hd;
	}
	
	//-------------------------
	// --- Instance Handling ---
	// -------------------------
	
	public String getInstanceName() {
		String instanceName = context.getInitParameter("service");
		if (instanceName == null)
			instanceName = "default";
		return instanceName;
	}
	
	public InstanceConfiguration getInstanceConfiguration() throws UnknownInstanceException {
		return this.instances.get(this.getInstanceName());
	}

	public Enumeration getInstances() {
		return this.instances.keys();
	}

	public InstanceConfiguration getInstance(String id) throws UnknownInstanceException {
		return this.instances.get(id);
	}

	public InstanceConfiguration getInstance(int index) {
		return this.instances.get(index);
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
		return this.applications.keys();
	}

	public ApplicationSpecification getApplication(String id) {
		return this.applications.getApplication(id);
	}

	public ListModel getApplicationListModel() {
		return this.applications;
	}

	// -----------------------
	// --- Plugin Handling ---
	// -----------------------

	public Enumeration getPlugins() {
		return this.plugins.keys();
	}

	public PluginSpecification getPlugin(String id) {
		return this.plugins.getPlugin(id);
	}

	public ListModel getPluginListModel() {
		return this.plugins;
	}
	
	// -----------------------
	// --- utility methods ---
	// -----------------------

}
