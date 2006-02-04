/*
 * Created on 25.01.2006
 *
 * This file belongs to the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup;

import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.environment.instance.InstanceConfiguration;

/**
 * This class is a central controller for the bootstrapping process. It is responsible for
 * assembling a number of actions that are executed sequentially and that, if completed
 * successfully, create a new DeepaMehta instance.
 * @author vwegert
 */
public class ActionList {

    private static Log logger = LogFactory.getLog(ActionList.class);
    private Vector actions;
    
    /**
     * Default constructor.
     */
    public ActionList() {
        this.actions = new Vector();
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
            // check and if necessary prepare storage area
            this.actions.addElement(new SetupStorageAction(config));
            // check and if necessary prepare structures
            this.actions.addElement(new SetupStructureAction(config));
            // check and if necessary prepare basic contents
            this.actions.addElement(new SetupContentsAction(config));
            // create and populate working directory
            this.actions.addElement(new SetupWorkingDirAction(config));
        }        
        
        // all types: store configuration
        this.actions.addElement(new StoreInstanceAction(config));
        
    }
        
    /**
     * This method executes the list of steps that has been prepared using #prepare().
     * @return <code>true</code> if all of the actions were executed successfully.
     */
    public boolean execute() {
        for (Iterator iter = this.actions.iterator(); iter.hasNext();) {
            SetupAction action = (SetupAction) iter.next();
            if (!action.canExecute()) {
                logger.error("Unable to execute setup action '" + action.getDescription() + "'. Cannot continue setup.");
                return false;
            } else {
                logger.debug("Executing action '" + action.getDescription() + "'...");
                if (!action.execute()) {
                    logger.error("Something went wrong during execution of setup action '" + action.getDescription() + "'. Cannot continue setup.");
                    return false;
                }
            }            
        } 
        return true;
    }
    
}
