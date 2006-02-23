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

	private static Log logger = LogFactory.getLog(ClassSpecification.class);
	
    private String className, classSource;
    private URLClassLoader loader;
    private Class clazz;
    
    /**
     * The default constructor. Does not initialize anything. 
     */
    public ClassSpecification() {
        super();
    }

    /**
     * @return Returns the className.
     */
    public String getClassName() {
        return this.className;
    }
    /**
     * @param className The className to set.
     */
    public void setClassName(String className) {
        this.className = className;
    }
    /**
     * @return Returns the classSource.
     */
    public String getClassSource() {
        return this.classSource;
    }
    /**
     * @param classSource The classSource to set.
     */
    public void setClassSource(String classSource) {
        this.classSource = classSource;
    }

}
