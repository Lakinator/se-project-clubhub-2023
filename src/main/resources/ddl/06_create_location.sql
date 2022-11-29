CREATE TABLE `locations`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `country`       varchar(255) DEFAULT NULL,
    `city`          varchar(255) DEFAULT NULL,
    `postal_code`   varchar(32)  DEFAULT NULL,
    `street`        varchar(255) DEFAULT NULL,
    `street_number` varchar(64)  DEFAULT NULL,
    `description`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;