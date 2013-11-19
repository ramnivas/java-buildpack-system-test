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

package com.gopivotal.cloudfoundry.test.support.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
final class YamlManifestFactory implements ManifestFactory {

    private final String defaultBuildpack;

    private final MemorySizeParser memorySizeParser;

    private final String overrideBuildpack;

    @Autowired
    YamlManifestFactory(@Value("${default.buildpack:https://github.com/cloudfoundry/java-buildpack.git}") String
                                defaultBuildpack, MemorySizeParser memorySizeParser,
                        @Value("${override.buildpack:#{null}}") String
            overrideBuildpack) {
        this.defaultBuildpack = defaultBuildpack;
        this.memorySizeParser = memorySizeParser;
        this.overrideBuildpack = overrideBuildpack;
    }

    @Override
    public Manifest create(File applicationPath) {
        return new YamlManifest(applicationPath, this.defaultBuildpack, this.memorySizeParser, this.overrideBuildpack);
    }
}
