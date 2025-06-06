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

package org.apache.hc.client5.http.impl.routing;

import java.net.URI;

import org.apache.hc.client5.http.routing.RoutingSupport;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.net.URIAuthority;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class TestRoutingSupport {

    // @Test
    // public void testDetermineHost() throws Exception {
    //     final HttpRequest request1 = new BasicHttpRequest("GET", "/");
    //     final HttpHost host1 = RoutingSupport.determineHost(request1);
    //     Assert.assertThat(host1, CoreMatchers.nullValue());

    //     final HttpRequest request2 = new BasicHttpRequest("GET", new URI("https://somehost:8443/"));
    //     final HttpHost host2 = RoutingSupport.determineHost(request2);
    //     Assert.assertThat(host2, CoreMatchers.equalTo(new HttpHost("https", "somehost", 8443)));
    // }

    // @Test(expected = ProtocolException.class)
    // public void testDetermineHostMissingScheme() throws Exception {
    //     final HttpRequest request1 = new BasicHttpRequest("GET", "/");
    //     request1.setAuthority(new URIAuthority("host"));
    //     RoutingSupport.determineHost(request1);
    // }

}
