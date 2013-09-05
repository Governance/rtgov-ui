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
 *
 * @author eric.wittmann@redhat.com
 */
@Portable
public class QName implements Serializable {

    private static final long serialVersionUID = 5117186649985575810L;

    private String namespaceURI;
    private String localPart;

    /**
     * Constructor.
     */
    public QName() {
    }

    /**
     * Constructor.
     * @param namespaceURI
     * @param localPart
     */
    public QName(String namespaceURI, String localPart) {
        this.namespaceURI = namespaceURI;
        this.localPart = localPart;
    }

    /**
     * @return the namespaceURI
     */
    public String getNamespaceURI() {
        return namespaceURI;
    }

    /**
     * @return the localPart
     */
    public String getLocalPart() {
        return localPart;
    }

    /**
     * @param namespaceURI the namespaceURI to set
     */
    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    /**
     * @param localPart the localPart to set
     */
    public void setLocalPart(String localPart) {
        this.localPart = localPart;
    }

}
