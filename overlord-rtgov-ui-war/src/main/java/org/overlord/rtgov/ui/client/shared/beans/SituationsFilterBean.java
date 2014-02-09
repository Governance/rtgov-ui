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
package org.overlord.rtgov.ui.client.shared.beans;

import java.util.Date;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * All of the user's filter settings (configured on the left-hand sidebar of
 * the Situations page).
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
public class SituationsFilterBean {

    private String severity;
    private String type;
    private String resolutionState;
    private Date timestampFrom;
    private Date timestampTo;

    /**
     * Constructor.
     */
    public SituationsFilterBean() {
    }

    /**
     * @return the severity
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
	 * @return the resolutionState
	 */
	public String getResolutionState() {
		return resolutionState;
	}

	/**
     * @return the timestampFrom
     */
    public Date getTimestampFrom() {
        return timestampFrom;
    }

    /**
     * @return the timestampTo
     */
    public Date getTimestampTo() {
        return timestampTo;
    }

    /**
     * @param severity the severity to set
     */
    public SituationsFilterBean setSeverity(String severity) {
        this.severity = severity;
        return this;
    }

    /**
     * @param type the type to set
     */
    public SituationsFilterBean setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * @param timestampFrom the timestampFrom to set
     */
    public SituationsFilterBean setTimestampFrom(Date timestampFrom) {
        this.timestampFrom = timestampFrom;
        return this;
    }

    /**
     * @param timestampTo the timestampTo to set
     */
    public SituationsFilterBean setTimestampTo(Date timestampTo) {
        this.timestampTo = timestampTo;
        return this;
    }

	/**
	 * @param resolutionState the resolutionState to set
	 */
	public SituationsFilterBean setResolutionState(String resolutionState) {
		this.resolutionState = resolutionState;
		return this;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((resolutionState == null) ? 0 : resolutionState.hashCode());
		result = prime * result
				+ ((severity == null) ? 0 : severity.hashCode());
		result = prime * result
				+ ((timestampFrom == null) ? 0 : timestampFrom.hashCode());
		result = prime * result
				+ ((timestampTo == null) ? 0 : timestampTo.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/* (non-Javadoc)
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
		SituationsFilterBean other = (SituationsFilterBean) obj;
		if (resolutionState == null) {
			if (other.resolutionState != null)
				return false;
		} else if (!resolutionState.equals(other.resolutionState))
			return false;
		if (severity == null) {
			if (other.severity != null)
				return false;
		} else if (!severity.equals(other.severity))
			return false;
		if (timestampFrom == null) {
			if (other.timestampFrom != null)
				return false;
		} else if (!timestampFrom.equals(other.timestampFrom))
			return false;
		if (timestampTo == null) {
			if (other.timestampTo != null)
				return false;
		} else if (!timestampTo.equals(other.timestampTo))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SituationsFilterBean [severity=" + severity + ", type=" + type
				+ ", resolutionState=" + resolutionState + ", timestampFrom="
				+ timestampFrom + ", timestampTo=" + timestampTo + "]";
	}

}
