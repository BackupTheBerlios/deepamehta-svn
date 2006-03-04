/*
 * Created on 04.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment.instance;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.Environment;
import de.deepamehta.service.CorporateMemory;

/**
 * This class contains the configuration data required to setup a corporate 
 * memory instance. The contents of this class correspond to the &lt;cm&gt; 
 * element used in the configuration file. The only fixed property of this 
 * class is the implementing class; the entire CM configuration is handled 
 * using a property-value-based approach.  
 * @author vwegert
 */
public class CorporateMemoryConfiguration extends AbstractTableModel {

    private static Log logger = LogFactory.getLog(CorporateMemoryConfiguration.class);
    private String implementingClass;
    private Hashtable properties;
    
    
    /**
     * Default constructor without parameters required for digester. Note: The class
     * will not work properly without the implementing class set.
     */
    public CorporateMemoryConfiguration() {
        this.properties = new Hashtable();
        this.implementingClass = null;
    }
    
    /**
     * This constructor creates a new CM configuration based upon the 
     * implementing class specified.
     * @param implementingClass The name of the class implementing the 
     * interface <code>CorporateMemory</code>.
     */
    public CorporateMemoryConfiguration(String implementingClass) {
        this();
        logger.debug("Creating new CM configuration based on class " + implementingClass);
        this.implementingClass = implementingClass;
    }
    
	/**
	 * @param implementingClass The implementing class to set.
	 */
	public void setImplementingClassName(String implementingClass) {
		this.implementingClass = implementingClass;
	}
    
    /**
     * @return Returns the name of the implementing class.
     */
    public String getImplementingClassName() {
        return this.implementingClass;
    }
    
    /**
     * @return Returns an instance of <code>Class</code> representing the 
     * implementing class;
     */
    public Class getImplementingClass() {
    	if (this.implementingClass != null) {
    		try {
    			return Environment.loadClass(this.implementingClass);
    		} catch (ClassNotFoundException e) {
    			logger.error("Unable to find CM implementation " + this.implementingClass);
    			return null;
    		} 
    	} else {
    		logger.error("No CM implementation provided.");
    		return null;
    	}
    }
    
    /**
     * @return Returns an instance of the implementing class or 
     * <code>null</code> if the class could not be instantiated.
     */
    public CorporateMemory getInstance() {
        Class implementation = getImplementingClass();
        if (implementation != null) {
            try {
                return (CorporateMemory) implementation.newInstance();
            } catch (InstantiationException e) {
                logger.error("Unable to instantiate CM implementation " + this.implementingClass, e);
                return null;
            } catch (IllegalAccessException e) {
                logger.error("Access violation while trying to instantiate CM implementation " + this.implementingClass, e);
                return null;
            }
        } else {
            return null;
        }
    }
    
    /**
     * Changes the value of the property <code>name</code> to <code>value</code>. 
     * @param name The name of the property. 
     * @param value The value to set the property to.
     */
    public void setProperty(String name, String value) {
        logger.debug("Setting property '" + name + "' to value '" + value + "'");
        this.properties.put(name, value);
    }
    
    /**
     * Retrieves the value of a property.
     * @param name The name of the property.
     * @return The value of the property, or <code>null</code> if the property
     * is not set.
     */
    public String getProperty(String name) {
        if (this.properties.containsKey(name)) {
            return (String) this.properties.get(name);
        } else {
            logger.warn("Property '" + name + "' is not set.");
            return ""; 
        }
    }
    
    /**
     * Removes a property and its value from the configuration.
     * @param property The name of the property.
     */
    public void removeProperty(String property) {
        this.properties.remove(property);
        
    }

    /**
     * @return A set of all properties currently specified.
     */
    public Set getProperties() {
        return this.properties.keySet();
    }
    
    /**
     * @return The total number of properties currently specified.
     */
    public int numProperties() {
        return this.properties.size();
    }
    
    /**
     * This method determines whether a property is specified.
     * @param key The name of a property.
     * @return <code>true</code> if the property is specified.
     */
    public boolean containsProperty(Object key) {
        return this.properties.containsKey(key);
    }

    /**
     * Converts the contents of the configuration into DOM objects. This method
     * is used by the instance configuration during serialization to write
     * changes to the configuration file.  
     * @param doc The document that will contain the nodes.
     * @return an Element representing the &lt;cm&gt; element of the
     * configuration file.
     */
    public Node toNode(Document doc) {
        Node me = doc.createElement("cm");
        ((Element) me).setAttribute("class", getImplementingClassName());
        for (Enumeration e = this.properties.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            Node property = doc.createElement("property");
            ((Element) property).setAttribute("name", key);
            ((Element) property).setAttribute("value", getProperty(key));
            me.appendChild(property);
        }
        return me;
    }

	/**
	 * MISSDOC No documentation for method getRowCount of type CorporateMemoryConfiguration
	 * @return
	 */
	public int getRowCount() {
		return numProperties();
	}

	/**
	 * MISSDOC No documentation for method getColumnCount of type CorporateMemoryConfiguration
	 * @return
	 */
	public int getColumnCount() {
		return 2; // name, value
	}

	/**
	 * MISSDOC No documentation for method getValueAt of type CorporateMemoryConfiguration
	 * @param rowIndex
	 * @param columnIndex
	 * @return
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		String key = (String)getProperties().toArray()[rowIndex];
		switch(columnIndex) {
		case 0: return key;
		case 1: return getProperty(key);
		default: return null;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
        switch(column) {
        case 0: return DeepaMehtaMessages.getString("CorporateMemoryConfiguration.ColumnTitleName"); //$NON-NLS-1$
        case 1: return DeepaMehtaMessages.getString("CorporateMemoryConfiguration.ColumnTitleValue"); //$NON-NLS-1$
        default: return super.getColumnName(column);
        }
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex == 1);
	}

}
