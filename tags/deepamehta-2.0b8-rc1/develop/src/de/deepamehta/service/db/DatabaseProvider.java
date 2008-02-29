package de.deepamehta.service.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface DatabaseProvider {
	public static class DbmsHint extends org.apache.avalon.framework.Enum{
		protected DbmsHint(String name) {
			super(name);
		}
	}

	void release();
	
	Connection getConnection() throws SQLException;

	DbmsHint getDbmsHint();

	void freeConnection(Connection con) throws SQLException;

	Statement getStatement() throws SQLException;

	void checkPointNeeded();

	DatabaseOptimizer getDatabaseOptimizer();

	public void logStatement(String arg0);
}
