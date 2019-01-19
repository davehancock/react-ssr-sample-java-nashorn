package com.djh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author David Hancock
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.setProperty("nashorn.args", "--language=es6");
        SpringApplication.run(Application.class);
    }

}
