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
package org.overlord.monitoring.ui.server.services.activator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.jboss.as.controller.ModelController;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.as.server.Services;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceActivator;
import org.jboss.msc.service.ServiceActivatorContext;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceRegistryException;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

/**
 * ServiceActivator to gain access to the ModelController, based on a post by John Mazz.
 *
 * @author kconner@redhat.com
 */
public class MonitoringServiceActivator  implements ServiceActivator {
   private static volatile ModelController controller;
   private static volatile ExecutorService executor;

   private static final Logger LOG = Logger.getLogger(MonitoringServiceActivator.class) ;


   public static ModelControllerClient getClient() {
      return controller.createClient(executor);
   }


   @Override
   public void activate(ServiceActivatorContext context) throws ServiceRegistryException {
      final GetModelControllerService service = new GetModelControllerService();
      LOG.info("Activating monitoring service") ;
      context
          .getServiceTarget()
          .addService(ServiceName.of("management", "client", "getter"), service)
          .addDependency(Services.JBOSS_SERVER_CONTROLLER, ModelController.class, service.modelControllerValue)
          .install();
   }


   private class GetModelControllerService implements Service<Void> {
      private InjectedValue<ModelController> modelControllerValue = new InjectedValue<ModelController>();

      @Override
      public Void getValue() throws IllegalStateException, IllegalArgumentException {
         return null;
      }


      @Override
      public void start(StartContext context) throws StartException {
         LOG.info("Starting GetModelControllerService") ;
         MonitoringServiceActivator.executor = Executors.newFixedThreadPool(5, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
               Thread t = new Thread(r);
               t.setDaemon(true);
               t.setName("MonitoringServiceActivatorModelControllerClientThread");
               return t;
            }
         });
         MonitoringServiceActivator.controller = modelControllerValue.getValue();
         LOG.info("Started GetModelControllerService") ;
      }


      @Override
      public void stop(StopContext context) {
         LOG.info("Stopping GetModelControllerService") ;
         try {
            MonitoringServiceActivator.executor.shutdownNow();
         } finally {
            MonitoringServiceActivator.executor = null;
            MonitoringServiceActivator.controller = null;
         }
         LOG.info("Stopped GetModelControllerService") ;
      }
   }
}
