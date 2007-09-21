package de.deepamehta.service.db;

import de.deepamehta.util.Benchmark;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class implements some database related cleanup routines. If there is an
 * error and the db is not in an consistent state (maybe due to autocommit and
 * not using transactions), this helps to return to the consistent state.
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
					sweep(statement);
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

		private void sweep(Statement statement) throws SQLException {
			sweepAssocStaleTopic(statement, "1", "source");
			sweepAssocStaleTopic(statement, "2", "target");
			sweepNoneViewTopicInView(statement, "Association");
			sweepNoneViewTopicInView(statement, "Topic");
			sweepRropNoRef(statement, "Association");
			sweepRropNoRef(statement, "Topic");
		}

		private void sweepAssocStaleTopic(Statement statement, String whichRef,
				String whichDesc) throws SQLException {
			sweep(statement, "Association a LEFT JOIN Topic t ON "
					+ compareIDVer("a.Topic", whichRef, "t.", "")
					+ " WHERE t.ID IS NULL", "Associations with stale "
					+ whichDesc + " Topic");
		}

		private void sweepNoneViewTopicInView(Statement statement, String what)
				throws SQLException {
			sweep(statement, "View" + what + " v LEFT JOIN Topic t ON "
					+ compareIDVer("t.", "", "v.ViewTopic", "")
					+ " WHERE t.ID IS NULL", "non-existent ViewTopic in "
					+ what + "s");
		}

		private void sweepRropNoRef(Statement statement, String what)
				throws SQLException {
			sweep(statement, what + "Prop p LEFT JOIN " + what + " r ON "
					+ compareIDVer("r.", "", "p." + what, "")
					+ " WHERE r.ID IS NULL", "non-existent " + what + " in "
					+ what + "Properties");
		}

		private String compareIDVer(String cmp1, String add1, String cmp2,
				String add2) {
			return cmp1 + "ID" + add1 + " = " + cmp2 + "ID" + add2 + " AND "
					+ cmp1 + "Version" + add1 + " = " + cmp2 + "Version" + add2;
		}

		private void sweep(Statement statement, String cmd, String message)
				throws SQLException {
			String query = "SELECT * FROM " + cmd;
			System.out.println(query);
			ResultSet res = statement.executeQuery(query);
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
//				query = "DELETE FROM " + cmd;
//				System.out.println(query);
//				int cnt = statement.executeUpdate(query);
//				System.out.println("*** Deleted " + cnt + " " + message + "!");
			}
		}
	}

	private final DatabaseProvider provider;

	public DatabaseSweeper(DatabaseProvider provider) throws SQLException {
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
