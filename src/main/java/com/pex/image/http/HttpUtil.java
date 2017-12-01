package com.pex.image.http;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;

/**
 * Created by rhernandez on 11/29/17.
 */
public class HttpUtil {
    public static CloseableHttpAsyncClient createClient() throws IOReactorException {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setIoThreadCount( Runtime.getRuntime().availableProcessors() )
                .setConnectTimeout( 1000 )
                .setSoTimeout( 10000 )
                .build();

        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor( ioReactorConfig );

        SSLContext sslContext = SSLContexts.createSystemDefault();

        Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder.<SchemeIOSessionStrategy>create()
                .register( "http", NoopIOSessionStrategy.INSTANCE )
                .register( "https", new SSLIOSessionStrategy(sslContext))
                .build();

        PoolingNHttpClientConnectionManager manager =
                new PoolingNHttpClientConnectionManager(ioReactor,sessionStrategyRegistry );
        manager.setMaxTotal(20);
        manager.setDefaultMaxPerRoute(5);

        CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setConnectionManager( manager )
                .build();

        client.start();

        return client;
    }
}
