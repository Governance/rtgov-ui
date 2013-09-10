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

import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.databinding.client.api.InitialState;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageState;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.monitoring.ui.client.local.ClientMessages;
import org.overlord.monitoring.ui.client.local.pages.services.GatewayMetricsTable;
import org.overlord.monitoring.ui.client.local.services.NotificationService;
import org.overlord.monitoring.ui.client.local.services.ServicesRpcService;
import org.overlord.monitoring.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.monitoring.ui.client.local.util.DOMUtil;
import org.overlord.monitoring.ui.client.local.util.DataBindingLongConverter;
import org.overlord.monitoring.ui.client.local.util.DataBindingQNameLocalPartConverter;
import org.overlord.monitoring.ui.client.local.util.DataBindingQNameNamespaceConverter;
import org.overlord.monitoring.ui.client.shared.beans.ServiceBean;
import org.overlord.sramp.ui.client.local.widgets.common.HtmlSnippet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * The Service Details page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/monitoring/ui/client/local/site/serviceDetails.html#page")
@Page(path="serviceDetails")
@Dependent
public class ServiceDetailsPage extends AbstractPage {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected ServicesRpcService servicesService;
    @Inject
    protected NotificationService notificationService;

    @PageState
    private String id;

    @Inject @AutoBound
    protected DataBinder<ServiceBean> service;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    private TransitionAnchor<DashboardPage> toDashboardPage;
    @Inject @DataField("back-to-services")
    private TransitionAnchor<ServicesPage> toServicesPage;

    // Header
    @Inject @DataField @Bound(property="name", converter=DataBindingQNameLocalPartConverter.class)
    InlineLabel serviceName;

    // Properties
    @Inject @DataField @Bound(property="name", converter=DataBindingQNameNamespaceConverter.class)
    InlineLabel serviceNamespace;
    @Inject @DataField @Bound(property="application", converter=DataBindingQNameNamespaceConverter.class)
    InlineLabel applicationNamespace;
    @Inject @DataField @Bound(property="application", converter=DataBindingQNameLocalPartConverter.class)
    InlineLabel applicationName;
    @Inject @DataField @Bound(property="serviceInterface")
    InlineLabel serviceInterface;

    // Message Counts
    @Inject @DataField("total-count")
    InlineLabel totalCount;
    @Inject @DataField("success-count") @Bound(property="successCount", converter=DataBindingLongConverter.class)
    InlineLabel successCount;
    @Inject @DataField("fault-count") @Bound(property="faultCount", converter=DataBindingLongConverter.class)
    InlineLabel faultCount;
    @Inject @DataField("success-rate")
    InlineLabel successRate;

    // Processing Times
    @Inject @DataField("total-time") @Bound(property="totalTime", converter=DataBindingLongConverter.class)
    InlineLabel totalTime;
    @Inject @DataField("average-time") @Bound(property="averageTime", converter=DataBindingLongConverter.class)
    InlineLabel averageTime;
    @Inject @DataField("min-time") @Bound(property="minTime", converter=DataBindingLongConverter.class)
    InlineLabel minTime;
    @Inject @DataField("max-time") @Bound(property="maxTime", converter=DataBindingLongConverter.class)
    InlineLabel maxTime;

    // Gateway Metrics
    @Inject @DataField("gateway-table") @Bound(property="gatewayMetrics")
    GatewayMetricsTable gatewayMetricsTable;

    @Inject @DataField("service-details-loading-spinner")
    protected HtmlSnippet loading;
    protected Element pageContent;

    /**
     * Constructor.
     */
    public ServiceDetailsPage() {
    }

    /**
     * Called after the widget is constructed.
     */
    @PostConstruct
    protected void onPostConstruct() {
        pageContent = DOMUtil.findElementById(getElement(), "service-details-content-wrapper"); //$NON-NLS-1$
        pageContent.addClassName("hide"); //$NON-NLS-1$
    }

    /**
     * @see org.overlord.sramp.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        pageContent.addClassName("hide"); //$NON-NLS-1$
        loading.getElement().removeClassName("hide"); //$NON-NLS-1$
        servicesService.getService(id, new IRpcServiceInvocationHandler<ServiceBean>() {
            @Override
            public void onReturn(ServiceBean data) {
                updateMetaData(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("service-details.error-getting-detail-info"), error); //$NON-NLS-1$
            }
        });
    }

    /**
     * Called when the service is loaded.
     * @param service
     */
    protected void updateMetaData(ServiceBean service) {
        this.service.setModel(service, InitialState.FROM_MODEL);
        loading.getElement().addClassName("hide"); //$NON-NLS-1$
        pageContent.removeClassName("hide"); //$NON-NLS-1$
        long total = service.getSuccessCount() + service.getFaultCount();
        totalCount.setText(String.valueOf(total));
        if (total > 0) {
            long rate = (service.getSuccessCount() * 100) / total;
            successRate.setText(String.valueOf(rate) + "%"); //$NON-NLS-1$
        } else {
            successRate.setText("0%"); //$NON-NLS-1$
        }
    }

}
