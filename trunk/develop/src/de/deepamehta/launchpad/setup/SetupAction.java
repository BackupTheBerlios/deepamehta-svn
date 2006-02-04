/*
 * Created on 25.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup;

/**
 * Every action class that is used by the action list has to implement this interface. 
 * @author vwegert
 * @see ActionList
 */
public interface SetupAction {
    
	
	
    /**
     * @return Returns a textual single-line description of the action that can be
     * presented to the user.
     */
    String getDescription();
    
    /**
     * @return <code>false</code> if any static condition might prevent the action from
     * being executed. Note: Do NOT check for prerequisites here.
     * @see ActionList#execute()
     */
    boolean canExecute();
    
    /**
     * Executes the action encorporated by the class.
     * @return <code>true</code> if the action was completed successfully.
     */
    boolean execute();

}
