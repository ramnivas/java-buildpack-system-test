/*
 * Copyright 2013 the original author or authors.
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

package com.gopivotal.cloudfoundry.test.support.service;

import com.gopivotal.cloudfoundry.test.support.util.RandomizedNameFactory;
import org.cloudfoundry.client.lib.CloudFoundryOperations;
import org.cloudfoundry.client.lib.domain.CloudService;
import org.cloudfoundry.client.lib.domain.CloudServiceOffering;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class ClearDbServiceTest {

    private final CloudService cloudService;

    private final ClearDbService service;

    public ClearDbServiceTest() {
        CloudFoundryOperations cloudFoundryOperations = createCloudFoundryOperations();
        RandomizedNameFactory randomizedNameFactory = createRandomizedNameFactory();
        this.service = createService(cloudFoundryOperations, randomizedNameFactory);
        this.cloudService = createCloudService(cloudFoundryOperations);
    }

    private static CloudFoundryOperations createCloudFoundryOperations() {
        CloudFoundryOperations cloudFoundryOperations = mock(CloudFoundryOperations.class);

        CloudServiceOffering cloudServiceOffering = new CloudServiceOffering(null, "cleardb");
        when(cloudFoundryOperations.getServiceOfferings()).thenReturn(Arrays.asList(cloudServiceOffering));

        return cloudFoundryOperations;
    }

    private static RandomizedNameFactory createRandomizedNameFactory() {
        RandomizedNameFactory randomizedNameFactory = mock(RandomizedNameFactory.class);
        when(randomizedNameFactory.create("cleardb")).thenReturn("randomized-name");

        return randomizedNameFactory;
    }

    private static ClearDbService createService(CloudFoundryOperations cloudFoundryOperations,
                                                RandomizedNameFactory randomizedNameFactory) {
        return new ClearDbService(cloudFoundryOperations, randomizedNameFactory);
    }

    private static CloudService createCloudService(CloudFoundryOperations cloudFoundryOperations) {
        ArgumentCaptor<CloudService> cloudService = ArgumentCaptor.forClass(CloudService.class);
        verify(cloudFoundryOperations).createService(cloudService.capture());

        return cloudService.getValue();
    }

    @Test
    public void test() {
        assertEquals("cleardb", this.cloudService.getLabel());
        assertEquals("spark", this.cloudService.getPlan());

        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("VCAP_SERVICES", "{\"cleardb-n/a\":[{\"name\":\"randomized-name\"," +
                "\"label\":\"cleardb-n/a\",\"tags\":[\"mysql\",\"relational\"],\"plan\":\"spark\"," +
                "\"credentials\":{\"jdbcUrl\":\"http://test.jdbc.url\",\"uri\":\"test-uri\",\"name\":\"test-name\"," +
                "\"hostname\":\"test-host-name\",\"port\":\"3306\",\"username\":\"test-username\"," +
                "\"password\":\"test-password\"}}]}");

        assertEquals(URI.create("http://test.jdbc.url"), this.service.getEndpoint(environmentVariables));
    }

    @Test
    public void testUserInfoRemoval() {
        assertEquals("cleardb", this.cloudService.getLabel());
        assertEquals("spark", this.cloudService.getPlan());

        Map<String, String> environmentVariables = new HashMap<>();
        environmentVariables.put("VCAP_SERVICES", "{\"cleardb-n/a\":[{\"name\":\"randomized-name\"," +
                "\"label\":\"cleardb-n/a\",\"tags\":[\"mysql\",\"relational\"],\"plan\":\"spark\"," +
                "\"credentials\":{\"jdbcUrl\":\"http://test-user:test-password@test.jdbc.url\",\"uri\":\"test-uri\"," +
                "\"name\":\"test-name\"," +
                "\"hostname\":\"test-host-name\",\"port\":\"3306\",\"username\":\"test-username\"," +
                "\"password\":\"test-password\"}}]}");

        assertEquals(URI.create("http://test.jdbc.url"), this.service.getEndpoint(environmentVariables));
    }
}
