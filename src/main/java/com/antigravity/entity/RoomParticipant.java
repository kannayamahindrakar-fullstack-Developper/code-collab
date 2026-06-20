package com.antigravity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_participants", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"room_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 20)
    @Builder.Default
    private String permission = "READ_WRITE";

    @Column(name = "joined_at", updatable = false)
    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();
}
