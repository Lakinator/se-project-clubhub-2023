-- auto generated group definition

CREATE TABLE `group`
(
    `id`      bigint(20) NOT NULL AUTO_INCREMENT,
    `club_id` bigint(20)   DEFAULT NULL,
    `name`    varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_GROUP_ON_CLUB` (`club_id`),
    CONSTRAINT `FK_GROUP_ON_CLUB` FOREIGN KEY (`club_id`) REFERENCES `club` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;