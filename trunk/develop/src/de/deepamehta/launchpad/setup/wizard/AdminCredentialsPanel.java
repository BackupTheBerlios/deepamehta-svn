/*
 * Created on 02.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup.wizard;

import de.deepamehta.DeepaMehtaMessages;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
/**
 * This panel obtains the database admin credentials required during setup.
 * @author vwegert
 */
public class AdminCredentialsPanel extends AbstractWizardPanel {

	private static final long serialVersionUID = 5704621651429364478L;
	private JTextArea taGlobalExplanation = null;
	private JPanel panelFiller = null;
	private JLabel labelUser = null;
	private JLabel labelPassword = null;
	private JTextField editUser = null;
	private JPasswordField editPassword = null;
	private JTextArea taAdminCredentials = null;
	private JPanel panelTitle = null;
	private JLabel labelTitle = null;
	
    /**
     * default constructor
     * @param parent
     */
    public AdminCredentialsPanel(SetupWizard parent) {
        super(parent);
		initialize();
    }
    
	/**
	 * This method initializes the GUI components
	 */
	private  void initialize() {
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		this.labelPassword = new JLabel();
		this.labelUser = new JLabel();
		GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(522, 389);
		gridBagConstraints17.gridx = 1;
		gridBagConstraints17.gridy = 3;
		gridBagConstraints17.weightx = 1.0;
		gridBagConstraints17.weighty = 0.0D;
		gridBagConstraints17.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints21.gridx = 0;
		gridBagConstraints21.gridy = 17;
		gridBagConstraints21.gridwidth = 3;
		gridBagConstraints21.weighty = 1.0D;
		gridBagConstraints17.gridwidth = 2;
		gridBagConstraints17.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints32.gridx = 1;
		gridBagConstraints32.gridy = 11;
		gridBagConstraints32.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints32.insets = new java.awt.Insets(2,2,2,2);
		this.labelUser.setText(DeepaMehtaMessages.getString("SetupWizard.AdminCredentialsPanel.User")); //$NON-NLS-1$
		gridBagConstraints33.gridx = 1;
		gridBagConstraints33.gridy = 12;
		gridBagConstraints33.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints33.insets = new java.awt.Insets(2,2,2,2);
		this.labelPassword.setText(DeepaMehtaMessages.getString("SetupWizard.AdminCredentialsPanel.Password")); //$NON-NLS-1$
		gridBagConstraints34.gridx = 2;
		gridBagConstraints34.gridy = 11;
		gridBagConstraints34.weightx = 1.0;
		gridBagConstraints34.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints34.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints35.gridx = 2;
		gridBagConstraints35.gridy = 12;
		gridBagConstraints35.weightx = 1.0;
		gridBagConstraints35.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints35.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints36.gridx = 2;
		gridBagConstraints36.gridy = 13;
		gridBagConstraints36.weightx = 1.0;
		gridBagConstraints36.weighty = 0.0D;
		gridBagConstraints36.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints36.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints8.gridx = 0;
		gridBagConstraints8.gridy = 0;
		gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.gridwidth = 3;
		this.add(getTaGlobalExplanation(), gridBagConstraints17);
		this.add(getPanelFiller(), gridBagConstraints21);
		this.add(this.labelUser, gridBagConstraints32);
		this.add(this.labelPassword, gridBagConstraints33);
		this.add(getEditUser(), gridBagConstraints34);
		this.add(getEditPassword(), gridBagConstraints35);
		this.add(getTaAdminCredentials(), gridBagConstraints36);
		this.add(getPanelTitle(), gridBagConstraints8);
	}
	
	/**
	 * This method initializes the text area containing the global explanation.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaGlobalExplanation() {
		if (this.taGlobalExplanation == null) {
			this.taGlobalExplanation = new JTextArea();
			this.taGlobalExplanation.setBackground(null);
			this.taGlobalExplanation.setEditable(false);
			this.taGlobalExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.AdminCredentialsPanel.GlobalExplanation")); //$NON-NLS-1$
			this.taGlobalExplanation.setLineWrap(true);
			this.taGlobalExplanation.setWrapStyleWord(true);
		}
		return this.taGlobalExplanation;
	}
	
	/**
	 * This method initializes the filler panel required for correct alignment	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getPanelFiller() {
		if (this.panelFiller == null) {
			this.panelFiller = new JPanel();
		}
		return this.panelFiller;
	}

	/**
	 * This method initializes the text field for the user name	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEditUser() {
		if (this.editUser == null) {
			this.editUser = new JTextField();
			this.editUser.setText("root");
		}
		return this.editUser;
	}
	
	/**
	 * This method initializes the text field for the password	
	 * @return javax.swing.JPasswordField	
	 */    
	private JPasswordField getEditPassword() {
		if (this.editPassword == null) {
			this.editPassword = new JPasswordField();
			this.editPassword.setText("");
		}
		return this.editPassword;
	}
	
	/**
	 * This method initializes the text area explaining the database credentials	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaAdminCredentials() {
		if (this.taAdminCredentials == null) {
			this.taAdminCredentials = new JTextArea();
			this.taAdminCredentials.setBackground(null);
			this.taAdminCredentials.setEditable(false);
			this.taAdminCredentials.setLineWrap(true);
			this.taAdminCredentials.setWrapStyleWord(true);
			this.taAdminCredentials.setText(DeepaMehtaMessages.getString("SetupWizard.AdminCredentialsPanel.CredentialsExplanation")); //$NON-NLS-1$
		}
		return this.taAdminCredentials;
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
			this.labelTitle.setText(DeepaMehtaMessages.getString("SetupWizard.AdminCredentialsPanel.Title")); //$NON-NLS-1$
			this.labelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
			this.panelTitle.add(this.labelTitle, null);
		}
		return this.panelTitle;
	}

    /**
     * @return Returns the user name.
     */
    public String getUser() {
        return getEditUser().getText();
    }

    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return getEditPassword().getText();
    }
                
}  //  @jve:decl-index=0:visual-constraint="19,2"
