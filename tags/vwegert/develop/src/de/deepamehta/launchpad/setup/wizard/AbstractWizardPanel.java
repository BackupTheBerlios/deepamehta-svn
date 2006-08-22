/*
 * Created on 01.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup.wizard;

import java.util.List;

import jwf.WizardPanel;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentFactory;

/**
 * This is the base class for all wizard panels. It provides sensible default implementations
 * for all (well, almost all) of the abstract methods of WizardPanel.
 * @author vwegert
 */
public abstract class AbstractWizardPanel extends WizardPanel {

    protected SetupWizard wizard;
    protected Environment env;
    
    /**
     * The default constructor stores the reference to the parent wizard.
     * @param parent
     */
    public AbstractWizardPanel(SetupWizard parent) {
    	this.env = EnvironmentFactory.getEnvironment(); 
        this.wizard = parent;
    }
    
    /* (non-Javadoc)
     * @see jwf.WizardPanel#hasNext()
     */
    public boolean hasNext() {
        // all panels except the last one have a next panel
        return (this.wizard.getNextPanel(this) != null);
    }
    
    /* (non-Javadoc)
     * @see jwf.WizardPanel#next()
     */
    public WizardPanel next() {
        return this.wizard.getNextPanel(this);
    }

    /* (non-Javadoc)
     * @see jwf.WizardPanel#canFinish()
     */
    public boolean canFinish() {
        // we can only finish on the last panel
        return (this.wizard.getNextPanel(this) == null);
    }

    /* (non-Javadoc)
     * @see jwf.WizardPanel#display()
     */
    public void display() {
        // default implementation does nothing 
    }
    
    /* (non-Javadoc)
     * @see jwf.WizardPanel#finish()
     */
    public void finish() {
        // default implementation does nothing
    }
    
    /* (non-Javadoc)
     * @see jwf.WizardPanel#validateFinish(java.util.List)
     */
    public boolean validateFinish(List list) {
        return true;
    }
    
    /* (non-Javadoc)
     * @see jwf.WizardPanel#validateNext(java.util.List)
     */
    public boolean validateNext(List list) {
        return true;
    }
}
