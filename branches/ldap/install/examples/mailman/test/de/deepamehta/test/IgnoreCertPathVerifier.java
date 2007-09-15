/**
 * 
 */
package de.deepamehta.test;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author danny
 *
 */
public class IgnoreCertPathVerifier implements HostnameVerifier {

	/* (non-Javadoc)
	 * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String, javax.net.ssl.SSLSession)
	 */
	public boolean verify(String hostname, SSLSession session) {
		System.out.println("IgnoreCertPathVerifier asked :D");
		return true;
	}

}
