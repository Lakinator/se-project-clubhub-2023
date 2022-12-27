CREATE TABLE `group_event_attendances`
(
    `id`                 BIGINT(20)   NOT NULL AUTO_INCREMENT,
    `group_event_id`     BIGINT(20)   DEFAULT NULL,
    `user_id`            BIGINT(20)   DEFAULT NULL,
    `status`             VARCHAR(255) DEFAULT NULL,
    `is_not_removed`     BIT(1)       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_GROUP_EVENT_ATTENDANCES_ON_GROUP_EVENT` (`group_event_id`),
    KEY `FK_GROUP_EVENT_ATTENDANCES_ON_USER` (`user_id`),
    CONSTRAINT `FK_GROUP_EVENT_ATTENDANCES_ON_GROUP_EVENT` FOREIGN KEY (`group_event_id`) REFERENCES `group_events` (`id`),
    CONSTRAINT `FK_GROUP_EVENT_ATTENDANCES_ON_USER` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;