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
public class FaultsFilterBean {

    private String serviceName;
    private String faultReason;
    private boolean archived;
    private Date faultAfter;
    private String serviceRetries;
    private Date processingStartedFrom;
    private Date processingStartedTo;

    /**
     * Constructor.
     */
    public FaultsFilterBean() {
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @return the faultReason
     */
    public String getFaultReason() {
        return faultReason;
    }

    /**
     * @return the archived
     */
    public boolean isArchived() {
        return archived;
    }

    /**
     * @return the faultAfter
     */
    public Date getFaultAfter() {
        return faultAfter;
    }

    /**
     * @return the serviceRetries
     */
    public String getServiceRetries() {
        return serviceRetries;
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
    public FaultsFilterBean setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    /**
     * @param faultReason the faultReason to set
     */
    public FaultsFilterBean setFaultReason(String faultReason) {
        this.faultReason = faultReason;
        return this;
    }

    /**
     * @param archived the archived to set
     */
    public FaultsFilterBean setArchived(boolean archived) {
        this.archived = archived;
        return this;
    }

    /**
     * @param faultAfter the faultAfter to set
     */
    public FaultsFilterBean setFaultAfter(Date faultAfter) {
        this.faultAfter = faultAfter;
        return this;
    }

    /**
     * @param serviceRetries the serviceRetries to set
     */
    public FaultsFilterBean setServiceRetries(String serviceRetries) {
        this.serviceRetries = serviceRetries;
        return this;
    }

    /**
     * @param processingStartedFrom the processingStartedFrom to set
     */
    public FaultsFilterBean setProcessingStartedFrom(Date processingStartedFrom) {
        this.processingStartedFrom = processingStartedFrom;
        return this;
    }

    /**
     * @param processingStartedTo the processingStartedTo to set
     */
    public FaultsFilterBean setProcessingStartedTo(Date processingStartedTo) {
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
        result = prime * result + (archived ? 1231 : 1237);
        result = prime * result + ((faultAfter == null) ? 0 : faultAfter.hashCode());
        result = prime * result + ((faultReason == null) ? 0 : faultReason.hashCode());
        result = prime * result + ((processingStartedFrom == null) ? 0 : processingStartedFrom.hashCode());
        result = prime * result + ((processingStartedTo == null) ? 0 : processingStartedTo.hashCode());
        result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
        result = prime * result + ((serviceRetries == null) ? 0 : serviceRetries.hashCode());
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
        FaultsFilterBean other = (FaultsFilterBean) obj;
        if (archived != other.archived)
            return false;
        if (faultAfter == null) {
            if (other.faultAfter != null)
                return false;
        } else if (!faultAfter.equals(other.faultAfter))
            return false;
        if (faultReason == null) {
            if (other.faultReason != null)
                return false;
        } else if (!faultReason.equals(other.faultReason))
            return false;
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
        if (serviceName == null) {
            if (other.serviceName != null)
                return false;
        } else if (!serviceName.equals(other.serviceName))
            return false;
        if (serviceRetries == null) {
            if (other.serviceRetries != null)
                return false;
        } else if (!serviceRetries.equals(other.serviceRetries))
            return false;
        return true;
    }

}
