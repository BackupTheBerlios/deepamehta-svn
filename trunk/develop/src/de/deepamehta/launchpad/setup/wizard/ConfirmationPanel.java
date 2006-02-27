/*
 * Created on 02.01.2006
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad.setup.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListModel;

import de.deepamehta.DeepaMehtaMessages;
/**
 * This panel is the last panel of the wizard. It summarizes the actions planned.
 * @author vwegert
 */
public class ConfirmationPanel extends AbstractWizardPanel {

	private static final long serialVersionUID = -1934797486863842019L;
	private JTextArea taActionList = null;
	private JTextArea taNextSteps = null;
	private JList listActions = null;
	private JPanel panelTitle = null;
	private JLabel labelTitle = null;
	
    /**
     * default constructor
     * @param parent
     */
    public ConfirmationPanel(SetupWizard parent) {
        super(parent);
		initialize();
    }
    
	/**
	 * This method initializes the GUI Components
	 */
	private  void initialize() {
		GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(300,200);
		gridBagConstraints20.gridx = 0;
		gridBagConstraints20.gridy = 5;
		gridBagConstraints20.weightx = 1.0;
		gridBagConstraints20.weighty = 0.0D;
		gridBagConstraints20.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints20.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 2;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints1.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints10.gridx = 0;
		gridBagConstraints10.gridy = 0;
		gridBagConstraints10.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints11.gridx = 0;
		gridBagConstraints11.gridy = 1;
		gridBagConstraints11.weightx = 1.0;
		gridBagConstraints11.weighty = 0.0D;
		gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
		this.add(getTaNextSteps(), gridBagConstraints20);
		this.add(getListActions(), gridBagConstraints1);
		this.add(getPanelTitle(), gridBagConstraints10);
		this.add(getTaActionList(), gridBagConstraints11);
	}
	
	/**
	 * This method initializes the text area containing the global explanation.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaActionList() {
		if (this.taActionList == null) {
			this.taActionList = new JTextArea();
			this.taActionList.setBackground(null);
			this.taActionList.setEditable(false);
			this.taActionList.setText(DeepaMehtaMessages.getString("SetupWizard.ConfirmationPanel.GlobalExplanation")); //$NON-NLS-1$
			this.taActionList.setLineWrap(true);
			this.taActionList.setWrapStyleWord(true);
		}
		return this.taActionList;
	}
	
	/**
	 * This method initializes the lower explanation text.	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaNextSteps() {
		if (this.taNextSteps == null) {
			this.taNextSteps = new JTextArea();
			this.taNextSteps.setBackground(null);
			this.taNextSteps.setText(DeepaMehtaMessages.getString("SetupWizard.ConfirmationPanel.NextStepsExplanation")); //$NON-NLS-1$
			this.taNextSteps.setEditable(false);
			this.taNextSteps.setLineWrap(true);
			this.taNextSteps.setWrapStyleWord(true);
		}
		return this.taNextSteps;
	}
	
	/**
	 * This method initializes the list of actions.	
	 * @return javax.swing.JList	
	 */    
	private JList getListActions() {
		if (this.listActions == null) {
			this.listActions = new JList();
		}
		return this.listActions;
	}
	
 	/**
	 * This method initializes the panel containing the title	
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getPanelTitle() {
		if (this.panelTitle == null) {
			this.labelTitle = new JLabel();
			this.panelTitle = new JPanel();
			this.labelTitle.setText(DeepaMehtaMessages.getString("SetupWizard.ConfirmationPanel.Title")); //$NON-NLS-1$
			this.labelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
			this.panelTitle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			this.panelTitle.add(this.labelTitle, null);
		}
		return this.panelTitle;
	}
	
	/* (non-Javadoc)
     * @see jwf.WizardPanel#canFinish()
     */
    public boolean canFinish() {
        return true;
    }
    
    /* (non-Javadoc)
     * @see jwf.WizardPanel#hasNext()
     */
    public boolean hasNext() {
        return false;
    }
    
    public void setModel(ListModel model) {
    	getListActions().setModel(model);
    }
    
}
