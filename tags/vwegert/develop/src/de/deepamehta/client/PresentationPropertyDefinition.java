package de.deepamehta.client;

import de.deepamehta.PresentableTopic;
import de.deepamehta.PropertyDefinition;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.DeepaMehtaUtils;
//
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.text.*;
import java.io.*;
import java.util.*;



/**
 * Extends {@link de.deepamehta.PropertyDefinition} by providing a method to create a
 * suitable GUI component for editing the value of the respective property.
 * <P>
 * <CODE>PresentationPropertyDefinition</CODE> objects are created while reading a
 * {@link PresentationType}. 
 * <P>
 * <HR>
 * Last functional change: 12.10.2003 (2.0b2)<BR>
 * Last documentation update: 7.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
class PresentationPropertyDefinition extends PropertyDefinition {



	// ********************
	// *** Constructors ***
	// ********************



	public PresentationPropertyDefinition(PropertyDefinition propDef) {
		super(propDef);
	}

	/**
	 * Stream constructor.
	 *
	 * @see		PresentationType#PresentationType
	 */
	public PresentationPropertyDefinition(DataInputStream in) throws IOException {
		super(in);
	}



	// **************
	// *** Method ***
	// **************



	/**
	 * Creates a GUI component for editing a property according to this property
	 * definition.
	 *
	 * @param	actionListener		may be null
	 *
	 * @return	A 1- or 2-element array holding the view and the model (depends on
	 *			visualization mode) 
	 *
	 * @see		PropertyPanel.PropertyField#PropertyPanel.PropertyField
	 */
	Object[] createGUIComponent(ActionListener actionListener, PropertyPanelControler controler) {
		Object result[] = new Object[2];
		if (visualization.equals(VISUAL_FIELD) ||
			visualization.equals(VISUAL_FILE_CHOOSER) ||
			visualization.equals(VISUAL_COLOR_CHOOSER) ||
			visualization.equals("")) {
			JComponent field = createTextField(actionListener);
			// --- add action button ---
			if (hasActionButton) {
				result[0] = addActionButton(field, actionListener, BoxLayout.X_AXIS);
			} else {
				result[0] = field;
			}
			result[1] = field;
			return result;
		} else if (visualization.equals(VISUAL_PASSWORD_FIELD)) {
			// ### VISUAL_PASSWORD_FIELD doesn't yet support action buttons
			JComponent field = createPasswordField(actionListener);
			result[0] = field;
			result[1] = field;		// ### needed?
			return result;
		} else if (visualization.equals(VISUAL_AREA)) {
			// ### VISUAL_AREA doesn't yet support action buttons
			JComponent area = createTextArea();
			result[0] = new JScrollPane(area);
			result[1] = area;
			return result;
		} else if (visualization.equals(VISUAL_TEXT_EDITOR)) {
			createTextEditor(actionListener, result, controler);
			return result;
		} else if (visualization.equals(VISUAL_CHOICE)) {
			result[0] = createOptionMenu(actionListener, controler);
			return result;
		} else if (visualization.equals(VISUAL_RADIOBUTTONS)) {
			ButtonGroup group = new ButtonGroup();		
			result[0] = createOptionButtons(actionListener, group, controler);
			result[1] = group;
			return result;
		} else if (visualization.equals(VISUAL_SWITCH)) {
			result[0] = createSwitch(actionListener);
			return result;
		} else if (visualization.equals(VISUAL_DATE_CHOOSER)) {
			createDateChooser(actionListener, result);
			return result;
		} else if (visualization.equals(VISUAL_TIME_CHOOSER)) {
			createTimeChooser(actionListener, result);
			return result;
		} else if (visualization.equals(VISUAL_HIDDEN)) {
			return result;
		} else {
			System.out.println("*** PresentationPropertyDefinition.createGUIComponent" +
				"(): unexpected visualization mode (\"" + visualization + "\") -- default " +
				"used (\"Input Field\")");
			result[0] = createTextField(actionListener);
			result[1] = result[0];	// ### needed?
			return result;
		}
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	private JComponent createTextField(ActionListener actionListener) {
		JTextField field = new JTextField() /* ### no effect {
			public Dimension getPreferredSize() {
				return new Dimension(0, 20);
			}
		} */ ;
		if (actionListener != null) {
			// a single-line input field triggers an action event
			field.addActionListener(actionListener);
			field.setActionCommand(CMD_SUBMIT_FORM);
		}
		return field;
	}

	private JComponent createPasswordField(ActionListener actionListener) {
		JPasswordField field = new JPasswordField() /* ### no effect {
			public Dimension getPreferredSize() {
				return new Dimension(0, 20);
			}
		} */ ;
		if (actionListener != null) {
			// a single-line input field triggers an action event
			field.addActionListener(actionListener);
			field.setActionCommand(CMD_SUBMIT_FORM);
		}
		return field;
	}

	private JComponent createTextArea() {
		JTextArea area = new JTextArea(50, 0);	// ### 50 rows
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		return area;
	}

	private void createTextEditor(ActionListener actionListener, Object[] result, PropertyPanelControler controler) {
		TextEditorPanel textEditor = new TextEditorPanel(EDITOR_TYPE_STYLED, (GraphPanelControler) controler, true) {
			public Dimension getPreferredSize() {
				return new Dimension(0, 640);	// ### height 640 pixels
			}
		};
		// --- add action button ---
		if (hasActionButton) {
			result[0] = addActionButton(textEditor, actionListener, BoxLayout.Y_AXIS);
		} else {
			result[0] = textEditor;
		}
		result[1] = textEditor;
	}

	private JComponent createOptionMenu(ActionListener actionListener, PropertyPanelControler controler) {
		JComboBox cbox = new JComboBox();
		cbox.setMaximumRowCount(32);
		// go through all options of this property definition
		Enumeration e = options.elements();
		while (e.hasMoreElements()) {
			PresentableTopic option = (PresentableTopic) e.nextElement();
			ImageIcon icon = controler.getIcon(option.getAppearanceParam());
			cbox.addItem(new ComboBoxItem(icon, option.getName(), option.getID()));
		}
		// add renderer
		cbox.setRenderer(new ComboBoxRenderer());
		// add listener
		if (actionListener != null) {
			cbox.addActionListener(actionListener);
			cbox.setActionCommand(PropertyPanel.CMD_TOPIC_DATA_CHANGED_MENU);
		}
		return cbox;
	}

	private JComponent createOptionButtons(ActionListener actionListener,
													ButtonGroup group,
													PropertyPanelControler controler) {
		JPanel panel = new JPanel();
		panel.setBackground(COLOR_PROPERTY_PANEL);
		// --- creating radio buttons ---
		Enumeration e = options.elements();
		PresentableTopic option;
		// ### ImageIcon icon;
		JRadioButton button;
		while (e.hasMoreElements()) {
			option = (PresentableTopic) e.nextElement();
			// ### icon = controler.getIcon(option.getAppearanceParam());
			// create radio button
			button = new JRadioButton(option.getName() /* ###, icon */);
			// add listener
			if (actionListener != null) {
				button.addActionListener(actionListener);
				button.setActionCommand(PropertyPanel.CMD_TOPIC_DATA_CHANGED_BUTTONS);
			}
			//
			panel.add(button);
			group.add(button);
		}
		return panel;
	}

	private JComponent createSwitch(ActionListener actionListener) {
		JCheckBox cbox = new JCheckBox();
		cbox.setBackground(COLOR_PROPERTY_PANEL);
		if (actionListener != null) {
			cbox.addActionListener(actionListener);
			cbox.setActionCommand(PropertyPanel.CMD_TOPIC_DATA_CHANGED_CHECKBOX);
		}
		return cbox;
	}

	private void createDateChooser(ActionListener actionListener, Object[] result) {
		JPanel panel = new JPanel();
		panel.setBackground(COLOR_PROPERTY_PANEL);
		// year menu
		JComboBox yearMenu = new JComboBox();
		yearMenu.setMaximumRowCount(32);
		yearMenu.addItem(VALUE_NOT_SET);
		for (int i = YEAR_MAX; i >= YEAR_MIN; i--) {
			yearMenu.addItem(Integer.toString(i));
		}
		// month menu
		JComboBox monthMenu = new JComboBox();
		monthMenu.setMaximumRowCount(32);
		monthMenu.addItem(VALUE_NOT_SET);
		for (int i = 0; i < 12; i++) {
			monthMenu.addItem(monthNames[i]);
		}
		// day menu
		JComboBox dayMenu = new JComboBox();
		dayMenu.setMaximumRowCount(32);
		dayMenu.addItem(VALUE_NOT_SET);
		for (int i = 1; i <= 31; i++) {
			dayMenu.addItem(Integer.toString(i));
		}
		// add listener
		if (actionListener != null) {
			dayMenu.addActionListener(actionListener);
			dayMenu.setActionCommand(PropertyPanel.CMD_TOPIC_DATA_CHANGED_MENU);
			monthMenu.addActionListener(actionListener);
			monthMenu.setActionCommand(PropertyPanel.CMD_TOPIC_DATA_CHANGED_MENU);
			yearMenu.addActionListener(actionListener);
			yearMenu.setActionCommand(PropertyPanel.CMD_TOPIC_DATA_CHANGED_MENU);
		}
		// view
		panel.add(yearMenu);
		panel.add(monthMenu);
		panel.add(dayMenu);
		// model
		JComboBox[] model = new JComboBox[3];
		model[0] = yearMenu;
		model[1] = monthMenu;
		model[2] = dayMenu;
		//
		result[0] = panel;
		result[1] = model;
	}

	private void createTimeChooser(ActionListener actionListener, Object[] result) {
		JPanel panel = new JPanel();
		panel.setBackground(COLOR_PROPERTY_PANEL);
		// hour menu
		JComboBox hourMenu = new JComboBox();
		hourMenu.setMaximumRowCount(32);
		hourMenu.addItem(VALUE_NOT_SET);
		for (int i = 0; i < 24; i++) {
			hourMenu.addItem(Integer.toString(i));
		}
		// minute menu
		JComboBox minuteMenu = new JComboBox();
		minuteMenu.setMaximumRowCount(32);
		minuteMenu.addItem(VALUE_NOT_SET);
		for (int i = 0; i < 60; i++) {
			minuteMenu.addItem(DeepaMehtaUtils.align(Integer.toString(i)));
		}
		// add listener
		if (actionListener != null) {
			hourMenu.addActionListener(actionListener);
			hourMenu.setActionCommand(PropertyPanel.CMD_TOPIC_DATA_CHANGED_MENU);
			minuteMenu.addActionListener(actionListener);
			minuteMenu.setActionCommand(PropertyPanel.CMD_TOPIC_DATA_CHANGED_MENU);
		}
		// view
		panel.add(hourMenu);
		panel.add(minuteMenu);
		// model
		JComboBox[] model = new JComboBox[3];
		model[0] = hourMenu;
		model[1] = minuteMenu;
		//
		result[0] = panel;
		result[1] = model;
	}

	// ---

	private JPanel addActionButton(JComponent prop, ActionListener actionListener, int layout) {
		JButton button = new JButton(actionButtonLabel);
		button.setActionCommand(actionCommand);
		button.addActionListener(actionListener);
		JPanel p = new JPanel();
		p.setBackground(COLOR_PROPERTY_PANEL);
		p.setLayout(new BoxLayout(p, layout));
		p.add(prop);
		p.add(button);
		return p;
	}
}
