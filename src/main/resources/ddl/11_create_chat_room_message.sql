CREATE TABLE `chat_room_messages`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id`      bigint(20)    DEFAULT NULL,
    `chat_room_id` bigint(20)    DEFAULT NULL,
    `created_on`   datetime      DEFAULT NULL,
    `updated_on`   datetime      DEFAULT NULL,
    `message`      varchar(1024) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_CHAT_ROOM_MESSAGES_ON_CHAT_ROOM` (`chat_room_id`),
    KEY `FK_CHAT_ROOM_MESSAGES_ON_USER` (`user_id`),
    CONSTRAINT `FK_CHAT_ROOM_MESSAGES_ON_CHAT_ROOM` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_rooms` (`id`),
    CONSTRAINT `FK_CHAT_ROOM_MESSAGES_ON_USER` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;