package de.deepamehta.topics.helper;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;



/**
 * Strips off the startDocument() and endDocument() events
 * to enable nesting of SAX events from different sources.
 */
public class EmbeddedContentHandlerProxy extends ContentHandlerProxy {



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * References checked: 19.10.2003 (2.0b2)
	 *
	 * @see		TopicMapExporter#exportProperties
	 * @see		SVGInsertContentHandlerProxy#processingInstruction
	 */
	public EmbeddedContentHandlerProxy(ContentHandler handler) {
		super(handler);
	}



	// ***************
	// *** Methods ***
	// ***************



	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}
}
