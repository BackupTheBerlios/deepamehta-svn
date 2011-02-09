package de.deepamehta.service;

import de.deepamehta.DeepaMehtaException;

/**
 * A class to simplify the retrieval of a DeepaMehta instance - {@link ApplicationService} and
 * {@link CorporateMemory}.
 * 
 * @author Malte Reißig (mre@deepamehta.de)
 * @see ApplicationService
 * @see CorporateMemory
 *
 */
public class PojoApplicationServiceProvider implements ApplicationServiceHost {

	/** The <code>ApplicationService</code> instance. */
	private ApplicationService as;
	/** The <code>CorporateMemory</code> instance. */
	private CorporateMemory cm;

	/** The home location - installation directory - of DeepaMehta. */
	private String home;
	/** The service name identifying the DeepaMehta instance. */
	private String serviceName;

	/** Whether we already have a service instance or not. */
	private boolean running = false;

	/**
	 * Looks up - respectively creates - the requested <code>ApplicationService</code>.
	 *
	 * @throws DeepaMehtaException If instantiating the service fails
	 *
	 * @see PojoApplicationServiceProvider#shutdown()
	 */
	public synchronized void startup() {
		try {
			if (serviceName == null || serviceName.trim().equals("")) {
				throw new DeepaMehtaException("Startup failed: No or empty service name given.");
			}
			ApplicationServiceInstance instance = ApplicationServiceInstance.lookup(
			        serviceName, home != null ? home + "/install/config/dm.properties" : "../config/dm.properties");
			as = ApplicationService.create(this, instance);
			cm = as.cm;
			running = true;
		} catch (Exception e) {
			throw new DeepaMehtaException("Startup failed due to nested errors: " + e);
		}
	}

	/**
	 * Shuts down the previously initialized <code>ApplicationService</code>.
	 *
	 * If there is no instance, the method will simply return.
	 *
	 * @throws DeepaMehtaException If shutting down the service fails
	 *
	 * @see PojoApplicationServiceProvider#startup()
	 */
	public synchronized void shutdown() {
		if (!running) {
			return;
		}
		try {
			as.shutdown();
			running = false;
		} catch (Exception e) {
			throw new DeepaMehtaException("Shutdown failed due to nested errors: " + e);
		}
	}

	public void broadcastChangeNotification(String topicID) {
		// TODO Auto-generated method stub
	}

	public String getCommInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendDirectives(Session session, CorporateDirectives directives, ApplicationService as, String topicmapID,
			String viewmode) {
		// TODO Auto-generated method stub
	}

	/**
	 * Returns the home location - installation directory - of DeepaMehta.
	 *
	 * @return The home location od DeepaMehta
	 */
	public String getHome() {
		return home;
	}

	/**
	 * Sets the home location - installation directory - of DeepaMehta.
	 *
	 * @param home The home location of DeepaMehta
	 */
	public void setHome(String home) {
		this.home = home;
	}

	/**
	 * Returns the service name - the name of the DeepaMetha instance.
	 *
	 * @return The service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Sets the service name - the name of the DeepaMetha instance.
	 *
	 * This will decide which instance of DeepaMehta will be looked up.
	 *
	 * @param serviceName The service name
	 *
	 * @see ApplicationServiceInstance#lookup(String, String)
	 */
	public void setServiceName(String service) {
		this.serviceName = service;
	}

	/**
	 * Returns the instantiated <code>ApplicationService</code> or throws an Exception.
	 *
	 * If the service hasn't been instantiated properly a DeepaMehtaException will be thrown.
	 *
	 * @return The application service instance
	 *
	 * @throws DeepaMehtaException If th service hasn't been instantiated properly
	 *
	 * @see ApplicationService
	 */
	public ApplicationService getApplicationService() {
		if (!running) {
			throw new DeepaMehtaException("The application service is not running!");
		}
		return as;
	}

	/**
	 * Returns the instantiated <code>CorporateMemory</code> or throws an Exception.
	 *
	 * If the service, and so the corporate memory hasn't been instantiated properly
	 * a DeepaMehtaException will be thrown.
	 *
	 * @return The corporate memory instance
	 *
	 * @see CorporateMemory
	 */
	public CorporateMemory getCorporateMemory() {
		if (!running) {
			throw new DeepaMehtaException("The application service is not running!");
		}
		return cm;
	}

}