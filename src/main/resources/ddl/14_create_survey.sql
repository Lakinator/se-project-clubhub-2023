CREATE TABLE `surveys`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id`    bigint(20)   DEFAULT NULL,
    `group_id`   bigint(20)   DEFAULT NULL,
    `created_on` datetime     DEFAULT NULL,
    `updated_on` datetime     DEFAULT NULL,
    `message`    varchar(255) DEFAULT NULL,
    `options`    varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_ANNOUNCEMENTS_ON_GROUP` (`group_id`),
    KEY `FK_ANNOUNCEMENTS_ON_USER` (`user_id`),
    CONSTRAINT `FK_SURVEYS_ON_GROUP` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`),
    CONSTRAINT `FK_SURVEYS_ON_USER` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;