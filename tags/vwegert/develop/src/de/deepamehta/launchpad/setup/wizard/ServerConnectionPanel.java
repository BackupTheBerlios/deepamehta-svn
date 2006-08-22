/*
 * Created on 02.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import de.deepamehta.DeepaMehtaMessages;
/**
 * This panel obtains the interface and port the server will use.
 * @author vwegert
 */
public class ServerConnectionPanel extends AbstractWizardPanel {

	private static final long serialVersionUID = -8078786378554510684L;
	private JPanel panelTitle = null;
	private JLabel labelTitle = null;
	private JTextArea taGlobalExplanation = null;
	private JLabel labelInterface = null;
	private JTextField editInterface = null;
	private JTextArea taServer = null;
	private JLabel labelPort = null;
	private JTextField editPort = null;
	private JTextArea taPort = null;
	private JPanel panelFiller = null;
	
    /**
     * default constructor
     * @param parent
     */
    public ServerConnectionPanel(SetupWizard parent) {
        super(parent);
		initialize();
    }
    
	/**
	 * This method initializes the GUI components
	 */
	private void initialize() {
		this.labelPort = new JLabel();
		this.labelInterface = new JLabel();
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(401, 211);
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.weighty = 0.0D;
		gridBagConstraints1.weightx = 10.0D;
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints3.gridx = 0;
		gridBagConstraints3.gridy = 1;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.weighty = 0.0D;
		gridBagConstraints3.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints3.gridwidth = 2;
		gridBagConstraints3.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.gridy = 2;
		gridBagConstraints4.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
		this.labelInterface.setText(DeepaMehtaMessages.getString("SetupWizard.ServerConnectionPanel.Interface")); //$NON-NLS-1$
		gridBagConstraints5.gridx = 1;
		gridBagConstraints5.gridy = 2;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints6.gridx = 1;
		gridBagConstraints6.gridy = 3;
		gridBagConstraints6.weightx = 1.0;
		gridBagConstraints6.weighty = 0.0D;
		gridBagConstraints6.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints6.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints7.gridx = 0;
		gridBagConstraints7.gridy = 4;
		gridBagConstraints7.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
		this.labelPort.setText(DeepaMehtaMessages.getString("SetupWizard.ServerConnectionPanel.Port")); //$NON-NLS-1$
		gridBagConstraints8.gridx = 1;
		gridBagConstraints8.gridy = 4;
		gridBagConstraints8.weightx = 1.0;
		gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints9.gridx = 1;
		gridBagConstraints9.gridy = 5;
		gridBagConstraints9.weightx = 1.0;
		gridBagConstraints9.weighty = 0.0D;
		gridBagConstraints9.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints9.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints10.gridx = 1;
		gridBagConstraints10.gridy = 6;
		gridBagConstraints10.weighty = 1.0D;
		gridBagConstraints10.weightx = 1.0D;
		this.add(getPanelTitle(), gridBagConstraints1);
		this.add(getTaGlobalExplanation(), gridBagConstraints3);
		this.add(this.labelInterface, gridBagConstraints4);
		this.add(getEditInterface(), gridBagConstraints5);
		this.add(this.labelPort, gridBagConstraints7);
		this.add(getTaServer(), gridBagConstraints6);
		this.add(getEditPort(), gridBagConstraints8);
		this.add(getTaPort(), gridBagConstraints9);
		this.add(getPanelFiller(), gridBagConstraints10);
	}

	/**
	 * This method initializes the panel containing the title.	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getPanelTitle() {
		if (this.panelTitle == null) {
			this.labelTitle = new JLabel();
			this.panelTitle = new JPanel();
			this.panelTitle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			this.labelTitle.setText(DeepaMehtaMessages.getString("SetupWizard.ServerConnectionPanel.Title")); //$NON-NLS-1$
			this.labelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
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
			this.taGlobalExplanation.setEditable(false);
			this.taGlobalExplanation.setLineWrap(true);
			this.taGlobalExplanation.setBackground(null);
			this.taGlobalExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.ServerConnectionPanel.GlobalExplanation")); //$NON-NLS-1$
			this.taGlobalExplanation.setWrapStyleWord(true);
		}
		return this.taGlobalExplanation;
	}
	
	/**
	 * This method initializes the text field for the interface	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEditInterface() {
		if (this.editInterface == null) {
			this.editInterface = new JTextField();
		}
		return this.editInterface;
	}
	
	/**
	 * This method initializes the text area explaining the interface	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaServer() {
		if (this.taServer == null) {
			this.taServer = new JTextArea();
			this.taServer.setEditable(false);
			this.taServer.setLineWrap(true);
			this.taServer.setBackground(null);
			this.taServer.setText(DeepaMehtaMessages.getString("SetupWizard.ServerConnectionPanel.InterfaceExplanation")); //$NON-NLS-1$
			this.taServer.setWrapStyleWord(true);
		}
		return this.taServer;
	}
	
	/**
	 * This method initializes the text field for the port 	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEditPort() {
		if (this.editPort == null) {
			this.editPort = new JTextField();
		}
		return this.editPort;
	}
	
	/**
	 * This method initializes the text area explaining the port	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaPort() {
		if (this.taPort == null) {
			this.taPort = new JTextArea();
			this.taPort.setEditable(false);
			this.taPort.setLineWrap(true);
			this.taPort.setBackground(null);
			this.taPort.setText(DeepaMehtaMessages.getString("SetupWizard.ServerConnectionPanel.PortExplanation")); //$NON-NLS-1$
			this.taPort.setWrapStyleWord(true);
		}
		return this.taPort;
	}
	
	/**
	 * This method initializes the filler panel required for correct alignment.	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getPanelFiller() {
		if (this.panelFiller == null) {
			this.panelFiller = new JPanel();
		}
		return this.panelFiller;
	}

    /**
     * @return Returns the interface specified.
     */
    public String getInterface() {
        return getEditInterface().getText();
    }
    
    /**
     * @return Returns the port specified.
     */
    public int getPort() {
        return Integer.parseInt(getEditPort().getText());
    }

	/* (non-Javadoc)
	 * @see de.deepamehta.launchpad.setup.wizard.AbstractWizardPanel#validateNext(java.util.List)
	 */
	public boolean validateNext(List list) {
		
		boolean valid = true;

//		if (getEditInterface().getText().equals("")) {
//			list.add("The interface is required.");
//			valid = false;
//		}
		
		if (getEditPort().getText().equals("")) {
			list.add(DeepaMehtaMessages.getString("SetupWizard.ServerConnectionPanel.ErrorMissingPort")); //$NON-NLS-1$
			valid = false;
		} else {
			try {
				Integer.parseInt(getEditPort().getText());
			} catch (NumberFormatException e) {
				list.add(DeepaMehtaMessages.getString("SetupWizard.ServerConnectionPanel.ErrorNonNumericPort")); //$NON-NLS-1$
				valid = false;
			}
		}
		
		return valid;
	}
}  //  @jve:decl-index=0:visual-constraint="15,9"
