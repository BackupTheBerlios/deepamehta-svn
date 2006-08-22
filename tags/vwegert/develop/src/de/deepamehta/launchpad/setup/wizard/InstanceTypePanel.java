/*
 * Created on 01.01.2006
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
import de.deepamehta.environment.instance.InstanceType;
/**
 * This panel determines whether the user wants to create a monolithic, server or client instance.
 * @author vwegert
 */
public class InstanceTypePanel extends AbstractWizardPanel {

	private static final long serialVersionUID = 5337760799449290899L;
	private JTextArea taGlobalExplanation = null;
	private JRadioButton rbMonolithic = null;
	private JLabel labelMonolithic = null;
	private JTextArea taMonolithicExplanation = null;
	private JRadioButton rbServer = null;
	private JLabel labelServer = null;
	private JTextArea taServerExplanation = null;
	private JRadioButton rbClient = null;
	private JLabel labelClient = null;
	private JTextArea taClientExplanation = null;
	private JPanel filler = null;
	private JPanel panelTitle = null;
	private JLabel labelTitle = null;
	
	private ButtonGroup group = null;
	
    /**
     * default constructor
     * @param parent
     */
    public InstanceTypePanel(SetupWizard parent) {
        super(parent);
        initialize();
        initializeButtonGroup();
    }

	/**
	 * This method initializes the GUI components
	 */
	private  void initialize() {
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		this.labelClient = new JLabel();
		this.labelServer = new JLabel();
		this.labelMonolithic = new JLabel();
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(300,200);
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.weightx = 0.0D;
		gridBagConstraints1.weighty = 0.0D;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints3.gridx = 1;
		gridBagConstraints3.gridy = 2;
		gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints3.insets = new java.awt.Insets(2,2,2,2);
		this.labelMonolithic.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceTypePanel.MonolithicInstance")); //$NON-NLS-1$
		gridBagConstraints4.gridx = 1;
		gridBagConstraints4.gridy = 3;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.weighty = 0.0D;
		gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.gridy = 4;
		gridBagConstraints6.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints7.gridx = 1;
		gridBagConstraints7.gridy = 4;
		gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints7.insets = new java.awt.Insets(2,2,2,2);
		this.labelServer.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceTypePanel.ServerInstance")); //$NON-NLS-1$
		gridBagConstraints8.gridx = 1;
		gridBagConstraints8.gridy = 5;
		gridBagConstraints8.weightx = 1.0;
		gridBagConstraints8.weighty = 0.0D;
		gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints9.gridx = 0;
		gridBagConstraints9.gridy = 6;
		gridBagConstraints11.gridx = 1;
		gridBagConstraints11.gridy = 6;
		gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints11.insets = new java.awt.Insets(2,2,2,2);
		this.labelClient.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceTypePanel.ClientInstance")); //$NON-NLS-1$
		gridBagConstraints12.gridx = 1;
		gridBagConstraints12.gridy = 7;
		gridBagConstraints12.weightx = 1.0;
		gridBagConstraints12.weighty = 0.0D;
		gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints12.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints13.gridx = 1;
		gridBagConstraints13.gridy = 8;
		gridBagConstraints13.gridwidth = 2;
		gridBagConstraints13.weighty = 10.0D;
		gridBagConstraints5.gridx = 0;
		gridBagConstraints5.gridy = 0;
		gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.gridwidth = 2;
		this.add(getTaGlobalExplanation(), gridBagConstraints1);
		this.add(getRbMonolithic(), gridBagConstraints2);
		this.add(this.labelMonolithic, gridBagConstraints3);
		this.add(getTaMonolithicExplanation(), gridBagConstraints4);
		this.add(getRbServer(), gridBagConstraints6);
		this.add(this.labelServer, gridBagConstraints7);
		this.add(getTaServerExplanation(), gridBagConstraints8);
		this.add(getRbClient(), gridBagConstraints9);
		this.add(this.labelClient, gridBagConstraints11);
		this.add(getTaClientExplanation(), gridBagConstraints12);
		this.add(getFiller(), gridBagConstraints13);
		this.add(getPanelTitle(), gridBagConstraints5);
	}
	
