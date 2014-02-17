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
package org.overlord.rtgov.ui.server.services;

import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.overlord.rtgov.ui.client.shared.beans.SituationBean;
import org.overlord.rtgov.ui.client.shared.beans.SituationResultSetBean;
import org.overlord.rtgov.ui.client.shared.beans.SituationsFilterBean;
import org.overlord.rtgov.ui.client.shared.exceptions.UiException;
import org.overlord.rtgov.ui.client.shared.services.ISituationsService;
import org.overlord.rtgov.ui.server.cdi.RequiresAuthentication;

/**
 * Concrete implementation of the situations service.
 *
 * @author eric.wittmann@redhat.com
 */
@Service
public class SituationsService implements ISituationsService {

    @Inject ISituationsServiceImpl impl;

    /**
     * Constructor.
     */
    public SituationsService() {
    }
    
    /**
     * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#search(org.overlord.rtgov.ui.client.shared.beans.SituationsFilterBean, int, java.lang.String, boolean)
     */
    @Override
    @RequiresAuthentication
    public SituationResultSetBean search(SituationsFilterBean filters, int page, String sortColumn,
            boolean ascending) throws UiException {
        return impl.search(filters, page, sortColumn, ascending);
    }

    /**
     * @see org.overlord.ISituationsService.ui.client.shared.services.ISituationsService#getService(java.lang.String)
     */
    @Override
    public SituationBean get(String situationId) throws UiException {
        return impl.get(situationId);
    }
    
    /**
     * @see org.overlord.rtgov.ui.client.shared.services.ISituationsService#resubmit(java.lang.String, java.lang.String)
     */
    @Override
    public void resubmit(String situationId, String message) throws UiException {
        impl.resubmit(situationId, message);
    }

}
