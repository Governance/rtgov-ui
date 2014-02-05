/*
 * 2012-3 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.overlord.rtgov.ui.tests.services.switchyard;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.provider.ServicesProvider;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class SwitchYardServicesProviderTest {
	
	@Inject
	private ServicesProvider _provider;
	
	private static final QName ORDER_APP=new QName("urn:switchyard-quickstart-demo:multiapp:0.1.0", "orders");
	private static final QName CONSUMER_SERVICE_APP=new QName("urn:switchyard-quickstart-demo:multiapp:0.1.0", "consumer-service");
    
	@Deployment(name="rtgov-ui-test", order=0)
    public static WebArchive createDeployment1() {
        String rtgovuiversion=System.getProperty("rtgov-ui.version");
        
        return ShrinkWrap.create(WebArchive.class, "rtgov-ui-test.war")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsManifestResource("META-INF/jboss-deployment-structure.xml", "jboss-deployment-structure.xml")
            .addAsLibraries(Maven.resolver().resolve("org.overlord.rtgov.ui:rtgov-ui-services-switchyard:"+rtgovuiversion).withTransitivity().asFile())
    		.addAsLibraries(Maven.resolver().resolve("org.overlord.rtgov.ui:rtgov-ui-core:"+rtgovuiversion).withTransitivity().asFile());
    }
    
    @Deployment(name="switchyard-quickstart-demo-multi-artifacts", order=2, testable=false)
    public static JavaArchive createDeployment2() {
        String version=System.getProperty("switchyard.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.switchyard.quickstarts.demos:switchyard-quickstart-demo-multi-artifacts:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(JavaArchive.class,
        					TestUtils.copyToTmpFile(archiveFile, "OrderService.jar"));
    }
   
    @Deployment(name="switchyard-quickstart-demo-multi-order-consumer", order=3, testable=false)
    public static JavaArchive createDeployment3() {
        String version=System.getProperty("switchyard.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.switchyard.quickstarts.demos:switchyard-quickstart-demo-multi-order-consumer:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(JavaArchive.class, archiveFile);
    }
   
    @Deployment(name="switchyard-quickstart-demo-multi-order-service", order=4, testable=false)
    public static JavaArchive createDeployment4() {
        String version=System.getProperty("switchyard.version");

        java.io.File archiveFile=Maven.resolver().resolve("org.switchyard.quickstarts.demos:switchyard-quickstart-demo-multi-order-service:"+version)
                .withoutTransitivity().asSingleFile();
        
        return ShrinkWrap.createFromZipFile(JavaArchive.class, archiveFile);
    }
   
    @Test @OperateOnDeployment(value="rtgov-ui-test")
    public void testGetApplicationNames() {
    	if (_provider == null) {
    		fail("Provider not set");
    	}
    	
        try {
			java.util.List<QName> appNames=_provider.getApplicationNames();
			
			if (appNames.size() != 2) {
				fail("Should be 2 deployed applications: "+appNames.size());
			}
			
			if (!appNames.contains(CONSUMER_SERVICE_APP)) {
				fail("Failed to find app: "+CONSUMER_SERVICE_APP);
			}
			
			if (!appNames.contains(ORDER_APP)) {
				fail("Failed to find app: "+ORDER_APP);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to get app names: "+e.getMessage());
		}
        
    }
}