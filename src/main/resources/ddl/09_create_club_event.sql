CREATE TABLE `club_events`
(
    `id`      bigint(20) NOT NULL,
    `club_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_CLUB_EVENTS_ON_CLUB` (`club_id`),
    CONSTRAINT `FK_CLUB_EVENTS_ON_CLUB` FOREIGN KEY (`club_id`) REFERENCES `clubs` (`id`),
    CONSTRAINT `FK_CLUB_EVENTS_ON_ID` FOREIGN KEY (`id`) REFERENCES `generic_events` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;