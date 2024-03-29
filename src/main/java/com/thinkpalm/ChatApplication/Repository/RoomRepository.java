package com.thinkpalm.ChatApplication.Repository;

import com.thinkpalm.ChatApplication.Model.RoomModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface RoomRepository extends JpaRepository<RoomModel,Integer> {
    RoomModel findByName(String name);
    @Query(value = "SELECT * FROM chatdb.room where room.name = ?1",nativeQuery = true)
    List<RoomModel> existByRoomName(String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE room SET name = ?2 WHERE id = ?1",nativeQuery = true)
    void updateRoomName(Integer roomId, String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE room SET description = ?2 WHERE id = ?1",nativeQuery = true)
    void updateRoomDescription(Integer roomId, String name);
    @Query(value = "SELECT id,name,description,room_pic FROM chatdb.room where room.room_code = ?1",nativeQuery = true)
    Map<String, Object> findRoomByRoomCode(String roomCode);

    @Query(value = "SELECT u.name FROM chatdb.participant as p inner join chatdb.user as u on p.user_id = u.id where p.room_id = ?1 and p.is_active = true",nativeQuery = true)
    List<String> getActiveUsers(Integer roomId);
}
