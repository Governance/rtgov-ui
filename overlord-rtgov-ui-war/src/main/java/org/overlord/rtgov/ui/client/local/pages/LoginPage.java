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

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.base.MessageBuilder;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.bus.client.protocols.SecurityCommands;
import org.jboss.errai.bus.client.protocols.SecurityParts;
import org.jboss.errai.bus.server.service.ErraiService;
import org.jboss.errai.common.client.protocols.MessageParts;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

@Templated("/org/overlord/rtgov/ui/client/local/site/login.html#page")
@Page(path = "login")
@Dependent
public class LoginPage extends AbstractPage {

	@Inject
	@DataField
	private TextBox username;

	@Inject
	@DataField
	private PasswordTextBox password;

	@Inject
	@DataField
	private Anchor login;

	@EventHandler("login")
	private void loginClicked(ClickEvent event) {
		ErraiBus.get().send(MessageBuilder.createMessage(ErraiService.AUTHORIZATION_SVC_SUBJECT)
		        .command(SecurityCommands.AuthRequest)
		        .with(MessageParts.ReplyTo, "LoginClient")
		        .with(SecurityParts.Name, username.getValue())
		        .with(SecurityParts.Password, password.getValue())
		        .done().getMessage());
		event.preventDefault();
	}
    
	@PostConstruct
	public void buildUI() {
        ErraiBus.get().subscribe("LoginClient", new MessageCallback() {
          @Override
          public void callback(final Message message) {
            if (message.getCommandType().equals("FailedAuth")) {
            	username.setStyleName(".alert-error");
            	password.setStyleName("alert-error",true);
            }
            else if (message.getCommandType().equals("SuccessfulAuth")) {
                History.back();
            }
          }
        });
	}

}
