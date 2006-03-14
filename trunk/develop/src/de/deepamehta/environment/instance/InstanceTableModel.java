/*
 * Created on 23.12.2005
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.environment.instance;

import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaMessages;

/**
 * This table model is used to display the instances in a JTable.
 * @author vwegert
 */
public class InstanceTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -4405129123779695114L;
	private static Log logger = LogFactory.getLog(InstanceTableModel.class);
    
    private InstanceManager manager;
	private boolean detailedView;
	
	/** 
	 * default constructor
	 * @param mgr
	 */
	public InstanceTableModel(InstanceManager mgr) 
	{
		super();
		this.detailedView = false;
		this.manager = mgr;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return this.manager.size();
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
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int row, int column) {

		InstanceConfiguration config;
		config = this.manager.get(row);
        
        if (this.detailedView) {
			switch(column) {
			case 0: return null; // TODO icon
			case 1: return config.getId();
			case 2: return config.getDescription();
			case 3: return config.getInstanceType().toString();
			case 4: return config.getConnectionDisplayText();
			case 5: return config.getDatabaseDisplayText();
			}
		} else {
			switch(column) {
			case 0: return null; // TODO icon
			case 1: return config.getDescription();
			}
		}
		return null;
	}

	/**
	 * @return Returns the detailedView.
	 */
	public boolean isDetailedView() {
		return this.detailedView;
	}
	
	/**
	 * @param detailedView The detailedView to set.
	 */
	public void setDetailedView(boolean detailedView) {
		this.detailedView = detailedView;
		fireTableStructureChanged();
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int column) {
		if (this.detailedView) {
			switch(column) {
			case 0: return null; // icon has no title
			case 1: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleID"); 			//$NON-NLS-1$
			case 2: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleDescription"); //$NON-NLS-1$
			case 3: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleType"); 		//$NON-NLS-1$
			case 4: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleConnection");  //$NON-NLS-1$
			case 5: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleDatabase"); 	//$NON-NLS-1$
			}
		} else {
			switch(column) {
			case 0: return null; // icon has no title
			case 1: return DeepaMehtaMessages.getString("InstanceTableModel.ColumnTitleDescription"); //$NON-NLS-1$
			}
		}
		return super.getColumnName(column);
	}
}
