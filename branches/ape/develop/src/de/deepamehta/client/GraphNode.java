package de.deepamehta.client;

import java.awt.Image;
import java.awt.Point;
import java.util.Enumeration;



/**
 * <P>
 * <HR>
 * Last functional change: 5.7.2001 (2.0a11-pre8)<BR>
 * Last documentation update: 21.4.2001 (2.0a10-pre6)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public interface GraphNode {

	String getID();
	String getName();
	String getType();
	
	Point getGeometry();
	int getAppearanceMode();
	String getAppearanceParam();
	String getLabel();

	Image getImage();
	int getImageSize();

	void setGeometry(Point p);

	void addEdge(GraphEdge edge);
	void removeEdge(GraphEdge edge);
	Enumeration getEdges();

	GraphNode relatedNode(GraphEdge edge);
}
