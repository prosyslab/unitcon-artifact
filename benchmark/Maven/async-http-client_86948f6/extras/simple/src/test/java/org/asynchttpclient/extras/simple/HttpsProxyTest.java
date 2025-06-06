package org.asynchttpclient.extras.simple;

import static org.asynchttpclient.test.TestUtils.*;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.asynchttpclient.AbstractBasicTest;
import org.asynchttpclient.Response;
import org.asynchttpclient.test.EchoHandler;
import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HttpsProxyTest extends AbstractBasicTest {

    private Server server2;

    public AbstractHandler configureHandler() throws Exception {
        return new ConnectHandler();
    }

    // @BeforeClass(alwaysRun = true)
    // public void setUpGlobal() throws Exception {
    //     port1 = findFreePort();
    //     server = newJettyHttpServer(port1);
    //     server.setHandler(configureHandler());
    //     server.start();

    //     port2 = findFreePort();

    //     server2 = newJettyHttpsServer(port2);
    //     server2.setHandler(new EchoHandler());
    //     server2.start();

    //     logger.info("Local HTTP server started successfully");
    // }

    // @AfterClass(alwaysRun = true)
    // public void tearDownGlobal() throws Exception {
    //     server.stop();
    //     server2.stop();
    // }

    // @Test(groups = "online")
    // public void testSimpleAHCConfigProxy() throws IOException, InterruptedException, ExecutionException, TimeoutException {

    //     try (SimpleAsyncHttpClient client = new SimpleAsyncHttpClient.Builder()//
    //             .setProxyHost("localhost")//
    //             .setProxyPort(port1)//
    //             .setFollowRedirect(true)//
    //             .setUrl(getTargetUrl2())//
    //             .setAcceptAnyCertificate(true)//
    //             .setHeader("Content-Type", "text/html")//
    //             .build()) {
    //         Response r = client.get().get();

    //         assertEquals(r.getStatusCode(), 200);
    //     }
    // }
}
