-- auto generated role definition

CREATE TABLE `role`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id`  bigint(20)   DEFAULT NULL,
    `group_id` bigint(20)   DEFAULT NULL,
    `name`     varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_ROLE_ON_GROUP` (`group_id`),
    KEY `FK_ROLE_ON_USER` (`user_id`),
    CONSTRAINT `FK_ROLE_ON_GROUP` FOREIGN KEY (`group_id`) REFERENCES `group` (`id`),
    CONSTRAINT `FK_ROLE_ON_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;