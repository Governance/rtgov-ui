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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.jboss.dmr.ModelNode;
import org.overlord.monitoring.ui.client.shared.beans.ComponentServiceBean;
import org.overlord.monitoring.ui.client.shared.beans.ComponentServiceResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.ComponentServiceSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.GatewayMetric;
import org.overlord.monitoring.ui.client.shared.beans.QName;
import org.overlord.monitoring.ui.client.shared.beans.ServiceBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceResultSetBean;
import org.overlord.monitoring.ui.client.shared.beans.ServiceSummaryBean;
import org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean;
import org.overlord.monitoring.ui.client.shared.exceptions.UiException;
import org.overlord.monitoring.ui.server.services.IServicesServiceImpl;
import org.overlord.monitoring.ui.server.services.activator.MonitoringServiceActivator;

/**
 * SwitchYard implementation of the services service. :)
 *
 * @author kconner@redhat.com
 */
@ApplicationScoped
@Alternative
public class SwitchYardServicesServiceImpl implements IServicesServiceImpl {
    private static final String SUBSYSTEM = "subsystem";
    private static final String SWITCHYARD = "switchyard";

    private static final String OPERATION = "operation";
    private static final String ADDRESS = "address";
    private static final String APPLICATION_NAME = "application-name";
    private static final String SERVICE_NAME = "service-name";

    private static final String NAME = "name";
    private static final String APPLICATION = "application";
    private static final String INTERFACE = "interface";
    private static final String GATEWAYS = "gateways";
    private static final String PROMOTED_SERVICE = "promotedService";
    private static final String TYPE = "type";

    private static final String SUCCESS_COUNT = "successCount";
    private static final String FAULT_COUNT = "faultCount";
    private static final String AVERAGE_TIME = "averageTime";
    private static final String MIN_TIME = "minTime";
    private static final String MAX_TIME = "maxTime";
    private static final String TOTAL_TIME = "totalTime";
    private static final String TOTAL_COUNT = "totalCount";

    private static final String READ_SERVICE = "read-service";
    private static final String SHOW_METRICS = "show-metrics";

    private static final String RESULT = "result";

    private static final char ESCAPE_CHAR = '\\';
    private static final char SEPARATOR_CHAR = ':';

