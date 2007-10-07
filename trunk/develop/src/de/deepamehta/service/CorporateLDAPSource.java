package de.deepamehta.service;

import de.deepamehta.DeepaMehtaConstants;
import de.deepamehta.DeepaMehtaException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;



/** 
 * An Implementation of CorporateDataSource for access to LDAP directory services.
 */
public class CorporateLDAPSource implements CorporateDatasource, DeepaMehtaConstants {

	private DirContext initial;

	/**
	 *  A directory context to work with.
	 */
	private DirContext ctx;

	/**
	 *  Used to determine whether object search should be
	 *  performed in onelevel scope or subtree scope.
	 */
	private SearchControls controls;

	/**
	 *  The URL connect String.
	 */
	private String url;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 *  @param url a valid LDAP URL of the form <CODE>
     *             ldap://host:port?login=name&password=pwd&baseDN=bdn&searchScope=scp
	 *             </CODE>, where:
     *                   host is either an IP address or a DNS hostname, 
	 *                   port is usually 389 or 636 (SSL-connection),
	 *                   name is a valid login name on the LDAP system,
	 *                   pwd is the password according to the login name,
	 *                   bdn is the Base DN (only elements below this DN will be found), and
	 *                   scp is the search scope. This may be either "ONELEVEL_SCOPE" or "SUBTREE_SCOPE".
	 *                                            In the first case, only the current directory will be searched, 
	 *                                            otherwise subdirectories will be searched as well.
	 *  For instance:
	 *  ldap://192.168.251.145:389?login=olaf@staps2000.local&password=olaf&baseDN=DC=staps2000,DC=local&searchScope=SUBTREE_SCOPE
	 */
	public CorporateLDAPSource(String url) throws Exception {	
		this.url = url;
		reconnect();
	}



	// *********************************************************************
	// *** Implementation of interface de.deepamehta.CorporateDatasource ***
	// *********************************************************************



	// --- queryElements (3 forms) ---

	/**
	 *  Retrieves Elements belonging to a certain type and
	 *  fulfilling a set of conditions.
	 *
	 *  Note that the caseSensitiv parameter isn't used,
	 *  since the definition whether attribute identification
	 *  is case-sensitive or not is part of the LDAP attribute definition.
	 *
	 *  @param type maps to the value of the objectClass attribute
	 *  @param conditions Map of (name, value) attribute pairs
	 *  @param caseSensitiv unused (see above)
	 */
	public Vector queryElements(String type, Hashtable conditions, boolean caseSensitiv) throws Exception {
		String filter = createFilter(conditions);
		filter += createExactFilter("objectClass", type);
		return performSearch(conjunctFilters(filter));
	}

	/**
	 *  Retrieves Elements belonging to a certain type and
	 *  fulfilling a certain "fieldName=fieldValue" condition.
	 *  
	 *  Note that the caseSensitiv parameter isn't used,
	 *  since the definition whether attribute identification
	 *  is case-sensitive or not is part of the LDAP attribute definition.
	 *
	 *  @param type maps to the value of the objectClass attribute
	 *  @param fieldName name of a single attribute
	 *  @param fieldValue attribute value
	 *  @param caseSensitiv unused (see above)
	 */
	public Vector queryElements(String type, String fieldName, String fieldValue, boolean caseSensitiv) throws Exception {
		String filter = createFilter(fieldName, fieldValue);
		filter += createExactFilter("objectClass", type);
		return performSearch(conjunctFilters(filter));
	}

	public Vector queryElements(String elementType, String id, String relatedElementType, String helperElementType) {
		return new Vector();	// ### not yet implemented
	}

	// ---

	/** 
	 *  Retrieves one Element by its identifier.
	 *
	 *  @param type maps to the value of the objectClass attribute
	 *  @param id maps to the DN of this element.
	 */
	public Hashtable queryElement(String type, String id) throws Exception {	
		DirContext ctx = (DirContext) createContext(initial).lookup(id);
		return transferAttributes(ctx);
	}

