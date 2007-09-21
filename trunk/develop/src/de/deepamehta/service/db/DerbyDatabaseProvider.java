package de.deepamehta.service.db;

import de.deepamehta.ConfigurationConstants;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DerbyDatabaseProvider extends DefaultDatabaseProvider {

	public class DerbyDatabaseOptimizer extends DatabaseOptimizer {
		private Statement statement;

		public void optimize_internal() throws SQLException {
			statement = getStatement();
			syscs_compress_table("Topic");
			syscs_compress_table("Association");
			syscs_compress_table("ViewTopic");
			syscs_compress_table("ViewAssociation");
			syscs_compress_table("TopicProp");
			syscs_compress_table("AssociationProp");
			syscs_compress_table("KeyGenerator");
			statement.close();
		}

		private void syscs_compress_table(String table) throws SQLException {
			statement.execute("CALL SYSCS_UTIL.SYSCS_COMPRESS_TABLE('"
					+ conf.getProperty(ConfigurationConstants.Database.DB_USER)
							.toUpperCase() + "', '" + table.toUpperCase()
					+ "', 1)");
		}
	}

	/** String for detecting Derby in the jdbc-driver-name */
	private static final String DBMS_HINT_DERBY_STR = "derby";

	private Properties conf;

	public DerbyDatabaseProvider(Properties conf)
			throws ClassNotFoundException, SQLException,
			InstantiationException, IllegalAccessException {
		super(conf);
	}

	protected void setupDatabaseProvider(Properties conf)
			throws ClassNotFoundException, SQLException,
			InstantiationException, IllegalAccessException {
		super.setupDatabaseProvider(conf);
		this.conf = conf;
	}

	public static boolean isResponsibleFor(String url) {
		return url.indexOf(DBMS_HINT_DERBY_STR) >= 0;
	}

	public DatabaseOptimizer getDatabaseOptimizer() {
		return new DerbyDatabaseOptimizer();
	}
}
