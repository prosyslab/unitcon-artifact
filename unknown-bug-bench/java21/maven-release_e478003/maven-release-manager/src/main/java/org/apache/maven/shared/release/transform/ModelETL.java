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
package org.apache.maven.shared.release.transform;

import java.io.File;

import org.apache.maven.model.Model;
import org.apache.maven.shared.release.ReleaseExecutionException;

/**
 * <p>ModelETL interface.</p>
 *
 * @author Robert Scholte
 * @since 3.0
 */
public interface ModelETL {
    /**
     * <p>extract.</p>
     *
     * @param pomFile a {@link java.io.File} object
     * @throws org.apache.maven.shared.release.ReleaseExecutionException if any.
     */
    void extract(File pomFile) throws ReleaseExecutionException;

    /**
     * <p>transform.</p>
     */
    void transform();

    /**
     * <p>load.</p>
     *
     * @param pomFile a {@link java.io.File} object
     * @throws org.apache.maven.shared.release.ReleaseExecutionException if any.
     */
    void load(File pomFile) throws ReleaseExecutionException;

    // will be removed once transform() is implemented
    /**
     * <p>getModel.</p>
     *
     * @return a {@link org.apache.maven.model.Model} object
     */
    @Deprecated
    Model getModel();
}
