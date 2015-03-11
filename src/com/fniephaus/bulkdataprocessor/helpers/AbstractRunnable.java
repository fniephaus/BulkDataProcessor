package com.fniephaus.bulkdataprocessor.helpers;

import java.util.logging.Logger;

import javax.persistence.EntityManager;

public abstract class AbstractRunnable implements Runnable {
	protected SharedResource mResource;
	protected EntityManager mEntityManager;
	protected final static Logger mLogger = SharedResource.Logger;

	public AbstractRunnable(SharedResource pResource) {
		this.mResource = pResource;
	}

	public final void run() {
		mEntityManager = mResource.createEntityManager();
		execute();
		mEntityManager.close();
	}

	protected abstract void execute();
}
