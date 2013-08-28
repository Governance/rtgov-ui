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

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * All of the user's filter settings (configured on the left-hand sidebar of
 * the Services page).
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
public class ServicesFilterBean {

    private String serviceName;
    private String serviceCategory;
    private String processingState;
    private String serviceStyle;
    private Date processingStartedFrom;
    private Date processingStartedTo;

    /**
     * Constructor.
     */
    public ServicesFilterBean() {
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return the serviceCategory
     */
    public String getServiceCategory() {
        return serviceCategory;
    }

    /**
     * @return the processingState
     */
    public String getProcessingState() {
        return processingState;
    }

    /**
     * @return the serviceStyle
     */
    public String getServiceStyle() {
        return serviceStyle;
    }

    /**
     * @return the processingStartedFrom
     */
    public Date getProcessingStartedFrom() {
        return processingStartedFrom;
    }

    /**
     * @return the processingStartedTo
     */
    public Date getProcessingStartedTo() {
        return processingStartedTo;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public ServicesFilterBean setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    /**
     * @param serviceCategory the serviceCategory to set
     */
    public ServicesFilterBean setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
        return this;
    }

    /**
     * @param processingState the processingState to set
     */
    public ServicesFilterBean setProcessingState(String processingState) {
        this.processingState = processingState;
        return this;
    }

    /**
     * @param serviceStyle the serviceStyle to set
     */
    public ServicesFilterBean setServiceStyle(String serviceStyle) {
        this.serviceStyle = serviceStyle;
        return this;
    }

    /**
     * @param processingStartedFrom the processingStartedFrom to set
     */
    public ServicesFilterBean setProcessingStartedFrom(Date processingStartedFrom) {
        this.processingStartedFrom = processingStartedFrom;
        return this;
    }

    /**
     * @param processingStartedTo the processingStartedTo to set
     */
    public ServicesFilterBean setProcessingStartedTo(Date processingStartedTo) {
        this.processingStartedTo = processingStartedTo;
        return this;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((processingStartedFrom == null) ? 0 : processingStartedFrom.hashCode());
        result = prime * result + ((processingStartedTo == null) ? 0 : processingStartedTo.hashCode());
        result = prime * result + ((processingState == null) ? 0 : processingState.hashCode());
        result = prime * result + ((serviceCategory == null) ? 0 : serviceCategory.hashCode());
        result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
        result = prime * result + ((serviceStyle == null) ? 0 : serviceStyle.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServicesFilterBean other = (ServicesFilterBean) obj;
        if (processingStartedFrom == null) {
            if (other.processingStartedFrom != null)
                return false;
        } else if (!processingStartedFrom.equals(other.processingStartedFrom))
            return false;
        if (processingStartedTo == null) {
            if (other.processingStartedTo != null)
                return false;
        } else if (!processingStartedTo.equals(other.processingStartedTo))
            return false;
        if (processingState == null) {
            if (other.processingState != null)
                return false;
        } else if (!processingState.equals(other.processingState))
            return false;
        if (serviceCategory == null) {
            if (other.serviceCategory != null)
                return false;
        } else if (!serviceCategory.equals(other.serviceCategory))
            return false;
        if (serviceName == null) {
            if (other.serviceName != null)
                return false;
        } else if (!serviceName.equals(other.serviceName))
            return false;
        if (serviceStyle == null) {
            if (other.serviceStyle != null)
                return false;
        } else if (!serviceStyle.equals(other.serviceStyle))
            return false;
        return true;
    }

}
