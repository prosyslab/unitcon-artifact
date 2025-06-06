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
package org.apache.hc.core5.http.examples;

import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpConnection;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Message;
import org.apache.hc.core5.http.impl.Http1StreamListener;
import org.apache.hc.core5.http.impl.bootstrap.AsyncRequesterBootstrap;
import org.apache.hc.core5.http.impl.bootstrap.HttpAsyncRequester;
import org.apache.hc.core5.http.message.RequestLine;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.http.nio.entity.StringAsyncEntityConsumer;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.apache.hc.core5.http.nio.support.BasicResponseConsumer;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.http.support.BasicRequestBuilder;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.Timeout;

/**
 * Example of SNI (Server Name Identification) usage with async I/O.
 */
public class AsyncClientSNIExample {

    public static void main(final String[] args) throws Exception {

        final IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(5, TimeUnit.SECONDS)
                .build();

        // Create and start requester
        final HttpAsyncRequester requester = AsyncRequesterBootstrap.bootstrap()
                .setIOReactorConfig(ioReactorConfig)
                .setStreamListener(new Http1StreamListener() {

                    @Override
                    public void onRequestHead(final HttpConnection connection, final HttpRequest request) {
                        System.out.println(connection.getRemoteAddress() + " " + new RequestLine(request));
                    }

                    @Override
                    public void onResponseHead(final HttpConnection connection, final HttpResponse response) {
                        System.out.println(connection.getRemoteAddress() + " " + new StatusLine(response));
                    }

                    @Override
                    public void onExchangeComplete(final HttpConnection connection, final boolean keepAlive) {
                        if (keepAlive) {
                            System.out.println(connection.getRemoteAddress() + " exchange completed (connection kept alive)");
                        } else {
                            System.out.println(connection.getRemoteAddress() + " exchange completed (connection closed)");
                        }
                    }

                })
                .create();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("HTTP requester shutting down");
            requester.close(CloseMode.GRACEFUL);
        }));
        requester.start();

        // Physical endpoint (google.com)
        final InetAddress targetAddress = InetAddress.getByName("www.google.com");
        // Target host (google.ch)
        final HttpHost target = new HttpHost("https", targetAddress, "www.google.ch", 443);
        final HttpCoreContext coreContext = HttpCoreContext.create();

        final CountDownLatch latch = new CountDownLatch(1);
        final HttpRequest request = BasicRequestBuilder.get()
                .setPath("/")
                .build();
        requester.execute(
                target,
                new BasicRequestProducer(request, null),
                new BasicResponseConsumer<>(new StringAsyncEntityConsumer()),
                null,
                Timeout.ofSeconds(5),
                coreContext,
                new FutureCallback<Message<HttpResponse, String>>() {

                    @Override
                    public void completed(final Message<HttpResponse, String> message) {
                        final HttpResponse response = message.getHead();
                        System.out.println(request.getRequestUri() + "->" + response.getCode());
                        final SSLSession sslSession = coreContext.getSSLSession();
                        if (sslSession != null) {
                            try {
                                System.out.println("Peer: " + sslSession.getPeerPrincipal());
                                System.out.println("TLS protocol: " + sslSession.getProtocol());
                                System.out.println("TLS cipher suite: " + sslSession.getCipherSuite());
                            } catch (final SSLPeerUnverifiedException ignore) {
                            }
                        }
                        latch.countDown();
                    }

                    @Override
                    public void failed(final Exception ex) {
                        System.out.println(request.getRequestUri() + "->" + ex);
                        latch.countDown();
                    }

                    @Override
                    public void cancelled() {
                        System.out.println(request.getRequestUri() + " cancelled");
                        latch.countDown();
                    }

                });

        latch.await();
        System.out.println("Shutting down I/O reactor");
        requester.initiateShutdown();
    }

}
