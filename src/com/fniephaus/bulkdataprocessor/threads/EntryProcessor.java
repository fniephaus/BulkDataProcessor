package com.fniephaus.bulkdataprocessor.threads;

import com.fniephaus.bulkdataprocessor.Entry;
import com.fniephaus.bulkdataprocessor.helpers.AbstractRunnable;
import com.fniephaus.bulkdataprocessor.helpers.Configuration;
import com.fniephaus.bulkdataprocessor.helpers.SharedResource;
import com.fniephaus.bulkdataprocessor.helpers.Configuration.StatusType;

public class EntryProcessor extends AbstractRunnable {
	private static final boolean DryRun = Configuration
			.getBoolean("general.dryrun");
	private static final int DBBatchSize = Configuration
			.getInt("performance.db_batch_size");
	private static Entry mPoisonPill = SharedResource.PoisonPill;
	private int mDBCount = 0;

	public EntryProcessor(SharedResource pResource) {
		super(pResource);
	}

	public void execute() {
		mEntityManager.getTransaction().begin();

		boolean running = true;
		entryloop: while (running) {
			try {
				Entry entry = mResource.getTask();

				// Check for poison pill object
				if (entry == mPoisonPill) {
					// Re-add poison pill object for others
					mResource.addTask(mPoisonPill);

					// Quit execution
					running = false;
					break;
				}

				/*
				 * Do stuff with the entry and update as you wish
				 */
				String field = entry.getField();
				if (field == null || field.isEmpty()) {
					updateEntry(entry, StatusType.ERROR);
					continue entryloop;
				}

				// Entry appears to be OK
				updateEntry(entry, StatusType.OK);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
		mEntityManager.getTransaction().commit();
	}

	private void updateEntry(Entry pEntry, StatusType pStatus) {
		pEntry.setStatus(pStatus);
		mResource.incrementCount(pStatus);
		if (DryRun)
			return;

		mEntityManager.merge(pEntry);

		if (++mDBCount % DBBatchSize == 0) {
			mEntityManager.getTransaction().commit();
			mEntityManager.clear();
			mEntityManager.getTransaction().begin();
		}
	}
}
