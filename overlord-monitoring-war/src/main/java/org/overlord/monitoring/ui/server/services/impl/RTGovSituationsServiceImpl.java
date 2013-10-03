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
import javax.inject.Inject;

import org.overlord.monitoring.ui.client.shared.beans.CallTraceBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean;
import org.overlord.monitoring.ui.client.shared.beans.TraceNodeBean;
import org.overlord.monitoring.ui.client.shared.exceptions.UiException;
import org.overlord.monitoring.ui.server.i18n.Messages;
import org.overlord.monitoring.ui.server.services.ISituationsServiceImpl;
import org.overlord.rtgov.active.collection.ActiveCollectionContext;
import org.overlord.rtgov.active.collection.predicate.Predicate;
//import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.call.trace.CallTraceService;

/**
 * Concrete implementation of the faults service using RTGov situations.
 *
 */
@ApplicationScoped
@Alternative
public class RTGovSituationsServiceImpl implements ISituationsServiceImpl {

	private static volatile Messages i18n = new Messages();

	private SituationRepository _repository=null;
	
	@Inject
	private CallTraceService _callTraceService=null;
	
	//@Inject
	//private ActivityServer _activityServer=null;

    /**
     * Constructor.
     */
    public RTGovSituationsServiceImpl() {
    	_repository = new SituationRepository();
    	//_callTraceService.setActivityServer(_activityServer);
    }


    /**
     * @see org.overlord.monitoring.ui.server.services.ISituationsServiceImpl#search(org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean, int)
     */
    @Override
    public SituationResultSetBean search(SituationsFilterBean filters, int page) throws UiException {
        SituationResultSetBean rval = new SituationResultSetBean();
        ArrayList<SituationSummaryBean> situations = new ArrayList<SituationSummaryBean>();

        try {
        	getHistorySituationList(filters, situations);
        } catch (Exception e) {
        	throw new UiException("Failed to get situation list", e); //$NON-NLS-1$
        }

        rval.setSituations(situations);
        rval.setItemsPerPage(situations.size());
        rval.setStartIndex(0);
        rval.setTotalResults(situations.size());

        return (rval);
    }

    /**
     * This method builds a list of situations, associated with the supplied filter,
     * from the 'situations' database.
     *
     * @param filters The filter
     * @param situations The result list
     * @throws Exception Failed to get situations list
     */
    protected void getHistorySituationList(SituationsFilterBean filters, java.util.List<SituationSummaryBean> situations) throws Exception {
    	java.util.List<Situation> results=_repository.getSituations(filters);

    	for (Situation item : results) {
        	situations.add(getSituationBean(item));
        }
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.ISituationsServiceImpl#getService(java.lang.String)
     */
    @Override
    public SituationBean get(String situationId) throws UiException {
    	SituationBean ret=null;
    	
    	try {
	    	Situation situation=_repository.getSituation(situationId);

	    	if (situation == null) {
	            throw new UiException(i18n.format("RTGovSituationsServiceImpl.SitNotFound", situationId)); //$NON-NLS-1$
	    	}

	    	ret = getSituationBean(situation);
	    	
	        CallTraceBean callTrace = createMockCallTrace();
	        ret.setCallTrace(callTrace);

    	} catch (UiException uie) {
    		throw uie;

    	} catch (Exception e) {
    		throw new UiException("Failed to retrieve situation", e); //$NON-NLS-1$
    	}
    	
    	return (ret);
    }

    /**
     * This method retrieves the call trace for the supplied situation.
     * 
     * @param situation The situation
     * @return The call trace
     */
    protected CallTraceBean getCallTrace(Situation situation) {
        CallTraceBean ret = new CallTraceBean();
    	
        return (ret);
    }
    
    /**
     * Creates a mock call trace!
     */
    protected CallTraceBean createMockCallTrace() {
        CallTraceBean callTrace = new CallTraceBean();

        /*
        TraceNodeBean rootNode = createTraceNode("Success", "urn:switchyard:parent", "submitOrder", 47, 100); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        callTrace.getTasks().add(rootNode);

        TraceNodeBean childNode = createTraceNode("Success", "urn:switchyard:application", "lookupItem", 10, 55); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        rootNode.getTasks().add(childNode);
        TraceNodeBean leafNode = createTraceNode("Success", null, null, 3, 30); //$NON-NLS-1$
        leafNode.setDescription("Information: Found the item."); //$NON-NLS-1$
        childNode.getTasks().add(leafNode);
        leafNode = createTraceNode("Success", null, null, 7, 70); //$NON-NLS-1$
        leafNode.setDescription("Information: Secured the item."); //$NON-NLS-1$
        childNode.getTasks().add(leafNode);

        childNode = createTraceNode("Success", "urn:switchyard:application", "deliver", 8, 44); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        rootNode.getTasks().add(childNode);
        leafNode = createTraceNode("Success", null, null, 4, 100); //$NON-NLS-1$
        leafNode.setDescription("Information: Delivering the order."); //$NON-NLS-1$
        childNode.getTasks().add(leafNode);
*/

        return callTrace;
    }

    /**
     * Creates a single trace node.
     */
    protected TraceNodeBean createTraceNode(String status, String iface, String op, long duration, int percentage) {
        TraceNodeBean rootNode = new TraceNodeBean();
        rootNode.setStatus(status);
        rootNode.setIface(iface);
        rootNode.setOperation(op);
        rootNode.setDuration(duration);
        rootNode.setPercentage(percentage);
        return rootNode;
    }

    /**
     * Get situation summary from the original situation.
     *
     * @param situation The situation
     * @return The summary
     */
    protected static SituationBean getSituationBean(Situation situation) {
    	SituationBean ret=new SituationBean();

    	ret.setSituationId(situation.getId());
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
