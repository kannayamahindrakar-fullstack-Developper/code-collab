package com.antigravity.controller;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.antigravity.service.ChatService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@SuppressWarnings("rawtypes")
public class SocketModule {

    private final SocketIOServer server;
    private final ChatService chatService;

    // Track roomCode -> List of active users {socketId, username}
    private final Map<String, List<Map<String, String>>> roomUsers = new ConcurrentHashMap<>();

    public SocketModule(SocketIOServer server, ChatService chatService) {
        this.server = server;
        this.chatService = chatService;
        server.addConnectListener(onConnected());
        server.addDisconnectListener(onDisconnected());
        server.addEventListener("join_room", Map.class, onJoinRoom());
        server.addEventListener("code_change", Map.class, onCodeChange());
        server.addEventListener("file_created", Map.class, onFileCreated());
        server.addEventListener("file_deleted", Map.class, onFileDeleted());
        server.addEventListener("file_renamed", Map.class, onFileRenamed());
        server.addEventListener("private_message", Map.class, onPrivateMessage());
        server.addEventListener("webrtc_offer", Map.class, onWebRtcOffer());
        server.addEventListener("webrtc_answer", Map.class, onWebRtcAnswer());
        server.addEventListener("webrtc_ice_candidate", Map.class, onWebRtcIceCandidate());
        server.addEventListener("kick_user", Map.class, onKickUser());
    }

    private ConnectListener onConnected() {
        return client -> {
            log.info("Client connected: " + client.getSessionId().toString());
        };
    }

    private DisconnectListener onDisconnected() {
        return client -> {
            String sessionId = client.getSessionId().toString();
            log.info("Client disconnected: " + sessionId);
            
            // Remove user from room tracking
            roomUsers.forEach((roomCode, users) -> {
                boolean removed = users.removeIf(u -> u.get("socketId").equals(sessionId));
                if (removed) {
                    // Broadcast updated list to remaining users
                    client.getNamespace().getRoomOperations(roomCode).sendEvent("room_users", users);
                }
            });
        };
    }

    private DataListener<Map> onJoinRoom() {
        return (client, data, ackSender) -> {
            String roomCode = (String) data.get("roomCode");
            String username = (String) data.get("username");
            log.info("Client [{}] ({}) joining room: {}", client.getSessionId().toString(), username, roomCode);
            client.joinRoom(roomCode);
            
            // Add to room tracking
            roomUsers.putIfAbsent(roomCode, Collections.synchronizedList(new ArrayList<>()));
            List<Map<String, String>> users = roomUsers.get(roomCode);
            
            Map<String, String> userMap = new HashMap<>();
            userMap.put("socketId", client.getSessionId().toString());
            userMap.put("username", username != null ? username : "Anonymous");
            users.add(userMap);

            // Broadcast the full active users list to everyone in the room
            client.getNamespace().getRoomOperations(roomCode).sendEvent("room_users", users);
            
            // Still send individual joined event for WebRTC logic
            client.getNamespace().getRoomOperations(roomCode).getClients().forEach(c -> {
                if (!c.getSessionId().equals(client.getSessionId())) {
                    c.sendEvent("user_joined", userMap);
                }
            });
        };
    }

    private DataListener<Map> onCodeChange() {
        return (client, data, ackSender) -> {
            String roomCode = (String) data.get("roomCode");
            
            // Broadcast the code change to all clients in the room EXCEPT the sender
            client.getNamespace().getRoomOperations(roomCode).getClients().forEach(c -> {
                if (!c.getSessionId().equals(client.getSessionId())) {
                    c.sendEvent("receive_code", data);
                }
            });
        };
    }

    private DataListener<Map> onFileCreated() {
        return (client, data, ackSender) -> {
            String roomCode = (String) data.get("roomCode");
            client.getNamespace().getRoomOperations(roomCode).getClients().forEach(c -> {
                if (!c.getSessionId().equals(client.getSessionId())) {
                    c.sendEvent("receive_new_file", data);
                }
            });
        };
    }

    private DataListener<Map> onFileDeleted() {
        return (client, data, ackSender) -> {
            String roomCode = (String) data.get("roomCode");
            client.getNamespace().getRoomOperations(roomCode).getClients().forEach(c -> {
                if (!c.getSessionId().equals(client.getSessionId())) {
                    c.sendEvent("receive_file_deleted", data);
                }
            });
        };
    }

    private DataListener<Map> onFileRenamed() {
        return (client, data, ackSender) -> {
            String roomCode = (String) data.get("roomCode");
            client.getNamespace().getRoomOperations(roomCode).getClients().forEach(c -> {
                if (!c.getSessionId().equals(client.getSessionId())) {
                    c.sendEvent("receive_file_renamed", data);
                }
            });
        };
    }

    private DataListener<Map> onPrivateMessage() {
        return (client, data, ackSender) -> {
            String roomCode = (String) data.get("roomCode");
            String targetSocketId = (String) data.get("targetSocketId");
            String senderUsername = (String) data.get("senderUsername");
            String receiverUsername = (String) data.get("receiverUsername");
            String content = (String) data.get("content");

            // Save to DB
            chatService.saveMessage(roomCode, senderUsername, receiverUsername, content);

            // Route to target user socket directly
            server.getClient(java.util.UUID.fromString(targetSocketId)).sendEvent("receive_private_message", data);
        };
    }

    @SuppressWarnings("unchecked")
    private DataListener<Map> onWebRtcOffer() {
        return (client, data, ackSender) -> {
            String targetSocketId = (String) data.get("targetSocketId");
            data.put("callerSocketId", client.getSessionId().toString()); // Inject caller ID
            server.getClient(java.util.UUID.fromString(targetSocketId)).sendEvent("webrtc_offer", data);
        };
    }

    @SuppressWarnings("unchecked")
    private DataListener<Map> onWebRtcAnswer() {
        return (client, data, ackSender) -> {
            String targetSocketId = (String) data.get("targetSocketId");
            data.put("answererSocketId", client.getSessionId().toString()); // Inject answerer ID
            server.getClient(java.util.UUID.fromString(targetSocketId)).sendEvent("webrtc_answer", data);
        };
    }

    @SuppressWarnings("unchecked")
    private DataListener<Map> onWebRtcIceCandidate() {
        return (client, data, ackSender) -> {
            String targetSocketId = (String) data.get("targetSocketId");
            data.put("senderSocketId", client.getSessionId().toString());
            server.getClient(java.util.UUID.fromString(targetSocketId)).sendEvent("webrtc_ice_candidate", data);
        };
    }

    private DataListener<Map> onKickUser() {
        return (client, data, ackSender) -> {
            String targetSocketId = (String) data.get("targetSocketId");
            // Find the target client and send kicked event
            try {
                java.util.UUID uuid = java.util.UUID.fromString(targetSocketId);
                server.getClient(uuid).sendEvent("kicked");
            } catch (Exception e) {
                log.error("Could not kick user", e);
            }
        };
    }
}
