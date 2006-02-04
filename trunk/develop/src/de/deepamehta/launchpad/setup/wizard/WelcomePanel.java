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
import javax.swing.JTextArea;
/**
 * This panels shows a welcome text and explans what this wizard is for.
 * @author vwegert
 */
public class WelcomePanel extends AbstractWizardPanel {

	private static final long serialVersionUID = 437789288752362670L;
	private JTextArea taGlobalExplanation = null;
	private JPanel filler = null;
	private JPanel panelTitle = null;
	private JLabel labelTitle = null;

	/**
	 * default constructor
     * @param parent
     */
    public WelcomePanel(SetupWizard parent) {
        super(parent);
		initialize();
 }
    
	/**
	 * This method initializes the GUI components.
	 */
	private  void initialize() {
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
		this.setLayout(new GridBagLayout());
		this.setSize(300,200);
		gridBagConstraints14.gridx = 0;
		gridBagConstraints14.gridy = 1;
		gridBagConstraints14.weightx = 1.0;
		gridBagConstraints14.weighty = 0.0D;
		gridBagConstraints14.fill = java.awt.GridBagConstraints.BOTH;
		gridBagConstraints14.gridwidth = 2;
		gridBagConstraints14.insets = new java.awt.Insets(2,2,2,2);
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.weighty = 1.0D;
		gridBagConstraints4.gridx = 1;
		gridBagConstraints4.gridy = 0;
		gridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		this.add(getTaGlobalExplanation(), gridBagConstraints14);
		this.add(getFiller(), gridBagConstraints2);
		this.add(getPanelTitle(), gridBagConstraints4);
	}
	/**
	 * This method initializes the text area containing the explanation 	
	 * @return javax.swing.JTextArea	
	 */    
	private JTextArea getTaGlobalExplanation() {
		if (this.taGlobalExplanation == null) {
			this.taGlobalExplanation = new JTextArea();
			this.taGlobalExplanation.setBackground(null);
			this.taGlobalExplanation.setEditable(false);
			this.taGlobalExplanation.setText(Messages.getString("WelcomePanel.GlobalExplanation")); //$NON-NLS-1$
			this.taGlobalExplanation.setLineWrap(true);
		}
		return this.taGlobalExplanation;
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
	 * This method initializes the panel containing the title.
	 * @return javax.swing.JPanel	
	 */    
	private JPanel getPanelTitle() {
		if (this.panelTitle == null) {
			this.labelTitle = new JLabel();
			this.panelTitle = new JPanel();
			this.panelTitle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
			this.labelTitle.setText(Messages.getString("WelcomePanel.Title")); //$NON-NLS-1$
			this.labelTitle.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 18));
			this.panelTitle.add(this.labelTitle, null);
		}
		return this.panelTitle;
	}
}  //  @jve:decl-index=0:visual-constraint="10,21"
