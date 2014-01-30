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
package org.overlord.rtgov.ui.server.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ReferenceResultSetBean;
import org.overlord.rtgov.ui.client.model.ReferenceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceResultSetBean;
import org.overlord.rtgov.ui.client.model.ServiceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServicesFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.overlord.rtgov.ui.provider.ServicesProvider;
import org.overlord.rtgov.ui.server.services.IServicesServiceImpl;

/**
 * SwitchYard implementation of the services service. :)
 *
 * @author kconner@redhat.com
 */
@ApplicationScoped
@Alternative
public class ServicesProviderServiceImpl implements IServicesServiceImpl {

	@Inject
	private Instance<ServicesProvider> _providers;

	/**
     * @see org.overlord.rtgov.ui.server.services.IServicesServiceImpl#getApplicationNames()
     */
    @Override
    public List<QName> getApplicationNames() throws UiException {
        final List<QName> apps = new ArrayList<QName>();

        for (ServicesProvider sp : _providers) {
        	apps.addAll(sp.getApplicationNames());
        }

        return apps;
    }

    /**
     * @see org.overlord.rtgov.ui.server.services.IServicesServiceImpl#findServices(org.overlord.rtgov.ui.client.model.ServicesFilterBean, int)
     */
    @Override
    public ServiceResultSetBean findServices(final ServicesFilterBean filters, final int page,
            final String sortColumn, final boolean ascending) throws UiException {
        final ServiceResultSetBean serviceResult = new ServiceResultSetBean();
        final ArrayList<ServiceSummaryBean> services = new ArrayList<ServiceSummaryBean>();

        for (ServicesProvider sp : _providers) {
        	services.addAll(sp.findServices(filters));
        }
        
        // TODO: Pagination support

        serviceResult.setServices(services);
        serviceResult.setItemsPerPage(services.size());
        serviceResult.setStartIndex(0);
        serviceResult.setTotalResults(services.size());
        
        return serviceResult;
    }

    /**
     * @see org.overlord.rtgov.ui.server.services.IServicesServiceImpl#findReferences(org.overlord.rtgov.ui.client.model.ServicesFilterBean, int)
     */
    @Override
    public ReferenceResultSetBean findReferences(
            ServicesFilterBean filters, int page, String sortColumn,
            boolean ascending) throws UiException {
        final ReferenceResultSetBean referenceResult = new ReferenceResultSetBean();
        final List<ReferenceSummaryBean> references = new ArrayList<ReferenceSummaryBean>();

        for (ServicesProvider sp : _providers) {
        	references.addAll(sp.findReferences(filters));
        }
        
        // TODO: Pagination support

        referenceResult.setReferences(references);
        referenceResult.setItemsPerPage(references.size());
        referenceResult.setStartIndex(0);
        referenceResult.setTotalResults(references.size());
        return referenceResult;
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IServicesServiceImpl#getService(java.lang.String)
     */
    @Override
    public ServiceBean getService(final String uuid) throws UiException {
        
        for (ServicesProvider sp : _providers) {
        	ServiceBean sb=sp.getService(uuid);
        	if (sb != null) {
        		return sb;
        	}
        }
    	
    	return null;
    }

    /**
     * @see org.overlord.rtgov.ui.server.services.IServicesServiceImpl#getReference(java.lang.String)
     */
    @Override
    public ReferenceBean getReference(final String referenceId) throws UiException {
        for (ServicesProvider sp : _providers) {
        	ReferenceBean rb=sp.getReference(referenceId);
        	if (rb != null) {
        		return rb;
        	}
        }
    	
    	return null;
    }
}
