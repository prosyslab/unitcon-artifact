/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avro.reflect;

import org.apache.avro.Schema;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public class TestReflectData {
  // @Test
  // @SuppressWarnings("unchecked")
  // public void testWeakSchemaCaching() throws Exception {
  //   int numSchemas = 1000000;
  //   for (int i = 0; i < numSchemas; i++) {
  //     // Create schema
  //     Schema schema = Schema.createRecord("schema", null, null, false);
  //     schema.setFields(Collections.emptyList());

  //     ReflectData.get().getRecordState(new Object(), schema);
  //   }

  //   // Reflect the number of schemas currently in the cache
  //   ReflectData.ClassAccessorData classData = ReflectData.ACCESSOR_CACHE
  //       .get(Object.class);

  //   System.gc(); // Not guaranteed, but seems to be reliable enough

  //   assertThat("ReflectData cache should release references",
  //       classData.bySchema.size(), lessThan(numSchemas));
  // }
}
