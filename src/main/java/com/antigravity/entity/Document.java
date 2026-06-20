package com.antigravity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 50)
    @Builder.Default
    private String language = "plaintext";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "last_modified")
    @Builder.Default
    private LocalDateTime lastModified = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        lastModified = LocalDateTime.now();
    }
}
