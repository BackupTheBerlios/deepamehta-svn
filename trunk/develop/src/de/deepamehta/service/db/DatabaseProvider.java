package de.deepamehta.service.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;



public interface DatabaseProvider {
	
	String getDbmsHint();
	//
	Connection getConnection() throws SQLException;
	void freeConnection(Connection con) throws SQLException;
	//
	Statement getStatement() throws SQLException;
	PreparedStatement getPreparedStatement(String sql) throws SQLException;
	public void logStatement(String sql);
	//
	void checkPointNeeded();
	DatabaseOptimizer getDatabaseOptimizer();
	//
	void release();
}
