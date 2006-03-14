/*
 * Created on 09.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment.plugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import de.deepamehta.environment.ClassSpecification;
import de.deepamehta.environment.Environment;

/**
 * This class manages the plugins that can be loaded by the DeepaMehta core at runtime.
 * It is responsible for both parsing the plugin configuration file and loading the plugins.  
 * @author vwegert
 */
public class PluginManager implements ListModel {

    private static Log logger = LogFactory.getLog(PluginManager.class);
    private Environment env;
    private Hashtable plugins;
    private EventListenerList listenerList = new EventListenerList();
    
    /**
     * The default constructor to create a new plugin manager. 
     */
    public PluginManager(Environment parent) {
        super();
        this.env = parent;
        this.plugins = new Hashtable();
    }
    
    /**
     * Reads a plugin configuration file and loads the plugins.
     * @param filename The configuration file to read.
     */
    public void loadFromFile(String filename) {
        
        Vector fileContents;
        
        logger.debug("Trying to load plugins from file " + filename + "...");

        Digester digester = new Digester();
        digester.setNamespaceAware(true);
        digester.setValidating(true);
        digester.setSchema(env.getHomeDirectory() + "/bin/schema/PluginDefinition.xsd"); // TODO Remove hard-coded path.

        digester.addObjectCreate("plugins", Vector.class );
        digester.addObjectCreate("plugins/plugin", PluginSpecification.class);
        digester.addSetProperties("plugins/plugin/", "name", "name");
        
        digester.addObjectCreate("plugins/plugin/preload", ClassSpecification.class );
        digester.addSetProperties("plugins/plugin/preload/", "class", "className" );
        digester.addSetProperties("plugins/plugin/preload/", "loadFrom", "classSource" );
        digester.addSetNext("plugins/plugin/preload", "addPreloadClass" );

        digester.addObjectCreate("plugins/plugin/main", ClassSpecification.class );
        digester.addSetProperties("plugins/plugin/main/", "class", "className" );
        digester.addSetProperties("plugins/plugin/main/", "loadFrom", "classSource" );
        digester.addSetNext("plugins/plugin/main", "setMainClass" );

        digester.addObjectCreate("plugins/plugin/postload", ClassSpecification.class );
        digester.addSetProperties("plugins/plugin/postload/", "class", "className" );
        digester.addSetProperties("plugins/plugin/postload/", "loadFrom", "classSource" );
        digester.addSetNext("plugins/plugin/postload", "addPostloadClass" );

        digester.addSetNext("plugins/plugin", "add" );

        try {
            fileContents = (Vector) digester.parse(filename);
        } catch (IOException e) {
            logger.error("Unable to parse plugin definition file " + filename + " because of I/O error.", e);
            fileContents = null;
        } catch (SAXException e) {
            logger.error("Unable to parse plugin definition file " + filename + " because of XML parser error.", e);
            fileContents = null;
        }
        
        if (fileContents != null) 
        {
            for (int i = 0; i < fileContents.size(); i++) {
                addPlugin((PluginSpecification) fileContents.get(i));
            }
        }
        
    }

    /**
     * Adds a plugin specification to the list of plugins and loads the plugin itself.
     * @param specification
     */
    private void addPlugin(PluginSpecification specification) {
        
        if (!this.plugins.containsKey(specification.getMainClass().getClassName())) {
            logger.debug("Loading plugin " + specification.getName() + "...");
            try {
            	// TODO Load preload and postload classes too.

    			try {
    				String source = specification.getMainClass().getClassSource();
    				if (!source.equals("core")) {
    					if (!source.startsWith(env.getFileSeparator())) {
    						// TODO How about Windoze?
    						source = env.getHomeDirectory() + env.getFileSeparator() + source;
    					}
    					env.loadExternalJAR(new URL("file://" + source)); // TODO URL assembly?
    				}
    			} catch (MalformedURLException e) {
    				logger.error("Unable to load JAR " + specification.getMainClass().getClassSource(), e);
    			}
                env.loadClass(specification.getMainClass().getClassName());
                this.plugins.put(specification.getMainClass().getClassName(), specification);
            } catch (ClassNotFoundException e) {
                logger.error("Unable to load plugin " + specification.getName(), e);
			}
        } else {
            logger.debug("Plugin " + specification.getName() + " already loaded.");
        }
    }

	/* (non-Javadoc)
	 * @see java.util.Hashtable#get(java.lang.Object)
	 */
	public PluginSpecification getPlugin(String key) {
		return (PluginSpecification) plugins.get(key);
	}

	/* (non-Javadoc)
	 * @see java.util.Hashtable#keys()
	 */
	public Enumeration keys() {
		return plugins.keys();
	}

	public int getSize() {
		return plugins.size();
	}

	public Object getElementAt(int index) {
	  	return this.plugins.keySet().toArray()[index];
	}

	public void addListDataListener(ListDataListener l) {
		listenerList.add(ListDataListener.class, l);		
	}

	public void removeListDataListener(ListDataListener l) {
		listenerList.remove(ListDataListener.class, l);		
	}
    
    
}
