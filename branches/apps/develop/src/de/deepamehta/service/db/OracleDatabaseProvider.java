package de.deepamehta.service.db;

import java.sql.SQLException;
import java.util.Properties;



public class OracleDatabaseProvider extends DefaultDatabaseProvider {

	/** Oracle DBMS Hint */
	public static final String DBMS_HINT_ORACLE = "ORACLE";

	/** String for detecting Oracle in the jdbc-driver-name */
	private static final String DBMS_HINT_ORACLE_STR = "oracle";

	public OracleDatabaseProvider(Properties conf) throws ClassNotFoundException,
						SQLException, InstantiationException, IllegalAccessException {
		super(conf);
	}

	public String getDbmsHint() {
		return DBMS_HINT_ORACLE;
	}

	public static boolean isResponsibleFor(String url) {
		return url.indexOf(DBMS_HINT_ORACLE_STR) >= 0;
	}
}
