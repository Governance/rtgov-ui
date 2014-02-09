package org.overlord.rtgov.ui.server.services.impl;

import static junit.framework.Assert.assertEquals;

import java.util.Map;

import javax.inject.Inject;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.metamodel.Metamodel;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.ui.client.shared.beans.SituationsFilterBean;
import org.overlord.rtgov.ui.server.services.impl.RTGovRepository.SituationsResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration
public class RTGovRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    RTGovRepository rtGovRepository;
    @PersistenceContext
    EntityManager entityManager;
    @Inject
    EntityManagerFactory entityManagerFactory;

    @Before
    public void init() {
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
        openSituation.getProperties().put("resolutionState", "Open");
        entityManager.persist(openSituation);
        Situation closedSituation = new Situation();
        closedSituation.setId("closedSituation");
        closedSituation.setTimestamp(System.currentTimeMillis());
        closedSituation.getProperties().put("resolutionState", "Closed");
        entityManager.persist(closedSituation);

        SituationsResult situations = findSituationsByFilterBean(openSituation);
        Assert.assertNotNull(situations);
        Assert.assertTrue(1 == situations.getTotalCount());
        Assert.assertEquals(openSituation, situations.getSituations().get(0));
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
