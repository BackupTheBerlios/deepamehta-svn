package de.deepamehta.service.db;

import de.deepamehta.ConfigurationConstants;

import java.sql.SQLException;
import java.util.Properties;

public abstract class DatabaseProviderFactory {

	public static DatabaseProvider getProvider(Properties conf)
			throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		String dbUrl = conf.getProperty(ConfigurationConstants.Database.DB_URL);
		if (OracleDatabaseProvider.isResponsibleFor(dbUrl)) {
			return new OracleDatabaseProvider(conf);
		}
		if (HsqlDatabaseProvider.isResponsibleFor(dbUrl)) {
			return new HsqlDatabaseProvider(conf);
		}
		if (DerbyDatabaseProvider.isResponsibleFor(dbUrl)) {
			return new DerbyDatabaseProvider(conf);
		}
		return new DefaultDatabaseProvider(conf);
	}

}
