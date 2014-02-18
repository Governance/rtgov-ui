/*
 * Copyright 2012 JBoss Inc
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
package org.overlord.rtgov.ui.client.local.pages;

import static org.jboss.errai.bus.client.api.base.MessageBuilder.createMessage;
import static org.jboss.errai.bus.client.protocols.SecurityCommands.AuthRequest;
import static org.jboss.errai.bus.server.service.ErraiService.AUTHORIZATION_SVC_SUBJECT;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.bus.client.protocols.SecurityParts;
import org.jboss.errai.common.client.protocols.MessageParts;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.overlord.rtgov.ui.client.local.ClientMessages;
import org.overlord.rtgov.ui.client.local.services.NotificationService;
import org.overlord.rtgov.ui.client.shared.beans.NotificationBean;
import org.overlord.rtgov.ui.client.shared.exceptions.UiException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

@Templated("/org/overlord/rtgov/ui/client/local/site/login.html#page")
@Page(path = "login")
@Dependent
public class LoginPage extends AbstractPage {
	private static final String LOGIN_CLIENT_SUBJECT = "LoginClient";
	@Inject
	NotificationService notificationService;
	@Inject
	ClientMessages i18n;
	@Inject
	@DataField
	TextBox username;
	@Inject
	@DataField
	PasswordTextBox password;
	@Inject
	@DataField("btn-login")
	Button loginButton;

	@EventHandler("btn-login")
	private void loginClicked(ClickEvent event) {
		final NotificationBean notificationBean = notificationService.startProgressNotification(
				i18n.format("login.title"), //$NON-NLS-1$
				i18n.format("login.login-msg")); //$NON-NLS-1$
		MessageBus bus = ErraiBus.get();
		bus.subscribe(LOGIN_CLIENT_SUBJECT, new MessageCallback() {
			@Override
			public void callback(final Message message) {
				String userName = (String) message.getParts().get(SecurityParts.Name.name());
				if (message.getCommandType().equals("FailedAuth")) {
					notificationService.completeProgressNotification(notificationBean.getUuid(),
							i18n.format("login.login-error"),
							new UiException(i18n.format("login.login-error-msg", userName)));
				} else if (message.getCommandType().equals("SuccessfulAuth")) {
					notificationService.completeProgressNotification(notificationBean.getUuid(),
							i18n.format("login.login-success"), //$NON-NLS-1$
							i18n.format("login.login-success-msg", userName));
					History.back();
				}
			}
		});
		bus.send(createMessage(AUTHORIZATION_SVC_SUBJECT).command(AuthRequest)
				.with(MessageParts.ReplyTo, LOGIN_CLIENT_SUBJECT).with(SecurityParts.Name.name(), username.getValue())
				.with(SecurityParts.Password, password.getValue()).done().getMessage());
	}

	@PostConstruct
	public void buildUI() {

	}

}
