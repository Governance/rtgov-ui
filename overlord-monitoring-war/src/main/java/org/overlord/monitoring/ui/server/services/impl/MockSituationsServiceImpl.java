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

import org.overlord.monitoring.ui.client.shared.beans.CallTraceBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean;
import org.overlord.monitoring.ui.client.shared.beans.TraceNodeBean;
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

    /**
     * @see org.overlord.monitoring.ui.server.services.ISituationsServiceImpl#getService(java.lang.String)
     */
    @Override
    public SituationBean get(String situationId) throws UiException {
        SituationBean situation = new SituationBean();
        situation.setSituationId("1"); //$NON-NLS-1$
        situation.setSeverity("critical"); //$NON-NLS-1$
        situation.setType("Rate Limit Exceeded"); //$NON-NLS-1$
        situation.setSubject("{urn:namespace}ImportantService|VeryImportantOperation"); //$NON-NLS-1$
        situation.setTimestamp(new Date());
        situation.setDescription("Some description of the Situation goes here in this column so that it can be read by the user."); //$NON-NLS-1$
        situation.getProperties().put("Property-1", "Property one Value"); //$NON-NLS-1$ //$NON-NLS-2$
        situation.getProperties().put("Property-2", "Property two Value"); //$NON-NLS-1$ //$NON-NLS-2$
        situation.getProperties().put("Property-3", "Property three Value"); //$NON-NLS-1$ //$NON-NLS-2$
        situation.getContext().put("Context-1", "This is the value of the context 1 property."); //$NON-NLS-1$ //$NON-NLS-2$
        situation.getContext().put("Context-2", "This is the value of the context 2 property."); //$NON-NLS-1$ //$NON-NLS-2$

        CallTraceBean callTrace = createMockCallTrace();
        situation.setCallTrace(callTrace);

        return situation;
    }

    /**
     * Creates a mock call trace!
     */
    protected CallTraceBean createMockCallTrace() {
        CallTraceBean callTrace = new CallTraceBean();

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

}
