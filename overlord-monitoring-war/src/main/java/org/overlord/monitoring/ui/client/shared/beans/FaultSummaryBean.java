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
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * A simple data bean for returning summary information for single deployment.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class FaultSummaryBean {

    private String faultId;
    private String serviceName;
    private String faultReason;
    private Date faultTime;

    /**
     * Constructor.
     */
    public FaultSummaryBean() {
    }

    /**
     * @return the faultId
     */
    public String getFaultId() {
        return faultId;
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
     * @return the faultTime
     */
    public Date getFaultTime() {
        return faultTime;
    }

    /**
     * @param faultId the faultId to set
     */
    public void setFaultId(String faultId) {
        this.faultId = faultId;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * @param faultReason the faultReason to set
     */
    public void setFaultReason(String faultReason) {
        this.faultReason = faultReason;
    }

    /**
     * @param faultTime the faultTime to set
     */
    public void setFaultTime(Date faultTime) {
        this.faultTime = faultTime;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((faultId == null) ? 0 : faultId.hashCode());
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
        FaultSummaryBean other = (FaultSummaryBean) obj;
        if (faultId == null) {
            if (other.faultId != null)
                return false;
        } else if (!faultId.equals(other.faultId))
            return false;
        return true;
    }

}
