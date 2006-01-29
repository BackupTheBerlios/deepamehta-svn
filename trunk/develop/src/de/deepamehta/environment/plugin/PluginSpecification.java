/*
 * Created on 09.01.2006
 *
 * MISSDOC No header documentation in file PluginSpecification.java
 */
package de.deepamehta.environment.plugin;

import java.util.Vector;

/**
 * @author vwegert
 *
 * MISSDOC No documentation for type 
 */
public class PluginSpecification {

    private String name;
    private ClassSpecification mainClass;
    private Vector preloadClasses, postloadClasses;    
    
    /**
     * Default constructor - create an empty plugin specification- 
     */
    public PluginSpecification() {
        super();
        this.mainClass = new ClassSpecification();
        this.preloadClasses = new Vector();
        this.postloadClasses = new Vector();
    }

    /**
     * @return Returns the main class of the plugin.
     */
    public ClassSpecification getMainClass() {
        return this.mainClass;
    }

    /**
     * @return Returns the name of the plugin.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @param name The plugin name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return Returns the classes to be loaded after the main class is loaded.
     */
    public Vector getPostloadClasses() {
        return this.postloadClasses;
    }
    
    /**
     * @return Returns the classes to be loaded before the main class is loaded.
     */
    public Vector getPreloadClasses() {
        return this.preloadClasses;
    }
    
    /**
     * Adds a class specification to the classes to be loaded after the main class is loaded.
     * @param spec 
     * @return
     */
    public boolean addPostloadClass(ClassSpecification spec) {
        return this.postloadClasses.add(spec);
    }

    /**
     * Adds a class specification to the classes to be loaded before the main class is loaded.
     * @param spec 
     * @return
     */
    public boolean addPreloadClass(ClassSpecification spec) {
        return this.preloadClasses.add(spec);
    }
    
    /**
     * Changes the main class of the plugin.
     * @param mainClass The main class to set.
     */
    public void setMainClass(ClassSpecification mainClass) {
        this.mainClass = mainClass;
    }
}