package com.thinkpalm.ChatApplication.Repository;

import com.thinkpalm.ChatApplication.Model.RoomModel;
import com.thinkpalm.ChatApplication.Model.UserModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel,Integer> {
    Optional<UserModel> findByName(String username);

    @Transactional
    @Modifying
    @Query(value = "update user set bio = ?2 where name = ?1",nativeQuery = true)
    void updateUserBio(String username, String bio);

    @Query(value = "SELECT id, name, profile_pic, type, max(modified_at) as max_modified_at\n" +
            "            FROM (\n" +
            "                        SELECT u.id, u.name, u.profile_pic, 'user' as type, max(mr1.modified_at) as modified_at\n" +
            "                        FROM chatdb.message_receiver as mr1\n" +
            "                        INNER JOIN chatdb.message as m ON m.id = mr1.message_id\n" +
            "                        INNER JOIN chatdb.user as u ON u.id = mr1.receiver_id\n" +
            "                        WHERE m.sender_id = ?1\n" +
            "                        GROUP BY u.id, u.name, u.profile_pic\n" +
            "                        UNION\n" +
            "                        SELECT u.id, u.name, u.profile_pic, 'user' as type, max(mr1.modified_at) as modified_at\n" +
            "                        FROM chatdb.message_receiver as mr1\n" +
            "                        INNER JOIN chatdb.message as m ON m.id = mr1.message_id\n" +
            "                        INNER JOIN chatdb.user as u ON u.id = m.sender_id\n" +
            "                        WHERE mr1.receiver_id = ?1\n" +
            "                        GROUP BY u.id, u.name, u.profile_pic\n" +
            "                        UNION\n" +
            "                        SELECT r.id,r.name, r.room_pic, 'room' as type,CASE WHEN p.is_active IS false THEN p.left_at ELSE MAX(mr2.modified_at) END as timestamp\n" +
            "                        FROM chatdb.message_room as mr2\n" +
            "                        INNER JOIN chatdb.room as r ON r.id = mr2.room_id\n" +
            "                        INNER JOIN chatdb.participant as p ON p.room_id = r.id\n" +
            "                        WHERE p.user_id = ?1 or \n" +
            "\t\t\t\t\t\t\t(p.is_active is false and mr2.modified_at < p.left_at)\n" +
            "                        GROUP BY r.id, r.name, r.room_pic, p.left_at\n" +
            "                        UNION\n" +
            "                        SELECT r.id, r.name, r.room_pic, 'room' as type, r.created_at as modified_at\n" +
            "                        FROM chatdb.room as r\n" +
            "                        INNER JOIN chatdb.participant as p ON p.room_id = r.id\n" +
            "                        WHERE p.user_id = ?1\n" +
            "            ) AS combined_results\n" +
            "                    GROUP BY id, name, profile_pic, type\n" +
            "                    ORDER BY max_modified_at DESC;",nativeQuery = true)
    List<Map<String,Object>> findAllChatsOfUser(Integer currentUserId);

    @Query(value = "select * from user where name = ?1 or phone_number = ?2",nativeQuery = true)
    List<UserModel> existByNameOrPhonenumber(String name, String phoneNumber);

    @Query(value = "SELECT id, name, profile_pic, type, max(modified_at) as max_modified_at\n" +
            "            FROM (\n" +
            "                        SELECT u.id, u.name, u.profile_pic, 'user' as type, max(mr1.modified_at) as modified_at\n" +
            "                        FROM chatdb.message_receiver as mr1\n" +
            "                        INNER JOIN chatdb.message as m ON m.id = mr1.message_id\n" +
            "                        INNER JOIN chatdb.user as u ON u.id = mr1.receiver_id\n" +
            "                        WHERE m.sender_id = ?1\n" +
            "                        GROUP BY u.id, u.name, u.profile_pic\n" +
            "                        UNION\n" +
            "                        SELECT u.id, u.name, u.profile_pic, 'user' as type, max(mr1.modified_at) as modified_at\n" +
            "                        FROM chatdb.message_receiver as mr1\n" +
            "                        INNER JOIN chatdb.message as m ON m.id = mr1.message_id\n" +
            "                        INNER JOIN chatdb.user as u ON u.id = m.sender_id\n" +
            "                        WHERE mr1.receiver_id = ?1\n" +
            "                        GROUP BY u.id, u.name, u.profile_pic\n" +
            "                        UNION\n" +
            "                        SELECT r.id,r.name, r.room_pic, 'room' as type,CASE WHEN p.is_active IS false THEN p.left_at ELSE MAX(mr2.modified_at) END as timestamp\n" +
            "                        FROM chatdb.message_room as mr2\n" +
            "                        INNER JOIN chatdb.room as r ON r.id = mr2.room_id\n" +
            "                        INNER JOIN chatdb.participant as p ON p.room_id = r.id\n" +
            "                        WHERE p.user_id = ?1 or \n" +
            "\t\t\t\t\t\t\t(p.is_active is false and mr2.modified_at < p.left_at)\n" +
            "                        GROUP BY r.id, r.name, r.room_pic, p.left_at\n" +
            "                        UNION\n" +
            "                        SELECT r.id, r.name, r.room_pic, 'room' as type, r.created_at as modified_at\n" +
            "                        FROM chatdb.room as r\n" +
            "                        INNER JOIN chatdb.participant as p ON p.room_id = r.id\n" +
            "                        WHERE p.user_id = ?1\n" +
            "            ) AS combined_results\n" +
            "\t\t\t\t\twhere name LIKE CONCAT('%',?2,'%')\n" +
            "                    GROUP BY id, name, profile_pic, type\n" +
            "                    ORDER BY max_modified_at DESC;",nativeQuery = true)
    List<Map<String, Object>> searchChats(Integer currentUserId,String searchName);

    @Query(value = "SELECT r.*\n" +
            "FROM chatdb.room r\n" +
            "JOIN chatdb.participant p ON r.id = p.room_id\n" +
            "WHERE p.user_id = ?1 OR p.user_id = ?2\n" +
            "GROUP BY r.id\n" +
            "HAVING COUNT(DISTINCT p.user_id) = 2",nativeQuery = true)
    List<Map<String, Object>> getCommonRooms(Integer id, Integer id1);

    @Query(value = "SELECT u.id,u.name,u.profile_pic FROM chatdb.user as u where u.name LIKE CONCAT('%',?1,'%') && u.id != ?2",nativeQuery = true)
    List<Map<String, Object>> searchUsers(String searchName,Integer currentUserId);
}
