/*
 * Copyright (c) 2010-2012 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package org.asynchttpclient;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.asynchttpclient.Dsl.*;
import static org.asynchttpclient.test.TestUtils.*;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class NonAsciiContentLengthTest extends AbstractBasicTest {

    // @BeforeClass(alwaysRun = true)
    // public void setUpGlobal() throws Exception {
    //     port1 = findFreePort();
    //     server = newJettyHttpServer(port1);
    //     server.setHandler(new AbstractHandler() {

    //         public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    //             int MAX_BODY_SIZE = 1024; // Can only handle bodies of up to 1024 bytes.
    //             byte[] b = new byte[MAX_BODY_SIZE];
    //             int offset = 0;
    //             int numBytesRead;
    //             try (ServletInputStream is = request.getInputStream()) {
    //                 while ((numBytesRead = is.read(b, offset, MAX_BODY_SIZE - offset)) != -1) {
    //                     offset += numBytesRead;
    //                 }
    //             }
    //             assertEquals(request.getContentLength(), offset);
    //             response.setStatus(200);
    //             response.setCharacterEncoding(request.getCharacterEncoding());
    //             response.setContentLength(request.getContentLength());
    //             try (ServletOutputStream os = response.getOutputStream()) {
    //                 os.write(b, 0, offset);
    //             }
    //         }
    //     });
    //     server.start();
    // }

    // @Test(groups = "standalone")
    // public void testNonAsciiContentLength() throws Exception {
    //     execute("test");
    //     execute("\u4E00"); // Unicode CJK ideograph for one
    // }

    protected void execute(String body) throws IOException, InterruptedException, ExecutionException {
        try (AsyncHttpClient client = asyncHttpClient()) {
            BoundRequestBuilder r = client.preparePost(getTargetUrl()).setBody(body).setCharset(UTF_8);
            Future<Response> f = r.execute();
            Response resp = f.get();
            assertEquals(resp.getStatusCode(), 200);
            assertEquals(body, resp.getResponseBody(UTF_8));
        }
    }
}
