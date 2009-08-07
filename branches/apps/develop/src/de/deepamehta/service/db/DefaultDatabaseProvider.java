package de.deepamehta.service.db;

import de.deepamehta.Configuration;
import de.deepamehta.ConfigurationConstants;
import de.deepamehta.DeepaMehtaException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;



public class DefaultDatabaseProvider implements DatabaseProvider {

	public class DefaultDatabaseOptimizer extends DatabaseOptimizer {
		public void optimize_internal() {
			// do nothing
		}
	}

	/** SQL92 DBMS Hint */
	public static final String DBMS_HINT_SQL92 = "SQL92";

	private static final String DEFAULT_DB_TYPE = "mysql";

	private static Logger logger = Logger.getLogger("de.deepamehta");

	private final LinkedList freeCons = new LinkedList();

	private final LinkedList allCons = new LinkedList();

	private String jdbcURL;

	private Class driverClass;

	private Properties conProps = new Properties();

	private Driver driver;

	private String dbType;

	public DefaultDatabaseProvider(Properties conf) throws ClassNotFoundException, SQLException, InstantiationException,
													IllegalAccessException {
		setupDatabaseProvider(conf);
	}

	public DatabaseOptimizer getDatabaseOptimizer() {
		return new DefaultDatabaseOptimizer();
	}

	protected void setupDatabaseProvider(Properties conf) throws ClassNotFoundException, SQLException, InstantiationException,
			IllegalAccessException {
		jdbcURL = conf.getProperty(ConfigurationConstants.Database.DB_URL);
		dbType = conf.getProperty(ConfigurationConstants.Database.DB_TYPE);
		if (dbType == null) {
			dbType = DEFAULT_DB_TYPE;
		}
		Configuration c2;
		try {
			c2 = Configuration.getDbConfig(dbType);
		} catch (Exception e) {
			c2 = Configuration.getGlobalConfig();
		}
		String libs = c2.getProperty(ConfigurationConstants.Database.DB_LIBS);
		String driverClazz = c2.getProperty(ConfigurationConstants.Database.DB_DRIVER);
		//
		logger.info("using Database\n" + 
			"    Type: " + dbType + "\n" +
			"    URL: " + jdbcURL + "\n" +
			"    Driver: " + driverClazz);
		//
		driverClass = Class.forName(driverClazz);
		driver = (Driver) driverClass.newInstance();
		if (!driver.acceptsURL(jdbcURL)) {
			throw new DeepaMehtaException("JDBC-Driver and JDBC-Url does not match!");
		}
		String user = conf.getProperty(ConfigurationConstants.Database.DB_USER);
		if ((null != user) || ("".equals(user))) {
			setConnectionProperty("user", user);
			String password = conf.getProperty(ConfigurationConstants.Database.DB_PASSWORD);
			setConnectionProperty("password", password);
		}
	}

	public synchronized Connection getConnection() throws SQLException {
		if (0 == freeCons.size()) {
			return newConnection();
		}
		return (Connection) freeCons.removeFirst();
	}

	public String getDbmsHint() {
		return DBMS_HINT_SQL92;
	}

	public synchronized void freeConnection(Connection con) throws SQLException {
		freeCons.addLast(con);
	}

	protected void setConnectionProperty(String key, String value) {
		conProps.setProperty(key, value);
	}

	protected synchronized Connection newConnection() throws SQLException {
		Connection con = driver.connect(jdbcURL, conProps);
		con.setAutoCommit(true);
		allCons.add(con);
		logger.info("number of database connections: " + allCons.size() + " total, " + freeCons.size() + " free");
		return con;
	}

	protected void finalize() throws Throwable {
		closeAllCons();
		super.finalize();
	}

	protected void closeAllCons() throws SQLException {
		Connection con;
		while (allCons.size() > 0) {
			con = (Connection) allCons.removeFirst();
			con.close();
		}
	}

	public Statement getStatement() throws SQLException {
		Statement stmt = new AutoFreeConnectionStatement(this);
		return stmt;
	}

	public PreparedStatement getPreparedStatement(String sql) throws SQLException {
		PreparedStatement stmt = new AutoFreeConnectionPreparedStatement(this, sql);
		return stmt;
	}

	public void release() {
		try {
			logger.info("number of database connections: " + allCons.size() + " total, " + freeCons.size() + " free");
			closeAllCons();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Error releasing database provider ...", e);
		}
	}

	public void checkPointNeeded() {
	}

	public void logStatement(String arg0) {
	}
}
