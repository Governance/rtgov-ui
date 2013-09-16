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
import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

/**
 * Models the full details of a reference.
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
@Bindable
public class ReferenceBean implements Serializable {

    private static final long serialVersionUID = ReferenceBean.class.hashCode();

    private String referenceId;
    private QName name;
    private QName application;
    private String referenceInterface;
    private long successCount;
    private long faultCount;
    private long totalTime;
    private long averageTime;
    private long minTime;
    private long maxTime;
    private List<GatewayMetric> gatewayMetrics = new ArrayList<GatewayMetric>();

    /**
     * Constructor.
     */
    public ReferenceBean() {
    }

    /**
     * @return the referenceId
     */
    public String getReferenceId() {
        return referenceId;
    }

    /**
     * @return the name
     */
    public QName getName() {
        return name;
    }

    /**
     * @return the application
     */
    public QName getApplication() {
        return application;
    }

    /**
     * @return the referenceInterface
     */
    public String getReferenceInterface() {
        return referenceInterface;
    }

    /**
     * @return the successCount
     */
    public long getSuccessCount() {
        return successCount;
    }

    /**
     * @return the faultCount
     */
    public long getFaultCount() {
        return faultCount;
    }

    /**
     * @return the totalTime
     */
    public long getTotalTime() {
        return totalTime;
    }

    /**
     * @return the averageTime
     */
    public long getAverageTime() {
        return averageTime;
    }

    /**
     * @return the minTime
     */
    public long getMinTime() {
        return minTime;
    }

    /**
     * @return the maxTime
     */
    public long getMaxTime() {
        return maxTime;
    }

    /**
     * @param referenceId the referenceId to set
     */
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * @param name the name to set
     */
    public void setName(QName name) {
        this.name = name;
    }

    /**
     * @param application the application to set
     */
    public void setApplication(QName application) {
        this.application = application;
    }

    /**
     * @param referenceInterface the referenceInterface to set
     */
    public void setReferenceInterface(String referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

    /**
     * @param successCount the successCount to set
     */
    public void setSuccessCount(long successCount) {
        this.successCount = successCount;
    }

    /**
     * @param faultCount the faultCount to set
     */
    public void setFaultCount(long faultCount) {
        this.faultCount = faultCount;
    }

    /**
     * @param totalTime the totalTime to set
     */
    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    /**
     * @param averageTime the averageTime to set
     */
    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    /**
     * @param minTime the minTime to set
     */
    public void setMinTime(long minTime) {
        this.minTime = minTime;
    }

    /**
     * @param maxTime the maxTime to set
     */
    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    /**
     * Adds a single gateway metric to the bean.
     * @param name
     * @param type
     * @param messageCount
     * @param averageTime
     * @param timePercent
     * @param faultPercent
     */
    public void addGatewayMetric(String name, String type, long messageCount, long averageTime, int timePercent, int faultPercent) {
        GatewayMetric metric = new GatewayMetric();
        metric.setName(name);
        metric.setType(type);
        metric.setMessageCount(messageCount);
        metric.setAverageTime(averageTime);
        metric.setTimePercent(timePercent);
        metric.setFaultPercent(faultPercent);
        this.getGatewayMetrics().add(metric);
    }

    /**
     * @return the gatewayMetrics
     */
    public List<GatewayMetric> getGatewayMetrics() {
        return gatewayMetrics;
    }

    /**
     * @param gatewayMetrics the gatewayMetrics to set
     */
    public void setGatewayMetrics(List<GatewayMetric> gatewayMetrics) {
        this.gatewayMetrics = gatewayMetrics;
    }

}
