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

import java.util.List;

import javax.enterprise.context.Dependent;

import org.overlord.monitoring.ui.client.shared.beans.ReferenceMetric;
import org.overlord.sramp.ui.client.local.widgets.common.TemplatedWidgetTable;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * A table of reference metrics.
 *
 * @author eric.wittmann@redhat.com
 */
@Dependent
public class ReferenceMetricsTable extends TemplatedWidgetTable implements HasValue<List<ReferenceMetric>> {

    /**
     * Constructor.
     */
    public ReferenceMetricsTable() {
    }

    /**
     * Adds a single row to the table.
     * @param bean
     */
    public void addRow(final ReferenceMetric bean) {
        int rowIdx = this.rowElements.size();

        InlineLabel name = new InlineLabel(bean.getName());
        InlineLabel msgCount = new InlineLabel(String.valueOf(bean.getMessageCount()));
        InlineLabel avgTime = new InlineLabel(String.valueOf(bean.getAverageTime()));
        InlineLabel timePerc = new InlineLabel(String.valueOf(bean.getTimePercent()) + "%"); //$NON-NLS-1$
        InlineLabel faultPerc = new InlineLabel(String.valueOf(bean.getName()) + "%"); //$NON-NLS-1$

        add(rowIdx, 0, name);
        add(rowIdx, 1, msgCount);
        add(rowIdx, 2, avgTime);
        add(rowIdx, 3, timePerc);
        add(rowIdx, 4, faultPerc);
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<List<ReferenceMetric>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public List<ReferenceMetric> getValue() {
        // Not implemented (the table is read-only)
        return null;
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(List<ReferenceMetric> value) {
        setValue(value, true);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(List<ReferenceMetric> value, boolean fireEvents) {
        clear();
        for (ReferenceMetric metric : value) {
            addRow(metric);
        }
    }

}
