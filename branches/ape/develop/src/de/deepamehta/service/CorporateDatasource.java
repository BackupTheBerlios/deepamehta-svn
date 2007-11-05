package de.deepamehta.service;

import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;



/**
 * This interface abstracts a corporate datasource (e.g. a database existing in a company).
 * <P>
 * The abstraction is as follows: A <CODE>CorporateDatasource</CODE> contains <I>Elements</I>.
 * Elements are typed and have a unique ID (unique between all elements of the respective type).
 * A <CODE>CorporateDatasource</CODE> can contain elements of different types.
 * Elements consists of <I>Attributes</I>. An attribute consists of a name and a value.
 * <P>
 * The elements of a <CODE>CorporateDatasource</CODE> are accessible by the active kernel topics
 * {@link de.deepamehta.topics.DataConsumerTopic} and {@link de.deepamehta.topics.ElementContainerTopic}.
 * <P>
 * Currently there are <CODE>CorporateDatasource</CODE> implementations for SQL-databases
 * ({@link CorporateSQLSource}), XML-files ({@link CorporateXMLSource}) and directory services
 * ({@link CorporateLDAPSource}).
 * <P>
 * <HR>
 * Last functional change: 3.2.2003 (2.0a18-pre1)<BR>
 * Last documentation update: 17.12.2000 (2.0a8-pre6)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface CorporateDatasource {

	// --- queryElements (3 forms) ---

	Vector queryElements(String elementType, String attrName, String attrValue, boolean caseSensitiv) throws Exception;
	Vector queryElements(String elementType, Hashtable attributes, boolean caseSensitiv) throws Exception;
	Vector queryElements(String elementType, String id, String relatedElementType, String helperElementType) throws Exception;

	// --- getElementCount (3 forms) ---

	int getElementCount(String elementType) throws Exception;
	int getElementCount(String elementType, Hashtable attributes, boolean caseSensitiv) throws Exception;
	int getElementCount(String elementType, String id, String relatedElementType, String helperElementType) throws Exception;

	// ---

	Hashtable queryElement(String elementType, String id) throws Exception;

	// ---

	/**
	 * @param		elementType		The element type to query
	 * @param		query			The query specifying the set of element as base for grouping
	 * @param		groupingAttr	The grouping attribute (e.g. <CODE>"Year"</CODE>)
	 *
	 * @return		vector of 2-element <CODE>String</CODE>-arrays<BR>
	 *				element 1: the grouping value (e.g. <CODE>"1994"</CODE>)<BR>
	 *				element 2: number of elements in that group (<CODE>"18"</CODE>)
	 */
	Vector queryGroups(String elementType, Hashtable query, String groupingAttr) throws Exception;
}
