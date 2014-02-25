package org.overlord.rtgov.analytics.situation.store.jpa;

import static org.junit.Assert.*;
import static org.overlord.rtgov.ui.client.model.ResolutionState.IN_PROGRESS;

import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.store.SituationStore;
import org.overlord.rtgov.analytics.situation.store.SituationsQuery;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration
public class JPASituationStoreTest extends AbstractTransactionalJUnit4SpringContextTests {

	private JPASituationStore _situationStore;
    
	@PersistenceContext
    private EntityManager _entityManager;
	
	@Inject
	private EntityManagerFactory entityManagerFactory;
    
    @Before
    public void init() throws NamingException {
        this._situationStore = new JPASituationStore();
        this._situationStore.setEntityManager(this._entityManager);
        
        this._situationStore.setUserTransaction(new UserTransaction() {
			
			@Override
			public void setTransactionTimeout(int arg0) throws SystemException {
			}
			
			@Override
			public void setRollbackOnly() throws IllegalStateException, SystemException {
			}
			
			@Override
			public void rollback() throws IllegalStateException, SecurityException, SystemException {
			}
			
			@Override
			public int getStatus() throws SystemException {
				return Status.STATUS_ACTIVE;
			}
			
			@Override
			public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException,
					RollbackException, SecurityException, SystemException {
				
			}
			
			@Override
			public void begin() throws NotSupportedException, SystemException {
			}
		});
        
        /*
        InitialContext initialContext = new InitialContext();
		initialContext.rebind(USER_TRANSACTION, new UserTransaction() {
			
			@Override
			public void setTransactionTimeout(int arg0) throws SystemException {
			}
			
			@Override
			public void setRollbackOnly() throws IllegalStateException, SystemException {
			}
			
			@Override
			public void rollback() throws IllegalStateException, SecurityException, SystemException {
			}
			
			@Override
			public int getStatus() throws SystemException {
				return Status.STATUS_ACTIVE;
			}
			
			@Override
			public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException,
					RollbackException, SecurityException, SystemException {
				
			}
			
			@Override
			public void begin() throws NotSupportedException, SystemException {
			}
		});
		*/
    }

    @Test
    public void getSituationNotFound() throws Exception {
        try {
            _situationStore.getSituation("1");
            Assert.fail("NoResultException expected");
        } catch (NoResultException noResultException) {
        }
    }

    @Test
    public void getSituationById() throws Exception {
        Situation situation = new Situation();
        situation.setId("getSituationNotFound");
        situation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(situation);
        assertEquals(situation, _situationStore.getSituation(situation.getId()));
    }

    @Test
    public void getSituationsByResolutionState() throws Exception {
        Situation openSituation = new Situation();
        openSituation.setId("openSituation");
        openSituation.setTimestamp(System.currentTimeMillis());
        openSituation.getProperties().put("resolutionState", ResolutionState.REOPENED.name());
        _entityManager.persist(openSituation);
        Situation closedSituation = new Situation();
        closedSituation.setId("closedSituation");
        closedSituation.setTimestamp(System.currentTimeMillis());
        closedSituation.getProperties().put("resolutionState", ResolutionState.RESOLVED.name());
        _entityManager.persist(closedSituation);

        java.util.List<Situation> situations = findSituationsByFilterBean(openSituation);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(openSituation, situations.get(0));
    }
    
