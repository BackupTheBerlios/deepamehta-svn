package de.deepamehta;



/**
 * Describes a simplified form of an Association
 * according to the ISO 13250 Topic Maps standard.
 * <P>
 * <HR>
 * Last functional change: 17.2.2003 (2.0a18-pre2)<BR>
 * Last documentation update: 17.2.2003 (2.0a18-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface Association {

	public String getID();
	public String getType();
	public String getName();
	public String getTopicID1();
	public String getTopicID2();

	public void setID(String id);
	public void setType(String type);
	public void setName(String name);
	public void setTopicID1(String id);
	public void setTopicID2(String id);
}
