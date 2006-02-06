/*
 * Created on 25.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
class SetupStructureAction implements SetupAction {

    private static Log logger = LogFactory.getLog(SetupStorageAction.class);
    
    private CorporateMemoryConfiguration cmConfig;

    /**
     * Default constructor
     * @param spec The instance configuration to prepare
     */
    public SetupStructureAction(InstanceConfiguration spec) {
        try {
            this.cmConfig = spec.getCMConfig();
        } catch (EnvironmentException e) {
            logger.error("Unable to retrieve CM configuration for instance " + spec.getId(), e);
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
        
        // we need a CM configuration to continue
        if (this.cmConfig == null)
            return false;
        
        return true;
    }

    /* (non-Javadoc)
     * @see de.deepamehta.launchpad.setup.SetupAction#execute()
     */
    public boolean execute() {
        
        // create CM instance
        CorporateMemory cm = this.cmConfig.getInstance();
        if (cm == null) {
            logger.error("Unable to instantiate CM implementation.");
            return false;
        }

        // check (and if necessary initialize) the structures
        if (cm.checkStructure(this.cmConfig)) {
            logger.debug("The structure does not need adjustments.");
        } else {	
            logger.debug("The structure needs adjustments.");
            if (!cm.setupStructure(this.cmConfig)) {
                logger.error("unable to setup structure.");
                return false; 
            }
        }
        return true;
    }
}
