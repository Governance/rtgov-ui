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
import org.overlord.monitoring.ui.client.shared.beans.SituationBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean;
import org.overlord.monitoring.ui.client.shared.exceptions.UiException;
import org.overlord.monitoring.ui.client.shared.services.ISituationsService;

/**
 * Client-side service for making RPC calls to the remote deployments service.
 *
 * @author eric.wittmann@redhat.com
 */
@ApplicationScoped
public class SituationsRpcService {

    @Inject
    private Caller<ISituationsService> remoteSituationsService;

    /**
     * Constructor.
     */
    public SituationsRpcService() {
    }

    /**
     * @see org.overlord.monitoring.ui.client.shared.services.ISituationsService#search(SituationsFilterBean, int)
     */
    public void search(SituationsFilterBean filters, int page,
            final IRpcServiceInvocationHandler<SituationResultSetBean> handler) {
        // TODO only allow one search at a time.  If another search comes in before the previous one
        // finished, cancel the previous one.  In other words, only return the results of the *last*
        // search performed.
        RemoteCallback<SituationResultSetBean> successCallback = new DelegatingRemoteCallback<SituationResultSetBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteSituationsService.call(successCallback, errorCallback).search(filters, page);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

    /**
     * @see org.overlord.monitoring.ui.client.shared.services.ISituationsService#get(String)
     */
    public void get(String situationId, IRpcServiceInvocationHandler<SituationBean> handler) {
        RemoteCallback<SituationBean> successCallback = new DelegatingRemoteCallback<SituationBean>(handler);
        ErrorCallback<?> errorCallback = new DelegatingErrorCallback(handler);
        try {
            remoteSituationsService.call(successCallback, errorCallback).get(situationId);
        } catch (UiException e) {
            errorCallback.error(null, e);
        }
    }

}
