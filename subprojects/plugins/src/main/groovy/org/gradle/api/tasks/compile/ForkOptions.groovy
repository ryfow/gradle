/*
 * Copyright 2007 the original author or authors.
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
 
package org.gradle.api.tasks.compile

import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

/**
 * @author Hans Dockter
 */
class ForkOptions extends AbstractOptions {
    /**
     * The executable to use to fork the compiler.
     */
    @Input @Optional
    String executable = null

    /**
     * The initial heap size for the compiler process.
     */
    String memoryInitialSize = null

    /**
     * The maximum heap size for the compiler process.
     */
    String memoryMaximumSize = null

  /**
   * Directory for temporary files. Only used if compilation is done by an
   * underlying Ant javac task, happens in a forked process, and the command
   * line args length exceeds 4k. Defaults to <tt>java.io.tmpdir</tt>.
   */
    String tempDir = null

    /**
     * Any additional JVM arguments for the compiler process.
     */
    List jvmArgs = []

    /**
     * Whether to use the Gradle compiler daemon or simply forking a new process
     * for each Compile task. Defaults to <tt>false</tt>.
     */
    boolean useCompilerDaemon = false
    
    Map fieldName2AntMap() {
        [tempDir: 'tempdir']
    }

    List excludedFieldsFromOptionMap() {
        ["jvmArgs", "useAntForking", "useCompilerDaemon"]
    }
}
