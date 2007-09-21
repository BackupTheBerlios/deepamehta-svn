package de.deepamehta.service.db;

import de.deepamehta.util.Benchmark;

import java.sql.SQLException;

public abstract class DatabaseOptimizer {
	private class Worker implements Runnable {
		public void run() {
			try {
				optimize_internal();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public final void optimize() throws SQLException {
		try {
			Benchmark.run("Optimizing Database", new Worker());
		} catch (RuntimeException e) {
			SQLException cause = (SQLException) e.getCause();
			cause.printStackTrace();
			throw cause;
		}
	}

	protected abstract void optimize_internal() throws SQLException;
}
