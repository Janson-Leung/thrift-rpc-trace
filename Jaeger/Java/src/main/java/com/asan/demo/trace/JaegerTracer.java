package com.asan.demo.trace;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.samplers.ConstSampler;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * JaegerTracer
 *
 * @author Janson
 * @date 2019-06-19
 */
@Component
public class JaegerTracer {
    private final Logger logger = LoggerFactory.getLogger(JaegerTracer.class);

    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${jaeger.agent.host}")
    private String agentHost;

    @Value("${jaeger.agent.port}")
    private Integer agentPort;

    @PostConstruct
    private void init() {
        logger.info("【分布式跟踪】注册跟踪器：serviceName={}, agentHost={}, agentPort={}", serviceName, agentHost, agentPort);

        // 发送器配置
        Configuration.SenderConfiguration sender = new Configuration.SenderConfiguration();

        // 代理服务器ip
        sender.withAgentHost(agentHost);

        // 代理服务器端口
        sender.withAgentPort(agentPort);

        // 记录器配置
        Configuration.ReporterConfiguration reporter = new Configuration.ReporterConfiguration();
        reporter.withSender(sender);

        // 是否打印日志
        reporter.withLogSpans(false);

        // 缓存队列大小
        reporter.withMaxQueueSize(100);

        // 刷新间隔（毫秒）
        reporter.withFlushInterval(1000);

        // 采样器配置
        Configuration.SamplerConfiguration sampler = new Configuration.SamplerConfiguration();

        // 采样类型
        sampler.withType(ConstSampler.TYPE);

        // 采样比例：所有
        sampler.withParam(1);

        // 客户端配置
        Configuration config = new Configuration(serviceName);
        config.withReporter(reporter);
        config.withSampler(sampler);

        // traceId使用128位随机数，即32位字符串
        config.withTraceId128Bit(true);

        // 跟踪器
        Tracer tracer = config.getTracer();

        GlobalTracer.registerIfAbsent(tracer);
    }
}
