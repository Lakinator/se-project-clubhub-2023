CREATE TABLE group_event_requests
(
    `id`                 BIGINT(20)   NOT NULL,
    `creator_group_id`   BIGINT(20)   NULL,
    `requested_group_id` BIGINT(20)   NULL,
    `event_type`         VARCHAR(255) NULL,
    `request_status`     VARCHAR(255) NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT FK_GROUP_EVENT_REQUESTS_ON_ID FOREIGN KEY (`id`) REFERENCES generic_events (`id`),
    CONSTRAINT FK_GROUP_EVENT_REQUESTS_ON_CREATOR_GROUP FOREIGN KEY (`creator_group_id`) REFERENCES `groups` (`id`),
    CONSTRAINT FK_GROUP_EVENT_REQUESTS_ON_REQUESTED_GROUP FOREIGN KEY (`requested_group_id`) REFERENCES `groups` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;