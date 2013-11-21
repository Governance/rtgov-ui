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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.overlord.monitoring.ui.client.shared.beans.CallTraceBean;
import org.overlord.monitoring.ui.client.shared.beans.MessageBean;
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
import org.overlord.rtgov.activity.model.ActivityType;
import org.overlord.rtgov.activity.model.ActivityTypeId;
import org.overlord.rtgov.activity.model.Context;
import org.overlord.rtgov.activity.model.soa.RPCActivityType;
import org.overlord.rtgov.activity.server.ActivityServer;
import org.overlord.rtgov.analytics.situation.Situation;
import org.overlord.rtgov.call.trace.CallTraceService;
import org.overlord.rtgov.call.trace.model.Call;
import org.overlord.rtgov.call.trace.model.CallTrace;
import org.overlord.rtgov.call.trace.model.Task;
import org.overlord.rtgov.call.trace.model.TraceNode;

/**
 * Concrete implementation of the faults service using RTGov situations.
 *
 */
@ApplicationScoped
@Alternative
public class RTGovSituationsServiceImpl implements ISituationsServiceImpl {

	private static volatile Messages i18n = new Messages();

	private RTGovRepository _repository=null;

	@Inject
	private CallTraceService _callTraceService=null;

	@Inject
	private ActivityServer _activityServer=null;

	@Inject
	private ResubmitHandler _resubmitHandler=null;

    /**
     * Constructor.
     */
    public RTGovSituationsServiceImpl() {
    }

    @PostConstruct
    public void init() {
    	_repository = new RTGovRepository();
    	_callTraceService.setActivityServer(_activityServer);    	
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.ISituationsServiceImpl#search(org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean, int, java.lang.String, boolean)
     */
    @Override
    public SituationResultSetBean search(SituationsFilterBean filters, int page, String sortColumn,
            boolean ascending) throws UiException {
        
        // ******************************************************************************************
        // TODO implement sorting.  The value of sortColumn will be one of the values listed in
        // org.overlord.monitoring.ui.client.shared.beans.Constants
        // ******************************************************************************************
        
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
        	situations.add(RTGovSituationsUtil.getSituationBean(item));
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

	    	ret = RTGovSituationsUtil.getSituationBean(situation);
	    	
	    	MessageBean message = getMessage(situation);
	    	ret.setMessage(message);

	        CallTraceBean callTrace = getCallTrace(situation);
	        ret.setCallTrace(callTrace);

    	} catch (UiException uie) {
    		throw uie;

    	} catch (Exception e) {
    		throw new UiException("Failed to retrieve situation", e); //$NON-NLS-1$
    	}

    	return (ret);
    }

    /**
     * This method checks whether a request message exists for the supplied
     * situation and if so, returns a MessageBean to represent it's content.
     * 
     * @param situation The situation
     * @return The message, or null if not found
     * @throws UiException Failed to get message
     */
    protected MessageBean getMessage(Situation situation) throws UiException {
    	MessageBean ret=null;
    	
		for (ActivityTypeId id : situation.getActivityTypeIds()) {
			try {
    			ActivityType at=_repository.getActivityType(id);
    			
    			if (at instanceof RPCActivityType && ((RPCActivityType)at).isRequest()
    					&& ((RPCActivityType)at).getContent() != null) {
    				ret = new MessageBean();
    				ret.setContent(((RPCActivityType)at).getContent());
    				break;
    			}
			} catch (Exception e) {
	    		throw new UiException("Failed to get message for activity type id '"+id+"'", e);
			}
		}
		
    	return (ret);
    }
    
    /**
     * This method retrieves the call trace for the supplied situation.
     *
     * @param situation The situation
     * @return The call trace
     */
    protected CallTraceBean getCallTrace(Situation situation) throws UiException {
        CallTraceBean ret = new CallTraceBean();
        
        // Obtain call trace
        Context context=null;
        
        for (Context c : situation.getContext()) {
        	if (c.getType() == Context.Type.Conversation) {
        		context = c;
        		break;
        	}
        }
        
        if (context == null && situation.getContext().size() > 0) {
        	// If no conversation context available, then use any other
        	context = situation.getContext().iterator().next();
        }
        
        if (context != null) {
        	try {
        		CallTrace ct=_callTraceService.createCallTrace(context);
        		
        		if (ct != null) {
        			for (TraceNode tn : ct.getTasks()) {
        				ret.getTasks().add(createTraceNode(tn));
        			}
        			
        		}
        	} catch (Exception e) {
        		throw new UiException("Failed to get call trace for context '"+context+"'", e);
        	}
        }

        return (ret);
    }
    
    /**
     * This method creates a UI bean from the supplied trace node.
     * 
     * @param node The trace node
     * @return The trace node bean
     */
    protected TraceNodeBean createTraceNode(TraceNode node) {
    	TraceNodeBean ret=new TraceNodeBean();
    	
    	ret.setType(node.getClass().getSimpleName());
    	ret.setStatus(node.getStatus().name());
    	
    	if (node instanceof Task) {
    		Task task=(Task)node;
    		
    		ret.setDescription(task.getDescription());
    		
    	} else if (node instanceof Call) {
    		Call call=(Call)node;
    		
        	ret.setIface(call.getInterface());
            ret.setOperation(call.getOperation());
            ret.setDuration(call.getDuration());
            ret.setPercentage(call.getPercentage());
            ret.setComponent(call.getComponent());
            ret.setFault(call.getFault());
            ret.setPrincipal(call.getPrincipal());
            ret.setRequest(call.getRequest());
            ret.setResponse(call.getResponse());
            ret.setRequestLatency(call.getRequestLatency());
            ret.setResponseLatency(call.getResponseLatency());
            
            ret.setProperties(call.getProperties());
        	
        	for (TraceNode child : call.getTasks()) {
				ret.getTasks().add(createTraceNode(child));
        	}
    	}
    	
    	return (ret);
    }
    
    /**
     * @see org.overlord.monitoring.ui.server.services.ISituationsServiceImpl#resubmit(java.lang.String, java.lang.String)
     */
    @Override
    public void resubmit(String situationId, String message) throws UiException {
    	
    	try {
	    	Situation situation=_repository.getSituation(situationId);

	    	if (situation == null) {
	            throw new UiException(i18n.format("RTGovSituationsServiceImpl.SitNotFound", situationId)); //$NON-NLS-1$
	    	}

	    	_resubmitHandler.resubmit(situation, message);

    	} catch (UiException uie) {
    		throw uie;

    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new UiException(i18n.format("RTGovSituationsServiceImpl.ResubmitFailed", situationId+":"+e.getLocalizedMessage()), e); //$NON-NLS-1$
    	}

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
