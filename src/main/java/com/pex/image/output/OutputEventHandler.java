package com.pex.image.output;

import com.lmax.disruptor.EventHandler;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

/**
 * Created by rhernandez on 11/30/17.
 */
public class OutputEventHandler implements EventHandler<OutputEvent> {
    private static final Logger logger = LoggerFactory.getLogger(OutputEventHandler.class);
    private final File outputFile;
    private final CountDownLatch latch;

    public OutputEventHandler(File outputFile, CountDownLatch latch) {
        this.outputFile = outputFile;
        this.latch = latch;
    }

    @Override
    public void onEvent(OutputEvent event, long sequence, boolean endOfBatch) {
        MDC.setContextMap(event.getContextMap());
        try {
            logger.trace("Writing image to file");
            FileUtils.writeStringToFile(outputFile, event.getOutput(), Charset.defaultCharset(),true);
        } catch(IOException e) {
            logger.error("Error writing output to file: ", e);
        } finally {
            event = new OutputEvent();
            latch.countDown();
            MDC.clear();
        }
    }
}
