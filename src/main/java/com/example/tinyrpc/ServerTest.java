package com.example.tinyrpc;


import com.example.tinyrpc.transport.Server;
import com.example.tinyrpc.transport.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ServerTest {


    public static void main(String[] args) {
        SpringApplication.run(ServerTest.class, args);
        Server server = new NettyServer();
        server.run("127.0.0.1", 8787);
        System.out.println();
    }

}
