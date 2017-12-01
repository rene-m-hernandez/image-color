package com.pex.image.input;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.pex.image.processor.ColorFrequency;
import com.pex.image.output.OutputEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import static java.lang.String.format;

/**
 * Created by rhernandez on 11/29/17.
 */
public class InputWorkHandler implements WorkHandler<InputEvent> {
    private static final Logger logger = LoggerFactory.getLogger(InputWorkHandler.class);

    private final RingBuffer<OutputEvent> outputBuffer;
    private final CountDownLatch latch;

    public InputWorkHandler(RingBuffer<OutputEvent> outputBuffer, CountDownLatch latch) {
        this.outputBuffer = outputBuffer;
        this.latch = latch;
    }

    @Override
    public void onEvent(InputEvent event) {
        MDC.setContextMap(event.getContextMap());
        String imageUrl = event.getImageUrl();
        long outputSequence = outputBuffer.next();
        try {
            logger.trace("Processing image");

            InputStream inputStream = event.getInputStream();
            ColorFrequency colorFrequency = new ColorFrequency(inputStream);
            int[] dominantColors = colorFrequency.frequencies();

            OutputEvent outputEvent = outputBuffer.get(outputSequence);
            outputEvent.setOutput(
                    format("%s;%06X,%06X,%06X%n", imageUrl, dominantColors[0], dominantColors[1], dominantColors[2]));
            outputEvent.setContextMap(MDC.getCopyOfContextMap());
        } catch(IOException e) {
            logger.error("Error processing image {}: ", imageUrl,e);
            latch.countDown();
        } finally {
            event = new InputEvent();
            outputBuffer.publish(outputSequence);
            MDC.clear();
        }
    }
}
