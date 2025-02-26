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

import org.apache.http.protocol.HTTP;
import org.apache.sling.distribution.DistributionRequestType;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;

import static org.apache.sling.distribution.it.DistributionUtils.assertExists;
import static org.apache.sling.distribution.it.DistributionUtils.assertNotExists;
import static org.apache.sling.distribution.it.DistributionUtils.createRandomNode;
import static org.apache.sling.distribution.it.DistributionUtils.doExport;
import static org.apache.sling.distribution.it.DistributionUtils.doImport;

public class DistributionPackageExporterImporterTestIT extends DistributionIntegrationTestBase {

    @Test
    public void testAddExportImport() throws Exception {
        String nodePath = createRandomNode(publishClient, "/content/export_" + System.nanoTime());
        assertExists(publishClient, nodePath);

        String content = doExport(publishClient, "default", DistributionRequestType.ADD, nodePath);

        publishClient.deletePath(nodePath);
        assertNotExists(publishClient, nodePath);

        doImport(publishClient, "default", content.getBytes(HTTP.DEFAULT_CONTENT_CHARSET));
        assertExists(publishClient, nodePath);

    }

    @Test
    public void testDeleteExportImport() throws Exception {
        String nodePath = createRandomNode(publishClient, "/content/export_" + System.nanoTime());
        assertExists(publishClient, nodePath);

        String content = doExport(publishClient, "default", DistributionRequestType.DELETE, nodePath);

        doImport(publishClient, "default", content.getBytes(HTTP.DEFAULT_CONTENT_CHARSET));
        assertNotExists(publishClient, nodePath);
    }

    /*@AfterClass
    public static void killInstances(){
        killContainers();
    }*/
}
