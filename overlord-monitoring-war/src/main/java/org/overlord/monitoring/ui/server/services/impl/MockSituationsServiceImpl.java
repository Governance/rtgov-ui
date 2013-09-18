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

/**
 * Concrete implementation of the faults service.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
@Alternative
public class MockSituationsServiceImpl implements ISituationsServiceImpl {

    /**
     * Constructor.
     */
    public MockSituationsServiceImpl() {
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.ISituationsServiceImpl#search(org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean, int)
     */
    @Override
    public SituationResultSetBean search(SituationsFilterBean filters, int page) throws UiException {
        return createMockResponse();
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.ISituationsServiceImpl#getService(java.lang.String)
     */
    @Override
    public SituationBean get(String situationId) throws UiException {
        throw new UiException("Not implemented."); //$NON-NLS-1$
    }

    /**
     * Mock response data.
     */
    protected SituationResultSetBean createMockResponse() {
        SituationResultSetBean rval = new SituationResultSetBean();
        ArrayList<SituationSummaryBean> situations = new ArrayList<SituationSummaryBean>();
        rval.setSituations(situations);
        rval.setItemsPerPage(20);
        rval.setStartIndex(0);
        rval.setTotalResults(2);

        SituationSummaryBean situation = new SituationSummaryBean();
        situation.setSituationId("1"); //$NON-NLS-1$
        situation.setSeverity("critical"); //$NON-NLS-1$
        situation.setType("Rate Limit Exceeded"); //$NON-NLS-1$
        situation.setSubject("{urn:namespace}ImportantService|VeryImportantOperation"); //$NON-NLS-1$
        situation.setTimestamp(new Date());
        situation.setDescription("Some description of the Situation goes here in this column so that it can be read by the user."); //$NON-NLS-1$
        situation.getProperties().put("Property-1", "Property one Value"); //$NON-NLS-1$ //$NON-NLS-2$
        situation.getProperties().put("Property-2", "Property two Value"); //$NON-NLS-1$ //$NON-NLS-2$
        situation.getProperties().put("Property-3", "Property three Value"); //$NON-NLS-1$ //$NON-NLS-2$
        situations.add(situation);

        situation = new SituationSummaryBean();
        situation.setSituationId("2"); //$NON-NLS-1$
        situation.setSeverity("high"); //$NON-NLS-1$
        situation.setType("SLA Violation"); //$NON-NLS-1$
        situation.setSubject("{urn:namespace}ServiceA|OperationB"); //$NON-NLS-1$
        situation.setTimestamp(new Date());
        situation.setDescription("Some description of the Situation goes here in this column so that it can be read by the user."); //$NON-NLS-1$
        situations.add(situation);

        situation = new SituationSummaryBean();
        situation.setSituationId("3"); //$NON-NLS-1$
        situation.setSeverity("high"); //$NON-NLS-1$
        situation.setType("SLA Violation"); //$NON-NLS-1$
        situation.setSubject("{urn:namespace}ServiceA|OperationB"); //$NON-NLS-1$
        situation.setTimestamp(new Date());
        situation.setDescription("Some description of the Situation goes here in this column so that it can be read by the user."); //$NON-NLS-1$
        situation.getProperties().put("Property-1", "Property one Value"); //$NON-NLS-1$ //$NON-NLS-2$
        situation.getProperties().put("Property-2", "Property two Value"); //$NON-NLS-1$ //$NON-NLS-2$
        situations.add(situation);

        situation = new SituationSummaryBean();
        situation.setSituationId("4"); //$NON-NLS-1$
        situation.setSeverity("low"); //$NON-NLS-1$
        situation.setType("Rate Limit Approaching"); //$NON-NLS-1$
        situation.setSubject("{urn:namespace}SomeService|AnotherOperation"); //$NON-NLS-1$
        situation.setTimestamp(new Date());
        situation.setDescription("Some description of the Situation goes here in this column so that it can be read by the user."); //$NON-NLS-1$
        situations.add(situation);

        return rval;
    }

}
