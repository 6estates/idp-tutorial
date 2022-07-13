package com.sixestates;

import com.sixestates.Dao.Storeage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IdpSdkApplication {
    public static void main(String[] args) {
        Storeage.init();
        SpringApplication.run(IdpSdkApplication.class, args);
    }

}