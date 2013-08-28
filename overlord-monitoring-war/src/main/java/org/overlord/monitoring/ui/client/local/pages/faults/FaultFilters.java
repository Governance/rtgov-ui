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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.monitoring.ui.client.shared.beans.FaultsFilterBean;
import org.overlord.sramp.ui.client.local.widgets.bootstrap.DateBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The faults filtersPanel sidebar.  Whenever the user changes any of the settings in
 * the filter sidebar, a ValueChangeEvent will be fired.
 *
 * @author eric.wittmann@redhat.com
 */
@Templated("/org/overlord/monitoring/ui/client/local/site/faults.html#filter-sidebar")
@Dependent
public class FaultFilters extends Composite implements HasValueChangeHandlers<FaultsFilterBean> {

    private FaultsFilterBean currentState = new FaultsFilterBean();

    // Owner, type, bundle name
    @Inject @DataField
    protected ServiceNameListBox serviceName;
    @Inject @DataField
    protected TextBox faultReason;
    @Inject @DataField
    protected CheckBox archived;

    @Inject @DataField
    protected DateBox faultAfter;
    @Inject @DataField
    protected TextBox serviceRetries;

    @Inject @DataField
    protected DateBox processingStartedFrom;
    @Inject @DataField
    protected DateBox processingStartedTo;

    @Inject @DataField
    protected Anchor clearFilters;

    /**
     * Constructor.
     */
    public FaultFilters() {
    }

    /**
     * Called after construction and injection.
     */
    @SuppressWarnings("unchecked")
    @PostConstruct
    protected void postConstruct() {
        ClickHandler clearFilterHandler = new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setValue(new FaultsFilterBean());
                onFilterValueChange();
            }
        };
        clearFilters.addClickHandler(clearFilterHandler);
        @SuppressWarnings("rawtypes")
        ValueChangeHandler valueChangeHandler = new ValueChangeHandler() {
            @Override
            public void onValueChange(ValueChangeEvent event) {
                onFilterValueChange();
            }
        };
        serviceName.addValueChangeHandler(valueChangeHandler);
        faultReason.addValueChangeHandler(valueChangeHandler);
        archived.addValueChangeHandler(valueChangeHandler);
        faultAfter.addValueChangeHandler(valueChangeHandler);
        serviceRetries.addValueChangeHandler(valueChangeHandler);
        processingStartedFrom.addValueChangeHandler(valueChangeHandler);
        processingStartedTo.addValueChangeHandler(valueChangeHandler);
    }

    /**
     * Called whenever any filter value changes.
     */
    protected void onFilterValueChange() {
        FaultsFilterBean newState = new FaultsFilterBean();
        newState.setServiceName(serviceName.getValue())
            .setFaultReason(faultReason.getValue())
            .setArchived(archived.getValue())
            .setFaultAfter(faultAfter.getDateValue())
            .setServiceRetries(serviceRetries.getValue())
            .setProcessingStartedFrom(processingStartedFrom.getDateValue())
            .setProcessingStartedTo(processingStartedTo.getDateValue());

        FaultsFilterBean oldState = this.currentState;
        this.currentState = newState;
        // Only fire a change event if something actually changed.
        ValueChangeEvent.fireIfNotEqual(this, oldState, currentState);
    }

    /**
     * @return the current filter settings
     */
    public FaultsFilterBean getValue() {
        return this.currentState;
    }

    /**
     * @param value the new filter settings
     */
    public void setValue(FaultsFilterBean value) {
        serviceName.setValue(value.getServiceName() == null ? "" : value.getServiceName()); //$NON-NLS-1$
        faultReason.setValue(value.getFaultReason() == null ? "" : value.getFaultReason()); //$NON-NLS-1$
        archived.setValue(value.isArchived());
        faultAfter.setDateValue(value.getFaultAfter() == null ? null : value.getFaultAfter());
        serviceRetries.setValue(value.getServiceRetries() == null ? "" : value.getServiceRetries()); //$NON-NLS-1$
        processingStartedFrom.setDateValue(value.getProcessingStartedFrom() == null ? null : value.getProcessingStartedFrom());
        processingStartedTo.setDateValue(value.getProcessingStartedTo() == null ? null : value.getProcessingStartedTo());
        onFilterValueChange();
    }

    /**
     * Refresh any data in the filter panel.
     */
    public void refresh() {
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<FaultsFilterBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

}
