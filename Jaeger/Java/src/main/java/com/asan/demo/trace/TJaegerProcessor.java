package com.asan.demo.trace;

import com.alibaba.fastjson.JSON;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocol;

/**
 * TJaegerProcessor
 *
 * @author Janson
 * @date 2019-06-18
 */
public class TJaegerProcessor implements TProcessor {
    private TProcessor processor;

    public TJaegerProcessor(TProcessor processor) {
        this.processor = processor;
    }

    @Override
    public boolean process(TProtocol in, TProtocol out) throws TException {
        if (in instanceof TJaegerServerProtocol) {
            TJaegerServerProtocol serverProtocol = (TJaegerServerProtocol) in;
            serverProtocol.markTFramedTransport(in);

            TMessage tMessage = serverProtocol.readMessageBegin();

            String context = serverProtocol.readFieldZero();
            System.out.println("Server context: " + context);

            Tracer tracer = GlobalTracer.get();

            // Json format context：{"traceContext":{"uber-trace-id":"12351fa800399fc87996627f17243125:7996627f17243125:0:1"}}
            ContextCarrier carrier = JSON.parseObject(context, ContextCarrier.class);
            SpanContext spanContext = tracer.extract(Format.Builtin.TEXT_MAP, carrier);

            Span span = tracer.buildSpan("Java site: rpc.thrift " + tMessage.name + " receive").asChildOf(spanContext).start();
            tracer.activateSpan(span);

            serverProtocol.resetTFramedTransport(in);
        }

        boolean result = processor.process(in, out);

        if (in instanceof TJaegerServerProtocol) {
            GlobalTracer.get().activeSpan().finish();
        }

        return result;
    }
}
