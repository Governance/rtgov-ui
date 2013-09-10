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

import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.overlord.monitoring.ui.client.shared.beans.ComponentServiceBean;
import org.overlord.monitoring.ui.client.shared.beans.ComponentServiceResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceResultSetBean;
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

    @Inject IServicesServiceImpl impl;

    /**
     * Constructor.
     */
    public ServicesService() {
    }

    /**
     * @see org.overlord.monitoring.ui.client.shared.services.IServicesService#findServices(org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean, int, java.lang.String, boolean)
     */
    @Override
    public ServiceResultSetBean findServices(ServicesFilterBean filters, int page, String sortColumn,
            boolean ascending) throws UiException {
        return impl.findServices(filters, page, sortColumn, ascending);
    }

    /**
     * @see org.overlord.monitoring.ui.client.shared.services.IServicesService#findComponentServices(org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean, int, java.lang.String, boolean)
     */
    @Override
    public ComponentServiceResultSetBean findComponentServices(ServicesFilterBean filters, int page,
            String sortColumn, boolean ascending) throws UiException {
        return impl.findComponentServices(filters, page, sortColumn, ascending);
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IServicesService#getService(java.lang.String)
     */
    @Override
    public ServiceBean getService(String uuid) throws UiException {
        return impl.getService(uuid);
    }

    /**
     * @see org.overlord.monitoring.ui.client.shared.services.IServicesService#getComponentService(java.lang.String)
     */
    @Override
    public ComponentServiceBean getComponentService(String serviceId) throws UiException {
        return impl.getComponentService(serviceId);
    }
}
