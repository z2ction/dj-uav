package com.cleaner.djuav;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class DjUavApplication {

    public static void main(String[] args) {
        SpringApplication.run(DjUavApplication.class, args);
        log.info("knife4j Doc: http://localhost:4887/doc.html");
    }

}
