package de.rpc.deepamehta;

import de.deepamehta.DeepaMehtaConstants;



/**
 * DeepaMehta JSON RPC Web Service <br>
 * Requires DeepaMehta rev. 371 at branches /apps.
 * <p>
 * Last change: 21.9.2009<br>
 * Malte Rei&szlig<br>
 * mre@deepamehta.de
 */
public interface WebService extends DeepaMehtaConstants {

    // pages
    static final String PAGE_PRINT = "Print"; // session var is "html"
    // types
    static final String TOPICTYPE_POST = "tt-post"; // ### depends on patch currentoy t-12312
    static final String TOPICTYPE_COMMENT = "tt-comment"; // ### depends on patch currentoy t-12312
    // properties of a message
    static final String PROPERTY_MESSAGE_NAME = "Name";
    static final String PROPERTY_MESSAGE_CONTENT = "Description";
    static final String PROPERTY_MESSAGE_TIME = "Time";
    static final String PROPERTY_MESSAGE_AUTHOR = "Time";
    // properties of a comment
    static final String PROPERTY_COMMENT_AUTHOR = "Author"; // session var is
    static final String PROPERTY_COMMENT_DATE = "Date"; // session var is
    static final String PROPERTY_COMMENT_TIME = "Time"; // session var is

}
