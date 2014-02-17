package org.overlord.rtgov.ui.server.cdi;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.bus.server.annotations.security.RequireAuthentication;
import org.jboss.errai.bus.server.api.ServerMessageBus;
import org.jboss.errai.bus.server.security.auth.rules.RolesRequiredRule;

@Startup
@ApplicationScoped
public class SecurityConfiguration {
	@Inject
	private ServerMessageBus serverMessageBus;

	@Inject
	void initServices(final BeanManager beanManager) {
		serverMessageBus.send(MessageBuilder.createMessage(SecurityConfiguration.class.getSimpleName()).getMessage());
		serverMessageBus.subscribe(SecurityConfiguration.class.getSimpleName(), new MessageCallback() {

			@Override
			public void callback(Message message) {
				@SuppressWarnings("serial")
				Set<Bean<?>> beans = beanManager.getBeans(Object.class, new AnnotationLiteral<Any>() {
				});
				for (final Bean<?> bean : beans) {
					final Class<?> beanClass = bean.getBeanClass();
					if (beanClass.isAnnotationPresent(Service.class)
							&& bean.getBeanClass().isAnnotationPresent(RequireAuthentication.class)) {
						Service serviceAnnotation = beanClass.getAnnotation(Service.class);
						String serviceName = "".equals(serviceAnnotation.value()) ? beanClass.getSimpleName()
								: serviceAnnotation.value();
						serverMessageBus.addRule("org.overlord.rtgov.ui.client.shared.services.ISituationsService:RPC", new RolesRequiredRule(new HashSet<Object>(),
								serverMessageBus));

					}
				}

			}
		});

	}
}
