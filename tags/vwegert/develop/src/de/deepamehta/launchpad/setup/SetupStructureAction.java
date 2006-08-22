/*
 * Created on 25.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.EnvironmentException;
import de.deepamehta.environment.instance.CorporateMemoryConfiguration;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.service.CorporateMemory;

/**
 * This class is responsible for creating the structures of a corporate memory storage area.
 * @author vwegert
 * @see de.deepamehta.service.CorporateMemory#checkStructure(CorporateMemoryConfiguration)
 * @see de.deepamehta.service.CorporateMemory#setupStructure(CorporateMemoryConfiguration)
 */
class SetupStructureAction extends AbstractSetupAction {

    private static Log logger = LogFactory.getLog(SetupStorageAction.class);
    
    private CorporateMemoryConfiguration cmConfig;

    /**
     * Default constructor
     * @param config The instance configuration to prepare
     */
    public SetupStructureAction(InstanceConfiguration config) {
    	super(config);
        try {
            this.cmConfig = config.getCMConfig();
        } catch (EnvironmentException e) {
            logger.error("Unable to retrieve CM configuration for instance " + config.getId(), e);
            this.cmConfig = null;
        }
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#getDescription()
     */
    public String getDescription() {
    	if (this.cmConfig.getImplementingClassName().equals("de.deepamehta.service.RelationalCorporateMemory")) {
    		return DeepaMehtaMessages.getString("SetupStructureAction.SetupRelationalDescription"); //$NON-NLS-1$
    	} else {
    		return DeepaMehtaMessages.getString("SetupStructureAction.SetupGenericDescription"); //$NON-NLS-1$
    	}
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#canExecute()
     */
    public boolean canExecute() {
        this.messages.clear();
        // we need a CM configuration to continue
        if (this.cmConfig == null) {
        	addErrorMessage("Unable to determine Corporate Memory configuration.");
        	return false;
        }
        
        return true;
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#execute()
     */
    public boolean execute() {
        this.messages.clear();
        // create CM instance
        CorporateMemory cm = this.cmConfig.getInstance();
        if (cm == null) {
            addErrorMessage("Unable to instantiate CM implementation.");
            return false;
        }

        // check (and if necessary initialize) the structures
        if (cm.checkStructure(this.cmConfig)) {
            logger.debug("The structure does not need adjustments.");
        } else {	
            logger.debug("The structure needs adjustments.");
            try {
            	cm.setupStructure(this.cmConfig);
            } catch (DeepaMehtaException e) {
                addErrorMessage("Unable to setup structure.", e);
                return false; 
            }
        }
        return true;
    }
}
