package com.pex.image;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.pex.image.input.InputWorkHandler;
import com.pex.image.input.InputDisruptor;
import com.pex.image.input.InputEvent;
import com.pex.image.output.OutputDisruptor;
import com.pex.image.output.OutputEvent;
import com.pex.image.http.HttpAsyncImageCallback;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

import static com.pex.image.Util.countLines;
import static com.pex.image.http.HttpUtil.createClient;

public class App {
    private static final Logger logger = LoggerFactory.getLogger( App.class );

    public static void main(String[] args) throws IOException, InterruptedException {
        final CloseableHttpAsyncClient client = createClient();

        final File inputFile = new File(System.getProperty("input"));
        final File outputFile = new File(System.getProperty("output"));

        final CountDownLatch latch = new CountDownLatch(countLines(inputFile));

        final InputDisruptor inputDisruptor = new InputDisruptor(latch);
        final OutputDisruptor outputDisruptor = new OutputDisruptor(outputFile,latch);

        final RingBuffer<OutputEvent> outputBuffer = outputDisruptor.getDisruptor().getRingBuffer();

        WorkHandler<InputEvent>[] workHandlers = new WorkHandler[Runtime.getRuntime().availableProcessors()];

        for(int i = 0; i < workHandlers.length; i++) {
            workHandlers[i] = new InputWorkHandler(outputBuffer,latch);
        }

        final RingBuffer<InputEvent> inputBuffer = inputDisruptor.getDisruptor(workHandlers).getRingBuffer();

        HttpGet request;
        int imageId = 0;
        try(BufferedReader br = new BufferedReader(new InputStreamReader(FileUtils.openInputStream(inputFile)))) {
            for(String line; (line = br.readLine()) != null; ) {
                long sequence = inputBuffer.next();
                MDC.put("image-id",String.valueOf(imageId++));
                request = new HttpGet(line);

                client.execute(request,
                        new HttpAsyncImageCallback(request.getURI().toString(),latch,inputBuffer,sequence));
            }
            latch.await();
        } catch(InterruptedException e) {
            //
        } finally {
            client.close();
        }
        System.exit(0);
    }
}
