package com.antigravity.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String language;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "is_directory")
    @Builder.Default
    private Boolean isDirectory = false;

    @Column(name = "parent_id")
    private Long parentId;
}
