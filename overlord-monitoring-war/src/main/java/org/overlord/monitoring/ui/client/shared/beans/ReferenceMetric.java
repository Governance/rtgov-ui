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

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * @author eric.wittmann@redhat.com
 */
@Portable
public class ReferenceMetric implements Serializable {

    private static final long serialVersionUID = 6537537034504237001L;

    private String name;
    private long messageCount;
    private long averageTime;
    private int timePercent;
    private int faultPercent;

    /**
     * Constructor.
     */
    public ReferenceMetric() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the messageCount
     */
    public long getMessageCount() {
        return messageCount;
    }

    /**
     * @return the averageTime
     */
    public long getAverageTime() {
        return averageTime;
    }

    /**
     * @return the timePercent
     */
    public int getTimePercent() {
        return timePercent;
    }

    /**
     * @return the faultPercent
     */
    public int getFaultPercent() {
        return faultPercent;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param messageCount the messageCount to set
     */
    public void setMessageCount(long messageCount) {
        this.messageCount = messageCount;
    }

    /**
     * @param averageTime the averageTime to set
     */
    public void setAverageTime(long averageTime) {
        this.averageTime = averageTime;
    }

    /**
     * @param timePercent the timePercent to set
     */
    public void setTimePercent(int timePercent) {
        this.timePercent = timePercent;
    }

    /**
     * @param faultPercent the faultPercent to set
     */
    public void setFaultPercent(int faultPercent) {
        this.faultPercent = faultPercent;
    }

}
