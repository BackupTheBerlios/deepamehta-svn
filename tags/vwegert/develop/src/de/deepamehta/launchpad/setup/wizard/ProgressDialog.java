package de.deepamehta.launchpad.setup.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoundedRangeModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressDialog extends JDialog {

	private static final long serialVersionUID = 3151034783514739178L;
	
	private JPanel contentPane = null;
	private JLabel progressLabel = null;
	private JProgressBar progressBar = null;

	/**
	 * This is the default constructor
	 */
	public ProgressDialog(JFrame parent, BoundedRangeModel model) {
		super(parent);
		initialize();
		getProgressBar().setModel(model);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(396, 50);
		this.setContentPane(getContentsPane());
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getContentsPane() {
		if (contentPane == null) {
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0;
			gridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints1.gridy = 1;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.gridy = 0;
			progressLabel = new JLabel();
			progressLabel.setText("Please wait while the launch pad is creating the new instance...");
			contentPane = new JPanel();
			contentPane.setLayout(new GridBagLayout());
			contentPane.add(progressLabel, gridBagConstraints);
			contentPane.add(getProgressBar(), gridBagConstraints1);
		}
		return contentPane;
	}

	/**
	 * This method initializes jProgressBar	
	 * 	
	 * @return javax.swing.JProgressBar	
	 */
	private JProgressBar getProgressBar() {
		if (progressBar == null) {
			progressBar = new JProgressBar();
		}
		return progressBar;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
