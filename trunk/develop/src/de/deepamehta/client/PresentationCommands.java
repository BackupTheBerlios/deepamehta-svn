package de.deepamehta.client;

import de.deepamehta.Commands;
import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;
import de.deepamehta.Directives;

import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;



/**
 * <P>
 * <HR>
 * Last functional change: 26.12.2001 (2.0a14-pre5)<BR>
 * Last documentation update: 26.4.2001 (2.0a10-pre6)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
class PresentationCommands extends Commands implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	private PresentationService ps;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		PresentationDirectives#PresentationDirectives(Directives, PresentationService)
	 */
	PresentationCommands(Commands commands, PresentationService ps) {
		super(commands);
		this.ps = ps;
	}

	/**
	 * @see		PresentationDirectives#PresentationDirectives(DataInputStream, PresentationService)
	 */
	PresentationCommands(DataInputStream in, PresentationService ps) throws IOException {
		super(in);
		this.ps = ps;
	}



	// **************
	// *** Method ***
	// **************



	JPopupMenu popupMenu(ActionListener listener, String menuID) {
		JPopupMenu menu = new JPopupMenu(menuID);
		ArrayList items = new ArrayList(); 
		Enumeration e = groupCommands.elements();
		Commands command;
		while (e.hasMoreElements()) {
			command = (Commands) e.nextElement();
			JComponent mi = menuItem(command, listener, menuID);
			items.add(mi);
		}
		// let scroll helper do the work
		MenuScrollHelper.plug(menu, items);
		return menu;
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 * @param	menuID	used internally only.
	 *
	 * @throws	DeepaMehtaException	if the specified command has an unexpected type.
	 *			This could be caused by an out-of-sync communication link.
	 *
	 * @see		#popupMenu	(package)
	 * @see		#menu		(private)
	 */
	private JComponent menuItem(Commands command, ActionListener listener, String menuID)
															throws DeepaMehtaException {
		JMenuItem item;
		switch (command.type) {
		case TYPE_COMMAND:
			Icon icon = ps.getIcon(command.iconpath, command.iconfile);
			if ((command.state & COMMAND_STATE_RADIOBUTTON) != 0) {
				item = new JRadioButtonMenuItem(command.label, icon);
			} else if ((command.state & COMMAND_STATE_CHECKBOX) != 0) {
				item = new JCheckBoxMenuItem(command.label, icon);
			} else {
				item = new JMenuItem(command.label, icon);
			}
			item.setSelected((command.state & COMMAND_STATE_SELECTED) != 0);
			item.setEnabled((command.state & COMMAND_STATE_DISABLED) == 0);
			item.addActionListener(listener);
			item.setActionCommand(command.command);
			return item;
		case TYPE_COMMAND_SEPARATOR:
			return new JSeparator();
		case TYPE_COMMAND_GROUP:
			return menu(command, listener, menuID);
		default:
			throw new DeepaMehtaException("unexpected command type: " + command.type +
				" -- probably the communication link is out-of-sync");
		}
	}

	/**
	 * Returns a submenu (called for TYPE_COMMAND_GROUP)
	 *
	 * @see		#menuItem
	 */
	private JMenu menu(Commands commands, ActionListener listener, String menuID) {
		JMenu menu = new JMenu(commands.label);
		ArrayList items = new ArrayList(); 
		menu.setIcon(ps.getIcon(commands.iconpath, commands.iconfile));
		menu.getPopupMenu().setLabel(menuID);
		if (commands.isEmpty()) {
			menu.setEnabled(false);
		} else {
			Enumeration e = commands.groupCommands.elements();
			Commands command;
			while (e.hasMoreElements()) {
				command = (Commands) e.nextElement();
				JComponent mi = menuItem(command, listener, menuID);
				mi.setName(command.label);
				items.add(mi); // add to virtual menulist
			}
			MenuScrollHelper.plug(menu.getPopupMenu(), items);
		}
		return menu;
	}
}
