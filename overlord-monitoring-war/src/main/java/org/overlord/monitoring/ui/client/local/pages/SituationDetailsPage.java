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
import org.overlord.monitoring.ui.client.local.pages.situations.CallTraceDetails;
import org.overlord.monitoring.ui.client.local.pages.situations.CallTraceWidget;
import org.overlord.monitoring.ui.client.local.pages.situations.SituationPropertiesTable;
import org.overlord.monitoring.ui.client.local.services.NotificationService;
import org.overlord.monitoring.ui.client.local.services.SituationsRpcService;
import org.overlord.monitoring.ui.client.local.services.rpc.IRpcServiceInvocationHandler;
import org.overlord.monitoring.ui.client.local.util.DOMUtil;
import org.overlord.monitoring.ui.client.local.util.DataBindingDateTimeConverter;
import org.overlord.monitoring.ui.client.shared.beans.SituationBean;
import org.overlord.monitoring.ui.client.shared.beans.TraceNodeBean;
import org.overlord.sramp.ui.client.local.widgets.common.HtmlSnippet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * The Situation Details page.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/monitoring/ui/client/local/site/situationDetails.html#page")
@Page(path="situationDetails")
@Dependent
public class SituationDetailsPage extends AbstractPage {

    @Inject
    protected ClientMessages i18n;
    @Inject
    protected SituationsRpcService situationsService;
    @Inject
    protected NotificationService notificationService;

    @PageState
    private String id;

    @Inject @AutoBound
    protected DataBinder<SituationBean> situation;

    // Breadcrumbs
    @Inject @DataField("back-to-dashboard")
    private TransitionAnchor<DashboardPage> toDashboardPage;
    @Inject @DataField("to-situations")
    private TransitionAnchor<SituationsPage> toSituationsPage;
    @Inject @DataField("to-services")
    private TransitionAnchor<ServicesPage> toServicesPage;

    // Header
    @Inject @DataField @Bound(property="type")
    InlineLabel situationName;

    @Inject @DataField
    FlowPanel severity;

    // Properties
    @Inject @DataField @Bound(property="subject")
    InlineLabel subject;
    @Inject @DataField @Bound(property="timestamp", converter=DataBindingDateTimeConverter.class)
    InlineLabel timestamp;
    @Inject @DataField @Bound(property="description")
    InlineLabel description;

    @Inject @DataField("properties-table") @Bound(property="properties")
    SituationPropertiesTable propertiesTable;
    @Inject @DataField("context-table") @Bound(property="context")
    SituationPropertiesTable contextTable;

    @Inject @DataField("call-trace") @Bound(property="callTrace")
    CallTraceWidget callTrace;
    @Inject @DataField("call-trace-detail")
    CallTraceDetails callTraceDetail;

    @Inject @DataField("situation-details-loading-spinner")
    protected HtmlSnippet loading;
    protected Element pageContent;

    /**
     * Constructor.
     */
    public SituationDetailsPage() {
    }

    /**
     * Called after the widget is constructed.
     */
    @PostConstruct
    protected void onPostConstruct() {
        pageContent = DOMUtil.findElementById(getElement(), "situation-details-content-wrapper"); //$NON-NLS-1$
        pageContent.addClassName("hide"); //$NON-NLS-1$
        callTraceDetail.setVisible(false);
        callTrace.addSelectionHandler(new SelectionHandler<TraceNodeBean>() {
            @Override
            public void onSelection(SelectionEvent<TraceNodeBean> event) {
                onCallTraceNodeSelected(event.getSelectedItem());
            }
        });
    }

    /**
     * @see org.overlord.sramp.ui.client.local.pages.AbstractPage#onPageShowing()
     */
    @Override
    protected void onPageShowing() {
        pageContent.addClassName("hide"); //$NON-NLS-1$
        loading.getElement().removeClassName("hide"); //$NON-NLS-1$
        situationsService.get(id, new IRpcServiceInvocationHandler<SituationBean>() {
            @Override
            public void onReturn(SituationBean data) {
                updateMetaData(data);
            }
            @Override
            public void onError(Throwable error) {
                notificationService.sendErrorNotification(i18n.format("situation-details.error-getting-detail-info"), error); //$NON-NLS-1$
            }
        });
    }

    /**
     * Called when the situation is loaded.
     * @param situation
     */
    protected void updateMetaData(SituationBean situation) {
        this.situation.setModel(situation, InitialState.FROM_MODEL);
        severity.setStyleName("icon"); //$NON-NLS-1$
        severity.addStyleName("details-icon"); //$NON-NLS-1$
        severity.addStyleName("icon-severity-" + situation.getSeverity()); //$NON-NLS-1$
        loading.getElement().addClassName("hide"); //$NON-NLS-1$
        pageContent.removeClassName("hide"); //$NON-NLS-1$
    }

    /**
     * Event handler called when the user clicks an item in the call trace widget.
     * @param event
     */
    public void onCallTraceNodeSelected(TraceNodeBean node) {
        callTraceDetail.setValue(node);
        callTraceDetail.setVisible(true);
    }

}
