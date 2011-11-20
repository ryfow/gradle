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
package org.gradle.integtests

import java.util.logging.LogManager
import org.gradle.integtests.fixtures.GradleDistributionExecuter
import org.gradle.integtests.fixtures.GradleDistributionExecuter.Executer
import org.gradle.integtests.fixtures.internal.AbstractIntegrationSpec
import org.gradle.integtests.tooling.fixture.ToolingApi
import org.gradle.tooling.model.BuildableProject

class GlobalLoggingManipulationIntegrationTest extends AbstractIntegrationSpec {

    final ToolingApi toolingApi = new ToolingApi(distribution)

    def "tooling api does not replace standard streams"() {
        //(SF) only checking if the instances of out and err were not replaced
        //it would be nice to have more meaningful test that verifies the effects of replacing the streams
        given:
        def outInstance = System.out
        def errInstance = System.err

        buildFile << "task hey"

        when:
        BuildableProject model = toolingApi.embeddedOnly().withConnection { connection -> connection.getModel(BuildableProject.class) }

        then:
        model.tasks.find { it.name == 'hey' }
        System.out.is(outInstance)
        System.err.is(errInstance)
    }

    def "tooling api does not reset the java logging"() {
        //(SF) checking if the logger level was not overridden.
        //this gives some confidence that the LogManager was not reset
        given:
        LogManager.getLogManager().getLogger("").setLevel(java.util.logging.Level.OFF);
        buildFile << "task hey"

        when:
        assert java.util.logging.Level.OFF == LogManager.getLogManager().getLogger("").level
        BuildableProject model = toolingApi.embeddedOnly().withConnection { connection -> connection.getModel(BuildableProject.class) }

        then:
        model.tasks.find { it.name == 'hey' }
        java.util.logging.Level.OFF == LogManager.getLogManager().getLogger("").level
    }

    def "using gradle without tooling api may mess around the standard streams and java logging"() {
        //(SF) this test assumes that our logging commodity replaces standard streams and resets java logging
        //not great, but at least a start.
        given:

        //it only make sense to test in embedded mode.
        //Forking mode has his own process that we don't control from the test so it's hard to assert.
        executer = new GradleDistributionExecuter(Executer.embedded, distribution)

        def outInstance = System.out
        def errInstance = System.err

        buildFile << "task hey"

        when:
        succeeds 'hey'

        then:
        !System.out.is(outInstance)
        !System.err.is(errInstance)
        //We don't really care that the sys out/err has changed even though it affects other tests
        //All integration tests have this 'feature' and apparently it is not a great deal of a problem at the moment
    }
}
