package com.asan.demo.thrift;

import com.asan.demo.service.ApiDemo;
import com.asan.demo.service.impl.ApiDemoImpl;
import com.asan.demo.trace.TJaegerProcessor;
import com.asan.demo.trace.TJaegerServerProtocol;
import org.apache.thrift.TProcessor;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ThriftServer
 *
 * @author Janson
 * @date 2018-07-24
 */
@Component
public class ThriftServer {
    private final Logger logger = LoggerFactory.getLogger(ThriftServer.class);

    @Value("${thrift.port}")
    private int port;

    @Value("${thrift.minWorkerThreads}")
    private int minThreads;

    @Value("${thrift.maxWorkerThreads}")
    private int maxThreads;

    @Autowired
    private ApiDemoImpl apiDemoImpl;

    public void start() {
        try {
            TServerSocket serverTransport = new TServerSocket(port);
            TThreadPoolServer.Args serverArgs = new TThreadPoolServer.Args(serverTransport);

            // TBinaryProtocol.Factory proFactory = new TBinaryProtocol.Factory();
            TJaegerServerProtocol.Factory proFactory = new TJaegerServerProtocol.Factory();
            TTransportFactory transportFactory = new TTransportFactory();

            // 关联处理器与ApiDemo服务实现
            TProcessor processor = new ApiDemo.Processor<ApiDemo.Iface>(apiDemoImpl);
            TJaegerProcessor jaegerProcessor = new TJaegerProcessor(processor);

            serverArgs.processor(jaegerProcessor);
            serverArgs.protocolFactory(proFactory);
            serverArgs.transportFactory(transportFactory);
            serverArgs.minWorkerThreads(minThreads);
            serverArgs.maxWorkerThreads(maxThreads);

            TServer server = new TThreadPoolServer(serverArgs);
            logger.info("Start thrift server on port {}", port);

            server.serve();
        } catch (TTransportException e) {
            logger.error("Failed to start thrift server", e);
        }
    }
}
