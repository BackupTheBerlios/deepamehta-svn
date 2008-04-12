package de.deepamehta.service.db;

import de.deepamehta.Configuration;
import de.deepamehta.ConfigurationConstants;
import de.deepamehta.DeepaMehtaException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;



public class DefaultDatabaseProvider implements DatabaseProvider {

	public class DefaultDatabaseOptimizer extends DatabaseOptimizer {
		public void optimize_internal() {
			// do nothing
		}
	}

	/** SQL92 DBMS Hint */
	public static final DbmsHint DBMS_HINT_SQL92 = new DbmsHint("SQL92");

	private static final String DEFAULT_DB_TYPE = "mysql";

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

		System.out.println("Using Database");
		System.out.println("  Type : " + dbType);
		System.out.println("  URL : " + jdbcURL);
		System.out.println("  Driver : " + driverClazz);

		loadClassFromLibs(libs, driverClazz);
		driver = (Driver) driverClass.newInstance();
		if (!driver.acceptsURL(jdbcURL))
			throw new DeepaMehtaException("JDBC-Driver and JDBC-Url does not match!");
		String user = conf.getProperty(ConfigurationConstants.Database.DB_USER);
		if ((null != user) || ("".equals(user))) {
			setConnectionProperty("user", user);
			String password = conf.getProperty(ConfigurationConstants.Database.DB_PASSWORD);
			setConnectionProperty("password", password);
		}
	}

	private void loadClassFromLibs(String libs, String driverClazz) throws ClassNotFoundException {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources;
		try {
			resources = resolver.getResources("file:" + libs);
		} catch (IOException e) {
			throw new ClassNotFoundException(e.getMessage(), e);
		}
		loadClassFromLibs(resources, driverClazz);
	}

	private void loadClassFromLibs(Resource[] resources, String clazz) throws ClassNotFoundException {
		URL[] urls = new URL[resources.length];
		try {
			for (int i = 0; i < resources.length; i++) {
				Resource res = resources[i];
				File file = res.getFile();
				String path = file.getCanonicalPath();
				String urlString;
				if (file.isDirectory()) {
					urlString = "file:" + path + "/";
				} else {
					urlString = "jar:file:" + path + "!/";
				}
				urls[i] = new URL(urlString);
			}
		} catch (MalformedURLException e) {
			throw new ClassNotFoundException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ClassNotFoundException(e.getMessage(), e);
		}
		loadClassFromLibs(urls, clazz);
	}

	private void loadClassFromLibs(URL[] urls, String clazz) throws ClassNotFoundException {
		System.out.println("Using separate Classloader for:");
		for (int i = 0; i < urls.length; i++) {
			System.out.println("  " + urls[i].toExternalForm());
		}
		URLClassLoader classLoader = new URLClassLoader(urls);
		driverClass = classLoader.loadClass(clazz);
	}

	public synchronized Connection getConnection() throws SQLException {
		if (0 == freeCons.size()) {
			return newConnection();
		}
		return (Connection) freeCons.removeFirst();
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
		System.out.println("DB-Connections: ALL:" + allCons.size() + " FREE:" + freeCons.size());
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
		Connection con = getConnection();
		Statement stmt = new AutoFreeConnectionStatement(this, con);
		return stmt;
	}

	public void release() {
		try {
			System.out.println("all / free connections : " + allCons.size() + " / " + freeCons.size());
			closeAllCons();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void checkPointNeeded() {
	}

	private static PrintStream dblog = null;
	static {
		/* DEBUG */
		if (false) {
			try {
				dblog = new PrintStream(new FileOutputStream("db.log"));
			} catch (FileNotFoundException e) {
				dblog = System.err;
			}
		}
	}

	public void logStatement(String arg0) {
		if (null != dblog) {
			dblog.println(arg0);
		}
	}

}
