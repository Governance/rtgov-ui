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
package org.overlord.monitoring.ui.server.services.impl;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.overlord.rtgov.analytics.situation.Situation;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;

/**
 * This class provides the SwitchYard implementation of the ResubmitHandler
 * interface.
 *
 */
public class SwitchYardResubmitHandler implements ResubmitHandler {
	
	private static final String REMOTE_INVOKER_URL = "http://localhost:8080/switchyard-remote";

	/**
	 * {@inheritDoc}
	 */
	public void resubmit(Situation situation, String message) throws Exception {

		// Currently assumes message is xml
		DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
		
		java.io.InputStream is=new java.io.ByteArrayInputStream(message.getBytes());
		
		org.w3c.dom.Document doc=builder.parse(is);
		
		is.close();

		Object content=new DOMSource(doc.getDocumentElement());

		// Create a new remote client invoker
		RemoteInvoker invoker = new HttpInvoker(REMOTE_INVOKER_URL);
		
		String parts[]=situation.getSubject().split("\\x7C");
		
		if (parts.length != 2) {
			throw new Exception("Expecting 2 parts in the subject '"+situation.getSubject()+"', but got "+parts.length);
		}

		// Create the request message
		RemoteMessage rm = new RemoteMessage();
		rm.setService(QName.valueOf(parts[0])).setOperation(parts[1]).setContent(content);

		// Invoke the service
		RemoteMessage reply = invoker.invoke(rm);
		if (reply.isFault()) {
			if (reply.getContent() instanceof Exception) {
				throw (Exception)reply.getContent();
			}
			throw new Exception("Fault response received: "+reply.getContent());
		}

	}

}
