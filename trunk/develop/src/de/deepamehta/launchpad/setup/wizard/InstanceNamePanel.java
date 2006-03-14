/*
 * Created on 13.01.2006
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
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.instance.InstanceConfiguration;
import de.deepamehta.environment.instance.UnknownInstanceException;
/**
 * This panel obtains the instance ID and description.
 * @author vwegert
 */
public class InstanceNamePanel extends AbstractWizardPanel {

	private static final long serialVersionUID = -7066643466608313151L;
	private JPanel panelTitle = null;
	private JLabel labelTitle = null;
	private JTextArea taGlobalExplanation = null;
	private JTextArea taIDExplanation = null;
	private JLabel labelID = null;
	private JPanel filler = null;
	private JLabel labelDescription = null;
	private JTextArea taDescriptionExplanation = null;
	private JTextField editID = null;
	private JTextField editDescription = null;
	
    /**
     * default constructor
     * @param parent
     */
    public InstanceNamePanel(SetupWizard parent) {
        super(parent);
		initialize();
    }

    /**
	 * This method initializes the GUI components.
	 */
	private  void initialize() {
		GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		this.labelDescription = new JLabel();
		this.labelID = new JLabel();
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(300,200);
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.gridwidth = 3;
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 1;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.weighty = 0.0D;
		gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints2.gridwidth = 2;
		gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints4.gridx = 2;
		gridBagConstraints4.gridy = 3;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.weighty = 0.0D;
		gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints4.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints4.gridwidth = 1;
		gridBagConstraints5.gridx = 1;
		gridBagConstraints5.gridy = 2;
		gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
		gridBagConstraints5.insets = new java.awt.Insets(2,2,2,2);
		this.labelID.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceNamePanel.ID")); //$NON-NLS-1$
		gridBagConstraints6.gridx = 1;
		gridBagConstraints6.gridy = 6;
		gridBagConstraints6.fill = java.awt.GridBagConstraints.VERTICAL;
		gridBagConstraints6.weighty = 1.0D;
		gridBagConstraints8.gridx = 1;
		gridBagConstraints8.gridy = 4;
		gridBagConstraints8.anchor = java.awt.GridBagConstraints.WEST;
		this.labelDescription.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceNamePanel.Description")); //$NON-NLS-1$
		this.add(getPanelTitle(), gridBagConstraints1);
		gridBagConstraints9.gridx = 2;
		gridBagConstraints9.gridy = 5;
		gridBagConstraints9.weightx = 1.0;
		gridBagConstraints9.weighty = 0.0D;
		gridBagConstraints9.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints9.insets = new java.awt.Insets(0,2,2,2);
		gridBagConstraints9.gridwidth = 1;
		gridBagConstraints11.gridx = 2;
		gridBagConstraints11.gridy = 2;
		gridBagConstraints11.weightx = 1.0;
		gridBagConstraints11.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints21.gridx = 2;
		gridBagConstraints21.gridy = 4;
		gridBagConstraints21.weightx = 1.0;
		gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
		this.add(getTaGlobalExplanation(), gridBagConstraints2);
		this.add(getTaIDExplanation(), gridBagConstraints4);
		this.add(this.labelID, gridBagConstraints5);
		this.add(getFiller(), gridBagConstraints6);
		this.add(this.labelDescription, gridBagConstraints8);
		this.add(getTaDescriptionExplanation(), gridBagConstraints9);
		this.add(getEditID(), gridBagConstraints11);
		this.add(getEditDescription(), gridBagConstraints21);
	}
	
	/**
	 * This method initializes the panel containing the title.	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getPanelTitle() {
		if (this.panelTitle == null) {
			this.labelTitle = new JLabel();
			this.panelTitle = new JPanel();
			this.labelTitle.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceNamePanel.Title")); //$NON-NLS-1$
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
			this.taGlobalExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceNamePanel.GlobalExplanation")); //$NON-NLS-1$
			this.taGlobalExplanation.setBackground(null);
			this.taGlobalExplanation.setEditable(false);
			this.taGlobalExplanation.setLineWrap(true);
			this.taGlobalExplanation.setWrapStyleWord(true);
		}
		return this.taGlobalExplanation;
	}
	
	/**
	 * This method initializes the text area explaining the instance ID.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaIDExplanation() {
		if (this.taIDExplanation == null) {
			this.taIDExplanation = new JTextArea();
			this.taIDExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceNamePanel.IDExplanation")); //$NON-NLS-1$
			this.taIDExplanation.setBackground(null);
			this.taIDExplanation.setEditable(false);
			this.taIDExplanation.setLineWrap(true);
			this.taIDExplanation.setWrapStyleWord(true);
		}
		return this.taIDExplanation;
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
	 * This method initializes the text area explaining the description.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaDescriptionExplanation() {
		if (this.taDescriptionExplanation == null) {
			this.taDescriptionExplanation = new JTextArea();
			this.taDescriptionExplanation.setText(DeepaMehtaMessages.getString("SetupWizard.InstanceNamePanel.DescriptionExplanation")); //$NON-NLS-1$
			this.taDescriptionExplanation.setBackground(null);
			this.taDescriptionExplanation.setLineWrap(true);
			this.taDescriptionExplanation.setEditable(false);
			this.taDescriptionExplanation.setWrapStyleWord(true);
		}
		return this.taDescriptionExplanation;
	}
	
	/**
	 * This method initializes the text field for the ID
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEditID() {
		if (this.editID == null) {
			this.editID = new JTextField();
		}
		return this.editID;
	}
	
	/**
	 * This method initializes the text field for the description.	
	 * @return javax.swing.JTextField	
	 */    
	private JTextField getEditDescription() {
		if (this.editDescription == null) {
			this.editDescription = new JTextField();
		}
		return this.editDescription;
	}
	
    /**
     * @return Returns the instance ID.
     */
    public String getID() {
        return getEditID().getText();
    }
    
    /**
     * @return Returns the instance description.
     */
    public String getDescription() {
        return getEditDescription().getText();
    }

	/* (non-Javadoc)
	 * @see de.deepamehta.launchpad.setup.wizard.AbstractWizardPanel#validateNext(java.util.List)
	 */
	public boolean validateNext(List list) {
		
		boolean valid = true;
		
		if (getEditID().getText().equals("")) {
			list.add(DeepaMehtaMessages.getString("SetupWizard.InstanceNamePanel.ErrorMissingID")); //$NON-NLS-1$
			valid = false;
		} else {
			// check whether ID is valid
			String id = getEditID().getText();
			
			if (!id.matches("^[a-zA-Z0-9]*$")) {
				list.add(DeepaMehtaMessages.getString("SetupWizard.InstanceNamePanel.ErrorIDFormat")); //$NON-NLS-1$
				valid = false;
			} else {
				InstanceConfiguration inst;
				// check whether ID already exists
				try {
					inst = env.getInstance(id);
				} catch (UnknownInstanceException e) {
					inst = null;
				}
				if (inst != null) {
					list.add(DeepaMehtaMessages.getString("SetupWizard.InstanceNamePanel.ErrorDuplicateID", id));  //$NON-NLS-1$
					valid = false;
				}
			}
		}
		
		return valid;
	}
}
