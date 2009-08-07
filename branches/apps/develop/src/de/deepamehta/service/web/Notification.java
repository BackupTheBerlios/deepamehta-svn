package de.deepamehta.service.web;

import java.io.Serializable;



/**
 * <p>
 * <hr>
 * Last change: 30.6.2008 (2.0b8)<br>
 * J&ouml;rg Richter<br>
 * jri@deepamehta.de
 */
public class Notification implements Serializable {

	public int type;
	public String message;

	public Notification(String message, int type) {
		this.type = type;
		this.message = message;
	}
}
