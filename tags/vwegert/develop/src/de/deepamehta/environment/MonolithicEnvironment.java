/**
 * 
 */
package de.deepamehta.environment;

import java.util.Enumeration;
import java.util.List;

import javax.swing.ListModel;
import javax.swing.table.TableModel;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
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
 * MISSDOC No documentation for type MonolithicEnvironment
 * @author vwegert
 *
 */
public class MonolithicEnvironment extends AbstractEnvironment {

    private static final String DEFAULT_INSTANCE_FILE = "instances.xml";
    private static final String DEFAULT_PLUGIN_FILE = "plugins.xml";

    private static Log logger = LogFactory.getLog(MonolithicEnvironment.class);

    private String instanceFile, pluginFile, loggerConfigFile;

    private PluginManager plugins;
    private InstanceManager instances;
    private ApplicationManager applications;

	private String[] commandLineArguments;
	private List otherArguments;
	
	// ----------------------------------------
	// --- Initialization and Configuration ---
	// ----------------------------------------
	
	MonolithicEnvironment(String[] args) {
		super();
		this.commandLineArguments = args;
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
     * Parses the command line options and sets the instance variables accordingly. Note: This
     * method may cause the entire program to exit either if the command line parsing went wrong
     * or if the usage description is requested. This is the main reason why the environment 
     * should be initialized before any other component.
     */
    private void parseOptions() {
       
        Options options;
        CommandLineParser parser;
        CommandLine line = null;

        // define which options to recognize
        options = new Options();
        options.addOption(createOption("h", "help", "show the command line options available"));
        options.addOption(createOption("l", "logger-config", "use given filename as logger configuration", "loggerconfigfile"));
        options.addOption(createOption("i", "instances", "use given filename instead of the default " + DEFAULT_INSTANCE_FILE, "instancefile"));
        options.addOption(createOption("p", "plugins", "use given filename instead of the default " + DEFAULT_PLUGIN_FILE, "pluginfile"));
        
        // parse the command line arguments
        parser = new PosixParser();
        try {
            line = parser.parse(options, commandLineArguments);
            this.otherArguments = line.getArgList();
        }
        catch(ParseException exp) {
            System.err.println("Unable to parse command line. Reason: " + 
                    exp.getMessage() + ". Use -h or --help to get an overview of all options available.");
            System.exit(1);
        }
        
        // evaluate options
        if (line.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("TODO", options); // TODO Write some nice usage text.
            System.exit(0); 
        }
         
        if (line.hasOption("l")) {
            this.loggerConfigFile = line.getOptionValue("l");
        }
        if (line.hasOption("i")) {
            this.instanceFile = line.getOptionValue("i");
        } else {
            this.instanceFile = getHomeDirectory() + getFileSeparator() + DEFAULT_INSTANCE_FILE;
        }
        if (line.hasOption("p")) {
            this.pluginFile = line.getOptionValue("p");
        } else {
            this.pluginFile = getHomeDirectory() + getFileSeparator() + DEFAULT_PLUGIN_FILE;
        }
        
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

	// -------------------------
	// --- Instance Handling ---
	// -------------------------

	
	public String getInstanceName() {
		String instanceName;
	    switch (otherArguments.size()) {
	    case 0:
	        logger.debug("No instance name given, assuming default.");
	        instanceName = "default";
	        break;
	    case 1:
	        instanceName = (String) otherArguments.get(0);
	        break;
	    default:
	        logger.warn("Multiple command line arguments given - I'll choose the first one as the instance name and ignore the rest.");
	        instanceName = (String) otherArguments.get(0);
	        break;
	    }
	    
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

	public InstanceConfiguration getInstance(int index)  {
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

	/**
     * Convenience method to create a command line option without parameter.
     * @param shortOpt The short form of the option (e. g. 'h' for '-h').
     * @param longOpt The long option text (e. g. 'help' for '--help').
     * @param desc The description used when printing the usage information.
     * @return An Option object.
     */
    protected static Option createOption(String shortOpt, String longOpt, String desc) {
        return OptionBuilder.withDescription(desc).withLongOpt(longOpt).create(shortOpt);
    }

    /**
     * Convenience method to create a command line option with parameter.
     * @param shortOpt The short form of the option (e. g. 'h' for '-h').
     * @param longOpt The long option text (e. g. 'help' for '--help').
     * @param desc The description used when printing the usage information.
     * @param argName The name of the argument holding the parameter information.
     * @return An Option object.
     */
    protected static  Option createOption(String shortOpt, String longOpt, String desc, String argName) {
        return OptionBuilder.withArgName(argName).hasArg().withDescription(desc).withLongOpt(longOpt).create(shortOpt);
    }
}
