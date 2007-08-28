package de.deepamehta.service.db;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import de.deepamehta.ConfigurationConstants;
import de.deepamehta.DeepaMehtaException;

public class DefaultDatabaseProvider implements DatabaseProvider {

	public class DefaultDatabaseOptimizer extends DatabaseOptimizer {
		public void optimize_internal() {
			// do nothing
		}
	}

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
	}

	public DatabaseOptimizer getDatabaseOptimizer() {
		return new DefaultDatabaseOptimizer();
	}

	protected void setupDatabaseProvider(Properties conf)
			throws ClassNotFoundException, SQLException,
			InstantiationException, IllegalAccessException {
		this.jdbcURL = conf.getProperty(ConfigurationConstants.Database.DB_URL);
		System.out.println("Using Database URL " + jdbcURL);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources;
		try {
			resources = resolver
					.getResources("file:"
							+ conf
									.getProperty(ConfigurationConstants.Database.DB_LIBS));
		} catch (IOException e) {
			throw new ClassNotFoundException(e.getMessage(), e);
		}
		loadClassFromLibs(resources, conf
				.getProperty(ConfigurationConstants.Database.DB_DRIVER));
		driver = (Driver) driverClass.newInstance();
		if (!driver.acceptsURL(jdbcURL))
			throw new DeepaMehtaException(
					"JDBC-Driver and JDBC-Url does not match!");
		String user = conf.getProperty(ConfigurationConstants.Database.DB_USER);
		if ((null != user) || ("".equals(user))) {
			setConnectionProperty("user", user);
			String password = conf
					.getProperty(ConfigurationConstants.Database.DB_PASSWORD);
			setConnectionProperty("password", password);
		}
	}

	private void loadClassFromLibs(Resource[] resources, String clazz)
			throws ClassNotFoundException {
		URLClassLoader classLoader;
		try {
			System.out.println("Using Classloader for Jars:");
			URL[] urls = new URL[resources.length];
			for (int i = 0; i < resources.length; i++) {
				Resource res = resources[i];
				File file = res.getFile();
				String path = file.getCanonicalPath();
				System.out.println(path);
				String urlString = "jar:file:" + path + "!/";
				urls[i] = new URL(urlString);
			}
			classLoader = new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			throw new ClassNotFoundException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ClassNotFoundException(e.getMessage(), e);
		}
		driverClass = classLoader.loadClass(clazz);
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
