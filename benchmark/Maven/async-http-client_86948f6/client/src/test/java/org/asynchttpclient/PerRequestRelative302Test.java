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

import static org.asynchttpclient.Dsl.*;
import static org.asynchttpclient.test.TestUtils.*;
import static org.testng.Assert.*;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.asynchttpclient.uri.Uri;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class PerRequestRelative302Test extends AbstractBasicTest {

 // FIXME super NOT threadsafe!!!
    private final AtomicBoolean isSet = new AtomicBoolean(false);

    private class Relative302Handler extends AbstractHandler {

        public void handle(String s, Request r, HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException, ServletException {

            String param;
            httpResponse.setContentType(TEXT_HTML_CONTENT_TYPE_WITH_UTF_8_CHARSET);
            Enumeration<?> e = httpRequest.getHeaderNames();
            while (e.hasMoreElements()) {
                param = e.nextElement().toString();

                if (param.startsWith("X-redirect") && !isSet.getAndSet(true)) {
                    httpResponse.addHeader("Location", httpRequest.getHeader(param));
                    httpResponse.setStatus(302);
                    httpResponse.getOutputStream().flush();
                    httpResponse.getOutputStream().close();
                    return;
                }
            }
            httpResponse.setStatus(200);
            httpResponse.getOutputStream().flush();
            httpResponse.getOutputStream().close();
        }
    }

    // @BeforeClass(alwaysRun = true)
    // public void setUpGlobal() throws Exception {
    //     port1 = findFreePort();
    //     port2 = findFreePort();
    //     server = newJettyHttpServer(port1);

    //     server.setHandler(new Relative302Handler());
    //     server.start();
    //     logger.info("Local HTTP server started successfully");
    // }

    // @Test(groups = "online")
    // // FIXME threadsafe
    // public void runAllSequentiallyBecauseNotThreadSafe() throws Exception {
    //     redirected302Test();
    //     notRedirected302Test();
    //     relativeLocationUrl();
    //     redirected302InvalidTest();
    // }

    // // @Test(groups = "online")
    // public void redirected302Test() throws Exception {
    //     isSet.getAndSet(false);
    //     try (AsyncHttpClient c = asyncHttpClient()) {
    //         Response response = c.prepareGet(getTargetUrl()).setFollowRedirect(true).setHeader("X-redirect", "http://www.microsoft.com/").execute().get();

    //         assertNotNull(response);
    //         assertEquals(response.getStatusCode(), 200);

    //         String anyMicrosoftPage = "http://www.microsoft.com[^:]*:80";
    //         String baseUrl = getBaseUrl(response.getUri());

    //         assertTrue(baseUrl.matches(anyMicrosoftPage), "response does not show redirection to " + anyMicrosoftPage);
    //     }
    // }

    // // @Test(groups = "online")
    // public void notRedirected302Test() throws Exception {
    //     isSet.getAndSet(false);
    //     try (AsyncHttpClient c = asyncHttpClient(config().setFollowRedirect(true))) {
    //         Response response = c.prepareGet(getTargetUrl()).setFollowRedirect(false).setHeader("X-redirect", "http://www.microsoft.com/").execute().get();
    //         assertNotNull(response);
    //         assertEquals(response.getStatusCode(), 302);
    //     }
    // }

    private String getBaseUrl(Uri uri) {
        String url = uri.toString();
        int port = uri.getPort();
        if (port == -1) {
            port = getPort(uri);
            url = url.substring(0, url.length() - 1) + ":" + port;
        }
        return url.substring(0, url.lastIndexOf(":") + String.valueOf(port).length() + 1);
    }

    private static int getPort(Uri uri) {
        int port = uri.getPort();
        if (port == -1)
            port = uri.getScheme().equals("http") ? 80 : 443;
        return port;
    }

    // // @Test(groups = "standalone")
    // public void redirected302InvalidTest() throws Exception {
    //     isSet.getAndSet(false);
    //     try (AsyncHttpClient c = asyncHttpClient()) {
    //         // If the test hit a proxy, no ConnectException will be thrown and instead of 404 will be returned.
    //         Response response = c.preparePost(getTargetUrl()).setFollowRedirect(true).setHeader("X-redirect", String.format("http://localhost:%d/", port2)).execute().get();

    //         assertNotNull(response);
    //         assertEquals(response.getStatusCode(), 404);
    //     } catch (ExecutionException ex) {
    //         assertEquals(ex.getCause().getClass(), ConnectException.class);
    //     }
    // }

    // // @Test(groups = "standalone")
    // public void relativeLocationUrl() throws Exception {
    //     isSet.getAndSet(false);

    //     try (AsyncHttpClient c = asyncHttpClient()) {
    //         Response response = c.preparePost(getTargetUrl()).setFollowRedirect(true).setHeader("X-redirect", "/foo/test").execute().get();
    //         assertNotNull(response);
    //         assertEquals(response.getStatusCode(), 200);
    //         assertEquals(response.getUri().toString(), getTargetUrl());
    //     }
    // }
}
