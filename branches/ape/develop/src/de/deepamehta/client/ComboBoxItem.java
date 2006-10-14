package de.deepamehta.client;

import javax.swing.Icon;



class ComboBoxItem {

	Icon icon;
	String text;
	String topicID;

	ComboBoxItem(Icon icon, String text, String topicID) {
		this.icon = icon;
		this.text = text;
		this.topicID = topicID;
	}
}
