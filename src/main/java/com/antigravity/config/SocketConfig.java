package com.antigravity.config;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketConfig {

    @Value("${socket.host:localhost}")
    private String host;

    @Value("${socket.port:8085}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);
        // Allow CORS for the Socket.IO server
        config.setOrigin("*");

        return new SocketIOServer(config);
    }
}
