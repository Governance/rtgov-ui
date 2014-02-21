package org.overlord.rtgov.ui.server.services.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.overlord.rtgov.ui.client.shared.beans.ResolutionState.IN_PROGRESS;
import static org.overlord.rtgov.ui.server.services.impl.RTGovRepository.ASSIGNED_TO_PROPERTY;
import static org.overlord.rtgov.ui.server.services.impl.RTGovRepository.RESOLUTION_STATE_PROPERTY;

import java.util.Map;

import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.ui.client.shared.beans.ResolutionState;
import org.overlord.rtgov.ui.client.shared.beans.SituationsFilterBean;
import org.overlord.rtgov.ui.server.services.impl.RTGovRepository.SituationsResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration
public class RTGovRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    private static final String USER_TRANSACTION = "java:comp/UserTransaction";
	RTGovRepository rtGovRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Inject
    EntityManagerFactory entityManagerFactory;

    @Before
    public void init() throws NamingException {
        this.rtGovRepository = new RTGovRepository(new EntityManagerFactoryDelegate(this.entityManagerFactory) {
            @Override
            public EntityManager createEntityManager() {
                return entityManager;
            }

            @Override
            public EntityManager createEntityManager(@SuppressWarnings("rawtypes") Map map) {
                return entityManager;
            }
        });
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
    }

    @Test
    public void getSituationNotFound() throws Exception {
        try {
            rtGovRepository.getSituation("1");
            Assert.fail("NoResultException expected");
        } catch (NoResultException noResultException) {
        }
    }

    @Test
    public void getSituationById() throws Exception {
        Situation situation = new Situation();
        situation.setId("getSituationNotFound");
        situation.setTimestamp(System.currentTimeMillis());
        entityManager.persist(situation);
        assertEquals(situation, rtGovRepository.getSituation(situation.getId()));
    }

    @Test
    public void getSituationsByResolutionState() throws Exception {
        Situation openSituation = new Situation();
        openSituation.setId("openSituation");
        openSituation.setTimestamp(System.currentTimeMillis());
        openSituation.getProperties().put("resolutionState", ResolutionState.REOPENED.name());
        entityManager.persist(openSituation);
        Situation closedSituation = new Situation();
        closedSituation.setId("closedSituation");
        closedSituation.setTimestamp(System.currentTimeMillis());
        closedSituation.getProperties().put("resolutionState", ResolutionState.RESOLVED.name());
        entityManager.persist(closedSituation);

        SituationsResult situations = findSituationsByFilterBean(openSituation);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.getTotalCount());
        Assert.assertEquals(openSituation, situations.getSituations().get(0));
    }
    
    @Test
    public void getSituationsByUnresolvedResolutionState() throws Exception {
        Situation unresolvedSituation = new Situation();
        unresolvedSituation.setId("unresolvedSituation");
        unresolvedSituation.setTimestamp(System.currentTimeMillis());
        entityManager.persist(unresolvedSituation);
        SituationsFilterBean situationsFilterBean = new SituationsFilterBean();
        situationsFilterBean.setResolutionState(ResolutionState.UNRESOLVED.name());
        SituationsResult situations = rtGovRepository.getSituations(situationsFilterBean, 1, 50, null, true);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.getTotalCount());
        Assert.assertEquals(unresolvedSituation, situations.getSituations().get(0));
    }

	@Test
	public void assignSituation() throws Exception {
		Situation situation = new Situation();
		situation.setId("assignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		entityManager.persist(situation);
		Situation reload = rtGovRepository.getSituation(situation.getId());
		assertEquals(situation, reload);
		assertFalse(reload.getProperties().containsKey("assignedTo"));
		assertFalse(reload.getProperties().containsKey("resolutionState"));
		rtGovRepository.assignSituation(situation.getId(), "junit");
		reload = rtGovRepository.getSituation(situation.getId());
		assertEquals("junit",reload.getProperties().get("assignedTo"));
	}
    
	@Test
	public void closeSituationAndRemoveAssignment() throws Exception {
		Situation situation = new Situation();
		situation.setId("deassignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		entityManager.persist(situation);
		rtGovRepository.assignSituation(situation.getId(), "junit");
		Situation reload = rtGovRepository.getSituation(situation.getId());
		assertEquals("junit",reload.getProperties().get("assignedTo"));
		rtGovRepository.closeSituation(situation.getId());
		reload = rtGovRepository.getSituation(situation.getId());
		assertFalse(reload.getProperties().containsKey("assignedTo"));
	}
    
	@Test
	public void closeSituationResetOpenResolution() throws Exception {
		Situation situation = new Situation();
		situation.setId("deassignSituation");
		situation.setTimestamp(System.currentTimeMillis());
		entityManager.persist(situation);
		rtGovRepository.assignSituation(situation.getId(), "junit");
		rtGovRepository.updateResolutionState(situation.getId(),IN_PROGRESS);
		Situation reload = rtGovRepository.getSituation(situation.getId());
		assertEquals("junit",reload.getProperties().get(ASSIGNED_TO_PROPERTY));
		rtGovRepository.closeSituation(situation.getId());
		reload = rtGovRepository.getSituation(situation.getId());
		assertFalse(reload.getProperties().containsKey(RESOLUTION_STATE_PROPERTY));
		assertFalse(reload.getProperties().containsKey(ASSIGNED_TO_PROPERTY));
	}
    
	@Test
	public void updateResolutionState() throws Exception {
		Situation situation = new Situation();
		situation.setId("updateResolutionState");
		situation.setTimestamp(System.currentTimeMillis());
		entityManager.persist(situation);
		Situation reload = rtGovRepository.getSituation(situation.getId());
		assertFalse(reload.getProperties().containsKey(RESOLUTION_STATE_PROPERTY));
		rtGovRepository.updateResolutionState(situation.getId(),ResolutionState.IN_PROGRESS);
		reload = rtGovRepository.getSituation(situation.getId());
		assertEquals(ResolutionState.IN_PROGRESS.name(), reload.getProperties().get(RTGovRepository.RESOLUTION_STATE_PROPERTY));
	}

    private SituationsResult findSituationsByFilterBean(Situation openSituation) throws Exception {
        String resolutionState = openSituation.getProperties().get("resolutionState");
        SituationsFilterBean situationsFilterBean = new SituationsFilterBean();
        situationsFilterBean.setResolutionState(resolutionState);
        SituationsResult situations = rtGovRepository.getSituations(situationsFilterBean, 1, 50, null, true);
        return situations;
    }

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
}
