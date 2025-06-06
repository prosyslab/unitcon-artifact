/*
 * ====================================================================
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
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.hc.client5.http.auth;

import java.util.Arrays;

import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.junit.Assert;
import org.junit.Test;

public class TestAuthChallenge {

    // @Test
    // public void testAuthChallengeWithValue() {
    //     final AuthChallenge authChallenge = new AuthChallenge(ChallengeType.TARGET, StandardAuthScheme.BASIC, "blah", null);
    //     Assert.assertEquals(StandardAuthScheme.BASIC, authChallenge.getSchemeName());
    //     Assert.assertEquals("blah", authChallenge.getValue());
    //     Assert.assertEquals(null, authChallenge.getParams());
    //     Assert.assertEquals(StandardAuthScheme.BASIC + " blah", authChallenge.toString());
    // }

    // @Test
    // public void testAuthChallengeWithParams() {
    //     final AuthChallenge authChallenge = new AuthChallenge(ChallengeType.TARGET, StandardAuthScheme.BASIC, null,
    //             Arrays.asList(new BasicNameValuePair("blah", "this"), new BasicNameValuePair("blah", "that")));
    //     Assert.assertEquals(StandardAuthScheme.BASIC, authChallenge.getSchemeName());
    //     Assert.assertEquals(null, authChallenge.getValue());
    //     Assert.assertNotNull(authChallenge.getParams());
    //     Assert.assertEquals(StandardAuthScheme.BASIC + " [blah=this, blah=that]", authChallenge.toString());
    // }

}
