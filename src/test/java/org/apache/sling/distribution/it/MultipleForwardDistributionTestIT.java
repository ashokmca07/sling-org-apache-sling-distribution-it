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

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.sling.distribution.DistributionRequestType;
import org.apache.sling.testing.clients.ClientException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import java.io.IOException;

import static org.apache.sling.distribution.it.DistributionUtils.assertExists;
import static org.apache.sling.distribution.it.DistributionUtils.assertNotExists;
import static org.apache.sling.distribution.it.DistributionUtils.assertPostResourceWithParameters;
import static org.apache.sling.distribution.it.DistributionUtils.createRandomNode;
import static org.apache.sling.distribution.it.DistributionUtils.distribute;
import static org.apache.sling.distribution.it.DistributionUtils.doExport;
import static org.apache.sling.distribution.it.DistributionUtils.getResource;
import static org.apache.sling.distribution.it.DistributionUtils.queueUrl;
import static org.junit.Assert.assertEquals;

/**
 * Integration test for forward distribution
 */
public class MultipleForwardDistributionTestIT extends DistributionIntegrationTestBase {

    final static String DELETE_LIMIT = "100";

    @Test
    public void testAddContent() throws Exception {
        String nodePath = createRandomNode(authorClient, "/content/forward_add_" + System.nanoTime());
        assertExists(authorClient, nodePath);
        distribute(authorClient, "publish-multiple", DistributionRequestType.ADD, nodePath);
        assertExists(publishClient, nodePath);
    }

    @Test
    public void testDeleteContent() throws Exception {
        String nodePath = createRandomNode(publishClient, "/content/forward_del_" + System.nanoTime());
        assertExists(publishClient, nodePath);
        distribute(authorClient, "publish-multiple", DistributionRequestType.DELETE, nodePath);
        assertNotExists(publishClient, nodePath);
    }

    @Ignore
    @Test
    public void testAddContentCheckPassiveQueue() throws Exception {
        String nodePath = createRandomNode(authorClient, "/content/forward_add_" + System.nanoTime());
        assertExists(authorClient, nodePath);
        distribute(authorClient, "publish-multiple", DistributionRequestType.ADD, nodePath);
        assertExists(publishClient, nodePath);

        {
            JsonNode jsonNode = getResource(authorClient, queueUrl("publish-multiple") + "/passivequeue1");

            JsonNode queueItems = jsonNode.get("items");
            assertEquals(1, queueItems.size());
            assertEquals(1, jsonNode.get("itemsCount"));
        }

        String content = doExport(authorClient, "publish-multiple-passivequeue1", DistributionRequestType.PULL, null);

        {
            JsonNode jsonNode = getResource(authorClient, queueUrl("publish-multiple") + "/passivequeue1");

            JsonNode queueItems = jsonNode.get("items");
            assertEquals(0, queueItems.size());
            assertEquals(0, jsonNode.get("itemsCount"));
        }
    }

    @After
    public void clean() throws IOException, ClientException {
        assertPostResourceWithParameters(authorClient, 200, queueUrl("publish-multiple") + "/endpoint1",
                "operation", "delete", "limit", DELETE_LIMIT);

        assertPostResourceWithParameters(authorClient, 200, queueUrl("publish-multiple") + "/endpoint2",
                "operation", "delete", "limit", DELETE_LIMIT);
    }

    /*@AfterClass
    public static void killInstances(){
        killContainers();
    }*/
}
