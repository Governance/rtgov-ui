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
package org.overlord.rtgov.analytics.situation.store;

import org.overlord.rtgov.analytics.situation.Situation;

/**
 * This interface provides access to the Situation store.
 *
 */
public interface SituationStore {

    /**
     * This method returns the situation associated with the supplied id.
     *
     * @param id The id
     * @return The situation, or null if not found
     * @throws Exception Failed to get situation
     */
    public Situation getSituation(String id) throws Exception;

    /**
     * This method returns the list of situations that meet the criteria
     * specified in the query.
     * 
     * @param query The situations query
     * @return The list of situations
     * @throws Exception Failed to get situations
     */
    public java.util.List<Situation> getSituations(SituationsQuery query) throws Exception;

}
