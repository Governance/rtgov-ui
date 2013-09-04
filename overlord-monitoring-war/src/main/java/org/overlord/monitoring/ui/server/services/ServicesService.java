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
package org.overlord.monitoring.ui.server.services;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.bus.server.annotations.Service;
import org.overlord.monitoring.ui.client.shared.beans.ComponentServiceResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.ComponentServiceSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean;
import org.overlord.monitoring.ui.client.shared.exceptions.UiException;
import org.overlord.monitoring.ui.client.shared.services.IServicesService;

/**
 * Concrete implementation of the services service. :)
 *
 * @author eric.wittmann@redhat.com
 */
@Service
public class ServicesService implements IServicesService {

    /**
     * Constructor.
     */
    public ServicesService() {
    }

    /**
     * @see org.overlord.monitoring.ui.client.shared.services.IServicesService#findServices(org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean, int)
     */
    @Override
    public ServiceResultSetBean findServices(ServicesFilterBean filters, int page) throws UiException {
        return createMockServicesResponse();
    }

    /**
     * @see org.overlord.monitoring.ui.client.shared.services.IServicesService#findComponentServices(org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean, int)
     */
    @Override
    public ComponentServiceResultSetBean findComponentServices(ServicesFilterBean filters, int page)
            throws UiException {
        return createMockComponentServicesResponse();
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IServicesService#getService(java.lang.String)
     */
    @Override
    public ServiceBean getService(String uuid) throws UiException {
        return null;
    }

    /**
     * Mock response data.
     */
    protected ServiceResultSetBean createMockServicesResponse() {
        ServiceResultSetBean rval = new ServiceResultSetBean();
        ArrayList<ServiceSummaryBean> services = new ArrayList<ServiceSummaryBean>();
        rval.setServices(services);
        rval.setItemsPerPage(20);
        rval.setStartIndex(0);
        rval.setTotalResults(2);

        ServiceSummaryBean service = new ServiceSummaryBean();
        service.setName("CreateApplicationWebservice"); //$NON-NLS-1$
        service.setApplication("Contract"); //$NON-NLS-1$
        service.setIface("{urn:jboss:demo:create-application}CreateApplicationPT"); //$NON-NLS-1$
        service.setBindings("SOAP, JMS"); //$NON-NLS-1$
        service.setAverageDuration(2837l);
        services.add(service);

        service = new ServiceSummaryBean();
        service.setName("CreateQuoteWebservice"); //$NON-NLS-1$
        service.setApplication("Contract"); //$NON-NLS-1$
        service.setIface("{urn:jboss:demo:create-application}CreateQuotePT"); //$NON-NLS-1$
        service.setBindings("SOAP"); //$NON-NLS-1$
        service.setAverageDuration(2837l);
        services.add(service);

        return rval;
    }

    /**
     * Mock response data.
     */
    protected ComponentServiceResultSetBean createMockComponentServicesResponse() {
        ComponentServiceResultSetBean rval = new ComponentServiceResultSetBean();
        List<ComponentServiceSummaryBean> services = new ArrayList<ComponentServiceSummaryBean>();
        rval.setServices(services);
        rval.setItemsPerPage(20);
        rval.setStartIndex(0);
        rval.setTotalResults(2);

        ComponentServiceSummaryBean service = new ComponentServiceSummaryBean();
        service.setName("CreateApplicationService"); //$NON-NLS-1$
        service.setApplication("Contract"); //$NON-NLS-1$
        service.setIface("org.jboss.demos.services.ICreateApplication"); //$NON-NLS-1$
        service.setImplementation("org.jboss.demos.services.impl.CreateApplicationService"); //$NON-NLS-1$
        service.setAverageDuration(2837l);
        services.add(service);

        service = new ComponentServiceSummaryBean();
        service.setName("CreateQuoteService"); //$NON-NLS-1$
        service.setApplication("Contract"); //$NON-NLS-1$
        service.setIface("org.jboss.demos.services.ICreateQuote"); //$NON-NLS-1$
        service.setImplementation("org.jboss.demos.services.impl.CreateQuoteService"); //$NON-NLS-1$
        service.setAverageDuration(2837l);
        services.add(service);

        return rval;
    }

}
