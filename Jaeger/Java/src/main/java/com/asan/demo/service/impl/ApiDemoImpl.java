package com.asan.demo.service.impl;

import com.asan.demo.service.ApiDemo;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * ApiDemoImpl
 *
 * @author Janson
 * @date 2018-07-24
 */
@Service
public class ApiDemoImpl implements ApiDemo.Iface {
    private final Logger logger = LoggerFactory.getLogger(ApiDemoImpl.class);

    @Override
    public String getDetailById(long id) throws TException {
        logger.info("id: {}", id);

        return "Hello [" + id + "]";
    }
}
