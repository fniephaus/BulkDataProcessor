package com.fniephaus.bulkdataprocessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fniephaus.bulkdataprocessor.helpers.Configuration;
import com.fniephaus.bulkdataprocessor.helpers.SharedResource;
import com.fniephaus.bulkdataprocessor.helpers.Configuration.StatusType;
import com.fniephaus.bulkdataprocessor.threads.DatabaseFetcher;
import com.fniephaus.bulkdataprocessor.threads.EntryProcessor;

public class Main {
	public final static Logger Logger = SharedResource.Logger;
	private final SharedResource mResource;
	private final ExecutorService mProducer = Executors
			.newSingleThreadExecutor();
	private final ExecutorService consumers = Executors
			.newFixedThreadPool(Configuration
					.getInt("performance.validation_threads"));

	public Main() {
		mResource = new SharedResource();
		setUpLogger();
		startDatabaseFetcher();
		startValidationThreads();
		awaitThreadCompletion();
		printResults();
		mResource.close();
	}

	public static void main(String[] args) {
		new Main();
	}

	private static void setUpLogger() {
		Level logging_level = Level.OFF;
		switch (Configuration.getString("general.logging_level")) {
		case "info":
			logging_level = Level.INFO;
		case "all":
			logging_level = Level.ALL;
		}
		Main.Logger.setLevel(logging_level);
	}

	private void awaitThreadCompletion() {
		try {
			// Wait for database fetcher to terminate
			mProducer.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

			// Wait for validation threads to terminate
			int tries = 10;
			while (!consumers.awaitTermination(1, TimeUnit.SECONDS)) {
				Logger.info("Waiting for validation threads to finish "
						+ mResource.getTaskCount() + " tasks.");
				if (tries-- <= 0) {
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void startValidationThreads() {
		for (int i = 0; i < Configuration
				.getInt("performance.validation_threads"); i++)
			consumers.execute(new EntryProcessor(mResource));

		// Make sure no new tasks are accepted
		consumers.shutdown();
	}

	private void startDatabaseFetcher() {
		mProducer.execute(new DatabaseFetcher(mResource));

		// Make sure no new tasks are accepted
		mProducer.shutdown();
	}

	public void printResults() {
		System.out.println("=================================================");
		int total = mResource.getTotal();
		int totalCheck = mResource.getEntryCount();
		// If mismatch, we have not checked as many entries as we fetched!
		String check = (total == totalCheck ? "" : "MISMATCH!");
		System.out.println("Total checked:\t\t" + total + "\t\t" + check);

		for (StatusType status : StatusType.values()) {
			printResult(status.name() + "\t", status, total);
		}

		System.out.println("");

		long executionTime = mResource.getMilliSecondsOfExecution();
		System.out.println("Time of execution:\t" + executionTime / 1000.0
				+ "s");
		System.out.println("=================================================");
	}

	private void printResult(String pText, StatusType pType, int pTotal) {
		int entries = mResource.getCount(pType);
		System.out.println(pText + "\t\t" + entries + "\t\t"
				+ (entries * 100.0f) / pTotal + "%");
	}
}
