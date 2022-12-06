package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.ChatRoom;
import de.oth.seproject.clubhub.persistence.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByGroup(Group group);

    Page<ChatRoom> findAllByGroup(Group group, Pageable pageable);

    /**
     * @param chatRoomId -
     * @param userId     -
     * @return Whether the user is part of this chat room
     */
    Boolean existsByIdAndUsers_Id(Long chatRoomId, Long userId);

}
