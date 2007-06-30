package de.deepamehta.service.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import de.deepamehta.util.Benchmark;

public class DatabaseSweeper {
	private final Connection con;

	public DatabaseSweeper(Connection con) {
		this.con = con;
	}

	public void sweep() throws SQLException {
		try {
			Benchmark.run("Sweeping Database", new Runnable() {
				public void run() {
					try {
						final Statement statement = con
								.createStatement();
						sweep(
								statement,
								"delete from association where not exists (select * from topic where topicid1 = topic.id);",
								"Deleted $$ Accosiations with stale target Topic!");
						sweep(
								statement,
								"delete from association where not exists (select * from topic where topicid2 = topic.id);",
								"Deleted $$ Accosiations with stale source Topic!");
						statement.execute("commit");
						statement.close();
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				}
			});
		} catch (RuntimeException e) {
			throw (SQLException) e.getCause();
		}
	}

	private void sweep(Statement statement, String cmd, String message)
			throws SQLException {
		int cnt = statement.executeUpdate(cmd);
		if (cnt > 0) {
			System.out.println(">  " + message.replace("$$", "" + cnt));
		}
	}
}
