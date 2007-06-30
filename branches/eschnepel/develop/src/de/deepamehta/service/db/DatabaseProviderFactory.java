package de.deepamehta.service.db;

import java.sql.SQLException;
import java.util.Properties;

import de.deepamehta.ConfigurationConstants;

public abstract class DatabaseProviderFactory {

	public static DatabaseProvider getProvider(Properties conf)
			throws ClassNotFoundException, SQLException {
		String dbUrl = conf.getProperty(ConfigurationConstants.Database.DB_URL);
		if (OracleDatabaseProvider.isResponsibleFor(dbUrl)) {
			return new OracleDatabaseProvider(conf);
		}
		if (HsqlDatabaseProvider.isResponsibleFor(dbUrl)) {
			return new HsqlDatabaseProvider(conf);
		}
		return new DefaultDatabaseProvider(conf);
	}

}
