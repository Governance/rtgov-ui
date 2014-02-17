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
package org.overlord.rtgov.ui.client.local;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.errai.bus.client.ErraiBus;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.bus.client.protocols.SecurityCommands;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.ui.nav.client.local.Navigation;
import org.jboss.errai.ui.shared.api.annotations.Bundle;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The main entry point into the Overlord:DTR ui app.
 * 
 * @author eric.wittmann@redhat.com
 */
@EntryPoint
@Bundle("messages.json")
public class App {

	@Inject
	private RootPanel rootPanel;
	@Inject
	private Navigation navigation;

	@PostConstruct
	public void buildUI() {
		IsWidget contentPanel = navigation.getContentPanel();
		rootPanel.add(contentPanel);
		ErraiBus.get().subscribe("LoginClient", new MessageCallback() {
			@Override
			public void callback(final Message message) {
				if (SecurityCommands.SecurityChallenge.name().equals(message.getCommandType())) {
					History.newItem("login");
				}
			}
		});
	}

}
