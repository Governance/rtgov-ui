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
public class ServiceSummaryBean {

    private String name;
    private String category;
    private String mep;
    private String address;
    private long averageDuration;
    private Date lastActivity;

    /**
     * Constructor.
     */
    public ServiceSummaryBean() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @return the mep
     */
    public String getMep() {
        return mep;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return the averageDuration
     */
    public long getAverageDuration() {
        return averageDuration;
    }

    /**
     * @return the lastActivity
     */
    public Date getLastActivity() {
        return lastActivity;
    }

    /**
     * @param name the name to set
     */
    public ServiceSummaryBean setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param category the category to set
     */
    public ServiceSummaryBean setCategory(String category) {
        this.category = category;
        return this;
    }

    /**
     * @param mep the mep to set
     */
    public ServiceSummaryBean setMep(String mep) {
        this.mep = mep;
        return this;
    }

    /**
     * @param address the address to set
     */
    public ServiceSummaryBean setAddress(String address) {
        this.address = address;
        return this;
    }

    /**
     * @param averageDuration the averageDuration to set
     */
    public ServiceSummaryBean setAverageDuration(long averageDuration) {
        this.averageDuration = averageDuration;
        return this;
    }

    /**
     * @param lastActivity the lastActivity to set
     */
    public ServiceSummaryBean setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
        return this;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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
        ServiceSummaryBean other = (ServiceSummaryBean) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
