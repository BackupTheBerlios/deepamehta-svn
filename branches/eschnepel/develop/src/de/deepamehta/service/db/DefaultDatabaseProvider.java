package de.deepamehta.service.db;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;

import de.deepamehta.ConfigurationConstants;
import de.deepamehta.DeepaMehtaException;

public class DefaultDatabaseProvider implements DatabaseProvider {

	/** SQL92 DBMS Hint */
	public static final DbmsHint DBMS_HINT_SQL92 = new DbmsHint("SQL92");

	private final LinkedList freeCons = new LinkedList();

	private final LinkedList allCons = new LinkedList();

	private String jdbcURL;

	private Class driverClass;

	private Properties conProps = new Properties();

	private Driver driver;

	public DefaultDatabaseProvider(Properties conf)
			throws ClassNotFoundException, SQLException,
			InstantiationException, IllegalAccessException {
		setupDatabaseProvider(conf);
		new DatabaseSweeper(this).sweep();
	}

	protected void setupDatabaseProvider(Properties conf)
			throws ClassNotFoundException, SQLException,
			InstantiationException, IllegalAccessException {
		this.jdbcURL = conf.getProperty(ConfigurationConstants.Database.DB_URL);
		System.out.println("Using Database URL "+jdbcURL);
		File libFile = new File(conf
				.getProperty(ConfigurationConstants.Database.DB_LIB));
		String urlString;
		try {
			urlString = "jar:" + libFile.toURL().toExternalForm() + "!/";
			ClassLoader classLoader = new URLClassLoader(new URL[] { new URL(
					urlString) });
			driverClass = classLoader.loadClass(conf
					.getProperty(ConfigurationConstants.Database.DB_DRIVER));
			driver = (Driver) driverClass.newInstance();
			if (!driver.acceptsURL(jdbcURL))
				throw new DeepaMehtaException(
						"JDBC-Driver and JDBC-Url does not match!");
		} catch (MalformedURLException e) {
			throw new ClassNotFoundException(e.getMessage());
		}
		setConnectionProperty("user", conf
				.getProperty(ConfigurationConstants.Database.DB_USER));
		setConnectionProperty("password", conf
				.getProperty(ConfigurationConstants.Database.DB_PASSWORD));
	}

	public synchronized Connection getConnection() throws SQLException {
		if (0 == freeCons.size()) {
			return newConnection();
		}
		return (Connection) freeCons.poll();
	}

	public DbmsHint getDbmsHint() {
		return DBMS_HINT_SQL92;
	}

	public synchronized void freeConnection(Connection con) throws SQLException {
		freeCons.addLast(con);
		// System.out.println("DB-Connections: ALL:"+allCons.size()+"
		// FREE:"+freeCons.size());
	}

	protected void setConnectionProperty(String key, String value) {
		conProps.setProperty(key, value);
	}

	protected synchronized Connection newConnection() throws SQLException {
		Connection con = driver.connect(jdbcURL, conProps);
		con.setAutoCommit(true);
		allCons.add(con);
		System.out.println("DB-Connections: ALL:" + allCons.size() + " FREE:"
				+ freeCons.size());
		return con;
	}

	protected void finalize() throws Throwable {
		closeAllCons();
		super.finalize();
	}

	protected void closeAllCons() throws SQLException {
		Connection con;
		while (null != (con = (Connection) allCons.poll())) {
			con.close();
		}
	}

	public Statement getStatement() throws SQLException {
		Connection con;
		// at least 2 free Connections for better
		// Performance of parallel accesses
		if (freeCons.size() < 2) {
			con = newConnection();
		} else {
			con = getConnection();
		}
		Statement stmt = new AutoFreeConnectionStatement(this, con);
		return stmt;
	}

	public void release() {
		try {
			System.out.println("all / free connections : " + allCons.size()
					+ " / " + freeCons.size());
			closeAllCons();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkPointNeeded() {
	}
}
