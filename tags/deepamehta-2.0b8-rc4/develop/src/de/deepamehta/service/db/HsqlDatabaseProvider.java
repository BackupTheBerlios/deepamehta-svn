package de.deepamehta.service.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;



public class HsqlDatabaseProvider extends DefaultDatabaseProvider {

	private class CheckpointThread extends TimerTask {
		public void run() {
			try {
				if (doNextCheckPoint)
					checkpoint();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/** HSQLDB DBMS Hint */
	public static final DbmsHint DBMS_HINT_HSQLDB = new DbmsHint("HSQLDB");

	/** String for detecting HSQLDB in the jdbc-driver-name */
	private static final String DBMS_HINT_HSQLDB_STR = "hsqldb";

	private static final String DEFAULT_DRIVER_CLASS = "org.hsqldb.jdbcDriver";

	private boolean doNextCheckPoint = false;

	public HsqlDatabaseProvider(Properties conf) throws ClassNotFoundException, SQLException, InstantiationException,
												IllegalAccessException {
		super(conf);
		setConnectionProperty("shutdown", "true");
		int rate = 5 * 60 * 1000;
		new Timer().scheduleAtFixedRate(new CheckpointThread(), rate, rate);
	}

	public DbmsHint getDbmsHint() {
		return DBMS_HINT_HSQLDB;
	}

	public void freeConnection(Connection con) throws SQLException {
		//checkpoint(con);
		super.freeConnection(con);
	}

	protected void closeAllCons() throws SQLException {
		checkpoint();
		super.closeAllCons();
	}

	private void checkpoint() throws SQLException {
		Connection con = getConnection();
		checkpoint(con);
		freeConnection(con);
	}

	private void checkpoint(Connection con) throws SQLException {
		System.out.println("HSQLDB:CHECKPIONT");
		Statement stmt = con.createStatement();
		stmt.execute("CHECKPOINT");
		stmt.close();
	}

	public static boolean isResponsibleFor(String url) {
		return url.indexOf(DBMS_HINT_HSQLDB_STR) >= 0;
	}

	public void checkPointNeeded() {
		super.checkPointNeeded();
		doNextCheckPoint = true;
	}

	public void logStatement(String stmt) {
		super.logStatement(stmt);
		/* DEBUG */
		if (false) {
			// do not use the AutoFreeConnectionStatement
			// as it would result in an endless loop
			try {
				Connection connection = getConnection();
				try {
					Statement statement = connection.createStatement();
					ResultSet rs = statement.executeQuery("EXPLAIN PLAN FOR " + stmt);
					ResultSetMetaData md = rs.getMetaData();
					int cc = md.getColumnCount();
					StringBuffer buf = new StringBuffer();
					for (int i = 1; i <= cc; i++) {
						buf.append(md.getColumnName(i) + ";");
					}
					super.logStatement(buf.toString());
					buf = new StringBuffer();
					while (rs.next()) {
						for (int i = 1; i <= cc; i++) {
							buf.append(rs.getString(i) + ";");
						}
						super.logStatement(buf.toString());
					}
					super.logStatement("");
					statement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					freeConnection(connection);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
