/*
 * Copyright 2010 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.asynchttpclient;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.asynchttpclient.Dsl.config;
import static org.asynchttpclient.test.TestUtils.*;
import static org.testng.Assert.*;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.net.ssl.SSLHandshakeException;
import javax.servlet.http.HttpServletResponse;

import org.asynchttpclient.channel.KeepAliveStrategy;
import org.asynchttpclient.test.EventCollectingHandler;
import org.asynchttpclient.testserver.HttpServer;
import org.asynchttpclient.testserver.HttpTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BasicHttpsTest extends HttpTest {

    private static HttpServer server;

    // @BeforeClass
    // public static void start() throws Throwable {
    //     server = new HttpServer();
    //     server.start();
    // }

    // @AfterClass
    // public static void stop() throws Throwable {
    //     server.close();
    // }

    private static String getTargetUrl() {
        return server.getHttpsUrl() + "/foo/bar";
    }

    // @Test
    // public void postBodyOverHttps() throws Throwable {
    //     withClient(config().setSslEngineFactory(createSslEngineFactory())).run(client -> {
    //         withServer(server).run(server -> {
    //             server.enqueueEcho();

    //             Response resp = client.preparePost(getTargetUrl()).setBody(SIMPLE_TEXT_FILE).setHeader(CONTENT_TYPE, "text/html").execute().get();
    //             assertNotNull(resp);
    //             assertEquals(resp.getStatusCode(), HttpServletResponse.SC_OK);
    //             assertEquals(resp.getResponseBody(), SIMPLE_TEXT_FILE_STRING);
    //         });
    //     });
    // }

    // @Test
    // public void multipleSequentialPostRequestsOverHttps() throws Throwable {
    //     withClient(config().setSslEngineFactory(createSslEngineFactory())).run(client -> {
    //         withServer(server).run(server -> {
    //             server.enqueueEcho();
    //             server.enqueueEcho();

    //             String body = "hello there";

    //             Response response = client.preparePost(getTargetUrl()).setBody(body).setHeader(CONTENT_TYPE, "text/html").execute().get(TIMEOUT, SECONDS);
    //             assertEquals(response.getResponseBody(), body);

    //             response = client.preparePost(getTargetUrl()).setBody(body).setHeader(CONTENT_TYPE, "text/html").execute().get(TIMEOUT, SECONDS);
    //             assertEquals(response.getResponseBody(), body);
    //         });
    //     });
    // }

    // @Test
    // public void multipleConcurrentPostRequestsOverHttpsWithDisabledKeepAliveStrategy() throws Throwable {

    //     KeepAliveStrategy keepAliveStrategy = new KeepAliveStrategy() {
    //         @Override
    //         public boolean keepAlive(Request ahcRequest, HttpRequest nettyRequest, HttpResponse nettyResponse) {
    //             return !ahcRequest.getUri().isSecured();
    //         }
    //     };

    //     withClient(config().setSslEngineFactory(createSslEngineFactory()).setKeepAliveStrategy(keepAliveStrategy)).run(client -> {
    //         withServer(server).run(server -> {
    //             server.enqueueEcho();
    //             server.enqueueEcho();
    //             server.enqueueEcho();

    //             String body = "hello there";

    //             client.preparePost(getTargetUrl()).setBody(body).setHeader(CONTENT_TYPE, "text/html").execute();
    //             client.preparePost(getTargetUrl()).setBody(body).setHeader(CONTENT_TYPE, "text/html").execute();

    //             Response response = client.preparePost(getTargetUrl()).setBody(body).setHeader(CONTENT_TYPE, "text/html").execute().get();
    //             assertEquals(response.getResponseBody(), body);
    //         });
    //     });
    // }

    // @Test
    // public void reconnectAfterFailedCertificationPath() throws Throwable {

    //     AtomicBoolean trust = new AtomicBoolean();

    //     withClient(config().setSslEngineFactory(createSslEngineFactory(trust))).run(client -> {
    //         withServer(server).run(server -> {
    //             server.enqueueEcho();
    //             server.enqueueEcho();

    //             String body = "hello there";

    //             // first request fails because server certificate is rejected
    //                 Throwable cause = null;
    //                 try {
    //                     client.preparePost(getTargetUrl()).setBody(body).setHeader(CONTENT_TYPE, "text/html").execute().get(TIMEOUT, SECONDS);
    //                 } catch (final ExecutionException e) {
    //                     cause = e.getCause();
    //                 }
    //                 assertNotNull(cause);

    //                 // second request should succeed
    //                 trust.set(true);
    //                 Response response = client.preparePost(getTargetUrl()).setBody(body).setHeader(CONTENT_TYPE, "text/html").execute().get(TIMEOUT, SECONDS);

    //                 assertEquals(response.getResponseBody(), body);
    //             });
    //     });
    // }

    // @Test(timeOut = 2000, expectedExceptions = SSLHandshakeException.class)
    // public void failInstantlyIfNotAllowedSelfSignedCertificate() throws Throwable {
    //     withClient(config().setRequestTimeout(2000)).run(client -> {
    //         withServer(server).run(server -> {
    //             server.enqueueEcho();
    //             try {
    //                 client.prepareGet(getTargetUrl()).execute().get(TIMEOUT, SECONDS);
    //             } catch (ExecutionException e) {
    //                 throw e.getCause().getCause();
    //             }
    //         });
    //     });
    // }

    // @Test(groups = "standalone")
    // public void testNormalEventsFired() throws Throwable {
    //     withClient(config().setSslEngineFactory(createSslEngineFactory())).run(client -> {
    //         withServer(server).run(server -> {
    //             EventCollectingHandler handler = new EventCollectingHandler();

    //             server.enqueueEcho();
    //             client.preparePost(getTargetUrl()).setBody("whatever").execute(handler).get(3, SECONDS);
    //             handler.waitForCompletion(3, SECONDS);

    //             Object[] expectedEvents = new Object[] { //
    //             CONNECTION_POOL_EVENT,//
    //                     HOSTNAME_RESOLUTION_EVENT,//
    //                     HOSTNAME_RESOLUTION_SUCCESS_EVENT,//
    //                     CONNECTION_OPEN_EVENT,//
    //                     CONNECTION_SUCCESS_EVENT,//
    //                     TLS_HANDSHAKE_EVENT,//
    //                     TLS_HANDSHAKE_SUCCESS_EVENT,//
    //                     REQUEST_SEND_EVENT,//
    //                     HEADERS_WRITTEN_EVENT,//
    //                     STATUS_RECEIVED_EVENT,//
    //                     HEADERS_RECEIVED_EVENT,//
    //                     CONNECTION_OFFER_EVENT,//
    //                     COMPLETED_EVENT };

    //             assertEquals(handler.firedEvents.toArray(), expectedEvents, "Got " + Arrays.toString(handler.firedEvents.toArray()));
    //         });
    //     });
    // }
}
