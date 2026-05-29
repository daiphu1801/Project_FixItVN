// backend/src/main/java/com/fixit/FixitApplication.java
package com.fixit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FixitApplication {

    public static void main(String[] args) {
        SpringApplication.run(FixitApplication.class, args);
    }
}
