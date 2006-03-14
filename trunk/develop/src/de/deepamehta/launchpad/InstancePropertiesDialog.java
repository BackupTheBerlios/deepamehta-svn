/**
 * 
 */
package de.deepamehta.launchpad;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentException;
import de.deepamehta.environment.EnvironmentFactory;
import de.deepamehta.environment.instance.InstanceConfiguration;

/**
 * MISSDOC No documentation for type InstancePropertiesDialog
 * @author vwegert
 *
 */
public class InstancePropertiesDialog extends JDialog {

	private static Log logger = LogFactory.getLog(InstancePropertiesDialog.class);
	
	private Environment env;
	
	private JPanel jContentPane = null;
	private JTabbedPane tabbedPane = null;
	private JPanel instanceSettingsPanel = null;
	private JPanel corporateMemorySettingsPanel = null;
	private JRadioButton monolithicRadioButton = null;
	private JLabel monolithicLabel = null;
	private JRadioButton serverRadioButton = null;
	private JLabel serverLabel = null;
	private JLabel serverInterfaceLabel = null;
	private JTextField serverInterfaceEdit = null;
	private JLabel serverPortLabel = null;
	private JTextField serverPortEdit = null;
	private JRadioButton clientRadioButton = null;
	private JLabel clientLabel = null;
	private JLabel clientHostLabel = null;
	private JTextField clientHostEdit = null;
	private JLabel clientPortLabel = null;
	private JTextField clientPortEdit = null;
	private JPanel instanceSettingsFiller = null;
	private JLabel implementationLabel = null;
	private JComboBox implementationComboBox = null;
	private JLabel propertiesLabel = null;
	private JScrollPane propertiesScrollPane = null;
	private JTable propertiesTable = null;
	private JPanel buttonPanel = null;
	private JButton okButton = null;
	private JButton cancelButton = null;

	private ButtonGroup instanceTypeButtonGroup = null;
	
	private InstanceConfiguration config = null;

	private JButton addPropertyButton = null;

	private JButton removePropertyButton = null;

	private JLabel typeLabel = null;

	private JLabel idLabel = null;

	private JLabel descriptionLabel = null;

	private JTextField idEdit = null;

	private JTextField descriptionEdit = null;
	
	/**
	 * This is the default constructor
	 */
	public InstancePropertiesDialog(JFrame parent) {
		super(parent);
		env = EnvironmentFactory.getEnvironment();
		initialize();
	}

	public void show(InstanceConfiguration config) {
		this.config = config;
		this.loadInstanceData();
		super.show();
	}

	/**
	 * MISSDOC No documentation for method loadInstanceData of type InstancePropertiesDialog
	 * @param config
	 */
	private void loadInstanceData() {
		
		getIdEdit().setText(config.getId());
		getDescriptionEdit().setText(config.getDescription());
		
		if (config.getInstanceType().isMonolithic()) {
			getMonolithicRadioButton().setSelected(true);
		}
		
		if (config.getInstanceType().isServer()) {
			getServerRadioButton().setSelected(true);
			getServerInterfaceEdit().setText(config.getServerInterface());
			getServerPortEdit().setText(Integer.toString(config.getServerPort()));
		}
		
		if (config.getInstanceType().isClient()) {
			getClientRadioButton().setSelected(true);
			getClientHostEdit().setText(config.getClientHost());
			getClientPortEdit().setText(Integer.toString(config.getClientPort()));
		}
		
		if (!config.getInstanceType().isClient()) {
			try {
				getImplementationComboBox().setSelectedItem(config.getCMConfig().getImplementingClassName());
				config.getCMConfig().backupProperties();
				getPropertiesTable().setModel(config.getCMConfig());
			} catch (EnvironmentException e) {
				logger.error("Unable to determine CM implementation.", e);
			}
		}
		
		adjustTypeDependentFields();
	}
	
	
	/**
	 * MISSDOC No documentation for method saveInstanceData of type InstancePropertiesDialog
	 * @param config
	 */
	private void saveInstanceData() {
		config.setDescription(getDescriptionEdit().getText());
		
		if (getMonolithicRadioButton().isSelected()) {
			config.setInstanceTypeMonolithic();	
		}
		
		if (getServerRadioButton().isSelected()) {
			config.setInstanceTypeServer();	
			config.setServerInterface(getServerInterfaceEdit().getText());
			config.setServerPort(Integer.parseInt(getServerPortEdit().getText()));
		}
		
		if (getClientRadioButton().isSelected()) {
			config.setInstanceTypeClient();
			config.setClientHost(getClientHostEdit().getText());
			config.setClientPort(Integer.parseInt(getClientPortEdit().getText()));
		}
		
		// TODO Save CM settings after editing.
		
		env.saveInstances();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(618, 349);
		this.setModal(true);
		this.setContentPane(getJContentPane());
		
		// setup radio button group 
		this.instanceTypeButtonGroup = new ButtonGroup();
		this.instanceTypeButtonGroup.add(getMonolithicRadioButton());
		this.instanceTypeButtonGroup.add(getServerRadioButton());
		this.instanceTypeButtonGroup.add(getClientRadioButton());
		
		// setup CM implementation combo box
		initializeImplementationComboBox();
	}

