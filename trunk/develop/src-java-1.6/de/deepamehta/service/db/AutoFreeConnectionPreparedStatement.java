package de.deepamehta.service.db;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public final class AutoFreeConnectionPreparedStatement implements PreparedStatement {

	private final PreparedStatement statement;

	private final DatabaseProvider provider;

	private final Connection con;

	private boolean finalClose = true;

	public AutoFreeConnectionPreparedStatement(DatabaseProvider provider, String sql)
			throws SQLException {
		this.provider = provider;
		this.con = provider.getConnection();
		this.statement = con.prepareStatement(sql);
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

	public void addBatch() throws SQLException {
		statement.addBatch();
	}

	public void clearParameters() throws SQLException {
		statement.clearParameters();
	}

	public boolean execute() throws SQLException {
		return statement.execute();
	}

	public ResultSet executeQuery() throws SQLException {
		return statement.executeQuery();
	}

	public int executeUpdate() throws SQLException {
		return statement.executeUpdate();
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return statement.getMetaData();
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		return statement.getParameterMetaData();
	}

	public void setArray(int i, Array x) throws SQLException {
		statement.setArray(i, x);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		statement.setAsciiStream(parameterIndex, x, length);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		statement.setBigDecimal(parameterIndex, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		statement.setBinaryStream(parameterIndex, x, length);
	}

	public void setBlob(int i, Blob x) throws SQLException {
		statement.setBlob(i, x);
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		statement.setBoolean(parameterIndex, x);
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		statement.setByte(parameterIndex, x);
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		statement.setBytes(parameterIndex, x);
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		statement.setCharacterStream(parameterIndex, reader, length);
	}

	public void setClob(int i, Clob x) throws SQLException {
		statement.setClob(i, x);
	}

	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		statement.setDate(parameterIndex, x, cal);
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		statement.setDate(parameterIndex, x);
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		statement.setDouble(parameterIndex, x);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		statement.setFloat(parameterIndex, x);
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		statement.setInt(parameterIndex, x);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		statement.setLong(parameterIndex, x);
	}

	public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
		statement.setNull(paramIndex, sqlType, typeName);
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		statement.setNull(parameterIndex, sqlType);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException {
		statement.setObject(parameterIndex, x, targetSqlType, scale);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		statement.setObject(parameterIndex, x, targetSqlType);
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		statement.setObject(parameterIndex, x);
	}

	public void setRef(int i, Ref x) throws SQLException {
		statement.setRef(i, x);
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		statement.setShort(parameterIndex, x);
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		statement.setString(parameterIndex, x);
	}

	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		statement.setTime(parameterIndex, x, cal);
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		statement.setTime(parameterIndex, x);
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		statement.setTimestamp(parameterIndex, x, cal);
	}

	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		statement.setTimestamp(parameterIndex, x);
	}

	/**
	 * Deprecated
	 */
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		statement.setUnicodeStream(parameterIndex, x, length);
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		statement.setURL(parameterIndex, x);
	}

	public boolean isClosed() throws SQLException {
		return statement.isClosed();
	}

	public boolean isPoolable() throws SQLException {
		return statement.isPoolable();
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return statement.isWrapperFor(arg0);
	}

	public void setAsciiStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		statement.setAsciiStream(arg0, arg1, arg2);
	}

	public void setAsciiStream(int arg0, InputStream arg1) throws SQLException {
		statement.setAsciiStream(arg0, arg1);
	}

	public void setBinaryStream(int arg0, InputStream arg1, long arg2) throws SQLException {
		statement.setBinaryStream(arg0, arg1, arg2);
	}

	public void setBinaryStream(int arg0, InputStream arg1) throws SQLException {
		statement.setBinaryStream(arg0, arg1);
	}

	public void setBlob(int arg0, InputStream arg1, long arg2) throws SQLException {
		statement.setBlob(arg0, arg1, arg2);
	}

	public void setBlob(int arg0, InputStream arg1) throws SQLException {
		statement.setBlob(arg0, arg1);
	}

	public void setCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
		statement.setCharacterStream(arg0, arg1, arg2);
	}

	public void setCharacterStream(int arg0, Reader arg1) throws SQLException {
		statement.setCharacterStream(arg0, arg1);
	}

	public void setClob(int arg0, Reader arg1, long arg2) throws SQLException {
		statement.setClob(arg0, arg1, arg2);
	}

	public void setClob(int arg0, Reader arg1) throws SQLException {
		statement.setClob(arg0, arg1);
	}

	public void setNCharacterStream(int arg0, Reader arg1, long arg2) throws SQLException {
		statement.setNCharacterStream(arg0, arg1, arg2);
	}

	public void setNCharacterStream(int arg0, Reader arg1) throws SQLException {
		statement.setNCharacterStream(arg0, arg1);
	}

	public void setNClob(int arg0, NClob arg1) throws SQLException {
		statement.setNClob(arg0, arg1);
	}

	public void setNClob(int arg0, Reader arg1, long arg2) throws SQLException {
		statement.setNClob(arg0, arg1, arg2);
	}

	public void setNClob(int arg0, Reader arg1) throws SQLException {
		statement.setNClob(arg0, arg1);
	}

	public void setNString(int arg0, String arg1) throws SQLException {
		statement.setNString(arg0, arg1);
	}

	public void setPoolable(boolean arg0) throws SQLException {
		statement.setPoolable(arg0);
	}

	public void setRowId(int arg0, RowId arg1) throws SQLException {
		statement.setRowId(arg0, arg1);
	}

	public void setSQLXML(int arg0, SQLXML arg1) throws SQLException {
		statement.setSQLXML(arg0, arg1);
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return statement.unwrap(arg0);
	}
}
