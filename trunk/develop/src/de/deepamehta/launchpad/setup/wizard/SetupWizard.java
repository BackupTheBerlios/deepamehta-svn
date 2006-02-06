/*
 * Created on 01.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup.wizard;

import java.awt.HeadlessException;
import java.util.Enumeration;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import jwf.Wizard;
import jwf.WizardListener;
import jwf.WizardPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.environment.instance.CorporateMemoryConfiguration;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.environment.instance.InstanceType;
import de.deepamehta.launchpad.setup.ActionList;
import de.deepamehta.service.CorporateMemory;

/**
 * This is the main class of the graphic setup wizard.
 * @author vwegert
 */
public class SetupWizard implements WizardListener {

    private static Log logger = LogFactory.getLog(SetupWizard.class);
    
    private JFrame dialog;
    private Wizard wizard;
    private ActionList actions;
    
    private WelcomePanel   				pWelcome = null;
    private SetupTypePanel 				pSetupType = null;
    private InstanceTypePanel 			pInstanceType = null;
    private CorporateMemoryPanel 		pCorporateMemory = null;
    private RelationalConnectionPanel 	pRelationalConnection = null;
    private AdminCredentialsPanel 		pAdminCredentials = null;
    private ServerConnectionPanel 		pServerConnection = null;
    private ClientConnectionPanel 		pClientConnection = null;
    private InstanceNamePanel           pInstanceName = null;
    private ConfirmationPanel 			pConfirmation = null;
    
    /**
     * Default constructor.
     * @param parent The parent window.
     * @throws HeadlessException @see JFrame#JFrame(java.lang.String)
     */
    public SetupWizard(JFrame parent) throws HeadlessException {
        
        logger.debug("Preparing setup wizard...");
                
        this.wizard = new Wizard();
        this.wizard.addWizardListener(this);
      
        // FIXME The wizard should be modal. 
        // For some unknown reason, using a JDialog disturbs the JWF so much that it ceases to work.
        // dialog = new JDialog(parent, "DeepaMehta Setup Wizard", true);
        this.dialog = new JFrame(Messages.getString("SetupWizard.Title")); //$NON-NLS-1$
        this.dialog.setContentPane(this.wizard);
        this.dialog.pack();
        this.dialog.setSize(600, 500);
        this.dialog.setLocation(50, 50);
        this.dialog.setResizable(false);
        
        // TODO Pressing enter in the wizard should move to the next page.
        // TODO It is not possible to click on the texts to activate the radio buttons.
        
        this.actions = new ActionList();
    }
    
    /**
     * This method shows the frame and starts the wizard.
     */
    public void run() {
        logger.debug("Starting setup wizard.,.");
        this.dialog.setVisible(true);
        this.wizard.start(getWelcomePanel());
    }
    
    /**
     * This method determines which page of the wizard should be shown after the current page.
     * @param current The current page of the wizard
     * @return Returns the next page to be shown
     */
    public WizardPanel getNextPanel(AbstractWizardPanel current) {
        
        InstanceType type;
        
        if (current.equals(getWelcomePanel())) {
        	return getSetupTypePanel();
        }
        
        if (current.equals(getSetupTypePanel())) {
            if (getSetupTypePanel().useDefault()) {
                return getAdminCredentialsPanel();
            } else {
                return getInstanceTypePanel();
            }
        }
        
        if (current.equals(getInstanceTypePanel())) {
            type = getInstanceTypePanel().getInstanceType();
            if (type.isMonolithic())
                return getCorporateMemoryPanel();
            if (type.isServer())
                return getCorporateMemoryPanel();
            if (type.isClient())
                return getClientConnectionPanel();
        	return null; // should never be reached
        }
        
        if (current.equals(getCorporateMemoryPanel())) {
            if (getCorporateMemoryPanel().getCMClass().equals("de.deepamehta.service.RelationalCorporateMemory"))
                return getRelationalConnectionPanel();
            // TODO re-write this some day....
        }
        
        if (current.equals(getRelationalConnectionPanel())) {
            return getAdminCredentialsPanel();
        }
        
        if (current.equals(getAdminCredentialsPanel())) {
            if (getSetupTypePanel().useDefault()) {
                // default setup: proceed to confirmation panel
                this.actions.prepare(getInstanceSpecification());
                getConfirmationPanel().setModel(this.actions);
                return getConfirmationPanel();
            } else {
                // manual configuration
                type = getInstanceTypePanel().getInstanceType();
                if (type.isMonolithic())
                    return getInstanceNamePanel();
                if (type.isServer())
                    return getServerConnectionPanel();
                return null; // should never be reached
            }
        }
        
        if (current.equals(getServerConnectionPanel())) {
            return getInstanceNamePanel();
        }
        
        if (current.equals(getClientConnectionPanel())) {
            return getInstanceNamePanel();            
        }
        
        if (current.equals(getInstanceNamePanel())) {
            this.actions.prepare(getInstanceSpecification());
            getConfirmationPanel().setModel(this.actions);
            return getConfirmationPanel();
        }
        
        return null;
    }
    
