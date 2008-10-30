package de.deepamehta.service;

import java.util.Vector;



/**
 * <p>
 * <hr>
 * Last sourcecode change: 29.10.2008 (2.0b8)<br>
 * Last documentation update: 14.10.2007 (2.0b8)<br>
 * J&ouml;rg Richter / Malte Rei&szlig;ig<br>
 * jri@deepamehta.de / mre@deepamehta.de
 */
public class TopicBeanField {

	public static final int TYPE_SINGLE = 0;
	public static final int TYPE_MULTI = 1;

	public int type;
	public String label, name, visualMode;
	public String value;	// used for TYPE_SINGLE. Must not be null.
	public Vector values;	// used for TYPE_MULTI. Must not be null.

	TopicBeanField(String name, String label, String visualMode, String value) {
		type = TYPE_SINGLE;
		this.name = name;
		this.value = value;
		this.visualMode = visualMode;
		this.label = label;
	}

	TopicBeanField(String name, String label, Vector values) {
		type = TYPE_MULTI;
		this.name = name;
		this.values = values;
		this.label = label;
	}
}
