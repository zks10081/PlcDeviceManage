package com.dmkj.plcdevice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@SpringBootApplication
@Controller
public class PlcDeviceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlcDeviceApplication.class, args);
    }
}
