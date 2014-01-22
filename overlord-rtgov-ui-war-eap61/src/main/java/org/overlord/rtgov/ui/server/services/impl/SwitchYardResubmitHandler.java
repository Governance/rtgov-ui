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
package org.overlord.rtgov.ui.server.services.impl;

import java.util.Collections;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.overlord.rtgov.common.util.RTGovProperties;
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
	
	private static final String SWITCHYARD_RESUBMIT_HANDLER_SERVER_URLS = "SwitchYardResubmitHandler.serverURLs";

	protected static final String DEFAULT_REMOTE_INVOKER_URL = "http://localhost:8080/switchyard-remote";
	
	private String _serverURLs=null;
    
	private java.util.List<String> _urlList=new java.util.ArrayList<String>();

	/**
	 * This method sets the comma separated list of SwitchYard server URLs.
	 * 
	 * @param urls The server URLs
	 */
	public void setServerURLs(String urls) {
		synchronized (_urlList) {
			_serverURLs = urls;
			
			_urlList.clear();
		}
	}
	
	/**
	 * This method returns the comma separated list of SwitchYard server URLs.
	 * 
	 * @return The server URLs
	 */
	public String getServerURLs() {
		if (_serverURLs == null) {
			_serverURLs = RTGovProperties.getProperties().getProperty(SWITCHYARD_RESUBMIT_HANDLER_SERVER_URLS);
		}
		return (_serverURLs);
	}
	
	/**
	 * This method returns a list of URLs to use for a particular invocation.
	 * If multiple URLs are available, the list will round robin to balance the
	 * load - however if one URL fails, then the next one in the list will be
	 * tried until successful or end of list reached.
	 * 
	 * @return The list of URLs
	 */
	protected java.util.List<String> getURLList() {
		java.util.List<String> ret=null;
		
		synchronized (_urlList) {
			if (_urlList.size() == 0) {
				
				if (getServerURLs() != null && getServerURLs().trim().length() > 0) {
					String[] urls=getServerURLs().split("[, ]");
					
					for (int i=0; i < urls.length; i++) {
						String url=urls[i].trim();
						
						if (url.length() > 0) {
							_urlList.add(url);
						}
					}
					
				} else {
					_urlList.add(DEFAULT_REMOTE_INVOKER_URL);
				}
			}
			
			if (_urlList.size() == 1) {
				// Only one entry in the list, so just return it
				ret = _urlList;
			} else {
				ret = new java.util.ArrayList<String>(_urlList);
				
				Collections.rotate(_urlList, -1);
			}
		}
		
		return (ret);
	}
	
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

		java.util.List<String> urls=getURLList();
		Exception exc=null;
		
		for (int i=0; i < urls.size(); i++) {
			try {
				// Create a new remote client invoker
				RemoteInvoker invoker = new HttpInvoker(urls.get(i));
				
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
				
				// Clear previous exceptions
				exc = null;
				
				continue;
			} catch (java.io.IOException e) {
				exc = e;
			}
		}
		
		if (exc != null) {
			// Report exception
			throw exc;
		}
	}

}
