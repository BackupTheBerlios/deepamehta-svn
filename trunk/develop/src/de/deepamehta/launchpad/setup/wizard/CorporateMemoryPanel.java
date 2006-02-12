/*
 * Created on 02.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.service.CorporateMemory;
/**
 * This panel gathers information about the CM implementation to use.
 * @author vwegert
 */
public class CorporateMemoryPanel extends AbstractWizardPanel {

 	private static final long serialVersionUID = -3348159159207400170L;
	private static Log logger = LogFactory.getLog(CorporateMemoryPanel.class);
    
	private JTextArea taGlobalExplanation = null;
	private JLabel labelCMClass = null;
	private JTextArea taCMClassExplanation = null;
	private JPanel filler = null;
	private JComboBox comboCMClass = null;
	private JPanel panelTitle = null;
	private JLabel labelTitle = null;
	private JLabel labelDriverPlugin = null;
	private JComboBox comboDriverPlugin = null;
	private JTextArea taDriverPluginExplanation = null;
	
    /**
     * default constructor
     * @param parent
     */
    public CorporateMemoryPanel(SetupWizard parent) {
        super(parent);
		initialize();
		initializeCMComboBox();
    }
    
	/**
	 * This method initializes this
	 */
	private  void initialize() {
		this.labelDriverPlugin = new JLabel();
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
		this.labelCMClass = new JLabel();
		GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(551, 362);
		gridBagConstraints14.gridx = 0;
		gridBagConstraints14.gridy = 1;
		gridBagConstraints14.weightx = 1.0;
		gridBagConstraints14.weighty = 0.0D;
		gridBagConstraints14.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints14.gridwidth = 2;
		gridBagConstraints14.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints18.gridx = 0;
		gridBagConstraints18.gridy = 4;
		gridBagConstraints18.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints18.insets = new java.awt.Insets(2,2,2,2);
		this.labelCMClass.setText(DeepaMehtaMessages.getString("SetupWizard.CorporateMemoryPanel.Implementation")); //$NON-NLS-1$
		gridBagConstraints20.gridx = 1;
		gridBagConstraints20.gridy = 5;
		gridBagConstraints20.weightx = 1.0;
		gridBagConstraints20.weighty = 0.0D;
		gridBagConstraints20.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints20.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints21.gridx = 0;
		gridBagConstraints21.gridy = 8;
		gridBagConstraints21.gridwidth = 2;
		gridBagConstraints21.weighty = 1.0D;
		gridBagConstraints22.gridx = 1;
		gridBagConstraints22.gridy = 4;
		gridBagConstraints22.weightx = 1.0;
		gridBagConstraints22.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints22.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints9.gridx = 0;
		gridBagConstraints9.gridy = 0;
		gridBagConstraints9.gridwidth = 2;
		gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints10.gridx = 0;
		gridBagConstraints10.gridy = 6;
		gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints10.insets = new java.awt.Insets(2,2,2,2);
		this.labelDriverPlugin.setText(DeepaMehtaMessages.getString("SetupWizard.CorporateMemoryPanel.Driver")); //$NON-NLS-1$
		gridBagConstraints11.gridx = 1;
		gridBagConstraints11.gridy = 6;
		gridBagConstraints11.weightx = 1.0;
		gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints11.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints12.gridx = 1;
		gridBagConstraints12.gridy = 7;
		gridBagConstraints12.weightx = 1.0;
		gridBagConstraints12.weighty = 0.0D;
		gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints12.insets = new java.awt.Insets(0,2,2,2);
		this.add(getTaGlobalExplanation(), gridBagConstraints14);
		this.add(this.labelCMClass, gridBagConstraints18);
		this.add(getTaCMClassExplanation(), gridBagConstraints20);
		this.add(getFiller(), gridBagConstraints21);
		this.add(getComboCMClass(), gridBagConstraints22);
		this.add(getPanelTitle(), gridBagConstraints9);
		this.add(this.labelDriverPlugin, gridBagConstraints10);
		this.add(getComboDriverPlugin(), gridBagConstraints11);
		this.add(getTaDriverPluginExplanation(), gridBagConstraints12);
	}
	
    /**
     * This method initializes the combo box containing the CM implementations. 
     */
    private void initializeCMComboBox() {
        // TODO remove hard-coded CM implementation name
        getComboCMClass().removeAllItems();
        getComboCMClass().addItem("de.deepamehta.service.RelationalCorporateMemory");
    }

