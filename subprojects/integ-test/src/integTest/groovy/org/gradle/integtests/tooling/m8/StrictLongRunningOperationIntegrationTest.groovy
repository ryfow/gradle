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

package org.gradle.integtests.tooling.m8

import org.gradle.integtests.fixtures.AvailableJavaHomes
import org.gradle.integtests.tooling.fixture.MaxTargetGradleVersion
import org.gradle.integtests.tooling.fixture.MinTargetGradleVersion
import org.gradle.integtests.tooling.fixture.MinToolingApiVersion
import org.gradle.integtests.tooling.fixture.ToolingApiSpecification
import org.gradle.tooling.GradleConnectionException
import org.gradle.tooling.model.UnsupportedMethodException
import org.gradle.tooling.model.build.BuildEnvironment
import org.gradle.tooling.model.internal.Exceptions
import spock.lang.IgnoreIf

@MinToolingApiVersion('1.0-milestone-8')
@MinTargetGradleVersion('1.0-milestone-3')
@MaxTargetGradleVersion('1.0-milestone-7')
class StrictLongRunningOperationIntegrationTest extends ToolingApiSpecification {

    def setup() {
        //this test does not make any sense in embedded mode
        //as we don't own the process and we will attempt to configure system wide options.
        toolingApi.isEmbedded = false
    }

    @IgnoreIf({ AvailableJavaHomes.bestAlternative == null })
    def "fails eagerly when java home unsupported for model"() {
        def java = AvailableJavaHomes.bestAlternative
        when:
        Exception e = maybeFailWithConnection {
            def model = it.model(BuildEnvironment.class)
            model.setJavaHome(java)
            model.get()
        }

        then:
        assertExceptionInformative(e, "setJavaHome()")
    }

    @IgnoreIf({ AvailableJavaHomes.bestAlternative == null })
    def "fails eagerly when java home unsupported for build"() {
        def java = AvailableJavaHomes.bestAlternative
        when:
        Exception e = maybeFailWithConnection {
            def build = it.newBuild()
            build.setJavaHome(java)
            build.forTasks('tasks').run()
        }

        then:
        assertExceptionInformative(e, "setJavaHome()")
    }

    def "fails eagerly when java args unsupported"() {
        when:
        Exception e = maybeFailWithConnection {
            def model = it.model(BuildEnvironment.class)
            model.setJvmArguments("-Xmx512m")
            model.get()
        }

        then:
        assertExceptionInformative(e, "setJvmArguments()")
    }

    def "fails eagerly when standard input unsupported"() {
        when:
        Exception e = maybeFailWithConnection {
            def model = it.model(BuildEnvironment.class)
            model.setStandardInput(new ByteArrayInputStream('yo!'.bytes))
            model.get()
        }

        then:
        assertExceptionInformative(e, "setStandardInput()")
        e.printStackTrace()
    }

    void assertExceptionInformative(Exception actual, String expectedMessageSubstring) {
        assert actual instanceof GradleConnectionException
        assert !actual.message.contains(Exceptions.INCOMPATIBLE_VERSION_HINT) //no need for hint, the message is already good
        assert actual.cause instanceof UnsupportedMethodException
        assert actual.cause.message.contains(expectedMessageSubstring)
    }
}
