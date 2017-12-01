package com.pex.image.output;

import com.lmax.disruptor.dsl.Disruptor;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

/**
 * Created by rhernandez on 11/30/17.
 */
public class OutputDisruptor {
    private final CountDownLatch latch;
    private final File outputFile;
    private Disruptor<OutputEvent> disruptor;

    public OutputDisruptor(File outputFile, CountDownLatch latch) {
        this.latch = latch;
        this.outputFile = outputFile;
    }

    public Disruptor<OutputEvent> getDisruptor() {
        if(disruptor ==  null) {
            int bufferSize = Integer.parseInt(System.getProperty("outputBufferSize"));

            Disruptor<OutputEvent> newDisruptor =
                    new Disruptor<>(OutputEvent::new, bufferSize, Executors.newSingleThreadExecutor());
            newDisruptor.handleEventsWith(new OutputEventHandler(outputFile,latch));
            newDisruptor.start();

            disruptor = newDisruptor;
        }

        return disruptor;
    }
}
