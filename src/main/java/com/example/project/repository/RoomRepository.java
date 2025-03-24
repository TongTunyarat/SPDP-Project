package com.example.project.repository;

import com.example.project.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, String> {
    Room findByRoomNumber(String roomNumber);
}
