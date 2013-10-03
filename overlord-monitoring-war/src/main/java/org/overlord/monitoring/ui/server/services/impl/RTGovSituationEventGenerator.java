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

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.RequestDispatcher;
import org.overlord.monitoring.ui.client.shared.beans.SituationEventBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean;
import org.overlord.monitoring.ui.server.services.ISituationEventGenerator;
import org.overlord.monitoring.ui.server.services.impl.RTGovSituationsServiceImpl.SituationsFilterPredicate;
import org.overlord.rtgov.active.collection.ActiveChangeListener;
import org.overlord.rtgov.active.collection.ActiveCollection;
import org.overlord.rtgov.active.collection.ActiveCollectionListener;
import org.overlord.rtgov.active.collection.ActiveCollectionManager;
import org.overlord.rtgov.active.collection.ActiveCollectionManagerAccessor;
import org.overlord.rtgov.active.collection.ActiveList;
import org.overlord.rtgov.active.collection.predicate.Predicate;
import org.overlord.rtgov.analytics.situation.Situation;

/**
 * Randomly generates situation events.
 * @author eric.wittmann@redhat.com
 */
@Singleton
@Alternative
public class RTGovSituationEventGenerator implements ISituationEventGenerator, ActiveChangeListener {

	private static final String SITUATIONS = "Situations"; //$NON-NLS-1$

	@Inject
    private RequestDispatcher _dispatcher;

	private ActiveCollectionManager _acmManager=null;
	private ActiveList _situations=null;

	/**
     * Constructor.
     */
    public RTGovSituationEventGenerator() {
    }

	@Override
	public void start() {
    	_acmManager = ActiveCollectionManagerAccessor.getActiveCollectionManager();

    	_acmManager.addActiveCollectionListener(new ActiveCollectionListener() {

			@Override
			public void registered(ActiveCollection ac) {
				if (ac.getName().equals(SITUATIONS)) {
					synchronized (SITUATIONS) {
						if (_situations == null) {
					    	_situations = (ActiveList)ac;
					    	_situations.addActiveChangeListener(RTGovSituationEventGenerator.this);		
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
		    		_situations.addActiveChangeListener(RTGovSituationEventGenerator.this);	
		    	}
			}
		}
	}

	@Override
	public void inserted(Object key, Object value) {
		if (value instanceof Situation) {
			SituationEventBean event=RTGovSituationsUtil.getSituationEventBean((Situation)value);
			
            MessageBuilder.createMessage()
		            .toSubject("SitWatch") //$NON-NLS-1$
		            .signalling()
		            .with("situation", event) //$NON-NLS-1$
		            .noErrorHandling()
		            .sendNowWith(_dispatcher);
		}
	}

	@Override
	public void updated(Object key, Object value) {
	}

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
        Predicate predicate=new SituationsFilterPredicate(filters);

        ActiveCollection acol=_acmManager.create(filters.toString(), _situations, predicate, null);

        for (Object item : acol) {
        	if (item instanceof Situation) {
        		situations.add(RTGovSituationsUtil.getSituationBean((Situation)item));
        	}
        }
    }
}
