/*
 * Created on 09.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class represents a class definition consisting of a class name and a location 
 * to load the class from.
 * @author vwegert
 */
public class ClassSpecification {

    private String className, classSource;
    
    /**
     * The default constructor. Does not initialize anything. 
     */
    public ClassSpecification() {
        super();
    }

    /**
     * @return Returns the class name.
     */
    public String getClassName() {
        return this.className;
    }
    /**
     * @param className The class name to set.
     */
    public void setClassName(String className) {
        this.className = className;
    }
    /**
     * @return Returns the class source. This may be either "core" or the name of an external JAR file.
     */
    public String getClassSource() {
        return this.classSource;
    }
    /**
     * @param classSource The class source to set.
     * @see ClassSpecification#setClassSource(String)
     */
    public void setClassSource(String classSource) {
        this.classSource = classSource;
    }

}
