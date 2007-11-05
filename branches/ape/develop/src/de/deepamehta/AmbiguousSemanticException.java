package de.deepamehta;



/**
 * Last functional change: 4.9.2001 (2.0a12-pre1)<BR>
 * Last documentation update: 4.9.2001 (2.0a12-pre1)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class AmbiguousSemanticException extends RuntimeException {

	BaseTopic defaultTopic;
	BaseAssociation defaultAssociation;

    /**
     * Constructs an <CODE>AmbiguousSemanticException</CODE> with the specified 
     * detail message. 
     *
     * @param   s   the detail message
     */
    public AmbiguousSemanticException(String s, BaseTopic defaultTopic) {
		super(s);
		this.defaultTopic = defaultTopic;
    }

    public AmbiguousSemanticException(String s, BaseAssociation defaultAssoc) {
		super(s);
		this.defaultAssociation = defaultAssoc;
    }

	public BaseTopic getDefaultTopic() {
		return defaultTopic;
	}

	public BaseAssociation getDefaultAssociation() {
		return defaultAssociation;
	}
}
