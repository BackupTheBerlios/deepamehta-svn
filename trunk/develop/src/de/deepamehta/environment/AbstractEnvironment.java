/**
 * 
 */
package de.deepamehta.environment;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.ListModel;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.environment.application.ApplicationSpecification;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.environment.instance.UnknownInstanceException;
import de.deepamehta.environment.plugin.PluginSpecification;

/**
 * MISSDOC No documentation for type AbstractEnvironment
 * @author vwegert
 *
 */
public abstract class AbstractEnvironment implements Environment {

	private static Log logger = LogFactory.getLog(AbstractEnvironment.class);
	
	private ArrayList externalClassSources;
	private URLClassLoader externalClassLoader = null;
	
	// ----------------------------------------
	// --- Initialization and Configuration ---
	// ----------------------------------------
	
	/**
	 * Default constructor for all environment implementations.
	 */
	protected AbstractEnvironment() {
		externalClassSources = new ArrayList();
	}
	
	// public void initialize() throws EnvironmentException;
	// This method has to be implemented by every environment type.

	// ----------------------------
	// --- Runtime Environment  ---
	// ----------------------------

	public String getClassPath() {
        return System.getProperty("java.class.path");
	}

	public String getEndorsedPath() {
		return System.getProperty("java.endorsed.dirs");
	}

	public String getJavaHome() {
        return System.getProperty("java.home");
	}

	public String getJavaRuntime() {
        // TODO Find some elegant way to determine the Java executable.
        if (System.getProperty("os.name").equals("Windows")) {
            return getJavaHome() + "\\bin\\java.exe";
        } else {
            return getJavaHome() + "/bin/java";
        }
	}

	public String getWorkingDirectory() {
		return System.getProperty("user.dir");
	}

	public String getHomeDirectory() {
		String hd = System.getProperty("de.deepamehta.home");
		if (hd == null) 
			 hd = System.getProperty("user.dir");
		return hd;
	}

	public String getFileSeparator() {
        return System.getProperty("file.separator");
	}

	// -----------------------------
	// --- Dynamic Class Loading ---
	// -----------------------------
    
	public void loadExternalJAR(URL source) {
		if (!externalClassSources.contains(source)) {
			logger.debug("Adding " + source + " to list of class sources.");
			externalClassSources.add(source);
			URL[] urls = new URL[externalClassSources.size()];
			urls = (URL[]) externalClassSources.toArray(urls);
			externalClassLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
		}
	}

	public Class loadClass(String name) throws ClassNotFoundException {
		return Class.forName(name, true, externalClassLoader);
		
		
		
//		Class clazz = null;
//		if (externalClassLoader != null) {
//			try {	
//				clazz = externalClassLoader.loadClass(name);
//			} catch (ClassNotFoundException e) {
//				clazz = null;
//			}
//		}
//		
//		if (clazz == null) {
//			clazz = Class.forName(name);
//		}
//		
//		return clazz;
//		
		
//		if (externalClassLoader == null) {
//			return Class.forName(name);
//		} else {
//			// TODO Ugly method to reverse the class loading order. Rewrite this someday.
////			try {
////				return Class.forName(name, true, externalClassLoader);
////			} catch (Exception e) {
//				return Class.forName(name);
////			}
//		}
	}

	// -------------------------
	// --- Instance Handling ---
	// -------------------------

	public String getInstanceName() {
		throw new UnsupportedFunctionException("This environment implementation does not support instance handling.");
	}

	public InstanceConfiguration getInstanceConfiguration() throws UnknownInstanceException {
		throw new UnsupportedFunctionException("This environment implementation does not support instance handling.");
	}

	public Enumeration getInstances() {
		throw new UnsupportedFunctionException("This environment implementation does not support instance handling.");
	}

	public InstanceConfiguration getInstance(String id) throws UnknownInstanceException {
		throw new UnsupportedFunctionException("This environment implementation does not support instance handling.");
	}

	public InstanceConfiguration getInstance(int index) {
		throw new UnsupportedFunctionException("This environment implementation does not support instance handling.");
	}
	
	public void addInstance(InstanceConfiguration instance) {
		throw new UnsupportedFunctionException("This environment implementation does not support instance handling.");
	}

	public void removeInstance(String id) {
		throw new UnsupportedFunctionException("This environment implementation does not support instance handling.");
	}

	public TableModel getInstanceTableModel() {
		throw new UnsupportedFunctionException("This environment implementation does not support instance handling.");
	}

	public void saveInstances() {
		throw new UnsupportedFunctionException("This environment implementation does not support instance handling.");
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

	// ------------------------------------------------------
	// --- utility methods for use by all derived classes ---
	// ------------------------------------------------------
	
    /**
     * This method generates debugging output with interesting information about the
     * environment DeepaMehta is running in. 
     */
    protected void logJavaDetails() {
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
		} catch (AccessControlException e) {
			logger.error("The VM properties can't be reported because this applet is not signed.");
		}
		
		try {
			logger.debug("Endorsed libraries loaded from " + getEndorsedPath());
			logger.debug("Current classpath is " + getClassPath());        
		} catch (AccessControlException e) {
			logger.error("The path properties can't be reported because this applet is not signed.");
		}
		
        // FIXME report DeepaMehta version (see DeepaMehtaConstants.CLIENT_VERSION and SERVER_VERSION)        
        //		logger.info("DeepaMehta " + CLIENT_VERSION + " runs as application on \"" + ps.hostAddress + "\" (" + ps.platform + ")");
        //        System.out.println("\n--- DeepaMehtaClient " + CLIENT_VERSION + " runs as application on \"" + ps.hostAddress + "\" (" + ps.platform + ") ---");
        //		System.out.println("\n--- DeepaMehta " + CLIENT_VERSION + " runs as applet on \"" + ps.hostAddress + "\" (" + ps.platform + ") ---");

    }
    
}
