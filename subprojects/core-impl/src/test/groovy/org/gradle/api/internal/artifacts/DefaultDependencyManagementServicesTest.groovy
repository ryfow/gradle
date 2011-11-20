/*
 * Copyright 2011 the original author or authors.
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
package org.gradle.api.internal.artifacts

import org.gradle.StartParameter
import org.gradle.api.internal.ClassPathRegistry
import org.gradle.api.internal.DomainObjectContext
import org.gradle.api.internal.Factory
import org.gradle.api.internal.Instantiator
import org.gradle.api.internal.artifacts.configurations.ConfigurationContainerInternal
import org.gradle.api.internal.artifacts.configurations.DefaultConfigurationContainer
import org.gradle.api.internal.artifacts.configurations.DependencyMetaDataProvider
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler
import org.gradle.api.internal.artifacts.dsl.dependencies.ProjectFinder
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.project.ServiceRegistry
import org.gradle.cache.CacheRepository
import org.gradle.cache.internal.FileLockManager
import org.gradle.listener.ListenerManager
import org.gradle.logging.LoggingManagerInternal
import org.gradle.logging.ProgressLoggerFactory
import org.gradle.util.TimeProvider
import spock.lang.Specification

class DefaultDependencyManagementServicesTest extends Specification {
    final ServiceRegistry parent = Mock()
    final FileResolver fileResolver = Mock()
    final DependencyMetaDataProvider dependencyMetaDataProvider = Mock()
    final ProjectFinder projectFinder = Mock()
    final Instantiator instantiator = Mock()
    final DomainObjectContext domainObjectContext = Mock()
    final DefaultRepositoryHandler repositoryHandler = Mock()
    final ConfigurationContainerInternal configurationContainer = Mock()
    final StartParameter startParameter = Mock()
    final ListenerManager listenerManager = Mock()
    final DefaultDependencyManagementServices services = new DefaultDependencyManagementServices(parent)

    def setup() {
        Factory<LoggingManagerInternal> loggingFactory = Mock()
        _ * parent.getFactory(LoggingManagerInternal) >> loggingFactory
        ProgressLoggerFactory progressLoggerFactory = Mock()
        _ * parent.get(ProgressLoggerFactory) >> progressLoggerFactory
        CacheRepository cacheRepository = Mock()
        _ * parent.get(CacheRepository) >> cacheRepository
        ClassPathRegistry classPathRegistry = Mock()
        _ * parent.get(ClassPathRegistry) >> classPathRegistry
        _ * parent.get(ListenerManager) >> listenerManager
        _ * parent.get(FileLockManager) >> Mock(FileLockManager)
        _ * parent.get(TimeProvider) >> Mock(TimeProvider)
    }

    def "can create dependency resolution services"() {
        given:
        _ * parent.get(Instantiator.class) >> instantiator
        _ * parent.get(StartParameter.class) >> startParameter
        1 * instantiator.newInstance(DefaultRepositoryHandler.class, _, _) >> repositoryHandler
        1 * instantiator.newInstance(DefaultConfigurationContainer.class, !null, instantiator,
                domainObjectContext, listenerManager, dependencyMetaDataProvider) >> configurationContainer

        when:
        def resolutionServices = services.create(fileResolver, dependencyMetaDataProvider, projectFinder, domainObjectContext)

        then:
        resolutionServices.resolveRepositoryHandler != null
        resolutionServices.configurationContainer != null
        resolutionServices.dependencyHandler != null
        resolutionServices.artifactHandler != null
        resolutionServices.publishServicesFactory != null
    }

    def "publish services provide a repository handler"() {
        DefaultRepositoryHandler publishRepositoryHandler = Mock()

        given:
        _ * parent.get(StartParameter.class) >> startParameter
        _ * parent.get(Instantiator.class) >> instantiator
        _ * instantiator.newInstance(DefaultRepositoryHandler.class, _, _) >> publishRepositoryHandler

        when:
        def resolutionServices = services.create(fileResolver, dependencyMetaDataProvider, projectFinder, domainObjectContext)
        def publishResolverHandler = resolutionServices.publishServicesFactory.create().repositoryHandler

        then:
        publishResolverHandler == publishRepositoryHandler
    }

    def "publish services provide an ArtifactPublisher"() {
        given:
        _ * parent.get(StartParameter.class) >> startParameter
        _ * parent.get(Instantiator.class) >> instantiator

        when:
        def resolutionServices = services.create(fileResolver, dependencyMetaDataProvider, projectFinder, domainObjectContext)
        def ivyService = resolutionServices.publishServicesFactory.create().artifactPublisher

        then:
        ivyService != null
    }
}
