package com.antigravity.config;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServerCommandLineRunner implements CommandLineRunner {

    private final SocketIOServer server;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting Socket.IO Server...");
        server.start();
        log.info("Socket.IO Server started successfully.");
    }

    @PreDestroy
    public void stopSocketServer() {
        log.info("Stopping Socket.IO Server...");
        server.stop();
        log.info("Socket.IO Server stopped.");
    }
}
