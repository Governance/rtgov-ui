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

import org.jboss.errai.bus.server.annotations.Remote;
import org.overlord.monitoring.ui.client.shared.beans.SituationBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.SituationsFilterBean;
import org.overlord.monitoring.ui.client.shared.exceptions.UiException;

/**
 * Provides a way to manage situations.
 *
 * @author eric.wittmann@redhat.com
 */
@Remote
public interface ISituationsServiceImpl {

    /**
     * Search for services using the given filters and search text.
     * @param filters
     * @param page
     * @param sortColumn
     * @param ascending
     * @throws UiException
     */
    public SituationResultSetBean search(SituationsFilterBean filters, int page, String sortColumn,
            boolean ascending) throws UiException;

    /**
     * Fetches a full service by its name.
     * @param name
     * @throws UiException
     */
    public SituationBean get(String name) throws UiException;

    /**
     * Resubmits a message.
     * @param situationId
     * @param message
     * @throws UiException
     */
    public void resubmit(String situationId, String message) throws UiException;

}
