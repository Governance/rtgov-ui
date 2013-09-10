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
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.overlord.monitoring.ui.client.shared.beans.ReferenceBean;
import org.overlord.monitoring.ui.client.shared.beans.ReferenceResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.ReferenceSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.QName;
import org.overlord.monitoring.ui.client.shared.beans.ServiceBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean;
import org.overlord.monitoring.ui.client.shared.exceptions.UiException;
import org.overlord.monitoring.ui.server.services.IServicesServiceImpl;

/**
 * Concrete implementation of the services service. :)
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
@Alternative
public class MockServicesServiceImpl implements IServicesServiceImpl {

    /**
     * Constructor.
     */
    public MockServicesServiceImpl() {
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.IServicesServiceImpl#findServices(org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean, int, java.lang.String, boolean)
     */
    @Override
    public ServiceResultSetBean findServices(ServicesFilterBean filters, int page, String sortColumn,
            boolean ascending) throws UiException {
        ServiceResultSetBean rval = new ServiceResultSetBean();
        ArrayList<ServiceSummaryBean> services = new ArrayList<ServiceSummaryBean>();
        rval.setServices(services);
        rval.setItemsPerPage(20);
        rval.setStartIndex(0);
        rval.setTotalResults(2);
        
        ServiceSummaryBean service = new ServiceSummaryBean();
        service.setServiceId("1"); //$NON-NLS-1$
        service.setName("CreateApplicationWebservice"); //$NON-NLS-1$
        service.setApplication("Contract"); //$NON-NLS-1$
        service.setIface("{urn:jboss:demo:create-application}CreateApplicationPT"); //$NON-NLS-1$
        service.setBindings("SOAP, JMS"); //$NON-NLS-1$
        service.setAverageDuration(2837l);
        services.add(service);
        
        service = new ServiceSummaryBean();
        service.setServiceId("2"); //$NON-NLS-1$
        service.setName("CreateQuoteWebservice"); //$NON-NLS-1$
        service.setApplication("Contract"); //$NON-NLS-1$
        service.setIface("{urn:jboss:demo:create-application}CreateQuotePT"); //$NON-NLS-1$
        service.setBindings("SOAP"); //$NON-NLS-1$
        service.setAverageDuration(2837l);
        services.add(service);
        
        return rval;
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.IServicesServiceImpl#findReferences(org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean, int, java.lang.String, boolean)
     */
    @Override
    public ReferenceResultSetBean findReferences(ServicesFilterBean filters, int page,
            String sortColumn, boolean ascending) throws UiException {
        ReferenceResultSetBean rval = new ReferenceResultSetBean();
        List<ReferenceSummaryBean> services = new ArrayList<ReferenceSummaryBean>();
        rval.setServices(services);
        rval.setItemsPerPage(20);
        rval.setStartIndex(0);
        rval.setTotalResults(2);
        
        ReferenceSummaryBean reference = new ReferenceSummaryBean();
        reference.setReferenceId("1"); //$NON-NLS-1$
        reference.setName("CreateApplicationService"); //$NON-NLS-1$
        reference.setApplication("Contract"); //$NON-NLS-1$
        reference.setIface("org.jboss.demos.services.ICreateApplication"); //$NON-NLS-1$
        reference.setBindings("SOAP, JMS"); //$NON-NLS-1$
        reference.setAverageDuration(2837l);
        services.add(reference);
        
        reference = new ReferenceSummaryBean();
        reference.setReferenceId("2"); //$NON-NLS-1$
        reference.setName("CreateQuoteService"); //$NON-NLS-1$
        reference.setApplication("Contract"); //$NON-NLS-1$
        reference.setIface("org.jboss.demos.services.ICreateQuote"); //$NON-NLS-1$
        reference.setBindings("SOAP"); //$NON-NLS-1$
        reference.setAverageDuration(2837l);
        services.add(reference);
        
        return rval;
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IServicesServiceImpl#getService(java.lang.String)
     */
    @Override
    public ServiceBean getService(String uuid) throws UiException {
        ServiceBean service = new ServiceBean();
        service.setServiceId("1"); //$NON-NLS-1$
        service.setName(new QName("urn:jboss:demo:services", "CreateApplicationWebservice")); //$NON-NLS-1$ //$NON-NLS-2$
        service.setApplication(new QName("urn:jboss:demos:applications", "Contract")); //$NON-NLS-1$ //$NON-NLS-2$
        service.setServiceInterface("{urn:jboss:demo:create-application}CreateApplicationPT"); //$NON-NLS-1$
        service.setSuccessCount(83);
        service.setFaultCount(4);
        service.setTotalTime(804);
        service.setAverageTime(41);
        service.setMinTime(3);
        service.setMaxTime(101);
        service.addGatewayMetric("_CreateApplicationWebservice_soap1", "soap", 87, 42, 90, 0); //$NON-NLS-1$ //$NON-NLS-2$
        service.addGatewayMetric("_CreateApplicationWebservice_jms1", "jms", 13, 35, 10, 0); //$NON-NLS-1$ //$NON-NLS-2$
        return service;
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.IServicesServiceImpl#getReference(java.lang.String)
     */
    @Override
    public ReferenceBean getReference(String serviceId) throws UiException {
        ReferenceBean reference = new ReferenceBean();
        reference.setReferenceId("1"); //$NON-NLS-1$
        reference.setName(new QName("urn:jboss:demo:services", "CreateApplicationService")); //$NON-NLS-1$ //$NON-NLS-2$
        reference.setApplication(new QName("urn:jboss:demos:applications", "Contract")); //$NON-NLS-1$ //$NON-NLS-2$
        reference.setServiceInterface("org.jboss.demos.services.ICreateApplication"); //$NON-NLS-1$
        reference.setSuccessCount(83);
        reference.setFaultCount(4);
        reference.setTotalTime(804);
        reference.setAverageTime(41);
        reference.setMinTime(3);
        reference.setMaxTime(101);
        reference.addGatewayMetric("_CreateApplicationWebservice_soap1", "soap", 87, 42, 90, 0); //$NON-NLS-1$ //$NON-NLS-2$
        reference.addGatewayMetric("_CreateApplicationWebservice_jms1", "jms", 13, 35, 10, 0); //$NON-NLS-1$ //$NON-NLS-2$
        return reference;
    }

}
