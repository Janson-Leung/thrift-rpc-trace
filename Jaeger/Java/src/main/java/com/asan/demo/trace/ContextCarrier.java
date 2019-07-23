package com.asan.demo.trace;

import io.opentracing.propagation.TextMap;
import lombok.Data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Context Carrier
 *
 * @author Janson
 * @date 2019-06-19
 */
@Data
public class ContextCarrier implements TextMap {
    private Map<String, String> traceContext = new HashMap<>();

    @Override
    public void put(String key, String value) {
        traceContext.put(key, value);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return traceContext.entrySet().iterator();
    }
}