	/**
	 * Categorizes elements according to the values of a certain grouping attribute.
	 *
	 * @param type			maps to the value of the objectClass attribute
	 * param conditions     Map of attribute name - attribute value pairs
	 * @param groupingField	The grouping attribute (e.g. "Year")
	 *
	 * @return vector of 2-element String-arrays
	 *         element 1: the grouping value (e.g. "1994")
	 *	       element 2: number of elements in that group ("18")
	 */
	public Vector queryGroups(String type, Hashtable conditions, String groupingField) throws Exception {
		Counter counter = new Counter();
		
		Vector elements = queryElements(type, conditions, false);
		Enumeration e2 = elements.elements();
		while (e2.hasMoreElements())
		{
			Hashtable hash = (Hashtable) e2.nextElement();
			String val = (String) hash.get(groupingField);
			counter.hit(val);
		}
		return counter.getResult();
	}

	/**
	 *  Retrieves the number of elements of a certain type.
	 *
	 *  @param type maps to the value of the objectClass attribute
	 *  @return the number of elements of this type
	 */
	public int getElementCount(String type) throws Exception {
		String filter = createExactFilter("objectClass", type);
		Enumeration e2 = createContext(ctx).search("", filter, controls);
		int count = 0;
		while (e2.hasMoreElements())
		{
			e2.nextElement();
			count++;
		}
		return count;
	}

	public int getElementCount(String elementType, Hashtable attributes, boolean caseSensitiv) throws Exception {
		return 0;	// ### not yet implemented	
	}

	public int getElementCount(String elementType, String id, String relatedElementType, String helperElementType) throws Exception {
		return 0;	// ### not yet implemented	
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	/**
	 *  Performs a (re)connect to the LDAP server.
	 */
	private void reconnect() throws NamingException
	{
		System.out.println( "=========> trying reconnect..." );
		int query_pos = url.indexOf("?");
		String firstUrl = url.substring(0, query_pos);
		
		String args = url.substring(query_pos + 1, url.length());
		Hashtable hash = parseArguments(args);
		
		String login       = getValue(hash, "login");
		String password    = getValue(hash, "password");
		String baseDN      = getValue(hash, "baseDN");
		String searchScope = getValue(hash, "searchScope");
		
		initial = createInitialContext(firstUrl, login, password);
		ctx = (DirContext) initial.lookup(baseDN);
		setSearchScope(searchScope);
	}

	/**
	 *  parses a String of the form <CODE>
	 *  key1=value1&key2=value2&key3=value3
	 *  </CODE> and returns the result as Hashtable.
	 */
	private Hashtable parseArguments(String arg)
	{
		Hashtable hash = new Hashtable();
		Enumeration tokens = new StringTokenizer(arg, "&");
		while (tokens.hasMoreElements())
		{
			String token = (String) tokens.nextElement();
			int pos = token.indexOf("=");
			String key = token.substring(0, pos);
			String val = token.substring(pos + 1, token.length());
			hash.put(key, val);
		}
		return hash;
	}

	/**
	 *  Utility method which throws an exception if
	 *  a value for <code>key</code> cannot be found in <code>hash</code>.
	 */
	private String getValue(Hashtable hash, String key)
	{
		String value = (String) hash.get(key);
		if (value == null) {
			throw new DeepaMehtaException("*** CorporateLDAPSource.getValue(): no value for key \"" + key + "\" defined!");
		}
		return value;
	}
	
	/**
	 *  Creates an initial DirContext.
	 */
	private DirContext createInitialContext(String url, String login, String password)
	throws NamingException
	{
		Hashtable env = new Hashtable();
		env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory" );

		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_PRINCIPAL, login);
		env.put(Context.SECURITY_CREDENTIALS, password);
		
		// requests communication via LDAP v3, if possible
		env.put("java.naming.ldap.version", "3");
		
		// declares the objectSid attribute to have binary values
//		env.put("java.naming.ldap.attributes.binary", "objectSid");  
		
		return new InitialDirContext(env);
	}
	
	/**
	 *  Sets the search scope.
	 */
	private void setSearchScope(String searchScope)
	{		
		controls = new SearchControls();
		
		if ("ONELEVEL_SCOPE".equalsIgnoreCase(searchScope)) {
			controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
		}
		
		else {
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		}
	}
	
