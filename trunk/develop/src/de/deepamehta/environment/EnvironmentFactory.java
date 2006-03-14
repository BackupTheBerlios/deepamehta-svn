/**
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment;

import javax.servlet.ServletContext;
import javax.swing.JApplet;

/**
 * This factory class provides a set of static methods to obtain an Environment instance.
 * It ensures that only a single instance is created and wraps the creation of the
 * different environment types.
 * @author vwegert
 *
 */
public class EnvironmentFactory {

	private static Environment singleton = null;
	
	/**
	 * The default constructor is private because the factory is 
	 * not supposed to be instantiated.
	 */
	private EnvironmentFactory() {
		// This class is not supposed to be instantiated.
	}
	
	/**
	 * This method returns the singleton instance of the environment. It assumes that the
	 * environment has already been initialized using one of the specialized methods.
	 * @return Returns the current environment. 
	 */
	public static Environment getEnvironment() {
		if (singleton == null) {
			throw new EnvironmentNotInitializedException();
		} else {
			return singleton;
		}
	}
	
	/**
	 * Initializes a new environment for use by the launch pad.
	 * @param args The command line arguments to parse.
	 * @return Returns the newly created environment.
	 * @throws EnvironmentException 
	 */
	public static Environment getLaunchPadEnvironment(String[] args) throws EnvironmentException {
		singleton = new LaunchPadEnvironment(args);
		singleton.initialize();
		return singleton;
	}
	
	/**
	 * Initializes a new environment for use by a monolithic instance.
	 * @param args The command line arguments to parse.
	 * @return Returns the newly created environment.
	 * @throws EnvironmentException 
	 */
	public static Environment getMonolithicEnvironment(String[] args) throws EnvironmentException {
		singleton = new MonolithicEnvironment(args);
		singleton.initialize();
		return singleton;
	}
	
	/**
	 * Initializes a new environment for use by a server instance.
	 * @param args The command line arguments to parse.
	 * @return Returns the newly created environment.
	 * @throws EnvironmentException 
	 */
	public static Environment getServerEnvironment(String[] args) throws EnvironmentException {
		singleton = new ServerEnvironment(args);
		singleton.initialize();
		return singleton;
	}
	
	/**
	 * Initializes a new environment for use by a standalone client instance.
	 * @param args The command line arguments to parse.
	 * @return Returns the newly created environment.
	 * @throws EnvironmentException 
	 */
	public static Environment getClientEnvironment(String[] args) throws EnvironmentException {
		singleton = new ClientEnvironment(args);
		singleton.initialize();
		return singleton;
	}
	
	/**
	 * Initializes a new environment for use by a servlet instance.
	 * @param context The servlet context provided by the container.
	 * @return Returns the newly created environment.
	 * @throws EnvironmentException 
	 */
	public static Environment getServletEnvironment(ServletContext context) throws EnvironmentException {
		singleton = new ServletEnvironment(context);
		singleton.initialize();
		return singleton;
	}
	
	/**
	 * Initializes a new environment for use by an applet instance.
	 * @param parent The parent applet.
	 * @return Returns the newly created environment.
	 * @throws EnvironmentException 
	 */
	public static Environment getAppletEnvironment(JApplet parent) throws EnvironmentException {
		singleton = new AppletEnvironment(parent);
		singleton.initialize();
		return singleton;
	}
	

	
	
}
