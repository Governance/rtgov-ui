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
package org.overlord.rtgov.ui.provider.situations;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.overlord.rtgov.active.collection.ActiveChangeListener;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionListener;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionManagerAccessor;
import org.overlord.rtgov.active.collection.ActiveList;
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
import org.overlord.rtgov.ui.client.model.CallTraceBean;
import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.ResolutionState;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationEventBean;
import org.overlord.rtgov.ui.client.model.SituationSummaryBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.client.model.TraceNodeBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.provider.ServicesProvider;
import org.overlord.rtgov.ui.provider.SituationEventListener;
import org.overlord.rtgov.ui.provider.SituationsProvider;
import org.overlord.rtgov.ui.provider.situations.RTGovRepository;

/**
 * Concrete implementation of the faults service using RTGov situations.
 *
 */
public class RTGovSituationsProvider implements SituationsProvider, ActiveChangeListener {

	private static final String PROVIDER_NAME = "rtgov"; //$NON-NLS-1$
	
	// Active collection name
	private static final String SITUATIONS = "Situations"; //$NON-NLS-1$

	private static volatile Messages i18n = new Messages();
	
	private RTGovRepository _repository;

	@Inject
	private CallTraceService _callTraceService;

	@Inject
	private ActivityServer _activityServer;

	@Inject
	private Instance<ServicesProvider> _providers;
	
	private java.util.List<SituationEventListener> _listeners=new java.util.ArrayList<SituationEventListener>();

	private ActiveList _situations;
	private ActiveCollectionManager _acmManager;
	
    /**
     * Constructor.
     */
    public RTGovSituationsProvider() {
    }

    @PostConstruct
    public void init() {
    	_repository = new RTGovRepository();
    	_callTraceService.setActivityServer(_activityServer);    

    	_acmManager = ActiveCollectionManagerAccessor.getActiveCollectionManager();

    	_acmManager.addActiveCollectionListener(new ActiveCollectionListener() {

			@Override
			public void registered(ActiveCollection ac) {
				if (ac.getName().equals(SITUATIONS)) {
					synchronized (SITUATIONS) {
						if (_situations == null) {
					    	_situations = (ActiveList)ac;
					    	_situations.addActiveChangeListener(RTGovSituationsProvider.this);		
						}
					}
				}
			}

			@Override
			public void unregistered(ActiveCollection ac) {
			}
    		
    	});
    	
    	// TEMPORARY WORKAROUND: Currently hen active collection listener is registered, existing
    	// collections are not notified to the listener, thus potentially causing a situation where
    	// a collection may be missed if registered prior to the listener being established (RTGOV-286).
		synchronized (SITUATIONS) {
			if (_situations == null) {
		    	_situations = (ActiveList)_acmManager.getActiveCollection(SITUATIONS);
		    	if (_situations != null) {
		    		_situations.addActiveChangeListener(RTGovSituationsProvider.this);	
		    	}
			}
		}
    }

    /**
     * {@inheritDoc}
     */
	public String getName() {
		return PROVIDER_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addSituationEventListener(SituationEventListener l) {
		synchronized (_listeners) {
			_listeners.add(l);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void removeSituationEventListener(SituationEventListener l) {
		synchronized (_listeners) {
			_listeners.remove(l);
		}		
	}
	
	/**
	 * This method fires the situation event to any registered listeners.
	 * 
	 * @param event The situation event
	 */
	protected void fireSituationEvent(SituationEventBean event) {
		synchronized (_listeners) {
			for (int i=0; i < _listeners.size(); i++) {
				_listeners.get(i).onSituationEvent(event);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
    public java.util.List<SituationSummaryBean> search(SituationsFilterBean filters) throws UiException {
        ArrayList<SituationSummaryBean> situations = new ArrayList<SituationSummaryBean>();

        try {
	    	java.util.List<Situation> results=_repository.getSituations(filters);
	
	    	for (Situation item : results) {
	        	situations.add(RTGovSituationsUtil.getSituationBean(item));
	        }
        } catch (Exception e) {
        	throw new UiException(e);
        }

        return (situations);
    }

    /**
     * @see org.overlord.rtgov.ui.server.services.ISituationsServiceImpl#getService(java.lang.String)
     */
    @Override
    public SituationBean get(String situationId) throws UiException {
    	SituationBean ret=null;

    	try {
	    	Situation situation=_repository.getSituation(situationId);

	    	if (situation == null) {
	            throw new UiException(i18n.format("RTGovSituationsProvider.SitNotFound", situationId)); //$NON-NLS-1$
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
     * @see org.overlord.rtgov.ui.server.services.ISituationsServiceImpl#resubmit(java.lang.String, java.lang.String)
     */
    @Override
    public void resubmit(String situationId, MessageBean message) throws UiException {
    	
    	try {
	    	Situation sit=_repository.getSituation(situationId);

	    	if (sit == null) {
	            throw new UiException(i18n.format("RTGovSituationsProvider.SitNotFound", situationId)); //$NON-NLS-1$
	    	}
	    	
			String parts[]=sit.getSubject().split("\\x7C");
			
			if (parts.length != 2) {
				throw new UiException(i18n.format("RTGovSituationsProvider.InvalidSubject", sit.getSubject(),
											parts.length));
			}
			
			String service=parts[0];
			String operation=parts[1];

	    	// Find service provider that can resubmit this message
	    	ServicesProvider provider=null;
	    	
	    	for (ServicesProvider sp : _providers) {
	    		if (sp.isServiceKnown(service)) {
	    			provider = sp;
	    			break;
	    		}
	    	}
	    	
	    	if (provider == null) {
	            throw new UiException(i18n.format("RTGovSituationsProvider.ResubmitProviderNotFound", situationId)); //$NON-NLS-1$
	    	}
	    	
	    	// TODO: Change to specify service, rather than situation - also possibly locate the
	    	// provider appropriate for the service rather than situation
	    	
	    	provider.resubmit(service, operation, message);

    	} catch (UiException uie) {
    		throw uie;

    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new UiException(i18n.format("RTGovSituationsProvider.ResubmitFailed", situationId+":"+e.getLocalizedMessage()), e); //$NON-NLS-1$
    	}
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inserted(Object key, Object value) {
		if (value instanceof Situation) {
			SituationEventBean event=RTGovSituationsUtil.getSituationEventBean((Situation)value);
			
			fireSituationEvent(event);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updated(Object key, Object value) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removed(Object key, Object value) {
	}

    /**
     * This method builds a list of situations, associated with the supplied filter,
     * from the 'situations' active collection.
     *
     * @param filters The filter
     * @param situations The result list
     * @throws Exception Failed to get situations list
     */
    protected void getActiveSituationList(SituationsFilterBean filters, java.util.List<SituationSummaryBean> situations) throws Exception {
        Predicate predicate=new org.overlord.rtgov.ui.provider.situations.SituationsFilterPredicate(filters);

        ActiveCollection acol=_acmManager.create(filters.toString(), _situations, predicate, null);

        for (Object item : acol) {
        	if (item instanceof Situation) {
        		situations.add(RTGovSituationsUtil.getSituationBean((Situation)item));
        	}
        }
    }

	@Override
	public void assign(String situationId,String userName) throws UiException {
		_repository.assignSituation(situationId, userName);
	}

	@Override
	public void close(String situationId) throws UiException {
		_repository.closeSituation(situationId);
	}

	@Override
	public void updateResolutionState(String situationId, ResolutionState resolutionState) {
		_repository.updateResolutionState(situationId, resolutionState);
	}
}
