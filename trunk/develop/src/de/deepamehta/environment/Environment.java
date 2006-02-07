/*
 * Created on 30.12.2005
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment;

import java.util.Iterator;
import java.util.List;

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

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.environment.instance.InstanceManager;
import de.deepamehta.environment.instance.InstanceType;
import de.deepamehta.environment.instance.UnknownInstanceException;
import de.deepamehta.environment.plugin.PluginManager;

/**
 * This class encapsulates information about the DeepaMetha configuraton 
 * as well as the runtime environment. 
 * @author vwegert
 */
public class Environment implements DeepaMehtaConstants {

    public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private static final String DEFAULT_INSTANCE_FILE = "instances.xml";
    private static final String DEFAULT_PLUGIN_FILE = "plugins.xml";
    private static Log logger = LogFactory.getLog(Environment.class);
    private static Environment singleton = null;
    private InstanceType instanceType;
    private PluginManager plugins;
    private InstanceManager instances;
    private String workingDirectory;
    private String instanceFile, pluginFile, loggerConfigFile;
    private List args; // anything left over after command line parsing
    
    
    /**
     * This is the internal constructor to initialize the one and only singleton 
     * instance of the Environment class. 
     * @param args command line arguments
     * @param type the instance type to startup
     */
    private Environment(String[] args, InstanceType type) {
        
        this.workingDirectory = System.getProperty("user.dir");
        this.instanceType = type;
        parseOptions(args);
        initializeLogger();
        
        logger.info("DeepaMehta starting...");
        // FIXME report DeepaMehta version (see DeepaMehtaConstants.CLIENT_VERSION and SERVER_VERSION)        
        //		logger.info("DeepaMehta " + CLIENT_VERSION + " runs as application on \"" + ps.hostAddress + "\" (" + ps.platform + ")");
        //        System.out.println("\n--- DeepaMehtaClient " + CLIENT_VERSION + " runs as application on \"" + ps.hostAddress + "\" (" + ps.platform + ") ---");
        //		System.out.println("\n--- DeepaMehta " + CLIENT_VERSION + " runs as applet on \"" + ps.hostAddress + "\" (" + ps.platform + ") ---");

        logJavaDetails();
        if (this.instanceType != InstanceType.CLIENT)
        	initializePluginManager();
        initializeInstanceManager();
    }
    
    
 
