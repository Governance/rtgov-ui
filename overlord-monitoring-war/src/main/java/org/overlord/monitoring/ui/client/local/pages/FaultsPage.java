/*
 * Copyright 2012 JBoss Inc
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
package org.overlord.monitoring.ui.client.local.pages;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.monitoring.ui.client.local.ClientMessages;
import org.overlord.monitoring.ui.client.local.pages.faults.FaultFilters;
import org.overlord.monitoring.ui.client.local.pages.faults.FaultTable;
import org.overlord.monitoring.ui.client.local.services.FaultsRpcService;
import org.overlord.monitoring.ui.client.local.services.NotificationService;
import org.overlord.monitoring.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.monitoring.ui.client.shared.beans.FaultResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.FaultSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.FaultsFilterBean;
import org.overlord.sramp.ui.client.local.widgets.bootstrap.Pager;
import org.overlord.sramp.ui.client.local.widgets.common.HtmlSnippet;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * The "Faults" page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/monitoring/ui/client/local/site/faults.html#page")
@Page(path="faults")
@Dependent
public class FaultsPage extends AbstractPage {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected FaultsRpcService faultsService;
    @Inject
    protected NotificationService notificationService;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    private TransitionAnchor<DashboardPage> toDashboardPage;
    @Inject @DataField("to-services")
    private TransitionAnchor<ServicesPage> toServicesPage;

    @Inject @DataField("filter-sidebar")
    protected FaultFilters filtersPanel;

    @Inject @DataField("btn-refresh")
    protected Button refreshButton;

    @Inject @DataField("faults-none")
    protected HtmlSnippet noDataMessage;
    @Inject @DataField("faults-searching")
    protected HtmlSnippet searchInProgressMessage;
    @Inject @DataField("faults-table")
    protected FaultTable faultsTable;

    @Inject @DataField("faults-pager")
    protected Pager pager;
    @DataField("faults-range")
    protected SpanElement rangeSpan = Document.get().createSpanElement();
    @DataField("faults-total")
    protected SpanElement totalSpan = Document.get().createSpanElement();

    private int currentPage = 1;

    /**
     * Constructor.
     */
    public FaultsPage() {
    }

    /**
     * Called whenver the page is shown.
     */
    @PageShown
    public void onPageShown() {
    }

    /**
     * Called after construction.
     */
    @PostConstruct
    protected void postConstruct() {
        filtersPanel.addValueChangeHandler(new ValueChangeHandler<FaultsFilterBean>() {
            @Override
            public void onValueChange(ValueChangeEvent<FaultsFilterBean> event) {
                doSearch();
            }
        });
        pager.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                doSearch(event.getValue());
            }
        });

        // Hide column 1 when in mobile mode.
        faultsTable.setColumnClasses(1, "desktop-only"); //$NON-NLS-1$

        this.rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Event handler that fires when the user clicks the refresh button.
     * @param event
     */
    @EventHandler("btn-refresh")
    public void onRefreshClick(ClickEvent event) {
        doSearch(currentPage);
    }

    /**
     * Kick off a search at this point so that we show some data in the UI.
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        // Kick off an artifact search
        doSearch();
        // Refresh the artifact filters
        filtersPanel.refresh();
    }

    /**
     * Search for artifacts based on the current filter settings and search text.
     */
    protected void doSearch() {
        doSearch(1);
    }

    /**
     * Search for faults based on the current filter settings.
     * @param page
     */
    protected void doSearch(int page) {
        onSearchStarting();
        currentPage = page;
        faultsService.search(filtersPanel.getValue(), page, new IRpcServiceInvocationHandler<FaultResultSetBean>() {
            @Override
            public void onReturn(FaultResultSetBean data) {
                updateTable(data);
                updatePager(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("faults.error-loading"), error); //$NON-NLS-1$
                noDataMessage.setVisible(true);
                searchInProgressMessage.setVisible(false);
            }
        });
    }

    /**
     * Called when a new search is kicked off.
     */
    protected void onSearchStarting() {
        this.pager.setVisible(false);
        this.searchInProgressMessage.setVisible(true);
        this.faultsTable.setVisible(false);
        this.noDataMessage.setVisible(false);
        this.rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Updates the table of faults with the given data.
     * @param data
     */
    protected void updateTable(FaultResultSetBean data) {
        this.faultsTable.clear();
        this.searchInProgressMessage.setVisible(false);
        if (data.getFaults().size() > 0) {
            for (FaultSummaryBean summaryBean : data.getFaults()) {
                this.faultsTable.addRow(summaryBean);
            }
            this.faultsTable.setVisible(true);
        } else {
            this.noDataMessage.setVisible(true);
        }
    }

    /**
     * Updates the pager with the given data.
     * @param data
     */
    protected void updatePager(FaultResultSetBean data) {
        int numPages = ((int) (data.getTotalResults() / data.getItemsPerPage())) + (data.getTotalResults() % data.getItemsPerPage() == 0 ? 0 : 1);
        int thisPage = (data.getStartIndex() / data.getItemsPerPage()) + 1;
        this.pager.setNumPages(numPages);
        this.pager.setPage(thisPage);
        if (numPages > 1)
            this.pager.setVisible(true);

        int startIndex = data.getStartIndex() + 1;
        int endIndex = startIndex + data.getFaults().size() - 1;
        String rangeText = "" + startIndex + "-" + endIndex; //$NON-NLS-1$ //$NON-NLS-2$
        String totalText = String.valueOf(data.getTotalResults());
        this.rangeSpan.setInnerText(rangeText);
        this.totalSpan.setInnerText(totalText);
    }

}
