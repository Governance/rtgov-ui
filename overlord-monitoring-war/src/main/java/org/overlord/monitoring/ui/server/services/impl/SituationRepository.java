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
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.analytics.situation.Situation.Severity;

/**
 * This class provides access to the situations db.
 *
 */
public class SituationRepository {

    private static final String OVERLORD_RTGOV_SITUATIONS = "overlord-rtgov-situations";

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
            LOG.finest("Get situation with id="+id);
        }

        EntityManager em=getEntityManager();
        
        Situation ret=null;
        
        try {
            ret=(Situation)em.createQuery("SELECT sit FROM Situation sit "
                                +"WHERE sit.id = '"+id+"'")
                                .getSingleResult();
            
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Result="+ret);
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
        		queryString.append("sit.severity = :severity ");
        	}
        	
        	if (filter.getType() != null && filter.getType().trim().length() > 0) {
        		if (queryString.length() > 0) {
        			queryString.append("AND ");
        		}
        		queryString.append("sit.type = '"+filter.getType()+"' ");
        	}
        	
        	if (filter.getTimestampFrom() != null) {
        		if (queryString.length() > 0) {
        			queryString.append("AND ");
        		}
        		queryString.append("sit.timestamp >= "+filter.getTimestampFrom().getTime()+" ");
        	}
        	
        	if (filter.getTimestampTo() != null) {
        		if (queryString.length() > 0) {
        			queryString.append("AND ");
        		}
        		queryString.append("sit.timestamp <= "+filter.getTimestampTo().getTime()+" ");
        	}
        	
        	if (queryString.length() > 0) {
        		queryString.insert(0, "WHERE ");
        	}
        	
        	queryString.insert(0, "SELECT sit from Situation sit ");
        	
        	Query query=em.createQuery(queryString.toString());
        	
        	if (filter.getSeverity() != null && filter.getSeverity().trim().length() > 0) {
        		String severityName=Character.toUpperCase(filter.getSeverity().charAt(0))
        						+filter.getSeverity().substring(1);
        		Severity severity=Severity.valueOf(severityName);
        		
        		query.setParameter("severity", severity);
        	}
        	
            ret = (java.util.List<Situation>)query.getResultList();
            
            // TODO: Temporary workaround until Situation model changed to use eager fetch
            try {
	            for (Situation sit : ret) {
	            	for (Context c : sit.getContext()) {
	            		
	            	}
	            	for (Object val : sit.getProperties().values()) {
	            		
	            	}
	            }
            } catch (Throwable t) {
            	// Ignore - may be due to change in API post ER4
            }
            		
            if (LOG.isLoggable(Level.FINEST)) {
                LOG.finest("Situations result="+ret);
            }
        } finally {
            closeEntityManager(em);
        }

        return (ret);        
    }

}
