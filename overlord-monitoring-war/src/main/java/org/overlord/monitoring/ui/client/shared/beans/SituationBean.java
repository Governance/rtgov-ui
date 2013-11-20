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
package org.overlord.monitoring.ui.client.shared.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Models the full details of a situation.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class SituationBean extends SituationSummaryBean implements Serializable {

    private static final long serialVersionUID = SituationBean.class.hashCode();

    private Map<String, String> context = new HashMap<String, String>();
    private MessageBean message;
    private CallTraceBean callTrace;

    /**
     * Constructor.
     */
    public SituationBean() {
    }

    /**
     * @return the context
     */
    public Map<String, String> getContext() {
        return context;
    }

    /**
     * @param context the context to set
     */
    public void setContext(Map<String, String> context) {
        this.context = context;
    }
    
    /**
     * This method returns the optional request message associated with the situation.
     * 
     * @return The optional request message
     */
    public MessageBean getMessage() {
    	return message;
    }
    
    /**
     * This method sets the optional request message.
     * 
     * @param mesg The optional request message
     */
    public void setMessage(MessageBean mesg) {
    	this.message = mesg;
    }

    /**
     * @return the callTrace
     */
    public CallTraceBean getCallTrace() {
        return callTrace;
    }

    /**
     * @param callTrace the callTrace to set
     */
    public void setCallTrace(CallTraceBean callTrace) {
        this.callTrace = callTrace;
    }

}