    /**
     * @see org.overlord.monitoring.ui.server.services.IServicesServiceImpl#findServices(org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean, int)
     */
    @Override
    public ServiceResultSetBean findServices(final ServicesFilterBean filters, final int page,
            final String sortColumn, final boolean ascending) throws UiException {
        final ServiceResultSetBean serviceResult = new ServiceResultSetBean();
        final ArrayList<ServiceSummaryBean> services = new ArrayList<ServiceSummaryBean>();

        final String applicationName = filters.getApplicationName();
//        final String processingState = filters.getProcessingState();
        final String serviceName = filters.getServiceName();

        final ModelNode operation = getBlankOperation(READ_SERVICE);
        if (applicationName != null) {
            operation.get(APPLICATION_NAME).set(applicationName);
        }
        if (serviceName != null) {
            operation.get(SERVICE_NAME).set(serviceName);
        }

        final Map<String, Map<String, BigDecimal>> averageDurations = getAverageDuration(applicationName, serviceName);

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
        serviceResult.setServices(services);
        serviceResult.setItemsPerPage(services.size());
        serviceResult.setStartIndex(0);
        serviceResult.setTotalResults(services.size());
        return serviceResult;
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.IServicesServiceImpl#findComponentServices(org.overlord.monitoring.ui.client.shared.beans.ServicesFilterBean, int)
     */
    @Override
    public ComponentServiceResultSetBean findComponentServices(
            ServicesFilterBean filters, int page, String sortColumn,
            boolean ascending) throws UiException {
      ComponentServiceResultSetBean rval = new ComponentServiceResultSetBean();
      List<ComponentServiceSummaryBean> services = new ArrayList<ComponentServiceSummaryBean>();
      rval.setServices(services);
      rval.setItemsPerPage(20);
      rval.setStartIndex(0);
      rval.setTotalResults(2);

      ComponentServiceSummaryBean service = new ComponentServiceSummaryBean();
      service.setServiceId("1"); //$NON-NLS-1$
      service.setName("CreateApplicationService"); //$NON-NLS-1$
      service.setApplication("Contract"); //$NON-NLS-1$
      service.setIface("org.jboss.demos.services.ICreateApplication"); //$NON-NLS-1$
      service.setImplementation("org.jboss.demos.services.impl.CreateApplicationService"); //$NON-NLS-1$
      service.setAverageDuration(2837l);
      services.add(service);

      service = new ComponentServiceSummaryBean();
      service.setServiceId("2"); //$NON-NLS-1$
      service.setName("CreateQuoteService"); //$NON-NLS-1$
      service.setApplication("Contract"); //$NON-NLS-1$
      service.setIface("org.jboss.demos.services.ICreateQuote"); //$NON-NLS-1$
      service.setImplementation("org.jboss.demos.services.impl.CreateQuoteService"); //$NON-NLS-1$
      service.setAverageDuration(2837l);
      services.add(service);

      return rval;
    }

    /**
     * @see org.overlord.dtgov.ui.client.shared.services.IServicesServiceImpl#getService(java.lang.String)
     */
    @Override
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
                    serviceResult.setServiceInterface(serviceNode.get(INTERFACE).asString());

                    final ModelNode metrics = getServiceMetrics(applicationName, serviceName);
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
                                gatewayMetric.setName(gateway.get(NAME).asString());
                                gatewayMetric.setType(gateway.get(TYPE).asString());
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
                    return serviceResult;
                }
            }
        }
        return null;
    }

    /**
     * @see org.overlord.monitoring.ui.server.services.IServicesServiceImpl#getComponentService(java.lang.String)
     */
    @Override
    public ComponentServiceBean getComponentService(String serviceId) throws UiException {
        ComponentServiceBean service = new ComponentServiceBean();
        service.setServiceId("1"); //$NON-NLS-1$
        service.setName(new QName("urn:jboss:demo:services", "CreateApplicationService")); //$NON-NLS-1$ //$NON-NLS-2$
        service.setApplication(new QName("urn:jboss:demos:applications", "Contract")); //$NON-NLS-1$ //$NON-NLS-2$
        service.setServiceInterface("org.jboss.demos.services.ICreateApplication"); //$NON-NLS-1$
        service.setServiceImplementation("org.jboss.demos.services.CreateApplicationService"); //$NON-NLS-1$
        service.setSuccessCount(83);
        service.setFaultCount(4);
        service.setTotalTime(804);
        service.setAverageTime(41);
        service.setMinTime(3);
        service.setMaxTime(101);
        service.addReferenceMetric("Inventory Service", 17, 7, 75, 100); //$NON-NLS-1$
        service.addReferenceMetric("Provisioning Service", 13, 3, 25, 0); //$NON-NLS-1$
        return service;
    }

    private static ModelNode getBlankOperation(final String operation) {
        final ModelNode modelNode = new ModelNode();
        final ModelNode address = new ModelNode();
        address.add(SUBSYSTEM, SWITCHYARD);
        modelNode.get(ADDRESS).set(address);
        modelNode.get(OPERATION).set(operation);
        return modelNode;
    }

    private static Map<String, Map<String, BigDecimal>> getAverageDuration(final String applicationName, final String serviceName) throws UiException {
        final Map<String, Map<String, BigDecimal>> averageDurations = new TreeMap<String, Map<String, BigDecimal>>();

        final ModelNode operation = getBlankOperation(SHOW_METRICS);
        if (serviceName != null) {
            operation.get(SERVICE_NAME).set(serviceName);
        } else {
            operation.get(SERVICE_NAME).set("*");
        }
        operation.get(TYPE).set("service");
        final ModelNode response = execute(operation);
        final ModelNode result = response.get().get(RESULT);
        if (result.isDefined()) {
            for (final ModelNode metricsNode: result.asList()) {
                final String nodeApplicationName = metricsNode.get(APPLICATION).asString();
                if ((applicationName == null) || applicationName.equals(nodeApplicationName)) {
                    Map<String, BigDecimal> applicationMap = averageDurations.get(nodeApplicationName);
                    if (applicationMap == null) {
                        applicationMap = new TreeMap<String, BigDecimal>();
                        averageDurations.put(nodeApplicationName, applicationMap);
                    }
                    final String nodeServiceName = metricsNode.get(NAME).asString();

                    applicationMap.put(nodeServiceName, metricsNode.get(AVERAGE_TIME).asBigDecimal());
                }
            }
        }
        return averageDurations;
    }

    private static ModelNode getServiceMetrics(final String applicationName, final String serviceName) throws UiException {
        final ModelNode operation = getBlankOperation(SHOW_METRICS);
        operation.get(SERVICE_NAME).set(serviceName);
        operation.get(TYPE).set("service");
        final ModelNode response = execute(operation);
        final ModelNode result = response.get().get(RESULT);
        if (result.isDefined()) {
            for (final ModelNode metricsNode: result.asList()) {
                final String nodeApplicationName = metricsNode.get(APPLICATION).asString();
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

        final String name = serviceNode.get(NAME).asString();
        final String application = serviceNode.get(APPLICATION).asString();
        final String intface = serviceNode.get(INTERFACE).asString();
        final String promotedService = serviceNode.get(PROMOTED_SERVICE).asString();

        result.setServiceId(generateId(application, name));
        result.setApplication(application);
        result.setName(name);
        result.setIface(intface);

        final List<ModelNode> gateways = serviceNode.get(GATEWAYS).asList();
        if (!gateways.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for(ModelNode gateway: gateways) {
                final String type = gateway.get(TYPE).asString();
                if (type != null) {
                    sb.append(type).append(", ");
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

    private static ModelNode execute(final ModelNode operation) throws UiException {
        try {
            return MonitoringServiceActivator.getClient().execute(operation);
        } catch (final IOException ioe) {
            throw new UiException("Failed to execute DMR operation", ioe);
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
}
