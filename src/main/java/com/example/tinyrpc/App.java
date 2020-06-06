package com.example.tinyrpc;


import com.example.tinyrpc.transport.Server;
import com.example.tinyrpc.transport.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        Server server = new NettyServer();
        server.run("127.0.0.1", 8787);
//        Server server = new NettyServer();
//        server.run("127.0.0.1", 8989);
        System.out.println();
    }

}
