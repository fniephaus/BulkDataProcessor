package com.fniephaus.bulkdataprocessor.threads;

import java.util.List;

import com.fniephaus.bulkdataprocessor.Entry;
import com.fniephaus.bulkdataprocessor.helpers.AbstractRunnable;
import com.fniephaus.bulkdataprocessor.helpers.Configuration;
import com.fniephaus.bulkdataprocessor.helpers.SharedResource;

public class DatabaseFetcher extends AbstractRunnable {
	private static final int DBFetchSIze = Configuration.getInt("performance.db_fetch_size");
	private static final int PrintThreshold = Configuration.getInt("general.print_threshold");

	public DatabaseFetcher(SharedResource pResource) {
		super(pResource);
	}

	public void execute() {
		if (!mResource.isConnected()) {
			mLogger.warning("Failed to make connection.");
			return;
		}

		int count = 0;
		List<Entry> entries;
		while ((entries = getAllEntriesIterable(count)).size() > 0) {

			for (Entry entry : entries) {
				mResource.addTask(entry);
				if (++count % PrintThreshold == 0) {
					mLogger.info(count + " entries processed...");
				}
			}

			mEntityManager.clear();
		}
		
		// Add dummy to inform others about termination
		mResource.addTask(SharedResource.PoisonPill);
		mResource.setEntryCount(count);
		mLogger.info("All entries fetched.");
	}

	private List<Entry> getAllEntriesIterable(int offset) {
		return mEntityManager.createQuery("FROM Entry e ORDER BY e.id", Entry.class)
				.setHint("org.hibernate.readOnly", true)
				.setFirstResult(offset)
				.setMaxResults(DBFetchSIze).getResultList();
	}
}