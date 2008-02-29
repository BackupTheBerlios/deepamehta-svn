package de.deepamehta.service;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.xml.parsers.NonValidatingDOMParser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;



/**
 * A {@link CorporateDatasource} implementation for XML data.
 * <P>
 * The XML parsing is performed by means of IBM's xml4j parser.
 * <P>
 * <HR>
 * Last functional update: 1.2.2003 (2.0a18-pre1)<BR>
 * Last documentation update: 6.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class CorporateXMLSource implements CorporateDatasource {



	// *************
	// *** Field ***
	// *************



	// key: entityname (String)
	// value: Hashtable
	//		key: id
	//		value: Hashtable
	//			key: fieldname
	//			value: value
	private Hashtable storeData = new Hashtable();



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		de.deepamehta.topics.DataSourceTopic#openCorporateDatasource
	 */
	public CorporateXMLSource(String xmlFile, String elementNames) throws SAXException, IOException {
		System.out.println("> CorporateXMLSource(): \"" + xmlFile + "\"");
		NonValidatingDOMParser parser = new NonValidatingDOMParser();
		parser.parse(new InputSource(new FileInputStream(xmlFile)));
		Document doc = parser.getDocument();
		Element docElement = doc.getDocumentElement();
		Element element; // = (Element) docElement.getFirstChild();
		// String elementType = element.getTagName();
		StringTokenizer st = new StringTokenizer(elementNames, ", ");
		NodeList elements;
		String elementName;
		Hashtable elementData;
		while (st.hasMoreTokens()) {
			elementName = st.nextToken();
			elements = docElement.getElementsByTagName(elementName);
			elementData = new Hashtable();
			storeData.put(elementName, elementData);
			System.out.println("> Store <" + docElement.getTagName() + "> contains " + elements.getLength() + " <" + elementName + "> elements");
			for (int n = 0; n < elements.getLength(); n++) {
				element = (Element) elements.item(n);
				storeElement(elementData, element);
			}
		}
	}



	// *********************************************************************
	// *** Implementation of interface de.deepamehta.CorporateDatasource ***
	// *********************************************************************



	// --- queryElements (3 forms) ---

	public Vector queryElements(String elementName, Hashtable conditions, boolean caseSensitiv /*, TypeTopic typeDef*/) {
		Vector result = new Vector();
		Hashtable elementData = getElementData(elementName);
		/* if (elementData == null) {
			System.out.println("*** CorporateXMLSource.queryElements(): entity \"" + elementName + "\" not known -- no result");
			return result;
		} */
		Enumeration e = elementData.elements();
		Hashtable data;
		try {
			while (e.hasMoreElements()) {
				data = (Hashtable) e.nextElement();
				if (matches(data, conditions, caseSensitiv)) {
					// add element to the result
					result.addElement(data);
				}
			}
		} catch (IllegalArgumentException e2) {
			System.out.println("*** CorporateXMLSource.queryElements(): entity \"" + elementName + "\": " + e2.getMessage() + " -- no result");
		}
		return result;
	}

	public Vector queryElements(String elementName, String fieldName, String fieldValue, boolean caseSensitiv) {
		Vector result = new Vector();
		Hashtable elementData = getElementData(elementName);
		/* if (elementData == null) {
			System.out.println("*** CorporateXMLSource.queryElements(): entity \"" + elementName + "\" not known -- no result");
			return result;
		} */
		Enumeration e = elementData.elements();
		Hashtable data;
		String value;
		while (e.hasMoreElements()) {
			data = (Hashtable) e.nextElement();
			value = (String) data.get(fieldName);
			if (value == null) {
				System.out.println("*** CorporateXMLSource.queryElements(): field \"" + fieldName + "\" not part of entity \"" + elementName + "\"");
				continue;
			}
			if (matches(value, fieldValue, caseSensitiv)) {
				result.addElement(data);
			}
		}
		return result;
	}

	public Vector queryElements(String elementType, String id, String relatedElementType, String helperElementType) {
		return new Vector();	// ### not yet implemented
	}

	// ---

	public Hashtable queryElement(String elementName, String id) {
		return (Hashtable) getElementData(elementName).get(id);
	}
