package com.asan.demo;

import com.asan.demo.thrift.ThriftServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DemoApplication.class, args);

        try {
            ThriftServer thriftServer = context.getBean(ThriftServer.class);
            thriftServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
