CREATE TABLE `group_events`
(
    `id`         bigint(20) NOT NULL,
    `group_id`   bigint(20)   DEFAULT NULL,
    `event_type` varchar(255) DEFAULT NULL,
    `team_is_final` bit(1) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_GROUP_EVENTS_ON_GROUP` (`group_id`),
    CONSTRAINT `FK_GROUP_EVENTS_ON_GROUP` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`),
    CONSTRAINT `FK_GROUP_EVENTS_ON_ID` FOREIGN KEY (`id`) REFERENCES `generic_events` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;