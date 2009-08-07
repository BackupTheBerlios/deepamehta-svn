package de.deepamehta.client;



/**
 *
 */
public interface GraphEdge {

	String getID();
	String getType();
	String getName();
	GraphNode getNode1();
	GraphNode getNode2();
}
