/*
 * Created on 13.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

import de.deepamehta.DeepaMehtaMessages;
/**
 * This panel determines whether the user wants a default setup or manual configuration
 * @author vwegert
 */
public class SetupTypePanel extends AbstractWizardPanel {

	private static final long serialVersionUID = -2535894700026964728L;

	private ButtonGroup group = null;
    
	private JPanel panelTitle = null;
	private JLabel labelTitle = null;
	private JTextArea taGlobalExplanation = null;
	private JRadioButton rbDefaultInstallation = null;
	private JTextArea taDefaultExplanation = null;
	private JLabel labelDefaultInstallation = null;
	private JPanel filler = null;
	private JRadioButton rbManualConfiguration = null;
	private JLabel labelManualConfiguration = null;
	private JTextArea taManualExplanation = null;
	
    /**
     * default constructor
     * @param parent
     */
    public SetupTypePanel(SetupWizard parent) {
        super(parent);
		initialize();
		initializeButtonGroup();
    }
    
	/**
	 * This method initializes the GUI components.
	 */
	private  void initialize() {
		this.labelManualConfiguration = new JLabel();
		this.labelDefaultInstallation = new JLabel();
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(300,200);
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.weighty = 0.0D;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.gridwidth = 2;
		gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 2;
		gridBagConstraints3.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints4.gridx = 1;
		gridBagConstraints4.gridy = 3;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.weighty = 0.0D;
		gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints5.gridx = 1;
		gridBagConstraints5.gridy = 2;
		gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints5.insets = new java.awt.Insets(2,2,2,2);
		this.labelDefaultInstallation.setText(DeepaMehtaMessages.getString("SetupWizard.SetupTypePanel.DefaultInstallation")); //$NON-NLS-1$
		gridBagConstraints6.gridx = 1;
		gridBagConstraints6.gridy = 6;
		gridBagConstraints6.fill = java.awt.GridBagConstraints.VERTICAL;
		gridBagConstraints6.weighty = 1.0D;
		gridBagConstraints7.gridx = 0;
		gridBagConstraints7.gridy = 4;
		gridBagConstraints7.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints8.gridx = 1;
		gridBagConstraints8.gridy = 4;
		gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints8.insets = new java.awt.Insets(2,2,2,2);
		this.labelManualConfiguration.setText(DeepaMehtaMessages.getString("SetupWizard.SetupTypePanel.ManualConfiguration")); //$NON-NLS-1$
		gridBagConstraints9.gridx = 1;
		gridBagConstraints9.gridy = 5;
		gridBagConstraints9.weightx = 1.0;
		gridBagConstraints9.weighty = 0.0D;
		gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints9.insets = new java.awt.Insets(0,2,2,2);
		this.add(getPanelTitle(), gridBagConstraints1);
		this.add(getTaGlobalExplanation(), gridBagConstraints2);
		this.add(getRbDefaultInstallation(), gridBagConstraints3);
		this.add(getTaDefaultExplanation(), gridBagConstraints4);
		this.add(this.labelDefaultInstallation, gridBagConstraints5);
		this.add(getFiller(), gridBagConstraints6);
		this.add(getRbManualConfiguration(), gridBagConstraints7);
		this.add(this.labelManualConfiguration, gridBagConstraints8);
		this.add(getTaManualExplanation(), gridBagConstraints9);
	}
	
	/**
	 * This method initializes the button group containing the radio buttons.
	 */
	private void initializeButtonGroup() {
	    this.group = new ButtonGroup();
	    this.group.add(this.rbDefaultInstallation);
	    this.group.add(this.rbManualConfiguration);
	}
	
	/**
	 * This method initializes the planel containing the title.	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getPanelTitle() {
		if (this.panelTitle == null) {
			this.labelTitle = new JLabel();
			this.panelTitle = new JPanel();
			this.labelTitle.setText(DeepaMehtaMessages.getString("SetupWizard.SetupTypePanel.Title")); //$NON-NLS-1$
			this.labelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
			this.panelTitle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			this.panelTitle.add(this.labelTitle, null);
		}
		return this.panelTitle;
	}
	/**
	 * This method initializes the text area containing the global explanation.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaGlobalExplanation() {
		if (this.taGlobalExplanation == null) {
			this.taGlobalExplanation = new JTextArea();
			this.taGlobalExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.SetupTypePanel.GlobalExplanation")); //$NON-NLS-1$
			this.taGlobalExplanation.setBackground(null);
			this.taGlobalExplanation.setEditable(false);
			this.taGlobalExplanation.setLineWrap(true);
			this.taGlobalExplanation.setWrapStyleWord(true);
		}
		return this.taGlobalExplanation;
	}
	
	/**
	 * This method initializes the radio button "default installation".	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getRbDefaultInstallation() {
		if (this.rbDefaultInstallation == null) {
			this.rbDefaultInstallation = new JRadioButton();
			this.rbDefaultInstallation.setSelected(true);
		}
		return this.rbDefaultInstallation;
	}
	
	/**
	 * This method initializes the text area explaining the default installation.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaDefaultExplanation() {
		if (this.taDefaultExplanation == null) {
			this.taDefaultExplanation = new JTextArea();
			this.taDefaultExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.SetupTypePanel.DefaultExplanation")); //$NON-NLS-1$
			this.taDefaultExplanation.setBackground(null);
			this.taDefaultExplanation.setEditable(false);
			this.taDefaultExplanation.setLineWrap(true);
			this.taDefaultExplanation.setWrapStyleWord(true);
		}
		return this.taDefaultExplanation;
	}
	
	/**
	 * This method initializes the filler required for correct alignment.	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getFiller() {
		if (this.filler == null) {
			this.filler = new JPanel();
		}
		return this.filler;
	}
	
	/**
	 * This method initializes the radio button "manual configuration".	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getRbManualConfiguration() {
		if (this.rbManualConfiguration == null) {
			this.rbManualConfiguration = new JRadioButton();
		}
		return this.rbManualConfiguration;
	}
	
	/**
	 * This method initializes the text area explaining the manual configuration.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaManualExplanation() {
		if (this.taManualExplanation == null) {
			this.taManualExplanation = new JTextArea();
			this.taManualExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.SetupTypePanel.ManualExplanation")); //$NON-NLS-1$
			this.taManualExplanation.setBackground(null);
			this.taManualExplanation.setLineWrap(true);
			this.taManualExplanation.setEditable(false);
			this.taManualExplanation.setWrapStyleWord(true);
		}
		return this.taManualExplanation;
	}

	/**
	 * @return <code>true</code> if the default configuration is to be used.
	 */
	public boolean useDefault() {
	    return getRbDefaultInstallation().isSelected();
	}
	
	/**
	 * @return <code>true</code> if manual configuration is requested.
	 */
	public boolean useManual() {
	    return getRbManualConfiguration().isSelected();
	}
	
}
