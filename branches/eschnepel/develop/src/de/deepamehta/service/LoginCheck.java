package de.deepamehta.service;



/**
 * ### to be dropped
 */
public interface LoginCheck {

	/**
	 *  Validates the given username and password.
	 *  @param username the cleartext login account
	 *  @param password the cleartext password
	 *  @return true if validation succeeded,
	 *          false otherwise.
	 */
	public boolean loginCheck(String username, String password);
}