    /**
     * This method assembles the instance configuration from the wizard panels. 
     * @return Returns the instance configuration containing all the information 
     * entered by the user.
     */
    private InstanceConfiguration getInstanceSpecification() {
        
        InstanceConfiguration instance;
        CorporateMemoryConfiguration cmConfig;
        
        
        instance = new InstanceConfiguration();
        
        // basic instance data
        if (getSetupTypePanel().useDefault()) {
            // default setup
            instance.setId("default");
            instance.setDescription(Messages.getString("SetupWizard.DefaultInstanceDescription")); //$NON-NLS-1$
            instance.setInstanceType(InstanceType.MONOLITHIC);
        } else {
            // manual setup
            instance.setId(getInstanceNamePanel().getID());
            instance.setDescription(getInstanceNamePanel().getDescription());
            instance.setInstanceType(getInstanceTypePanel().getInstanceType());
            if (instance.getInstanceType().isMonolithic()) {
                // manual monolithic setup 
            }
            if (instance.getInstanceType().isServer()) {
                // manual server setup
                instance.setServerInterface(getServerConnectionPanel().getInterface());
                instance.setServerPort(getServerConnectionPanel().getPort());
            }
            if (instance.getInstanceType().isClient()) {
                // manual client setup
                instance.setClientHost(getClientConnectionPanel().getHost());
                instance.setClientPort(getClientConnectionPanel().getPort());
            }
        }

        if (!instance.getInstanceType().isClient()) {
            // assemble CM configuration
            cmConfig = new CorporateMemoryConfiguration(getCorporateMemoryPanel().getCMClass());
            cmConfig.setProperty("driver", getCorporateMemoryPanel().getDriver());
            instance.setCMConfig(cmConfig);
            
            // enter connection data
            if (getSetupTypePanel().useDefault()) {
                // use default values for all properties
                CorporateMemory cm = cmConfig.getInstance();
                for (Enumeration p = cm.getSupportedProperties().elements(); p.hasMoreElements();) {
                    String property = (String) p.nextElement();
                    String value = cm.getDefaultPropertyValue(property);
                    if (value != null)
                        cmConfig.setProperty(property, value);
                }
            } else {
                // use values entered by user
                cmConfig.setProperty("host", getRelationalConnectionPanel().getHost());
                if (!getRelationalConnectionPanel().getPort().equals(""))
                	cmConfig.setProperty("port", getRelationalConnectionPanel().getPort());
                cmConfig.setProperty("database", getRelationalConnectionPanel().getDatabase());
                cmConfig.setProperty("user", getRelationalConnectionPanel().getUser());
                cmConfig.setProperty("password", getRelationalConnectionPanel().getPassword());
                if (!getRelationalConnectionPanel().getConnectionString().equals(("")))
                	cmConfig.setProperty("connection", getRelationalConnectionPanel().getConnectionString());
            }
            
            // add DBA credentials
            cmConfig.setProperty("dba_user", getAdminCredentialsPanel().getUser());
            cmConfig.setProperty("dba_password", getAdminCredentialsPanel().getPassword());
        }
        
        return instance;
    }
    
    
    /* (non-Javadoc)
     * @see jwf.WizardListener#wizardFinished(jwf.Wizard)
     */
    public void wizardFinished(Wizard wiz) {
        
        this.dialog.setVisible(false);
        logger.debug("GUI part of setup wizard finished, setting up instance...");
        
        // show "please wait" window
        // FIXME The "please wait" window doesn't contain any text.
        // FIXME The "please wait" window should be modal.
        JDialog pleaseWait = new JDialog();
        pleaseWait.getContentPane().add(new JLabel(Messages.getString("SetupWizard.PleaseWaitMessage"))); //$NON-NLS-1$
        pleaseWait.setTitle(Messages.getString("SetupWizard.PleaseWaitTitle")); //$NON-NLS-1$
        pleaseWait.setSize(250, 50);
        pleaseWait.setLocation(200, 200);
        pleaseWait.pack();
        pleaseWait.setVisible(true);
        
        // execute list of actions prepared previously
        this.actions.execute();
        
        // hide "please wait" window
        pleaseWait.setVisible(false);
        
    }

