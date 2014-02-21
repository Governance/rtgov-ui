package org.overlord.rtgov.ui.server.services.impl;

import javax.persistence.EntityManager;

public interface EntityManagerCallback<T> {

	T execute(EntityManager entityManager);

	abstract class Void implements EntityManagerCallback<Void> {

		public abstract void doExecute(EntityManager entityManager);

		@Override
		public Void execute(EntityManager entityManager) {
			doExecute(entityManager);
			return null;
		}

	}
}