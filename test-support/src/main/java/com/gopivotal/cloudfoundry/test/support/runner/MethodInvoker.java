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

package com.gopivotal.cloudfoundry.test.support.runner;

import com.gopivotal.cloudfoundry.test.support.application.Application;
import com.gopivotal.cloudfoundry.test.support.application.ApplicationFactory;
import com.gopivotal.cloudfoundry.test.support.service.ServicesHolder;
import com.gopivotal.cloudfoundry.test.support.util.TcfUtils;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

final class MethodInvoker extends Statement implements TestExecutionListener {

    private final FrameworkMethod frameworkMethod;

    private final Object instance;

    private final Object monitor = new Object();

    private final String name;

    private volatile TestContext testContext;

    MethodInvoker(FrameworkMethod frameworkMethod, Object instance, String name) {
        this.frameworkMethod = frameworkMethod;
        this.instance = instance;
        this.name = name;
    }

    @Override
    public void evaluate() throws Throwable {
        Application application = null;

        try {
            application = createApplication();
            application.getTestOperations().waitForStart();

            this.frameworkMethod.invokeExplosively(this.instance, application);
        } finally {
            TcfUtils.deleteQuietly(application);
        }
    }

    private Application createApplication() {
        synchronized (this.monitor) {
            ApplicationContext applicationContext = this.testContext.getApplicationContext();
            ServicesHolder servicesHolder = applicationContext.getBean(ServicesHolder.class);
            ApplicationFactory applicationFactory = applicationContext.getBean(ApplicationFactory.class);

            Application application = applicationFactory.create(this.name);
            return application.push().bind(servicesHolder.get()).start();
        }
    }

    @Override
    public void beforeTestClass(TestContext testContext) {
    }

    @Override
    public void prepareTestInstance(TestContext testContext) {
    }

    @Override
    public void beforeTestMethod(TestContext testContext) {
        synchronized (this.monitor) {
            this.testContext = testContext;
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
    }

    @Override
    public void afterTestClass(TestContext testContext) {
    }

}
