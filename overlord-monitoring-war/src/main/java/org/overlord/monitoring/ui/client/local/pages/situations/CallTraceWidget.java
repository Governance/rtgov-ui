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

import java.util.List;

import org.overlord.monitoring.ui.client.shared.beans.CallTraceBean;
import org.overlord.monitoring.ui.client.shared.beans.TraceNodeBean;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * A widget used to display a call trace.
 *
 * @author eric.wittmann@redhat.com
 */
public class CallTraceWidget extends Widget implements HasValue<CallTraceBean> {

    /**
     * Creates an empty flow panel.
     */
    public CallTraceWidget() {
        setElement((com.google.gwt.user.client.Element) Document.get().createULElement().cast());
    }

    /**
     * @see com.google.gwt.event.logical.shared.HasValueChangeHandlers#addValueChangeHandler(com.google.gwt.event.logical.shared.ValueChangeHandler)
     */
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<CallTraceBean> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#getValue()
     */
    @Override
    public CallTraceBean getValue() {
        return null;
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object)
     */
    @Override
    public void setValue(CallTraceBean value) {
        setValue(value, false);
    }

    /**
     * @see com.google.gwt.user.client.ui.HasValue#setValue(java.lang.Object, boolean)
     */
    @Override
    public void setValue(CallTraceBean value, boolean fireEvents) {
        if (value != null) {
            List<TraceNodeBean> tasks = value.getTasks();
            for (TraceNodeBean task : tasks) {
                LIElement li = createTreeNode(task);
                getElement().appendChild(li);
            }
        }
    }

    /**
     * Creates a single tree node from the given trace node.
     * @param node
     */
    protected LIElement createTreeNode(TraceNodeBean node) {
        boolean hasChildren = !node.getTasks().isEmpty();
        LIElement li = Document.get().createLIElement();
        li.setClassName(hasChildren ? "parent_li" : "leaf_li"); //$NON-NLS-1$ //$NON-NLS-2$
        if (hasChildren)
            li.setAttribute("role", "treeitem"); //$NON-NLS-1$ //$NON-NLS-2$
        SpanElement span = Document.get().createSpanElement();
        Element icon = Document.get().createElement("i"); //$NON-NLS-1$
        span.appendChild(icon);
        span.appendChild(Document.get().createTextNode(" ")); //$NON-NLS-1$
        if (node.getDescription() != null) {
            span.appendChild(Document.get().createTextNode(node.getDescription()));
            span.setClassName("Info"); //$NON-NLS-1$
            icon.setClassName("icon-info-sign"); //$NON-NLS-1$
        } else {
            span.setClassName(node.getStatus());
            icon.setClassName("icon-minus-sign"); //$NON-NLS-1$
            span.appendChild(Document.get().createTextNode(node.getOperation()));
            span.appendChild(Document.get().createTextNode(":{")); //$NON-NLS-1$
            span.appendChild(Document.get().createTextNode(node.getIface()));
            span.appendChild(Document.get().createTextNode("}")); //$NON-NLS-1$
        }
        li.appendChild(span);
        li.appendChild(Document.get().createTextNode(" [")); //$NON-NLS-1$
        li.appendChild(Document.get().createTextNode(String.valueOf(node.getDuration())));
        li.appendChild(Document.get().createTextNode("ms] (")); //$NON-NLS-1$
        li.appendChild(Document.get().createTextNode(String.valueOf(node.getPercentage())));
        li.appendChild(Document.get().createTextNode("%)")); //$NON-NLS-1$

        if (hasChildren) {
            UListElement ul = Document.get().createULElement();
            ul.setAttribute("role", "group"); //$NON-NLS-1$ //$NON-NLS-2$
            li.appendChild(ul);

            List<TraceNodeBean> tasks = node.getTasks();
            for (TraceNodeBean task : tasks) {
                LIElement tn = createTreeNode(task);
                ul.appendChild(tn);
            }
        }
        return li;
    }
}
