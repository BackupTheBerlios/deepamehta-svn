package de.deepamehta;



/**
 * Describes a simplified form of a Topic
 * according to the ISO 13250 Topic Maps standard.
 */
public interface Topic {

	public String getID();
	public String getType();
	public String getName();

	public void setID(String id);
	public void setType(String type);
	public void setName(String name);
}
