/*
 * Created on 25.01.2006
 *
 * This file belongs to the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.environment.instance.InstanceConfiguration;

/**
 * This class is a central controller for the bootstrapping process. It is responsible for
 * assembling a number of actions that are executed sequentially and that, if completed
 * successfully, create a new DeepaMehta instance.
 * @author vwegert
 */
public class ActionList implements ListModel {

    private static Log logger = LogFactory.getLog(ActionList.class);
    private Vector actions;
    private ArrayList messages;
    private DefaultBoundedRangeModel progressModel;
    
    /**
     * Default constructor.
     */
    public ActionList() {
        this.actions = new Vector();
        this.messages = new ArrayList();
        this.progressModel = new DefaultBoundedRangeModel();
        this.progressModel.setMinimum(0);
        this.progressModel.setExtent(1);
    }	
    
    
    /**
     * This method takes an instance configuration and creates a list of steps that are
     * necessary to create a new instance with this configuration "from a scratch". The 
     * steps can then be presented to the user and/or executed by other methods.
     * @param config The instance configuration to use
     */
    public void prepare(InstanceConfiguration config) {
    
        // throw away old actions
        this.actions.clear();
        
        // MONOLITHIC or SERVER only
        if (!config.getInstanceType().isClient()) {
            // create and populate working directory
            this.actions.addElement(new SetupWorkingDirAction(config));
            // check and if necessary prepare storage area
            this.actions.addElement(new SetupStorageAction(config));
            // check and if necessary prepare structures
            this.actions.addElement(new SetupStructureAction(config));
            // check and if necessary prepare basic contents
            this.actions.addElement(new SetupContentsAction(config));
        }        
        
        // all types: store configuration
        this.actions.addElement(new StoreInstanceAction(config));
        
        // update progress model
        this.progressModel.setMaximum(this.actions.size());
        this.progressModel.setValue(0);
    }
        
    /**
     * @return Returns the descriptions of the actions.
     */
    public String[] getDescriptions() {
    	
    	ArrayList descriptions = new ArrayList();
    	
    	for (Iterator iter = this.actions.iterator(); iter.hasNext();) {
			SetupAction action = (SetupAction) iter.next();
			descriptions.add(action.getDescription());
		}

    	return (String[]) descriptions.toArray();
    }
    
    
    /**
     * This method executes the list of steps that has been prepared using #prepare().
     * @return <code>true</code> if all of the actions were executed successfully.
     */
    public boolean execute() {
    	String[] msg;
    	this.messages.clear();
        for (Iterator iter = this.actions.iterator(); iter.hasNext();) {
            SetupAction action = (SetupAction) iter.next();
            if (!action.canExecute()) {
                this.messages.add("Unable to execute setup action '" + action.getDescription() + "'.'");
                msg = action.getErrorMessage();
                for (int i = 0; i < msg.length; i++) 
                    this.messages.add(msg[i]);
                this.messages.add("Cannot continue setup.");
                return false;
            } else {
                logger.debug("Executing action '" + action.getDescription() + "'...");
                if (!action.execute()) {
                    this.messages.add("Something went wrong during execution setup action '" + action.getDescription() + "'.'");
                    msg = action.getErrorMessage();
                    for (int i = 0; i < msg.length; i++) 
                        this.messages.add(msg[i]);
                    this.messages.add("Cannot continue setup.");
                    return false;
                }
            }    
            this.progressModel.setValue(this.progressModel.getValue() + 1);
        } 
        return true;
    }


	public int getSize() {
		return this.actions.size();
	}


	public Object getElementAt(int index) {
		return ((SetupAction)this.actions.get(index)).getDescription();
	}


	public void addListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
	}


	public void removeListDataListener(ListDataListener l) {
		// TODO Auto-generated method stub
	}


	/**
	 * @return Returns the progress model.
	 */
	public DefaultBoundedRangeModel getProgressModel() {
		return progressModel;
	}
    
	public String[] getMessages() {
		return (String[]) this.messages.toArray(new String [0]);
	}
	
}