	/**
	 *  Creates a search filter from a Hashtable of conditions.
	 *
	 *  AND-related substring filters:
	 *  (&(key1=*val1*)(key2=*val2*))
	 *  @see http://www.tc-trustcenter.de/certservices/ldap/ldap_manual.pdf
	 *
	 *  Note: some attribute fields don't allow substring search,
	 *  e.g. memberOf-Attribute in ActiveDirectory users
	 */
	private String createFilter(Hashtable hash)
	{
		String filter = "";
		Enumeration keys = hash.keys();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			filter += createFilter(key, hash.get(key));
		}
		return filter;
	}
	
	/**
	 *  Creates a single substring search filter from a key-value pair.
	 */
	private String createFilter(String key, Object val) {
		return "(" + key + "=*" + val + "*)";
	}
	
	/**
	 *  Creates a single search filter from a key-value pair.
	 */
	private String createExactFilter(String key, Object val) {
		return "(" + key + "=" + val + ")";
	}	
	
	/** To logically AND multiple filters. */
	private String conjunctFilters(String filters) {
		return "(&" + filters + ")";
	}
	
	/**
	 *  Puts the attributes of an element in a Hashtable.
	 *  Multiple values for an attribute will be concatenated
	 *  in one value, separated by the constant MULTIPLE_VALUE_DELIMITER.
	 *  @param ctx the context which attributes should be put in the Hashtable.
	 */
	private Hashtable transferAttributes(DirContext ctx) throws NamingException
	{	
		Hashtable hash = new Hashtable();
		
		// id for direct access to elements
		hash.put("ID", ctx.getNameInNamespace());
		
		Attributes attribs = ctx.getAttributes("");

		Enumeration e2 = attribs.getAll();		
		while (e2.hasMoreElements())
		{
			Attribute attr = (Attribute) e2.nextElement();		
			String attrname = attr.getID();
			
			// Get the list of values of this attribute
			Enumeration en = attr.getAll();		
			boolean firstValue = true;
			String valueList = "";
			
			while (en.hasMoreElements())
			{
				Object val = en.nextElement();
				valueList += (firstValue ? "" : MULTIPLE_VALUE_DELIMITER) + val;
				firstValue = false;
			}
			
			hash.put(attrname, valueList);
		}
		return hash;		
	}
	
	/**
	 *  @return a new instance of the DirContext
	 *  which may be used concurrently.
	 *  Performs a reconnect if an exception occurred.
	 */
	private DirContext createContext(DirContext ctx) throws NamingException
	{
		try {
			return (DirContext) ctx.lookup("");
		}
		catch(Exception e)
		{
			reconnect();
			return (DirContext) ctx.lookup("");
		}
	}	
	
	/**
	 *  Performs an LDAP search, using the default DirContext
	 *  and the given filter String.
	 *  @return a Vector of Hashtables, containing the attribute-value pairs.
	 */
	private Vector performSearch(String filter) throws NamingException
	{
		System.out.println("### performSearch: \"" + filter + "\"");
		Enumeration e2 = createContext(ctx).search("", filter, controls);
		Vector v = new Vector();
		while (e2.hasMoreElements())
		{
			NameClassPair pair = (NameClassPair) e2.nextElement();
			String objname = pair.getName();
			DirContext obj = (DirContext) createContext(ctx).lookup(objname);
			v.add(transferAttributes(obj));
		}
		return v;
	}
	
	/**
	 *  Helper class to count the occurrences of certain values
	 */
	private class Counter
	{
		Hashtable scores = new Hashtable();
		
		/**
		 *  Increases the score of the occurrence of val
		 */
		void hit(String val)
		{
			if (! scores.containsKey(val)) {
				scores.put(val, new Integer(1));
			}
			
			else {
				int count = ( (Integer) scores.get(val) ).intValue();
				scores.put(val, new Integer(count++));
			}
		}
		
		/**
		 *  @return a Vector of scores in a format suitable for queryGroups().
		 *  @see #queryGroups
		 */
		Vector getResult()
		{
			Vector result = new Vector();
			
			Enumeration keys = scores.keys();
			while (keys.hasMoreElements())
			{
				String [] groupData = new String[2];
				String key = (String) keys.nextElement();
				groupData[0] = key;
				groupData[1] = ( (Integer) scores.get(key) ).toString();
				result.add(groupData);
			}
			return result;
		}
	}
}