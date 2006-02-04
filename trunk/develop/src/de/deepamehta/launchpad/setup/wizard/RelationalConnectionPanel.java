/*
 * Created on 02.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * This panel obtains the information required to setup a relational CM.
 * @author vwegert
 */
public class RelationalConnectionPanel extends AbstractWizardPanel {

	private static final long serialVersionUID = 1392795821223706754L;
	private static Log logger = LogFactory.getLog(RelationalConnectionPanel.class);
    
	private JTextArea taGlobalExplanation = null;
	private JLabel labelServer = null;
	private JTextField editServer = null;
	private JTextArea taServerPort = null;
	private JPanel filler = null;
	private JLabel labelPort = null;
	private JTextField editPort = null;
	private JLabel labelDatabase = null;
	private JTextField editDatabase = null;
	private JTextArea taDatabase = null;
	private JLabel labelUser = null;
	private JLabel labelPassword = null;
	private JTextField editUser = null;
	private JPasswordField editPassword = null;
	private JTextArea taUserPassword = null;
	private JLabel labelConnectionString = null;
	private JTextField editConnectionString = null;
	private JTextArea taConnectionString = null;
	private JPanel panelTitle = null;
	private JLabel labelTitle = null;
	
    private boolean defaultValuesSet = false;
    
    /**
     * default constructor
     * @param parent
     */
    public RelationalConnectionPanel(SetupWizard parent) {
        super(parent);
		initialize();
    }   
  
