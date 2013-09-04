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
import org.overlord.monitoring.ui.client.shared.beans.ComponentServiceSummaryBean;
import org.overlord.sramp.ui.client.local.widgets.common.TemplatedWidgetTable;

import com.google.gwt.user.client.ui.InlineLabel;

/**
 * A table of component services.
 *
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class ComponentServiceTable extends TemplatedWidgetTable {

    @Inject
    protected ClientMessages i18n;
//    @Inject
//    protected TransitionAnchorFactory<ServiceDetailsPage> toDetailsPageLinkFactory;

    /**
     * Constructor.
     */
    public ComponentServiceTable() {
    }

    /**
     * Adds a single row to the table.
     * @param summaryBean
     */
    public void addRow(final ComponentServiceSummaryBean summaryBean) {
        int rowIdx = this.rowElements.size();

//        Anchor name = toDetailsPageLinkFactory.get("uuid", serviceSummaryBean.getUuid()); //$NON-NLS-1$
//        name.setText(serviceSummaryBean.getName());
        InlineLabel name = new InlineLabel(summaryBean.getName());
        InlineLabel application = new InlineLabel(summaryBean.getApplication());
        InlineLabel interf4ce = new InlineLabel(summaryBean.getIface());
        InlineLabel implementation = new InlineLabel(summaryBean.getImplementation());
        InlineLabel averageDuration = new InlineLabel(formatDuration(summaryBean.getAverageDuration()));

        add(rowIdx, 0, name);
        add(rowIdx, 1, application);
        add(rowIdx, 2, interf4ce);
        add(rowIdx, 3, implementation);
        add(rowIdx, 4, averageDuration);
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
