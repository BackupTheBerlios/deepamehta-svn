package de.deepamehta.service.web;



/**
 * <P>
 * <HR>
 * Last sourcecode change: 20.10.2002 (2.0a16)<BR>
 * Last documentation update: 20.10.2002 (2.0a16)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class Action {

	public String action;
	public String iconfile;
	public String name;

	public Action(String action, String iconfile) {
		this(action, iconfile, null);
	}

	public Action(String action, String iconfile, String name) {
		this.action = action;
		this.iconfile = iconfile;
		this.name = name;
	}
}
