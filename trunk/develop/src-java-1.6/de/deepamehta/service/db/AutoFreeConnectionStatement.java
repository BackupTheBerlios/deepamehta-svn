package de.deepamehta.service.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

public final class AutoFreeConnectionStatement implements Statement {

	private final Statement statement;

	private final DatabaseProvider provider;

	private final Connection con;

	private boolean finalClose = true;
	
	public AutoFreeConnectionStatement(DatabaseProvider provider)
			throws SQLException {
		this.provider = provider;
		this.con = provider.getConnection();
		this.statement = con.createStatement();
	}

	protected void finalize() throws Throwable {
		if (finalClose) {
			System.out.println("*** Closing statement while finalizing. "
					+ "Someone forgot to close it.");
			close();
		}
		super.finalize();
	}

	final public void addBatch(String arg0) throws SQLException {
		statement.addBatch(arg0);
	}

	final public void cancel() throws SQLException {
		statement.cancel();
	}

	final public void clearBatch() throws SQLException {
		statement.clearBatch();
	}

	final public void clearWarnings() throws SQLException {
		statement.clearWarnings();
	}

	final public void close() throws SQLException {
		statement.close();
		provider.freeConnection(con);
		finalClose = false;
	}

	final public boolean execute(String arg0, int arg1) throws SQLException {
		provider.logStatement(arg0);
		provider.checkPointNeeded();
		return statement.execute(arg0, arg1);
	}

	final public boolean execute(String arg0, int[] arg1) throws SQLException {
		provider.logStatement(arg0);
		provider.checkPointNeeded();
		return statement.execute(arg0, arg1);
	}

	final public boolean execute(String arg0, String[] arg1)
			throws SQLException {
		provider.logStatement(arg0);
		provider.checkPointNeeded();
		return statement.execute(arg0, arg1);
	}

	final public boolean execute(String arg0) throws SQLException {
		provider.logStatement(arg0);
		provider.checkPointNeeded();
		return statement.execute(arg0);
	}

	final public int[] executeBatch() throws SQLException {
		provider.checkPointNeeded();
		return statement.executeBatch();
	}

	final public ResultSet executeQuery(String arg0) throws SQLException {
		provider.logStatement(arg0);
		return statement.executeQuery(arg0);
	}

	final public int executeUpdate(String arg0, int arg1) throws SQLException {
		provider.logStatement(arg0);
		provider.checkPointNeeded();
		return statement.executeUpdate(arg0, arg1);
	}

	final public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		provider.logStatement(arg0);
		provider.checkPointNeeded();
		return statement.executeUpdate(arg0, arg1);
	}

	final public int executeUpdate(String arg0, String[] arg1)
			throws SQLException {
		provider.logStatement(arg0);
		provider.checkPointNeeded();
		return statement.executeUpdate(arg0, arg1);
	}

	final public int executeUpdate(String arg0) throws SQLException {
		provider.logStatement(arg0);
		provider.checkPointNeeded();
		return statement.executeUpdate(arg0);
	}

	final public Connection getConnection() throws SQLException {
		return con;
	}

	final public int getFetchDirection() throws SQLException {
		return statement.getFetchDirection();
	}

	final public int getFetchSize() throws SQLException {
		return statement.getFetchSize();
	}

	final public ResultSet getGeneratedKeys() throws SQLException {
		return statement.getGeneratedKeys();
	}

	final public int getMaxFieldSize() throws SQLException {
		return statement.getMaxFieldSize();
	}

	final public int getMaxRows() throws SQLException {
		return statement.getMaxRows();
	}

	final public boolean getMoreResults() throws SQLException {
		return statement.getMoreResults();
	}

	final public boolean getMoreResults(int arg0) throws SQLException {
		return statement.getMoreResults(arg0);
	}

	final public int getQueryTimeout() throws SQLException {
		return statement.getQueryTimeout();
	}

	final public ResultSet getResultSet() throws SQLException {
		return statement.getResultSet();
	}

	final public int getResultSetConcurrency() throws SQLException {
		return statement.getResultSetConcurrency();
	}

	final public int getResultSetHoldability() throws SQLException {
		return statement.getResultSetHoldability();
	}

	final public int getResultSetType() throws SQLException {
		return statement.getResultSetType();
	}

	final public int getUpdateCount() throws SQLException {
		return statement.getUpdateCount();
	}

	final public SQLWarning getWarnings() throws SQLException {
		return statement.getWarnings();
	}

	final public void setCursorName(String arg0) throws SQLException {
		statement.setCursorName(arg0);
	}

	final public void setEscapeProcessing(boolean arg0) throws SQLException {
		statement.setEscapeProcessing(arg0);
	}

	final public void setFetchDirection(int arg0) throws SQLException {
		statement.setFetchDirection(arg0);
	}

	final public void setFetchSize(int arg0) throws SQLException {
		statement.setFetchSize(arg0);
	}

	final public void setMaxFieldSize(int arg0) throws SQLException {
		statement.setMaxFieldSize(arg0);
	}

	final public void setMaxRows(int arg0) throws SQLException {
		statement.setMaxRows(arg0);
	}

	final public void setQueryTimeout(int arg0) throws SQLException {
		statement.setQueryTimeout(arg0);
	}

	public boolean isClosed() throws SQLException {
		return statement.isClosed();
	}

	public boolean isPoolable() throws SQLException {
		return statement.isPoolable();
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return statement.isWrapperFor(iface);
	}

	public void setPoolable(boolean poolable) throws SQLException {
		statement.setPoolable(poolable);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return statement.unwrap(iface);
	}
}
