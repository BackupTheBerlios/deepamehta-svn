/*
 * Created on 10.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment.instance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.Environment;

/**
 * This class is responsible for managing the instance configuration data at runtime. Its
 * capabilites include loading and storing the instance configuration file.
 * @author vwegert
 */
public class InstanceManager implements TableModel {
    
    public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
    private static Log logger = LogFactory.getLog(InstanceManager.class);
    private Environment env;
    private Hashtable instances;
	private boolean detailedView;
	protected EventListenerList listenerList;
    /**
     * The default constructor to create a new instance manager. 
     * @param parent The parent environment.
     */
    public InstanceManager(Environment parent) {
        super();
        this.env = parent;
        this.instances = new Hashtable();
        this.listenerList = new EventListenerList();
        this.detailedView = false;        
    }

    /**
     * This method parses an instance configuration file and adds the instance configurations
     * to the current set.
     * @param filename The input file to read.
     */
    public void loadFromFile(String filename) {
        
        Vector fileContents;
        
        logger.debug("Trying to load instances from file " + filename + "...");

        Digester digester = new Digester();
        digester.setNamespaceAware(true);
        digester.setValidating(true);
        digester.setSchema(env.getHomeDirectory() + "/bin/schema/InstanceDefinitions.xsd"); // TODO Remove hard-coded path.
        
        digester.addObjectCreate("instances", Vector.class );
        digester.addObjectCreate("instances/instance", InstanceConfiguration.class);
        digester.addSetProperties("instances/instance/", "id", "id");
        digester.addSetProperties("instances/instance/", "description", "description");

//    	TODO do not attempt to parse CM configuration during client startup
//        if (this.env.getEnvironmentType() == EnvironmentType.FAT) {
        	digester.addCallMethod("instances/instance/monolithic", "setInstanceTypeMonolithic");
        	digester.addObjectCreate("instances/instance/monolithic/cm", CorporateMemoryConfiguration.class);
        	digester.addSetProperties("instances/instance/monolithic/cm/", "class", "implementingClassName");
        	digester.addCallMethod("instances/instance/monolithic/cm/property", "setProperty", 2);
        	digester.addCallParam("instances/instance/monolithic/cm/property", 0, "name");
        	digester.addCallParam("instances/instance/monolithic/cm/property", 1, "value");
        	digester.addSetNext("instances/instance/monolithic/cm", "setCMConfig");
        	
        	digester.addCallMethod("instances/instance/server", "setInstanceTypeServer");
        	digester.addSetProperties("instances/instance/server/", "interface", "serverInterface");
        	digester.addSetProperties("instances/instance/server/", "port", "serverPort");
        	digester.addObjectCreate("instances/instance/server/cm", CorporateMemoryConfiguration.class);
        	digester.addSetProperties("instances/instance/server/cm/", "class", "implementingClassName");
        	digester.addCallMethod("instances/instance/server/cm/property", "setProperty", 2);
        	digester.addCallParam("instances/instance/server/cm/property", 0, "name");
        	digester.addCallParam("instances/instance/server/cm/property", 1, "value");
        	digester.addSetNext("instances/instance/server/cm", "setCMConfig");
//        }

        digester.addCallMethod("instances/instance/client", "setInstanceTypeClient");
        digester.addSetProperties("instances/instance/client/", "host", "clientHost");
        digester.addSetProperties("instances/instance/client/", "port", "clientPort");
        
        digester.addSetNext("instances/instance", "add" );

        try {
            fileContents = (Vector) digester.parse(filename);
        } catch (IOException e) {
            logger.error("Unable to parse instance definition file " + filename + " because of I/O error: " + e.getLocalizedMessage());
            fileContents = null;
        } catch (SAXException e) {
            logger.error("Unable to parse instance definition file " + filename + " because of XML parser error.", e);
            fileContents = null;
        }
        
        if (fileContents != null) 
        {
            for (int i = 0; i < fileContents.size(); i++) {
                add((InstanceConfiguration) fileContents.get(i));
            }
        }
        
    }
    
