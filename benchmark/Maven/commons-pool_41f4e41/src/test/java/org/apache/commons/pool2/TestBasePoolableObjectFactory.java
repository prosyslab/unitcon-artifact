/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.pool2;

import static org.junit.Assert.assertTrue;

import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.junit.Test;

/**
 */
public class TestBasePoolableObjectFactory {

    // @Test
    // public void testDefaultMethods() throws Exception {
    //     final PooledObjectFactory<Object> factory = new TestFactory();

    //     factory.activateObject(null); // a no-op
    //     factory.passivateObject(null); // a no-op
    //     factory.destroyObject(null); // a no-op
    //     assertTrue(factory.validateObject(null)); // constant true
    // }

    private static class TestFactory extends BasePooledObjectFactory<Object> {
        @Override
        public Object create() throws Exception {
            return null;
        }
        @Override
        public PooledObject<Object> wrap(final Object value) {
            return new DefaultPooledObject<>(value);
        }
    }
}