    /* (non-Javadoc)
     * @see jwf.WizardListener#wizardCancelled(jwf.Wizard)
     */
    public void wizardCancelled(Wizard wiz) {
        this.dialog.setVisible(false);
        logger.debug("Setup wizard cancelled.");
    }

    /* (non-Javadoc)
     * @see jwf.WizardListener#wizardPanelChanged(jwf.Wizard)
     */
    public void wizardPanelChanged(Wizard wiz) {
        // nothing to do here
    }
    
    private WelcomePanel getWelcomePanel() {
        if (this.pWelcome == null) {
            this.pWelcome = new WelcomePanel(this);
        }
        return this.pWelcome;
    }
    
    private SetupTypePanel getSetupTypePanel(){
        if (this.pSetupType == null) {
            this.pSetupType = new SetupTypePanel(this);
        }
        return this.pSetupType;
    }
    
    private InstanceTypePanel getInstanceTypePanel() {
        if (this.pInstanceType == null) {
            this.pInstanceType = new InstanceTypePanel(this);
        }
        return this.pInstanceType;
    }
    
    private CorporateMemoryPanel getCorporateMemoryPanel() {
        if (this.pCorporateMemory == null) {
            this.pCorporateMemory = new CorporateMemoryPanel(this);
        }
        return this.pCorporateMemory;
    }
    
    private RelationalConnectionPanel getRelationalConnectionPanel() {
        if (this.pRelationalConnection == null) {
            this.pRelationalConnection = new RelationalConnectionPanel(this);
        }
        return this.pRelationalConnection;
    }
    
    private AdminCredentialsPanel getAdminCredentialsPanel() {
        if (this.pAdminCredentials == null) {
            this.pAdminCredentials = new AdminCredentialsPanel(this);
        }
        return this.pAdminCredentials;
    }
    
    private ServerConnectionPanel getServerConnectionPanel() {
        if (this.pServerConnection == null) {
            this.pServerConnection = new ServerConnectionPanel(this);
        }
        return this.pServerConnection;
    }
    
    private ClientConnectionPanel getClientConnectionPanel() {
        if (this.pClientConnection == null) {
            this.pClientConnection = new ClientConnectionPanel(this);
        }
        return this.pClientConnection;
    }
    
    private InstanceNamePanel getInstanceNamePanel() {
        if (this.pInstanceName == null) {
            this.pInstanceName = new InstanceNamePanel(this);
        }
        return this.pInstanceName;
    }
    
    private ConfirmationPanel getConfirmationPanel() {
        if (this.pConfirmation == null) {
            this.pConfirmation = new ConfirmationPanel(this);
        }
        return this.pConfirmation;
    }
    
    
    
}
