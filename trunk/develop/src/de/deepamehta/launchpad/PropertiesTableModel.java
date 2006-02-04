/*
 * Created on 05.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad;

import javax.swing.table.AbstractTableModel;

import de.deepamehta.environment.instance.CorporateMemoryConfiguration;

/**
 * Table model to display the properties of a CM configuration in a JTable.
 * @author vwegert
 */
public class PropertiesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -7670536304543068630L;
    private CorporateMemoryConfiguration config;
     
    /**
     * default constructor
     */
    public PropertiesTableModel() {
        super();
        this.config = null;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        if (this.config == null) {
            return 0; 
        } else {
            return this.config.numProperties();
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 2; // name, value
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int column) {
        String key;
        
        if (this.config == null) {
            return null;
        } else {
            key = (String)this.config.getProperties().toArray()[row];
            switch(column) {
            case 0: return key;
            case 1: return this.config.getProperty(key);
            default: return null;
            }
        }
    }

    /**
     * @return Returns the configuration.
     */
    public CorporateMemoryConfiguration getConfig() {
        return this.config;
    }
    
    /**
     * @param config The configuration to set.
     */
    public void setConfig(CorporateMemoryConfiguration config) {
        this.config = config;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int column) {
        switch(column) {
        case 0: return "name";
        case 1: return "value";
        default: return super.getColumnName(column);
        }
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int row, int column) {
        return (column == 1);
    }
}
