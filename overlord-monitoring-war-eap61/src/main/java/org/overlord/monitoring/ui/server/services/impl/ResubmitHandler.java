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

import org.overlord.rtgov.analytics.situation.Situation;

/**
 * This interface represents a component responsible for resubmitting
 * a supplied modified message to a target service.
 *
 */
public interface ResubmitHandler {

	/**
	 * This method resubmits the supplied message to the target service
	 * and operation identified in the supplied situation.
	 * 
	 * @param situation The situation
	 * @param message The message
	 * @throws Exception Failed to resubmit the message
	 */
	public void resubmit(Situation situation, String message) throws Exception;

}
