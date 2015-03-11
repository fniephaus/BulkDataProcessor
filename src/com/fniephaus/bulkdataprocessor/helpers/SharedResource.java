package com.fniephaus.bulkdataprocessor.helpers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.fniephaus.bulkdataprocessor.Entry;
import com.fniephaus.bulkdataprocessor.helpers.Configuration.StatusType;

public class SharedResource {
	public final static Logger Logger = java.util.logging.Logger
			.getLogger("bulkdataprocessor");
	private final static BlockingQueue<Entry> mQueue = new ArrayBlockingQueue<Entry>(
			Configuration.getInt("performance.queue_size"));
	public final static Entry PoisonPill = new Entry();
	private final int[] mCounts = new int[StatusType.values().length];
	private final EntityManagerFactory mEntityManagerFactory;
	private int mEntryCount = 0;
	private final long mStartTime = System.nanoTime();

	public SharedResource() {
		mEntityManagerFactory = Persistence
				.createEntityManagerFactory("bulkdataprocessor");
	}

	public void addTask(Entry pEntry) {
		try {
			mQueue.put(pEntry);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		mEntityManagerFactory.close();
	}

	public EntityManager createEntityManager() {
		return mEntityManagerFactory.createEntityManager();
	}

	public int getCount(StatusType pValidType) {
		return mCounts[pValidType.ordinal()];
	}

	public int getEntryCount() {
		return mEntryCount;
	}

	public int getFakes() {
		int sum = 0;
		for (int i = 0; i < mCounts.length; i++) {
			// ignore valid and incomplete counts
			if (i == StatusType.OK.ordinal())
				continue;
			sum += mCounts[i];
		}
		return sum;
	}

	public long getMilliSecondsOfExecution() {
		return TimeUnit.MILLISECONDS.convert(System.nanoTime() - mStartTime,
				TimeUnit.NANOSECONDS);
	}

	public Entry getTask() throws InterruptedException {
		return mQueue.take();
	}

	public int getTaskCount() {
		return mQueue.size();
	}

	public int getTotal() {
		int sum = 0;
		for (int i = 0; i < mCounts.length; i++) {
			sum += mCounts[i];
		}
		return sum;
	}

	public synchronized void incrementCount(StatusType pValidType) {
		mCounts[pValidType.ordinal()]++;
	}

	public boolean isConnected() {
		return mEntityManagerFactory != null;
	}

	public synchronized void setEntryCount(int pCount) {
		mEntryCount = pCount;
	}

}
