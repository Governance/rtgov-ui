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
package org.overlord.monitoring.ui.client.local.pages.services;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.overlord.monitoring.ui.client.local.ClientMessages;
import org.overlord.monitoring.ui.client.shared.beans.ServiceSummaryBean;
import org.overlord.sramp.ui.client.local.widgets.common.TemplatedWidgetTable;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * A table of deployments.
 *
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class ServiceTable extends TemplatedWidgetTable {

    @Inject
    protected ClientMessages i18n;
//    @Inject
//    protected TransitionAnchorFactory<ServiceDetailsPage> toDetailsPageLinkFactory;

    /**
     * Constructor.
     */
    public ServiceTable() {
    }

    /**
     * Adds a single row to the table.
     * @param serviceSummaryBean
     */
    public void addRow(final ServiceSummaryBean serviceSummaryBean) {
        int rowIdx = this.rowElements.size();
        DateTimeFormat format = DateTimeFormat.getFormat(i18n.format("dateTime-format")); //$NON-NLS-1$

//        Anchor name = toDetailsPageLinkFactory.get("uuid", serviceSummaryBean.getUuid()); //$NON-NLS-1$
//        name.setText(serviceSummaryBean.getName());
        InlineLabel name = new InlineLabel(serviceSummaryBean.getName());
        InlineLabel category = new InlineLabel(serviceSummaryBean.getCategory());
        InlineLabel mep = new InlineLabel(serviceSummaryBean.getMep());
        InlineLabel address = new InlineLabel(serviceSummaryBean.getAddress());
        InlineLabel averageDuration = new InlineLabel(formatDuration(serviceSummaryBean.getAverageDuration()));
        InlineLabel lastActivity = new InlineLabel(format.format(serviceSummaryBean.getLastActivity()));

        add(rowIdx, 0, name);
        add(rowIdx, 1, category);
        add(rowIdx, 2, mep);
        add(rowIdx, 3, address);
        add(rowIdx, 4, averageDuration);
        add(rowIdx, 5, lastActivity);
    }

    /**
     * Formats an average duration (in milliseconds) into a human readable format.
     * @param averageDuration
     */
    private String formatDuration(long averageDuration) {
        // TODO implement this!
        return "TBD"; //$NON-NLS-1$
    }

}
