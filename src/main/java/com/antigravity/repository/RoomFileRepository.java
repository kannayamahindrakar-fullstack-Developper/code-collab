package com.antigravity.repository;

import com.antigravity.entity.RoomFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomFileRepository extends JpaRepository<RoomFile, Long> {
    List<RoomFile> findByRoomId(Long roomId);
}
