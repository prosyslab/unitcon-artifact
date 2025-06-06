/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package opennlp.tools.tokenize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

/**
 * Tests for the {@link TokenizerModel} class.
 */
public class TokenizerModelTest {

  // @Test
  // public void testSentenceModel() throws IOException {

  //   TokenizerModel model = TokenizerTestUtil.createSimpleMaxentTokenModel();

  //   ByteArrayOutputStream arrayOut = new ByteArrayOutputStream();
  //   model.serialize(arrayOut);
  //   arrayOut.close();

  //   model = new TokenizerModel(new ByteArrayInputStream(arrayOut.toByteArray()));
  //   // TODO: check that both maxent models are equal

  //   // Also test serialization after building model from an inputstream
  //   arrayOut = new ByteArrayOutputStream();
  //   model.serialize(arrayOut);
  //   arrayOut.close();

  //   new TokenizerModel(new ByteArrayInputStream(arrayOut.toByteArray()));

  //   // TODO: check that both maxent models are equal
  // }
}
