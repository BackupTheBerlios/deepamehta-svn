package de.deepamehta.service.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import de.deepamehta.util.Benchmark;

/**
 * This class implements some database related cleanup routines.
 * If there is an error and the db is not in an consistent state
 * (maybe due to autocommit and not using transactions),
 * this helps to return to the consistent state.
 * 
 * @author enrico
 */
public class DatabaseSweeper {
	private final class Worker implements Runnable {
		public void run() {
			try {
				Statement statement = provider.getStatement();
				Connection con = statement.getConnection();
				try {
					sweep(
							statement,
							"Association WHERE NOT EXISTS (SELECT * FROM Topic WHERE Association.TopicID1 = Topic.ID)",
							"Associations with stale target Topic");
					sweep(
							statement,
							"Association WHERE NOT EXISTS (SELECT * FROM Topic WHERE Association.TopicID2 = Topic.ID)",
							"Associations with stale source Topic");
					if (!con.getAutoCommit())
						con.commit();
				} catch (SQLException e) {
					if (!con.getAutoCommit())
						con.rollback();
					throw new RuntimeException(e);
				} finally {
					statement.close();
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

		private void sweep(Statement statement, String cmd, String message)
				throws SQLException {
			ResultSet res = statement.executeQuery("SELECT * FROM " + cmd);
			if (res.next()) {
				System.out.println("*** " + message + ":");
				ResultSetMetaData metaData = res.getMetaData();
				do {
					StringBuffer sb = new StringBuffer("***  ");
					int columnCount = metaData.getColumnCount();
					for (int i = 1; i <= columnCount; i++) {
						sb.append(" ");
						sb.append(metaData.getColumnLabel(i));
						sb.append(":\"");
						sb.append(res.getString(i));
						sb.append("\"");
					}
					System.out.println(sb.toString());
				} while (res.next());
				int cnt = statement.executeUpdate("DELETE FROM " + cmd);
				System.out.println("*** Deleted " + cnt + " " + message + "!");
			}
		}
	}

	private final DatabaseProvider provider;

	public DatabaseSweeper(DatabaseProvider provider)
			throws SQLException {
		this.provider = provider;
	}

	public void sweep() throws SQLException {
		try {
			Benchmark.run("Sweeping Database", new Worker());
		} catch (RuntimeException e) {
			SQLException cause = (SQLException) e.getCause();
			cause.printStackTrace();
			throw cause;
		}
	}
}
