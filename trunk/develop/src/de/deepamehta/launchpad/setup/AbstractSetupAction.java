package de.deepamehta.launchpad.setup;

import java.util.ArrayList;

import de.deepamehta.environment.Environment;
import de.deepamehta.environment.instance.InstanceConfiguration;

public abstract class AbstractSetupAction implements SetupAction {

    protected Environment env;
    protected InstanceConfiguration config;
    protected ArrayList messages;

	/**
	 * Default constructor
	 * @param conf
	 */
	public AbstractSetupAction(InstanceConfiguration conf) {
		this.env = Environment.getEnvironment();
		this.config = conf;
		this.messages = new ArrayList();
	}
    
	public String[] getErrorMessage() {
		return (String[]) this.messages.toArray(new String [0]);
	}
	
	protected void addErrorMessage(String msg) {
		this.messages.add(msg);
	}
	
	protected void addErrorMessage(Throwable err) {
		Throwable curr = err;
		while (curr != null) {
			addErrorMessage(curr.toString());
			curr = curr.getCause();
		}
	}
	
	protected void addErrorMessage(String msg, Throwable err) {
		addErrorMessage(msg);
		addErrorMessage(err);
	}

}
