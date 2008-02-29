package de.deepamehta.service;

import de.deepamehta.ConfigurationConstants;
import de.deepamehta.service.db.DatabaseProvider;
import de.deepamehta.service.db.DatabaseProviderFactory;
import de.deepamehta.util.CaseInsensitveHashtable;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;



/**
 * A {@link CorporateDatasource} implementation for SQL databases.
 * <P>
 * The database connection is established via JDBC.
 * <P>
 * <HR>
 * Last functional update: 3.2.2003 (2.0a18-pre1)<BR>
 * Last documentation update: 6.11.2000 (2.0a7-pre2)<BR>
 * J&ouml;rg Richter<BR>
 * jri@freenet.de
 */
public class CorporateSQLSource implements CorporateDatasource {



	// *************
	// *** Field ***
	// *************



	/**
	 * The connection to the database.
	 */
	private DatabaseProvider provider;



	// *******************
	// *** Constructor ***
	// *******************



	/**
	 * @see		de.deepamehta.topics.DataSourceTopic#openCorporateDatasource
	 */
	public CorporateSQLSource(String url, String dbtype, String user, String password) throws Exception {
		// create database connection
		System.out.println(">>> CorporateSQLSource(): connecting to database ... ");
		System.out.println(">    URL: \"" + url + "\"\n>    dbtype: \"" + dbtype + "\"");
		Properties conf = new Properties();
		conf.setProperty(ConfigurationConstants.Database.DB_TYPE, dbtype);
		conf.setProperty(ConfigurationConstants.Database.DB_URL, url);
		conf.setProperty(ConfigurationConstants.Database.DB_USER, user);
		conf.setProperty(ConfigurationConstants.Database.DB_PASSWORD, password);
		provider = DatabaseProviderFactory.getProvider(conf);
		Statement statement = provider.getStatement();
		statement.close();
		System.out.println(">>> connected.");
	}



	// *******************************************************
	// *** Implementation of interface CorporateDatasource ***
	// *******************************************************



	// --- queryElements (3 forms) ---

	public Vector queryElements(String elementName, Hashtable conditions, boolean caseSensitiv) throws SQLException {
		String queryStr = buildQuery(elementName, conditions, caseSensitiv, "*");
		//
		System.out.println("> CorporateSQLSource.queryElements(): \"" + queryStr + "\"");
		Statement stmt = provider.getStatement();
		ResultSet result = stmt.executeQuery(queryStr);
		Vector elements = queryResult(result);
		stmt.close();
		//
		return elements;
	}

	public Vector queryElements(String elementName, String fieldName, String fieldValue, boolean caseSensitiv)
																							throws SQLException {
		String queryStr = buildQuery(elementName, fieldName, fieldValue, caseSensitiv);
		if (queryStr == null) {
			System.out.println("*** CorporateSQLSource.queryElements(): by field \"" +
				fieldName + "\"=\"" + fieldValue + "\"");
			return new Vector();
		}
		//
		System.out.println("> CorporateSQLSource.queryElements(): \"" + queryStr + "\"");
		Statement stmt = provider.getStatement();
		ResultSet result = stmt.executeQuery(queryStr);
		Vector elements = queryResult(result);
		stmt.close();
		//
		return elements;
	}

	public Vector queryElements(String elementType, String id, String relatedElementType, String helperElementType)
																								throws Exception {
		String queryStr = buildQuery(elementType, id, relatedElementType, helperElementType);
		//
		System.out.println("> CorporateSQLSource.queryElements(): \"" + queryStr + "\"");
		Statement stmt = provider.getStatement();
		ResultSet result = stmt.executeQuery(queryStr);
		Vector elements = queryResult(result);
		stmt.close();
		//
		return elements;
	}

	// ---

	/**
	 * @see		de.deepamehta.topics.DataConsumerTopic#createNewTopic
	 */
	public Hashtable queryElement(String elementName, String id) throws SQLException {
		String queryStr = buildQuery(elementName, "ID", id, true);		// ### hardcoded
		if (queryStr == null) {
			System.out.println("*** CorporateSQLSource.queryElement(): by ID \"" + id + "\"");
			return new Hashtable();
		}
		System.out.println("> CorporateSQLSource.queryElement(): \"" + queryStr + "\"");
		Statement stmt = provider.getStatement();
		ResultSet result = stmt.executeQuery(queryStr);
		Vector queryResult = queryResult(result);
		stmt.close();
		if (queryResult.size() > 0) {
			return (Hashtable) queryResult.firstElement();
		} else {
			System.out.println("*** CorporateSQLSource.queryElement(): no \"" + elementName +
				"\" with ID \"" + id + "\" found -- no element available");
			return null;
		}
	}

	// ---

	/**
	 * @see		de.deepamehta.topics.ElementContainerTopic#autoSearch
	 */
	public Vector queryGroups(String elementName, Hashtable conditions, String groupingField) throws SQLException {
		String queryStr = buildQuery(elementName, conditions, false,
			groupingField + ", COUNT(*)") + " GROUP BY " + groupingField;
		System.out.println("> CorporateSQLSource.queryGroups(): \"" + queryStr + "\"");
		Statement stmt = provider.getStatement();
		ResultSet result = stmt.executeQuery(queryStr);
		Vector groups = queryGroups(result);
		stmt.close();
		return groups;
	}

	// ---

	public int getElementCount(String type) throws SQLException {
		String query = "SELECT COUNT(*) AS Cnt FROM " + type;
		//
		// ### System.out.println("> CorporateSQLSource.getElementCount(): \"" + query + "\"");
		Statement stmt = provider.getStatement();
		ResultSet result = stmt.executeQuery(query);
		int count = queryCount(result);
		stmt.close();
		//
		return count;
	}

