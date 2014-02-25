/*
 * Copyright 2013-4 Red Hat Inc
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
package org.overlord.rtgov.ui.provider.switchyard;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;

import org.jboss.dmr.ModelNode;
import org.overlord.rtgov.common.util.RTGovProperties;
import org.overlord.rtgov.ui.client.model.GatewayMetric;
import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ReferenceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServicesFilterBean;
import org.overlord.rtgov.ui.client.model.UiException;
import org.switchyard.remote.RemoteInvoker;
import org.switchyard.remote.RemoteMessage;
import org.switchyard.remote.http.HttpInvoker;

public class SwitchYardServicesProviderDMR { //implements org.overlord.rtgov.ui.provider.ServicesProvider {
	
    private static volatile Messages i18n = new Messages();

	private static final String PROVIDER_NAME = "switchyard";

	private static final String SWITCHYARD_RESUBMIT_HANDLER_SERVER_URLS = "SwitchYardServiceProvider.serverURLs";

	protected static final String DEFAULT_REMOTE_INVOKER_URL = "http://localhost:8080/switchyard-remote";
	
	private String _serverURLs=null;
    
	private java.util.List<String> _urlList=new java.util.ArrayList<String>();

    private static final String SUBSYSTEM = "subsystem"; //$NON-NLS-1$
    private static final String SWITCHYARD = "switchyard"; //$NON-NLS-1$

    private static final String OPERATION = "operation"; //$NON-NLS-1$
    private static final String ADDRESS = "address"; //$NON-NLS-1$
    private static final String APPLICATION_NAME = "application-name"; //$NON-NLS-1$
    private static final String SERVICE_NAME = "service-name"; //$NON-NLS-1$
    private static final String REFERENCE_NAME = "reference-name"; //$NON-NLS-1$

    private static final String NAME = "name"; //$NON-NLS-1$
    private static final String APPLICATION = "application"; //$NON-NLS-1$
    private static final String INTERFACE = "interface"; //$NON-NLS-1$
    private static final String GATEWAYS = "gateways"; //$NON-NLS-1$
    private static final String PROMOTED_REFERENCE = "promotedReference"; //$NON-NLS-1$
    private static final String PROMOTED_SERVICE = "promotedService"; //$NON-NLS-1$
    private static final String TYPE = "type"; //$NON-NLS-1$

    private static final String SUCCESS_COUNT = "successCount"; //$NON-NLS-1$
    private static final String FAULT_COUNT = "faultCount"; //$NON-NLS-1$
    private static final String AVERAGE_TIME = "averageTime"; //$NON-NLS-1$
    private static final String MIN_TIME = "minTime"; //$NON-NLS-1$
    private static final String MAX_TIME = "maxTime"; //$NON-NLS-1$
    private static final String TOTAL_TIME = "totalTime"; //$NON-NLS-1$
    private static final String TOTAL_COUNT = "totalCount"; //$NON-NLS-1$

    private static final String LIST_APPLICATIONS = "list-applications"; //$NON-NLS-1$
    private static final String READ_SERVICE = "read-service"; //$NON-NLS-1$
    private static final String READ_REFERENCE = "read-reference"; //$NON-NLS-1$
    private static final String SHOW_METRICS = "show-metrics"; //$NON-NLS-1$

    private static final String RESULT = "result"; //$NON-NLS-1$

    private static final char ESCAPE_CHAR = '\\';
    private static final char SEPARATOR_CHAR = ':';

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return PROVIDER_NAME;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isServiceKnown(String service) {
		// TODO:
		return (true);
	}

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
	public void resubmit(String service, String operation, MessageBean message) throws UiException {

		// Currently assumes message is xml
		org.w3c.dom.Document doc=null;
		
		try {
			DocumentBuilder builder=DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			java.io.InputStream is=new java.io.ByteArrayInputStream(message.getContent().getBytes());
			
			doc = builder.parse(is);
			
			is.close();
		} catch (Exception e) {
			throw new UiException(e);
		}

		Object content=new DOMSource(doc.getDocumentElement());

		java.util.List<String> urls=getURLList();
		Exception exc=null;
		
		for (int i=0; i < urls.size(); i++) {
			try {
				// Create a new remote client invoker
				RemoteInvoker invoker = new HttpInvoker(urls.get(i));
				
				// Create the request message
				RemoteMessage rm = new RemoteMessage();
				rm.setService(javax.xml.namespace.QName.valueOf(service)).setOperation(operation).setContent(content);
		
				// Invoke the service
				RemoteMessage reply = invoker.invoke(rm);
				if (reply.isFault()) {
					if (reply.getContent() instanceof Exception) {
						throw new UiException((Exception)reply.getContent());
					}
					throw new UiException("Fault response received: "+reply.getContent());
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
			throw new UiException(exc);
		}
	}

	/**
	 * {@inheritDoc}
	 */
    public List<QName> getApplicationNames() throws UiException {
        final List<QName> apps = new ArrayList<QName>();

        final ModelNode operation = getBlankOperation(LIST_APPLICATIONS);
        final ModelNode response = execute(operation);
        final ModelNode result = response.get(RESULT);
        if (result.isDefined()) {
            final List<ModelNode> applications = result.asList();
            for(ModelNode application: applications) {
                apps.add(parseQName(asString(application)));
            }
        }
        return apps;
    }

	/**
	 * {@inheritDoc}
	 */
    public java.util.List<ServiceSummaryBean> findServices(final ServicesFilterBean filters) throws UiException {
        final ArrayList<ServiceSummaryBean> services = new ArrayList<ServiceSummaryBean>();

        final String applicationName = filters.getApplicationName();
//        final String processingState = filters.getProcessingState();
        final String serviceName = filters.getServiceName();

        final ModelNode operation = getBlankOperation(READ_SERVICE);
        if (isSet(applicationName)) {
            operation.get(APPLICATION_NAME).set(applicationName);
        }
        if (isSet(serviceName)) {
            operation.get(SERVICE_NAME).set(serviceName);
        }

        final Map<String, Map<String, BigDecimal>> averageDurations = getAverageDuration(applicationName, serviceName, "service"); //$NON-NLS-1$

        final ModelNode response = execute(operation);
        final ModelNode result = response.get().get(RESULT);
        if (result.isDefined()) {
            for (final ModelNode serviceNode: result.asList()) {
                final ServiceSummaryBean service = createServiceSummaryBean(serviceNode, averageDurations);
                if (service != null) {
                    services.add(service);
                }
            }
        }

        return services;
    }

	/**
	 * This method returns the list of references associated with the supplied application
	 * and service.
	 * 
	 * @param applicationName The application
	 * @param serviceName The service name
	 * @return The list of references
	 * @throws UiException Failed to get the references
	 */
    protected List<ReferenceSummaryBean> getReferences(final String applicationName,
    							final String serviceName) throws UiException {
        final List<ReferenceSummaryBean> references = new ArrayList<ReferenceSummaryBean>();

        final ModelNode operation = getBlankOperation(READ_REFERENCE);
        if (isSet(applicationName)) {
            operation.get(APPLICATION_NAME).set(applicationName);
        }
        if (serviceName != null) {
            operation.get(SERVICE_NAME).set(serviceName);
        }

        final Map<String, Map<String, BigDecimal>> averageDurations = getAverageDuration(applicationName, serviceName, "reference"); //$NON-NLS-1$

        final ModelNode response = execute(operation);
        final ModelNode result = response.get().get(RESULT);
        if (result.isDefined()) {
            for (final ModelNode referenceNode: result.asList()) {
                final ReferenceSummaryBean reference = createReferenceSummaryBean(referenceNode, averageDurations);
                if (reference != null) {
                    references.add(reference);
                }
            }
        }

        return references;
    }

	/**
	 * {@inheritDoc}
	 */
    public ServiceBean getService(final String uuid) throws UiException {
        final ServiceBean serviceResult = new ServiceBean();

        final List<String> ids = parseId(uuid);
        if (ids.size() == 2) {
            final String applicationName = ids.get(0);
            final String serviceName = ids.get(1);

            final ModelNode operation = getBlankOperation(READ_SERVICE);
            operation.get(APPLICATION_NAME).set(applicationName);
            operation.get(SERVICE_NAME).set(serviceName);

            final ModelNode response = execute(operation);
            final ModelNode result = response.get().get(RESULT);
            if (result.isDefined()) {
                final List<ModelNode> services = result.asList();
                if ((services != null) && (services.size() == 1)) {
                    final ModelNode serviceNode = services.get(0);
                    serviceResult.setServiceId(uuid);
                    serviceResult.setApplication(parseQName(applicationName));
                    serviceResult.setName(parseQName(serviceName));
                    serviceResult.setServiceInterface(asString(serviceNode.get(INTERFACE)));

                    final ModelNode metrics = getMetrics(applicationName, serviceName, "service"); //$NON-NLS-1$
                    if (metrics.isDefined()) {
                        serviceResult.setSuccessCount(metrics.get(SUCCESS_COUNT).asLong());
                        serviceResult.setFaultCount(metrics.get(FAULT_COUNT).asLong());
                        final long totalTime = metrics.get(TOTAL_TIME).asLong();
                        serviceResult.setTotalTime(totalTime);
                        serviceResult.setAverageTime(metrics.get(AVERAGE_TIME).asLong());
                        serviceResult.setMaxTime(metrics.get(MAX_TIME).asLong());
                        serviceResult.setMinTime(metrics.get(MIN_TIME).asLong());

                        final ModelNode gateways = metrics.get(GATEWAYS);
                        if (gateways.isDefined()) {
                            final ModelNode systemMetrics = getSystemMetrics();
                            final int systemFaultCount = systemMetrics.isDefined() ? systemMetrics.get(FAULT_COUNT).asInt() : 0;
                            final long systemTotalTime = systemMetrics.isDefined() ? systemMetrics.get(TOTAL_TIME).asLong() : 0L;
                            final List<GatewayMetric> gatewayMetrics = new ArrayList<GatewayMetric>();
                            for (ModelNode gateway: gateways.asList()) {
                                final GatewayMetric gatewayMetric = new GatewayMetric();
                                gatewayMetric.setName(asString(gateway.get(NAME)));
                                gatewayMetric.setType(asString(gateway.get(TYPE)));
                                gatewayMetric.setAverageTime(gateway.get(AVERAGE_TIME).asLong());
                                gatewayMetric.setMessageCount(gateway.get(TOTAL_COUNT).asLong());
                                if (systemFaultCount > 0) {
                                    gatewayMetric.setFaultPercent((int)(100 * gateway.get(FAULT_COUNT).asLong() / systemFaultCount));
                                } else {
                                    gatewayMetric.setFaultPercent(0);
                                }
                                if (systemTotalTime > 0) {
                                    gatewayMetric.setTimePercent((int)(100 * gateway.get(TOTAL_TIME).asLong() / systemTotalTime));
                                } else {
                                    gatewayMetric.setTimePercent(0);
                                }
                                gatewayMetrics.add(gatewayMetric);
                            }
                            serviceResult.setGatewayMetrics(gatewayMetrics);
                        }
                    }
                    
                    // Get references
                    serviceResult.setReferences(getReferences(applicationName, serviceName));
                    
                    return serviceResult;
                }
            }
        }
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
    public ReferenceBean getReference(final String uuid) throws UiException {
        final ReferenceBean referenceResult = new ReferenceBean();

        final List<String> ids = parseId(uuid);
        if (ids.size() == 2) {
            final String applicationName = ids.get(0);
            final String referenceName = ids.get(1);

            final ModelNode operation = getBlankOperation(READ_REFERENCE);
            operation.get(APPLICATION_NAME).set(applicationName);
            operation.get(REFERENCE_NAME).set(referenceName);

            final ModelNode response = execute(operation);
            final ModelNode result = response.get().get(RESULT);
            if (result.isDefined()) {
                final List<ModelNode> references = result.asList();
                if ((references != null) && (references.size() == 1)) {
                    final ModelNode referenceNode = references.get(0);
                    referenceResult.setReferenceId(uuid);
                    referenceResult.setApplication(parseQName(applicationName));
                    referenceResult.setName(parseQName(referenceName));
                    referenceResult.setReferenceInterface(asString(referenceNode.get(INTERFACE)));

                    final ModelNode metrics = getMetrics(applicationName, referenceName, "reference"); //$NON-NLS-1$
                    if (metrics.isDefined()) {
                        referenceResult.setSuccessCount(metrics.get(SUCCESS_COUNT).asLong());
                        referenceResult.setFaultCount(metrics.get(FAULT_COUNT).asLong());
                        final long totalTime = metrics.get(TOTAL_TIME).asLong();
                        referenceResult.setTotalTime(totalTime);
                        referenceResult.setAverageTime(metrics.get(AVERAGE_TIME).asLong());
                        referenceResult.setMaxTime(metrics.get(MAX_TIME).asLong());
                        referenceResult.setMinTime(metrics.get(MIN_TIME).asLong());

                        final ModelNode gateways = metrics.get(GATEWAYS);
                        if (gateways.isDefined()) {
                            final ModelNode systemMetrics = getSystemMetrics();
                            final int systemFaultCount = systemMetrics.isDefined() ? systemMetrics.get(FAULT_COUNT).asInt() : 0;
                            final long systemTotalTime = systemMetrics.isDefined() ? systemMetrics.get(TOTAL_TIME).asLong() : 0L;
                            final List<GatewayMetric> gatewayMetrics = new ArrayList<GatewayMetric>();
                            for (ModelNode gateway: gateways.asList()) {
                                final GatewayMetric gatewayMetric = new GatewayMetric();
                                gatewayMetric.setName(asString(gateway.get(NAME)));
                                gatewayMetric.setType(asString(gateway.get(TYPE)));
                                gatewayMetric.setAverageTime(gateway.get(AVERAGE_TIME).asLong());
                                gatewayMetric.setMessageCount(gateway.get(TOTAL_COUNT).asLong());
                                if (systemFaultCount > 0) {
                                    gatewayMetric.setFaultPercent((int)(100 * gateway.get(FAULT_COUNT).asLong() / systemFaultCount));
                                } else {
                                    gatewayMetric.setFaultPercent(0);
                                }
                                if (systemTotalTime > 0) {
                                    gatewayMetric.setTimePercent((int)(100 * gateway.get(TOTAL_TIME).asLong() / systemTotalTime));
                                } else {
                                    gatewayMetric.setTimePercent(0);
                                }
                                gatewayMetrics.add(gatewayMetric);
                            }
                            referenceResult.setGatewayMetrics(gatewayMetrics);
                        }
                    }
                    return referenceResult;
                }
            }
        }
        return null;
    }

    private static ModelNode getBlankOperation(final String operation) {
        final ModelNode modelNode = new ModelNode();
        final ModelNode address = new ModelNode();
        address.add(SUBSYSTEM, SWITCHYARD);
        modelNode.get(ADDRESS).set(address);
        modelNode.get(OPERATION).set(operation);
        return modelNode;
    }

    private static Map<String, Map<String, BigDecimal>> getAverageDuration(final String applicationName, final String serviceName, final String type) throws UiException {
        final Map<String, Map<String, BigDecimal>> averageDurations = new TreeMap<String, Map<String, BigDecimal>>();

        final ModelNode operation = getBlankOperation(SHOW_METRICS);
        if (isSet(serviceName)) {
            operation.get(SERVICE_NAME).set(serviceName);
        } else {
            operation.get(SERVICE_NAME).set("*"); //$NON-NLS-1$
        }
        operation.get(TYPE).set(type);
        final ModelNode response = execute(operation);
        final ModelNode result = response.get().get(RESULT);
        if (result.isDefined()) {
            for (final ModelNode metricsNode: result.asList()) {
                final String nodeApplicationName = asString(metricsNode.get(APPLICATION));
                if ((applicationName == null) || applicationName.equals(nodeApplicationName)) {
                    Map<String, BigDecimal> applicationMap = averageDurations.get(nodeApplicationName);
                    if (applicationMap == null) {
                        applicationMap = new TreeMap<String, BigDecimal>();
                        averageDurations.put(nodeApplicationName, applicationMap);
                    }
                    final String nodeServiceName = asString(metricsNode.get(NAME));

                    applicationMap.put(nodeServiceName, metricsNode.get(AVERAGE_TIME).asBigDecimal());
                }
            }
        }
        return averageDurations;
    }

    private static ModelNode getMetrics(final String applicationName, final String serviceName, final String type) throws UiException {
        final ModelNode operation = getBlankOperation(SHOW_METRICS);
        operation.get(SERVICE_NAME).set(serviceName);
        operation.get(TYPE).set(type);
        final ModelNode response = execute(operation);
        final ModelNode result = response.get().get(RESULT);
        if (result.isDefined()) {
            for (final ModelNode metricsNode: result.asList()) {
                final String nodeApplicationName = asString(metricsNode.get(APPLICATION));
                if (applicationName.equals(nodeApplicationName)) {
                    return metricsNode;
                }
            }
        }
        return new ModelNode();
    }

    private static ModelNode getSystemMetrics() throws UiException {
        final ModelNode operation = getBlankOperation(SHOW_METRICS);
        final ModelNode response = execute(operation);
        final ModelNode result = response.get().get(RESULT);
        if (result.isDefined()) {
            final List<ModelNode> metrics = result.asList();
            if ((metrics != null) && (metrics.size() == 1)) {
                return metrics.get(0);
            }
        }
        return new ModelNode();
    }

    private static ServiceSummaryBean createServiceSummaryBean(final ModelNode serviceNode, final Map<String, Map<String, BigDecimal>> averageDurations) throws UiException {
        final ServiceSummaryBean result = new ServiceSummaryBean();

        final String name = asString(serviceNode.get(NAME));
        final String application = asString(serviceNode.get(APPLICATION));
        final String intface = asString(serviceNode.get(INTERFACE));
        final String promotedService = asString(serviceNode.get(PROMOTED_SERVICE));

        result.setServiceId(generateId(application, name));
        result.setApplication(application);
        result.setName(name);
        result.setIface(intface);

        final List<ModelNode> gateways = serviceNode.get(GATEWAYS).asList();
        if (!gateways.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for(ModelNode gateway: gateways) {
                final String type = asString(gateway.get(TYPE));
                if (type != null) {
                    sb.append(type).append(", "); //$NON-NLS-1$
                }
            }
            final int length = sb.length();
            if (length > 0) {
                sb.setLength(length-2);
            }
            result.setBindings(sb.toString());
        }

        if (promotedService != null) {
            final Map<String, BigDecimal> applicationMap = averageDurations.get(application);
            if (applicationMap != null) {
                final BigDecimal averageDuration = applicationMap.get(promotedService);
                if (averageDuration != null) {
                    result.setAverageDuration(averageDuration.longValue());
                }
            }
        }
        return result;
    }

    private static ReferenceSummaryBean createReferenceSummaryBean(final ModelNode referenceNode, final Map<String, Map<String, BigDecimal>> averageDurations) throws UiException {
        final ReferenceSummaryBean result = new ReferenceSummaryBean();

        final String name = asString(referenceNode.get(NAME));
        final String application = asString(referenceNode.get(APPLICATION));
        final String intface = asString(referenceNode.get(INTERFACE));
        final String promotedReference = asString(referenceNode.get(PROMOTED_REFERENCE));

        result.setReferenceId(generateId(application, name));
        result.setApplication(application);
        result.setName(name);
        result.setIface(intface);

        final List<ModelNode> gateways = referenceNode.get(GATEWAYS).asList();
        if (!gateways.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for(ModelNode gateway: gateways) {
                final String type = asString(gateway.get(TYPE));
                if (type != null) {
                    sb.append(type).append(", "); //$NON-NLS-1$
                }
            }
            final int length = sb.length();
            if (length > 0) {
                sb.setLength(length-2);
            }
            result.setBindings(sb.toString());
        }

        if (promotedReference != null) {
            final Map<String, BigDecimal> applicationMap = averageDurations.get(application);
            if (applicationMap != null) {
                final BigDecimal averageDuration = applicationMap.get(promotedReference);
                if (averageDuration != null) {
                    result.setAverageDuration(averageDuration.longValue());
                }
            }
        }
        return result;
    }

    private static ModelNode execute(final ModelNode operation) throws UiException {
        try {
            return RTGovUIServiceActivator.getClient().execute(operation);
        } catch (final IOException ioe) {
            throw new UiException(i18n.format("SwitchYardServicesProvider.DMROperationFailed"), ioe); //$NON-NLS-1$
        }
    }

    private static String generateId(final String application, final String name) {
        return escape(application) + ':' + escape(name);
    }

    private static List<String> parseId(final String id) {
        if (id == null) {
            return null;
        }
        final List<String> ids = new ArrayList<String>();
        final StringBuilder unescaped = new StringBuilder();

        final int length = id.length();
        for(int count = 0 ; count < length ; count++) {
            final char ch = id.charAt(count);
            switch (ch) {
            case ESCAPE_CHAR:
                count++;
                if (count < length) {
                    unescaped.append(id.charAt(count));
                }
                break;
            case SEPARATOR_CHAR:
                ids.add(unescaped.toString());
                unescaped.setLength(0);
                break;
            default:
                unescaped.append(ch);
            }
        }
        ids.add(unescaped.toString());
        return ids;
    }

    private static String escape(final String val) {
        if (val == null) {
            return null;
        }
        final StringBuilder escaped = new StringBuilder();
        final int length = val.length();
        for(int count = 0 ; count < length ; count++) {
            final char ch = val.charAt(count);
            switch (ch) {
            case ESCAPE_CHAR:
            case SEPARATOR_CHAR:
                escaped.append(ESCAPE_CHAR);
            default:
                escaped.append(ch);
            }
        }
        return escaped.toString();
    }

    private static QName parseQName(final String value) {
        final javax.xml.namespace.QName qname = javax.xml.namespace.QName.valueOf(value);
        return new QName(qname.getNamespaceURI(), qname.getLocalPart());
    }

    private static boolean isSet(final String name) {
        return ((name != null) && (name.length() > 0));
    }

    private static String asString(final ModelNode modelNode) {
        return (modelNode.isDefined() ? modelNode.asString() : null);
    }

}
