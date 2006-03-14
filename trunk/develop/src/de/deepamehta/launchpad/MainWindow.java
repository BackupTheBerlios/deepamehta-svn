/*
 * Created on 18.12.2005
 *
 * This file is part of the DeepaMehta framework.
 */
package de.deepamehta.launchpad;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.DeepaMehtaMessages;
import de.deepamehta.environment.Environment;
import de.deepamehta.environment.EnvironmentFactory;
import de.deepamehta.environment.instance.InstanceTableModel;

/**
 * This class represents the main window of the instance manager. It contains
 * the instance list as well as a menu and some buttons to issue commands.
 * @author vwegert
 */
public class MainWindow extends JFrame implements WindowListener, ActionListener, ListSelectionListener {
	
	private static final long serialVersionUID = 2315866133648402562L;
	private static final String CMD_INSTANCE_LAUNCH      = "LaunchInstance";
	private static final String CMD_INSTANCE_CREATE      = "CreateInstance";
	private static final String CMD_INSTANCE_EDIT        = "EditInstance";
	private static final String CMD_INSTANCE_DELETE      = "DeleteInstance";
	private static final String CMD_APPLICATION_INSTALL  = "InstallApplication"; 
	private static final String CMD_VIEW_SIMPLE          = "SimpleView";
	private static final String CMD_VIEW_DETAILED        = "DetailedView";
	private static final String CMD_EXIT                 = "ExitManager";
	
	/* component interconnections */
	private static Log logger = LogFactory.getLog(MainWindow.class);
	private LaunchPad manager;
	private Environment env;
	
	/* UI components */
	private DefaultListSelectionModel selModel;
	private JTable instanceTable;
	private JButton launchBtn, createBtn, editBtn, deleteBtn;
	
	/**
	 * default constructor used by the launch pad
	 * @param manager
	 * @throws HeadlessException
	 */
	public MainWindow(LaunchPad manager) throws HeadlessException {
		
		super(DeepaMehtaMessages.getString("LaunchPad.MainWindow.Title")); //$NON-NLS-1$
		this.manager = manager;
		this.env = EnvironmentFactory.getEnvironment();
		
		addWindowListener(this);
		setupMenu();
		this.selModel = new DefaultListSelectionModel();
		this.selModel.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		this.selModel.addListSelectionListener(this);
		this.instanceTable = new JTable(env.getInstanceTableModel(), null, this.selModel);
		initialize();
		
		setLocation(50, 50);
		setSize(400, 300);
		setVisible(true);		
	}
	
	/**
	 * Creates the menu entries of the main window.
	 */
	private void setupMenu() {
		JMenuBar menubar;
		JMenu menu;
		ButtonGroup bg;
		
		menubar = new JMenuBar();
		
		/* instance menu */
		menu = new JMenu(DeepaMehtaMessages.getString("LaunchPad.MainWindow.InstanceMenu"));//$NON-NLS-1$
		menu.add(makeMenuItem(DeepaMehtaMessages.getString("LaunchPad.MainWindow.LaunchInstance"), CMD_INSTANCE_LAUNCH));
		menu.addSeparator();
		menu.add(makeMenuItem(DeepaMehtaMessages.getString("LaunchPad.MainWindow.CreateInstance"), CMD_INSTANCE_CREATE)); //$NON-NLS-1$
		menu.add(makeMenuItem(DeepaMehtaMessages.getString("LaunchPad.MainWindow.EditInstance"), CMD_INSTANCE_EDIT)); //$NON-NLS-1$
		menu.add(makeMenuItem(DeepaMehtaMessages.getString("LaunchPad.MainWindow.DeleteInstance"), CMD_INSTANCE_DELETE)); //$NON-NLS-1$
		menu.addSeparator();
		menu.add(makeMenuItem(DeepaMehtaMessages.getString("LaunchPad.MainWindow.InstallApplication"), CMD_APPLICATION_INSTALL)); //$NON-NLS-1$
		menu.addSeparator();
		menu.add(makeMenuItem(DeepaMehtaMessages.getString("LaunchPad.MainWindow.ExitManager"), CMD_EXIT)); //$NON-NLS-1$
		menubar.add(menu);
		
		/* view menu */
		menu = new JMenu(DeepaMehtaMessages.getString("LaunchPad.MainWindow.ViewMenu")); //$NON-NLS-1$
		bg = new ButtonGroup();
		menu.add(makeMenuItem(DeepaMehtaMessages.getString("LaunchPad.MainWindow.SimpleView"), CMD_VIEW_SIMPLE, bg)); //$NON-NLS-1$
		menu.add(makeMenuItem(DeepaMehtaMessages.getString("LaunchPad.MainWindow.DetailedView"), CMD_VIEW_DETAILED, bg)); //$NON-NLS-1$
		menubar.add(menu);
		
		setJMenuBar(menubar);
	}

