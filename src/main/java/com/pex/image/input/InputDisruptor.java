package com.pex.image.input;

import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by rhernandez on 11/29/17.
 */
public class InputDisruptor {
    private final CountDownLatch latch;
    private Disruptor<InputEvent> disruptor;

    public InputDisruptor(CountDownLatch latch) {
        this.latch = latch;
    }

    public Disruptor<InputEvent> getDisruptor(WorkHandler<InputEvent>[] workHandlers) {
        if(disruptor ==  null) {
            int bufferSize = Integer.parseInt(System.getProperty("inputBufferSize"));

            Executor executor = new ThreadPoolExecutor(0, 5,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
            Disruptor<InputEvent> newDisruptor =
                    new Disruptor<>(InputEvent::new, bufferSize, executor);
            newDisruptor.handleEventsWithWorkerPool(workHandlers);
            newDisruptor.start();

            disruptor = newDisruptor;
        }

        return disruptor;
    }
}
