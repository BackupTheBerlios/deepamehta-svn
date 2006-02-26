package de.deepamehta.launchpad;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import de.deepamehta.environment.Environment;

class ApplicationSelectionDialog extends JDialog {

	private Environment env;
	
	private JPanel jContentPane = null;
	private JLabel labelExplanation = null;
	private JList listApplications = null;
	private JButton btnInstall = null;
	private JButton btnCancel = null;
	private JScrollPane scrollPaneApplications = null;
	
	private String selectedApplication = null;

	public ApplicationSelectionDialog(Dialog owner, String title, boolean modal) throws HeadlessException {
		super(owner, title, modal);
		initialize();
	}

	public ApplicationSelectionDialog(Frame owner, String title, boolean modal) throws HeadlessException {
		super(owner, title, modal);
		initialize();
	}

	/**
	 * This is the default constructor
	 */
	public ApplicationSelectionDialog() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		env = Environment.getEnvironment();
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		getListApplications().setModel(env.getApplications());
		getListApplications().setSelectedIndex(0);
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints11.gridy = 1;
			gridBagConstraints11.weightx = 1.0;
			gridBagConstraints11.weighty = 1.0;
			gridBagConstraints11.gridwidth = 2;
			gridBagConstraints11.gridx = 0;
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 1;
			gridBagConstraints3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints3.insets = new java.awt.Insets(4,2,4,4);
			gridBagConstraints3.gridy = 3;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.insets = new java.awt.Insets(4,4,4,2);
			gridBagConstraints2.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints2.gridy = 3;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridwidth = 2;
			gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints.gridy = 0;
			labelExplanation = new JLabel();
			labelExplanation.setText("Please select an application to install.");
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(labelExplanation, gridBagConstraints);
			jContentPane.add(getBtnInstall(), gridBagConstraints2);
			jContentPane.add(getBtnCancel(), gridBagConstraints3);
			jContentPane.add(getScrollPaneApplications(), gridBagConstraints11);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getListApplications() {
		if (listApplications == null) {
			listApplications = new JList();
			listApplications.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			listApplications.setSelectedIndex(0);
		}
		return listApplications;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnInstall() {
		if (btnInstall == null) {
			btnInstall = new JButton();
			btnInstall.setText("Install");
			btnInstall.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					selectedApplication = (String) getListApplications().getSelectedValue();
					hide();
				}
			});
		}
		return btnInstall;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBtnCancel() {
		if (btnCancel == null) {
			btnCancel = new JButton();
			btnCancel.setText("Cancel");
			btnCancel.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					selectedApplication = null;
					hide();
				}
			});
		}
		return btnCancel;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getScrollPaneApplications() {
		if (scrollPaneApplications == null) {
			scrollPaneApplications = new JScrollPane();
			scrollPaneApplications.setViewportView(getListApplications());
		}
		return scrollPaneApplications;
	}

	/**
	 * @return Returns the selectedApplication.
	 */
	public String getSelectedApplication() {
		return selectedApplication;
	}

}
