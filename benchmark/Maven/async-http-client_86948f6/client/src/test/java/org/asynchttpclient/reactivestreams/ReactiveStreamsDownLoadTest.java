package org.asynchttpclient.reactivestreams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.asynchttpclient.Dsl.*;
import static org.testng.Assert.*;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.HttpResponseHeaders;
import org.asynchttpclient.HttpResponseStatus;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.handler.StreamedAsyncHandler;
import org.asynchttpclient.test.TestUtils;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ReactiveStreamsDownLoadTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveStreamsDownLoadTest.class);

    private int serverPort = 8080;
    private File largeFile;
    private File smallFile;

    // @BeforeClass(alwaysRun = true)
    // public void setUpBeforeTest() throws Exception {
    //     largeFile = TestUtils.createTempFile(15 * 1024);
    //     smallFile = TestUtils.createTempFile(20);
    //     HttpStaticFileServer.start(serverPort);
    // }

    // @AfterClass(alwaysRun = true)
    // public void tearDown() throws Exception {
    //     HttpStaticFileServer.shutdown();
    // }

    // @Test(groups = "standalone")
    // public void streamedResponseLargeFileTest() throws Throwable {
    //     try (AsyncHttpClient c = asyncHttpClient()) {
    //         String largeFileName = "http://localhost:" + serverPort + "/" + largeFile.getName();
    //         ListenableFuture<SimpleStreamedAsyncHandler> future = c.prepareGet(largeFileName).execute(new SimpleStreamedAsyncHandler());
    //         byte[] result = future.get().getBytes();
    //         assertEquals(result.length, largeFile.length());
    //     }
    // }

    // @Test(groups = "standalone")
    // public void streamedResponseSmallFileTest() throws Throwable {
    //     try (AsyncHttpClient c = asyncHttpClient()) {
    //         String smallFileName = "http://localhost:" + serverPort + "/" + smallFile.getName();
    //         ListenableFuture<SimpleStreamedAsyncHandler> future = c.prepareGet(smallFileName).execute(new SimpleStreamedAsyncHandler());
    //         byte[] result = future.get().getBytes();
    //         LOGGER.debug("Result file size: " + result.length);
    //         assertEquals(result.length, smallFile.length());
    //     }
    // }

    static protected class SimpleStreamedAsyncHandler implements StreamedAsyncHandler<SimpleStreamedAsyncHandler> {
        private final SimpleSubscriber<HttpResponseBodyPart> subscriber;

        public SimpleStreamedAsyncHandler() {
            this(new SimpleSubscriber<HttpResponseBodyPart>());
        }

        public SimpleStreamedAsyncHandler(SimpleSubscriber<HttpResponseBodyPart> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public State onStream(Publisher<HttpResponseBodyPart> publisher) {
            LOGGER.debug("SimpleStreamedAsyncHandleronCompleted onStream");
            publisher.subscribe(subscriber);
            return State.CONTINUE;
        }

        @Override
        public void onThrowable(Throwable t) {
            throw new AssertionError(t);
        }

        @Override
        public State onBodyPartReceived(HttpResponseBodyPart bodyPart) throws Exception {
            LOGGER.debug("SimpleStreamedAsyncHandleronCompleted onBodyPartReceived");
            throw new AssertionError("Should not have received body part");
        }

        @Override
        public State onStatusReceived(HttpResponseStatus responseStatus) throws Exception {
            return State.CONTINUE;
        }

        @Override
        public State onHeadersReceived(HttpResponseHeaders headers) throws Exception {
            return State.CONTINUE;
        }

        @Override
        public SimpleStreamedAsyncHandler onCompleted() throws Exception {
            LOGGER.debug("SimpleStreamedAsyncHandleronCompleted onSubscribe");
            return this;
        }

        public byte[] getBytes() throws Throwable {
            List<HttpResponseBodyPart> bodyParts = subscriber.getElements();
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            for (HttpResponseBodyPart part : bodyParts) {
                bytes.write(part.getBodyPartBytes());
            }
            return bytes.toByteArray();
        }
    }

    /**
     * Simple subscriber that requests and buffers one element at a time.
     */
    static protected class SimpleSubscriber<T> implements Subscriber<T> {
        private volatile Subscription subscription;
        private volatile Throwable error;
        private final List<T> elements = Collections.synchronizedList(new ArrayList<T>());
        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public void onSubscribe(Subscription subscription) {
            LOGGER.debug("SimpleSubscriber onSubscribe");
            this.subscription = subscription;
            subscription.request(1);
        }

        @Override
        public void onNext(T t) {
            LOGGER.debug("SimpleSubscriber onNext");
            elements.add(t);
            subscription.request(1);
        }

        @Override
        public void onError(Throwable error) {
            LOGGER.error("SimpleSubscriber onError");
            this.error = error;
            latch.countDown();
        }

        @Override
        public void onComplete() {
            LOGGER.debug("SimpleSubscriber onComplete");
            latch.countDown();
        }

        public List<T> getElements() throws Throwable {
            latch.await();
            if (error != null) {
                throw error;
            } else {
                return elements;
            }
        }
    }
}
