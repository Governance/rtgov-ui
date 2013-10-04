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
package org.overlord.monitoring.ui.client.local.pages.situations;

import java.util.Map.Entry;
import org.overlord.monitoring.ui.client.shared.beans.TraceNodeBean;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

/**
 * Details widget when the user clicks on a node in the call trace widget.
 * @author eric.wittmann@redhat.com
 */
public class CallTraceDetails extends FlowPanel implements HasValue<TraceNodeBean> {

    /**
     * Constructor.
     */
    public CallTraceDetails() {
        setStyleName("span6"); //$NON-NLS-1$
        addStyleName("details-meta-data-section"); //$NON-NLS-1$
        addStyleName("call-trace-detail"); //$NON-NLS-1$
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<TraceNodeBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public TraceNodeBean getValue() {
        return null;
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(TraceNodeBean value) {
        setValue(value, false);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(TraceNodeBean value, boolean fireEvents) {
        clear();

        boolean isCall = "Call".equals(value.getType()); //$NON-NLS-1$
        Label label = null;

        if (isCall) {
            label = new Label("Selected Node");
            label.setStyleName("details-meta-data-section-header"); //$NON-NLS-1$
            add(label);
            addClearFix();
            label = new Label(value.getOperation() + ":{" + value.getComponent() + "}"); //$NON-NLS-1$ //$NON-NLS-2$
            label.setStyleName("details-meta-data-section-value"); //$NON-NLS-1$
            add(label);
            addClearFix();
            addDivider();
        }

        label = new Label("Summary");
        label.setStyleName("details-meta-data-section-header"); //$NON-NLS-1$
        add(label);
        addClearFix();

        addProperty("Type", value.getType());
        addProperty("Component", value.getComponent());
        addProperty("Interface", value.getIface());
        addProperty("Operation", value.getOperation());
        addProperty("Request", value.getRequest());
        addProperty("Response", value.getResponse());
        addProperty("Fault", value.getFault());
        if (value.getRequestLatency() != -1)
            addProperty("Request Latency", String.valueOf(value.getRequestLatency()) + "ms"); //$NON-NLS-2$
        if (value.getResponseLatency() != -1)
            addProperty("Response Latency", String.valueOf(value.getResponseLatency()) + "ms"); //$NON-NLS-2$
        if (value.getDuration() != -1)
            addProperty("Duration", String.valueOf(value.getDuration()) + "ms"); //$NON-NLS-2$
        if (value.getPercentage() != -1)
            addProperty("Percentage", String.valueOf(value.getPercentage()) + "ms"); //$NON-NLS-2$

        if (!value.getProperties().isEmpty()) {
            addDivider();
            label = new Label("Properties");
            label.setStyleName("details-meta-data-section-header"); //$NON-NLS-1$
            add(label);
            addClearFix();

            for (Entry<String, String> entry : value.getProperties().entrySet()) {
                addProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * @param label
     * @param value
     */
    protected void addProperty(String label, String value) {
        if (value == null)
            return;

        Label widget;
        widget = new Label(label);
        widget.setStyleName("details-meta-data-section-label"); //$NON-NLS-1$
        add(widget);
        widget = new Label(value);
        widget.setStyleName("details-meta-data-section-value"); //$NON-NLS-1$
        add(widget);
        addClearFix();
    }

    /**
     * Adds a clearfix div.
     */
    private void addClearFix() {
        Label label = new Label(" "); //$NON-NLS-1$
        label.setStyleName("clearfix"); //$NON-NLS-1$
        add(label);
    }

    /**
     * Adds a divider div.
     */
    private void addDivider() {
        Label label = new Label(" "); //$NON-NLS-1$
        label.setStyleName("divider-20"); //$NON-NLS-1$
        add(label);
    }

}