	/**
     * This method initializes the combo box containing the CM implementations. 
     */
    private void initializeImplementationComboBox() {
        // TODO remove hard-coded CM implementation name
        getImplementationComboBox().removeAllItems();
        getImplementationComboBox().addItem("de.deepamehta.service.RelationalCorporateMemory");
    }
    
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
			gridBagConstraints22.gridx = 0;
			gridBagConstraints22.weightx = 1.0D;
			gridBagConstraints22.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints22.gridy = 1;
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints21.gridy = 0;
			gridBagConstraints21.ipadx = -128;
			gridBagConstraints21.ipady = -234;
			gridBagConstraints21.weightx = 1.0;
			gridBagConstraints21.weighty = 1.0;
			gridBagConstraints21.gridx = 0;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getTabbedPane(), gridBagConstraints21);
			jContentPane.add(getButtonPanel(), gridBagConstraints22);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.addTab(DeepaMehtaMessages.getString("InstancePropertiesDialog.InstanceSettingsTabTitle"), null, getInstanceSettingsPanel(), null); //$NON-NLS-1$
			tabbedPane.addTab(DeepaMehtaMessages.getString("InstancePropertiesDialog.CorporateMemorySettingsTabTitle"), null, getCorporateMemorySettingsPanel(), null); //$NON-NLS-1$
		}
		return tabbedPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getInstanceSettingsPanel() {
		if (instanceSettingsPanel == null) {
			GridBagConstraints gridBagConstraints51 = new GridBagConstraints();
			gridBagConstraints51.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints51.gridy = 1;
			gridBagConstraints51.weightx = 1.0;
			gridBagConstraints51.gridwidth = 3;
			gridBagConstraints51.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints51.gridx = 1;
			GridBagConstraints gridBagConstraints41 = new GridBagConstraints();
			gridBagConstraints41.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints41.gridy = 0;
			gridBagConstraints41.weightx = 1.0;
			gridBagConstraints41.gridwidth = 3;
			gridBagConstraints41.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints41.gridx = 1;
			GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
			gridBagConstraints31.gridx = 0;
			gridBagConstraints31.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints31.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints31.gridy = 1;
			descriptionLabel = new JLabel();
			descriptionLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.InstanceDescriptionLabel")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints25 = new GridBagConstraints();
			gridBagConstraints25.gridx = 0;
			gridBagConstraints25.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints25.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints25.gridy = 0;
			idLabel = new JLabel();
			idLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.InstanceIDLabel")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints110 = new GridBagConstraints();
			gridBagConstraints110.gridx = 0;
			gridBagConstraints110.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints110.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints110.gridy = 2;
			typeLabel = new JLabel();
			typeLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.InstanceTypeLabel")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints14 = new GridBagConstraints();
			gridBagConstraints14.gridx = 2;
			gridBagConstraints14.weighty = 1.0D;
			gridBagConstraints14.gridy = 9;
			GridBagConstraints gridBagConstraints13 = new GridBagConstraints();
			gridBagConstraints13.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints13.gridy = 8;
			gridBagConstraints13.weightx = 1.0;
			gridBagConstraints13.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints13.gridx = 3;
			GridBagConstraints gridBagConstraints11 = new GridBagConstraints();
			gridBagConstraints11.gridx = 2;
			gridBagConstraints11.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints11.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints11.gridy = 8;
			clientPortLabel = new JLabel();
			clientPortLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.ClientPortLabel")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints12 = new GridBagConstraints();
			gridBagConstraints12.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints12.gridy = 7;
			gridBagConstraints12.weightx = 1.0;
			gridBagConstraints12.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints12.gridx = 3;
			GridBagConstraints gridBagConstraints10 = new GridBagConstraints();
			gridBagConstraints10.gridx = 2;
			gridBagConstraints10.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints10.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints10.gridy = 7;
			clientHostLabel = new JLabel();
			clientHostLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.ClientHostLabel")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints9 = new GridBagConstraints();
			gridBagConstraints9.gridx = 2;
			gridBagConstraints9.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints9.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints9.gridy = 6;
			clientLabel = new JLabel();
			clientLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.InstanceTypeClientLabel")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.gridx = 1;
			gridBagConstraints8.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints8.gridy = 6;
			GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
			gridBagConstraints7.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints7.gridy = 5;
			gridBagConstraints7.weightx = 1.0;
			gridBagConstraints7.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints7.gridx = 3;
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.gridx = 2;
			gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints6.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints6.gridy = 5;
			serverPortLabel = new JLabel();
			serverPortLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.ServerPortLabel")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints5.gridy = 4;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints5.gridx = 3;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.gridx = 2;
			gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints4.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints4.gridy = 4;
			serverInterfaceLabel = new JLabel();
			serverInterfaceLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.ServerInterfaceLabel")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = 2;
			gridBagConstraints3.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints3.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints3.gridy = 3;
			serverLabel = new JLabel();
			serverLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.InstanceTypeServerLabel")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 1;
			gridBagConstraints2.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints2.gridy = 3;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 2;
			gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints1.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints1.gridy = 2;
			monolithicLabel = new JLabel();
			monolithicLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.InstanceTypeMonolithicLabel")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 1;
			gridBagConstraints.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints.gridy = 2;
			instanceSettingsPanel = new JPanel();
			instanceSettingsPanel.setLayout(new GridBagLayout());
			instanceSettingsPanel.add(getServerRadioButton(), gridBagConstraints2);
			instanceSettingsPanel.add(serverLabel, gridBagConstraints3);
			instanceSettingsPanel.add(serverInterfaceLabel, gridBagConstraints4);
			instanceSettingsPanel.add(getServerInterfaceEdit(), gridBagConstraints5);
			instanceSettingsPanel.add(serverPortLabel, gridBagConstraints6);
			instanceSettingsPanel.add(getServerPortEdit(), gridBagConstraints7);
			instanceSettingsPanel.add(getClientRadioButton(), gridBagConstraints8);
			instanceSettingsPanel.add(clientLabel, gridBagConstraints9);
			instanceSettingsPanel.add(clientHostLabel, gridBagConstraints10);
			instanceSettingsPanel.add(getClientHostEdit(), gridBagConstraints12);
			instanceSettingsPanel.add(clientPortLabel, gridBagConstraints11);
			instanceSettingsPanel.add(getClientPortEdit(), gridBagConstraints13);
			instanceSettingsPanel.add(getInstanceSettingsFiller(), gridBagConstraints14);
			instanceSettingsPanel.add(idLabel, gridBagConstraints25);
			instanceSettingsPanel.add(getIdEdit(), gridBagConstraints41);
			instanceSettingsPanel.add(descriptionLabel, gridBagConstraints31);
			instanceSettingsPanel.add(getDescriptionEdit(), gridBagConstraints51);
			instanceSettingsPanel.add(typeLabel, gridBagConstraints110);
			instanceSettingsPanel.add(getMonolithicRadioButton(), gridBagConstraints);
			instanceSettingsPanel.add(monolithicLabel, gridBagConstraints1);
		}
		return instanceSettingsPanel;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getCorporateMemorySettingsPanel() {
		if (corporateMemorySettingsPanel == null) {
			GridBagConstraints gridBagConstraints18 = new GridBagConstraints();
			gridBagConstraints18.gridx = 2;
			gridBagConstraints18.weightx = 1.0D;
			gridBagConstraints18.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints18.insets = new java.awt.Insets(2,1,2,2);
			gridBagConstraints18.gridy = 3;
			GridBagConstraints gridBagConstraints17 = new GridBagConstraints();
			gridBagConstraints17.gridx = 1;
			gridBagConstraints17.weighty = 0.0D;
			gridBagConstraints17.weightx = 1.0D;
			gridBagConstraints17.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints17.insets = new java.awt.Insets(2,2,2,1);
			gridBagConstraints17.gridy = 3;
			GridBagConstraints gridBagConstraints20 = new GridBagConstraints();
			gridBagConstraints20.fill = java.awt.GridBagConstraints.BOTH;
			gridBagConstraints20.gridy = 2;
			gridBagConstraints20.weightx = 1.0;
			gridBagConstraints20.weighty = 1.0;
			gridBagConstraints20.gridwidth = 2;
			gridBagConstraints20.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints20.gridx = 1;
			GridBagConstraints gridBagConstraints19 = new GridBagConstraints();
			gridBagConstraints19.gridx = 0;
			gridBagConstraints19.anchor = java.awt.GridBagConstraints.NORTHWEST;
			gridBagConstraints19.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints19.gridy = 2;
			propertiesLabel = new JLabel();
			propertiesLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.CorporateMemoryPropertiesLabel")); //$NON-NLS-1$
			GridBagConstraints gridBagConstraints16 = new GridBagConstraints();
			gridBagConstraints16.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints16.gridy = 0;
			gridBagConstraints16.weightx = 1.0;
			gridBagConstraints16.gridwidth = 2;
			gridBagConstraints16.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints16.gridx = 1;
			GridBagConstraints gridBagConstraints15 = new GridBagConstraints();
			gridBagConstraints15.gridx = 0;
			gridBagConstraints15.anchor = java.awt.GridBagConstraints.WEST;
			gridBagConstraints15.insets = new java.awt.Insets(2,2,2,2);
			gridBagConstraints15.gridy = 0;
			implementationLabel = new JLabel();
			implementationLabel.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.CorporateMemoryImplementationLabel")); //$NON-NLS-1$
			corporateMemorySettingsPanel = new JPanel();
			corporateMemorySettingsPanel.setLayout(new GridBagLayout());
			corporateMemorySettingsPanel.add(implementationLabel, gridBagConstraints15);
			corporateMemorySettingsPanel.add(getImplementationComboBox(), gridBagConstraints16);
			corporateMemorySettingsPanel.add(propertiesLabel, gridBagConstraints19);
			corporateMemorySettingsPanel.add(getPropertiesScrollPane(), gridBagConstraints20);
			corporateMemorySettingsPanel.add(getAddPropertyButton(), gridBagConstraints17);
			corporateMemorySettingsPanel.add(getRemovePropertyButton(), gridBagConstraints18);
		}
		return corporateMemorySettingsPanel;
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getMonolithicRadioButton() {
		if (monolithicRadioButton == null) {
			monolithicRadioButton = new JRadioButton();
			monolithicRadioButton.setText(""); //$NON-NLS-1$
			monolithicRadioButton.setSelected(true);
			monolithicRadioButton.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					adjustTypeDependentFields();
				}
			});
		}
		return monolithicRadioButton;
	}

	/**
	 * MISSDOC No documentation for method adjustTypeDependentFields of type InstancePropertiesDialog
	 */
	protected void adjustTypeDependentFields() {
		if (getMonolithicRadioButton().isSelected()) {
			getMonolithicRadioButton().setEnabled(true);
			getServerRadioButton().setEnabled(true);
			getServerInterfaceEdit().setEnabled(false);
			getServerPortEdit().setEnabled(false);
			getClientRadioButton().setEnabled(false);
			getClientHostEdit().setEnabled(false);
			getClientPortEdit().setEnabled(false);
			getTabbedPane().setEnabledAt(1, true);
		}
		if (getServerRadioButton().isSelected()) {
			getMonolithicRadioButton().setEnabled(true);
			getServerRadioButton().setEnabled(true);
			getServerInterfaceEdit().setEnabled(true);
			getServerPortEdit().setEnabled(true);
			getClientRadioButton().setEnabled(false);
			getClientHostEdit().setEnabled(false);
			getClientPortEdit().setEnabled(false);		
			getTabbedPane().setEnabledAt(1, true);
		}
		if (getClientRadioButton().isSelected()) {
			getMonolithicRadioButton().setEnabled(false);
			getServerRadioButton().setEnabled(false);
			getServerInterfaceEdit().setEnabled(false);
			getServerPortEdit().setEnabled(false);
			getClientRadioButton().setEnabled(true);
			getClientHostEdit().setEnabled(true);
			getClientPortEdit().setEnabled(true);
			getTabbedPane().setEnabledAt(1, false);
		}
	}

	/**
	 * This method initializes jRadioButton1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getServerRadioButton() {
		if (serverRadioButton == null) {
			serverRadioButton = new JRadioButton();
			serverRadioButton.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					adjustTypeDependentFields();
				}
			});
		}
		return serverRadioButton;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getServerInterfaceEdit() {
		if (serverInterfaceEdit == null) {
			serverInterfaceEdit = new JTextField();
		}
		return serverInterfaceEdit;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getServerPortEdit() {
		if (serverPortEdit == null) {
			serverPortEdit = new JTextField();
		}
		return serverPortEdit;
	}

	/**
	 * This method initializes jRadioButton2	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getClientRadioButton() {
		if (clientRadioButton == null) {
			clientRadioButton = new JRadioButton();
			clientRadioButton.addChangeListener(new javax.swing.event.ChangeListener() {
				public void stateChanged(javax.swing.event.ChangeEvent e) {
					adjustTypeDependentFields();
				}
			});
		}
		return clientRadioButton;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getClientHostEdit() {
		if (clientHostEdit == null) {
			clientHostEdit = new JTextField();
		}
		return clientHostEdit;
	}

	/**
	 * This method initializes jTextField3	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getClientPortEdit() {
		if (clientPortEdit == null) {
			clientPortEdit = new JTextField();
		}
		return clientPortEdit;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getInstanceSettingsFiller() {
		if (instanceSettingsFiller == null) {
			instanceSettingsFiller = new JPanel();
		}
		return instanceSettingsFiller;
	}

	/**
	 * This method initializes jComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getImplementationComboBox() {
		if (implementationComboBox == null) {
			implementationComboBox = new JComboBox();
		}
		return implementationComboBox;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getPropertiesScrollPane() {
		if (propertiesScrollPane == null) {
			propertiesScrollPane = new JScrollPane();
			propertiesScrollPane.setViewportView(getPropertiesTable());
		}
		return propertiesScrollPane;
	}

	/**
	 * This method initializes jTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getPropertiesTable() {
		if (propertiesTable == null) {
			propertiesTable = new JTable();
		}
		return propertiesTable;
	}

	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			GridBagConstraints gridBagConstraints24 = new GridBagConstraints();
			gridBagConstraints24.insets = new java.awt.Insets(5,25,5,25);
			gridBagConstraints24.gridy = 0;
			gridBagConstraints24.gridx = 1;
			GridBagConstraints gridBagConstraints23 = new GridBagConstraints();
			gridBagConstraints23.insets = new java.awt.Insets(5,25,5,25);
			gridBagConstraints23.gridy = 0;
			gridBagConstraints23.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints23.gridx = 0;
			buttonPanel = new JPanel();
			buttonPanel.setLayout(new GridBagLayout());
			buttonPanel.add(getOkButton(), gridBagConstraints23);
			buttonPanel.add(getCancelButton(), gridBagConstraints24);
		}
		return buttonPanel;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton();
			okButton.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.OKButton")); //$NON-NLS-1$
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					saveInstanceData();
					hide();
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.CancelButton")); //$NON-NLS-1$
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try {
						config.getCMConfig().restoreProperties();
					} catch (EnvironmentException e1) {
						// May be harmless
					}
					hide();
				}
			});
		}
		return cancelButton;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getAddPropertyButton() {
		if (addPropertyButton == null) {
			addPropertyButton = new JButton();
			addPropertyButton.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.AddPropertyButton")); //$NON-NLS-1$
			addPropertyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// TODO Add code to insert a new property.
				}
			});
		}
		return addPropertyButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getRemovePropertyButton() {
		if (removePropertyButton == null) {
			removePropertyButton = new JButton();
			removePropertyButton.setText(DeepaMehtaMessages.getString("InstancePropertiesDialog.RemovePropertyButton")); //$NON-NLS-1$
			removePropertyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// TODO Add code to remove a property.
				}
			});
		}
		return removePropertyButton;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getIdEdit() {
		if (idEdit == null) {
			idEdit = new JTextField();
			idEdit.setEnabled(false);
		}
		return idEdit;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getDescriptionEdit() {
		if (descriptionEdit == null) {
			descriptionEdit = new JTextField();
		}
		return descriptionEdit;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
