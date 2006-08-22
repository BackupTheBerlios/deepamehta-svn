/**
 * 
 */
package de.deepamehta.launchpad;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentFactory;

/**
 * MISSDOC No documentation for type ProcessOutputWindow
 * @author vwegert
 */
public class ProcessOutputWindow extends JFrame {

	private static final long serialVersionUID = 6581480472489424101L;
	private JPanel jContentPane = null;
	private JPanel buttonPanel = null;
	private JButton saveAsButton = null;
	private JButton clearButton = null;

	private Environment env;
	private ProcessWatcher parent;
	private JTextArea outputArea = null;
	private JScrollPane outputScrollPane = null;
	/**
	 * This is the default constructor
	 */
	public ProcessOutputWindow(ProcessWatcher parent) {
		super();
		this.env = EnvironmentFactory.getEnvironment();
		this.parent = parent;
		initialize();
	}

	/**
	 * MISSDOC No documentation for method addOutputLine of type ProcessOutputWindow
	 * @param line
	 */
	public void addOutputLine(String line) {
		getOutputArea().append(line + "\n");
		getOutputArea().setCaretPosition(getOutputArea().getText().length());
	}
	
	/**
	 * MISSDOC No documentation for method addErrorLine of type ProcessOutputWindow
	 * @param line
	 */
	public void addErrorLine(String line) {
		// TODO Let error lines appear in a different color.
		getOutputArea().append(line + "\n");
		getOutputArea().setCaretPosition(getOutputArea().getText().length());
	}
	
	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle(DeepaMehtaMessages.getString("ProcessOutputWindow.WindowTitle",
				Integer.toString(parent.getPid()), parent.getConfig().getId()));  //$NON-NLS-1$
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
			gridBagConstraints11.gridx = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getButtonPanel(), gridBagConstraints);
			jContentPane.add(getOutputScrollPane(), gridBagConstraints11);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.insets = new java.awt.Insets(2,2,2,2);
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getClearButton(), gridBagConstraints2);
			buttonPanel.add(getSaveAsButton(), gridBagConstraints3);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getSaveAsButton() {
		if (saveAsButton == null) {
			saveAsButton = new JButton();
			saveAsButton.setText(DeepaMehtaMessages.getString("ProcessOutputWindow.SaveAsButton")); //$NON-NLS-1$
			saveAsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					saveOutput();
				}
			});
		}
		return saveAsButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getClearButton() {
		if (clearButton == null) {
			clearButton = new JButton();
			clearButton.setText(DeepaMehtaMessages.getString("ProcessOutputWindow.ClearButton")); //$NON-NLS-1$
			clearButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					clearOutput();
				}
			});
		}
		return clearButton;
	}

	/**
	 * This method initializes jTextArea	
	 * 	
	 * @return javax.swing.JTextArea	
	 */
	private JTextArea getOutputArea() {
		if (outputArea == null) {
			outputArea = new JTextArea();
			outputArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 10));
			outputArea.setEditable(false);
		}
		return outputArea;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getOutputScrollPane() {
		if (outputScrollPane == null) {
			outputScrollPane = new JScrollPane();
			outputScrollPane.setViewportView(getOutputArea());
		}
		return outputScrollPane;
	}

	/**
	 * MISSDOC No documentation for method saveOutput of type ProcessOutputWindow
	 */
	protected void saveOutput() {
	    JFileChooser chooser = new JFileChooser();
	    chooser.setCurrentDirectory(new File(env.getHomeDirectory()));
	    int returnVal = chooser.showSaveDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION) {
	    	// TODO Save the output.
	    }
	}
	
	/**
	 * MISSDOC No documentation for method clearOutput of type ProcessOutputWindow
	 */
	protected void clearOutput() {
		getOutputArea().setText("");
	}
	
}
