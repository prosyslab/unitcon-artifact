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

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.cache.HttpCacheEntry;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.utils.DateUtils;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HeaderElements;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.HttpVersion;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.MessageSupport;
import org.apache.hc.core5.util.ByteArrayBuffer;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * We are a conditionally-compliant HTTP/1.1 client with a cache. However, a lot
 * of the rules for proxies apply to us, as far as proper operation of the
 * requests that pass through us. Generally speaking, we want to make sure that
 * any response returned from our HttpClient.execute() methods is conditionally
 * compliant with the rules for an HTTP/1.1 server, and that any requests we
 * pass downstream to the backend HttpClient are are conditionally compliant
 * with the rules for an HTTP/1.1 client.
 */
public class TestProtocolRequirements extends AbstractProtocolTest {

    // @Test
    // public void testCacheMissOnGETUsesOriginResponse() throws Exception {
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     eqRequest(request),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();
    //     Assert.assertTrue(HttpTestUtils.semanticallyTransparent(originResponse, result));
    // }

    /*
     * "Proxy and gateway applications need to be careful when forwarding
     * messages in protocol versions different from that of the application.
     * Since the protocol version indicates the protocol capability of the
     * sender, a proxy/gateway MUST NOT send a message with a version indicator
     * which is greater than its actual version. If a higher version request is
     * received, the proxy/gateway MUST either downgrade the request version, or
     * respond with an error, or switch to tunnel behavior."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.1
     */
    // @Test
    // public void testHigherMajorProtocolVersionsOnRequestSwitchToTunnelBehavior() throws Exception {

    //     // tunnel behavior: I don't muck with request or response in
    //     // any way
    //     request = new BasicClassicHttpRequest("GET", "/foo");
    //     request.setVersion(new ProtocolVersion("HTTP", 2, 13));

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     eqRequest(request),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();
    //     Assert.assertSame(originResponse, result);
    // }

    // @Test
    // public void testHigher1_XProtocolVersionsDowngradeTo1_1() throws Exception {

    //     request = new BasicClassicHttpRequest("GET", "/foo");
    //     request.setVersion(new ProtocolVersion("HTTP", 1, 2));

    //     final ClassicHttpRequest downgraded = new BasicClassicHttpRequest("GET", "/foo");
    //     downgraded.setVersion(HttpVersion.HTTP_1_1);

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     eqRequest(downgraded),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();
    //     Assert.assertTrue(HttpTestUtils.semanticallyTransparent(originResponse, result));
    // }

    /*
     * "Due to interoperability problems with HTTP/1.0 proxies discovered since
     * the publication of RFC 2068[33], caching proxies MUST, gateways MAY, and
     * tunnels MUST NOT upgrade the request to the highest version they support.
     * The proxy/gateway's response to that request MUST be in the same major
     * version as the request."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.1
     */
    // @Test
    // public void testRequestsWithLowerProtocolVersionsGetUpgradedTo1_1() throws Exception {

    //     request = new BasicClassicHttpRequest("GET", "/foo");
    //     request.setVersion(new ProtocolVersion("HTTP", 1, 0));
    //     final ClassicHttpRequest upgraded = new BasicClassicHttpRequest("GET", "/foo");
    //     upgraded.setVersion(HttpVersion.HTTP_1_1);

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     eqRequest(upgraded),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();
    //     Assert.assertTrue(HttpTestUtils.semanticallyTransparent(originResponse, result));
    // }

    /*
     * "An HTTP server SHOULD send a response version equal to the highest
     * version for which the server is at least conditionally compliant, and
     * whose major version is less than or equal to the one received in the
     * request."
     *
     * http://www.ietf.org/rfc/rfc2145.txt
     */
    // @Test
    // public void testLowerOriginResponsesUpgradedToOurVersion1_1() throws Exception {
    //     originResponse = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
    //     originResponse.setVersion(new ProtocolVersion("HTTP", 1, 2));
    //     originResponse.setHeader("Date", DateUtils.formatDate(new Date()));
    //     originResponse.setHeader("Server", "MockOrigin/1.0");
    //     originResponse.setEntity(body);

    //     // not testing this internal behavior in this test, just want
    //     // to check the protocol version that comes out the other end
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();
    //     Assert.assertEquals(HttpVersion.HTTP_1_1, result.getVersion());
    // }

    // @Test
    // public void testResponseToA1_0RequestShouldUse1_1() throws Exception {
    //     request = new BasicClassicHttpRequest("GET", "/foo");
    //     request.setVersion(new ProtocolVersion("HTTP", 1, 0));

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();
    //     Assert.assertEquals(HttpVersion.HTTP_1_1, result.getVersion());
    // }

    /*
     * "A proxy MUST forward an unknown header, unless it is protected by a
     * Connection header." http://www.ietf.org/rfc/rfc2145.txt
     */
    // @Test
    // public void testForwardsUnknownHeadersOnRequestsFromHigherProtocolVersions() throws Exception {
    //     request = new BasicClassicHttpRequest("GET", "/foo");
    //     request.setVersion(new ProtocolVersion("HTTP", 1, 2));
    //     request.removeHeaders("Connection");
    //     request.addHeader("X-Unknown-Header", "some-value");

    //     final ClassicHttpRequest downgraded = new BasicClassicHttpRequest("GET", "/foo");
    //     downgraded.setVersion(HttpVersion.HTTP_1_1);
    //     downgraded.removeHeaders("Connection");
    //     downgraded.addHeader("X-Unknown-Header", "some-value");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     eqRequest(downgraded),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     execute(request);

    //     verifyMocks();
    // }

    /*
     * "A server MUST NOT send transfer-codings to an HTTP/1.0 client."
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.6
     */
    // @Test
    // public void testTransferCodingsAreNotSentToAnHTTP_1_0Client() throws Exception {

    //     originResponse.setHeader("Transfer-Encoding", "identity");

    //     final ClassicHttpRequest originalRequest = new BasicClassicHttpRequest("GET", "/foo");
    //     originalRequest.setVersion(new ProtocolVersion("HTTP", 1, 0));

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     final ClassicHttpResponse result = execute(originalRequest);

    //     verifyMocks();

    //     Assert.assertNull(result.getFirstHeader("TE"));
    //     Assert.assertNull(result.getFirstHeader("Transfer-Encoding"));
    // }

