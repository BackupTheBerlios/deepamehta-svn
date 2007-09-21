package de.deepamehta;

import java.util.Enumeration;
import java.util.Vector;



/**
 * A <CODE>Directives</CODE> object contains single directives the client is instructed
 * to process.
 * <P>
 * It is always the DeepaMehta Server who sends directives to the client. Sending directives to
 * the client means, instruct it to display some information or perform some action. Mostly directives are send synchronously
 * (as answer to a request), however some directives may be send asynchronously (push). Furthermore some
 * directives are so called <I>chained directives</I>. Processing a chained directive will issue
 * further requests.
 * <P>
 * The existing directives are listed in the table below. For every directive its parameters are listed, furthermore it is
 * indicated weather the directive is "chained" resp. the directive can be send asynchronously.
 *
 * <H4>Note to application programmers</H4>
 *
 * This is a crucial class for application programmers.
 * <P>
 * <HR>
 * Last functional change: 5.6.2002 (2.0a15-pre5)<BR>
 * Last documentation update: 17.12.2001 (2.0a14-pre5)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class Directives implements DeepaMehtaConstants {



	// **************
	// *** Fields ***
	// **************



	public Vector directives = new Vector();

	/**
	 * A flag indicating this directives are chained
	 * <P>
	 * [### explain]
	 */
	protected boolean isChainedResult;

	protected int chainedResultType;



	// *******************
	// *** Constructor ***
	// *******************


	
	public Directives() {
	}



	// ***************
	// *** Methods ***
	// ***************



	public int getDirectiveCount() {
		return directives.size();
	}

	public Enumeration getDirectives() {
		return directives.elements();
	}

	// ---

	/**
	 * @see		de.deepamehta.client.PresentationTopicMap#performTopicAction
	 * @see		de.deepamehta.service.InteractionConnection#performPerformTopicAction
	 */
	public boolean isChained() {
		return isChainedResult;
	}

	/**
	 * @see		de.deepamehta.service.InteractionConnection#performPerformTopicAction
	 */
	public int chainedResultType() {
		return chainedResultType;
	}

	/**
	 * @see		#add
	 * @see		de.deepamehta.client.PresentationDirectives#PresentationDirectives
	 */
	protected void setChained(int chainedResultType) {
		// error check
		if (isChainedResult) {
			System.out.println("*** Directives.setChained(): this directives are " +
				"already chained (chained directive type " + this.chainedResultType + ")" +
				" -- no further chained directive added (type " + chainedResultType + ")");
			return;
		}
		//
		this.isChainedResult = true;
		this.chainedResultType = chainedResultType;
	}
}