    /**
	 * This method initializes the text area containing a global explanation.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaGlobalExplanation() {
		if (this.taGlobalExplanation == null) {
			this.taGlobalExplanation = new JTextArea();
			this.taGlobalExplanation.setBackground(null);
			this.taGlobalExplanation.setEditable(false);
			this.taGlobalExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.CorporateMemoryPanel.GlobalExplananation")); //$NON-NLS-1$
			this.taGlobalExplanation.setLineWrap(true);
			this.taGlobalExplanation.setWrapStyleWord(true);
		}
		return this.taGlobalExplanation;
	}
	
	/**
	 * This method initializes the text area explaining the meaning of the CM implementation.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaCMClassExplanation() {
		if (this.taCMClassExplanation == null) {
			this.taCMClassExplanation = new JTextArea();
			this.taCMClassExplanation.setBackground(null);
			this.taCMClassExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.CorporateMemoryPanel.ImplementationExplanation")); //$NON-NLS-1$
			this.taCMClassExplanation.setLineWrap(true);
			this.taCMClassExplanation.setEditable(false);
			this.taCMClassExplanation.setWrapStyleWord(true);
		}
		return this.taCMClassExplanation;
	}
	
	/**
	 * This method initializes the filler panel required for correct alignment.	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getFiller() {
		if (this.filler == null) {
			this.filler = new JPanel();
		}
		return this.filler;
	}
	
	/**
	 * This method initializes the combobox containing the CM implementations available.	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getComboCMClass() {
		if (this.comboCMClass == null) {
			this.comboCMClass = new JComboBox();
			this.comboCMClass.addItemListener(new java.awt.event.ItemListener() { 
				public void itemStateChanged(java.awt.event.ItemEvent e) {    
				    updateDriverCombobox();
				}
			});
		}
		return this.comboCMClass;
	}
	
	/**
     * This method updates the contents of the driver combobox according to the capabilites 
     * of the CM implementation.
     */
    protected void updateDriverCombobox() {
        
        Vector drivers = null;
        
        String className = (String) getComboCMClass().getSelectedItem();
        logger.debug("CM implementation '" + className + "'selected.");
        logger.debug("Adjusting low-level driver selection...");
        
        try {
            CorporateMemory im = (CorporateMemory) Class.forName(className).newInstance();
            drivers = im.getSupportedPropertyValues("driver");
        } catch (ClassNotFoundException e) {
            logger.error("Unable to load CM implementation class " + className, e);
        } catch (SecurityException e) {
            logger.error("Unable to load CM implementation constructor due to security restrictions.", e);
        } catch (IllegalArgumentException e) {
            logger.error("Unable to instantiate CM implementation class " + className + ".", e);
        } catch (InstantiationException e) {
            logger.error("Unable to instantiate CM implementation class " + className + ".", e);
        } catch (IllegalAccessException e) {
            logger.error("Unable to instantiate CM implementation class " + className + ".", e);
        }
        
        getComboDriverPlugin().removeAllItems();
        if (drivers != null) {
            getComboDriverPlugin().setEnabled(true);
            for (Enumeration e = drivers.elements() ; e.hasMoreElements() ;) {
                getComboDriverPlugin().addItem(e.nextElement());
            }
        } else {
            getComboDriverPlugin().setEnabled(false);
        }
    }
    
    /**
	 * This method initializes the panel containing the title	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getPanelTitle() {
		if (this.panelTitle == null) {
			this.labelTitle = new JLabel();
			this.panelTitle = new JPanel();
			this.panelTitle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			this.labelTitle.setText(DeepaMehtaMessages.getString("SetupWizard.CorporateMemoryPanel.Title")); //$NON-NLS-1$
			this.labelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
			this.panelTitle.add(this.labelTitle, null);
		}
		return this.panelTitle;
	}
	
	/**
	 * This method initializes the combo box containing the driver plugins.	
	 * @return javax.swing.JComboBox	
	 */    
	private JComboBox getComboDriverPlugin() {
		if (this.comboDriverPlugin == null) {
			this.comboDriverPlugin = new JComboBox();
		}
		return this.comboDriverPlugin;
	}
	
	/**
	 * This method initializes the text area containing the explanation about the driver selection.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaDriverPluginExplanation() {
		if (this.taDriverPluginExplanation == null) {
			this.taDriverPluginExplanation = new JTextArea();
			this.taDriverPluginExplanation.setBackground(null);
			this.taDriverPluginExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.CorporateMemoryPanel.DriverExplanation")); //$NON-NLS-1$
			this.taDriverPluginExplanation.setEditable(false);
			this.taDriverPluginExplanation.setLineWrap(true);
			this.taDriverPluginExplanation.setWrapStyleWord(true);
		}
		return this.taDriverPluginExplanation;
	}
	
    /**
     * @return Returns the CM implementation selected.
     */
    public String getCMClass() {
        return (String) getComboCMClass().getSelectedItem();
    }

    /**
     * @return Returns the driver plugin selected.
     */
    public String getDriver() {
        return (String) getComboDriverPlugin().getSelectedItem();
    }
    
}  //  @jve:decl-index=0:visual-constraint="10,10"
