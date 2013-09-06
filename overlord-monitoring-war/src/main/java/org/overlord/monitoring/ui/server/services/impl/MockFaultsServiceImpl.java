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

import org.overlord.monitoring.ui.client.shared.beans.FaultBean;
import org.overlord.monitoring.ui.client.shared.beans.FaultResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.FaultSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.FaultsFilterBean;
import org.overlord.monitoring.ui.client.shared.exceptions.UiException;
import org.overlord.monitoring.ui.server.services.IFaultsServiceImpl;

/**
 * Concrete implementation of the faults service.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
@Alternative
public class MockFaultsServiceImpl implements IFaultsServiceImpl {

    /**
     * Constructor.
     */
    public MockFaultsServiceImpl() {
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.IFaultsServiceImpl#search(org.overlord.monitoring.ui.client.shared.beans.FaultsFilterBean, int)
     */
    @Override
    public FaultResultSetBean search(FaultsFilterBean filters, int page) throws UiException {
        return createMockResponse();
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.IFaultsServiceImpl#getService(java.lang.String)
     */
    @Override
    public FaultBean get(String uuid) throws UiException {
        return null;
    }

    /**
     * Mock response data.
     */
    protected FaultResultSetBean createMockResponse() {
        FaultResultSetBean rval = new FaultResultSetBean();
        ArrayList<FaultSummaryBean> faults = new ArrayList<FaultSummaryBean>();
        rval.setFaults(faults);
        rval.setItemsPerPage(20);
        rval.setStartIndex(0);
        rval.setTotalResults(2);

        FaultSummaryBean fault = new FaultSummaryBean();
        fault.setServiceName("CreateApplicationWebservice"); //$NON-NLS-1$
        fault.setFaultId("1"); //$NON-NLS-1$
        fault.setFaultReason("cvc-complex-type.2.4.a: Invalid content was found starting with element 'brand'. One of '{\"\":brand}' is expected."); //$NON-NLS-1$
        fault.setFaultTime(new Date());
        faults.add(fault);

        fault = new FaultSummaryBean();
        fault.setServiceName("CreateQuoteWebservice"); //$NON-NLS-1$
        fault.setFaultId("2"); //$NON-NLS-1$
        fault.setFaultReason("cvc-complex-type.2.4.a: Invalid content was found starting with element 'xyz'. One of '{\"\":pood}' is expected."); //$NON-NLS-1$
        fault.setFaultTime(new Date());
        faults.add(fault);

        return rval;
    }

}
