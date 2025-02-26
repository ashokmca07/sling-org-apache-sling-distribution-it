/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.distribution.it;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import static org.apache.sling.distribution.it.DistributionUtils.agentRootUrl;
import static org.apache.sling.distribution.it.DistributionUtils.agentUrl;
import static org.apache.sling.distribution.it.DistributionUtils.assertExists;
import static org.apache.sling.distribution.it.DistributionUtils.assertNotExists;
import static org.apache.sling.distribution.it.DistributionUtils.assertPostResourceWithParameters;
import static org.apache.sling.distribution.it.DistributionUtils.assertResponseContains;
import static org.apache.sling.distribution.it.DistributionUtils.authorAgentConfigUrl;
import static org.apache.sling.distribution.it.DistributionUtils.deleteNode;
import static org.apache.sling.distribution.it.DistributionUtils.logUrl;
import static org.apache.sling.distribution.it.DistributionUtils.publishAgentConfigUrl;
import static org.apache.sling.distribution.it.DistributionUtils.queueUrl;

/**
 * Integration test for {@link org.apache.sling.distribution.agent.spi.DistributionAgent} resources
 */
public class DistributionAgentResourcesIntegrationTestIT extends DistributionIntegrationTestBase {

    private String[] defaultAuthorAgentNames = new String[]{"publish", "publish-reverse"};
    private String[] defaultPublishAgentNames = new String[]{"reverse"};

    @Test
    public void testDefaultAgentConfigurationResourcesOnAuthor() throws Exception {
        for (String agentName : defaultAuthorAgentNames) {
            assertExists(authorClient, authorAgentConfigUrl(agentName));
        }
    }

    @Test
    public void testDefaultAgentConfigurationResourcesOnPublish() throws Exception {
        for (String agentName : defaultPublishAgentNames) {
            assertExists(publishClient, publishAgentConfigUrl(agentName));
        }
    }

    @Test
    public void testDefaultPublishAgentResources() throws Exception {
        // these agents do not exist as they are bundled to publish runMode
        for (String agentName : defaultPublishAgentNames) {
            assertNotExists(authorClient, agentUrl(agentName));
        }
    }

    @Test
    public void testDefaultAuthorAgentResources() throws Exception {
        // these agents exist as they are bundled to author runMode
        for (String agentName : defaultAuthorAgentNames) {
            assertExists(authorClient, agentUrl(agentName));
        }
    }

    @Test
    public void testDefaultPublishAgentQueueResources() throws Exception {
        // these agent queues do not exist as they are bundled to publish runMode
        for (String agentName : defaultPublishAgentNames) {
            assertNotExists(authorClient, queueUrl(agentName));
        }
    }

    @Test
    public void testDefaultAuthorAgentLogResources() throws Exception {
        // these agent queues exist as they are bundled to author runMode
        for (String agentName : defaultAuthorAgentNames) {
            assertExists(authorClient, logUrl(agentName));
        }
    }

    @Test
    public void testDefaultPublishAgentLogResources() throws Exception {
        // these agent queues do not exist as they are bundled to publish runMode
        for (String agentName : defaultPublishAgentNames) {
            assertNotExists(authorClient, logUrl(agentName));
        }
    }

    @Test
    public void testDefaultAuthorAgentQueueResources() throws Exception {
        // these agent queues exist as they are bundled to author runMode
        for (String agentName : defaultAuthorAgentNames) {
            assertExists(authorClient, queueUrl(agentName));
        }
    }

    @Test
    public void testDefaultAgentsRootResource() throws Exception {
        assertExists(authorClient, agentRootUrl());
        assertResponseContains(authorClient, agentRootUrl(),
                "sling:resourceType", "sling/distribution/service/agent/list",
                "items", "publish-reverse", "publish");
    }

    @Test
    public void testAgentConfigurationResourceCreate() throws Exception {
        String agentName = "sample-create-config" + UUID.randomUUID();
        String newConfigResource = authorAgentConfigUrl(agentName);

        assertPostResourceWithParameters(authorClient, 201, newConfigResource, "name", agentName, "type", "forward");
        assertExists(authorClient, newConfigResource);
        assertResponseContains(authorClient, newConfigResource,
                "sling:resourceType", "sling/distribution/setting",
                "name", agentName);
    }

    @Test
    public void testAgentConfigurationResourceDelete() throws Exception {
        String agentName = "sample-delete-config" + UUID.randomUUID();
        String newConfigResource = authorAgentConfigUrl(agentName);

        assertPostResourceWithParameters(authorClient, 201, newConfigResource, "name", agentName, "type", "forward");
        assertExists(authorClient, newConfigResource);

        deleteNode(authorClient, newConfigResource);
        assertNotExists(authorClient, newConfigResource);
    }


    @Test
    public void testAgentConfigurationResourceExtended() throws Exception {
        String agentName = "sample-create-config" + UUID.randomUUID();
        String newConfigResource = authorAgentConfigUrl(agentName);

        assertPostResourceWithParameters(authorClient, 201, newConfigResource, "name", agentName, "type", "forward", "etc.enabled", "true");

        assertExists(authorClient, newConfigResource);
        assertExists(authorClient, "/etc/distribution/" + agentName);
        assertResponseContains(authorClient, newConfigResource,
                "sling:resourceType", "sling/distribution/setting",
                "name", agentName);

        deleteNode(authorClient, newConfigResource);
        assertNotExists(authorClient, newConfigResource);
        assertNotExists(authorClient, "/etc/distribution/" + agentName);
    }

    @Test
    public void testAgentConfigurationResourceUpdate() throws Exception {
        String agentName = "sample-create-config" + UUID.randomUUID();
        String newConfigResource = authorAgentConfigUrl(agentName);

        assertPostResourceWithParameters(authorClient, 201, newConfigResource, "name", agentName, "type", "forward");
        assertExists(authorClient, newConfigResource);
        authorClient.setPropertyString(newConfigResource, "packageExporter", "exporters/remote/updated");
        assertResponseContains(authorClient, newConfigResource,
                "sling:resourceType", "sling/distribution/setting",
                "name", agentName,
                "packageExporter", "exporters/remote/updated");
    }

    /*@AfterClass
    public static void killInstances(){
        killContainers();
    }*/
}