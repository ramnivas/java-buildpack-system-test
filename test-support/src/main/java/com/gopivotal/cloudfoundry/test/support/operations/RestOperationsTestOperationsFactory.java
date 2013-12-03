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

package com.gopivotal.cloudfoundry.test.support.operations;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Component
final class RestOperationsTestOperationsFactory implements TestOperationsFactory {

    private static final Long SECOND = 1000L;

    private static final Long MINUTE = 60 * SECOND;

    private static final Long INTERVAL = 5 * SECOND;

    private static final Long TIMEOUT = 10 * MINUTE;

    private final RestOperations restOperations;

    RestOperationsTestOperationsFactory() {
        this.restOperations = createRestOperations();
    }

    private static RestOperations createRestOperations() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new NoErrorResponseErrorHandler());

        return restTemplate;
    }

    @Override
    public TestOperations create(String host) {
        return new RestOperationsTestOperations(INTERVAL, TIMEOUT, host, this.restOperations);
    }

    private static final class NoErrorResponseErrorHandler extends DefaultResponseErrorHandler {

        @Override
        protected boolean hasError(HttpStatus statusCode) {
            return false;
        }
    }

}