	/**
	 * Convencience method to create a simple menu item.
	 * @param label The text to display
	 * @param action The action to emit
	 * @return A menu item.
	 */
	private JMenuItem makeMenuItem(String label, String action) {
		JMenuItem item = new JMenuItem(label);
		item.setActionCommand(action);
		item.addActionListener(this);
		return item;
	}

	/**
	 * Convenience method to create a checkable menu item.
	 * @param label The text to display
	 * @param action The action to emit
	 * @param group The group to add this item to
	 * @return A menu item.
	 */
	private JMenuItem makeMenuItem(String label, String action, ButtonGroup group) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(label);
		item.setActionCommand(action);
		item.addActionListener(this);
		group.add(item);
		if (group.getButtonCount() == 1) {
			item.setSelected(true);
		}
		return item;
	}
	
	/**
	 * This method initializes the GUI components.
	 */
	private void initialize() {
		JPanel padding;
		JScrollPane tableScrollPane;
		GridBagLayout contentLayout;
		GridBagConstraints con;
		Container contentPane;

		contentLayout = new GridBagLayout();

		contentPane = getContentPane();
		contentPane.setLayout(contentLayout);
	
		/* add instance table */
		tableScrollPane = new JScrollPane(this.instanceTable);
		con = makeConstraints(0, 0, 1, 5);
		con.fill = GridBagConstraints.BOTH;
		con.weightx = 1;
		con.weighty = 1;
		contentLayout.setConstraints(tableScrollPane, con);
		contentPane.add(tableScrollPane);
		
		/* add buttons */
		this.launchBtn = makeButton(DeepaMehtaMessages.getString("LaunchPad.MainWindow.LaunchInstance"), CMD_INSTANCE_LAUNCH, false); //$NON-NLS-1$
		con = makeConstraints(1, 0, 1, 1);
		con.fill = GridBagConstraints.HORIZONTAL;
		contentLayout.setConstraints(this.launchBtn, con);
		contentPane.add(this.launchBtn);

		padding = new JPanel();
		con = makeConstraints(1, 1, 1, 1);
		con.fill = GridBagConstraints.BOTH;
		con.weighty = 1;
		contentLayout.setConstraints(padding, con);
		contentPane.add(padding);
		
		this.createBtn = makeButton(DeepaMehtaMessages.getString("LaunchPad.MainWindow.CreateInstanceButton"), CMD_INSTANCE_CREATE); //$NON-NLS-1$
		con = makeConstraints(1, 2, 1, 1);
		con.fill = GridBagConstraints.HORIZONTAL;
		contentLayout.setConstraints(this.createBtn, con);
		contentPane.add(this.createBtn);
		
		this.editBtn = makeButton(DeepaMehtaMessages.getString("LaunchPad.MainWindow.InstancePropertiesButton"), CMD_INSTANCE_EDIT, false); //$NON-NLS-1$
		con = makeConstraints(1, 3, 1, 1);
		con.fill = GridBagConstraints.HORIZONTAL;
		contentLayout.setConstraints(this.editBtn, con);
		contentPane.add(this.editBtn);
		
		this.deleteBtn = makeButton(DeepaMehtaMessages.getString("LaunchPad.MainWindow.DeleteInstanceButton"), CMD_INSTANCE_DELETE, false); //$NON-NLS-1$
		con = makeConstraints(1, 4, 1, 1);
		con.fill = GridBagConstraints.HORIZONTAL;
		contentLayout.setConstraints(this.deleteBtn, con);
		contentPane.add(this.deleteBtn);
		
	}
	
	/**
	 * Convenience method to create a button.
	 * @param label
	 * @param action
	 * @return
	 */
	private JButton makeButton(String label, String action) {
		JButton button;
		
		button = new JButton(label);
		button.addActionListener(this);
		button.setActionCommand(action);
		return button;
	}
	
	/**
	 * Convenience method to create a button
	 * @param label
	 * @param action
	 * @param enabled
	 * @return
	 */
	private JButton makeButton(String label, String action, boolean enabled) {
		JButton button = makeButton(label, action);
		button.setEnabled(enabled);
		return button;
	}
	
	/**
	 * Convenience function to create a GridBagConstraints instance
	 * @param column
	 * @param row
	 * @param width
	 * @param height
	 * @return
	 */
	private GridBagConstraints makeConstraints(int column, int row, int width, int height) {
		GridBagConstraints con = new GridBagConstraints();
		con.gridx = column;
		con.gridy = row;
		con.gridwidth = width;
		con.gridheight = height;
		con.insets = new Insets(2, 2, 2, 2);;
		return con;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent action) {
		
		int index;
		String command, id;
		
		command = action.getActionCommand();
		index = this.selModel.getMinSelectionIndex();
		if (index < 0) {
			id = "";
		} else {
			id = this.env.getInstance(index).getId();
		}
		logger.debug("Action " + command + " issued on instance " + id + ".");
		
		
		/* view changes are handled by the main window itself */
		if (command.equals(CMD_VIEW_SIMPLE)) {
			((InstanceTableModel)this.env.getInstanceTableModel()).setDetailedView(false);
			return; 
		}
		if (command.equals(CMD_VIEW_DETAILED)) {
		    ((InstanceTableModel)this.env.getInstanceTableModel()).setDetailedView(true);
			return; 
		}
		
		/* instance actions are passed on to the controller */
		if (command.equals(CMD_INSTANCE_LAUNCH)) {
			// TODO Ensure that an instance is selected.
			this.manager.launchInstance(id);
			return; 
		}
		if (command.equals(CMD_INSTANCE_CREATE)) {
			this.manager.createInstance();
			return; 
		}
		if (command.equals(CMD_INSTANCE_EDIT)) {
			// TODO Ensure that an instance is selected.
			this.manager.editInstance(id);
			return; 
		}
		if (command.equals(CMD_APPLICATION_INSTALL)) {
			// TODO Ensure that an instance is selected.
			this.manager.installApplication(id);
			return;
		}
		if (command.equals(CMD_INSTANCE_DELETE)) {
			// TODO Ensure that an instance is selected.
			this.manager.deleteInstance(id);
			return; 
		}
		
		/* exit manager */
		if (command.equals(CMD_EXIT)) {
		    this.manager.stop(0);
		}
		
		/* this point should never be reached */
		logger.warn("Unhandled action in MainWindow!");
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent event) {
		int index;

		if (event.getValueIsAdjusting()) {
			index = this.selModel.getMinSelectionIndex();
			if (index < 0) {
				logger.debug("No instance selected.");
				this.launchBtn.setEnabled(false);
				this.editBtn.setEnabled(false);
				this.deleteBtn.setEnabled(false);
			} else {
				logger.debug("Entry " + Integer.toString(selModel.getMinSelectionIndex()) + " selected.");
				this.launchBtn.setEnabled(true);
				this.editBtn.setEnabled(true);
				this.deleteBtn.setEnabled(true);
			}
		}
	}

	public void windowOpened(WindowEvent arg0) {
		// nothing to do here
	}

	public void windowClosing(WindowEvent arg0) {
		this.manager.stop(0);
	}

	public void windowClosed(WindowEvent arg0) {
		// nothing to do here
	}

	public void windowIconified(WindowEvent arg0) {
		// nothing to do here
	}

	public void windowDeiconified(WindowEvent arg0) {
		// nothing to do here
	}

	public void windowActivated(WindowEvent arg0) {
		// nothing to do here
	}

	public void windowDeactivated(WindowEvent arg0) {
		// nothing to do here
	}
	
}