	/**
	 * This method initializes the GUI Components
	 * 
	 */
	private  void initialize() {
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		this.labelConnectionString = new JLabel();
		this.labelPassword = new JLabel();
		this.labelUser = new JLabel();
		this.labelDatabase = new JLabel();
		this.labelPort = new JLabel();
		GridBagConstraints gridBagConstraints27 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints28 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints29 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints30 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints32 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints33 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints34 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints35 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints36 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints39 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints40 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
		this.labelServer = new JLabel();
		GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(528, 389);
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
		this.labelServer.setText(Messages.getString("RelationalConnectionPanel.Server")); //$NON-NLS-1$
		gridBagConstraints19.gridx = 1;
		gridBagConstraints19.gridy = 4;
		gridBagConstraints19.weightx = 1.0;
		gridBagConstraints19.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints19.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints20.gridx = 1;
		gridBagConstraints20.gridy = 8;
		gridBagConstraints20.weightx = 1.0;
		gridBagConstraints20.weighty = 0.0D;
		gridBagConstraints20.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints20.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints21.gridx = 0;
		gridBagConstraints21.gridy = 17;
		gridBagConstraints21.gridwidth = 3;
		gridBagConstraints21.weighty = 1.0D;
		gridBagConstraints27.gridx = 0;
		gridBagConstraints27.gridy = 7;
		gridBagConstraints27.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints27.insets = new java.awt.Insets(2,2,2,2);
		this.labelPort.setText(Messages.getString("RelationalConnectionPanel.Port")); //$NON-NLS-1$
		gridBagConstraints28.gridx = 1;
		gridBagConstraints28.gridy = 7;
		gridBagConstraints28.weightx = 1.0;
		gridBagConstraints28.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints28.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints29.gridx = 0;
		gridBagConstraints29.gridy = 9;
		gridBagConstraints29.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints29.insets = new java.awt.Insets(2,2,2,2);
		this.labelDatabase.setText(Messages.getString("RelationalConnectionPanel.Database")); //$NON-NLS-1$
		gridBagConstraints30.gridx = 1;
		gridBagConstraints30.gridy = 9;
		gridBagConstraints30.weightx = 1.0;
		gridBagConstraints30.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints30.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints31.gridx = 1;
		gridBagConstraints31.gridy = 10;
		gridBagConstraints31.weightx = 1.0;
		gridBagConstraints31.weighty = 0.0D;
		gridBagConstraints31.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints31.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints32.gridx = 0;
		gridBagConstraints32.gridy = 11;
		gridBagConstraints32.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints32.insets = new java.awt.Insets(2,2,2,2);
		this.labelUser.setText(Messages.getString("RelationalConnectionPanel.User")); //$NON-NLS-1$
		gridBagConstraints33.gridx = 0;
		gridBagConstraints33.gridy = 12;
		gridBagConstraints33.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints33.insets = new java.awt.Insets(2,2,2,2);
		this.labelPassword.setText(Messages.getString("RelationalConnectionPanel.Password")); //$NON-NLS-1$
		gridBagConstraints34.gridx = 1;
		gridBagConstraints34.gridy = 11;
		gridBagConstraints34.weightx = 1.0;
		gridBagConstraints34.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints34.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints35.gridx = 1;
		gridBagConstraints35.gridy = 12;
		gridBagConstraints35.weightx = 1.0;
		gridBagConstraints35.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints35.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints36.gridx = 1;
		gridBagConstraints36.gridy = 13;
		gridBagConstraints36.weightx = 1.0;
		gridBagConstraints36.weighty = 0.0D;
		gridBagConstraints36.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints36.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints39.gridx = 0;
		gridBagConstraints39.gridy = 15;
		gridBagConstraints39.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints39.insets = new java.awt.Insets(2,2,2,2);
		this.labelConnectionString.setText(Messages.getString("RelationalConnectionPanel.Connection")); //$NON-NLS-1$
		gridBagConstraints40.gridx = 1;
		gridBagConstraints40.gridy = 15;
		gridBagConstraints40.weightx = 1.0;
		gridBagConstraints40.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints40.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints41.gridx = 1;
		gridBagConstraints41.gridy = 16;
		gridBagConstraints41.weightx = 1.0;
		gridBagConstraints41.weighty = 0.0D;
		gridBagConstraints41.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints41.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints8.gridx = 0;
		gridBagConstraints8.gridy = 0;
		gridBagConstraints8.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.gridwidth = 2;
		this.add(getTaGlobalExplanation(), gridBagConstraints14);
		this.add(this.labelServer, gridBagConstraints18);
		this.add(getEditServer(), gridBagConstraints19);
		this.add(getTaServerPort(), gridBagConstraints20);
		this.add(getFiller(), gridBagConstraints21);
		this.add(this.labelPort, gridBagConstraints27);
		this.add(this.labelDatabase, gridBagConstraints29);
		this.add(getTaDatabase(), gridBagConstraints31);
		this.add(this.labelUser, gridBagConstraints32);
		this.add(this.labelPassword, gridBagConstraints33);
		this.add(getEditUser(), gridBagConstraints34);
		this.add(getEditPassword(), gridBagConstraints35);
		this.add(getTaUserPassword(), gridBagConstraints36);
		this.add(this.labelConnectionString, gridBagConstraints39);
		this.add(getEditConnectionString(), gridBagConstraints40);
		this.add(getTaConnectionString(), gridBagConstraints41);
		this.add(getEditPort(), gridBagConstraints28);
		this.add(getEditDatabase(), gridBagConstraints30);
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
			this.taGlobalExplanation.setText(Messages.getString("RelationalConnectionPanel.GlobalExplanation")); //$NON-NLS-1$
			this.taGlobalExplanation.setLineWrap(true);
			this.taGlobalExplanation.setWrapStyleWord(true);
		}
		return this.taGlobalExplanation;
	}
	
	/**
	 * This method initializes the text field for the server name.	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEditServer() {
		if (this.editServer == null) {
			this.editServer = new JTextField();
			this.editServer.setText("");
		}
		return this.editServer;
	}
	
	/**
	 * This method initializes the text area explaining the DB server and port.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaServerPort() {
		if (this.taServerPort == null) {
			this.taServerPort = new JTextArea();
			this.taServerPort.setBackground(null);
			this.taServerPort.setText(Messages.getString("RelationalConnectionPanel.ServerPortExplanation")); //$NON-NLS-1$
			this.taServerPort.setLineWrap(true);
			this.taServerPort.setWrapStyleWord(true);
		}
		return this.taServerPort;
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
	 * This method initializes the text field for the DB server port	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEditPort() {
		if (this.editPort == null) {
			this.editPort = new JTextField();
		}
		return this.editPort;
	}
	
	/**
	 * This method initializes the text field for the database name	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEditDatabase() {
		if (this.editDatabase == null) {
			this.editDatabase = new JTextField();
			this.editDatabase.setText("");
		}
		return this.editDatabase;
	}
	
	/**
	 * This method initializes the text area explaining the database name	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaDatabase() {
		if (this.taDatabase == null) {
			this.taDatabase = new JTextArea();
			this.taDatabase.setBackground(null);
			this.taDatabase.setEditable(false);
			this.taDatabase.setText(Messages.getString("RelationalConnectionPanel.DatabaseExplanation")); //$NON-NLS-1$
			this.taDatabase.setLineWrap(true);
			this.taDatabase.setWrapStyleWord(true);
		}
		return this.taDatabase;
	}
	
	/**
	 * This method initializes the text field for the database user	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEditUser() {
		if (this.editUser == null) {
			this.editUser = new JTextField();
			this.editUser.setText("");
		}
		return this.editUser;
	}
	
	/**
	 * This method initializes the edit field for the database password	
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
	private JTextArea getTaUserPassword() {
		if (this.taUserPassword == null) {
			this.taUserPassword = new JTextArea();
			this.taUserPassword.setBackground(null);
			this.taUserPassword.setEditable(false);
			this.taUserPassword.setText(Messages.getString("RelationalConnectionPanel.CredentialsExplanation")); //$NON-NLS-1$
			this.taUserPassword.setLineWrap(true);
			this.taUserPassword.setWrapStyleWord(true);
		}
		return this.taUserPassword;
	}
	
	/**
	 * This method initializes the text field for the database connection string	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEditConnectionString() {
		if (this.editConnectionString == null) {
			this.editConnectionString = new JTextField();
		}
		return this.editConnectionString;
	}
	
	/**
	 * This method initializes the text area explaining the connection string.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaConnectionString() {
		if (this.taConnectionString == null) {
			this.taConnectionString = new JTextArea();
			this.taConnectionString.setBackground(null);
			this.taConnectionString.setEditable(false);
			this.taConnectionString.setText(Messages.getString("RelationalConnectionPanel.ConnectionExplanation")); //$NON-NLS-1$
			this.taConnectionString.setLineWrap(true);
			this.taConnectionString.setWrapStyleWord(true);
		}
		return this.taConnectionString;
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
			this.labelTitle.setText(Messages.getString("RelationalConnectionPanel.Title")); //$NON-NLS-1$
			this.labelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
			this.panelTitle.add(this.labelTitle, null);
		}
		return this.panelTitle;
	}

    /**
     * @return Returns the connection string entered.
     */
    public String getConnectionString() {
        return getEditConnectionString().getText();
    }

    /**
     * @return Returns the database server host.
     */
    public String getHost() {
        return getEditServer().getText();
    }

    /**
     * @return Returns the database server port.
     */
    public String getPort() {
        return getEditPort().getText();
    }

    /**
     * @return Returns the database name.
     */
    public String getDatabase() {
        return getEditDatabase().getText();
    }

    /**
     * @return Returns the database user.
     */
    public String getUser() {
        return getEditUser().getText();
    }

    /**
     * @return Returns the database password.
     */
    public String getPassword() {
        return getEditPassword().getText();
    }
    
    /* (non-Javadoc)
     * @see jwf.WizardPanel#display()
     */
    public void display() {
        super.display();
        if (!this.defaultValuesSet) {
            logger.debug("Initializing panel with default values.");
            // FIXME initialize this panel            this.defaultValuesSet = true;
        }
    }
}  //  @jve:decl-index=0:visual-constraint="12,10"