    @Test
    public void getSituationsByUnresolvedResolutionState() throws Exception {
        Situation unresolvedSituation = new Situation();
        unresolvedSituation.setId("unresolvedSituation");
        unresolvedSituation.setTimestamp(System.currentTimeMillis());
        _entityManager.persist(unresolvedSituation);
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setResolutionState(ResolutionState.UNRESOLVED.name());
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.size());
        Assert.assertEquals(unresolvedSituation, situations.get(0));
    }

	@Test
	public void assignSituation() throws Exception {
		Situation situation = new Situation();
		situation.setId("assignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		_entityManager.persist(situation);
		Situation reload = _situationStore.getSituation(situation.getId());
		assertEquals(situation, reload);
		assertFalse(reload.getProperties().containsKey("assignedTo"));
		assertFalse(reload.getProperties().containsKey("resolutionState"));
		_situationStore.assignSituation(situation.getId(), "junit");
		reload = _situationStore.getSituation(situation.getId());
		assertEquals("junit",reload.getProperties().get("assignedTo"));
	}

	@Test
	public void closeSituationAndRemoveAssignment() throws Exception {
		Situation situation = new Situation();
		situation.setId("deassignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		_entityManager.persist(situation);
		_situationStore.assignSituation(situation.getId(), "junit");
		Situation reload = _situationStore.getSituation(situation.getId());
		assertEquals("junit",reload.getProperties().get("assignedTo"));
		_situationStore.closeSituation(situation.getId());
		reload = _situationStore.getSituation(situation.getId());
		assertFalse(reload.getProperties().containsKey("assignedTo"));
	}
    
	@Test
	public void closeSituationResetOpenResolution() throws Exception {
		Situation situation = new Situation();
		situation.setId("deassignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		_entityManager.persist(situation);
		_situationStore.assignSituation(situation.getId(), "junit");
		_situationStore.updateResolutionState(situation.getId(),IN_PROGRESS);
		Situation reload = _situationStore.getSituation(situation.getId());
		assertEquals("junit",reload.getProperties().get(SituationStore.ASSIGNED_TO_PROPERTY));
		_situationStore.closeSituation(situation.getId());
		reload = _situationStore.getSituation(situation.getId());
		assertFalse(reload.getProperties().containsKey(SituationStore.RESOLUTION_STATE_PROPERTY));
		assertFalse(reload.getProperties().containsKey(SituationStore.ASSIGNED_TO_PROPERTY));
	}
    
	@Test
	public void updateResolutionState() throws Exception {
		Situation situation = new Situation();
		situation.setId("updateResolutionState");
		situation.setTimestamp(System.currentTimeMillis());
		_entityManager.persist(situation);
		Situation reload = _situationStore.getSituation(situation.getId());
		assertFalse(reload.getProperties().containsKey(SituationStore.RESOLUTION_STATE_PROPERTY));
		_situationStore.updateResolutionState(situation.getId(),ResolutionState.IN_PROGRESS);
		reload = _situationStore.getSituation(situation.getId());
		assertEquals(ResolutionState.IN_PROGRESS.name(), reload.getProperties().get(SituationStore.RESOLUTION_STATE_PROPERTY));
	}
    
    private java.util.List<Situation> findSituationsByFilterBean(Situation openSituation) throws Exception {
        String resolutionState = openSituation.getProperties().get("resolutionState");
        SituationsQuery sitQuery = new SituationsQuery();
        sitQuery.setResolutionState(resolutionState);
        java.util.List<Situation> situations = _situationStore.getSituations(sitQuery);
        return situations;
    }

    /*
    private static class EntityManagerFactoryDelegate implements EntityManagerFactory {
        private final EntityManagerFactory delegate;

        EntityManagerFactoryDelegate(EntityManagerFactory delegate) {
            super();
            this.delegate = delegate;
        }

        public EntityManager createEntityManager() {
            return delegate.createEntityManager();
        }

        public EntityManager createEntityManager(@SuppressWarnings("rawtypes") Map map) {
            return delegate.createEntityManager(map);
        }

        public CriteriaBuilder getCriteriaBuilder() {
            return delegate.getCriteriaBuilder();
        }

        public Metamodel getMetamodel() {
            return delegate.getMetamodel();
        }

        public boolean isOpen() {
            return delegate.isOpen();
        }

        public void close() {
            delegate.close();
        }

        public Map<String, Object> getProperties() {
            return delegate.getProperties();
        }

        public Cache getCache() {
            return delegate.getCache();
        }

        public PersistenceUnitUtil getPersistenceUnitUtil() {
            return delegate.getPersistenceUnitUtil();
        }
    }
    */
}
