SET sql_mode = '';

#spring.datasource.initialization-mode=always

CREATE TABLE IF NOT EXISTS `product` (

                                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `created_by` varchar(255) DEFAULT NULL,
                                        `created_date` datetime(6) DEFAULT NULL,
                                        `description` varchar(255) DEFAULT NULL,
                                        `image_path` varchar(255) DEFAULT NULL,
                                        `last_modified_by` varchar(255) DEFAULT NULL,
                                        `last_modified_date` datetime(6) DEFAULT NULL,
                                        `name` varchar(255) DEFAULT NULL,
                                        `price` double NOT NULL,
                                        `unit_in_stock` int(11) NOT NULL,
                                        `category_id` bigint(20) DEFAULT NULL,
                                        `brand_id` bigint(20) DEFAULT NULL,
                                        `is_available` bit(1) DEFAULT NULL,
                                        PRIMARY KEY (`id`)

) ENGINE=InnoDB;




CREATE TABLE IF NOT EXISTS `review` (
                                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                        `content` varchar(255) DEFAULT NULL,
                                        `created_date` datetime(6) DEFAULT NULL,
                                        `intent` varchar(255) DEFAULT NULL,
                                        `rating` int(11) NOT NULL,
                                        `product_id` bigint(20) DEFAULT NULL,
                                        `user_id` bigint(20) DEFAULT NULL,
                                        PRIMARY KEY (`id`)

) ENGINE=InnoDB;


#create user x7
#create product x6
/*
INSERT INTO `review` (`id`, `content`, `created_date`, `intent`, `rating`, `product_id`, `user_id`) VALUES

(1, 'string', '2020-08-17 10:40:34.726000', 'string', 4, 1, 1),
(2, 'string', '2020-08-17 10:49:58.549000', 'string', 4, 1, 2),
(3, 'string', '2020-08-17 12:49:10.737000', 'string', 4, 3, 3),
(4, 'string', '2020-08-17 10:40:34.726000', 'string', 4, 1, 4),
(5, 'string', '2020-08-17 10:49:58.549000', 'string', 5, 1, 5),
(6, 'string', '2020-08-17 12:49:10.737000', 'string', 4, 3, 6),
(7, 'string', '2020-08-17 10:40:34.726000', 'string', 5, 1, 7),
(8, 'string', '2020-08-17 10:49:58.549000', 'string', 5, 2, 1),
(9, 'string', '2020-08-17 12:49:10.737000', 'string', 2, 3, 1),
(10, 'string', '2020-08-17 10:40:34.726000', 'string', 4, 4, 1),
(11, 'string', '2020-08-17 10:49:58.549000', 'string', 5, 5, 1),
(12, 'string', '2020-08-17 12:49:10.737000', 'string', 4, 2, 2),
(13, 'string', '2020-08-17 10:40:34.726000', 'string', 2, 3, 2),
(14, 'string', '2020-08-17 10:49:58.549000', 'string', 4, 4, 2),
(15, 'string', '2020-08-17 12:49:10.737000', 'string', 5, 5, 2),
(16, 'string', '2020-08-17 10:49:58.549000', 'string', 5, 1, 3),
(17, 'string', '2020-08-17 12:49:10.737000', 'string', 5, 2, 3),
(18, 'string', '2020-08-17 10:40:34.726000', 'string', 2, 4, 3),
(19, 'string', '2020-08-17 10:49:58.549000', 'string', 5, 5, 3),
(20, 'string', '2020-08-17 12:49:10.737000', 'string', 1, 6, 3),
(21, 'string', '2020-08-17 10:49:58.549000', 'string', 5, 2, 4),
(22, 'string', '2020-08-17 12:49:10.737000', 'string', 2, 3, 4),
(23, 'string', '2020-08-17 10:40:34.726000', 'string', 4, 4, 4),
(24, 'string', '2020-08-17 10:49:58.549000', 'string', 3, 5, 4),
(25, 'string', '2020-08-17 12:49:10.737000', 'string', 1, 6, 4),
(26, 'string', '2020-08-17 10:49:58.549000', 'string', 4, 2, 5),
(27, 'string', '2020-08-17 12:49:10.737000', 'string', 2, 3, 5),
(28, 'string', '2020-08-17 10:40:34.726000', 'string', 4, 4, 5),
(29, 'string', '2020-08-17 10:49:58.549000', 'string', 5, 5, 5),
(30, 'string', '2020-08-17 12:49:10.737000', 'string', 2, 6, 5),
(31, 'string', '2020-08-17 10:49:58.549000', 'string', 5, 1, 6),
(32, 'string', '2020-08-17 12:49:10.737000', 'string', 5, 2, 6),
(33, 'string', '2020-08-17 10:40:34.726000', 'string', 4, 4, 6),
(34, 'string', '2020-08-17 10:49:58.549000', 'string', 3, 5, 6),
(35, 'string', '2020-08-17 12:49:10.737000', 'string', 2, 6, 6);

 */


CREATE TABLE IF NOT EXISTS `pantry` (
                                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB;

/*
INSERT INTO `pantry` (`id`) VALUES
(1),
(2),
(3),
(4),
(5),
(6);

 */


CREATE TABLE IF NOT EXISTS `window_shopping` (
                                                 `item_id` bigint(20) NOT NULL,
                                                 `product_id` bigint(20) NOT NULL,
                                                 PRIMARY KEY (`item_id`,`product_id`)

) ENGINE=InnoDB;

#TanimotoCoefficientSimilarity
/*
INSERT INTO `window_shopping` (`item_id`, `product_id`) VALUES

(1, 3),
(1, 2),
(1, 4),
(1, 5),
(2, 3),
(2, 4),
(2, 5),
(2, 1),
(3, 2),
(3, 4),
(3, 5),
(3, 1),
(4, 2),
(4, 3),
(4, 5),
(4, 1),
(5, 2),
(5, 3),
(5, 4),
(5, 1),
(6, 5),
(6, 2),
(6, 3),
(6, 4);


 */




