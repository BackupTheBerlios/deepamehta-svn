/**
 * 
 */
package de.deepamehta.topics.example;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.deepamehta.service.Session;
import de.deepamehta.service.web.DeepaMehtaServlet;
import de.deepamehta.service.web.RequestParameter;

/**
 * MISSDOC No documentation for type CounterServlet
 * @author vwegert
 *
 */
public class CounterServlet extends DeepaMehtaServlet {

	private static Log logger = LogFactory.getLog(CounterServlet.class);
	
	private String counterID;
	private String counterName;
	private CounterTopic counter;
	
	/* (non-Javadoc)
	 * @see de.deepamehta.service.web.DeepaMehtaServlet#init()
	 */
	public void init() {
		super.init();
		this.counterID = sc.getInitParameter("counter");
		if (this.counterID == null) this.counterID = "t-ex-webcounter";
		this.counter = (CounterTopic) as.getLiveTopic(this.counterID, 1);
		this.counterName = counter.getName();
		logger.info("Initialized counter servlet for counter " + counterName + " (" + counterID + ").");
	}

	/* (non-Javadoc)
	 * @see de.deepamehta.service.web.DeepaMehtaServlet#performAction(java.lang.String, de.deepamehta.service.web.RequestParameter, de.deepamehta.service.Session)
	 */
	protected String performAction(String action, RequestParameter params, Session session) throws ServletException {
		session.setAttribute("counter", this.counter);
		session.setAttribute("counterName", this.counterName);
		return "WebCounter";
	}

}
