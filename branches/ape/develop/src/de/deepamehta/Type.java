package de.deepamehta;

import java.util.Vector;



/**
 * The Type interface is implemented by classes who represents Topic Types resp.
 * Association Types.
 * <P>
 * <HR>
 * Last functional change: 7.4.2003 (2.0a18-pre9)<BR>
 * Last documentation update: 1.8.2001 (2.0a11)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface Type {

	public String getID();
	public String getName();
	public int getVersion();

	/**
	 * Vector of ({@link PropertyDefinition})
	 */
	public Vector getTypeDefinition();

	public boolean isSearchType();
}
