package com.asan.demo.trace;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.TIOStreamTransport;
import org.apache.thrift.transport.TTransport;

import java.io.BufferedInputStream;
import java.lang.reflect.Field;

/**
 * TJaegerServerProtocol
 *
 * @author Janson
 * @date 2019-06-18
 */
public class TJaegerServerProtocol extends TBinaryProtocol {

    public TJaegerServerProtocol(TTransport transport) {
        super(transport);
    }

    public String readFieldZero() throws TException {
        TField field = readFieldBegin();

        String value = null;
        if (field.id == 0 && field.type == TType.STRING) {
            value = readString();
        }

        readFieldEnd();

        return value;
    }

    public void markTFramedTransport(TProtocol in) {
        try {
            Field tioInputStream = TIOStreamTransportFieldsCache.getInstance().getTIOInputStream();
            if (tioInputStream == null){
                return;
            }

            BufferedInputStream inputStream = (BufferedInputStream) tioInputStream.get(in.getTransport());
            inputStream.mark(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重置TFramedTransport流，不影响Thrift原有流程
     */
    public void resetTFramedTransport(TProtocol in) {
        try {
            Field tioInputStream = TIOStreamTransportFieldsCache.getInstance().getTIOInputStream();
            if (tioInputStream == null){
                return;
            }

            BufferedInputStream inputStream = (BufferedInputStream) tioInputStream.get(in.getTransport());
            inputStream.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class TIOStreamTransportFieldsCache {
        private volatile static TIOStreamTransportFieldsCache instance;
        private final Field inputStream_;
        private final String TIOStreamTransport_inputStream_ = "inputStream_";

        private TIOStreamTransportFieldsCache() throws Exception {
            inputStream_ = TIOStreamTransport.class.getDeclaredField(TIOStreamTransport_inputStream_);
            inputStream_.setAccessible(true);
        }

        public static TIOStreamTransportFieldsCache getInstance() throws Exception {
            if (instance == null) {
                synchronized (TIOStreamTransportFieldsCache.class) {
                    if (instance == null) {
                        instance = new TIOStreamTransportFieldsCache();
                    }
                }
            }

            return instance;
        }

        public Field getTIOInputStream() {
            return inputStream_;
        }
    }

    public static class Factory implements TProtocolFactory {
        @Override
        public TProtocol getProtocol(TTransport transport) {
            return new TJaegerServerProtocol(transport);
        }
    }
}
