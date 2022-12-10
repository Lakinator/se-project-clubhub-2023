package de.oth.seproject.clubhub.persistence.repository;

import de.oth.seproject.clubhub.persistence.model.ChatRoom;
import de.oth.seproject.clubhub.persistence.model.ChatRoomMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomMessageRepository extends JpaRepository<ChatRoomMessage, Long> {

    Page<ChatRoomMessage> findAllByChatRoom(ChatRoom chatRoom, Pageable pageable);

}