    /**
     * This method initializes the button group. 
     */
    private void initializeButtonGroup() {
        this.group = new ButtonGroup();
        this.group.add(getRbMonolithic());
        this.group.add(getRbServer());
        this.group.add(getRbClient());
    }
    
	/**
	 * This method initializes the text area containing the global explanation.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaGlobalExplanation() {
		if (this.taGlobalExplanation == null) {
			this.taGlobalExplanation = new JTextArea();
			this.taGlobalExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceTypePanel.GlobalExplanation")); //$NON-NLS-1$
			this.taGlobalExplanation.setEditable(false);
			this.taGlobalExplanation.setBackground(null);
			this.taGlobalExplanation.setLineWrap(true);
			this.taGlobalExplanation.setWrapStyleWord(true);
		}
		return this.taGlobalExplanation;
	}
	
	/**
	 * This method initializes the radio button "monolithic"	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getRbMonolithic() {
		if (this.rbMonolithic == null) {
			this.rbMonolithic = new JRadioButton();
			this.rbMonolithic.setSelected(true);
		}
		return this.rbMonolithic;
	}
	
	/**
	 * This method initializes the text area explaining a monolithic instance	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaMonolithicExplanation() {
		if (this.taMonolithicExplanation == null) {
			this.taMonolithicExplanation = new JTextArea();
			this.taMonolithicExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceTypePanel.MonolithicExplanation")); //$NON-NLS-1$
			this.taMonolithicExplanation.setEditable(false);
			this.taMonolithicExplanation.setBackground(null);
			this.taMonolithicExplanation.setLineWrap(true);
			this.taMonolithicExplanation.setWrapStyleWord(true);
		}
		return this.taMonolithicExplanation;
	}
	
	/**
	 * This method initializes the radion button "server"	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getRbServer() {
		if (this.rbServer == null) {
			this.rbServer = new JRadioButton();
		}
		return this.rbServer;
	}
	
	/**
	 * This method initializes the text area explaining a server instance	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaServerExplanation() {
		if (this.taServerExplanation == null) {
			this.taServerExplanation = new JTextArea();
			this.taServerExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceTypePanel.ServerExplanation")); //$NON-NLS-1$
			this.taServerExplanation.setBackground(null);
			this.taServerExplanation.setEditable(false);
			this.taServerExplanation.setLineWrap(true);
			this.taServerExplanation.setWrapStyleWord(true);
		}
		return this.taServerExplanation;
	}
	
	/**
	 * This method initializes the radio button "client"	
	 * @return javax.swing.JRadioButton	
	 */    
	private JRadioButton getRbClient() {
		if (this.rbClient == null) {
			this.rbClient = new JRadioButton();
		}
		return this.rbClient;
	}
	
	/**
	 * This method initializes the text area explaining a client instance	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaClientExplanation() {
		if (this.taClientExplanation == null) {
			this.taClientExplanation = new JTextArea();
			this.taClientExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceTypePanel.ClientExplanation")); //$NON-NLS-1$
			this.taClientExplanation.setEditable(false);
			this.taClientExplanation.setBackground(null);
			this.taClientExplanation.setLineWrap(true);
			this.taClientExplanation.setWrapStyleWord(true);
		}
		return this.taClientExplanation;
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
	 * This method initializes the panel containing the title	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getPanelTitle() {
		if (this.panelTitle == null) {
			this.labelTitle = new JLabel();
			this.panelTitle = new JPanel();
			this.panelTitle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			this.labelTitle.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceTypePanel.Title")); //$NON-NLS-1$
			this.labelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
			this.panelTitle.add(this.labelTitle, null);
		}
		return this.panelTitle;
	}
	
    /**
     * @return Returns the instance type selected by the user.
     */
    public InstanceType getInstanceType() {
        if (this.rbMonolithic.isSelected())
            return InstanceType.MONOLITHIC;
        if (this.rbServer.isSelected())
            return InstanceType.SERVER;
        if (this.rbClient.isSelected())
            return InstanceType.CLIENT;
        return null; // should never be reached
    }
	
}
