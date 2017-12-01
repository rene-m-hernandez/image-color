package com.pex.image.http;

import com.lmax.disruptor.RingBuffer;
import com.pex.image.input.InputEvent;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by rhernandez on 11/29/17.
 */

public class HttpAsyncImageCallback implements FutureCallback<HttpResponse> {
    private Logger logger = LoggerFactory.getLogger(HttpAsyncImageCallback.class);
    private final String imageUrl;
    private final CountDownLatch latch;
    private final RingBuffer<InputEvent> ringBuffer;
    private final long sequence;
    private final Map contextMap;

    public HttpAsyncImageCallback(String imageUrl, CountDownLatch latch, RingBuffer<InputEvent> ringBuffer, long sequence) {
        this.imageUrl = imageUrl;
        this.latch = latch;
        this.ringBuffer = ringBuffer;
        this.sequence = sequence;
        this.contextMap = MDC.getCopyOfContextMap();
    }

    @Override
    public void completed(HttpResponse result) {
        MDC.setContextMap(contextMap);
        logger.trace("Fetched: {}", imageUrl);

        InputEvent event = ringBuffer.get(sequence);
        try {
            event.setImageUrl(imageUrl);
            event.setInputStream(
                    new ByteArrayInputStream(IOUtils.toByteArray(result.getEntity().getContent())));
            event.setContextMap(contextMap);
        } catch(IOException e) {
            logger.error("Error saving image: {}", e.getMessage());
            latch.countDown();
        }

        ringBuffer.publish(sequence);
        MDC.clear();
    }

    @Override
    public void failed(Exception ex) {
        MDC.setContextMap(contextMap);
        logger.error("Error fetching image: {}", ex);
        latch.countDown();
        MDC.clear();
    }

    @Override
    public void cancelled() {
        MDC.setContextMap(contextMap);
        logger.warn("Cancelled");
        latch.countDown();
        MDC.clear();
    }
}