    /**
     * This method stores the current set of configuration data into a file.
     * @param filename The name of the file to write.
     */
    public void saveToFile(String filename) {
    
        logger.debug("Saving instances to file " + filename);
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            logger.error("Unable to initialize XML output components.", e);
            return;
        }

        Document doc = builder.newDocument();

        // root element <dm:instances>
        Node root = doc.createElement("dmi:instances");
        ((Element) root).setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        ((Element) root).setAttribute("xsi:schemaLocation", "http://www.deepamehta.de/schema/InstanceDefinition.xsd InstanceDefinition.xsd");
        ((Element) root).setAttribute("xmlns:dmi", "http://www.deepamehta.de/schema/InstanceDefinition.xsd");
        doc.appendChild(root);
        
        // instance elements
        for (Enumeration e = instances.elements(); e.hasMoreElements();) {
            InstanceConfiguration spec = (InstanceConfiguration) e.nextElement();
            root.appendChild(spec.toNode(doc));
        }
        
        // Use a XSLT transformer for writing the new XML file 
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            FileOutputStream os = new FileOutputStream(new File(filename));
            StreamResult result = new StreamResult( os );
            transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(source, result);
            logger.info("Wrote instance configuration to " + filename + ".");
        } catch (TransformerConfigurationException e1) {
            logger.error("Unable to initialize output XML transformer.", e1);
        } catch (FileNotFoundException e1) {
            logger.error("Unable to write to file " + filename + ".");
        } catch (TransformerFactoryConfigurationError e1) {
            logger.error("Unable to initialize output XML transformer.", e1);
        } catch (TransformerException e1) {
            logger.error("Unable to transform output file.", e1);
        }
         
        fireTableDataChanged();

    }
    
    /**
     * Add a new instance configuration to the current set. 
     * @param configuration The configuration to add.
     */
    public void add(InstanceConfiguration configuration) {
        if (!this.instances.containsKey(configuration.getId())) {
            logger.debug("Registering instance " + configuration.getId() + "...");
            this.instances.put(configuration.getId(), configuration);
        } else {
            logger.debug("Instance " + configuration.getId() + " already registered.");
        }
    }    
    
    /**
     * Retrieves an instance configuration from the current set. 
     * @param id The ID of the configuration
     * @return Returns the instance configuration.
     * @throws UnknownInstanceException This unchecked exception is thrown if the caller
     * tries to retrieve an unknown instance configuration
     */
    public InstanceConfiguration get(String id) throws UnknownInstanceException {
        if (this.instances.containsKey(id)) {
            return (InstanceConfiguration) this.instances.get(id);
        } else {
            throw new UnknownInstanceException(id);
        }
    }

    /**
     * Retrieves an instance configuration by its position as specified by the
     * table model.
     * @param pos
     * @return
     */
    public InstanceConfiguration get(int pos) {
    	try {
			return get((String)this.instances.keySet().toArray()[pos]);
		} catch (UnknownInstanceException e) {
			// This should never happen.
			logger.error("Failed to read an instance configuration specified in the key set.", e);
			return null;
		}
    }
 
    /**
     * @return Returns the number of instances in the current set.
     */
    public int size() {
        return this.instances.size();
    }

	/**
	 * MISSDOC No documentation for method keys of type InstanceManager
	 * @return
	 * @see java.util.Hashtable#keys()
	 */
	public Enumeration keys() {
		return instances.keys();
	}

	/**
	 * @return Returns <code>true</code> if the detailed view is enabled.
	 */
	public boolean isDetailedView() {
		return this.detailedView;
	}
	
	/**
	 * @param detailedView Whether the detailed view should be enabled.
	 */
	public void setDetailedView(boolean detailedView) {
		this.detailedView = detailedView;
		fireTableStructureChanged();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return this.instances.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		if (this.detailedView) {
			return 6; // icon, id, name, type, conn, db
		} else {	
			return 2; // icon, name
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		if (this.detailedView) {
			switch(columnIndex) {
			case 0: return null; // icon has no title
			case 1: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleID"); 			//$NON-NLS-1$
			case 2: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleDescription"); //$NON-NLS-1$
			case 3: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleType"); 		//$NON-NLS-1$
			case 4: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleConnection");  //$NON-NLS-1$
			case 5: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleDatabase"); 	//$NON-NLS-1$
			}
		} else {
			switch(columnIndex) {
			case 0: return null; // icon has no title
			case 1: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleDescription"); //$NON-NLS-1$
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex) {
		if (columnIndex == 0) {
			return ImageIcon.class;
		} else {
			return Object.class;
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// None of the table cells are editable directly.
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		InstanceConfiguration config;
		config = get(rowIndex);
        
        if (this.detailedView) {
			switch(columnIndex) {
			case 0: return config.getInstanceType().getIcon();
			case 1: return config.getId();
			case 2: return config.getDescription();
			case 3: return config.getInstanceType().toString();
			case 4: return config.getConnectionDisplayText();
			case 5: return config.getDatabaseDisplayText();
			}
		} else {
			switch(columnIndex) {
			case 0: return config.getInstanceType().getIcon();
			case 1: return config.getDescription();
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// Since none of the cells are editable, we should never arrive here.
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#addTableModelListener(javax.swing.event.TableModelListener)
	 */
	public void addTableModelListener(TableModelListener l) {
		listenerList.add(TableModelListener.class, l);
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#removeTableModelListener(javax.swing.event.TableModelListener)
	 */
	public void removeTableModelListener(TableModelListener l) {
		listenerList.remove(TableModelListener.class, l);
	}
	
	/**
	 * Notifies all listeners that all cell values in the table's
	 * rows may have changed. The number of rows may also have changed
	 * and the <code>JTable</code> should redraw the
	 * table from scratch. The structure of the table (as in the order of the
	 * columns) is assumed to be the same.
	 *
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.JTable#tableChanged(TableModelEvent)
	 * @see javax.swing.table.AbstractTableModel#fireTableChanged(javax.swing.event.TableModelEvent)
	 */
	public void fireTableDataChanged() {
		fireTableChanged(new TableModelEvent(this));
	}
	
	/**
	 * Notifies all listeners that the table's structure has changed.
	 * The number of columns in the table, and the names and types of
	 * the new columns may be different from the previous state.
	 * If the <code>JTable</code> receives this event and its
	 * <code>autoCreateColumnsFromModel</code>
	 * flag is set it discards any table columns that it had and reallocates
	 * default columns in the order they appear in the model. This is the
	 * same as calling <code>setModel(TableModel)</code> on the
	 * <code>JTable</code>.
	 *
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.table.AbstractTableModel#fireTableStructureChanged()
	 */
	public void fireTableStructureChanged() {
		fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
	}
	
	/**
	 * Notifies all listeners that rows in the range
	 * <code>[firstRow, lastRow]</code>, inclusive, have been inserted.
	 *
	 * @param  firstRow  the first row
	 * @param  lastRow   the last row
	 *
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.table.AbstractTableModel#fireTableRowsInserted(int, int)
	 *
	 */
	public void fireTableRowsInserted(int firstRow, int lastRow) {
		fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
				TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}
	
	/**
	 * Forwards the given notification event to all
	 * <code>TableModelListeners</code> that registered
	 * themselves as listeners for this table model.
	 *
	 * @param e  the event to be forwarded
	 *
	 * @see #addTableModelListener
	 * @see TableModelEvent
	 * @see EventListenerList
	 * @see javax.swing.table.AbstractTableModel#fireTableChanged(javax.swing.event.TableModelEvent)
	 */
	public void fireTableChanged(TableModelEvent e) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==TableModelListener.class) {
				((TableModelListener)listeners[i+1]).tableChanged(e);
			}
		}
	}

}
