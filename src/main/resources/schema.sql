SET sql_mode = '';
SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '');

CREATE TABLE IF NOT EXISTS `review` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `content` varchar(255) DEFAULT NULL,
                                        `created_date` datetime(6) DEFAULT NULL,
                                        `intent` varchar(255) DEFAULT NULL,
                                        `rating` int(11) NOT NULL,
                                        `product_id` bigint(20) DEFAULT NULL,
                                        `user_id` bigint(20) DEFAULT NULL,
                                        PRIMARY KEY (`id`)

) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `review` (`id`, `content`, `created_date`, `intent`, `rating`, `product_id`, `user_id`) VALUES
(1, 'string', NULL, 'string', 4, 1, 1),
(2, 'string', NULL, 'string', 4, 1, 2),
(3, 'string', NULL, 'string', 4, 3, 3),
(4, 'string', NULL, 'string', 4, 1, 4),
(5, 'string', NULL, 'string', 5, 1, 5),
(6, 'string', NULL, 'string', 4, 3, 6),
(7, 'string', NULL, 'string', 4, 1, 7),
(8, 'string', NULL, 'string', 5, 2, 1),
(9, 'string', NULL, 'string', 2, 3, 1),
(10, 'string', NULL, 'string', 4, 4, 1),
(11, 'string', NULL, 'string', 5, 5, 1),
(12, 'string', NULL, 'string', 4, 2, 2),
(13, 'string', NULL, 'string', 2, 3, 2),
(14, 'string', NULL, 'string', 4, 4, 2),
(15, 'string', NULL, 'string', 5, 5, 2),
(16, 'string', NULL, 'string', 5, 1, 3),
(17, 'string', NULL, 'string', 5, 2, 3),
(18, 'string', NULL, 'string', 2, 4, 3),
(19, 'string', NULL, 'string', 5, 5, 3),
(20, 'string', NULL, 'string', 1, 6, 3),
(21, 'string', NULL, 'string', 5, 2, 4),
(22, 'string', NULL, 'string', 2, 3, 4),
(23, 'string', NULL, 'string', 4, 4, 4),
(24, 'string', NULL, 'string', 3, 5, 4),
(25, 'string', NULL, 'string', 1, 6, 4),
(26, 'string', NULL, 'string', 4, 2, 5),
(27, 'string', NULL, 'string', 2, 3, 5),
(28, 'string', NULL, 'string', 4, 4, 5),
(29, 'string', NULL, 'string', 5, 5, 5),
(30, 'string', NULL, 'string', 2, 6, 5),
(31, 'string', NULL, 'string', 5, 1, 6),
(32, 'string', NULL, 'string', 5, 2, 6),
(33, 'string', NULL, 'string', 4, 4, 6),
(34, 'string', NULL, 'string', 3, 5, 6),
(35, 'string', NULL, 'string', 2, 6, 6),
(36, 'string', NULL, 'string', 5, 2, 7),
(37, 'string', NULL, 'string', 2, 3, 7),
(38, 'string', NULL, 'string', 4, 4, 7),
(39, 'string', NULL, 'string', 5, 5, 7),
(40, 'string', NULL, 'string', 1, 14, 1);








