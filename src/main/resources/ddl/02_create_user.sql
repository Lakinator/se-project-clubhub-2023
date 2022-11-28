CREATE TABLE `users`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `first_name` varchar(255) DEFAULT NULL,
    `last_name`  varchar(255) DEFAULT NULL,
    `email`      varchar(255) DEFAULT NULL,
    `password`   varchar(255) DEFAULT NULL,
    `active`     bit(1)       DEFAULT NULL,
    `club_id`    bigint(20)   DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_USERS_ON_CLUB` (`club_id`),
    CONSTRAINT `FK_USERS_ON_CLUB` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;