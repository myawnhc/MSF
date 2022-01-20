/*
 * Copyright 2018-2022 Hazelcast, Inc
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.hazelcast.jet.contrib.grpc.impl;

import com.hazelcast.function.FunctionEx;
import com.hazelcast.function.SupplierEx;
import com.hazelcast.internal.util.ExceptionUtil;
import com.hazelcast.jet.core.Processor;
import com.hazelcast.jet.pipeline.SourceBuilder.SourceBuffer;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

//import io.undertow.Undertow;
//import io.undertow.server.HttpServerExchange;
//import io.undertow.server.handlers.AllowedMethodsHandler;
//import io.undertow.server.handlers.ExceptionHandler;
//import io.undertow.util.StatusCodes;
//import org.xnio.SslClientAuthMode;

//import static io.undertow.Handlers.exceptionHandler;
//import static io.undertow.Handlers.path;
//import static io.undertow.UndertowOptions.ENABLE_HTTP2;
//import static io.undertow.util.Methods.POST;
//import static io.undertow.util.Methods.PUT;
//import static org.xnio.Options.SSL_CLIENT_AUTH_MODE;

public class GrpcListenerSourceContext<T> {

    private final BlockingQueue<T> queue = new ArrayBlockingQueue<>(1024);
    private final List<T> buffer = new ArrayList<>(1024);
    //private final Undertow undertow;
    private final FunctionEx<byte[], T> mapToItemFn;

    private static final Logger logger = Logger.getLogger(GrpcListenerSourceContext.class.getName());

    private Server server;
    //private BindableService serviceImpl;

    public void start(int port, BindableService serviceImpl) throws IOException {
       // ServiceConfig.ServiceProperties props = ServiceConfig.get("account-service");

        /* The port on which the server should run */
//        int port = props.getGrpcPort();
        server = ServerBuilder.forPort(port)
                .addService(serviceImpl)
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    GrpcListenerSourceContext.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    public GrpcListenerSourceContext(
            @Nonnull Processor.Context context,
            int port,
//            boolean mutualAuthentication,
            @Nonnull SupplierEx<String> hostFn,
//            @Nullable SupplierEx<SSLContext> sslContextFn,
            @Nonnull FunctionEx<byte[], T> mapToItemFn,
            @Nonnull BindableService serviceImpl
    ) {
        //this.logger = context.logger();
        this.mapToItemFn = mapToItemFn;
//        String host = hostFn.get();
        try {
            this.start(port, serviceImpl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // TODO: Start gRPC server here instead of http[s]
//        Undertow.Builder builder = Undertow.builder();
//        if (sslContextFn != null) {
//            if (mutualAuthentication) {
//                builder.setServerOption(SSL_CLIENT_AUTH_MODE, SslClientAuthMode.REQUIRED);
//            }
//            builder.addHttpsListener(port, host, sslContextFn.get());
//            logger.info("Starting to listen HTTPS messages on https://" + host + ":" + port);
//        } else {
//            builder.addHttpListener(port, host);
//            logger.info("Starting to listen HTTP messages on http://" + host + ":" + port);
//        }
//
//        undertow = builder.setServerOption(ENABLE_HTTP2, true).setHandler(handler()).build();
//        undertow.start();
    }

    public void fillBuffer(SourceBuffer<T> sourceBuffer) {
        queue.drainTo(buffer);
        buffer.forEach(sourceBuffer::add);
        buffer.clear();
    }

    public void close() {
//        undertow.stop();  // TODO: stop grpc
    }

//    private AllowedMethodsHandler handler() {
//        return new AllowedMethodsHandler(path().addExactPath("/", exceptionHandler(this::handleMainPath)
//                .addExceptionHandler(JsonProcessingException.class, this::handleJsonException)), POST, PUT);
//    }
//
//    private void handleMainPath(HttpServerExchange exchange) {
//        exchange.getRequestReceiver().receiveFullBytes(this::consumeMessage, this::consumeException);
//    }
//
//    private void handleJsonException(HttpServerExchange exchange) {
//        logger.warning("Supplied payload is not a valid JSON: " +
//                exchange.getAttachment(ExceptionHandler.THROWABLE).getMessage());
//        exchange.setStatusCode(StatusCodes.BAD_REQUEST);
//    }

    private void consumeMessage(/*HttpServerExchange exchange,*/ byte[] message) {
        try {
            queue.put(mapToItemFn.apply(message));
        } catch (InterruptedException e) {
            throw ExceptionUtil.rethrow(e);
        } finally {
//            exchange.endExchange();
        }
    }

    private void consumeException(/*HttpServerExchange ignored,*/ IOException exception) {
        throw ExceptionUtil.sneakyThrow(exception);
    }
}