    /**
     * Convenience method to create a command line option without parameter.
     * @param shortOpt The short form of the option (e. g. 'h' for '-h').
     * @param longOpt The long option text (e. g. 'help' for '--help').
     * @param desc The description used when printing the usage information.
     * @return An Option object.
     */
    private Option createOption(String shortOpt, String longOpt, String desc) {
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
    private Option createOption(String shortOpt, String longOpt, String desc, String argName) {
        return OptionBuilder.withArgName(argName).hasArg().withDescription(desc).withLongOpt(longOpt).create(shortOpt);
    }
    
    /**
     * Parses the command line options and sets the instance variables accordingly. Note: This
     * method may cause the entire program to exit either if the command line parsing went wrong
     * or if the usage description is requested. This is the main reason why the environment 
     * should be initialized before any other component.
     * @param cmdLineArguments The command line arguments to process.
     */
    private void parseOptions(String[] cmdLineArguments) {
       
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
            line = parser.parse( options, cmdLineArguments );
            this.args = line.getArgList();
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
            this.instanceFile = DEFAULT_INSTANCE_FILE;
        }
        if (line.hasOption("p")) {
            this.pluginFile = line.getOptionValue("p");
        } else {
            this.pluginFile = DEFAULT_PLUGIN_FILE;
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
    }

    /**
     * This method generates debugging output with interesting information about the
     * environment DeepaMehta is running in. 
     */
    private void logJavaDetails() {
        logger.debug("Endorsed libraries loaded from " + getEndorsedPath());
        logger.debug("Current classpath is " + getClassPath());        
		try {
			// first get the properties to prevent partial output in case of exception
			String specVendor    = System.getProperty("java.specification.vendor");
			String specName      = System.getProperty("java.specification.name");
			String specVersion   = System.getProperty("java.specification.version");
			String vmSpecVendor  = System.getProperty("java.vm.specification.vendor");
			String vmSpecName    = System.getProperty("java.vm.specification.name");
			String vmSpecVersion = System.getProperty("java.vm.specification.version");
			String vmVendor      = System.getProperty("java.vm.vendor");
			String vmName        = System.getProperty("java.vm.name");
			String vmVersion     = System.getProperty("java.vm.version");
			
			logger.debug("Java Runtime Environment specification: " + 
			        specVendor + "/" + specName + "/" + specVersion);
			logger.debug("Java Virtual Machine specification: " +
			        vmSpecVendor + "/" + vmSpecName + "/" + vmSpecVersion);
			logger.debug("Java Virtual Machine implementation: " +
			        vmVendor + "/" + vmName + "/" + vmVersion);
		} catch (Exception e) {
			logger.error("The VM properties can't be reported because this applet is not signed.", e);
		}
    }

    /**
     * Initializes the plugin manager and loads the plugin configuration file.
     */
    private void initializePluginManager() {
        this.plugins = new PluginManager();
        this.plugins.loadFromFile(this.pluginFile);
    }

    /**
     * Initializes the instance manager and loads the instance configuration file.
     */
    private void initializeInstanceManager() {
        this.instances = new InstanceManager(this);
        this.instances.loadFromFile(this.instanceFile);
    }

    /**
     * Initializes the environment by parsing the command line arguments and initializing
     * the associated components. This method is supposed to be called exactly once during 
     * the initialization of the process.
     * @param args The command line arguments.
     * @return Returns the singleton instance of the environment. 
     */
    public static Environment getEnvironment(String[] args, InstanceType type) {
        if (singleton == null) {
            singleton = new Environment(args, type);
        } 
        return singleton;
    }
    
    /**
     * @return Returns the one and only singleton instance of the environment.
     */
    public static Environment getEnvironment() {
        if (singleton == null) {
            throw new EnvironmentNotInitializedException();
        } else {
            return singleton;
        }
    }

    /**
     * @return Returns the current class path relative to the current working directory.
     */
    public String getClassPath() {
        return System.getProperty("java.class.path").replaceAll(getWorkingDirectory() + "/", "");
    }
    
    /**
     * @return Returns the path to load endorsed libraries from.
     */
    public String getEndorsedPath() {
        return System.getProperty("java.endorsed.dirs");
    }
    
    /**
     * @return Returns the Java home directory.
     */
    public String getJavaHome() {
        return System.getProperty("java.home");
    }
    
    /**
     * @return Returns the platform-dependent JRE executable.
     */
    public String getJavaRuntime() {
        // TODO Find some elegant way to determine the Java executable.
        if (System.getProperty("os.name").equals("Windows")) {
            return getJavaHome() + "\\bin\\java.exe";
        } else {
            return getJavaHome() + "/bin/java";
        }
    }

    /**
     * @return Returns the current working directory.
     */
    public String getWorkingDirectory() {
        return this.workingDirectory;
    }
    
    /**
     * @return Returns the name of the instance configuration file.
     */
    public String getInstanceFile() {
        return this.instanceFile;
    }
    
    /**
     * @return Returns the instance of the instance manager.
     */
    public InstanceManager getInstances() {
        return this.instances;
    }
    
    /**
     * @return Returns the name of the plugin configuration file.
     */
    public String getPluginFile() {
        return this.pluginFile;
    }
    
    /**
     * @return Returns the instance of the plugin manager.
     */
    public PluginManager getPlugins() {
        return this.plugins;
    }
    
    /**
     * @param index An index between 0 and <code>numArguments() - 1</code>.
     * @return Returns the argument specified by the index parameter.
     */
    public String getArgument(int index) {
        return (String) this.args.get(index);
    }
    
    /**
     * @return Returns <code>true</code> if any arguments were specified on the command line.
     */
    public boolean hasArguments() {
        return !(this.args.isEmpty());
    }
    
    /**
     * @return Returns an interator to loop over the command line arguments.
     */
    public Iterator argumentIterator() {
        return this.args.iterator();
    }
    
    /**
     * @return Returns the number of arguments left after the options have been processed.
     */
    public int numArguments() {
        return this.args.size();
    }

    /**
     * @return Returns the path separator for the current platform.
     */
    public String getFileSeparator() {
        return System.getProperty("file.separator");
    }

    /**
     * @return Returns the name of the ZIP file used for bootstrapping the working directory
     * of a newly created instance.
     */
    public String getInstanceDataSourceFile() {
        return getWorkingDirectory() + getFileSeparator() + "bin" 
        		+ getFileSeparator() + "instance-data.zip"; 
        // TODO Remove hard-coded ZIP file name and path.
    }
    
    /**
     * This method tries to guess which instance should be started. If the command line contains 
     * one or several arguments that have not been filtered by the CLI parser, the first argument
     * is selected as instance name. If no instance name is provided by the caller, the instance
     * name 'default' is returned.
     * @return The name of the instance to be started.
     */
    public String guessInstanceName() {
        
        String instanceName;
        
	    switch (numArguments()) {
	    case 0:
	        logger.debug("No instance name given, assuming default.");
	        instanceName = "default";
	        break;
	    case 1:
	        instanceName = getArgument(0);
	        break;
	    default:
	        logger.warn("Multiple command line arguments given - I'll choose the first one as the instance name and ignore the rest.");
	        instanceName = getArgument(0);
	        break;
	    }
	    
	    return instanceName;

    }
    
    /**
     * A convenience method that does essentially the same thing as 
     * <code>guessInstanceName()</code>, but returns the instance configuration instead
     * of the name.
     * @see de.deepamehta.environment.Environment#guessInstanceName()
     * @return Returns the instance configuration object specified by the caller. 
     * @throws UnknownInstanceException 
     */
    public InstanceConfiguration guessInstance() throws UnknownInstanceException  {
        return getInstances().get(guessInstanceName());
    }



	/**
	 * @return Returns the instance type.
	 */
	public InstanceType getInstanceType() {
		return this.instanceType;
	}
}
