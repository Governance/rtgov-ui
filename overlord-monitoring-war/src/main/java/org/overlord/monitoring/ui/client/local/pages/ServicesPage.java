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
import org.overlord.monitoring.ui.client.local.events.TableSortEvent;
import org.overlord.monitoring.ui.client.local.pages.services.ComponentServiceTable;
import org.overlord.monitoring.ui.client.local.pages.services.ServiceFilters;
import org.overlord.monitoring.ui.client.local.pages.services.ServiceTable;
import org.overlord.monitoring.ui.client.local.services.NotificationService;
import org.overlord.monitoring.ui.client.local.services.ServicesRpcService;
import org.overlord.monitoring.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.monitoring.ui.client.local.widgets.common.SortableTemplatedWidgetTable.SortColumn;
import org.overlord.monitoring.ui.client.shared.beans.ComponentServiceResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.ComponentServiceSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean;
import org.overlord.sramp.ui.client.local.widgets.bootstrap.Pager;
import org.overlord.sramp.ui.client.local.widgets.common.HtmlSnippet;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;

/**
 * The "Services" page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/monitoring/ui/client/local/site/services.html#page")
@Page(path="services")
@Dependent
public class ServicesPage extends AbstractPage {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected ServicesRpcService servicesService;
    @Inject
    protected NotificationService notificationService;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    private TransitionAnchor<DashboardPage> toDashboardPage;

    @Inject @DataField("filter-sidebar")
    protected ServiceFilters filtersPanel;

    @Inject @DataField("services-btn-refresh")
    protected Button servicesRefreshButton;
    @Inject @DataField("cs-btn-refresh")
    protected Button componentServicesRefreshButton;

    @Inject @DataField("services-none")
    protected HtmlSnippet noDataMessage;
    @Inject @DataField("services-searching")
    protected HtmlSnippet searchInProgressMessage;
    @Inject @DataField("services-table")
    protected ServiceTable servicesTable;
    @Inject @DataField("cs-none")
    protected HtmlSnippet noDataMessage_cs;
    @Inject @DataField("cs-searching")
    protected HtmlSnippet searchInProgressMessage_cs;
    @Inject @DataField("cs-table")
    protected ComponentServiceTable componentServicesTable;

    @Inject @DataField("services-pager")
    protected Pager pager;
    @DataField("services-range")
    protected SpanElement rangeSpan = Document.get().createSpanElement();
    @DataField("services-total")
    protected SpanElement totalSpan = Document.get().createSpanElement();
    @Inject @DataField("cs-pager")
    protected Pager pager_cs;
    @DataField("cs-range")
    protected SpanElement rangeSpan_cs = Document.get().createSpanElement();
    @DataField("cs-total")
    protected SpanElement totalSpan_cs = Document.get().createSpanElement();

    private int currentServicesPage = 1;
    private int currentServicesPage_cs = 1;

    /**
     * Constructor.
     */
    public ServicesPage() {
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
        filtersPanel.addValueChangeHandler(new ValueChangeHandler<ServicesFilterBean>() {
            @Override
            public void onValueChange(ValueChangeEvent<ServicesFilterBean> event) {
                doServicesSearch();
                doComponentServicesSearch();
            }
        });
        pager.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                doServicesSearch(event.getValue());
            }
        });
        pager_cs.addValueChangeHandler(new ValueChangeHandler<Integer>() {
            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                doComponentServicesSearch(event.getValue());
            }
        });
        servicesTable.addTableSortHandler(new TableSortEvent.Handler() {
            @Override
            public void onTableSort(TableSortEvent event) {
                doServicesSearch(currentServicesPage);
            }
        });
        componentServicesTable.addTableSortHandler(new TableSortEvent.Handler() {
            @Override
            public void onTableSort(TableSortEvent event) {
                doComponentServicesSearch(currentServicesPage_cs);
            }
        });

        servicesTable.setColumnClasses(2, "desktop-only"); //$NON-NLS-1$
        servicesTable.setColumnClasses(3, "desktop-only"); //$NON-NLS-1$
        servicesTable.setColumnClasses(4, "desktop-only"); //$NON-NLS-1$
        componentServicesTable.setColumnClasses(2, "desktop-only"); //$NON-NLS-1$
        componentServicesTable.setColumnClasses(3, "desktop-only"); //$NON-NLS-1$
        componentServicesTable.setColumnClasses(4, "desktop-only"); //$NON-NLS-1$

        this.rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan.setInnerText("?"); //$NON-NLS-1$
        this.rangeSpan_cs.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan_cs.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Event handler that fires when the user clicks the refresh button.
     * @param event
     */
    @EventHandler("services-btn-refresh")
    public void onRefreshClick(ClickEvent event) {
        doServicesSearch(currentServicesPage);
    }

    /**
     * Event handler that fires when the user clicks the refresh button.
     * @param event
     */
    @EventHandler("cs-btn-refresh")
    public void onRefreshClick_cs(ClickEvent event) {
        doComponentServicesSearch(currentServicesPage_cs);
    }

    /**
     * Kick off a search at this point so that we show some data in the UI.
     * @see org.overlord.dtgov.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        doServicesSearch();
        doComponentServicesSearch();
        filtersPanel.refresh();
    }

    /**
     * Search for services based on the current filter settings.
     */
    protected void doServicesSearch() {
        doServicesSearch(1);
    }

    /**
     * Search for services based on the current filter settings.
     */
    protected void doComponentServicesSearch() {
        doComponentServicesSearch(1);
    }

    /**
     * Search for services based on the current filter settings.
     * @param page
     */
    protected void doServicesSearch(int page) {
        onServicesSearchStarting();
        currentServicesPage = page;
        SortColumn currentSortColumn = this.servicesTable.getCurrentSortColumn();
        servicesService.findServices(filtersPanel.getValue(), page, currentSortColumn.columnId,
                currentSortColumn.ascending, new IRpcServiceInvocationHandler<ServiceResultSetBean>() {
            @Override
            public void onReturn(ServiceResultSetBean data) {
                updateServicesTable(data);
                updateServicesPager(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("services.error-loading"), error); //$NON-NLS-1$
                noDataMessage.setVisible(true);
                searchInProgressMessage.setVisible(false);
            }
        });
    }

    /**
     * Search for services based on the current filter settings.
     * @param page
     */
    protected void doComponentServicesSearch(int page) {
        onComponentServicesSearchStarting();
        currentServicesPage_cs = page;
        SortColumn currentSortColumn = this.componentServicesTable.getCurrentSortColumn();
        servicesService.findComponentServices(filtersPanel.getValue(), page, currentSortColumn.columnId,
                currentSortColumn.ascending,
                new IRpcServiceInvocationHandler<ComponentServiceResultSetBean>() {
            @Override
            public void onReturn(ComponentServiceResultSetBean data) {
                updateComponentServicesTable(data);
                updateComponentServicesPager(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("services.error-loading"), error); //$NON-NLS-1$
                noDataMessage_cs.setVisible(true);
                searchInProgressMessage_cs.setVisible(false);
            }
        });
    }

    /**
     * Called when a new search is kicked off.
     */
    protected void onServicesSearchStarting() {
        this.pager.setVisible(false);
        this.searchInProgressMessage.setVisible(true);
        this.servicesTable.setVisible(false);
        this.noDataMessage.setVisible(false);
        this.rangeSpan.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Called when a new search is kicked off.
     */
    protected void onComponentServicesSearchStarting() {
        this.pager_cs.setVisible(false);
        this.searchInProgressMessage_cs.setVisible(true);
        this.componentServicesTable.setVisible(false);
        this.noDataMessage_cs.setVisible(false);
        this.rangeSpan_cs.setInnerText("?"); //$NON-NLS-1$
        this.totalSpan_cs.setInnerText("?"); //$NON-NLS-1$
    }

    /**
     * Updates the table of services with the given data.
     * @param data
     */
    protected void updateServicesTable(ServiceResultSetBean data) {
        this.servicesTable.clear();
        this.searchInProgressMessage.setVisible(false);
        if (data.getServices().size() > 0) {
            for (ServiceSummaryBean deploymentSummaryBean : data.getServices()) {
                this.servicesTable.addRow(deploymentSummaryBean);
            }
            this.servicesTable.setVisible(true);
        } else {
            this.noDataMessage.setVisible(true);
        }
    }

    /**
     * Updates the table of services with the given data.
     * @param data
     */
    protected void updateComponentServicesTable(ComponentServiceResultSetBean data) {
        this.componentServicesTable.clear();
        this.searchInProgressMessage_cs.setVisible(false);
        if (data.getServices().size() > 0) {
            for (ComponentServiceSummaryBean deploymentSummaryBean : data.getServices()) {
                this.componentServicesTable.addRow(deploymentSummaryBean);
            }
            this.componentServicesTable.setVisible(true);
        } else {
            this.noDataMessage_cs.setVisible(true);
        }
    }

    /**
     * Updates the pager with the given data.
     * @param data
     */
    protected void updateServicesPager(ServiceResultSetBean data) {
        int numPages = ((int) (data.getTotalResults() / data.getItemsPerPage())) + (data.getTotalResults() % data.getItemsPerPage() == 0 ? 0 : 1);
        int thisPage = (data.getStartIndex() / data.getItemsPerPage()) + 1;
        this.pager.setNumPages(numPages);
        this.pager.setPage(thisPage);
        if (numPages > 1)
            this.pager.setVisible(true);

        int startIndex = data.getStartIndex() + 1;
        int endIndex = startIndex + data.getServices().size() - 1;
        String rangeText = "" + startIndex + "-" + endIndex; //$NON-NLS-1$ //$NON-NLS-2$
        String totalText = String.valueOf(data.getTotalResults());
        this.rangeSpan.setInnerText(rangeText);
        this.totalSpan.setInnerText(totalText);
    }

    /**
     * Updates the pager with the given data.
     * @param data
     */
    protected void updateComponentServicesPager(ComponentServiceResultSetBean data) {
        int numPages = ((int) (data.getTotalResults() / data.getItemsPerPage())) + (data.getTotalResults() % data.getItemsPerPage() == 0 ? 0 : 1);
        int thisPage = (data.getStartIndex() / data.getItemsPerPage()) + 1;
        this.pager_cs.setNumPages(numPages);
        this.pager_cs.setPage(thisPage);
        if (numPages > 1)
            this.pager_cs.setVisible(true);

        int startIndex = data.getStartIndex() + 1;
        int endIndex = startIndex + data.getServices().size() - 1;
        String rangeText = "" + startIndex + "-" + endIndex; //$NON-NLS-1$ //$NON-NLS-2$
        String totalText = String.valueOf(data.getTotalResults());
        this.rangeSpan_cs.setInnerText(rangeText);
        this.totalSpan_cs.setInnerText(totalText);
    }

}
