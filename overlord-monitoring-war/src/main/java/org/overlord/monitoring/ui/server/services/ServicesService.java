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
import java.util.Date;

import org.jboss.errai.bus.server.annotations.Service;
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
     * @see org.overlord.monitoring.ui.client.shared.services.IServicesService#search(org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean, int)
     */
    @Override
    public ServiceResultSetBean search(ServicesFilterBean filters, int page) throws UiException {
        return createMockResponse();
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IServicesService#get(java.lang.String)
     */
    @Override
    public ServiceBean get(String uuid) throws UiException {
        return null;
    }

    /**
     * Mock response data.
     */
    protected ServiceResultSetBean createMockResponse() {
        ServiceResultSetBean rval = new ServiceResultSetBean();
        ArrayList<ServiceSummaryBean> services = new ArrayList<ServiceSummaryBean>();
        rval.setServices(services);
        rval.setItemsPerPage(20);
        rval.setStartIndex(0);
        rval.setTotalResults(2);

        ServiceSummaryBean service = new ServiceSummaryBean();
        service.setName("CreateApplicationWebservice"); //$NON-NLS-1$
        service.setCategory("Contract"); //$NON-NLS-1$
        service.setMep("<->"); //$NON-NLS-1$
        service.setAddress("invm://43124321872361264823764816238163286416431643/false?false#10000"); //$NON-NLS-1$
        service.setAverageDuration(2837l);
        service.setLastActivity(new Date());
        services.add(service);

        service = new ServiceSummaryBean();
        service.setName("CreateQuoteWebservice"); //$NON-NLS-1$
        service.setCategory("Contract"); //$NON-NLS-1$
        service.setMep("<->"); //$NON-NLS-1$
        service.setAddress("invm://14398724987124987129487jjjjj21948791349871249012;94943987149712479890/false?false#10000"); //$NON-NLS-1$
        service.setAverageDuration(2837l);
        service.setLastActivity(new Date());
        services.add(service);

        return rval;
    }

}
