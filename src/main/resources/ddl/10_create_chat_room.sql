CREATE TABLE `chat_rooms`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT,
    `group_id` bigint(20)   DEFAULT NULL,
    `name`     varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_CHAT_ROOMS_ON_GROUP` (`group_id`),
    CONSTRAINT `FK_CHAT_ROOMS_ON_GROUP` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

-- create join table after committing create table rooms

CREATE TABLE `chat_room_users`
(
    `chat_room_id` bigint(20) NOT NULL,
    `user_id`      bigint(20) NOT NULL,
    PRIMARY KEY (`chat_room_id`, `user_id`),
    KEY `fk_charoouse_on_user` (`user_id`),
    CONSTRAINT `fk_charoouse_on_chat_room` FOREIGN KEY (`chat_room_id`) REFERENCES `chat_rooms` (`id`),
    CONSTRAINT `fk_charoouse_on_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;