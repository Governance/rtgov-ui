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

import java.util.ArrayList;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.overlord.monitoring.ui.client.shared.beans.SituationBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean;
import org.overlord.monitoring.ui.client.shared.exceptions.UiException;
import org.overlord.monitoring.ui.server.services.ISituationsServiceImpl;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionContext;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionManagerAccessor;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.predicate.Predicate;
import org.overlord.rtgov.analytics.situation.Situation;

/**
 * Concrete implementation of the faults service using RTGov situations.
 *
 */
@ApplicationScoped
@Alternative
public class RTGovSituationsServiceImpl implements ISituationsServiceImpl {

	private static final String SITUATIONS = "Situations";
	
	private ActiveCollectionManager _acmManager=null;
	private ActiveList _situations=null;
	
    /**
     * Constructor.
     */
    public RTGovSituationsServiceImpl() {
    	_acmManager = ActiveCollectionManagerAccessor.getActiveCollectionManager();
    	
    	// Get 'situations' active collection
    	_situations = (ActiveList)
                _acmManager.getActiveCollection(SITUATIONS);
    }

    
    /**
     * @see org.overlord.monitoring.ui.server.services.ISituationsServiceImpl#search(org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean, int)
     */
    @Override
    public SituationResultSetBean search(SituationsFilterBean filters, int page) throws UiException {
        SituationResultSetBean rval = new SituationResultSetBean();
        ArrayList<SituationSummaryBean> situations = new ArrayList<SituationSummaryBean>();
        
        Predicate predicate=new SituationsFilterPredicate(filters);
        
        ActiveCollection acol=_acmManager.create(filters.toString(), _situations, predicate, null);
        
        for (Object item : acol) {
        	if (item instanceof Situation) {
        		situations.add(getSummaryBean((Situation)item));
        	}
        }
        
        rval.setSituations(situations);
        rval.setItemsPerPage(situations.size());
        rval.setStartIndex(0);
        rval.setTotalResults(situations.size());
        
        return (rval);
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.ISituationsServiceImpl#getService(java.lang.String)
     */
    @Override
    public SituationBean get(String situationId) throws UiException {
        throw new UiException("Not implemented."); //$NON-NLS-1$
    }

    /**
     * Get situation summary from the original situation.
     * 
     * @param situation The situation
     * @return The summary
     */
    protected static SituationSummaryBean getSummaryBean(Situation situation) {
    	SituationSummaryBean ret=new SituationSummaryBean();
    	
    	ret.setSeverity(situation.getSeverity().name().toLowerCase());
    	ret.setType(situation.getType());
    	ret.setSubject(situation.getSubject());
    	ret.setTimestamp(new Date(situation.getTimestamp()));
    	ret.setDescription(situation.getDescription());
    	ret.getProperties().putAll(situation.getProperties());
    	
    	return (ret);
    }

    /**
     * This class provides the 'situations filter' based predicate implementation.
     *
     */
    public static class SituationsFilterPredicate extends Predicate {
    	
    	private SituationsFilterBean _filter=null;
    	
    	/**
    	 * The situations filter predicate constructor.
    	 * 
    	 * @param filter The filter
    	 */
    	public SituationsFilterPredicate(SituationsFilterBean filter) {
    		_filter = filter;
    	}

    	/**
    	 * {@inheritDoc}
    	 */
		@Override
		public boolean evaluate(ActiveCollectionContext context, Object item) {
			
			if (item instanceof Situation) {
				Situation situation=(Situation)item;
				
				if (_filter != null) {
					// Check severity
					if (_filter.getSeverity() != null
							&& _filter.getSeverity().trim().length() > 0
							&& !_filter.getSeverity().equalsIgnoreCase(situation.getSeverity().name())) {
						return (false);
					}
					
					// Check type
					if (_filter.getType() != null
							&& _filter.getType().trim().length() > 0
							&& !_filter.getType().equals(situation.getType())) {
						return (false);
					}
										
					// Check start date/time
					if (_filter.getTimestampFrom() != null
							&& situation.getTimestamp() < _filter.getTimestampFrom().getTime()) {
						return (false);
					}
					
					// Check end date/time
					if (_filter.getTimestampTo() != null
							&& situation.getTimestamp() > _filter.getTimestampTo().getTime()) {
						return (false);
					}
				}

				return (true);
			}
			
			return (false);
		}
    	
    }
}
