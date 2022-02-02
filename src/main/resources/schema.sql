SET sql_mode = '';
SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '');

CREATE TABLE IF NOT EXISTS `review` (
                          `id` BIGINT(19) NOT NULL AUTO_INCREMENT,
                          `content` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
                          `created_date` DATETIME(6) NULL DEFAULT NULL,
                          `intent` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8mb4_0900_ai_ci',
                          `predicted_rating` INT(10) NOT NULL ,
                          `rating` INT(10) NOT NULL,
                          `rating_probability` DOUBLE NOT NULL,
                          `product_id` BIGINT(19) NULL DEFAULT NULL,
                          `user_id` BIGINT(19) NULL DEFAULT NULL,
                          PRIMARY KEY (`id`) USING BTREE

)
    COLLATE='utf8mb4_0900_ai_ci'
    ENGINE=InnoDB
    AUTO_INCREMENT=11
;



INSERT INTO `review` (`id`, `content`, `created_date`, `intent`, `predicted_rating`,  `rating`, `product_id`, `rating_probability`, `user_id`) VALUES
(1, 'string', NULL, 'string', 0,  4, 1, 0, 1),
(2, 'string', NULL, 'string', 0,  4, 1, 0, 2),
(3, 'string', NULL, 'string', 0,  4, 3, 0, 3),
(4, 'string', NULL, 'string', 0,  4, 1, 0, 4),
(5, 'string', NULL, 'string', 0,  5, 1, 0, 5),
(6, 'string', NULL, 'string', 0,  4, 3, 0, 6),
(7, 'string', NULL, 'string', 0,  4, 1, 0, 7),
(8, 'string', NULL, 'string', 0,  5, 2, 0, 1),
(9, 'string', NULL, 'string', 0,  2, 3, 0, 1),
(10, 'string', NULL, 'string', 0, 4, 4, 0 , 1),
(11, 'string', NULL, 'string', 0, 5, 5, 0 , 1),
(12, 'string', NULL, 'string', 0, 4, 2, 0 , 2),
(13, 'string', NULL, 'string', 0, 2, 3, 0 , 2),
(14, 'string', NULL, 'string', 0, 4, 4, 0 , 2),
(15, 'string', NULL, 'string', 0, 5, 5, 0 , 2),
(16, 'string', NULL, 'string', 0, 5, 1, 0 , 3),
(17, 'string', NULL, 'string', 0, 5, 2, 0 , 3),
(18, 'string', NULL, 'string', 0, 2, 4, 0 , 3),
(19, 'string', NULL, 'string', 0, 5, 5, 0 , 3),
(20, 'string', NULL, 'string', 0, 1, 6, 0 , 3),
(21, 'string', NULL, 'string', 0, 5, 2, 0 , 4),
(22, 'string', NULL, 'string', 0, 2, 3, 0 , 4),
(23, 'string', NULL, 'string', 0, 4, 4, 0 , 4),
(24, 'string', NULL, 'string', 0, 3, 5, 0 , 4),
(25, 'string', NULL, 'string', 0, 1, 6, 0 , 4),
(26, 'string', NULL, 'string', 0, 4, 2, 0 , 5),
(27, 'string', NULL, 'string', 0, 2, 3, 0 , 5),
(28, 'string', NULL, 'string', 0, 4, 4, 0 , 5),
(29, 'string', NULL, 'string', 0, 5, 5, 0 , 5),
(30, 'string', NULL, 'string', 0, 2, 6, 0 , 5),
(31, 'string', NULL, 'string', 0, 5, 1, 0 , 6),
(32, 'string', NULL, 'string', 0, 5, 2, 0 , 6),
(33, 'string', NULL, 'string', 0, 4, 4, 0 , 6),
(34, 'string', NULL, 'string', 0, 3, 5, 0 , 6),
(35, 'string', NULL, 'string', 0, 2, 6, 0 , 6),
(36, 'string', NULL, 'string', 0, 5, 2, 0 , 7),
(37, 'string', NULL, 'string', 0, 2, 3, 0 , 7),
(38, 'string', NULL, 'string', 0, 4, 4, 0 , 7),
(39, 'string', NULL, 'string', 0, 5, 5, 0 , 7),
(40, 'string', NULL, 'string', 0, 1, 14, 0, 1);








