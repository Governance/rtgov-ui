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
package org.overlord.rtgov.ui.server.cdi;

import static java.util.Collections.emptySet;
import static org.jboss.errai.bus.client.api.base.MessageBuilder.createMessage;
import static org.jboss.errai.config.rebind.ProxyUtil.createCallSignature;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.bus.server.annotations.Remote;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.api.ServerMessageBus;
import org.jboss.errai.bus.server.security.auth.rules.RolesRequiredRule;

/**
 * 
 * @author eric.wittmann@redhat.com
 */
public class StartupBeanExtension implements Extension {
	private final Set<Bean<?>> startupBeans = new LinkedHashSet<Bean<?>>();
	private final Map<Class<?>, Set<String>> remoteInterfaceMethods = new HashMap<Class<?>, Set<String>>();

	<T> void observeResources(@Observes final ProcessAnnotatedType<T> event) {
		final AnnotatedType<T> type = event.getAnnotatedType();
		if (!type.isAnnotationPresent(Service.class)) {
			return;
		}
		Class<T> ¢lass = type.getJavaClass();
		Class<?> remoteInterface = null;
		for (Class<?> _interface : ¢lass.getInterfaces()) {
			if (_interface.isAnnotationPresent(Remote.class)) {
				remoteInterface = _interface;
				break;
			}

		}
		if (remoteInterface == null) {
			return;
		}
		HashSet<String> remoteMethods = new HashSet<String>();
		remoteInterfaceMethods.put(remoteInterface, remoteMethods);
		for (Method method : ¢lass.getMethods()) {
			if (!method.isAnnotationPresent(RequiresAuthentication.class)) {
				continue;
			}
			remoteMethods.add(createCallSignature(remoteInterface, method));
		}
	}

	<X> void processBean(@Observes ProcessBean<X> event) {
		if (event.getAnnotated().isAnnotationPresent(Startup.class)
		        && event.getAnnotated().isAnnotationPresent(ApplicationScoped.class)) {
			startupBeans.add(event.getBean());
		}
	}

	void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager manager) {
		for (Bean<?> bean : startupBeans) {
			// the call to toString() is a cheat to force the bean to be
			// initialized
			manager.getReference(bean, bean.getBeanClass(), manager.createCreationalContext(bean)).toString();
		}
		@SuppressWarnings("unchecked")
		Bean<ServerMessageBus> bean = (Bean<ServerMessageBus>) manager
		        .resolve(manager.getBeans(ServerMessageBus.class));
		final ServerMessageBus messageBus = manager.getContext(bean.getScope()).get(bean,
		        manager.createCreationalContext(bean));
		messageBus.send(createMessage(StartupBeanExtension.class.getSimpleName()).getMessage());
		messageBus.subscribe(StartupBeanExtension.class.getSimpleName(), new MessageCallback() {

			@Override
			public void callback(Message message) {
				for (Entry<Class<?>, Set<String>> entry : remoteInterfaceMethods.entrySet()) {
					String remoteInterfaceName = entry.getKey().getName();
					final Set<String> signatures = entry.getValue();
					messageBus.addRule(remoteInterfaceName + ":RPC", new RolesRequiredRule(emptySet(), messageBus) {
						public boolean decision(Message message) {
							if (signatures.contains(message.getCommandType())) {
								return super.decision(message);
							}
							return true;
						};
					});
				}
			}
		});
	}
}
