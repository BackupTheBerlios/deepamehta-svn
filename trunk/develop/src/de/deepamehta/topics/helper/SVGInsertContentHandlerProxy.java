package de.deepamehta.topics.helper;

import de.deepamehta.topics.TopicMapTopic;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.xml.transform.sax.SAXResult;



/**
 * Looks for the following XML processing instruction:<br>
 * &lt;?insert svg?&gt;<br>
 * If this instruction is found, the SVG graphic will be inserted.
 */
public class SVGInsertContentHandlerProxy extends ContentHandlerProxy {

	private TopicMapTopic tmtopic;
	private String svgStylesheetName;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * References checked: 19.10.2003 (2.0b2)
	 *
	 * @param	svgStylesheetName	the name of the SVG stylesheet
	 *
	 * @see		TopicMapExporter#createPDFFile
	 */
	public SVGInsertContentHandlerProxy(TopicMapTopic tmtopic, String svgStylesheetName, ContentHandler handler) {
		super(handler);
		this.tmtopic = tmtopic;
		this.svgStylesheetName = svgStylesheetName;
	}



	// ***************
	// *** Methods ***
	// ***************


	public void processingInstruction(String target, String data) throws SAXException {
		if ("insert".equals(target) && "svg".equals(data)) {
			TopicMapExporter.transformTopicmap(tmtopic, svgStylesheetName,
				new SAXResult(new EmbeddedContentHandlerProxy(handler)));
		}
	}
}