/*
	public String queryElement(String elementName, String id, String fieldName) {
		return (String) queryElement(elementName, id).get(fieldName);
	}
*/
	// Called from
	//		- ContainerTopic.autoSearch()
	public Vector queryGroups(String elementName, Hashtable conditions, String groupingField) {
		Vector grouping = new Vector();
		int[] itemCount = new int[7];	// #
		Enumeration e = queryElements(elementName, conditions, false).elements();
		Hashtable data;
		String item;
		int index;
		boolean overflow = false;
		while (e.hasMoreElements()) {
			data = (Hashtable) e.nextElement();
			item = (String) data.get(groupingField);
			if (item == null) {
				continue;
			}
			index = grouping.indexOf(item);
			if (index == -1) {
				index = grouping.size();
				if (index < 7) {		// #
					grouping.addElement(item);
					itemCount[index]++;
				} else {
					overflow = true;
				}
			} else {
				itemCount[index]++;
			}
		}
		if (overflow) {
			System.out.println("* CorporateXMLSource.queryGroups(): there are more than 7 \"" + groupingField + "\" groups -- only 7 groups returned");	// #
		}
		// build result vector
		Vector result = new Vector();
		int groups = grouping.size();
		String[] group;
		for (int i = 0; i < groups; i++) {
			group = new String[2];
			group[0] = (String) grouping.elementAt(i);
			group[1] = Integer.toString(itemCount[i]);
			result.addElement(group);
		}
		return result;
	}

	public int getElementCount(String type) {
		return getElementData(type).size();
	}

	public int getElementCount(String elementType, Hashtable attributes, boolean caseSensitiv) throws Exception {
		return 0;	// ### not yet implemented	
	}

	public int getElementCount(String elementType, String id, String relatedElementType, String helperElementType) throws Exception {
		return 0;	// ### not yet implemented	
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	private Hashtable getElementData(String elementName) {
		return (Hashtable) storeData.get(elementName);
	}

	private void storeElement(Hashtable elementData, Element element) {
		String tagname;
		Node node;
		String value;
		String id = element.getAttribute("ID");
		if (id.equals("")) {
			System.out.println("*** CorporateXMLSource.storeElement(): element is missing \"ID\" attribute -- not stored");
			return;
		}
		Hashtable topicData = new Hashtable();
		topicData.put("ID", id);
		element = (Element) element.getFirstChild();
		while (element != null) {
			tagname = element.getTagName();
			node = element.getFirstChild();
			value = node != null ? node.getNodeValue() : "";
			topicData.put(tagname, value);
			element = (Element) element.getNextSibling();
		}
		elementData.put(id, topicData);
	}

	private boolean matches(Hashtable data, Hashtable queryData, boolean caseSensitiv) throws IllegalArgumentException {
		// System.out.println("CorporateXMLSource.matches(): " + data);
		Enumeration e = queryData.keys();
		String fieldname;
		String value;
		String compValue;
		boolean matches = true;
		// go through all fields of the query form
		while (e.hasMoreElements()) {
			fieldname = (String) e.nextElement();
			value = (String) queryData.get(fieldname);
			// only consider filled fields
			if (!value.equals("")) {
				// if (!value.equals(data.get(fieldname))) {
				compValue = (String) data.get(fieldname);
				if (compValue == null) {
					throw new IllegalArgumentException("field \"" + fieldname + "\" unknown");
				}
				if (!matches(compValue, value, caseSensitiv)) {
					matches = false;
					break;
				}
			}
		}
		return matches;
	}

	private boolean matches(String str1, String needle, boolean caseSensitiv) {
		if (caseSensitiv) {
			return str1.equals(needle);
		} else {
			str1 = str1.toLowerCase();
			return str1.indexOf(needle.toLowerCase()) != -1;
		}
	} 
}
