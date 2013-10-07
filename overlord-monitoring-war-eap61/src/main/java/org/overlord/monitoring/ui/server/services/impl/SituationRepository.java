/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.monitoring.ui.server.services.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean;
import org.overlord.monitoring.ui.server.i18n.Messages;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.Situation.Severity;

/**
 * This class provides access to the situations db.
 *
 */
public class SituationRepository {

    private static final String OVERLORD_RTGOV_SITUATIONS = "overlord-rtgov-situations"; //$NON-NLS-1$
    private static volatile Messages i18n = new Messages();

    private EntityManagerFactory _entityManagerFactory=null;

    private static final Logger LOG=Logger.getLogger(SituationRepository.class.getName());

    /**
     * The situation repository constructor.
     */
    public SituationRepository() {
    	init();
    }

    /**
     * Initialize the situation repository.
     */
    protected void init() {
        _entityManagerFactory = Persistence.createEntityManagerFactory(OVERLORD_RTGOV_SITUATIONS);
    }

    /**
     * This method returns an entity manager.
     *
     * @return The entity manager
     */
    protected EntityManager getEntityManager() {
        return (_entityManagerFactory.createEntityManager());
    }

    /**
     * This method closes the supplied entity manager.
     *
     * @param em The entity manager
     */
    protected void closeEntityManager(EntityManager em) {
        if (em != null) {
            em.close();
        }
    }

    /**
     * This method returns the situation associated with the supplied id.
     *
     * @param id The id
     * @return The situation, or null if not found
     * @throws Exception Failed to get situation
     */
    public Situation getSituation(String id) throws Exception {
        if (LOG.isLoggable(Level.FINEST)) {
            LOG.finest(i18n.format("SituationsRepository.GetSit", id)); //$NON-NLS-1$
        }

        EntityManager em=getEntityManager();

        Situation ret=null;

        try {
            ret=(Situation)em.createQuery("SELECT sit FROM Situation sit " //$NON-NLS-1$
                                +"WHERE sit.id = '"+id+"'") //$NON-NLS-1$ //$NON-NLS-2$
                                .getSingleResult();

            // TODO: Temporary workaround until Situation model changed to use eager fetch
            if (ret != null) {
                loadSituation(ret);
            }

            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest(i18n.format("SituationsRepository.Result", ret)); //$NON-NLS-1$
            }
        } finally {
            closeEntityManager(em);
        }

        return (ret);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public java.util.List<Situation> getSituations(SituationsFilterBean filter) throws Exception {
        java.util.List<Situation> ret=null;

        EntityManager em=getEntityManager();

        try {
        	// Build the query string
        	StringBuffer queryString=new StringBuffer();

        	if (filter.getSeverity() != null && filter.getSeverity().trim().length() > 0) {
        		queryString.append("sit.severity = :severity "); //$NON-NLS-1$
        	}

        	if (filter.getType() != null && filter.getType().trim().length() > 0) {
        		if (queryString.length() > 0) {
        			queryString.append("AND "); //$NON-NLS-1$
        		}
        		queryString.append("sit.type = '"+filter.getType()+"' ");  //$NON-NLS-1$//$NON-NLS-2$
        	}

        	if (filter.getTimestampFrom() != null) {
        		if (queryString.length() > 0) {
        			queryString.append("AND "); //$NON-NLS-1$
        		}
        		queryString.append("sit.timestamp >= "+filter.getTimestampFrom().getTime()+" ");  //$NON-NLS-1$//$NON-NLS-2$
        	}

        	if (filter.getTimestampTo() != null) {
        		if (queryString.length() > 0) {
        			queryString.append("AND "); //$NON-NLS-1$
        		}
        		queryString.append("sit.timestamp <= "+filter.getTimestampTo().getTime()+" ");  //$NON-NLS-1$//$NON-NLS-2$
        	}

        	if (queryString.length() > 0) {
        		queryString.insert(0, "WHERE "); //$NON-NLS-1$
        	}

        	queryString.insert(0, "SELECT sit from Situation sit "); //$NON-NLS-1$

        	Query query=em.createQuery(queryString.toString());

        	if (filter.getSeverity() != null && filter.getSeverity().trim().length() > 0) {
        		String severityName=Character.toUpperCase(filter.getSeverity().charAt(0))
        						+filter.getSeverity().substring(1);
        		Severity severity=Severity.valueOf(severityName);

        		query.setParameter("severity", severity); //$NON-NLS-1$
        	}

            ret = query.getResultList();

            // TODO: Temporary workaround until Situation model changed to use eager fetch
            for (Situation sit : ret) {
                loadSituation(sit);
            }

            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest(i18n.format("SituationsRepository.SitResult", ret)); //$NON-NLS-1$
            }
        } finally {
            closeEntityManager(em);
        }

        return (ret);
    }

    private void loadSituation(final Situation sit) {
        try {
                for (@SuppressWarnings("unused") Context c : sit.getContext()) {
                }
                for (@SuppressWarnings("unused") Object val : sit.getProperties().values()) {
                }
        } catch (Throwable t) {
                // Ignore - may be due to change in API post ER4
        }
    }

}
