-- auto generated announcement definition

CREATE TABLE `announcement`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id`    bigint(20)   DEFAULT NULL,
    `club_id`    bigint(20)   DEFAULT NULL,
    `created_on` datetime     DEFAULT NULL,
    `updated_on` datetime     DEFAULT NULL,
    `message`    varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_ANNOUNCEMENT_ON_CLUB` (`club_id`),
    KEY `FK_ANNOUNCEMENT_ON_USER` (`user_id`),
    CONSTRAINT `FK_ANNOUNCEMENT_ON_CLUB` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`),
    CONSTRAINT `FK_ANNOUNCEMENT_ON_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;