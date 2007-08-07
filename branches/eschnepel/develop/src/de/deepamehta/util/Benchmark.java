package de.deepamehta.util;

public class Benchmark {

	public static void run(String taskName, Runnable test)
			throws RuntimeException {
		System.out.println("> Start of task \"" + taskName + "\" ...");
		long start = System.currentTimeMillis();
		try {
			test.run();
		} finally {
			System.out.println("> Done with task \"" + taskName + "\" in "
					+ (System.currentTimeMillis() - start) / 1000.0
					+ "seconds.");
		}
	}
}
