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
package org.apache.hc.client5.http.impl.cache;

import java.net.SocketTimeoutException;
import java.util.Date;

import org.apache.hc.client5.http.utils.DateUtils;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.junit.Assert;
import org.junit.Test;

/**
 * This class tests behavior that is allowed (MAY) by the HTTP/1.1 protocol
 * specification and for which we have implemented the behavior in HTTP cache.
 */
public class TestProtocolAllowedBehavior extends AbstractProtocolTest {

    // @Test
    // public void testNonSharedCacheReturnsStaleResponseWhenRevalidationFailsForProxyRevalidate()
    //     throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET","/");
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     originResponse.setHeader("Date", DateUtils.formatDate(tenSecondsAgo));
    //     originResponse.setHeader("Cache-Control","max-age=5,proxy-revalidate");
    //     originResponse.setHeader("Etag","\"etag\"");

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET","/");

    //     backendExpectsAnyRequest().andThrow(new SocketTimeoutException());

    //     replayMocks();
    //     behaveAsNonSharedCache();
    //     execute(req1);
    //     final HttpResponse result = execute(req2);
    //     verifyMocks();

    //     Assert.assertEquals(HttpStatus.SC_OK, result.getCode());
    // }

    // @Test
    // public void testNonSharedCacheMayCacheResponsesWithCacheControlPrivate()
    //     throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET","/");
    //     originResponse.setHeader("Cache-Control","private,max-age=3600");

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET","/");

    //     replayMocks();
    //     behaveAsNonSharedCache();
    //     execute(req1);
    //     final HttpResponse result = execute(req2);
    //     verifyMocks();

    //     Assert.assertEquals(HttpStatus.SC_OK, result.getCode());
    // }
}
