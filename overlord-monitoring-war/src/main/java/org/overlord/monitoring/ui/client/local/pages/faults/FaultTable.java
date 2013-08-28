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
package org.overlord.monitoring.ui.client.local.pages.faults;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.overlord.monitoring.ui.client.local.ClientMessages;
import org.overlord.monitoring.ui.client.shared.beans.FaultSummaryBean;
import org.overlord.sramp.ui.client.local.widgets.common.TemplatedWidgetTable;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A table of deployments.
 *
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class FaultTable extends TemplatedWidgetTable {

    @Inject
    protected ClientMessages i18n;
//    @Inject
//    protected TransitionAnchorFactory<ServiceDetailsPage> toDetailsPageLinkFactory;

    /**
     * Constructor.
     */
    public FaultTable() {
    }

    /**
     * Adds a single row to the table.
     * @param faultSummaryBean
     */
    public void addRow(final FaultSummaryBean faultSummaryBean) {
        int rowIdx = this.rowElements.size();
        DateTimeFormat format = DateTimeFormat.getFormat(i18n.format("dateTime-format")); //$NON-NLS-1$

//        Anchor name = toDetailsPageLinkFactory.get("uuid", serviceSummaryBean.getUuid()); //$NON-NLS-1$
//        name.setText(serviceSummaryBean.getName());
        InlineLabel serviceName = new InlineLabel(faultSummaryBean.getServiceName());
        InlineLabel faultReason = new InlineLabel(faultSummaryBean.getFaultReason());
        InlineLabel faultTime = new InlineLabel(format.format(faultSummaryBean.getFaultTime()));
        Widget actions = createActionPanel(faultSummaryBean);

        add(rowIdx, 0, serviceName);
        add(rowIdx, 1, faultReason);
        add(rowIdx, 2, faultTime);
        add(rowIdx, 3, actions);
    }

    /**
     * Creates the action buttons.
     * @param faultSummaryBean
     */
    private Widget createActionPanel(FaultSummaryBean faultSummaryBean) {
        FlowPanel actions = new FlowPanel();
        actions.getElement().setClassName("table-actions"); //$NON-NLS-1$

        String downloadFaultIconHtml = "<div class=\"icon icon-download-fault\"></div>"; //$NON-NLS-1$
        Anchor downloadFaultActionButton = new Anchor(downloadFaultIconHtml, true);
        String downloadInitialIconHtml = "<div class=\"icon icon-download-initial\"></div>"; //$NON-NLS-1$
        Anchor downloadInitialActionButton = new Anchor(downloadInitialIconHtml, true);
        String retryIconHtml = "<div class=\"icon icon-retry\"></div>"; //$NON-NLS-1$
        Anchor retryActionButton = new Anchor(retryIconHtml, true);

        actions.add(downloadFaultActionButton);
        actions.add(downloadInitialActionButton);
        actions.add(retryActionButton);
        return actions;
    }

}
