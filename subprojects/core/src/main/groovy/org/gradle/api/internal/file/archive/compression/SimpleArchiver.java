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

package org.gradle.api.internal.file.archive.compression;

import org.gradle.api.tasks.bundling.Compression;
import org.gradle.api.tasks.bundling.CompressionAware;

import java.io.*;

/**
 * by Szczepan Faber, created at: 11/16/11
 */
public class SimpleArchiver implements Archiver, CompressionAware {

    public InputStream decompress(File source) {
        try {
            return new FileInputStream(source);
        } catch (Exception e) {
            String message = String.format("Unable to create input stream for file: %s due to: %s.", source.getName(), e.getMessage());
            throw new RuntimeException(message, e);
        }
    }

    public OutputStream compress(File destination) {
        try {
            return new FileOutputStream(destination);
        } catch (Exception e) {
            String message = String.format("Unable to create output stream for file: %s due to: %s ", destination, e.getMessage());
            throw new RuntimeException(message, e);
        }
    }

    public Compression getCompression() {
        return Compression.NONE;
    }
}
