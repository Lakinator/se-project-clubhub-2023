CREATE TABLE `generic_events`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id`     bigint(20)   DEFAULT NULL,
    `place_id`    bigint(20)   DEFAULT NULL,
    `event_date`  date         DEFAULT NULL,
    `event_start` datetime     DEFAULT NULL,
    `event_end`   datetime     DEFAULT NULL,
    `title`       varchar(255) DEFAULT NULL,
    `description` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_GENERIC_EVENTS_ON_PLACE` (`place_id`),
    KEY `FK_GENERIC_EVENTS_ON_USER` (`user_id`),
    CONSTRAINT `FK_GENERIC_EVENTS_ON_PLACE` FOREIGN KEY (`place_id`) REFERENCES `locations` (`id`),
    CONSTRAINT `FK_GENERIC_EVENTS_ON_USER` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;