    /*
     * "Multiple message-header fields with the same field-name MAY be present
     * in a message if and only if the entire field-value for that header field
     * is defined as a comma-separated list [i.e., #(values)]. It MUST be
     * possible to combine the multiple header fields into one
     * "field-name: field-value" pair, without changing the semantics of the
     * message, by appending each subsequent field-value to the first, each
     * separated by a comma. The order in which header fields with the same
     * field-name are received is therefore significant to the interpretation of
     * the combined field value, and thus a proxy MUST NOT change the order of
     * these field values when a message is forwarded."
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
     */
    private void testOrderOfMultipleHeadersIsPreservedOnRequests(final String h, final ClassicHttpRequest request) throws Exception {
        final Capture<ClassicHttpRequest> reqCapture = EasyMock.newCapture();

        EasyMock.expect(
                mockExecChain.proceed(
                        EasyMock.capture(reqCapture),
                        EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
        replayMocks();

        execute(request);

        verifyMocks();

        final ClassicHttpRequest forwarded = reqCapture.getValue();
        Assert.assertNotNull(forwarded);
        final String expected = HttpTestUtils.getCanonicalHeaderValue(request, h);
        final String actual = HttpTestUtils.getCanonicalHeaderValue(forwarded, h);
        if (!actual.contains(expected)) {
            Assert.assertEquals(expected, actual);
        }

    }

    // @Test
    // public void testOrderOfMultipleAcceptHeaderValuesIsPreservedOnRequests() throws Exception {
    //     request.addHeader("Accept", "audio/*; q=0.2, audio/basic");
    //     request.addHeader("Accept", "text/*, text/html, text/html;level=1, */*");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Accept", request);
    // }

    // @Test
    // public void testOrderOfMultipleAcceptCharsetHeadersIsPreservedOnRequests() throws Exception {
    //     request.addHeader("Accept-Charset", "iso-8859-5");
    //     request.addHeader("Accept-Charset", "unicode-1-1;q=0.8");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Accept-Charset", request);
    // }

    // @Test
    // public void testOrderOfMultipleAcceptEncodingHeadersIsPreservedOnRequests() throws Exception {
    //     request.addHeader("Accept-Encoding", "identity");
    //     request.addHeader("Accept-Encoding", "compress, gzip");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Accept-Encoding", request);
    // }

    // @Test
    // public void testOrderOfMultipleAcceptLanguageHeadersIsPreservedOnRequests() throws Exception {
    //     request.addHeader("Accept-Language", "da, en-gb;q=0.8, en;q=0.7");
    //     request.addHeader("Accept-Language", "i-cherokee");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Accept-Encoding", request);
    // }

    // @Test
    // public void testOrderOfMultipleAllowHeadersIsPreservedOnRequests() throws Exception {
    //     final BasicClassicHttpRequest put = new BasicClassicHttpRequest("PUT", "/");
    //     put.setEntity(body);
    //     put.addHeader("Allow", "GET, HEAD");
    //     put.addHeader("Allow", "DELETE");
    //     put.addHeader("Content-Length", "128");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Allow", put);
    // }

    // @Test
    // public void testOrderOfMultipleCacheControlHeadersIsPreservedOnRequests() throws Exception {
    //     request.addHeader("Cache-Control", "max-age=5");
    //     request.addHeader("Cache-Control", "min-fresh=10");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Cache-Control", request);
    // }

    // @Test
    // public void testOrderOfMultipleContentEncodingHeadersIsPreservedOnRequests() throws Exception {
    //     final BasicClassicHttpRequest post = new BasicClassicHttpRequest("POST", "/");
    //     post.setEntity(body);
    //     post.addHeader("Content-Encoding", "gzip");
    //     post.addHeader("Content-Encoding", "compress");
    //     post.addHeader("Content-Length", "128");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Content-Encoding", post);
    // }

    // @Test
    // public void testOrderOfMultipleContentLanguageHeadersIsPreservedOnRequests() throws Exception {
    //     final BasicClassicHttpRequest post = new BasicClassicHttpRequest("POST", "/");
    //     post.setEntity(body);
    //     post.addHeader("Content-Language", "mi");
    //     post.addHeader("Content-Language", "en");
    //     post.addHeader("Content-Length", "128");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Content-Language", post);
    // }

    // @Test
    // public void testOrderOfMultipleExpectHeadersIsPreservedOnRequests() throws Exception {
    //     final BasicClassicHttpRequest post = new BasicClassicHttpRequest("POST", "/");
    //     post.setEntity(body);
    //     post.addHeader("Expect", "100-continue");
    //     post.addHeader("Expect", "x-expect=true");
    //     post.addHeader("Content-Length", "128");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Expect", post);
    // }

    // @Test
    // public void testOrderOfMultiplePragmaHeadersIsPreservedOnRequests() throws Exception {
    //     request.addHeader("Pragma", "no-cache");
    //     request.addHeader("Pragma", "x-pragma-1, x-pragma-2");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Pragma", request);
    // }

    // @Test
    // public void testOrderOfMultipleViaHeadersIsPreservedOnRequests() throws Exception {
    //     request.addHeader("Via", "1.0 fred, 1.1 nowhere.com (Apache/1.1)");
    //     request.addHeader("Via", "1.0 ricky, 1.1 mertz, 1.0 lucy");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Via", request);
    // }

    // @Test
    // public void testOrderOfMultipleWarningHeadersIsPreservedOnRequests() throws Exception {
    //     request.addHeader("Warning", "199 fred \"bargle\"");
    //     request.addHeader("Warning", "199 barney \"bungle\"");
    //     testOrderOfMultipleHeadersIsPreservedOnRequests("Warning", request);
    // }

    private void testOrderOfMultipleHeadersIsPreservedOnResponses(final String h) throws Exception {
        EasyMock.expect(
                mockExecChain.proceed(
                        EasyMock.isA(ClassicHttpRequest.class),
                        EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
        replayMocks();

        final ClassicHttpResponse result = execute(request);

        verifyMocks();

        Assert.assertNotNull(result);
        Assert.assertEquals(HttpTestUtils.getCanonicalHeaderValue(originResponse, h), HttpTestUtils
                .getCanonicalHeaderValue(result, h));

    }

    // @Test
    // public void testOrderOfMultipleAllowHeadersIsPreservedOnResponses() throws Exception {
    //     originResponse = new BasicClassicHttpResponse(405, "Method Not Allowed");
    //     originResponse.addHeader("Allow", "HEAD");
    //     originResponse.addHeader("Allow", "DELETE");
    //     testOrderOfMultipleHeadersIsPreservedOnResponses("Allow");
    // }

    // @Test
    // public void testOrderOfMultipleCacheControlHeadersIsPreservedOnResponses() throws Exception {
    //     originResponse.addHeader("Cache-Control", "max-age=0");
    //     originResponse.addHeader("Cache-Control", "no-store, must-revalidate");
    //     testOrderOfMultipleHeadersIsPreservedOnResponses("Cache-Control");
    // }

    // @Test
    // public void testOrderOfMultipleContentEncodingHeadersIsPreservedOnResponses() throws Exception {
    //     originResponse.addHeader("Content-Encoding", "gzip");
    //     originResponse.addHeader("Content-Encoding", "compress");
    //     testOrderOfMultipleHeadersIsPreservedOnResponses("Content-Encoding");
    // }

    // @Test
    // public void testOrderOfMultipleContentLanguageHeadersIsPreservedOnResponses() throws Exception {
    //     originResponse.addHeader("Content-Language", "mi");
    //     originResponse.addHeader("Content-Language", "en");
    //     testOrderOfMultipleHeadersIsPreservedOnResponses("Content-Language");
    // }

    // @Test
    // public void testOrderOfMultiplePragmaHeadersIsPreservedOnResponses() throws Exception {
    //     originResponse.addHeader("Pragma", "no-cache, x-pragma-2");
    //     originResponse.addHeader("Pragma", "x-pragma-1");
    //     testOrderOfMultipleHeadersIsPreservedOnResponses("Pragma");
    // }

    // @Test
    // public void testOrderOfMultipleViaHeadersIsPreservedOnResponses() throws Exception {
    //     originResponse.addHeader("Via", "1.0 fred, 1.1 nowhere.com (Apache/1.1)");
    //     originResponse.addHeader("Via", "1.0 ricky, 1.1 mertz, 1.0 lucy");
    //     testOrderOfMultipleHeadersIsPreservedOnResponses("Via");
    // }

    // @Test
    // public void testOrderOfMultipleWWWAuthenticateHeadersIsPreservedOnResponses() throws Exception {
    //     originResponse.addHeader("WWW-Authenticate", "x-challenge-1");
    //     originResponse.addHeader("WWW-Authenticate", "x-challenge-2");
    //     testOrderOfMultipleHeadersIsPreservedOnResponses("WWW-Authenticate");
    // }

    /*
     * "However, applications MUST understand the class of any status code, as
     * indicated by the first digit, and treat any unrecognized response as
     * being equivalent to the x00 status code of that class, with the exception
     * that an unrecognized response MUST NOT be cached."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html#sec6.1.1
     */
    private void testUnknownResponseStatusCodeIsNotCached(final int code) throws Exception {

        emptyMockCacheExpectsNoPuts();

        originResponse = new BasicClassicHttpResponse(code, "Moo");
        originResponse.setHeader("Date", DateUtils.formatDate(new Date()));
        originResponse.setHeader("Server", "MockOrigin/1.0");
        originResponse.setHeader("Cache-Control", "max-age=3600");
        originResponse.setEntity(body);

        EasyMock.expect(
                mockExecChain.proceed(
                        EasyMock.isA(ClassicHttpRequest.class),
                        EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

        replayMocks();

        execute(request);

        // in particular, there were no storage calls on the cache
        verifyMocks();
    }

    // @Test
    // public void testUnknownResponseStatusCodesAreNotCached() throws Exception {
    //     for (int i = 102; i <= 199; i++) {
    //         testUnknownResponseStatusCodeIsNotCached(i);
    //     }
    //     for (int i = 207; i <= 299; i++) {
    //         testUnknownResponseStatusCodeIsNotCached(i);
    //     }
    //     for (int i = 308; i <= 399; i++) {
    //         testUnknownResponseStatusCodeIsNotCached(i);
    //     }
    //     for (int i = 418; i <= 499; i++) {
    //         testUnknownResponseStatusCodeIsNotCached(i);
    //     }
    //     for (int i = 506; i <= 999; i++) {
    //         testUnknownResponseStatusCodeIsNotCached(i);
    //     }
    // }

    /*
     * "Unrecognized header fields SHOULD be ignored by the recipient and MUST
     * be forwarded by transparent proxies."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec7.html#sec7.1
     */
    // @Test
    // public void testUnknownHeadersOnRequestsAreForwarded() throws Exception {
    //     request.addHeader("X-Unknown-Header", "blahblah");
    //     final Capture<ClassicHttpRequest> reqCap = EasyMock.newCapture();
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.capture(reqCap),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     execute(request);

    //     verifyMocks();
    //     final ClassicHttpRequest forwarded = reqCap.getValue();
    //     final Header[] hdrs = forwarded.getHeaders("X-Unknown-Header");
    //     Assert.assertEquals(1, hdrs.length);
    //     Assert.assertEquals("blahblah", hdrs[0].getValue());
    // }

    // @Test
    // public void testUnknownHeadersOnResponsesAreForwarded() throws Exception {
    //     originResponse.addHeader("X-Unknown-Header", "blahblah");
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();
    //     final Header[] hdrs = result.getHeaders("X-Unknown-Header");
    //     Assert.assertEquals(1, hdrs.length);
    //     Assert.assertEquals("blahblah", hdrs[0].getValue());
    // }

    // /*
    //  * "If a client will wait for a 100 (Continue) response before sending the
    //  * request body, it MUST send an Expect request-header field (section 14.20)
    //  * with the '100-continue' expectation."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec8.html#sec8.2.3
    //  */
    // @Test
    // public void testRequestsExpecting100ContinueBehaviorShouldSetExpectHeader() throws Exception {
    //     final BasicClassicHttpRequest post = new BasicClassicHttpRequest("POST", "/");
    //     post.setHeader(HttpHeaders.EXPECT, HeaderElements.CONTINUE);
    //     post.setHeader("Content-Length", "128");
    //     post.setEntity(new StringEntity("whatever"));

    //     final Capture<ClassicHttpRequest> reqCap = EasyMock.newCapture();

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.capture(reqCap),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     execute(post);

    //     verifyMocks();

    //     final ClassicHttpRequest forwarded = reqCap.getValue();
    //     boolean foundExpect = false;
    //     final Iterator<HeaderElement> it = MessageSupport.iterate(forwarded, HttpHeaders.EXPECT);
    //     while (it.hasNext()) {
    //         final HeaderElement elt = it.next();
    //         if ("100-continue".equalsIgnoreCase(elt.getName())) {
    //             foundExpect = true;
    //             break;
    //         }
    //     }
    //     Assert.assertTrue(foundExpect);
    // }

    // /*
    //  * "If a client will wait for a 100 (Continue) response before sending the
    //  * request body, it MUST send an Expect request-header field (section 14.20)
    //  * with the '100-continue' expectation."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec8.html#sec8.2.3
    //  */
    // @Test
    // public void testRequestsNotExpecting100ContinueBehaviorShouldNotSetExpectContinueHeader() throws Exception {
    //     final BasicClassicHttpRequest post = new BasicClassicHttpRequest("POST", "/");
    //     post.setHeader("Content-Length", "128");
    //     post.setEntity(new StringEntity("whatever"));

    //     final Capture<ClassicHttpRequest> reqCap = EasyMock.newCapture();

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.capture(reqCap),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     execute(post);

    //     verifyMocks();

    //     final ClassicHttpRequest forwarded = reqCap.getValue();
    //     boolean foundExpect = false;
    //     final Iterator<HeaderElement> it = MessageSupport.iterate(forwarded, HttpHeaders.EXPECT);
    //     while (it.hasNext()) {
    //         final HeaderElement elt = it.next();
    //         if ("100-continue".equalsIgnoreCase(elt.getName())) {
    //             foundExpect = true;
    //             break;
    //         }
    //     }
    //     Assert.assertFalse(foundExpect);
    // }

    // /*
    //  * "If a proxy receives a request that includes an Expect request- header
    //  * field with the '100-continue' expectation, and the proxy either knows
    //  * that the next-hop server complies with HTTP/1.1 or higher, or does not
    //  * know the HTTP version of the next-hop server, it MUST forward the
    //  * request, including the Expect header field.
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec8.html#sec8.2.3
    //  */
    // @Test
    // public void testExpectHeadersAreForwardedOnRequests() throws Exception {
    //     // This would mostly apply to us if we were part of an
    //     // application that was a proxy, and would be the
    //     // responsibility of the greater application. Our
    //     // responsibility is to make sure that if we get an
    //     // entity-enclosing request that we properly set (or unset)
    //     // the Expect header per the request.expectContinue() flag,
    //     // which is tested by the previous few tests.
    // }

    // /*
    //  * "A proxy MUST NOT forward a 100 (Continue) response if the request
    //  * message was received from an HTTP/1.0 (or earlier) client and did not
    //  * include an Expect request-header field with the '100-continue'
    //  * expectation. This requirement overrides the general rule for forwarding
    //  * of 1xx responses (see section 10.1)."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec8.html#sec8.2.3
    //  */
    // @Test
    // public void test100ContinueResponsesAreNotForwardedTo1_0ClientsWhoDidNotAskForThem() throws Exception {

    //     final BasicClassicHttpRequest post = new BasicClassicHttpRequest("POST", "/");
    //     post.setVersion(new ProtocolVersion("HTTP", 1, 0));
    //     post.setEntity(body);
    //     post.setHeader("Content-Length", "128");

    //     originResponse = new BasicClassicHttpResponse(100, "Continue");
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     try {
    //         // if a 100 response gets up to us from the HttpClient
    //         // backend, we can't really handle it at that point
    //         execute(post);
    //         Assert.fail("should have thrown an exception");
    //     } catch (final ClientProtocolException expected) {
    //     }

    //     verifyMocks();
    // }

    // /*
    //  * "9.2 OPTIONS. ...Responses to this method are not cacheable.
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.2
    //  */
    // @Test
    // public void testResponsesToOPTIONSAreNotCacheable() throws Exception {
    //     emptyMockCacheExpectsNoPuts();
    //     request = new BasicClassicHttpRequest("OPTIONS", "/");
    //     originResponse.addHeader("Cache-Control", "max-age=3600");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     execute(request);

    //     verifyMocks();
    // }

    // /*
    //  * "A 200 response SHOULD .... If no response body is included, the response
    //  * MUST include a Content-Length field with a field-value of '0'."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.2
    //  */
    // @Test
    // public void test200ResponseToOPTIONSWithNoBodyShouldIncludeContentLengthZero() throws Exception {

    //     request = new BasicClassicHttpRequest("OPTIONS", "/");
    //     originResponse.setEntity(null);
    //     originResponse.setHeader("Content-Length", "0");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();
    //     final Header contentLength = result.getFirstHeader("Content-Length");
    //     Assert.assertNotNull(contentLength);
    //     Assert.assertEquals("0", contentLength.getValue());
    // }

    // /*
    //  * "When a proxy receives an OPTIONS request on an absoluteURI for which
    //  * request forwarding is permitted, the proxy MUST check for a Max-Forwards
    //  * field. If the Max-Forwards field-value is zero ("0"), the proxy MUST NOT
    //  * forward the message; instead, the proxy SHOULD respond with its own
    //  * communication options."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.2
    //  */
    // @Test
    // public void testDoesNotForwardOPTIONSWhenMaxForwardsIsZeroOnAbsoluteURIRequest() throws Exception {
    //     request = new BasicClassicHttpRequest("OPTIONS", "*");
    //     request.setHeader("Max-Forwards", "0");

    //     replayMocks();
    //     execute(request);
    //     verifyMocks();
    // }

    // /*
    //  * "If the Max-Forwards field-value is an integer greater than zero, the
    //  * proxy MUST decrement the field-value when it forwards the request."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.2
    //  */
    // @Test
    // public void testDecrementsMaxForwardsWhenForwardingOPTIONSRequest() throws Exception {

    //     request = new BasicClassicHttpRequest("OPTIONS", "*");
    //     request.setHeader("Max-Forwards", "7");

    //     final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.capture(cap),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();
    //     execute(request);
    //     verifyMocks();

    //     final ClassicHttpRequest captured = cap.getValue();
    //     Assert.assertEquals("6", captured.getFirstHeader("Max-Forwards").getValue());
    // }

    // /*
    //  * "If no Max-Forwards field is present in the request, then the forwarded
    //  * request MUST NOT include a Max-Forwards field."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.2
    //  */
    // @Test
    // public void testDoesNotAddAMaxForwardsHeaderToForwardedOPTIONSRequests() throws Exception {
    //     request = new BasicClassicHttpRequest("OPTIONS", "/");
    //     final Capture<ClassicHttpRequest> reqCap = EasyMock.newCapture();
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.capture(reqCap),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();
    //     execute(request);
    //     verifyMocks();

    //     final ClassicHttpRequest forwarded = reqCap.getValue();
    //     Assert.assertNull(forwarded.getFirstHeader("Max-Forwards"));
    // }

    // /*
    //  * "The HEAD method is identical to GET except that the server MUST NOT
    //  * return a message-body in the response."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.4
    //  */
    // @Test
    // public void testResponseToAHEADRequestMustNotHaveABody() throws Exception {
    //     request = new BasicClassicHttpRequest("HEAD", "/");
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();

    //     Assert.assertTrue(result.getEntity() == null || result.getEntity().getContentLength() == 0);
    // }

    /*
     * "If the new field values indicate that the cached entity differs from the
     * current entity (as would be indicated by a change in Content-Length,
     * Content-MD5, ETag or Last-Modified), then the cache MUST treat the cache
     * entry as stale."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.4
     */
    private void testHEADResponseWithUpdatedEntityFieldsMakeACacheEntryStale(final String eHeader,
            final String oldVal, final String newVal) throws Exception {

        // put something cacheable in the cache
        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
        final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
        resp1.addHeader("Cache-Control", "max-age=3600");
        resp1.setHeader(eHeader, oldVal);

        // get a head that penetrates the cache
        final ClassicHttpRequest req2 = new BasicClassicHttpRequest("HEAD", "/");
        req2.addHeader("Cache-Control", "no-cache");
        final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();
        resp2.setEntity(null);
        resp2.setHeader(eHeader, newVal);

        // next request doesn't tolerate stale entry
        final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");
        req3.addHeader("Cache-Control", "max-stale=0");
        final ClassicHttpResponse resp3 = HttpTestUtils.make200Response();
        resp3.setHeader(eHeader, newVal);

        EasyMock.expect(
                mockExecChain.proceed(
                        eqRequest(req1),
                        EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
        EasyMock.expect(
                mockExecChain.proceed(
                        eqRequest(req2),
                        EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
        EasyMock.expect(
                mockExecChain.proceed(
                        EasyMock.isA(ClassicHttpRequest.class),
                        EasyMock.isA(ExecChain.Scope.class))).andReturn(resp3);

        replayMocks();

        execute(req1);
        execute(req2);
        execute(req3);

        verifyMocks();
    }

    // @Test
    // public void testHEADResponseWithUpdatedContentLengthFieldMakeACacheEntryStale() throws Exception {
    //     testHEADResponseWithUpdatedEntityFieldsMakeACacheEntryStale("Content-Length", "128", "127");
    // }

    // @Test
    // public void testHEADResponseWithUpdatedContentMD5FieldMakeACacheEntryStale() throws Exception {
    //     testHEADResponseWithUpdatedEntityFieldsMakeACacheEntryStale("Content-MD5",
    //             "Q2hlY2sgSW50ZWdyaXR5IQ==", "Q2hlY2sgSW50ZWdyaXR5IR==");

    // }

    // @Test
    // public void testHEADResponseWithUpdatedETagFieldMakeACacheEntryStale() throws Exception {
    //     testHEADResponseWithUpdatedEntityFieldsMakeACacheEntryStale("ETag", "\"etag1\"",
    //             "\"etag2\"");
    // }

    // @Test
    // public void testHEADResponseWithUpdatedLastModifiedFieldMakeACacheEntryStale() throws Exception {
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     final Date sixSecondsAgo = new Date(now.getTime() - 6 * 1000L);
    //     testHEADResponseWithUpdatedEntityFieldsMakeACacheEntryStale("Last-Modified", DateUtils
    //             .formatDate(tenSecondsAgo), DateUtils.formatDate(sixSecondsAgo));
    // }

    /*
     * "9.5 POST. Responses to this method are not cacheable, unless the
     * response includes appropriate Cache-Control or Expires header fields."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.5
     */
    // @Test
    // public void testResponsesToPOSTWithoutCacheControlOrExpiresAreNotCached() throws Exception {
    //     emptyMockCacheExpectsNoPuts();

    //     final BasicClassicHttpRequest post = new BasicClassicHttpRequest("POST", "/");
    //     post.setHeader("Content-Length", "128");
    //     post.setEntity(HttpTestUtils.makeBody(128));

    //     originResponse.removeHeaders("Cache-Control");
    //     originResponse.removeHeaders("Expires");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     execute(post);

    //     verifyMocks();
    // }

    // /*
    //  * "9.5 PUT. ...Responses to this method are not cacheable."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.6
    //  */
    // @Test
    // public void testResponsesToPUTsAreNotCached() throws Exception {
    //     emptyMockCacheExpectsNoPuts();

    //     final BasicClassicHttpRequest put = new BasicClassicHttpRequest("PUT", "/");
    //     put.setEntity(HttpTestUtils.makeBody(128));
    //     put.addHeader("Content-Length", "128");

    //     originResponse.setHeader("Cache-Control", "max-age=3600");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     execute(put);

    //     verifyMocks();
    // }

    // /*
    //  * "9.6 DELETE. ... Responses to this method are not cacheable."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.7
    //  */
    // @Test
    // public void testResponsesToDELETEsAreNotCached() throws Exception {
    //     emptyMockCacheExpectsNoPuts();

    //     request = new BasicClassicHttpRequest("DELETE", "/");
    //     originResponse.setHeader("Cache-Control", "max-age=3600");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     execute(request);

    //     verifyMocks();
    // }

    // /*
    //  * "9.8 TRACE ... Responses to this method MUST NOT be cached."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#sec9.8
    //  */
    // @Test
    // public void testResponsesToTRACEsAreNotCached() throws Exception {
    //     emptyMockCacheExpectsNoPuts();

    //     request = new BasicClassicHttpRequest("TRACE", "/");
    //     originResponse.setHeader("Cache-Control", "max-age=3600");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     execute(request);

    //     verifyMocks();
    // }

    // /*
    //  * "The 204 response MUST NOT include a message-body, and thus is always
    //  * terminated by the first empty line after the header fields."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5
    //  */
    // @Test
    // public void test204ResponsesDoNotContainMessageBodies() throws Exception {
    //     originResponse = new BasicClassicHttpResponse(HttpStatus.SC_NO_CONTENT, "No Content");
    //     originResponse.setEntity(HttpTestUtils.makeBody(entityLength));

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();
    // }

    // /*
    //  * "The [206] response MUST include the following header fields:
    //  *
    //  * - Either a Content-Range header field (section 14.16) indicating the
    //  * range included with this response, or a multipart/byteranges Content-Type
    //  * including Content-Range fields for each part. If a Content-Length header
    //  * field is present in the response, its value MUST match the actual number
    //  * of OCTETs transmitted in the message-body.
    //  *
    //  * - Date
    //  *
    //  * - ETag and/or Content-Location, if the header would have been sent in a
    //  * 200 response to the same request
    //  *
    //  * - Expires, Cache-Control, and/or Vary, if the field-value might differ
    //  * from that sent in any previous response for the same variant"
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.7
    //  */
    // @Test
    // public void test206ResponseGeneratedFromCacheMustHaveContentRangeOrMultipartByteRangesContentType() throws Exception {

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag", "\"etag\"");
    //     resp1.setHeader("Cache-Control", "max-age=3600");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range", "bytes=0-50");

    //     backendExpectsAnyRequestAndReturn(resp1).times(1, 2);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     if (HttpStatus.SC_PARTIAL_CONTENT == result.getCode()) {
    //         if (result.getFirstHeader("Content-Range") == null) {
    //             final HeaderElement elt = MessageSupport.parse(result.getFirstHeader("Content-Type"))[0];
    //             Assert.assertTrue("multipart/byteranges".equalsIgnoreCase(elt.getName()));
    //             Assert.assertNotNull(elt.getParameterByName("boundary"));
    //             Assert.assertNotNull(elt.getParameterByName("boundary").getValue());
    //             Assert.assertFalse("".equals(elt.getParameterByName("boundary").getValue().trim()));
    //         }
    //     }
    // }

    // @Test
    // public void test206ResponseGeneratedFromCacheMustHaveABodyThatMatchesContentLengthHeaderIfPresent() throws Exception {

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag", "\"etag\"");
    //     resp1.setHeader("Cache-Control", "max-age=3600");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range", "bytes=0-50");

    //     backendExpectsAnyRequestAndReturn(resp1).times(1, 2);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     if (HttpStatus.SC_PARTIAL_CONTENT == result.getCode()) {
    //         final Header h = result.getFirstHeader("Content-Length");
    //         if (h != null) {
    //             final int contentLength = Integer.parseInt(h.getValue());
    //             int bytesRead = 0;
    //             final InputStream i = result.getEntity().getContent();
    //             while ((i.read()) != -1) {
    //                 bytesRead++;
    //             }
    //             i.close();
    //             Assert.assertEquals(contentLength, bytesRead);
    //         }
    //     }
    // }

    // @Test
    // public void test206ResponseGeneratedFromCacheMustHaveDateHeader() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag", "\"etag\"");
    //     resp1.setHeader("Cache-Control", "max-age=3600");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range", "bytes=0-50");

    //     backendExpectsAnyRequestAndReturn(resp1).times(1, 2);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     if (HttpStatus.SC_PARTIAL_CONTENT == result.getCode()) {
    //         Assert.assertNotNull(result.getFirstHeader("Date"));
    //     }
    // }

    // @Test
    // public void test206ResponseReturnedToClientMustHaveDateHeader() throws Exception {
    //     request.addHeader("Range", "bytes=0-50");
    //     originResponse = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     originResponse.setHeader("Date", DateUtils.formatDate(new Date()));
    //     originResponse.setHeader("Server", "MockOrigin/1.0");
    //     originResponse.setEntity(HttpTestUtils.makeBody(500));
    //     originResponse.setHeader("Content-Range", "bytes 0-499/1234");
    //     originResponse.removeHeaders("Date");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);
    //     Assert.assertTrue(result.getCode() != HttpStatus.SC_PARTIAL_CONTENT
    //             || result.getFirstHeader("Date") != null);

    //     verifyMocks();
    // }

    // @Test
    // public void test206ContainsETagIfA200ResponseWouldHaveIncludedIt() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");

    //     originResponse.addHeader("Cache-Control", "max-age=3600");
    //     originResponse.addHeader("ETag", "\"etag1\"");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.addHeader("Range", "bytes=0-50");

    //     backendExpectsAnyRequest().andReturn(originResponse).times(1, 2);

    //     replayMocks();

    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);

    //     verifyMocks();

    //     if (result.getCode() == HttpStatus.SC_PARTIAL_CONTENT) {
    //         Assert.assertNotNull(result.getFirstHeader("ETag"));
    //     }
    // }

    // @Test
    // public void test206ContainsContentLocationIfA200ResponseWouldHaveIncludedIt() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");

    //     originResponse.addHeader("Cache-Control", "max-age=3600");
    //     originResponse.addHeader("Content-Location", "http://foo.example.com/other/url");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.addHeader("Range", "bytes=0-50");

    //     backendExpectsAnyRequest().andReturn(originResponse).times(1, 2);

    //     replayMocks();

    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);

    //     verifyMocks();

    //     if (result.getCode() == HttpStatus.SC_PARTIAL_CONTENT) {
    //         Assert.assertNotNull(result.getFirstHeader("Content-Location"));
    //     }
    // }

    // @Test
    // public void test206ResponseIncludesVariantHeadersIfValueMightDiffer() throws Exception {

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.addHeader("Accept-Encoding", "gzip");

    //     final Date now = new Date();
    //     final Date inOneHour = new Date(now.getTime() + 3600 * 1000L);
    //     originResponse.addHeader("Cache-Control", "max-age=3600");
    //     originResponse.addHeader("Expires", DateUtils.formatDate(inOneHour));
    //     originResponse.addHeader("Vary", "Accept-Encoding");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.addHeader("Cache-Control", "no-cache");
    //     req2.addHeader("Accept-Encoding", "gzip");
    //     final Date nextSecond = new Date(now.getTime() + 1000L);
    //     final Date inTwoHoursPlusASec = new Date(now.getTime() + 2 * 3600 * 1000L + 1000L);

    //     final ClassicHttpResponse originResponse2 = HttpTestUtils.make200Response();
    //     originResponse2.setHeader("Date", DateUtils.formatDate(nextSecond));
    //     originResponse2.setHeader("Cache-Control", "max-age=7200");
    //     originResponse2.setHeader("Expires", DateUtils.formatDate(inTwoHoursPlusASec));
    //     originResponse2.setHeader("Vary", "Accept-Encoding");

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");
    //     req3.addHeader("Range", "bytes=0-50");
    //     req3.addHeader("Accept-Encoding", "gzip");

    //     backendExpectsAnyRequest().andReturn(originResponse);
    //     backendExpectsAnyRequestAndReturn(originResponse2).times(1, 2);

    //     replayMocks();

    //     execute(req1);
    //     execute(req2);
    //     final ClassicHttpResponse result = execute(req3);

    //     verifyMocks();

    //     if (result.getCode() == HttpStatus.SC_PARTIAL_CONTENT) {
    //         Assert.assertNotNull(result.getFirstHeader("Expires"));
    //         Assert.assertNotNull(result.getFirstHeader("Cache-Control"));
    //         Assert.assertNotNull(result.getFirstHeader("Vary"));
    //     }
    // }

    // /*
    //  * "If the [206] response is the result of an If-Range request that used a
    //  * weak validator, the response MUST NOT include other entity-headers; this
    //  * prevents inconsistencies between cached entity-bodies and updated
    //  * headers."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.7
    //  */
    // @Test
    // public void test206ResponseToConditionalRangeRequestDoesNotIncludeOtherEntityHeaders() throws Exception {

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");

    //     final Date now = new Date();
    //     final Date oneHourAgo = new Date(now.getTime() - 3600 * 1000L);
    //     originResponse = HttpTestUtils.make200Response();
    //     originResponse.addHeader("Allow", "GET,HEAD");
    //     originResponse.addHeader("Cache-Control", "max-age=3600");
    //     originResponse.addHeader("Content-Language", "en");
    //     originResponse.addHeader("Content-Encoding", "x-coding");
    //     originResponse.addHeader("Content-MD5", "Q2hlY2sgSW50ZWdyaXR5IQ==");
    //     originResponse.addHeader("Content-Length", "128");
    //     originResponse.addHeader("Content-Type", "application/octet-stream");
    //     originResponse.addHeader("Last-Modified", DateUtils.formatDate(oneHourAgo));
    //     originResponse.addHeader("ETag", "W/\"weak-tag\"");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.addHeader("If-Range", "W/\"weak-tag\"");
    //     req2.addHeader("Range", "bytes=0-50");

    //     backendExpectsAnyRequest().andReturn(originResponse).times(1, 2);

    //     replayMocks();

    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);

    //     verifyMocks();

    //     if (result.getCode() == HttpStatus.SC_PARTIAL_CONTENT) {
    //         Assert.assertNull(result.getFirstHeader("Allow"));
    //         Assert.assertNull(result.getFirstHeader("Content-Encoding"));
    //         Assert.assertNull(result.getFirstHeader("Content-Language"));
    //         Assert.assertNull(result.getFirstHeader("Content-MD5"));
    //         Assert.assertNull(result.getFirstHeader("Last-Modified"));
    //     }
    // }

    // /*
    //  * "Otherwise, the [206] response MUST include all of the entity-headers
    //  * that would have been returned with a 200 (OK) response to the same
    //  * [If-Range] request."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.7
    //  */
    // @Test
    // public void test206ResponseToIfRangeWithStrongValidatorReturnsAllEntityHeaders() throws Exception {

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");

    //     final Date now = new Date();
    //     final Date oneHourAgo = new Date(now.getTime() - 3600 * 1000L);
    //     originResponse.addHeader("Allow", "GET,HEAD");
    //     originResponse.addHeader("Cache-Control", "max-age=3600");
    //     originResponse.addHeader("Content-Language", "en");
    //     originResponse.addHeader("Content-Encoding", "x-coding");
    //     originResponse.addHeader("Content-MD5", "Q2hlY2sgSW50ZWdyaXR5IQ==");
    //     originResponse.addHeader("Content-Length", "128");
    //     originResponse.addHeader("Content-Type", "application/octet-stream");
    //     originResponse.addHeader("Last-Modified", DateUtils.formatDate(oneHourAgo));
    //     originResponse.addHeader("ETag", "\"strong-tag\"");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.addHeader("If-Range", "\"strong-tag\"");
    //     req2.addHeader("Range", "bytes=0-50");

    //     backendExpectsAnyRequest().andReturn(originResponse).times(1, 2);

    //     replayMocks();

    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);

    //     verifyMocks();

    //     if (result.getCode() == HttpStatus.SC_PARTIAL_CONTENT) {
    //         Assert.assertEquals("GET,HEAD", result.getFirstHeader("Allow").getValue());
    //         Assert.assertEquals("max-age=3600", result.getFirstHeader("Cache-Control").getValue());
    //         Assert.assertEquals("en", result.getFirstHeader("Content-Language").getValue());
    //         Assert.assertEquals("x-coding", result.getFirstHeader("Content-Encoding").getValue());
    //         Assert.assertEquals("Q2hlY2sgSW50ZWdyaXR5IQ==", result.getFirstHeader("Content-MD5")
    //                 .getValue());
    //         Assert.assertEquals(originResponse.getFirstHeader("Last-Modified").getValue(), result
    //                 .getFirstHeader("Last-Modified").getValue());
    //     }
    // }

    // /*
    //  * "A cache MUST NOT combine a 206 response with other previously cached
    //  * content if the ETag or Last-Modified headers do not match exactly, see
    //  * 13.5.4."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.7
    //  */
    // @Test
    // public void test206ResponseIsNotCombinedWithPreviousContentIfETagDoesNotMatch() throws Exception {

    //     final Date now = new Date();

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Cache-Control", "max-age=3600");
    //     resp1.setHeader("ETag", "\"etag1\"");
    //     final byte[] bytes1 = new byte[128];
    //     for (int i = 0; i < bytes1.length; i++) {
    //         bytes1[i] = (byte) 1;
    //     }
    //     resp1.setEntity(new ByteArrayEntity(bytes1, null));

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Cache-Control", "no-cache");
    //     req2.setHeader("Range", "bytes=0-50");

    //     final Date inOneSecond = new Date(now.getTime() + 1000L);
    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT,
    //             "Partial Content");
    //     resp2.setHeader("Date", DateUtils.formatDate(inOneSecond));
    //     resp2.setHeader("Server", resp1.getFirstHeader("Server").getValue());
    //     resp2.setHeader("ETag", "\"etag2\"");
    //     resp2.setHeader("Content-Range", "bytes 0-50/128");
    //     final byte[] bytes2 = new byte[51];
    //     for (int i = 0; i < bytes2.length; i++) {
    //         bytes2[i] = (byte) 2;
    //     }
    //     resp2.setEntity(new ByteArrayEntity(bytes2, null));

    //     final Date inTwoSeconds = new Date(now.getTime() + 2000L);
    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp3 = HttpTestUtils.make200Response();
    //     resp3.setHeader("Date", DateUtils.formatDate(inTwoSeconds));
    //     resp3.setHeader("Cache-Control", "max-age=3600");
    //     resp3.setHeader("ETag", "\"etag2\"");
    //     final byte[] bytes3 = new byte[128];
    //     for (int i = 0; i < bytes3.length; i++) {
    //         bytes3[i] = (byte) 2;
    //     }
    //     resp3.setEntity(new ByteArrayEntity(bytes3, null));

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp1);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp2);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp3).times(0, 1);
    //     replayMocks();

    //     execute(req1);
    //     execute(req2);
    //     final ClassicHttpResponse result = execute(req3);

    //     verifyMocks();

    //     final InputStream i = result.getEntity().getContent();
    //     int b;
    //     boolean found1 = false;
    //     boolean found2 = false;
    //     while ((b = i.read()) != -1) {
    //         if (b == 1) {
    //             found1 = true;
    //         }
    //         if (b == 2) {
    //             found2 = true;
    //         }
    //     }
    //     i.close();
    //     Assert.assertFalse(found1 && found2); // mixture of content
    // }

    // @Test
    // public void test206ResponseIsNotCombinedWithPreviousContentIfLastModifiedDoesNotMatch() throws Exception {

    //     final Date now = new Date();

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     final Date oneHourAgo = new Date(now.getTime() - 3600L);
    //     resp1.setHeader("Cache-Control", "max-age=3600");
    //     resp1.setHeader("Last-Modified", DateUtils.formatDate(oneHourAgo));
    //     final byte[] bytes1 = new byte[128];
    //     for (int i = 0; i < bytes1.length; i++) {
    //         bytes1[i] = (byte) 1;
    //     }
    //     resp1.setEntity(new ByteArrayEntity(bytes1, null));

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Cache-Control", "no-cache");
    //     req2.setHeader("Range", "bytes=0-50");

    //     final Date inOneSecond = new Date(now.getTime() + 1000L);
    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT,
    //             "Partial Content");
    //     resp2.setHeader("Date", DateUtils.formatDate(inOneSecond));
    //     resp2.setHeader("Server", resp1.getFirstHeader("Server").getValue());
    //     resp2.setHeader("Last-Modified", DateUtils.formatDate(now));
    //     resp2.setHeader("Content-Range", "bytes 0-50/128");
    //     final byte[] bytes2 = new byte[51];
    //     for (int i = 0; i < bytes2.length; i++) {
    //         bytes2[i] = (byte) 2;
    //     }
    //     resp2.setEntity(new ByteArrayEntity(bytes2, null));

    //     final Date inTwoSeconds = new Date(now.getTime() + 2000L);
    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp3 = HttpTestUtils.make200Response();
    //     resp3.setHeader("Date", DateUtils.formatDate(inTwoSeconds));
    //     resp3.setHeader("Cache-Control", "max-age=3600");
    //     resp3.setHeader("ETag", "\"etag2\"");
    //     final byte[] bytes3 = new byte[128];
    //     for (int i = 0; i < bytes3.length; i++) {
    //         bytes3[i] = (byte) 2;
    //     }
    //     resp3.setEntity(new ByteArrayEntity(bytes3, null));

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp1);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp2);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp3).times(0, 1);
    //     replayMocks();

    //     execute(req1);
    //     execute(req2);
    //     final ClassicHttpResponse result = execute(req3);

    //     verifyMocks();

    //     final InputStream i = result.getEntity().getContent();
    //     int b;
    //     boolean found1 = false;
    //     boolean found2 = false;
    //     while ((b = i.read()) != -1) {
    //         if (b == 1) {
    //             found1 = true;
    //         }
    //         if (b == 2) {
    //             found2 = true;
    //         }
    //     }
    //     i.close();
    //     Assert.assertFalse(found1 && found2); // mixture of content
    // }

    // /*
    //  * "A cache that does not support the Range and Content-Range headers MUST
    //  * NOT cache 206 (Partial) responses."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.7
    //  */
    // @Test
    // public void test206ResponsesAreNotCachedIfTheCacheDoesNotSupportRangeAndContentRangeHeaders() throws Exception {

    //     if (!supportsRangeAndContentRangeHeaders(impl)) {
    //         emptyMockCacheExpectsNoPuts();

    //         request = new BasicClassicHttpRequest("GET", "/");
    //         request.addHeader("Range", "bytes=0-50");

    //         originResponse = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT,"Partial Content");
    //         originResponse.setHeader("Content-Range", "bytes 0-50/128");
    //         originResponse.setHeader("Cache-Control", "max-age=3600");
    //         final byte[] bytes = new byte[51];
    //         new Random().nextBytes(bytes);
    //         originResponse.setEntity(new ByteArrayEntity(bytes, null));

    //         EasyMock.expect(
    //                 mockExecChain.proceed(
    //                         EasyMock.isA(ClassicHttpRequest.class),
    //                         EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //         replayMocks();
    //         execute(request);
    //         verifyMocks();
    //     }
    // }

    // /*
    //  * "10.3.4 303 See Other ... The 303 response MUST NOT be cached, but the
    //  * response to the second (redirected) request might be cacheable."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.4
    //  */
    // @Test
    // public void test303ResponsesAreNotCached() throws Exception {
    //     emptyMockCacheExpectsNoPuts();

    //     request = new BasicClassicHttpRequest("GET", "/");

    //     originResponse = new BasicClassicHttpResponse(HttpStatus.SC_SEE_OTHER, "See Other");
    //     originResponse.setHeader("Date", DateUtils.formatDate(new Date()));
    //     originResponse.setHeader("Server", "MockServer/1.0");
    //     originResponse.setHeader("Cache-Control", "max-age=3600");
    //     originResponse.setHeader("Content-Type", "application/x-cachingclient-test");
    //     originResponse.setHeader("Location", "http://foo.example.com/other");
    //     originResponse.setEntity(HttpTestUtils.makeBody(entityLength));

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();
    //     execute(request);
    //     verifyMocks();
    // }

    // /*
    //  * "The 304 response MUST NOT contain a message-body, and thus is always
    //  * terminated by the first empty line after the header fields."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
    //  */
    // @Test
    // public void test304ResponseDoesNotContainABody() throws Exception {
    //     request.setHeader("If-None-Match", "\"etag\"");

    //     originResponse = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED,"Not Modified");
    //     originResponse.setHeader("Date", DateUtils.formatDate(new Date()));
    //     originResponse.setHeader("Server", "MockServer/1.0");
    //     originResponse.setHeader("Content-Length", "128");
    //     originResponse.setEntity(HttpTestUtils.makeBody(entityLength));

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();
    // }

    // /*
    //  * "The [304] response MUST include the following header fields: - Date,
    //  * unless its omission is required by section 14.18.1 [clockless origin
    //  * servers]."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
    //  */
    // @Test
    // public void test304ResponseWithDateHeaderForwardedFromOriginIncludesDateHeader() throws Exception {

    //     request.setHeader("If-None-Match", "\"etag\"");

    //     originResponse = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED,"Not Modified");
    //     originResponse.setHeader("Date", DateUtils.formatDate(new Date()));
    //     originResponse.setHeader("Server", "MockServer/1.0");
    //     originResponse.setHeader("ETag", "\"etag\"");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();
    //     Assert.assertNotNull(result.getFirstHeader("Date"));
    // }

    // @Test
    // public void test304ResponseGeneratedFromCacheIncludesDateHeader() throws Exception {

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     originResponse.setHeader("Cache-Control", "max-age=3600");
    //     originResponse.setHeader("ETag", "\"etag\"");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("If-None-Match", "\"etag\"");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse).times(1, 2);
    //     replayMocks();

    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);

    //     verifyMocks();
    //     if (result.getCode() == HttpStatus.SC_NOT_MODIFIED) {
    //         Assert.assertNotNull(result.getFirstHeader("Date"));
    //     }
    // }

    // /*
    //  * "The [304] response MUST include the following header fields: - ETag
    //  * and/or Content-Location, if the header would have been sent in a 200
    //  * response to the same request."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
    //  */
    // @Test
    // public void test304ResponseGeneratedFromCacheIncludesEtagIfOriginResponseDid() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     originResponse.setHeader("Cache-Control", "max-age=3600");
    //     originResponse.setHeader("ETag", "\"etag\"");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("If-None-Match", "\"etag\"");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse).times(1, 2);
    //     replayMocks();

    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);

    //     verifyMocks();
    //     if (result.getCode() == HttpStatus.SC_NOT_MODIFIED) {
    //         Assert.assertNotNull(result.getFirstHeader("ETag"));
    //     }
    // }

    // @Test
    // public void test304ResponseGeneratedFromCacheIncludesContentLocationIfOriginResponseDid() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     originResponse.setHeader("Cache-Control", "max-age=3600");
    //     originResponse.setHeader("Content-Location", "http://foo.example.com/other");
    //     originResponse.setHeader("ETag", "\"etag\"");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("If-None-Match", "\"etag\"");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse).times(1, 2);
    //     replayMocks();

    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);

    //     verifyMocks();
    //     if (result.getCode() == HttpStatus.SC_NOT_MODIFIED) {
    //         Assert.assertNotNull(result.getFirstHeader("Content-Location"));
    //     }
    // }

    // /*
    //  * "The [304] response MUST include the following header fields: ... -
    //  * Expires, Cache-Control, and/or Vary, if the field-value might differ from
    //  * that sent in any previous response for the same variant
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
    //  */
    // @Test
    // public void test304ResponseGeneratedFromCacheIncludesExpiresCacheControlAndOrVaryIfResponseMightDiffer() throws Exception {

    //     final Date now = new Date();
    //     final Date inTwoHours = new Date(now.getTime() + 2 * 3600 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("Accept-Encoding", "gzip");

    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag", "\"v1\"");
    //     resp1.setHeader("Cache-Control", "max-age=7200");
    //     resp1.setHeader("Expires", DateUtils.formatDate(inTwoHours));
    //     resp1.setHeader("Vary", "Accept-Encoding");
    //     resp1.setEntity(HttpTestUtils.makeBody(entityLength));

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("Accept-Encoding", "gzip");
    //     req1.setHeader("Cache-Control", "no-cache");

    //     final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();
    //     resp2.setHeader("ETag", "\"v2\"");
    //     resp2.setHeader("Cache-Control", "max-age=3600");
    //     resp2.setHeader("Expires", DateUtils.formatDate(inTwoHours));
    //     resp2.setHeader("Vary", "Accept-Encoding");
    //     resp2.setEntity(HttpTestUtils.makeBody(entityLength));

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");
    //     req3.setHeader("Accept-Encoding", "gzip");
    //     req3.setHeader("If-None-Match", "\"v2\"");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp1);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp2).times(1, 2);
    //     replayMocks();

    //     execute(req1);
    //     execute(req2);
    //     final ClassicHttpResponse result = execute(req3);

    //     verifyMocks();

    //     if (result.getCode() == HttpStatus.SC_NOT_MODIFIED) {
    //         Assert.assertNotNull(result.getFirstHeader("Expires"));
    //         Assert.assertNotNull(result.getFirstHeader("Cache-Control"));
    //         Assert.assertNotNull(result.getFirstHeader("Vary"));
    //     }
    // }

    // /*
    //  * "Otherwise (i.e., the conditional GET used a weak validator), the
    //  * response MUST NOT include other entity-headers; this prevents
    //  * inconsistencies between cached entity-bodies and updated headers."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
    //  */
    // @Test
    // public void test304GeneratedFromCacheOnWeakValidatorDoesNotIncludeOtherEntityHeaders() throws Exception {

    //     final Date now = new Date();
    //     final Date oneHourAgo = new Date(now.getTime() - 3600 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");

    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag", "W/\"v1\"");
    //     resp1.setHeader("Allow", "GET,HEAD");
    //     resp1.setHeader("Content-Encoding", "x-coding");
    //     resp1.setHeader("Content-Language", "en");
    //     resp1.setHeader("Content-Length", "128");
    //     resp1.setHeader("Content-MD5", "Q2hlY2sgSW50ZWdyaXR5IQ==");
    //     resp1.setHeader("Content-Type", "application/octet-stream");
    //     resp1.setHeader("Last-Modified", DateUtils.formatDate(oneHourAgo));
    //     resp1.setHeader("Cache-Control", "max-age=7200");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("If-None-Match", "W/\"v1\"");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp1).times(1, 2);
    //     replayMocks();

    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);

    //     verifyMocks();

    //     if (result.getCode() == HttpStatus.SC_NOT_MODIFIED) {
    //         Assert.assertNull(result.getFirstHeader("Allow"));
    //         Assert.assertNull(result.getFirstHeader("Content-Encoding"));
    //         Assert.assertNull(result.getFirstHeader("Content-Length"));
    //         Assert.assertNull(result.getFirstHeader("Content-MD5"));
    //         Assert.assertNull(result.getFirstHeader("Content-Type"));
    //         Assert.assertNull(result.getFirstHeader("Last-Modified"));
    //     }
    // }

    // /*
    //  * "If a 304 response indicates an entity not currently cached, then the
    //  * cache MUST disregard the response and repeat the request without the
    //  * conditional."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
    //  */
    // @Test
    // public void testNotModifiedOfNonCachedEntityShouldRevalidateWithUnconditionalGET() throws Exception {

    //     final Date now = new Date();

    //     // load cache with cacheable entry
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag", "\"etag1\"");
    //     resp1.setHeader("Cache-Control", "max-age=3600");

    //     // force a revalidation
    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Cache-Control", "max-age=0,max-stale=0");

    //     // updated ETag provided to a conditional revalidation
    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED,
    //             "Not Modified");
    //     resp2.setHeader("Date", DateUtils.formatDate(now));
    //     resp2.setHeader("Server", "MockServer/1.0");
    //     resp2.setHeader("ETag", "\"etag2\"");

    //     // conditional validation uses If-None-Match
    //     final ClassicHttpRequest conditionalValidation = new BasicClassicHttpRequest("GET", "/");
    //     conditionalValidation.setHeader("If-None-Match", "\"etag1\"");

    //     // unconditional validation doesn't use If-None-Match
    //     final ClassicHttpRequest unconditionalValidation = new BasicClassicHttpRequest("GET", "/");
    //     // new response to unconditional validation provides new body
    //     final ClassicHttpResponse resp3 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag", "\"etag2\"");
    //     resp1.setHeader("Cache-Control", "max-age=3600");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp1);
    //     // this next one will happen once if the cache tries to
    //     // conditionally validate, zero if it goes full revalidation
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     eqRequest(conditionalValidation),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp2).times(0, 1);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     eqRequest(unconditionalValidation),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp3);
    //     replayMocks();

    //     execute(req1);
    //     execute(req2);

    //     verifyMocks();
    // }

    // /*
    //  * "If a cache uses a received 304 response to processChallenge a cache entry, the
    //  * cache MUST processChallenge the entry to reflect any new field values given in the
    //  * response.
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
    //  */
    // @Test
    // public void testCacheEntryIsUpdatedWithNewFieldValuesIn304Response() throws Exception {

    //     final Date now = new Date();
    //     final Date inFiveSeconds = new Date(now.getTime() + 5000L);

    //     final ClassicHttpRequest initialRequest = new BasicClassicHttpRequest("GET", "/");

    //     final ClassicHttpResponse cachedResponse = HttpTestUtils.make200Response();
    //     cachedResponse.setHeader("Cache-Control", "max-age=3600");
    //     cachedResponse.setHeader("ETag", "\"etag\"");

    //     final ClassicHttpRequest secondRequest = new BasicClassicHttpRequest("GET", "/");
    //     secondRequest.setHeader("Cache-Control", "max-age=0,max-stale=0");

    //     final ClassicHttpRequest conditionalValidationRequest = new BasicClassicHttpRequest("GET", "/");
    //     conditionalValidationRequest.setHeader("If-None-Match", "\"etag\"");

    //     final ClassicHttpRequest unconditionalValidationRequest = new BasicClassicHttpRequest("GET", "/");

    //     // to be used if the cache generates a conditional validation
    //     final ClassicHttpResponse conditionalResponse = HttpTestUtils.make304Response();
    //     conditionalResponse.setHeader("Date", DateUtils.formatDate(inFiveSeconds));
    //     conditionalResponse.setHeader("Server", "MockUtils/1.0");
    //     conditionalResponse.setHeader("ETag", "\"etag\"");
    //     conditionalResponse.setHeader("X-Extra", "junk");

    //     // to be used if the cache generates an unconditional validation
    //     final ClassicHttpResponse unconditionalResponse = HttpTestUtils.make200Response();
    //     unconditionalResponse.setHeader("Date", DateUtils.formatDate(inFiveSeconds));
    //     unconditionalResponse.setHeader("ETag", "\"etag\"");

    //     final Capture<ClassicHttpRequest> cap1 = EasyMock.newCapture();
    //     final Capture<ClassicHttpRequest> cap2 = EasyMock.newCapture();

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(cachedResponse);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.and(eqRequest(conditionalValidationRequest), EasyMock.capture(cap1)),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(conditionalResponse).times(0, 1);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.and(eqRequest(unconditionalValidationRequest), EasyMock.capture(cap2)),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(unconditionalResponse).times(0, 1);

    //     replayMocks();

    //     execute(initialRequest);
    //     final ClassicHttpResponse result = execute(secondRequest);

    //     verifyMocks();

    //     Assert.assertTrue((cap1.hasCaptured() && !cap2.hasCaptured())
    //             || (!cap1.hasCaptured() && cap2.hasCaptured()));

    //     if (cap1.hasCaptured()) {
    //         Assert.assertEquals(DateUtils.formatDate(inFiveSeconds), result.getFirstHeader("Date")
    //                 .getValue());
    //         Assert.assertEquals("junk", result.getFirstHeader("X-Extra").getValue());
    //     }
    // }

    // /*
    //  * "10.4.2 401 Unauthorized ... The response MUST include a WWW-Authenticate
    //  * header field (section 14.47) containing a challenge applicable to the
    //  * requested resource."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2
    //  */
    // @Test
    // public void testMustIncludeWWWAuthenticateHeaderOnAnOrigin401Response() throws Exception {
    //     originResponse = new BasicClassicHttpResponse(401, "Unauthorized");
    //     originResponse.setHeader("WWW-Authenticate", "x-scheme x-param");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);
    //     if (result.getCode() == 401) {
    //         Assert.assertNotNull(result.getFirstHeader("WWW-Authenticate"));
    //     }

    //     verifyMocks();
    // }

    // /*
    //  * "10.4.6 405 Method Not Allowed ... The response MUST include an Allow
    //  * header containing a list of valid methods for the requested resource.
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2
    //  */
    // @Test
    // public void testMustIncludeAllowHeaderFromAnOrigin405Response() throws Exception {
    //     originResponse = new BasicClassicHttpResponse(405, "Method Not Allowed");
    //     originResponse.setHeader("Allow", "GET, HEAD");

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);
    //     if (result.getCode() == 405) {
    //         Assert.assertNotNull(result.getFirstHeader("Allow"));
    //     }

    //     verifyMocks();
    // }

    // /*
    //  * "10.4.8 407 Proxy Authentication Required ... The proxy MUST return a
    //  * Proxy-Authenticate header field (section 14.33) containing a challenge
    //  * applicable to the proxy for the requested resource."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.8
    //  */
    // @Test
    // public void testMustIncludeProxyAuthenticateHeaderFromAnOrigin407Response() throws Exception {
    //     originResponse = new BasicClassicHttpResponse(407, "Proxy Authentication Required");
    //     originResponse.setHeader("Proxy-Authenticate", "x-scheme x-param");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);
    //     if (result.getCode() == 407) {
    //         Assert.assertNotNull(result.getFirstHeader("Proxy-Authenticate"));
    //     }

    //     verifyMocks();
    // }

    // /*
    //  * "10.4.17 416 Requested Range Not Satisfiable ... This response MUST NOT
    //  * use the multipart/byteranges content-type."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.17
    //  */
    // @Test
    // public void testMustNotAddMultipartByteRangeContentTypeTo416Response() throws Exception {
    //     originResponse = new BasicClassicHttpResponse(416, "Requested Range Not Satisfiable");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();

    //     if (result.getCode() == 416) {
    //         final Iterator<HeaderElement> it = MessageSupport.iterate(result, HttpHeaders.CONTENT_TYPE);
    //         while (it.hasNext()) {
    //             final HeaderElement elt = it.next();
    //             Assert.assertFalse("multipart/byteranges".equalsIgnoreCase(elt.getName()));
    //         }
    //     }
    // }

    // @Test
    // public void testMustNotUseMultipartByteRangeContentTypeOnCacheGenerated416Responses() throws Exception {

    //     originResponse.setEntity(HttpTestUtils.makeBody(entityLength));
    //     originResponse.setHeader("Content-Length", "128");
    //     originResponse.setHeader("Cache-Control", "max-age=3600");

    //     final ClassicHttpRequest rangeReq = new BasicClassicHttpRequest("GET", "/");
    //     rangeReq.setHeader("Range", "bytes=1000-1200");

    //     final ClassicHttpResponse orig416 = new BasicClassicHttpResponse(416,
    //             "Requested Range Not Satisfiable");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);
    //     // cache may 416 me right away if it understands byte ranges,
    //     // ok to delegate to origin though
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(orig416).times(0, 1);

    //     replayMocks();
    //     execute(request);
    //     final ClassicHttpResponse result = execute(rangeReq);
    //     verifyMocks();

    //     // might have gotten a 416 from the origin or the cache
    //     if (result.getCode() == 416) {
    //         final Iterator<HeaderElement> it = MessageSupport.iterate(result, HttpHeaders.CONTENT_TYPE);
    //         while (it.hasNext()) {
    //             final HeaderElement elt = it.next();
    //             Assert.assertFalse("multipart/byteranges".equalsIgnoreCase(elt.getName()));
    //         }
    //     }
    // }

    // /*
    //  * "A correct cache MUST respond to a request with the most up-to-date
    //  * response held by the cache that is appropriate to the request (see
    //  * sections 13.2.5, 13.2.6, and 13.12) which meets one of the following
    //  * conditions:
    //  *
    //  * 1. It has been checked for equivalence with what the origin server would
    //  * have returned by revalidating the response with the origin server
    //  * (section 13.3);
    //  *
    //  * 2. It is "fresh enough" (see section 13.2). In the default case, this
    //  * means it meets the least restrictive freshness requirement of the client,
    //  * origin server, and cache (see section 14.9); if the origin server so
    //  * specifies, it is the freshness requirement of the origin server alone.
    //  *
    //  * If a stored response is not "fresh enough" by the most restrictive
    //  * freshness requirement of both the client and the origin server, in
    //  * carefully considered circumstances the cache MAY still return the
    //  * response with the appropriate Warning header (see section 13.1.5 and
    //  * 14.46), unless such a response is prohibited (e.g., by a "no-store"
    //  * cache-directive, or by a "no-cache" cache-request-directive; see section
    //  * 14.9).
    //  *
    //  * 3. It is an appropriate 304 (Not Modified), 305 (Proxy Redirect), or
    //  * error (4xx or 5xx) response message."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.1.1
    //  */
    // @Test
    // public void testMustReturnACacheEntryIfItCanRevalidateIt() throws Exception {

    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     final Date nineSecondsAgo = new Date(now.getTime() - 9 * 1000L);
    //     final Date eightSecondsAgo = new Date(now.getTime() - 8 * 1000L);

    //     final Header[] hdrs = new Header[] {
    //             new BasicHeader("Date", DateUtils.formatDate(nineSecondsAgo)),
    //             new BasicHeader("Cache-Control", "max-age=0"),
    //             new BasicHeader("ETag", "\"etag\""),
    //             new BasicHeader("Content-Length", "128")
    //     };

    //     final byte[] bytes = new byte[128];
    //     new Random().nextBytes(bytes);

    //     final HttpCacheEntry entry = HttpTestUtils.makeCacheEntry(tenSecondsAgo, eightSecondsAgo, hdrs, bytes);

    //     impl = new CachingExec(mockCache, null, config);

    //     request = new BasicClassicHttpRequest("GET", "/thing");

    //     final ClassicHttpRequest validate = new BasicClassicHttpRequest("GET", "/thing");
    //     validate.setHeader("If-None-Match", "\"etag\"");

    //     final ClassicHttpResponse notModified = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED, "Not Modified");
    //     notModified.setHeader("Date", DateUtils.formatDate(now));
    //     notModified.setHeader("ETag", "\"etag\"");

    //     EasyMock.expect(
    //             mockCache.getCacheEntry(EasyMock.eq(host), eqRequest(request)))
    //             .andReturn(entry);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     eqRequest(validate),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(notModified);
    //     EasyMock.expect(mockCache.updateCacheEntry(
    //             EasyMock.eq(host),
    //             eqRequest(request),
    //             EasyMock.eq(entry),
    //             eqResponse(notModified),
    //             EasyMock.isA(Date.class),
    //             EasyMock.isA(Date.class)))
    //         .andReturn(HttpTestUtils.makeCacheEntry());

    //     replayMocks();
    //     execute(request);
    //     verifyMocks();
    // }

    // @Test
    // public void testMustReturnAFreshEnoughCacheEntryIfItHasIt() throws Exception {

    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     final Date nineSecondsAgo = new Date(now.getTime() - 9 * 1000L);
    //     final Date eightSecondsAgo = new Date(now.getTime() - 8 * 1000L);

    //     final Header[] hdrs = new Header[] {
    //             new BasicHeader("Date", DateUtils.formatDate(nineSecondsAgo)),
    //             new BasicHeader("Cache-Control", "max-age=3600"),
    //             new BasicHeader("Content-Length", "128")
    //     };

    //     final byte[] bytes = new byte[128];
    //     new Random().nextBytes(bytes);

    //     final HttpCacheEntry entry = HttpTestUtils.makeCacheEntry(tenSecondsAgo, eightSecondsAgo, hdrs, bytes);

    //     impl = new CachingExec(mockCache, null, config);
    //     request = new BasicClassicHttpRequest("GET", "/thing");

    //     EasyMock.expect(mockCache.getCacheEntry(EasyMock.eq(host), eqRequest(request))).andReturn(entry);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();

    //     Assert.assertEquals(200, result.getCode());
    // }

    // /*
    //  * "If the cache can not communicate with the origin server, then a correct
    //  * cache SHOULD respond as above if the response can be correctly served
    //  * from the cache; if not it MUST return an error or warning indicating that
    //  * there was a communication failure."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.1.1
    //  *
    //  * "111 Revalidation failed MUST be included if a cache returns a stale
    //  * response because an attempt to revalidate the response failed, due to an
    //  * inability to reach the server."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.46
    //  */
    // @Test
    // public void testMustServeAppropriateErrorOrWarningIfNoOriginCommunicationPossible() throws Exception {

    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     final Date nineSecondsAgo = new Date(now.getTime() - 9 * 1000L);
    //     final Date eightSecondsAgo = new Date(now.getTime() - 8 * 1000L);

    //     final Header[] hdrs = new Header[] {
    //             new BasicHeader("Date", DateUtils.formatDate(nineSecondsAgo)),
    //             new BasicHeader("Cache-Control", "max-age=0"),
    //             new BasicHeader("Content-Length", "128"),
    //             new BasicHeader("Last-Modified", DateUtils.formatDate(tenSecondsAgo))
    //     };

    //     final byte[] bytes = new byte[128];
    //     new Random().nextBytes(bytes);

    //     final HttpCacheEntry entry = HttpTestUtils.makeCacheEntry(tenSecondsAgo, eightSecondsAgo, hdrs, bytes);

    //     impl = new CachingExec(mockCache, null, config);
    //     request = new BasicClassicHttpRequest("GET", "/thing");

    //     EasyMock.expect(mockCache.getCacheEntry(EasyMock.eq(host), eqRequest(request))).andReturn(entry);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andThrow(
    //             new IOException("can't talk to origin!")).anyTimes();

    //     replayMocks();

    //     final ClassicHttpResponse result = execute(request);

    //     verifyMocks();

    //     final int status = result.getCode();
    //     if (status == 200) {
    //         boolean foundWarning = false;
    //         for (final Header h : result.getHeaders("Warning")) {
    //             if (h.getValue().split(" ")[0].equals("111")) {
    //                 foundWarning = true;
    //             }
    //         }
    //         Assert.assertTrue(foundWarning);
    //     } else {
    //         Assert.assertTrue(status >= 500 && status <= 599);
    //     }
    // }

    // /*
    //  * "Whenever a cache returns a response that is neither first-hand nor
    //  * "fresh enough" (in the sense of condition 2 in section 13.1.1), it MUST
    //  * attach a warning to that effect, using a Warning general-header."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.1.2
    //  */
    // @Test
    // public void testAttachesWarningHeaderWhenGeneratingStaleResponse() throws Exception {
    //     // covered by previous test
    // }

    // /*
    //  * "1xx Warnings that describe the freshness or revalidation status of the
    //  * response, and so MUST be deleted after a successful revalidation."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.1.2
    //  */
    // @Test
    // public void test1xxWarningsAreDeletedAfterSuccessfulRevalidation() throws Exception {

    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 25 * 1000L);
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Date", DateUtils.formatDate(tenSecondsAgo));
    //     resp1.setHeader("ETag", "\"etag\"");
    //     resp1.setHeader("Cache-Control", "max-age=5");
    //     resp1.setHeader("Warning", "110 squid \"stale stuff\"");
    //     resp1.setHeader("Via", "1.1 fred");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");

    //     final ClassicHttpRequest validate = new BasicClassicHttpRequest("GET", "/");
    //     validate.setHeader("If-None-Match", "\"etag\"");

    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED,
    //             "Not Modified");
    //     resp2.setHeader("Date", DateUtils.formatDate(now));
    //     resp2.setHeader("Server", "MockServer/1.0");
    //     resp2.setHeader("ETag", "\"etag\"");
    //     resp2.setHeader("Via", "1.1 fred");

    //     backendExpectsAnyRequestAndReturn(resp1);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     eqRequest(validate),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp2);

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");

    //     replayMocks();

    //     final ClassicHttpResponse stale = execute(req1);
    //     Assert.assertNotNull(stale.getFirstHeader("Warning"));

    //     final ClassicHttpResponse result1 = execute(req2);
    //     final ClassicHttpResponse result2 = execute(req3);

    //     verifyMocks();

    //     boolean found1xxWarning = false;
    //     final Iterator<HeaderElement> it = MessageSupport.iterate(result1, HttpHeaders.WARNING);
    //     while (it.hasNext()) {
    //         final HeaderElement elt = it.next();
    //         if (elt.getName().startsWith("1")) {
    //             found1xxWarning = true;
    //         }
    //     }
    //     final Iterator<HeaderElement> it2 = MessageSupport.iterate(result2, HttpHeaders.WARNING);
    //     while (it2.hasNext()) {
    //         final HeaderElement elt = it2.next();
    //         if (elt.getName().startsWith("1")) {
    //             found1xxWarning = true;
    //         }
    //     }
    //     Assert.assertFalse(found1xxWarning);
    // }

    // /*
    //  * "2xx Warnings that describe some aspect of the entity body or entity
    //  * headers that is not rectified by a revalidation (for example, a lossy
    //  * compression of the entity bodies) and which MUST NOT be deleted after a
    //  * successful revalidation."
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.1.2
    //  */
    // @Test
    // public void test2xxWarningsAreNotDeletedAfterSuccessfulRevalidation() throws Exception {
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Date", DateUtils.formatDate(tenSecondsAgo));
    //     resp1.setHeader("ETag", "\"etag\"");
    //     resp1.setHeader("Cache-Control", "max-age=5");
    //     resp1.setHeader("Via", "1.1 xproxy");
    //     resp1.setHeader("Warning", "214 xproxy \"transformed stuff\"");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");

    //     final ClassicHttpRequest validate = new BasicClassicHttpRequest("GET", "/");
    //     validate.setHeader("If-None-Match", "\"etag\"");

    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED,
    //             "Not Modified");
    //     resp2.setHeader("Date", DateUtils.formatDate(now));
    //     resp2.setHeader("Server", "MockServer/1.0");
    //     resp2.setHeader("ETag", "\"etag\"");
    //     resp1.setHeader("Via", "1.1 xproxy");

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     eqRequest(validate),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp2);

    //     replayMocks();

    //     final ClassicHttpResponse stale = execute(req1);
    //     Assert.assertNotNull(stale.getFirstHeader("Warning"));

    //     final ClassicHttpResponse result1 = execute(req2);
    //     final ClassicHttpResponse result2 = execute(req3);

    //     verifyMocks();

    //     boolean found214Warning = false;
    //     final Iterator<HeaderElement> it = MessageSupport.iterate(result1, HttpHeaders.WARNING);
    //     while (it.hasNext()) {
    //         final HeaderElement elt = it.next();
    //         final String[] parts = elt.getName().split(" ");
    //         if ("214".equals(parts[0])) {
    //             found214Warning = true;
    //         }
    //     }
    //     Assert.assertTrue(found214Warning);

    //     found214Warning = false;
    //     final Iterator<HeaderElement> it2 = MessageSupport.iterate(result2, HttpHeaders.WARNING);
    //     while (it2.hasNext()) {
    //         final HeaderElement elt = it2.next();
    //         final String[] parts = elt.getName().split(" ");
    //         if ("214".equals(parts[0])) {
    //             found214Warning = true;
    //         }
    //     }
    //     Assert.assertTrue(found214Warning);
    // }

    // /*
    //  * "When a response is generated from a cache entry, the cache MUST include
    //  * a single Age header field in the response with a value equal to the cache
    //  * entry's current_age."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.2.3
    //  */
    // @Test
    // public void testAgeHeaderPopulatedFromCacheEntryCurrentAge() throws Exception {

    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     final Date nineSecondsAgo = new Date(now.getTime() - 9 * 1000L);
    //     final Date eightSecondsAgo = new Date(now.getTime() - 8 * 1000L);

    //     final Header[] hdrs = new Header[] {
    //             new BasicHeader("Date", DateUtils.formatDate(nineSecondsAgo)),
    //             new BasicHeader("Cache-Control", "max-age=3600"),
    //             new BasicHeader("Content-Length", "128")
    //     };

    //     final byte[] bytes = new byte[128];
    //     new Random().nextBytes(bytes);

    //     final HttpCacheEntry entry = HttpTestUtils.makeCacheEntry(tenSecondsAgo, eightSecondsAgo, hdrs, bytes);

    //     impl = new CachingExec(mockCache, null, config);
    //     request = new BasicClassicHttpRequest("GET", "/thing");

    //     EasyMock.expect(mockCache.getCacheEntry(EasyMock.eq(host), eqRequest(request))).andReturn(entry);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();

    //     Assert.assertEquals(200, result.getCode());
    //     Assert.assertEquals("11", result.getFirstHeader("Age").getValue());
    // }

    // /*
    //  * "If none of Expires, Cache-Control: max-age, or Cache-Control: s-maxage
    //  * (see section 14.9.3) appears in the response, and the response does not
    //  * include other restrictions on caching, the cache MAY compute a freshness
    //  * lifetime using a heuristic. The cache MUST attach Warning 113 to any
    //  * response whose age is more than 24 hours if such warning has not already
    //  * been added."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.2.4
    //  *
    //  * "113 Heuristic expiration MUST be included if the cache heuristically
    //  * chose a freshness lifetime greater than 24 hours and the response's age
    //  * is greater than 24 hours."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.46
    //  */
    // @Test
    // public void testHeuristicCacheOlderThan24HoursHasWarningAttached() throws Exception {

    //     final Date now = new Date();
    //     final Date thirtySixHoursAgo = new Date(now.getTime() - 36 * 3600 * 1000L);
    //     final Date oneYearAgo = new Date(now.getTime() - 365 * 24 * 3600 * 1000L);
    //     final Date requestTime = new Date(thirtySixHoursAgo.getTime() - 1000L);
    //     final Date responseTime = new Date(thirtySixHoursAgo.getTime() + 1000L);

    //     final Header[] hdrs = new Header[] {
    //             new BasicHeader("Date", DateUtils.formatDate(thirtySixHoursAgo)),
    //             new BasicHeader("Cache-Control", "public"),
    //             new BasicHeader("Last-Modified", DateUtils.formatDate(oneYearAgo)),
    //             new BasicHeader("Content-Length", "128")
    //     };

    //     final byte[] bytes = new byte[128];
    //     new Random().nextBytes(bytes);

    //     final HttpCacheEntry entry = HttpTestUtils.makeCacheEntry(requestTime, responseTime, hdrs, bytes);

    //     impl = new CachingExec(mockCache, null, config);

    //     request = new BasicClassicHttpRequest("GET", "/thing");

    //     final ClassicHttpResponse validated = HttpTestUtils.make200Response();
    //     validated.setHeader("Cache-Control", "public");
    //     validated.setHeader("Last-Modified", DateUtils.formatDate(oneYearAgo));
    //     validated.setHeader("Content-Length", "128");
    //     validated.setEntity(new ByteArrayEntity(bytes, null));

    //     final HttpCacheEntry cacheEntry = HttpTestUtils.makeCacheEntry();

    //     final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();

    //     mockCache.flushCacheEntriesInvalidatedByExchange(
    //             EasyMock.isA(HttpHost.class),
    //             EasyMock.isA(ClassicHttpRequest.class),
    //             EasyMock.isA(ClassicHttpResponse.class));
    //     EasyMock.expect(mockCache.getCacheEntry(EasyMock.eq(host), eqRequest(request))).andReturn(entry);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.capture(cap),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(validated).times(0, 1);
    //     EasyMock.expect(mockCache.getCacheEntry(
    //             EasyMock.isA(HttpHost.class),
    //             EasyMock.isA(ClassicHttpRequest.class))).andReturn(entry).times(0, 1);
    //     EasyMock.expect(mockCache.createCacheEntry(
    //             EasyMock.isA(HttpHost.class),
    //             EasyMock.isA(ClassicHttpRequest.class),
    //             eqCloseableResponse(validated),
    //             EasyMock.isA(ByteArrayBuffer.class),
    //             EasyMock.isA(Date.class),
    //             EasyMock.isA(Date.class))).andReturn(cacheEntry).times(0, 1);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();

    //     Assert.assertEquals(200, result.getCode());
    //     if (!cap.hasCaptured()) {
    //         // heuristic cache hit
    //         boolean found113Warning = false;
    //         final Iterator<HeaderElement> it = MessageSupport.iterate(result, HttpHeaders.WARNING);
    //         while (it.hasNext()) {
    //             final HeaderElement elt = it.next();
    //             final String[] parts = elt.getName().split(" ");
    //             if ("113".equals(parts[0])) {
    //                 found113Warning = true;
    //                 break;
    //             }
    //         }
    //         Assert.assertTrue(found113Warning);
    //     }
    // }

    // /*
    //  * "If a cache has two fresh responses for the same representation with
    //  * different validators, it MUST use the one with the more recent Date
    //  * header."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.2.5
    //  */
    // @Test
    // public void testKeepsMostRecentDateHeaderForFreshResponse() throws Exception {

    //     final Date now = new Date();
    //     final Date inFiveSecond = new Date(now.getTime() + 5 * 1000L);

    //     // put an entry in the cache
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");

    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Date", DateUtils.formatDate(inFiveSecond));
    //     resp1.setHeader("ETag", "\"etag1\"");
    //     resp1.setHeader("Cache-Control", "max-age=3600");
    //     resp1.setHeader("Content-Length", "128");

    //     // force another origin hit
    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Cache-Control", "no-cache");

    //     final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();
    //     resp2.setHeader("Date", DateUtils.formatDate(now)); // older
    //     resp2.setHeader("ETag", "\"etag2\"");
    //     resp2.setHeader("Cache-Control", "max-age=3600");
    //     resp2.setHeader("Content-Length", "128");

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     eqRequest(req1),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp1);

    //     backendExpectsAnyRequestAndReturn(resp2);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     final ClassicHttpResponse result = execute(req3);
    //     verifyMocks();
    //     Assert.assertEquals("\"etag1\"", result.getFirstHeader("ETag").getValue());
    // }

    /*
     * "Clients MAY issue simple (non-subrange) GET requests with either weak
     * validators or strong validators. Clients MUST NOT use weak validators in
     * other forms of request."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.3.3
     *
     * Note that we can't determine a priori whether a given HTTP-date is a weak
     * or strong validator, because that might depend on an upstream client
     * having a cache with a Last-Modified and Date entry that allows the date
     * to be a strong validator. We can tell when *we* are generating a request
     * for validation, but we can't tell if we receive a conditional request
     * from upstream.
     */
    private ClassicHttpResponse testRequestWithWeakETagValidatorIsNotAllowed(final String header) throws Exception {
        final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();
        EasyMock.expect(
                mockExecChain.proceed(
                        EasyMock.capture(cap),
                        EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse).times(0, 1);

        replayMocks();
        final ClassicHttpResponse response = execute(request);
        verifyMocks();

        // it's probably ok to return a 400 (Bad Request) to this client
        if (cap.hasCaptured()) {
            final ClassicHttpRequest forwarded = cap.getValue();
            final Header h = forwarded.getFirstHeader(header);
            if (h != null) {
                Assert.assertFalse(h.getValue().startsWith("W/"));
            }
        }
        return response;

    }

    // @Test
    // public void testSubrangeGETWithWeakETagIsNotAllowed() throws Exception {
    //     request = new BasicClassicHttpRequest("GET", "/");
    //     request.setHeader("Range", "bytes=0-500");
    //     request.setHeader("If-Range", "W/\"etag\"");

    //     final ClassicHttpResponse response = testRequestWithWeakETagValidatorIsNotAllowed("If-Range");
    //     Assert.assertTrue(response.getCode() == HttpStatus.SC_BAD_REQUEST);
    // }

    // @Test
    // public void testPUTWithIfMatchWeakETagIsNotAllowed() throws Exception {
    //     final ClassicHttpRequest put = new BasicClassicHttpRequest("PUT", "/");
    //     put.setEntity(HttpTestUtils.makeBody(128));
    //     put.setHeader("Content-Length", "128");
    //     put.setHeader("If-Match", "W/\"etag\"");
    //     request = put;

    //     testRequestWithWeakETagValidatorIsNotAllowed("If-Match");
    // }

    // @Test
    // public void testPUTWithIfNoneMatchWeakETagIsNotAllowed() throws Exception {
    //     final ClassicHttpRequest put = new BasicClassicHttpRequest("PUT", "/");
    //     put.setEntity(HttpTestUtils.makeBody(128));
    //     put.setHeader("Content-Length", "128");
    //     put.setHeader("If-None-Match", "W/\"etag\"");
    //     request = put;

    //     testRequestWithWeakETagValidatorIsNotAllowed("If-None-Match");
    // }

    // @Test
    // public void testDELETEWithIfMatchWeakETagIsNotAllowed() throws Exception {
    //     request = new BasicClassicHttpRequest("DELETE", "/");
    //     request.setHeader("If-Match", "W/\"etag\"");

    //     testRequestWithWeakETagValidatorIsNotAllowed("If-Match");
    // }

    // @Test
    // public void testDELETEWithIfNoneMatchWeakETagIsNotAllowed() throws Exception {
    //     request = new BasicClassicHttpRequest("DELETE", "/");
    //     request.setHeader("If-None-Match", "W/\"etag\"");

    //     testRequestWithWeakETagValidatorIsNotAllowed("If-None-Match");
    // }

    // /*
    //  * "A cache or origin server receiving a conditional request, other than a
    //  * full-body GET request, MUST use the strong comparison function to
    //  * evaluate the condition."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.3.3
    //  */
    // @Test
    // public void testSubrangeGETMustUseStrongComparisonForCachedResponse() throws Exception {
    //     final Date now = new Date();
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Date", DateUtils.formatDate(now));
    //     resp1.setHeader("Cache-Control", "max-age=3600");
    //     resp1.setHeader("ETag", "\"etag\"");

    //     // according to weak comparison, this would match. Strong
    //     // comparison doesn't, because the cache entry's ETag is not
    //     // marked weak. Therefore, the If-Range must fail and we must
    //     // either get an error back or the full entity, but we better
    //     // not get the conditionally-requested Partial Content (206).
    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range", "bytes=0-50");
    //     req2.setHeader("If-Range", "W/\"etag\"");

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp1).times(1, 2);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     Assert.assertFalse(HttpStatus.SC_PARTIAL_CONTENT == result.getCode());
    // }

    // /*
    //  * "HTTP/1.1 clients: - If an entity tag has been provided by the origin
    //  * server, MUST use that entity tag in any cache-conditional request (using
    //  * If- Match or If-None-Match)."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.3.4
    //  */
    // @Test
    // public void testValidationMustUseETagIfProvidedByOriginServer() throws Exception {

    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Date", DateUtils.formatDate(now));
    //     resp1.setHeader("Cache-Control", "max-age=3600");
    //     resp1.setHeader("Last-Modified", DateUtils.formatDate(tenSecondsAgo));
    //     resp1.setHeader("ETag", "W/\"etag\"");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Cache-Control", "max-age=0,max-stale=0");

    //     final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp1);

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.capture(cap),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp1);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     verifyMocks();

    //     final ClassicHttpRequest validation = cap.getValue();
    //     boolean isConditional = false;
    //     final String[] conditionalHeaders = { "If-Range", "If-Modified-Since", "If-Unmodified-Since",
    //             "If-Match", "If-None-Match" };

    //     for (final String ch : conditionalHeaders) {
    //         if (validation.getFirstHeader(ch) != null) {
    //             isConditional = true;
    //             break;
    //         }
    //     }

    //     if (isConditional) {
    //         boolean foundETag = false;
    //         final Iterator<HeaderElement> it = MessageSupport.iterate(validation, HttpHeaders.IF_MATCH);
    //         while (it.hasNext()) {
    //             final HeaderElement elt = it.next();
    //             if ("W/\"etag\"".equals(elt.getName())) {
    //                 foundETag = true;
    //             }
    //         }
    //         final Iterator<HeaderElement> it2 = MessageSupport.iterate(validation, HttpHeaders.IF_NONE_MATCH);
    //         while (it2.hasNext()) {
    //             final HeaderElement elt = it2.next();
    //             if ("W/\"etag\"".equals(elt.getName())) {
    //                 foundETag = true;
    //             }
    //         }
    //         Assert.assertTrue(foundETag);
    //     }
    // }

    // /*
    //  * "An HTTP/1.1 caching proxy, upon receiving a conditional request that
    //  * includes both a Last-Modified date and one or more entity tags as cache
    //  * validators, MUST NOT return a locally cached response to the client
    //  * unless that cached response is consistent with all of the conditional
    //  * header fields in the request."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.3.4
    //  */
    // @Test
    // public void testConditionalRequestWhereNotAllValidatorsMatchCannotBeServedFromCache() throws Exception {
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     final Date twentySecondsAgo = new Date(now.getTime() - 20 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Date", DateUtils.formatDate(now));
    //     resp1.setHeader("Cache-Control", "max-age=3600");
    //     resp1.setHeader("Last-Modified", DateUtils.formatDate(tenSecondsAgo));
    //     resp1.setHeader("ETag", "W/\"etag\"");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("If-None-Match", "W/\"etag\"");
    //     req2.setHeader("If-Modified-Since", DateUtils.formatDate(twentySecondsAgo));

    //     // must hit the origin again for the second request
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp1).times(2);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     Assert.assertFalse(HttpStatus.SC_NOT_MODIFIED == result.getCode());
    // }

    // @Test
    // public void testConditionalRequestWhereAllValidatorsMatchMayBeServedFromCache() throws Exception {
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Date", DateUtils.formatDate(now));
    //     resp1.setHeader("Cache-Control", "max-age=3600");
    //     resp1.setHeader("Last-Modified", DateUtils.formatDate(tenSecondsAgo));
    //     resp1.setHeader("ETag", "W/\"etag\"");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("If-None-Match", "W/\"etag\"");
    //     req2.setHeader("If-Modified-Since", DateUtils.formatDate(tenSecondsAgo));

    //     // may hit the origin again for the second request
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.isA(ClassicHttpRequest.class),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp1).times(1,2);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     verifyMocks();
    // }


    // /*
    //  * "However, a cache that does not support the Range and Content-Range
    //  * headers MUST NOT cache 206 (Partial Content) responses."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.4
    //  */
    // @Test
    // public void testCacheWithoutSupportForRangeAndContentRangeHeadersDoesNotCacheA206Response() throws Exception {

    //     if (!supportsRangeAndContentRangeHeaders(impl)) {
    //         emptyMockCacheExpectsNoPuts();

    //         final ClassicHttpRequest req = new BasicClassicHttpRequest("GET", "/");
    //         req.setHeader("Range", "bytes=0-50");

    //         final ClassicHttpResponse resp = new BasicClassicHttpResponse(206, "Partial Content");
    //         resp.setHeader("Content-Range", "bytes 0-50/128");
    //         resp.setHeader("ETag", "\"etag\"");
    //         resp.setHeader("Cache-Control", "max-age=3600");

    //         EasyMock.expect(mockExecChain.proceed(
    //                 EasyMock.isA(ClassicHttpRequest.class),
    //                 EasyMock.isA(ExecChain.Scope.class))).andReturn(resp);

    //         replayMocks();
    //         execute(req);
    //         verifyMocks();
    //     }
    // }

    // /*
    //  * "A response received with any other status code (e.g. status codes 302
    //  * and 307) MUST NOT be returned in a reply to a subsequent request unless
    //  * there are cache-control directives or another header(s) that explicitly
    //  * allow it. For example, these include the following: an Expires header
    //  * (section 14.21); a 'max-age', 's-maxage', 'must-revalidate',
    //  * 'proxy-revalidate', 'public' or 'private' cache-control directive
    //  * (section 14.9)."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.4
    //  */
    // @Test
    // public void test302ResponseWithoutExplicitCacheabilityIsNotReturnedFromCache() throws Exception {
    //     originResponse = new BasicClassicHttpResponse(302, "Temporary Redirect");
    //     originResponse.setHeader("Location", "http://foo.example.com/other");
    //     originResponse.removeHeaders("Expires");
    //     originResponse.removeHeaders("Cache-Control");

    //     backendExpectsAnyRequest().andReturn(originResponse).times(2);

    //     replayMocks();
    //     execute(request);
    //     execute(request);
    //     verifyMocks();
    // }

    /*
     * "A transparent proxy MUST NOT modify any of the following fields in a
     * request or response, and it MUST NOT add any of these fields if not
     * already present: - Content-Location - Content-MD5 - ETag - Last-Modified
     */
    private void testDoesNotModifyHeaderFromOrigin(final String header, final String value) throws Exception {
        originResponse = HttpTestUtils.make200Response();
        originResponse.setHeader(header, value);

        backendExpectsAnyRequest().andReturn(originResponse);

        replayMocks();
        final ClassicHttpResponse result = execute(request);
        verifyMocks();

        Assert.assertEquals(value, result.getFirstHeader(header).getValue());
    }

    // @Test
    // public void testDoesNotModifyContentLocationHeaderFromOrigin() throws Exception {

    //     final String url = "http://foo.example.com/other";
    //     testDoesNotModifyHeaderFromOrigin("Content-Location", url);
    // }

    // @Test
    // public void testDoesNotModifyContentMD5HeaderFromOrigin() throws Exception {
    //     testDoesNotModifyHeaderFromOrigin("Content-MD5", "Q2hlY2sgSW50ZWdyaXR5IQ==");
    // }

    // @Test
    // public void testDoesNotModifyEtagHeaderFromOrigin() throws Exception {
    //     testDoesNotModifyHeaderFromOrigin("Etag", "\"the-etag\"");
    // }

    // @Test
    // public void testDoesNotModifyLastModifiedHeaderFromOrigin() throws Exception {
    //     final String lm = DateUtils.formatDate(new Date());
    //     testDoesNotModifyHeaderFromOrigin("Last-Modified", lm);
    // }

    private void testDoesNotAddHeaderToOriginResponse(final String header) throws Exception {
        originResponse.removeHeaders(header);

        backendExpectsAnyRequest().andReturn(originResponse);

        replayMocks();
        final ClassicHttpResponse result = execute(request);
        verifyMocks();

        Assert.assertNull(result.getFirstHeader(header));
    }

    // @Test
    // public void testDoesNotAddContentLocationToOriginResponse() throws Exception {
    //     testDoesNotAddHeaderToOriginResponse("Content-Location");
    // }

    // @Test
    // public void testDoesNotAddContentMD5ToOriginResponse() throws Exception {
    //     testDoesNotAddHeaderToOriginResponse("Content-MD5");
    // }

    // @Test
    // public void testDoesNotAddEtagToOriginResponse() throws Exception {
    //     testDoesNotAddHeaderToOriginResponse("ETag");
    // }

    // @Test
    // public void testDoesNotAddLastModifiedToOriginResponse() throws Exception {
    //     testDoesNotAddHeaderToOriginResponse("Last-Modified");
    // }

    private void testDoesNotModifyHeaderFromOriginOnCacheHit(final String header, final String value) throws Exception {

        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
        final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");

        originResponse = HttpTestUtils.make200Response();
        originResponse.setHeader("Cache-Control", "max-age=3600");
        originResponse.setHeader(header, value);

        backendExpectsAnyRequest().andReturn(originResponse);

        replayMocks();
        execute(req1);
        final ClassicHttpResponse result = execute(req2);
        verifyMocks();

        Assert.assertEquals(value, result.getFirstHeader(header).getValue());
    }

    // @Test
    // public void testDoesNotModifyContentLocationFromOriginOnCacheHit() throws Exception {
    //     final String url = "http://foo.example.com/other";
    //     testDoesNotModifyHeaderFromOriginOnCacheHit("Content-Location", url);
    // }

    // @Test
    // public void testDoesNotModifyContentMD5FromOriginOnCacheHit() throws Exception {
    //     testDoesNotModifyHeaderFromOriginOnCacheHit("Content-MD5", "Q2hlY2sgSW50ZWdyaXR5IQ==");
    // }

    // @Test
    // public void testDoesNotModifyEtagFromOriginOnCacheHit() throws Exception {
    //     testDoesNotModifyHeaderFromOriginOnCacheHit("Etag", "\"the-etag\"");
    // }

    // @Test
    // public void testDoesNotModifyLastModifiedFromOriginOnCacheHit() throws Exception {
    //     final String lm = DateUtils.formatDate(new Date(System.currentTimeMillis() - 10 * 1000L));
    //     testDoesNotModifyHeaderFromOriginOnCacheHit("Last-Modified", lm);
    // }

    private void testDoesNotAddHeaderOnCacheHit(final String header) throws Exception {

        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
        final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");

        originResponse.addHeader("Cache-Control", "max-age=3600");
        originResponse.removeHeaders(header);

        backendExpectsAnyRequest().andReturn(originResponse);

        replayMocks();
        execute(req1);
        final ClassicHttpResponse result = execute(req2);
        verifyMocks();

        Assert.assertNull(result.getFirstHeader(header));
    }

    // @Test
    // public void testDoesNotAddContentLocationHeaderOnCacheHit() throws Exception {
    //     testDoesNotAddHeaderOnCacheHit("Content-Location");
    // }

    // @Test
    // public void testDoesNotAddContentMD5HeaderOnCacheHit() throws Exception {
    //     testDoesNotAddHeaderOnCacheHit("Content-MD5");
    // }

    // @Test
    // public void testDoesNotAddETagHeaderOnCacheHit() throws Exception {
    //     testDoesNotAddHeaderOnCacheHit("ETag");
    // }

    // @Test
    // public void testDoesNotAddLastModifiedHeaderOnCacheHit() throws Exception {
    //     testDoesNotAddHeaderOnCacheHit("Last-Modified");
    // }

    private void testDoesNotModifyHeaderOnRequest(final String header, final String value) throws Exception {
        final BasicClassicHttpRequest req = new BasicClassicHttpRequest("POST","/");
        req.setEntity(HttpTestUtils.makeBody(128));
        req.setHeader("Content-Length","128");
        req.setHeader(header,value);

        final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();

        EasyMock.expect(
                mockExecChain.proceed(
                        EasyMock.capture(cap),
                        EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

        replayMocks();
        execute(req);
        verifyMocks();

        final ClassicHttpRequest captured = cap.getValue();
        Assert.assertEquals(value, captured.getFirstHeader(header).getValue());
    }

    // @Test
    // public void testDoesNotModifyContentLocationHeaderOnRequest() throws Exception {
    //     final String url = "http://foo.example.com/other";
    //     testDoesNotModifyHeaderOnRequest("Content-Location",url);
    // }

    // @Test
    // public void testDoesNotModifyContentMD5HeaderOnRequest() throws Exception {
    //     testDoesNotModifyHeaderOnRequest("Content-MD5", "Q2hlY2sgSW50ZWdyaXR5IQ==");
    // }

    // @Test
    // public void testDoesNotModifyETagHeaderOnRequest() throws Exception {
    //     testDoesNotModifyHeaderOnRequest("ETag","\"etag\"");
    // }

    // @Test
    // public void testDoesNotModifyLastModifiedHeaderOnRequest() throws Exception {
    //     final long tenSecondsAgo = System.currentTimeMillis() - 10 * 1000L;
    //     final String lm = DateUtils.formatDate(new Date(tenSecondsAgo));
    //     testDoesNotModifyHeaderOnRequest("Last-Modified", lm);
    // }

    private void testDoesNotAddHeaderToRequestIfNotPresent(final String header) throws Exception {
        final BasicClassicHttpRequest req = new BasicClassicHttpRequest("POST","/");
        req.setEntity(HttpTestUtils.makeBody(128));
        req.setHeader("Content-Length","128");
        req.removeHeaders(header);

        final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();

        EasyMock.expect(
                mockExecChain.proceed(
                        EasyMock.capture(cap),
                        EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

        replayMocks();
        execute(req);
        verifyMocks();

        final ClassicHttpRequest captured = cap.getValue();
        Assert.assertNull(captured.getFirstHeader(header));
    }

    // @Test
    // public void testDoesNotAddContentLocationToRequestIfNotPresent() throws Exception {
    //     testDoesNotAddHeaderToRequestIfNotPresent("Content-Location");
    // }

    // @Test
    // public void testDoesNotAddContentMD5ToRequestIfNotPresent() throws Exception {
    //     testDoesNotAddHeaderToRequestIfNotPresent("Content-MD5");
    // }

    // @Test
    // public void testDoesNotAddETagToRequestIfNotPresent() throws Exception {
    //     testDoesNotAddHeaderToRequestIfNotPresent("ETag");
    // }

    // @Test
    // public void testDoesNotAddLastModifiedToRequestIfNotPresent() throws Exception {
    //     testDoesNotAddHeaderToRequestIfNotPresent("Last-Modified");
    // }

    /* " A transparent proxy MUST NOT modify any of the following
     * fields in a response: - Expires
     * but it MAY add any of these fields if not already present. If
     * an Expires header is added, it MUST be given a field-value
     * identical to that of the Date header in that response.
     */
    // @Test
    // public void testDoesNotModifyExpiresHeaderFromOrigin() throws Exception {
    //     final long inTenSeconds = System.currentTimeMillis() + 10 * 1000L;
    //     final String expires = DateUtils.formatDate(new Date(inTenSeconds));
    //     testDoesNotModifyHeaderFromOrigin("Expires", expires);
    // }

    // @Test
    // public void testDoesNotModifyExpiresHeaderFromOriginOnCacheHit() throws Exception {
    //     final long inTenSeconds = System.currentTimeMillis() + 10 * 1000L;
    //     final String expires = DateUtils.formatDate(new Date(inTenSeconds));
    //     testDoesNotModifyHeaderFromOriginOnCacheHit("Expires", expires);
    // }

    // @Test
    // public void testExpiresHeaderMatchesDateIfAddedToOriginResponse() throws Exception {
    //     originResponse.removeHeaders("Expires");

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();

    //     final Header expHdr = result.getFirstHeader("Expires");
    //     if (expHdr != null) {
    //         Assert.assertEquals(result.getFirstHeader("Date").getValue(),
    //                             expHdr.getValue());
    //     }
    // }

    // @Test
    // public void testExpiresHeaderMatchesDateIfAddedToCacheHit() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");

    //     originResponse.setHeader("Cache-Control","max-age=3600");
    //     originResponse.removeHeaders("Expires");

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     final Header expHdr = result.getFirstHeader("Expires");
    //     if (expHdr != null) {
    //         Assert.assertEquals(result.getFirstHeader("Date").getValue(),
    //                             expHdr.getValue());
    //     }
    // }

    /* "A proxy MUST NOT modify or add any of the following fields in
     * a message that contains the no-transform cache-control
     * directive, or in any request: - Content-Encoding - Content-Range
     * - Content-Type"
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.5.2
     */
    private void testDoesNotModifyHeaderFromOriginResponseWithNoTransform(final String header, final String value) throws Exception {
        originResponse.addHeader("Cache-Control","no-transform");
        originResponse.setHeader(header, value);

        backendExpectsAnyRequest().andReturn(originResponse);

        replayMocks();
        final ClassicHttpResponse result = execute(request);
        verifyMocks();

        Assert.assertEquals(value, result.getFirstHeader(header).getValue());
    }

    // @Test
    // public void testDoesNotModifyContentEncodingHeaderFromOriginResponseWithNoTransform() throws Exception {
    //     testDoesNotModifyHeaderFromOriginResponseWithNoTransform("Content-Encoding","gzip");
    // }

    // @Test
    // public void testDoesNotModifyContentRangeHeaderFromOriginResponseWithNoTransform() throws Exception {
    //     request.setHeader("If-Range","\"etag\"");
    //     request.setHeader("Range","bytes=0-49");

    //     originResponse = new BasicClassicHttpResponse(206, "Partial Content");
    //     originResponse.setEntity(HttpTestUtils.makeBody(50));
    //     testDoesNotModifyHeaderFromOriginResponseWithNoTransform("Content-Range","bytes 0-49/128");
    // }

    // @Test
    // public void testDoesNotModifyContentTypeHeaderFromOriginResponseWithNoTransform() throws Exception {
    //     testDoesNotModifyHeaderFromOriginResponseWithNoTransform("Content-Type","text/html;charset=utf-8");
    // }

    private void testDoesNotModifyHeaderOnCachedResponseWithNoTransform(final String header, final String value) throws Exception {
        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
        final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");

        originResponse.addHeader("Cache-Control","max-age=3600, no-transform");
        originResponse.setHeader(header, value);

        backendExpectsAnyRequest().andReturn(originResponse);

        replayMocks();
        execute(req1);
        final ClassicHttpResponse result = execute(req2);
        verifyMocks();

        Assert.assertEquals(value, result.getFirstHeader(header).getValue());
    }

    // @Test
    // public void testDoesNotModifyContentEncodingHeaderOnCachedResponseWithNoTransform() throws Exception {
    //     testDoesNotModifyHeaderOnCachedResponseWithNoTransform("Content-Encoding","gzip");
    // }

    // @Test
    // public void testDoesNotModifyContentTypeHeaderOnCachedResponseWithNoTransform() throws Exception {
    //     testDoesNotModifyHeaderOnCachedResponseWithNoTransform("Content-Type","text/html;charset=utf-8");
    // }

    // @Test
    // public void testDoesNotModifyContentRangeHeaderOnCachedResponseWithNoTransform() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("If-Range","\"etag\"");
    //     req1.setHeader("Range","bytes=0-49");
    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("If-Range","\"etag\"");
    //     req2.setHeader("Range","bytes=0-49");

    //     originResponse.addHeader("Cache-Control","max-age=3600, no-transform");
    //     originResponse.setHeader("Content-Range", "bytes 0-49/128");

    //     backendExpectsAnyRequest().andReturn(originResponse).times(1,2);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     Assert.assertEquals("bytes 0-49/128",
    //                         result.getFirstHeader("Content-Range").getValue());
    // }

    // @Test
    // public void testDoesNotAddContentEncodingHeaderToOriginResponseWithNoTransformIfNotPresent() throws Exception {
    //     originResponse.addHeader("Cache-Control","no-transform");
    //     testDoesNotAddHeaderToOriginResponse("Content-Encoding");
    // }

    // @Test
    // public void testDoesNotAddContentRangeHeaderToOriginResponseWithNoTransformIfNotPresent() throws Exception {
    //     originResponse.addHeader("Cache-Control","no-transform");
    //     testDoesNotAddHeaderToOriginResponse("Content-Range");
    // }

    // @Test
    // public void testDoesNotAddContentTypeHeaderToOriginResponseWithNoTransformIfNotPresent() throws Exception {
    //     originResponse.addHeader("Cache-Control","no-transform");
    //     testDoesNotAddHeaderToOriginResponse("Content-Type");
    // }

    // /* no add on cache hit with no-transform */
    // @Test
    // public void testDoesNotAddContentEncodingHeaderToCachedResponseWithNoTransformIfNotPresent() throws Exception {
    //     originResponse.addHeader("Cache-Control","no-transform");
    //     testDoesNotAddHeaderOnCacheHit("Content-Encoding");
    // }

    // @Test
    // public void testDoesNotAddContentRangeHeaderToCachedResponseWithNoTransformIfNotPresent() throws Exception {
    //     originResponse.addHeader("Cache-Control","no-transform");
    //     testDoesNotAddHeaderOnCacheHit("Content-Range");
    // }

    // @Test
    // public void testDoesNotAddContentTypeHeaderToCachedResponseWithNoTransformIfNotPresent() throws Exception {
    //     originResponse.addHeader("Cache-Control","no-transform");
    //     testDoesNotAddHeaderOnCacheHit("Content-Type");
    // }

    // /* no modify on request */
    // @Test
    // public void testDoesNotAddContentEncodingToRequestIfNotPresent() throws Exception {
    //     testDoesNotAddHeaderToRequestIfNotPresent("Content-Encoding");
    // }

    // @Test
    // public void testDoesNotAddContentRangeToRequestIfNotPresent() throws Exception {
    //     testDoesNotAddHeaderToRequestIfNotPresent("Content-Range");
    // }

    // @Test
    // public void testDoesNotAddContentTypeToRequestIfNotPresent() throws Exception {
    //     testDoesNotAddHeaderToRequestIfNotPresent("Content-Type");
    // }

    // @Test
    // public void testDoesNotAddContentEncodingHeaderToRequestIfNotPresent() throws Exception {
    //     testDoesNotAddHeaderToRequestIfNotPresent("Content-Encoding");
    // }

    // @Test
    // public void testDoesNotAddContentRangeHeaderToRequestIfNotPresent() throws Exception {
    //     testDoesNotAddHeaderToRequestIfNotPresent("Content-Range");
    // }

    // @Test
    // public void testDoesNotAddContentTypeHeaderToRequestIfNotPresent() throws Exception {
    //     testDoesNotAddHeaderToRequestIfNotPresent("Content-Type");
    // }

    /* "When a cache makes a validating request to a server, and the
     * server provides a 304 (Not Modified) response or a 206 (Partial
     * Content) response, the cache then constructs a response to send
     * to the requesting client.
     *
     * If the status code is 304 (Not Modified), the cache uses the
     * entity-body stored in the cache entry as the entity-body of
     * this outgoing response.
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.5.3
     */
    public void testCachedEntityBodyIsUsedForResponseAfter304Validation() throws Exception {
        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
        final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
        resp1.setHeader("Cache-Control","max-age=3600");
        resp1.setHeader("ETag","\"etag\"");

        final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
        req2.setHeader("Cache-Control","max-age=0, max-stale=0");
        final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED, "Not Modified");

        backendExpectsAnyRequestAndReturn(resp1);
        backendExpectsAnyRequestAndReturn(resp2);

        replayMocks();
        execute(req1);
        final ClassicHttpResponse result = execute(req2);
        verifyMocks();

        final InputStream i1 = resp1.getEntity().getContent();
        final InputStream i2 = result.getEntity().getContent();
        int b1, b2;
        while((b1 = i1.read()) != -1) {
            b2 = i2.read();
            Assert.assertEquals(b1, b2);
        }
        b2 = i2.read();
        Assert.assertEquals(-1, b2);
        i1.close();
        i2.close();
    }

    /* "The end-to-end headers stored in the cache entry are used for
     * the constructed response, except that ...
     *
     * - any end-to-end headers provided in the 304 or 206 response MUST
     *  replace the corresponding headers from the cache entry.
     *
     * Unless the cache decides to remove the cache entry, it MUST
     * also replace the end-to-end headers stored with the cache entry
     * with corresponding headers received in the incoming response,
     * except for Warning headers as described immediately above."
     */
    private void decorateWithEndToEndHeaders(final ClassicHttpResponse r) {
        r.setHeader("Allow","GET");
        r.setHeader("Content-Encoding","gzip");
        r.setHeader("Content-Language","en");
        r.setHeader("Content-Length", "128");
        r.setHeader("Content-Location","http://foo.example.com/other");
        r.setHeader("Content-MD5", "Q2hlY2sgSW50ZWdyaXR5IQ==");
        r.setHeader("Content-Type", "text/html;charset=utf-8");
        r.setHeader("Expires", DateUtils.formatDate(new Date(System.currentTimeMillis() + 10 * 1000L)));
        r.setHeader("Last-Modified", DateUtils.formatDate(new Date(System.currentTimeMillis() - 10 * 1000L)));
        r.setHeader("Location", "http://foo.example.com/other2");
        r.setHeader("Pragma", "x-pragma");
        r.setHeader("Retry-After","180");
    }

    // @Test
    // public void testResponseIncludesCacheEntryEndToEndHeadersForResponseAfter304Validation() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("ETag","\"etag\"");
    //     decorateWithEndToEndHeaders(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Cache-Control", "max-age=0, max-stale=0");
    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED, "Not Modified");
    //     resp2.setHeader("Date", DateUtils.formatDate(new Date()));
    //     resp2.setHeader("Server", "MockServer/1.0");

    //     backendExpectsAnyRequestAndReturn(resp1);
    //     backendExpectsAnyRequestAndReturn(resp2);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     final String[] endToEndHeaders = {
    //         "Cache-Control", "ETag", "Allow", "Content-Encoding",
    //         "Content-Language", "Content-Length", "Content-Location",
    //         "Content-MD5", "Content-Type", "Expires", "Last-Modified",
    //         "Location", "Pragma", "Retry-After"
    //     };
    //     for(final String h : endToEndHeaders) {
    //         Assert.assertEquals(HttpTestUtils.getCanonicalHeaderValue(resp1, h),
    //                             HttpTestUtils.getCanonicalHeaderValue(result, h));
    //     }
    // }

    // @Test
    // public void testUpdatedEndToEndHeadersFrom304ArePassedOnResponseAndUpdatedInCacheEntry() throws Exception {

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("ETag","\"etag\"");
    //     decorateWithEndToEndHeaders(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Cache-Control", "max-age=0, max-stale=0");
    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED, "Not Modified");
    //     resp2.setHeader("Cache-Control", "max-age=1800");
    //     resp2.setHeader("Date", DateUtils.formatDate(new Date()));
    //     resp2.setHeader("Server", "MockServer/1.0");
    //     resp2.setHeader("Allow", "GET,HEAD");
    //     resp2.setHeader("Content-Language", "en,en-us");
    //     resp2.setHeader("Content-Location", "http://foo.example.com/new");
    //     resp2.setHeader("Content-Type","text/html");
    //     resp2.setHeader("Expires", DateUtils.formatDate(new Date(System.currentTimeMillis() + 5 * 1000L)));
    //     resp2.setHeader("Location", "http://foo.example.com/new2");
    //     resp2.setHeader("Pragma","x-new-pragma");
    //     resp2.setHeader("Retry-After","120");

    //     backendExpectsAnyRequestAndReturn(resp1);
    //     backendExpectsAnyRequestAndReturn(resp2);

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result1 = execute(req2);
    //     final ClassicHttpResponse result2 = execute(req3);
    //     verifyMocks();

    //     final String[] endToEndHeaders = {
    //         "Date", "Cache-Control", "Allow", "Content-Language",
    //         "Content-Location", "Content-Type", "Expires", "Location",
    //         "Pragma", "Retry-After"
    //     };
    //     for(final String h : endToEndHeaders) {
    //         Assert.assertEquals(HttpTestUtils.getCanonicalHeaderValue(resp2, h),
    //                             HttpTestUtils.getCanonicalHeaderValue(result1, h));
    //         Assert.assertEquals(HttpTestUtils.getCanonicalHeaderValue(resp2, h),
    //                             HttpTestUtils.getCanonicalHeaderValue(result2, h));
    //     }
    // }

    /* "If a header field-name in the incoming response matches more
     * than one header in the cache entry, all such old headers MUST
     * be replaced."
     */
    // @Test
    // public void testMultiHeadersAreSuccessfullyReplacedOn304Validation() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.addHeader("Cache-Control","max-age=3600");
    //     resp1.addHeader("Cache-Control","public");
    //     resp1.setHeader("ETag","\"etag\"");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Cache-Control", "max-age=0, max-stale=0");
    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED, "Not Modified");
    //     resp2.setHeader("Cache-Control", "max-age=1800");

    //     backendExpectsAnyRequestAndReturn(resp1);
    //     backendExpectsAnyRequestAndReturn(resp2);

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result1 = execute(req2);
    //     final ClassicHttpResponse result2 = execute(req3);
    //     verifyMocks();

    //     final String h = "Cache-Control";
    //     Assert.assertEquals(HttpTestUtils.getCanonicalHeaderValue(resp2, h),
    //                         HttpTestUtils.getCanonicalHeaderValue(result1, h));
    //     Assert.assertEquals(HttpTestUtils.getCanonicalHeaderValue(resp2, h),
    //                         HttpTestUtils.getCanonicalHeaderValue(result2, h));
    // }

    // /* "If a cache has a stored non-empty set of subranges for an
    //  * entity, and an incoming response transfers another subrange,
    //  * the cache MAY combine the new subrange with the existing set if
    //  * both the following conditions are met:
    //  *
    //  * - Both the incoming response and the cache entry have a cache
    //  * validator.
    //  *
    //  * - The two cache validators match using the strong comparison
    //  * function (see section 13.3.3).
    //  *
    //  * If either requirement is not met, the cache MUST use only the
    //  * most recent partial response (based on the Date values
    //  * transmitted with every response, and using the incoming
    //  * response if these values are equal or missing), and MUST
    //  * discard the other partial information."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.5.4
    //  */
    // @Test
    // public void testCannotCombinePartialResponseIfIncomingResponseDoesNotHaveACacheValidator() throws Exception {

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("Range","bytes=0-49");

    //     final Date now = new Date();
    //     final Date oneSecondAgo = new Date(now.getTime() - 1 * 1000L);
    //     final Date twoSecondsAgo = new Date(now.getTime() - 2 * 1000L);

    //     final ClassicHttpResponse resp1 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp1.setEntity(HttpTestUtils.makeBody(50));
    //     resp1.setHeader("Server","MockServer/1.0");
    //     resp1.setHeader("Date", DateUtils.formatDate(twoSecondsAgo));
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("Content-Range","bytes 0-49/128");
    //     resp1.setHeader("ETag","\"etag1\"");

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range","bytes=50-127");

    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp2.setEntity(HttpTestUtils.makeBody(78));
    //     resp2.setHeader("Cache-Control","max-age=3600");
    //     resp2.setHeader("Content-Range","bytes 50-127/128");
    //     resp2.setHeader("Server","MockServer/1.0");
    //     resp2.setHeader("Date", DateUtils.formatDate(oneSecondAgo));

    //     backendExpectsAnyRequestAndReturn(resp2);

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");

    //     final ClassicHttpResponse resp3 = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
    //     resp3.setEntity(HttpTestUtils.makeBody(128));
    //     resp3.setHeader("Server","MockServer/1.0");
    //     resp3.setHeader("Date", DateUtils.formatDate(now));

    //     backendExpectsAnyRequestAndReturn(resp3);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     execute(req3);
    //     verifyMocks();
    // }

    // @Test
    // public void testCannotCombinePartialResponseIfCacheEntryDoesNotHaveACacheValidator() throws Exception {

    //     final Date now = new Date();
    //     final Date oneSecondAgo = new Date(now.getTime() - 1 * 1000L);
    //     final Date twoSecondsAgo = new Date(now.getTime() - 2 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("Range","bytes=0-49");

    //     final ClassicHttpResponse resp1 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp1.setEntity(HttpTestUtils.makeBody(50));
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("Content-Range","bytes 0-49/128");
    //     resp1.setHeader("Server","MockServer/1.0");
    //     resp1.setHeader("Date", DateUtils.formatDate(twoSecondsAgo));

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range","bytes=50-127");

    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp2.setEntity(HttpTestUtils.makeBody(78));
    //     resp2.setHeader("Cache-Control","max-age=3600");
    //     resp2.setHeader("Content-Range","bytes 50-127/128");
    //     resp2.setHeader("ETag","\"etag1\"");
    //     resp2.setHeader("Server","MockServer/1.0");
    //     resp2.setHeader("Date", DateUtils.formatDate(oneSecondAgo));

    //     backendExpectsAnyRequestAndReturn(resp2);

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");

    //     final ClassicHttpResponse resp3 = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
    //     resp3.setEntity(HttpTestUtils.makeBody(128));
    //     resp3.setHeader("Server","MockServer/1.0");
    //     resp3.setHeader("Date", DateUtils.formatDate(now));

    //     backendExpectsAnyRequestAndReturn(resp3);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     execute(req3);
    //     verifyMocks();
    // }

    // @Test
    // public void testCannotCombinePartialResponseIfCacheValidatorsDoNotStronglyMatch() throws Exception {

    //     final Date now = new Date();
    //     final Date oneSecondAgo = new Date(now.getTime() - 1 * 1000L);
    //     final Date twoSecondsAgo = new Date(now.getTime() - 2 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("Range","bytes=0-49");

    //     final ClassicHttpResponse resp1 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp1.setEntity(HttpTestUtils.makeBody(50));
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("Content-Range","bytes 0-49/128");
    //     resp1.setHeader("ETag","\"etag1\"");
    //     resp1.setHeader("Server","MockServer/1.0");
    //     resp1.setHeader("Date", DateUtils.formatDate(twoSecondsAgo));

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range","bytes=50-127");

    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp2.setEntity(HttpTestUtils.makeBody(78));
    //     resp2.setHeader("Cache-Control","max-age=3600");
    //     resp2.setHeader("Content-Range","bytes 50-127/128");
    //     resp2.setHeader("ETag","\"etag2\"");
    //     resp2.setHeader("Server","MockServer/1.0");
    //     resp2.setHeader("Date", DateUtils.formatDate(oneSecondAgo));

    //     backendExpectsAnyRequestAndReturn(resp2);

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");

    //     final ClassicHttpResponse resp3 = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
    //     resp3.setEntity(HttpTestUtils.makeBody(128));
    //     resp3.setHeader("Server","MockServer/1.0");
    //     resp3.setHeader("Date", DateUtils.formatDate(now));

    //     backendExpectsAnyRequestAndReturn(resp3);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     execute(req3);
    //     verifyMocks();
    // }

    // @Test
    // public void testMustDiscardLeastRecentPartialResponseIfIncomingRequestDoesNotHaveCacheValidator() throws Exception {

    //     final Date now = new Date();
    //     final Date oneSecondAgo = new Date(now.getTime() - 1 * 1000L);
    //     final Date twoSecondsAgo = new Date(now.getTime() - 2 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("Range","bytes=0-49");

    //     final ClassicHttpResponse resp1 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp1.setEntity(HttpTestUtils.makeBody(50));
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("Content-Range","bytes 0-49/128");
    //     resp1.setHeader("ETag","\"etag1\"");
    //     resp1.setHeader("Server","MockServer/1.0");
    //     resp1.setHeader("Date", DateUtils.formatDate(twoSecondsAgo));

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range","bytes=50-127");

    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp2.setEntity(HttpTestUtils.makeBody(78));
    //     resp2.setHeader("Cache-Control","max-age=3600");
    //     resp2.setHeader("Content-Range","bytes 50-127/128");
    //     resp2.setHeader("Server","MockServer/1.0");
    //     resp2.setHeader("Date", DateUtils.formatDate(oneSecondAgo));

    //     backendExpectsAnyRequestAndReturn(resp2);

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");
    //     req3.setHeader("Range","bytes=0-49");

    //     final ClassicHttpResponse resp3 = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
    //     resp3.setEntity(HttpTestUtils.makeBody(128));
    //     resp3.setHeader("Server","MockServer/1.0");
    //     resp3.setHeader("Date", DateUtils.formatDate(now));

    //     // must make this request; cannot serve from cache
    //     backendExpectsAnyRequestAndReturn(resp3);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     execute(req3);
    //     verifyMocks();
    // }

    // @Test
    // public void testMustDiscardLeastRecentPartialResponseIfCachedResponseDoesNotHaveCacheValidator() throws Exception {

    //     final Date now = new Date();
    //     final Date oneSecondAgo = new Date(now.getTime() - 1 * 1000L);
    //     final Date twoSecondsAgo = new Date(now.getTime() - 2 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("Range","bytes=0-49");

    //     final ClassicHttpResponse resp1 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp1.setEntity(HttpTestUtils.makeBody(50));
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("Content-Range","bytes 0-49/128");
    //     resp1.setHeader("Server","MockServer/1.0");
    //     resp1.setHeader("Date", DateUtils.formatDate(twoSecondsAgo));

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range","bytes=50-127");

    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp2.setEntity(HttpTestUtils.makeBody(78));
    //     resp2.setHeader("Cache-Control","max-age=3600");
    //     resp2.setHeader("Content-Range","bytes 50-127/128");
    //     resp2.setHeader("ETag","\"etag1\"");
    //     resp2.setHeader("Server","MockServer/1.0");
    //     resp2.setHeader("Date", DateUtils.formatDate(oneSecondAgo));

    //     backendExpectsAnyRequestAndReturn(resp2);

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");
    //     req3.setHeader("Range","bytes=0-49");

    //     final ClassicHttpResponse resp3 = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
    //     resp3.setEntity(HttpTestUtils.makeBody(128));
    //     resp3.setHeader("Server","MockServer/1.0");
    //     resp3.setHeader("Date", DateUtils.formatDate(now));

    //     // must make this request; cannot serve from cache
    //     backendExpectsAnyRequestAndReturn(resp3);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     execute(req3);
    //     verifyMocks();
    // }

    // @Test
    // public void testMustDiscardLeastRecentPartialResponseIfCacheValidatorsDoNotStronglyMatch() throws Exception {

    //     final Date now = new Date();
    //     final Date oneSecondAgo = new Date(now.getTime() - 1 * 1000L);
    //     final Date twoSecondsAgo = new Date(now.getTime() - 2 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("Range","bytes=0-49");

    //     final ClassicHttpResponse resp1 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp1.setEntity(HttpTestUtils.makeBody(50));
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("Content-Range","bytes 0-49/128");
    //     resp1.setHeader("Etag","\"etag1\"");
    //     resp1.setHeader("Server","MockServer/1.0");
    //     resp1.setHeader("Date", DateUtils.formatDate(twoSecondsAgo));

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range","bytes=50-127");

    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp2.setEntity(HttpTestUtils.makeBody(78));
    //     resp2.setHeader("Cache-Control","max-age=3600");
    //     resp2.setHeader("Content-Range","bytes 50-127/128");
    //     resp2.setHeader("ETag","\"etag2\"");
    //     resp2.setHeader("Server","MockServer/1.0");
    //     resp2.setHeader("Date", DateUtils.formatDate(oneSecondAgo));

    //     backendExpectsAnyRequestAndReturn(resp2);

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");
    //     req3.setHeader("Range","bytes=0-49");

    //     final ClassicHttpResponse resp3 = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
    //     resp3.setEntity(HttpTestUtils.makeBody(128));
    //     resp3.setHeader("Server","MockServer/1.0");
    //     resp3.setHeader("Date", DateUtils.formatDate(now));

    //     // must make this request; cannot serve from cache
    //     backendExpectsAnyRequestAndReturn(resp3);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     execute(req3);
    //     verifyMocks();
    // }

    // @Test
    // public void testMustDiscardLeastRecentPartialResponseIfCacheValidatorsDoNotStronglyMatchEvenIfResponsesOutOfOrder() throws Exception {

    //     final Date now = new Date();
    //     final Date oneSecondAgo = new Date(now.getTime() - 1 * 1000L);
    //     final Date twoSecondsAgo = new Date(now.getTime() - 2 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("Range","bytes=0-49");

    //     final ClassicHttpResponse resp1 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp1.setEntity(HttpTestUtils.makeBody(50));
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("Content-Range","bytes 0-49/128");
    //     resp1.setHeader("Etag","\"etag1\"");
    //     resp1.setHeader("Server","MockServer/1.0");
    //     resp1.setHeader("Date", DateUtils.formatDate(oneSecondAgo));

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range","bytes=50-127");

    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp2.setEntity(HttpTestUtils.makeBody(78));
    //     resp2.setHeader("Cache-Control","max-age=3600");
    //     resp2.setHeader("Content-Range","bytes 50-127/128");
    //     resp2.setHeader("ETag","\"etag2\"");
    //     resp2.setHeader("Server","MockServer/1.0");
    //     resp2.setHeader("Date", DateUtils.formatDate(twoSecondsAgo));

    //     backendExpectsAnyRequestAndReturn(resp2);

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");
    //     req3.setHeader("Range","bytes=50-127");

    //     final ClassicHttpResponse resp3 = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
    //     resp3.setEntity(HttpTestUtils.makeBody(128));
    //     resp3.setHeader("Server","MockServer/1.0");
    //     resp3.setHeader("Date", DateUtils.formatDate(now));

    //     // must make this request; cannot serve from cache
    //     backendExpectsAnyRequestAndReturn(resp3);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     execute(req3);
    //     verifyMocks();
    // }

    // @Test
    // public void testMustDiscardCachedPartialResponseIfCacheValidatorsDoNotStronglyMatchAndDateHeadersAreEqual() throws Exception {

    //     final Date now = new Date();
    //     final Date oneSecondAgo = new Date(now.getTime() - 1 * 1000L);

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("Range","bytes=0-49");

    //     final ClassicHttpResponse resp1 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp1.setEntity(HttpTestUtils.makeBody(50));
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("Content-Range","bytes 0-49/128");
    //     resp1.setHeader("Etag","\"etag1\"");
    //     resp1.setHeader("Server","MockServer/1.0");
    //     resp1.setHeader("Date", DateUtils.formatDate(oneSecondAgo));

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Range","bytes=50-127");

    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_PARTIAL_CONTENT, "Partial Content");
    //     resp2.setEntity(HttpTestUtils.makeBody(78));
    //     resp2.setHeader("Cache-Control","max-age=3600");
    //     resp2.setHeader("Content-Range","bytes 50-127/128");
    //     resp2.setHeader("ETag","\"etag2\"");
    //     resp2.setHeader("Server","MockServer/1.0");
    //     resp2.setHeader("Date", DateUtils.formatDate(oneSecondAgo));

    //     backendExpectsAnyRequestAndReturn(resp2);

    //     final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");
    //     req3.setHeader("Range","bytes=0-49");

    //     final ClassicHttpResponse resp3 = new BasicClassicHttpResponse(HttpStatus.SC_OK, "OK");
    //     resp3.setEntity(HttpTestUtils.makeBody(128));
    //     resp3.setHeader("Server","MockServer/1.0");
    //     resp3.setHeader("Date", DateUtils.formatDate(now));

    //     // must make this request; cannot serve from cache
    //     backendExpectsAnyRequestAndReturn(resp3);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     execute(req3);
    //     verifyMocks();
    // }

    // /* "When the cache receives a subsequent request whose Request-URI
    //  * specifies one or more cache entries including a Vary header
    //  * field, the cache MUST NOT use such a cache entry to construct a
    //  * response to the new request unless all of the selecting
    //  * request-headers present in the new request match the
    //  * corresponding stored request-headers in the original request."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.6
    //  */
    // @Test
    // public void testCannotUseVariantCacheEntryIfNotAllSelectingRequestHeadersMatch() throws Exception {

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("Accept-Encoding","gzip");

    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag","\"etag1\"");
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("Vary","Accept-Encoding");

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.removeHeaders("Accept-Encoding");

    //     final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();
    //     resp2.setHeader("ETag","\"etag1\"");
    //     resp2.setHeader("Cache-Control","max-age=3600");

    //     // not allowed to have a cache hit; must forward request
    //     backendExpectsAnyRequestAndReturn(resp2);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     verifyMocks();
    // }

    // /* "A Vary header field-value of "*" always fails to match and
    //  * subsequent requests on that resource can only be properly
    //  * interpreted by the origin server."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.6
    //  */
    // @Test
    // public void testCannotServeFromCacheForVaryStar() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");

    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag","\"etag1\"");
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("Vary","*");

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");

    //     final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();
    //     resp2.setHeader("ETag","\"etag1\"");
    //     resp2.setHeader("Cache-Control","max-age=3600");

    //     // not allowed to have a cache hit; must forward request
    //     backendExpectsAnyRequestAndReturn(resp2);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     verifyMocks();
    // }

    // /* " If the selecting request header fields for the cached entry
    //  * do not match the selecting request header fields of the new
    //  * request, then the cache MUST NOT use a cached entry to satisfy
    //  * the request unless it first relays the new request to the
    //  * origin server in a conditional request and the server responds
    //  * with 304 (Not Modified), including an entity tag or
    //  * Content-Location that indicates the entity to be used.
    //  *
    //  * If an entity tag was assigned to a cached representation, the
    //  * forwarded request SHOULD be conditional and include the entity
    //  * tags in an If-None-Match header field from all its cache
    //  * entries for the resource. This conveys to the server the set of
    //  * entities currently held by the cache, so that if any one of
    //  * these entities matches the requested entity, the server can use
    //  * the ETag header field in its 304 (Not Modified) response to
    //  * tell the cache which entry is appropriate. If the entity-tag of
    //  * the new response matches that of an existing entry, the new
    //  * response SHOULD be used to processChallenge the header fields of the
    //  * existing entry, and the result MUST be returned to the client.
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.6
    //  */
    // @Test
    // public void testNonmatchingVariantCannotBeServedFromCacheUnlessConditionallyValidated() throws Exception {

    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     req1.setHeader("User-Agent","MyBrowser/1.0");

    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag","\"etag1\"");
    //     resp1.setHeader("Cache-Control","max-age=3600");
    //     resp1.setHeader("Vary","User-Agent");
    //     resp1.setHeader("Content-Type","application/octet-stream");

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("User-Agent","MyBrowser/1.5");

    //     final ClassicHttpRequest conditional = new BasicClassicHttpRequest("GET", "/");
    //     conditional.setHeader("User-Agent","MyBrowser/1.5");
    //     conditional.setHeader("If-None-Match","\"etag1\"");

    //     final ClassicHttpResponse resp200 = HttpTestUtils.make200Response();
    //     resp200.setHeader("ETag","\"etag1\"");
    //     resp200.setHeader("Vary","User-Agent");

    //     final ClassicHttpResponse resp304 = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED, "Not Modified");
    //     resp304.setHeader("ETag","\"etag1\"");
    //     resp304.setHeader("Vary","User-Agent");

    //     final Capture<ClassicHttpRequest> condCap = EasyMock.newCapture();
    //     final Capture<ClassicHttpRequest> uncondCap = EasyMock.newCapture();

    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.and(eqRequest(conditional), EasyMock.capture(condCap)),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp304).times(0,1);
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.and(eqRequest(req2), EasyMock.capture(uncondCap)),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp200).times(0,1);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     if (HttpStatus.SC_OK == result.getCode()) {
    //         Assert.assertTrue(condCap.hasCaptured()
    //                           || uncondCap.hasCaptured());
    //         if (uncondCap.hasCaptured()) {
    //             Assert.assertTrue(HttpTestUtils.semanticallyTransparent(resp200, result));
    //         }
    //     }
    // }

    /* "Some HTTP methods MUST cause a cache to invalidate an
     * entity. This is either the entity referred to by the
     * Request-URI, or by the Location or Content-Location headers (if
     * present). These methods are:
     * - PUT
     * - DELETE
     * - POST
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.9
     */
    protected void testUnsafeOperationInvalidatesCacheForThatUri(
            final ClassicHttpRequest unsafeReq) throws Exception {
        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
        final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
        resp1.setHeader("Cache-Control","public, max-age=3600");

        backendExpectsAnyRequestAndReturn(resp1);

        final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_NO_CONTENT, "No Content");

        backendExpectsAnyRequestAndReturn(resp2);

        final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/");
        final ClassicHttpResponse resp3 = HttpTestUtils.make200Response();
        resp3.setHeader("Cache-Control","public, max-age=3600");

        // this origin request MUST happen due to invalidation
        backendExpectsAnyRequestAndReturn(resp3);

        replayMocks();
        execute(req1);
        execute(unsafeReq);
        execute(req3);
        verifyMocks();
    }

    // @Test
    // public void testPutToUriInvalidatesCacheForThatUri() throws Exception {
    //     final ClassicHttpRequest req = makeRequestWithBody("PUT","/");
    //     testUnsafeOperationInvalidatesCacheForThatUri(req);
    // }

    // @Test
    // public void testDeleteToUriInvalidatesCacheForThatUri() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("DELETE","/");
    //     testUnsafeOperationInvalidatesCacheForThatUri(req);
    // }

    // @Test
    // public void testPostToUriInvalidatesCacheForThatUri() throws Exception {
    //     final ClassicHttpRequest req = makeRequestWithBody("POST","/");
    //     testUnsafeOperationInvalidatesCacheForThatUri(req);
    // }

    protected void testUnsafeMethodInvalidatesCacheForHeaderUri(
            final ClassicHttpRequest unsafeReq) throws Exception {
        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/content");
        final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
        resp1.setHeader("Cache-Control","public, max-age=3600");

        backendExpectsAnyRequestAndReturn(resp1);

        final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_NO_CONTENT, "No Content");

        backendExpectsAnyRequestAndReturn(resp2);

        final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/content");
        final ClassicHttpResponse resp3 = HttpTestUtils.make200Response();
        resp3.setHeader("Cache-Control","public, max-age=3600");

        // this origin request MUST happen due to invalidation
        backendExpectsAnyRequestAndReturn(resp3);

        replayMocks();
        execute(req1);
        execute(unsafeReq);
        execute(req3);
        verifyMocks();
    }

    protected void testUnsafeMethodInvalidatesCacheForUriInContentLocationHeader(
            final ClassicHttpRequest unsafeReq) throws Exception {
        unsafeReq.setHeader("Content-Location","http://foo.example.com/content");
        testUnsafeMethodInvalidatesCacheForHeaderUri(unsafeReq);
    }

    protected void testUnsafeMethodInvalidatesCacheForRelativeUriInContentLocationHeader(
            final ClassicHttpRequest unsafeReq) throws Exception {
        unsafeReq.setHeader("Content-Location","/content");
        testUnsafeMethodInvalidatesCacheForHeaderUri(unsafeReq);
    }

    protected void testUnsafeMethodInvalidatesCacheForUriInLocationHeader(
            final ClassicHttpRequest unsafeReq) throws Exception {
        unsafeReq.setHeader("Location","http://foo.example.com/content");
        testUnsafeMethodInvalidatesCacheForHeaderUri(unsafeReq);
    }

    // @Test
    // public void testPutInvalidatesCacheForThatUriInContentLocationHeader() throws Exception {
    //     final ClassicHttpRequest req2 = makeRequestWithBody("PUT","/");
    //     testUnsafeMethodInvalidatesCacheForUriInContentLocationHeader(req2);
    // }

    // @Test
    // public void testPutInvalidatesCacheForThatUriInLocationHeader() throws Exception {
    //     final ClassicHttpRequest req = makeRequestWithBody("PUT","/");
    //     testUnsafeMethodInvalidatesCacheForUriInLocationHeader(req);
    // }

    // @Test
    // public void testPutInvalidatesCacheForThatUriInRelativeContentLocationHeader() throws Exception {
    //     final ClassicHttpRequest req = makeRequestWithBody("PUT","/");
    //     testUnsafeMethodInvalidatesCacheForRelativeUriInContentLocationHeader(req);
    // }

    // @Test
    // public void testDeleteInvalidatesCacheForThatUriInContentLocationHeader() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("DELETE", "/");
    //     testUnsafeMethodInvalidatesCacheForUriInContentLocationHeader(req);
    // }

    // @Test
    // public void testDeleteInvalidatesCacheForThatUriInRelativeContentLocationHeader() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("DELETE", "/");
    //     testUnsafeMethodInvalidatesCacheForRelativeUriInContentLocationHeader(req);
    // }

    // @Test
    // public void testDeleteInvalidatesCacheForThatUriInLocationHeader() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("DELETE", "/");
    //     testUnsafeMethodInvalidatesCacheForUriInLocationHeader(req);
    // }

    // @Test
    // public void testPostInvalidatesCacheForThatUriInContentLocationHeader() throws Exception {
    //     final ClassicHttpRequest req = makeRequestWithBody("POST","/");
    //     testUnsafeMethodInvalidatesCacheForUriInContentLocationHeader(req);
    // }

    // @Test
    // public void testPostInvalidatesCacheForThatUriInLocationHeader() throws Exception {
    //     final ClassicHttpRequest req = makeRequestWithBody("POST","/");
    //     testUnsafeMethodInvalidatesCacheForUriInLocationHeader(req);
    // }

    // @Test
    // public void testPostInvalidatesCacheForRelativeUriInContentLocationHeader() throws Exception {
    //     final ClassicHttpRequest req = makeRequestWithBody("POST","/");
    //     testUnsafeMethodInvalidatesCacheForRelativeUriInContentLocationHeader(req);
    // }

    /* "In order to prevent denial of service attacks, an invalidation based on the URI
     *  in a Location or Content-Location header MUST only be performed if the host part
     *  is the same as in the Request-URI."
     *
     *  http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.10
     */
    protected void testUnsafeMethodDoesNotInvalidateCacheForHeaderUri(
            final ClassicHttpRequest unsafeReq) throws Exception {

        final HttpHost otherHost = new HttpHost("bar.example.com", 80);
        final HttpRoute otherRoute = new HttpRoute(otherHost);
        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/content");
        final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
        resp1.setHeader("Cache-Control","public, max-age=3600");

        backendExpectsAnyRequestAndReturn(resp1);

        final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_NO_CONTENT, "No Content");

        backendExpectsAnyRequestAndReturn(resp2);

        final ClassicHttpRequest req3 = new BasicClassicHttpRequest("GET", "/content");

        replayMocks();
        execute(req1);
        execute(unsafeReq);
        execute(req3);
        verifyMocks();
    }

    protected void testUnsafeMethodDoesNotInvalidateCacheForUriInContentLocationHeadersFromOtherHosts(
            final ClassicHttpRequest unsafeReq) throws Exception {
        unsafeReq.setHeader("Content-Location","http://bar.example.com/content");
        testUnsafeMethodDoesNotInvalidateCacheForHeaderUri(unsafeReq);
    }

    protected void testUnsafeMethodDoesNotInvalidateCacheForUriInLocationHeadersFromOtherHosts(
            final ClassicHttpRequest unsafeReq) throws Exception {
        unsafeReq.setHeader("Location","http://bar.example.com/content");
        testUnsafeMethodDoesNotInvalidateCacheForHeaderUri(unsafeReq);
    }

    protected ClassicHttpRequest makeRequestWithBody(final String method, final String requestUri) {
        final ClassicHttpRequest req =
            new BasicClassicHttpRequest(method, requestUri);
        final int nbytes = 128;
        req.setEntity(HttpTestUtils.makeBody(nbytes));
        req.setHeader("Content-Length",""+nbytes);
        return req;
    }

    // @Test
    // public void testPutDoesNotInvalidateCacheForUriInContentLocationHeadersFromOtherHosts() throws Exception {
    //     final ClassicHttpRequest req = makeRequestWithBody("PUT","/");
    //     testUnsafeMethodDoesNotInvalidateCacheForUriInContentLocationHeadersFromOtherHosts(req);
    // }

    // @Test
    // public void testPutDoesNotInvalidateCacheForUriInLocationHeadersFromOtherHosts() throws Exception {
    //     final ClassicHttpRequest req = makeRequestWithBody("PUT","/");
    //     testUnsafeMethodDoesNotInvalidateCacheForUriInLocationHeadersFromOtherHosts(req);
    // }

    // @Test
    // public void testPostDoesNotInvalidateCacheForUriInContentLocationHeadersFromOtherHosts() throws Exception {
    //     final ClassicHttpRequest req = makeRequestWithBody("POST","/");
    //     testUnsafeMethodDoesNotInvalidateCacheForUriInContentLocationHeadersFromOtherHosts(req);
    // }

    // @Test
    // public void testPostDoesNotInvalidateCacheForUriInLocationHeadersFromOtherHosts() throws Exception {
    //     final ClassicHttpRequest req = makeRequestWithBody("POST","/");
    //     testUnsafeMethodDoesNotInvalidateCacheForUriInLocationHeadersFromOtherHosts(req);
    // }

    // @Test
    // public void testDeleteDoesNotInvalidateCacheForUriInContentLocationHeadersFromOtherHosts() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("DELETE", "/");
    //     testUnsafeMethodDoesNotInvalidateCacheForUriInContentLocationHeadersFromOtherHosts(req);
    // }

    // @Test
    // public void testDeleteDoesNotInvalidateCacheForUriInLocationHeadersFromOtherHosts() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("DELETE", "/");
    //     testUnsafeMethodDoesNotInvalidateCacheForUriInLocationHeadersFromOtherHosts(req);
    // }

    /* "All methods that might be expected to cause modifications to the origin
     * server's resources MUST be written through to the origin server. This
     * currently includes all methods except for GET and HEAD. A cache MUST NOT
     * reply to such a request from a client before having transmitted the
     * request to the inbound server, and having received a corresponding
     * response from the inbound server."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec13.html#sec13.11
     */
    private void testRequestIsWrittenThroughToOrigin(final ClassicHttpRequest req) throws Exception {
        final ClassicHttpResponse resp = new BasicClassicHttpResponse(HttpStatus.SC_NO_CONTENT, "No Content");
        final ClassicHttpRequest wrapper = req;
        EasyMock.expect(
                mockExecChain.proceed(
                        eqRequest(wrapper),
                        EasyMock.isA(ExecChain.Scope.class))).andReturn(resp);

        replayMocks();
        execute(wrapper);
        verifyMocks();
    }

    // @Test @Ignore
    // public void testOPTIONSRequestsAreWrittenThroughToOrigin() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("OPTIONS","*");
    //     testRequestIsWrittenThroughToOrigin(req);
    // }

    // @Test
    // public void testPOSTRequestsAreWrittenThroughToOrigin() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("POST","/");
    //     req.setEntity(HttpTestUtils.makeBody(128));
    //     req.setHeader("Content-Length","128");
    //     testRequestIsWrittenThroughToOrigin(req);
    // }

    // @Test
    // public void testPUTRequestsAreWrittenThroughToOrigin() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("PUT","/");
    //     req.setEntity(HttpTestUtils.makeBody(128));
    //     req.setHeader("Content-Length","128");
    //     testRequestIsWrittenThroughToOrigin(req);
    // }

    // @Test
    // public void testDELETERequestsAreWrittenThroughToOrigin() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("DELETE", "/");
    //     testRequestIsWrittenThroughToOrigin(req);
    // }

    // @Test
    // public void testTRACERequestsAreWrittenThroughToOrigin() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("TRACE","/");
    //     testRequestIsWrittenThroughToOrigin(req);
    // }

    // @Test
    // public void testCONNECTRequestsAreWrittenThroughToOrigin() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("CONNECT","/");
    //     testRequestIsWrittenThroughToOrigin(req);
    // }

    // @Test
    // public void testUnknownMethodRequestsAreWrittenThroughToOrigin() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("UNKNOWN","/");
    //     testRequestIsWrittenThroughToOrigin(req);
    // }

    // /* "If a cache receives a value larger than the largest positive
    //  * integer it can represent, or if any of its age calculations
    //  * overflows, it MUST transmit an Age header with a value of
    //  * 2147483648 (2^31)."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.6
    //  */
    // @Test
    // public void testTransmitsAgeHeaderIfIncomingAgeHeaderTooBig() throws Exception {
    //     final String reallyOldAge = "1" + Long.MAX_VALUE;
    //     originResponse.setHeader("Age",reallyOldAge);

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();

    //     Assert.assertEquals("2147483648",
    //                         result.getFirstHeader("Age").getValue());
    // }

    // /* "A proxy MUST NOT modify the Allow header field even if it does not
    //  * understand all the methods specified, since the user agent might
    //  * have other means of communicating with the origin server.
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.7
    //  */
    // @Test
    // public void testDoesNotModifyAllowHeaderWithUnknownMethods() throws Exception {
    //     final String allowHeaderValue = "GET, HEAD, FOOBAR";
    //     originResponse.setHeader("Allow",allowHeaderValue);
    //     backendExpectsAnyRequest().andReturn(originResponse);
    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();
    //     Assert.assertEquals(HttpTestUtils.getCanonicalHeaderValue(originResponse,"Allow"),
    //                         HttpTestUtils.getCanonicalHeaderValue(result, "Allow"));
    // }

    /* "When a shared cache (see section 13.7) receives a request
     * containing an Authorization field, it MUST NOT return the
     * corresponding response as a reply to any other request, unless one
     * of the following specific exceptions holds:
     *
     * 1. If the response includes the "s-maxage" cache-control
     *    directive, the cache MAY use that response in replying to a
     *    subsequent request. But (if the specified maximum age has
     *    passed) a proxy cache MUST first revalidate it with the origin
     *    server, using the request-headers from the new request to allow
     *    the origin server to authenticate the new request. (This is the
     *    defined behavior for s-maxage.) If the response includes "s-
     *    maxage=0", the proxy MUST always revalidate it before re-using
     *    it.
     *
     * 2. If the response includes the "must-revalidate" cache-control
     *    directive, the cache MAY use that response in replying to a
     *    subsequent request. But if the response is stale, all caches
     *    MUST first revalidate it with the origin server, using the
     *    request-headers from the new request to allow the origin server
     *    to authenticate the new request.
     *
     * 3. If the response includes the "public" cache-control directive,
     *    it MAY be returned in reply to any subsequent request.
     */
    protected void testSharedCacheRevalidatesAuthorizedResponse(
            final ClassicHttpResponse authorizedResponse, final int minTimes, final int maxTimes) throws Exception {
        if (config.isSharedCache()) {
            final String authorization = StandardAuthScheme.BASIC + " dXNlcjpwYXNzd2Q=";
            final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
            req1.setHeader("Authorization",authorization);

            backendExpectsAnyRequestAndReturn(authorizedResponse);

            final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
            final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();
            resp2.setHeader("Cache-Control","max-age=3600");

            if (maxTimes > 0) {
                // this request MUST happen
                backendExpectsAnyRequest().andReturn(resp2).times(minTimes,maxTimes);
            }

            replayMocks();
            execute(req1);
            execute(req2);
            verifyMocks();
        }
    }

    // @Test
    // public void testSharedCacheMustNotNormallyCacheAuthorizedResponses() throws Exception {
    //     final ClassicHttpResponse resp = HttpTestUtils.make200Response();
    //     resp.setHeader("Cache-Control","max-age=3600");
    //     resp.setHeader("ETag","\"etag\"");
    //     testSharedCacheRevalidatesAuthorizedResponse(resp, 1, 1);
    // }

    // @Test
    // public void testSharedCacheMayCacheAuthorizedResponsesWithSMaxAgeHeader() throws Exception {
    //     final ClassicHttpResponse resp = HttpTestUtils.make200Response();
    //     resp.setHeader("Cache-Control","s-maxage=3600");
    //     resp.setHeader("ETag","\"etag\"");
    //     testSharedCacheRevalidatesAuthorizedResponse(resp, 0, 1);
    // }

    // @Test
    // public void testSharedCacheMustRevalidateAuthorizedResponsesWhenSMaxAgeIsZero() throws Exception {
    //     final ClassicHttpResponse resp = HttpTestUtils.make200Response();
    //     resp.setHeader("Cache-Control","s-maxage=0");
    //     resp.setHeader("ETag","\"etag\"");
    //     testSharedCacheRevalidatesAuthorizedResponse(resp, 1, 1);
    // }

    // @Test
    // public void testSharedCacheMayCacheAuthorizedResponsesWithMustRevalidate() throws Exception {
    //     final ClassicHttpResponse resp = HttpTestUtils.make200Response();
    //     resp.setHeader("Cache-Control","must-revalidate");
    //     resp.setHeader("ETag","\"etag\"");
    //     testSharedCacheRevalidatesAuthorizedResponse(resp, 0, 1);
    // }

    // @Test
    // public void testSharedCacheMayCacheAuthorizedResponsesWithCacheControlPublic() throws Exception {
    //     final ClassicHttpResponse resp = HttpTestUtils.make200Response();
    //     resp.setHeader("Cache-Control","public");
    //     testSharedCacheRevalidatesAuthorizedResponse(resp, 0, 1);
    // }

    protected void testSharedCacheMustUseNewRequestHeadersWhenRevalidatingAuthorizedResponse(
            final ClassicHttpResponse authorizedResponse) throws Exception {
        if (config.isSharedCache()) {
            final String authorization1 = StandardAuthScheme.BASIC + " dXNlcjpwYXNzd2Q=";
            final String authorization2 = StandardAuthScheme.BASIC + " dXNlcjpwYXNzd2Qy";

            final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
            req1.setHeader("Authorization",authorization1);

            backendExpectsAnyRequestAndReturn(authorizedResponse);

            final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
            req2.setHeader("Authorization",authorization2);

            final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();

            final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();
            EasyMock.expect(
                    mockExecChain.proceed(
                            EasyMock.capture(cap),
                            EasyMock.isA(ExecChain.Scope.class))).andReturn(resp2);

            replayMocks();
            execute(req1);
            execute(req2);
            verifyMocks();

            final ClassicHttpRequest captured = cap.getValue();
            Assert.assertEquals(HttpTestUtils.getCanonicalHeaderValue(req2, "Authorization"),
                    HttpTestUtils.getCanonicalHeaderValue(captured, "Authorization"));
        }
    }

    // @Test
    // public void testSharedCacheMustUseNewRequestHeadersWhenRevalidatingAuthorizedResponsesWithSMaxAge() throws Exception {
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Date",DateUtils.formatDate(tenSecondsAgo));
    //     resp1.setHeader("ETag","\"etag\"");
    //     resp1.setHeader("Cache-Control","s-maxage=5");

    //     testSharedCacheMustUseNewRequestHeadersWhenRevalidatingAuthorizedResponse(resp1);
    // }

    // @Test
    // public void testSharedCacheMustUseNewRequestHeadersWhenRevalidatingAuthorizedResponsesWithMustRevalidate() throws Exception {
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Date",DateUtils.formatDate(tenSecondsAgo));
    //     resp1.setHeader("ETag","\"etag\"");
    //     resp1.setHeader("Cache-Control","maxage=5, must-revalidate");

    //     testSharedCacheMustUseNewRequestHeadersWhenRevalidatingAuthorizedResponse(resp1);
    // }

    // /* "If a cache returns a stale response, either because of a max-stale
    //  * directive on a request, or because the cache is configured to
    //  * override the expiration time of a response, the cache MUST attach a
    //  * Warning header to the stale response, using Warning 110 (Response
    //  * is stale).
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.3
    //  *
    //  * "110 Response is stale MUST be included whenever the returned
    //  * response is stale."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.46
    //  */
    // @Test
    // public void testWarning110IsAddedToStaleResponses() throws Exception {
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Date", DateUtils.formatDate(tenSecondsAgo));
    //     resp1.setHeader("Cache-Control","max-age=5");
    //     resp1.setHeader("Etag","\"etag\"");

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Cache-Control","max-stale=60");
    //     final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();

    //     final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.capture(cap),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp2).times(0,1);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     if (!cap.hasCaptured()) {
    //         boolean found110Warning = false;
    //         final Iterator<HeaderElement> it = MessageSupport.iterate(result, HttpHeaders.WARNING);
    //         while (it.hasNext()) {
    //             final HeaderElement elt = it.next();
    //             final String[] parts = elt.getName().split("\\s");
    //             if ("110".equals(parts[0])) {
    //                 found110Warning = true;
    //                 break;
    //             }
    //         }
    //         Assert.assertTrue(found110Warning);
    //     }
    // }

    // /* "Field names MUST NOT be included with the no-cache directive in a
    //  * request."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4
    //  */
    // @Test
    // public void testDoesNotTransmitNoCacheDirectivesWithFieldsDownstream() throws Exception {
    //     request.setHeader("Cache-Control","no-cache=\"X-Field\"");
    //     final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();
    //     EasyMock.expect(mockExecChain.proceed(
    //             EasyMock.capture(cap),
    //             EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse).times(0,1);

    //     replayMocks();
    //     try {
    //         execute(request);
    //     } catch (final ClientProtocolException acceptable) {
    //     }
    //     verifyMocks();

    //     if (cap.hasCaptured()) {
    //         final ClassicHttpRequest captured = cap.getValue();
    //         final Iterator<HeaderElement> it = MessageSupport.iterate(captured, HttpHeaders.CACHE_CONTROL);
    //         while (it.hasNext()) {
    //             final HeaderElement elt = it.next();
    //             if ("no-cache".equals(elt.getName())) {
    //                 Assert.assertNull(elt.getValue());
    //             }
    //         }
    //     }
    // }

    /* "The request includes a "no-cache" cache-control directive or, for
     * compatibility with HTTP/1.0 clients, "Pragma: no-cache".... The
     * server MUST NOT use a cached copy when responding to such a request."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4
     */
    protected void testCacheIsNotUsedWhenRespondingToRequest(final ClassicHttpRequest req) throws Exception {
        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
        final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
        resp1.setHeader("Etag","\"etag\"");
        resp1.setHeader("Cache-Control","max-age=3600");

        backendExpectsAnyRequestAndReturn(resp1);

        final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();
        resp2.setHeader("Etag","\"etag2\"");
        resp2.setHeader("Cache-Control","max-age=1200");

        final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();
        EasyMock.expect(mockExecChain.proceed(
                EasyMock.capture(cap),
                EasyMock.isA(ExecChain.Scope.class))).andReturn(resp2);

        replayMocks();
        execute(req1);
        final ClassicHttpResponse result = execute(req);
        verifyMocks();

        Assert.assertTrue(HttpTestUtils.semanticallyTransparent(resp2, result));
        final ClassicHttpRequest captured = cap.getValue();
        Assert.assertTrue(HttpTestUtils.equivalent(req, captured));
    }

    // @Test
    // public void testCacheIsNotUsedWhenRespondingToRequestWithCacheControlNoCache() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("GET", "/");
    //     req.setHeader("Cache-Control","no-cache");
    //     testCacheIsNotUsedWhenRespondingToRequest(req);
    // }

    // @Test
    // public void testCacheIsNotUsedWhenRespondingToRequestWithPragmaNoCache() throws Exception {
    //     final ClassicHttpRequest req = new BasicClassicHttpRequest("GET", "/");
    //     req.setHeader("Pragma","no-cache");
    //     testCacheIsNotUsedWhenRespondingToRequest(req);
    // }

    /* "When the must-revalidate directive is present in a response received
     * by a cache, that cache MUST NOT use the entry after it becomes stale
     * to respond to a subsequent request without first revalidating it with
     * the origin server. (I.e., the cache MUST do an end-to-end
     * revalidation every time, if, based solely on the origin server's
     * Expires or max-age value, the cached response is stale.)"
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4
     */
    protected void testStaleCacheResponseMustBeRevalidatedWithOrigin(
            final ClassicHttpResponse staleResponse) throws Exception {
        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");

        backendExpectsAnyRequestAndReturn(staleResponse);

        final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
        req2.setHeader("Cache-Control","max-stale=3600");
        final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();
        resp2.setHeader("ETag","\"etag2\"");
        resp2.setHeader("Cache-Control","max-age=5, must-revalidate");

        final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();
        // this request MUST happen
        EasyMock.expect(
                mockExecChain.proceed(
                        EasyMock.capture(cap),
                        EasyMock.isA(ExecChain.Scope.class))).andReturn(resp2);

        replayMocks();
        execute(req1);
        execute(req2);
        verifyMocks();

        final ClassicHttpRequest reval = cap.getValue();
        boolean foundMaxAge0 = false;
        final Iterator<HeaderElement> it = MessageSupport.iterate(reval, HttpHeaders.CACHE_CONTROL);
        while (it.hasNext()) {
            final HeaderElement elt = it.next();
            if ("max-age".equalsIgnoreCase(elt.getName())
                    && "0".equals(elt.getValue())) {
                foundMaxAge0 = true;
            }
        }
        Assert.assertTrue(foundMaxAge0);
    }

    // @Test
    // public void testStaleEntryWithMustRevalidateIsNotUsedWithoutRevalidatingWithOrigin() throws Exception {
    //     final ClassicHttpResponse response = HttpTestUtils.make200Response();
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     response.setHeader("Date",DateUtils.formatDate(tenSecondsAgo));
    //     response.setHeader("ETag","\"etag1\"");
    //     response.setHeader("Cache-Control","max-age=5, must-revalidate");

    //     testStaleCacheResponseMustBeRevalidatedWithOrigin(response);
    // }


    /* "In all circumstances an HTTP/1.1 cache MUST obey the must-revalidate
     * directive; in particular, if the cache cannot reach the origin server
     * for any reason, it MUST generate a 504 (Gateway Timeout) response."
     */
    protected void testGenerates504IfCannotRevalidateStaleResponse(
            final ClassicHttpResponse staleResponse) throws Exception {
        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");

        backendExpectsAnyRequestAndReturn(staleResponse);

        final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");

        backendExpectsAnyRequest().andThrow(new SocketTimeoutException());

        replayMocks();
        execute(req1);
        final ClassicHttpResponse result = execute(req2);
        verifyMocks();

        Assert.assertEquals(HttpStatus.SC_GATEWAY_TIMEOUT,
                            result.getCode());
    }

    // @Test
    // public void testGenerates504IfCannotRevalidateAMustRevalidateEntry() throws Exception {
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     resp1.setHeader("ETag","\"etag\"");
    //     resp1.setHeader("Date", DateUtils.formatDate(tenSecondsAgo));
    //     resp1.setHeader("Cache-Control","max-age=5,must-revalidate");

    //     testGenerates504IfCannotRevalidateStaleResponse(resp1);
    // }

    /* "The proxy-revalidate directive has the same meaning as the must-
     * revalidate directive, except that it does not apply to non-shared
     * user agent caches."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.4
     */
    // @Test
    // public void testStaleEntryWithProxyRevalidateOnSharedCacheIsNotUsedWithoutRevalidatingWithOrigin() throws Exception {
    //     if (config.isSharedCache()) {
    //         final ClassicHttpResponse response = HttpTestUtils.make200Response();
    //         final Date now = new Date();
    //         final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //         response.setHeader("Date",DateUtils.formatDate(tenSecondsAgo));
    //         response.setHeader("ETag","\"etag1\"");
    //         response.setHeader("Cache-Control","max-age=5, proxy-revalidate");

    //         testStaleCacheResponseMustBeRevalidatedWithOrigin(response);
    //     }
    // }

    // @Test
    // public void testGenerates504IfSharedCacheCannotRevalidateAProxyRevalidateEntry() throws Exception {
    //     if (config.isSharedCache()) {
    //         final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //         final Date now = new Date();
    //         final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //         resp1.setHeader("ETag","\"etag\"");
    //         resp1.setHeader("Date", DateUtils.formatDate(tenSecondsAgo));
    //         resp1.setHeader("Cache-Control","max-age=5,proxy-revalidate");

    //         testGenerates504IfCannotRevalidateStaleResponse(resp1);
    //     }
    // }

    /* "[The cache control directive] "private" Indicates that all or part of
     * the response message is intended for a single user and MUST NOT be
     * cached by a shared cache."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1
     */
    // @Test
    // public void testCacheControlPrivateIsNotCacheableBySharedCache() throws Exception {
    //    if (config.isSharedCache()) {
    //            final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //            final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //            resp1.setHeader("Cache-Control","private,max-age=3600");

    //            backendExpectsAnyRequestAndReturn(resp1);

    //            final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //            final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();
    //            // this backend request MUST happen
    //            backendExpectsAnyRequestAndReturn(resp2);

    //            replayMocks();
    //            execute(req1);
    //            execute(req2);
    //            verifyMocks();
    //    }
    // }

    // @Test
    // public void testCacheControlPrivateOnFieldIsNotReturnedBySharedCache() throws Exception {
    //    if (config.isSharedCache()) {
    //            final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //            final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //            resp1.setHeader("X-Personal","stuff");
    //            resp1.setHeader("Cache-Control","private=\"X-Personal\",s-maxage=3600");

    //            backendExpectsAnyRequestAndReturn(resp1);

    //            final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //            final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();

    //            // this backend request MAY happen
    //            backendExpectsAnyRequestAndReturn(resp2).times(0,1);

    //            replayMocks();
    //            execute(req1);
    //            final ClassicHttpResponse result = execute(req2);
    //            verifyMocks();
    //            Assert.assertNull(result.getFirstHeader("X-Personal"));
    //    }
    // }

    /* "If the no-cache directive does not specify a field-name, then a
     * cache MUST NOT use the response to satisfy a subsequent request
     * without successful revalidation with the origin server. This allows
     * an origin server to prevent caching even by caches that have been
     * configured to return stale responses to client requests."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1
     */
    // @Test
    // public void testNoCacheCannotSatisfyASubsequentRequestWithoutRevalidation() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag","\"etag\"");
    //     resp1.setHeader("Cache-Control","no-cache");

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();

    //     // this MUST happen
    //     backendExpectsAnyRequestAndReturn(resp2);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     verifyMocks();
    // }

    // @Test
    // public void testNoCacheCannotSatisfyASubsequentRequestWithoutRevalidationEvenWithContraryIndications() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag","\"etag\"");
    //     resp1.setHeader("Cache-Control","no-cache,s-maxage=3600");

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     req2.setHeader("Cache-Control","max-stale=7200");
    //     final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();

    //     // this MUST happen
    //     backendExpectsAnyRequestAndReturn(resp2);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     verifyMocks();
    // }

    /* "If the no-cache directive does specify one or more field-names, then
     * a cache MAY use the response to satisfy a subsequent request, subject
     * to any other restrictions on caching. However, the specified
     * field-name(s) MUST NOT be sent in the response to a subsequent request
     * without successful revalidation with the origin server."
     */
    // @Test
    // public void testNoCacheOnFieldIsNotReturnedWithoutRevalidation() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("ETag","\"etag\"");
    //     resp1.setHeader("X-Stuff","things");
    //     resp1.setHeader("Cache-Control","no-cache=\"X-Stuff\", max-age=3600");

    //     backendExpectsAnyRequestAndReturn(resp1);

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();
    //     resp2.setHeader("ETag","\"etag\"");
    //     resp2.setHeader("X-Stuff","things");
    //     resp2.setHeader("Cache-Control","no-cache=\"X-Stuff\",max-age=3600");

    //     final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.capture(cap),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(resp2).times(0,1);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     if (!cap.hasCaptured()) {
    //         Assert.assertNull(result.getFirstHeader("X-Stuff"));
    //     }
    // }

    /* "The purpose of the no-store directive is to prevent the inadvertent
     * release or retention of sensitive information (for example, on backup
     * tapes). The no-store directive applies to the entire message, and MAY
     * be sent either in a response or in a request. If sent in a request, a
     * cache MUST NOT store any part of either this request or any response
     * to it. If sent in a response, a cache MUST NOT store any part of
     * either this response or the request that elicited it. This directive
     * applies to both non- shared and shared caches. "MUST NOT store" in
     * this context means that the cache MUST NOT intentionally store the
     * information in non-volatile storage, and MUST make a best-effort
     * attempt to remove the information from volatile storage as promptly
     * as possible after forwarding it."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.2
     */
    // @Test
    // public void testNoStoreOnRequestIsNotStoredInCache() throws Exception {
    //     emptyMockCacheExpectsNoPuts();
    //     request.setHeader("Cache-Control","no-store");
    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     execute(request);
    //     verifyMocks();
    // }

    // @Test
    // public void testNoStoreOnRequestIsNotStoredInCacheEvenIfResponseMarkedCacheable() throws Exception {
    //     emptyMockCacheExpectsNoPuts();
    //     request.setHeader("Cache-Control","no-store");
    //     originResponse.setHeader("Cache-Control","max-age=3600");
    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     execute(request);
    //     verifyMocks();
    // }

    // @Test
    // public void testNoStoreOnResponseIsNotStoredInCache() throws Exception {
    //     emptyMockCacheExpectsNoPuts();
    //     originResponse.setHeader("Cache-Control","no-store");
    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     execute(request);
    //     verifyMocks();
    // }

    // @Test
    // public void testNoStoreOnResponseIsNotStoredInCacheEvenWithContraryIndicators() throws Exception {
    //     emptyMockCacheExpectsNoPuts();
    //     originResponse.setHeader("Cache-Control","no-store,max-age=3600");
    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     execute(request);
    //     verifyMocks();
    // }

    /* "If multiple encodings have been applied to an entity, the content
     * codings MUST be listed in the order in which they were applied."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.11
     */
    // @Test
    // public void testOrderOfMultipleContentEncodingHeaderValuesIsPreserved() throws Exception {
    //     originResponse.addHeader("Content-Encoding","gzip");
    //     originResponse.addHeader("Content-Encoding","deflate");
    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();
    //     int total_encodings = 0;
    //     final Iterator<HeaderElement> it = MessageSupport.iterate(result, HttpHeaders.CONTENT_ENCODING);
    //     while (it.hasNext()) {
    //         final HeaderElement elt = it.next();
    //         switch(total_encodings) {
    //             case 0:
    //                 Assert.assertEquals("gzip", elt.getName());
    //                 break;
    //             case 1:
    //                 Assert.assertEquals("deflate", elt.getName());
    //                 break;
    //             default:
    //                 Assert.fail("too many encodings");
    //         }
    //         total_encodings++;
    //     }
    //     Assert.assertEquals(2, total_encodings);
    // }

    // @Test
    // public void testOrderOfMultipleParametersInContentEncodingHeaderIsPreserved() throws Exception {
    //     originResponse.addHeader("Content-Encoding","gzip,deflate");
    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();
    //     int total_encodings = 0;
    //     final Iterator<HeaderElement> it = MessageSupport.iterate(result, HttpHeaders.CONTENT_ENCODING);
    //     while (it.hasNext()) {
    //         final HeaderElement elt = it.next();
    //         switch(total_encodings) {
    //             case 0:
    //                 Assert.assertEquals("gzip", elt.getName());
    //                 break;
    //             case 1:
    //                 Assert.assertEquals("deflate", elt.getName());
    //                 break;
    //             default:
    //                 Assert.fail("too many encodings");
    //         }
    //         total_encodings++;
    //     }
    //     Assert.assertEquals(2, total_encodings);
    // }

    /* "A cache cannot assume that an entity with a Content-Location
     * different from the URI used to retrieve it can be used to respond
     * to later requests on that Content-Location URI."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.14
     */
    // @Test
    // public void testCacheDoesNotAssumeContentLocationHeaderIndicatesAnotherCacheableResource() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/foo");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Cache-Control","public,max-age=3600");
    //     resp1.setHeader("Etag","\"etag\"");
    //     resp1.setHeader("Content-Location","http://foo.example.com/bar");

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/bar");
    //     final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();
    //     resp2.setHeader("Cache-Control","public,max-age=3600");
    //     resp2.setHeader("Etag","\"etag\"");

    //     backendExpectsAnyRequestAndReturn(resp1);
    //     backendExpectsAnyRequestAndReturn(resp2);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     verifyMocks();
    // }

    /* "A received message that does not have a Date header field MUST be
     * assigned one by the recipient if the message will be cached by that
     * recipient or gatewayed via a protocol which requires a Date."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.18
     */
    // @Test
    // public void testCachedResponsesWithMissingDateHeadersShouldBeAssignedOne() throws Exception {
    //     originResponse.removeHeaders("Date");
    //     originResponse.setHeader("Cache-Control","public");
    //     originResponse.setHeader("ETag","\"etag\"");

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();
    //     Assert.assertNotNull(result.getFirstHeader("Date"));
    // }

    /* "The Expires entity-header field gives the date/time after which the
     * response is considered stale.... HTTP/1.1 clients and caches MUST
     * treat other invalid date formats, especially including the value '0',
     * as in the past (i.e., 'already expired')."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.21
     */
    private void testInvalidExpiresHeaderIsTreatedAsStale(
            final String expiresHeader) throws Exception {
        final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
        final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
        resp1.setHeader("Cache-Control","public");
        resp1.setHeader("ETag","\"etag\"");
        resp1.setHeader("Expires", expiresHeader);

        final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
        final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();

        backendExpectsAnyRequestAndReturn(resp1);
        // second request to origin MUST happen
        backendExpectsAnyRequestAndReturn(resp2);

        replayMocks();
        execute(req1);
        execute(req2);
        verifyMocks();
    }

    // @Test
    // public void testMalformedExpiresHeaderIsTreatedAsStale() throws Exception {
    //     testInvalidExpiresHeaderIsTreatedAsStale("garbage");
    // }

    // @Test
    // public void testExpiresZeroHeaderIsTreatedAsStale() throws Exception {
    //     testInvalidExpiresHeaderIsTreatedAsStale("0");
    // }

    // /* "To mark a response as 'already expired,' an origin server sends
    //  * an Expires date that is equal to the Date header value."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.21
    //  */
    // @Test
    // public void testExpiresHeaderEqualToDateHeaderIsTreatedAsStale() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Cache-Control","public");
    //     resp1.setHeader("ETag","\"etag\"");
    //     resp1.setHeader("Expires", resp1.getFirstHeader("Date").getValue());

    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpResponse resp2 = HttpTestUtils.make200Response();

    //     backendExpectsAnyRequestAndReturn(resp1);
    //     // second request to origin MUST happen
    //     backendExpectsAnyRequestAndReturn(resp2);

    //     replayMocks();
    //     execute(req1);
    //     execute(req2);
    //     verifyMocks();
    // }

    /* "If the response is being forwarded through a proxy, the proxy
     * application MUST NOT modify the Server response-header."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.38
     */
    // @Test
    // public void testDoesNotModifyServerResponseHeader() throws Exception {
    //     final String server = "MockServer/1.0";
    //     originResponse.setHeader("Server", server);

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();
    //     Assert.assertEquals(server, result.getFirstHeader("Server").getValue());
    // }

    /* "If multiple encodings have been applied to an entity, the transfer-
     * codings MUST be listed in the order in which they were applied."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.41
     */
    // @Test
    // public void testOrderOfMultipleTransferEncodingHeadersIsPreserved() throws Exception {
    //     originResponse.addHeader("Transfer-Encoding","chunked");
    //     originResponse.addHeader("Transfer-Encoding","x-transfer");

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();
    //     int transfer_encodings = 0;
    //     final Iterator<HeaderElement> it = MessageSupport.iterate(result, HttpHeaders.TRANSFER_ENCODING);
    //     while (it.hasNext()) {
    //         final HeaderElement elt = it.next();
    //         switch(transfer_encodings) {
    //             case 0:
    //                 Assert.assertEquals("chunked",elt.getName());
    //                 break;
    //             case 1:
    //                 Assert.assertEquals("x-transfer",elt.getName());
    //                 break;
    //             default:
    //                 Assert.fail("too many transfer encodings");
    //         }
    //         transfer_encodings++;
    //     }
    //     Assert.assertEquals(2, transfer_encodings);
    // }

    // @Test
    // public void testOrderOfMultipleTransferEncodingsInSingleHeadersIsPreserved() throws Exception {
    //     originResponse.addHeader("Transfer-Encoding","chunked, x-transfer");

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();
    //     int transfer_encodings = 0;
    //     final Iterator<HeaderElement> it = MessageSupport.iterate(result, HttpHeaders.TRANSFER_ENCODING);
    //     while (it.hasNext()) {
    //         final HeaderElement elt = it.next();
    //         switch(transfer_encodings) {
    //             case 0:
    //                 Assert.assertEquals("chunked",elt.getName());
    //                 break;
    //             case 1:
    //                 Assert.assertEquals("x-transfer",elt.getName());
    //                 break;
    //             default:
    //                 Assert.fail("too many transfer encodings");
    //         }
    //         transfer_encodings++;
    //     }
    //     Assert.assertEquals(2, transfer_encodings);
    // }

    // /* "A Vary field value of '*' signals that unspecified parameters
    //  * not limited to the request-headers (e.g., the network address
    //  * of the client), play a role in the selection of the response
    //  * representation. The '*' value MUST NOT be generated by a proxy
    //  * server; it may only be generated by an origin server."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.44
    //  */
    // @Test
    // public void testVaryStarIsNotGeneratedByProxy() throws Exception {
    //     request.setHeader("User-Agent","my-agent/1.0");
    //     originResponse.setHeader("Cache-Control","public, max-age=3600");
    //     originResponse.setHeader("Vary","User-Agent");
    //     originResponse.setHeader("ETag","\"etag\"");

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();
    //     final Iterator<HeaderElement> it = MessageSupport.iterate(result, HttpHeaders.VARY);
    //     while (it.hasNext()) {
    //         final HeaderElement elt = it.next();
    //         Assert.assertFalse("*".equals(elt.getName()));
    //     }
    // }

    // /* "The Via general-header field MUST be used by gateways and proxies
    //  * to indicate the intermediate protocols and recipients between the
    //  * user agent and the server on requests, and between the origin server
    //  * and the client on responses."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.45
    //  */
    // @Test
    // public void testProperlyFormattedViaHeaderIsAddedToRequests() throws Exception {
    //     final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();
    //     request.removeHeaders("Via");
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.capture(cap),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();
    //     execute(request);
    //     verifyMocks();

    //     final ClassicHttpRequest captured = cap.getValue();
    //     final String via = captured.getFirstHeader("Via").getValue();
    //     assertValidViaHeader(via);
    // }

    // @Test
    // public void testProperlyFormattedViaHeaderIsAddedToResponses() throws Exception {
    //     originResponse.removeHeaders("Via");
    //     backendExpectsAnyRequest().andReturn(originResponse);
    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();
    //     assertValidViaHeader(result.getFirstHeader("Via").getValue());
    // }


    private void assertValidViaHeader(final String via) {
        //        Via =  "Via" ":" 1#( received-protocol received-by [ comment ] )
        //        received-protocol = [ protocol-name "/" ] protocol-version
        //        protocol-name     = token
        //        protocol-version  = token
        //        received-by       = ( host [ ":" port ] ) | pseudonym
        //        pseudonym         = token

        final String[] parts = via.split("\\s+");
        Assert.assertTrue(parts.length >= 2);

        // received protocol
        final String receivedProtocol = parts[0];
        final String[] protocolParts = receivedProtocol.split("/");
        Assert.assertTrue(protocolParts.length >= 1);
        Assert.assertTrue(protocolParts.length <= 2);

        final String tokenRegexp = "[^\\p{Cntrl}()<>@,;:\\\\\"/\\[\\]?={} \\t]+";
        for(final String protocolPart : protocolParts) {
            Assert.assertTrue(Pattern.matches(tokenRegexp, protocolPart));
        }

        // received-by
        if (!Pattern.matches(tokenRegexp, parts[1])) {
            // host : port
            new HttpHost(parts[1]); // TODO - unused - is this a test bug? else use Assert.assertNotNull
        }

        // comment
        if (parts.length > 2) {
            final StringBuilder buf = new StringBuilder(parts[2]);
            for(int i=3; i<parts.length; i++) {
                buf.append(" "); buf.append(parts[i]);
            }
            Assert.assertTrue(isValidComment(buf.toString()));
        }
    }

    private boolean isValidComment(final String s) {
        final String leafComment = "^\\(([^\\p{Cntrl}()]|\\\\\\p{ASCII})*\\)$";
        final String nestedPrefix = "^\\(([^\\p{Cntrl}()]|\\\\\\p{ASCII})*\\(";
        final String nestedSuffix = "\\)([^\\p{Cntrl}()]|\\\\\\p{ASCII})*\\)$";

        if (Pattern.matches(leafComment,s)) {
            return true;
        }
        final Matcher pref = Pattern.compile(nestedPrefix).matcher(s);
        final Matcher suff = Pattern.compile(nestedSuffix).matcher(s);
        if (!pref.find()) {
            return false;
        }
        if (!suff.find()) {
            return false;
        }
        return isValidComment(s.substring(pref.end() - 1, suff.start() + 1));
    }


    /*
     * "The received-protocol indicates the protocol version of the message
     * received by the server or client along each segment of the request/
     * response chain. The received-protocol version is appended to the Via
     * field value when the message is forwarded so that information about
     * the protocol capabilities of upstream applications remains visible
     * to all recipients."
     *
     * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.45
     */
    // @Test
    // public void testViaHeaderOnRequestProperlyRecordsClientProtocol() throws Exception {
    //     final ClassicHttpRequest originalRequest = new BasicClassicHttpRequest("GET", "/");
    //     originalRequest.setVersion(HttpVersion.HTTP_1_0);
    //     request = originalRequest;
    //     request.removeHeaders("Via");
    //     final Capture<ClassicHttpRequest> cap = EasyMock.newCapture();
    //     EasyMock.expect(
    //             mockExecChain.proceed(
    //                     EasyMock.capture(cap),
    //                     EasyMock.isA(ExecChain.Scope.class))).andReturn(originResponse);

    //     replayMocks();
    //     execute(request);
    //     verifyMocks();

    //     final ClassicHttpRequest captured = cap.getValue();
    //     final String via = captured.getFirstHeader("Via").getValue();
    //     final String protocol = via.split("\\s+")[0];
    //     final String[] protoParts = protocol.split("/");
    //     if (protoParts.length > 1) {
    //         Assert.assertTrue("http".equalsIgnoreCase(protoParts[0]));
    //     }
    //     Assert.assertEquals("1.0",protoParts[protoParts.length-1]);
    // }

    // @Test
    // public void testViaHeaderOnResponseProperlyRecordsOriginProtocol() throws Exception {

    //     originResponse = new BasicClassicHttpResponse(HttpStatus.SC_NO_CONTENT, "No Content");
    //     originResponse.setVersion(HttpVersion.HTTP_1_0);

    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();

    //     final String via = result.getFirstHeader("Via").getValue();
    //     final String protocol = via.split("\\s+")[0];
    //     final String[] protoParts = protocol.split("/");
    //     Assert.assertTrue(protoParts.length >= 1);
    //     Assert.assertTrue(protoParts.length <= 2);
    //     if (protoParts.length > 1) {
    //         Assert.assertTrue("http".equalsIgnoreCase(protoParts[0]));
    //     }
    //     Assert.assertEquals("1.0", protoParts[protoParts.length - 1]);
    // }

    // /* "A cache MUST NOT delete any Warning header that it received with
    //  * a message."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.46
    //  */
    // @Test
    // public void testRetainsWarningHeadersReceivedFromUpstream() throws Exception {
    //     originResponse.removeHeaders("Warning");
    //     final String warning = "199 fred \"misc\"";
    //     originResponse.addHeader("Warning", warning);
    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();
    //     Assert.assertEquals(warning,
    //             result.getFirstHeader("Warning").getValue());
    // }

    // /* "However, if a cache successfully validates a cache entry, it
    //  * SHOULD remove any Warning headers previously attached to that
    //  * entry except as specified for specific Warning codes. It MUST
    //  * then add any Warning headers received in the validating response."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.46
    //  */
    // @Test
    // public void testUpdatesWarningHeadersOnValidation() throws Exception {
    //     final ClassicHttpRequest req1 = new BasicClassicHttpRequest("GET", "/");
    //     final ClassicHttpRequest req2 = new BasicClassicHttpRequest("GET", "/");

    //     final Date now = new Date();
    //     final Date twentySecondsAgo = new Date(now.getTime() - 20 * 1000L);
    //     final ClassicHttpResponse resp1 = HttpTestUtils.make200Response();
    //     resp1.setHeader("Date", DateUtils.formatDate(twentySecondsAgo));
    //     resp1.setHeader("Cache-Control","public,max-age=5");
    //     resp1.setHeader("ETag", "\"etag1\"");
    //     final String oldWarning = "113 wilma \"stale\"";
    //     resp1.setHeader("Warning", oldWarning);

    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     final ClassicHttpResponse resp2 = new BasicClassicHttpResponse(HttpStatus.SC_NOT_MODIFIED, "Not Modified");
    //     resp2.setHeader("Date", DateUtils.formatDate(tenSecondsAgo));
    //     resp2.setHeader("ETag", "\"etag1\"");
    //     final String newWarning = "113 betty \"stale too\"";
    //     resp2.setHeader("Warning", newWarning);

    //     backendExpectsAnyRequestAndReturn(resp1);
    //     backendExpectsAnyRequestAndReturn(resp2);

    //     replayMocks();
    //     execute(req1);
    //     final ClassicHttpResponse result = execute(req2);
    //     verifyMocks();

    //     boolean oldWarningFound = false;
    //     boolean newWarningFound = false;
    //     for(final Header h : result.getHeaders("Warning")) {
    //         for(final String warnValue : h.getValue().split("\\s*,\\s*")) {
    //             if (oldWarning.equals(warnValue)) {
    //                 oldWarningFound = true;
    //             } else if (newWarning.equals(warnValue)) {
    //                 newWarningFound = true;
    //             }
    //         }
    //     }
    //     Assert.assertFalse(oldWarningFound);
    //     Assert.assertTrue(newWarningFound);
    // }

    // /* "If an implementation sends a message with one or more Warning
    //  * headers whose version is HTTP/1.0 or lower, then the sender MUST
    //  * include in each warning-value a warn-date that matches the date
    //  * in the response."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.46
    //  */
    // @Test
    // public void testWarnDatesAreAddedToWarningsOnLowerProtocolVersions() throws Exception {
    //     final String dateHdr = DateUtils.formatDate(new Date());
    //     final String origWarning = "110 fred \"stale\"";
    //     originResponse.setCode(HttpStatus.SC_OK);
    //     originResponse.setVersion(HttpVersion.HTTP_1_0);
    //     originResponse.addHeader("Warning", origWarning);
    //     originResponse.setHeader("Date", dateHdr);
    //     backendExpectsAnyRequest().andReturn(originResponse);
    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();
    //     // note that currently the implementation acts as an HTTP/1.1 proxy,
    //     // which means that all the responses from the caching module should
    //     // be HTTP/1.1, so we won't actually be testing anything here until
    //     // that changes.
    //     if (HttpVersion.HTTP_1_0.greaterEquals(result.getVersion())) {
    //         Assert.assertEquals(dateHdr, result.getFirstHeader("Date").getValue());
    //         boolean warningFound = false;
    //         final String targetWarning = origWarning + " \"" + dateHdr + "\"";
    //         for(final Header h : result.getHeaders("Warning")) {
    //             for(final String warning : h.getValue().split("\\s*,\\s*")) {
    //                 if (targetWarning.equals(warning)) {
    //                     warningFound = true;
    //                     break;
    //                 }
    //             }
    //         }
    //         Assert.assertTrue(warningFound);
    //     }
    // }

    // /* "If an implementation receives a message with a warning-value that
    //  * includes a warn-date, and that warn-date is different from the Date
    //  * value in the response, then that warning-value MUST be deleted from
    //  * the message before storing, forwarding, or using it. (This prevents
    //  * bad consequences of naive caching of Warning header fields.) If all
    //  * of the warning-values are deleted for this reason, the Warning
    //  * header MUST be deleted as well."
    //  *
    //  * http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.46
    //  */
    // @Test
    // public void testStripsBadlyDatedWarningsFromForwardedResponses() throws Exception {
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     originResponse.setHeader("Date", DateUtils.formatDate(now));
    //     originResponse.addHeader("Warning", "110 fred \"stale\", 110 wilma \"stale\" \""
    //             + DateUtils.formatDate(tenSecondsAgo) + "\"");
    //     originResponse.setHeader("Cache-Control","no-cache,no-store");
    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();

    //     for(final Header h : result.getHeaders("Warning")) {
    //         Assert.assertFalse(h.getValue().contains("wilma"));
    //     }
    // }

    // @Test
    // public void testStripsBadlyDatedWarningsFromStoredResponses() throws Exception {
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     originResponse.setHeader("Date", DateUtils.formatDate(now));
    //     originResponse.addHeader("Warning", "110 fred \"stale\", 110 wilma \"stale\" \""
    //             + DateUtils.formatDate(tenSecondsAgo) + "\"");
    //     originResponse.setHeader("Cache-Control","public,max-age=3600");
    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();

    //     for(final Header h : result.getHeaders("Warning")) {
    //         Assert.assertFalse(h.getValue().contains("wilma"));
    //     }
    // }

    // @Test
    // public void testRemovesWarningHeaderIfAllWarnValuesAreBadlyDated() throws Exception {
    //     final Date now = new Date();
    //     final Date tenSecondsAgo = new Date(now.getTime() - 10 * 1000L);
    //     originResponse.setHeader("Date", DateUtils.formatDate(now));
    //     originResponse.addHeader("Warning", "110 wilma \"stale\" \""
    //             + DateUtils.formatDate(tenSecondsAgo) + "\"");
    //     backendExpectsAnyRequest().andReturn(originResponse);

    //     replayMocks();
    //     final ClassicHttpResponse result = execute(request);
    //     verifyMocks();

    //     final Header[] warningHeaders = result.getHeaders("Warning");
    //     Assert.assertTrue(warningHeaders == null || warningHeaders.length == 0);
    // }

}
