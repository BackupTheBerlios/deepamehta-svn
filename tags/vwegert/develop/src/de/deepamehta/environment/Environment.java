/*
 * Created on 30.12.2005
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.swing.ListModel;
import javax.swing.table.TableModel;

import de.deepamehta.environment.application.ApplicationSpecification;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.environment.instance.UnknownInstanceException;
import de.deepamehta.environment.plugin.PluginSpecification;

/**
 * This is the interface of every environment implementation. The environment encapsulates information 
 * about the DeepaMetha configuraton as well as the runtime environment. 
 * @author vwegert
 * @see de.deepamehta.environment.EnvironmentFactory
 */
public interface Environment {

	// ----------------------------------------
	// --- Initialization and Configuration ---
	// ----------------------------------------
	
	/*
	 * Every implementation has to provide a constructor that is used by the factory class.
	 * Note that the constructor may only perform the most basic initialisation!
	 */
	
	/**
	 * This method initializes the environment.
	 * @throws EnvironmentException
	 */
	public void initialize() throws EnvironmentException;
	
	// ----------------------------
	// --- Runtime Environment  ---
	// ----------------------------
	
    /**
     * @return Returns the current class path relative to the current working directory.
     */
    public String getClassPath();
    
    /**
     * @return Returns the path to load endorsed libraries from.
     */
    public String getEndorsedPath();
    
    /**
     * @return Returns the Java home directory.
     */
    public String getJavaHome();
    
    /**
     * @return Returns the platform-dependent JRE executable.
     */
    public String getJavaRuntime();

    /**
     * @return Returns the current working directory. For an instance running its
     * own application service, the working directory is the data directory of the
     * instance, for any other scenario (e. g. the Launch Pad) it is the DeepaMehta 
     * home.
     */
    public String getWorkingDirectory();
    
    /**
     * @return Returns the home directory of the DeepaMehta installation. This is the 
     * path DeepaMehta was installed to. 
     */
    public String getHomeDirectory();

    /**
     * @return Returns the directory that contains the core contents (the XMLS and ZIP 
     * files used during instance bootstrapping).
     */
    public String getContentDirectory();   
    
    /**
     * @return Returns the path separator for the current platform.
     */
    public String getFileSeparator();

	// -----------------------------
	// --- Dynamic Class Loading ---
	// -----------------------------
    
	/**
	 * Adds an external JAR file to the class loader used by the environment.
	 * @param source An URL pointing to the JAR file to load.
	 * @see Environment#loadClass(String)
	 */
	public void loadExternalJAR(URL source);
	
	/**
	 * Similar to <code>Class.forName</code>, but uses the class loader prepared by
	 * <code>loadExternalJar()</code> so that the environment is able to integrate 
	 * external JAR files.
	 * @param name The name of the class to load.
	 * @return Returns an instance of the specified class.
	 * @throws ClassNotFoundException
	 */
	public Class loadClass(String name) throws ClassNotFoundException;
    
	// -------------------------
	// --- Instance Handling ---
	// -------------------------

    /**
     * @return Returns the name of the instance to run. This name may be derived from
     * the command line or any other environmental parameters.  
     */
    public String getInstanceName();
    
    /**
     * @return Returns the configuration of the instance to run.
     * @throws UnknownInstanceException
     * @see Environment#getInstanceName()
     */
    public InstanceConfiguration getInstanceConfiguration() throws UnknownInstanceException;

    /**
     * @return Returns an enumeration of Strings representing valid instance names.
     */
    public Enumeration getInstances();
    
	/**
	 * MISSDOC No documentation for method getInstance of type Environment
	 * @param id
	 * @return
	 * @throws UnknownInstanceException
	 */
	public InstanceConfiguration getInstance(String id) throws UnknownInstanceException;
	
	/**
	 * MISSDOC No documentation for method getInstance of type Environment
	 * @param index
	 * @return
	 */
	public InstanceConfiguration getInstance(int index);
	
	/**
	 * Adds an instance configuration to the list of configurations. Note that the list is 
	 * not saved automatically.
	 * @param instance The instance configuration to add.
	 * @see Environment#saveInstances()
	 */
	public void addInstance(InstanceConfiguration instance);
    
	/**
	 * MISSDOC No documentation for method removeInstance of type Environment
	 * @param id
	 */
	public void removeInstance(String id);
    
	/**
	 * MISSDOC No documentation for method getInstanceModel of type Environment
	 * @return
	 */
	public TableModel getInstanceTableModel();
    
	/**
	 * MISSDOC No documentation for method saveInstances of type Environment
	 */
	public void saveInstances();
    
	// ----------------------------
	// --- Application Handling ---
	// ----------------------------

	/**
	 * @return Returns an Enumeration of Strings representing valid application IDs.
	 */
	public Enumeration getApplications();
	
	/**
	 * MISSDOC No documentation for method getApplication of type Environment
	 * @param id
	 * @return
	 */
	public ApplicationSpecification getApplication(String id);
	
	/**
	 * MISSDOC No documentation for method getApplicationModel of type Environment
	 * @return
	 */
	public ListModel getApplicationListModel();
    
	// -----------------------
	// --- Plugin Handling ---
	// -----------------------

	/**
	 * @return Returns and Enumeration of Strings representing valid plugin IDs.
	 */
	public Enumeration getPlugins();
	
    /**
     * MISSDOC No documentation for method getPlugin of type Environment
     * @param id
     * @return
     */
    public PluginSpecification getPlugin(String id);
    
    /**
     * MISSDOC No documentation for method getPluginListModel of type Environment
     * @return
     */
    public ListModel getPluginListModel();
    
}
