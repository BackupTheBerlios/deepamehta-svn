package de.deepamehta.client;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;



class ComboBoxRenderer extends JLabel implements ListCellRenderer {

	ComboBoxRenderer() {
		setOpaque(true);	// otherwise the background isn't painted
	}

	public Component getListCellRendererComponent(JList list, Object value, int index,
										boolean isSelected, boolean cellHasFocus) {
		ComboBoxItem item = (ComboBoxItem) value;
		/* ### System.out.println(">>> menu item \"" + icon.getDescription() + "\" is " +
			"rendered, selected: " + isSelected + ", background: " + (isSelected ? list.getSelectionBackground() :
			list.getBackground())); */
		if (item == null) {
			setText("null ?!?!");
			setIcon(null);
		} else {
			setText(item.text);
			setIcon(item.icon);
		}
		setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
		return this;
	}
}
