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
package org.overlord.monitoring.ui.client.local.services;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.overlord.monitoring.ui.client.local.services.rpc.DelegatingErrorCallback;
import org.overlord.monitoring.ui.client.local.services.rpc.DelegatingRemoteCallback;
import org.overlord.monitoring.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.monitoring.ui.client.shared.beans.ServiceBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean;
import org.overlord.monitoring.ui.client.shared.exceptions.UiException;
import org.overlord.monitoring.ui.client.shared.services.IServicesService;

/**
 * Client-side service for making RPC calls to the remote deployments service.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class ServicesRpcService {

    @Inject
    private Caller<IServicesService> remoteServicesService;

    /**
     * Constructor.
     */
    public ServicesRpcService() {
    }

    /**
     * @see org.overlord.monitoring.ui.client.shared.services.IServicesService#search(ServicesFilterBean, int)
     */
    public void search(ServicesFilterBean filters, int page,
            final IRpcServiceInvocationHandler<ServiceResultSetBean> handler) {
        // TODO only allow one search at a time.  If another search comes in before the previous one
        // finished, cancel the previous one.  In other words, only return the results of the *last*
        // search performed.
        RemoteCallback<ServiceResultSetBean> successCallback = new DelegatingRemoteCallback<ServiceResultSetBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteServicesService.call(successCallback, errorCallback).search(filters, page);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.monitoring.ui.client.shared.services.IServicesService#get(String)
     */
    public void get(String uuid, IRpcServiceInvocationHandler<ServiceBean> handler) {
        RemoteCallback<ServiceBean> successCallback = new DelegatingRemoteCallback<ServiceBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteServicesService.call(successCallback, errorCallback).get(uuid);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

}