	public int getElementCount(String elementType, Hashtable attributes, boolean caseSensitiv) throws Exception {
		String queryStr = buildQuery(elementType, attributes, caseSensitiv, "*", true);		// ### onlyCount=true
		//
		// ### System.out.println("> CorporateSQLSource.getElementCount(): \"" + queryStr + "\"");
		Statement stmt = provider.getStatement();
		ResultSet result = stmt.executeQuery(queryStr);
		int count = queryCount(result);
		stmt.close();
		//
		return count;
	}

	public int getElementCount(String elementType, String id, String relatedElementType, String helperElementType) throws Exception {
		String queryStr = buildQuery(elementType, id, relatedElementType, helperElementType, true);		// ### onlyCount=true
		//
		// ### System.out.println("> CorporateSQLSource.getElementCount(): \"" + queryStr + "\"");
		Statement stmt = provider.getStatement();
		ResultSet result = stmt.executeQuery(queryStr);
		int count = queryCount(result);
		stmt.close();
		//
		return count;
	}



	// ***********************
	// *** Private Methods ***
	// ***********************



	// --- buildQuery (5 forms) ---

	/**
	 * Lookup by single attribute.
	 */
	private String buildQuery(String elementName, String fieldName, String value, boolean caseSensitiv) {
		Hashtable attributes = new Hashtable();
		attributes.put(fieldName, value);
		return buildQuery(elementName, attributes, caseSensitiv, "*");
	}

	/**
	 * Lookup by attribute set.
	 */
	private String buildQuery(String elementType, Hashtable attributes, boolean caseSensitiv, String fieldsToSelect) {
		return buildQuery(elementType, attributes, caseSensitiv, fieldsToSelect, false);
	}

	/**
	 * Lookup by attribute set.
	 *
	 * @see		#queryElements(String elementName, Hashtable attributes, boolean caseSensitiv)
	 */
	private String buildQuery(String elementType, Hashtable attributes, boolean caseSensitiv, String fieldsToSelect,
																								boolean onlyCount) {
		String select = onlyCount ? "COUNT(" + fieldsToSelect + ") AS Cnt" : fieldsToSelect;
		StringBuffer query = new StringBuffer("SELECT " + select + " FROM " + elementType);
		boolean firstCond = true;
		Enumeration e = attributes.keys();
		while (e.hasMoreElements()) {
			String fieldName = (String) e.nextElement();
			String value = (String) attributes.get(fieldName);
			if (!value.equals("")) {
				query.append(firstCond ? " WHERE " : " AND ");
				if (caseSensitiv) {
					query.append(fieldName + "='" + value.replaceAll("'", "\\'") + "'");
				} else {
					query.append("LOWER(" + fieldName + ") LIKE '%" + value.toLowerCase() + "%'");
				}
				firstCond = false;
			}
		}
		return query.toString();
	}

	/**
	 * Join.
	 */
	private String buildQuery(String elementType, String id, String relatedElementType, String helperElementType) {
		return buildQuery(elementType, id, relatedElementType, helperElementType, false);
	}

	/**
	 * Join.
	 */
	private String buildQuery(String elementType, String id, String relatedElementType, String helperElementType,
																								boolean onlyCount) {
		String select = onlyCount ? "COUNT(*) AS Cnt" : relatedElementType + ".*";
		return "SELECT " + select + " FROM " + relatedElementType + ", " + helperElementType + " WHERE " +
			helperElementType + "." + relatedElementType + "ID=" + relatedElementType + ".ID AND " +
			helperElementType + "." + elementType + "ID='" + id + "'";
	}

	// ---

	/**
	 * @see		#queryElements
	 * @see		#queryElements
	 * @see		#queryElement
	 *
	 * Returns a <CODE>ResultSet</CODE> as vector of records.
	 * The fields of one record are stored in a hashtable:
	 * <P>
	 * Key: fieldname (<CODE>String</CODE>)<BR>
	 * Value: value (<CODE>String</CODE>)
	 */
	private Vector queryResult(ResultSet result) {
		Vector res = new Vector();	// the result object
		try {
			ResultSetMetaData meta;
			meta = result.getMetaData();
			int cols = meta.getColumnCount();
			Hashtable elementData;
			String fieldName;
			String value;
			int recNr = 0;
			// loop through all the records
			while (result.next()) {
				elementData = new CaseInsensitveHashtable();
				for (int c = 1; c <= cols; c++) {
					fieldName = meta.getColumnName(c);
					value = result.getString(c);
					// System.out.println("\"" + fieldName + "\": \"" + value + "\"");
					if (value != null) {
						elementData.put(fieldName, value);
					}
				}
				// add element to the result
				res.addElement(elementData);
				recNr++;
			}
			System.out.println("> CorporateSQLSource.queryResult(): " + recNr +
				" records, " + cols + " columns");
		} catch (SQLException e) {
			System.out.println("*** CorporateSQLSource.queryResult(): " + e);
		}
		return res;
	}

	/**
	 * @see		#queryGroups(String elementName, Hashtable conditions,
	 *															String groupingField)
	 */
	private Vector queryGroups(ResultSet result) throws SQLException {
		Vector res = new Vector();	// the result object
		String groupData[];
		int groupCount = 0;
		// loop through all the records
		while (result.next()) {
			groupData = new String[2];
			groupData[0] = result.getString(1);
			groupData[1] = Integer.toString(result.getInt(2));
			// ### SMC library returns numeric table values as floats -- stupid !!!
			// groupData[1] = result.getString(2);
			// add element to the result
			res.addElement(groupData);
			groupCount++;
		}
		System.out.println("> CorporateSQLSource.queryGroups(): " + groupCount +
																		" groups");
		return res;
	}
	
	private int queryCount(ResultSet result) throws SQLException {
		result.next();
		return result.getInt("Cnt");
	}
}
