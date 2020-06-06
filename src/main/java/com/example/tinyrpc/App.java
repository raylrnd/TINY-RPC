package com.example.tinyrpc;


import com.example.tinyrpc.transport.TestHello;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class App {


    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        new TestHello().contextLoads();
    }

}
