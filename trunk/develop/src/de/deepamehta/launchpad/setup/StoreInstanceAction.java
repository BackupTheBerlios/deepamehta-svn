/*
 * Created on 25.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentException;
import de.deepamehta.environment.instance.InstanceConfiguration;

/**
 * This class stores the newly created instance configuration in the central configuration file.
 * @author vwegert
 */
class StoreInstanceAction extends AbstractSetupAction {

    private static Log logger = LogFactory.getLog(StoreInstanceAction.class);
    
    /**
     * Default constructor
     * @param config The instance configuration to use.
     */
    public StoreInstanceAction(InstanceConfiguration config) {
    	super(config);
        this.config = config;
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#getDescription()
     */
    public String getDescription() {
        return DeepaMehtaMessages.getString("StoreInstanceAction.SetupDescription"); //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#canExecute()
     */
    public boolean canExecute() {
		this.messages.clear();
        // right now, there's no reason not to execute this step 
        return true;
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#execute()
     */
    public boolean execute() {
		this.messages.clear();
        try {
            if (!this.config.getInstanceType().isClient()) {
                // remove the admin properties
                this.config.getCMConfig().removeProperty("dba_user");
                this.config.getCMConfig().removeProperty("dba_password");
            }
            // save instance configuration
            env.addInstance(this.config);
            env.saveInstances();
        } catch (EnvironmentException e) {
            addErrorMessage("Unable to save new instance.", e);
            return false;
        }
        
        return true;
    }

}
