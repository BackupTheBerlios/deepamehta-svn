package de.deepamehta.topics.helper;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;



/**
 * Base helper class to modify the behaviour of a ContentHandler.
 * ### rather: wraps a ContentHandler and delegates SAX events to it.
 */
public abstract class ContentHandlerProxy implements ContentHandler {

	protected ContentHandler handler;

	public ContentHandlerProxy(ContentHandler handler) {
		this.handler = handler;
	}

	public void startDocument() throws SAXException {
		handler.startDocument();
	}

	public void endDocument() throws SAXException {
		handler.endDocument();
	}

	public void startElement(String namespaceURI, String localName,
						String qName, Attributes atts) throws SAXException {
		handler.startElement(namespaceURI, localName, qName, atts);
	}

	public void endElement(String namespaceURI, String localName, String qName)
														throws SAXException {
		handler.endElement(namespaceURI, localName, qName);
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException {
		handler.startPrefixMapping(prefix, uri);
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		handler.endPrefixMapping(prefix);
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		handler.characters(ch, start, length);
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
														throws SAXException {
		handler.ignorableWhitespace(ch, start, length);
	}

	public void processingInstruction(String target, String data)
														throws SAXException {
		handler.processingInstruction(target, data);
	}

	public void setDocumentLocator(Locator locator) {
		handler.setDocumentLocator(locator);
	}

	public void skippedEntity(String name) throws SAXException {
		handler.skippedEntity(name);
	}
}