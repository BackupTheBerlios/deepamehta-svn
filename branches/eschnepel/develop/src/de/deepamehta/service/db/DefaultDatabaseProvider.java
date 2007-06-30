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

public class DefaultDatabaseProvider implements DatabaseProvider {

	/** SQL92 DBMS Hint */
	public static final DbmsHint DBMS_HINT_SQL92 = new DbmsHint("SQL92");

	private final LinkedList freeCons = new LinkedList();

	private final LinkedList allCons = new LinkedList();

	private String jdbcURL;

	private Class driverClass;

	private Properties conProps = new Properties();;

	public DefaultDatabaseProvider(Properties conf)
			throws ClassNotFoundException, SQLException {
		setupDatabaseProvider(conf);
	}

	protected void setupDatabaseProvider(Properties conf)
			throws ClassNotFoundException, SQLException {
		this.jdbcURL = conf.getProperty(ConfigurationConstants.Database.DB_URL);
		File libFile = new File(conf
				.getProperty(ConfigurationConstants.Database.DB_LIB));
		String urlString;
		try {
			urlString = "jar:" + libFile.toURL().toExternalForm() + "!/";
			ClassLoader classLoader = new URLClassLoader(new URL[] { new URL(
					urlString) });
			driverClass = classLoader.loadClass(conf
					.getProperty(ConfigurationConstants.Database.DB_DRIVER));
		} catch (MalformedURLException e) {
			throw new ClassNotFoundException(e.getMessage());
		}
	}

	public Connection getConnection() throws SQLException {
		if (0 == freeCons.size()) {
			return newConnection();
		}
		return (Connection) freeCons.poll();
	}

	public DbmsHint getDbmsHint() {
		return DBMS_HINT_SQL92;
	}

	public void freeConnection(Connection con) throws SQLException {
		freeCons.addLast(con);
//		System.out.println("DB-Connections: ALL:"+allCons.size()+" FREE:"+freeCons.size());
	}

	protected void setConnectionProperties(String key, String value) {
		conProps.setProperty(key, value);
	}

	protected Connection newConnection() throws SQLException {
		try {
			Driver driver = (Driver) driverClass.newInstance();
			Connection con = driver.connect(jdbcURL, conProps);
			con.setAutoCommit(true);
			if (allCons.size() == 0) {
				new DatabaseSweeper(con).sweep();
			}
			allCons.add(con);
			System.out.println("DB-Connections: ALL:"+allCons.size()+" FREE:"+freeCons.size());
			return con;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
		// at least 5 free Connections for better
		// Performance of parallel accesses
		if (freeCons.size() < 1) {
			con = newConnection();
		} else {
			con = getConnection();
		}
		Statement stmt = con.createStatement();
		freeConnection(con);
		/*
		try {
			throw new Exception();
		} catch (Exception e) {
			StackTraceElement[] stes = e.getStackTrace();
			int pos = 1;
			StackTraceElement lste;
			for (int i=0;i<3;i++){
				lste=stes[pos];
				while (pos<stes.length && lste.getClassName().equals(stes[pos].getClassName())){
					pos++;
				}
				System.out.println(stes[pos-1].toString());
			}
		}*/
		return stmt;
	}

	public void release() {
		try {
			closeAllCons();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
