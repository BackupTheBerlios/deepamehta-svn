package de.deepamehta.service.db;

import java.sql.SQLException;

import de.deepamehta.util.Benchmark;

public abstract class DatabaseOptimizer {

	public class Worker implements Runnable {
		public void run() {
			try {
				optimize_internal();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	final void optimize() throws SQLException {
		try {
			Benchmark.run("Optimizing Database", new Worker());
		} catch (RuntimeException e) {
			SQLException cause = (SQLException) e.getCause();
			cause.printStackTrace();
			throw cause;
		}
	}

	abstract void optimize_internal() throws SQLException;
}
