SET sql_mode = '';

#spring.datasource.initialization-mode=always

CREATE TABLE IF NOT EXISTS `brand` (
                                       `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                       `name` varchar(255) DEFAULT NULL,
                                       PRIMARY KEY (`id`)
) ENGINE=InnoDB;

INSERT INTO `brand` (`id`, `name`) VALUES
(1, 'brand'),
(2, 'brand2');


CREATE TABLE IF NOT EXISTS `category` (
                                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                          `name` varchar(255) DEFAULT NULL,
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB;

INSERT INTO `category` (`id`, `name`) VALUES
(1, 'Home Appliances'),
(2, 'Fashion'),
(3, 'TV'),
(4, 'Home Garden & DIY'),
(5, 'Personal Care');




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


INSERT INTO `product` (`id`, `created_by`, `created_date`, `description`, `image_path`, `last_modified_by`, `last_modified_date`, `name`, `price`, `unit_in_stock`, `category_id`, `brand_id`,`is_available`) VALUES
(1, 'string@gmail.com', '2020-10-04 10:29:09.002000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:29:09.040000', 'string', 100, 100, 1, 1, 1),
(2, 'string@gmail.com', '2020-10-04 10:42:10.915000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:42:10.953000', 'string', 300, 5, 1, 1, 1),
(3, 'string@gmail.com', '2020-10-04 10:42:23.109000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:42:23.110000', 'String string', 200, 5, 1, 1, 1),
(4, 'string@gmail.com', '2020-10-04 10:44:31.608000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:44:31.645000', 'string', 360, 5, 1, 1, 1),
(5, 'string@gmail.com', '2020-10-04 10:45:16.872000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:45:16.907000', 'String string strung', 360, 5, 1, 1, 1),
(6, 'string@gmail.com', '2020-10-04 10:47:13.157000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:47:13.199000', 'string', 1000, 5, 1, 1, 1),
(7, 'string@gmail.com', '2020-10-04 10:47:20.371000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:47:20.371000', 'string', 100, 5, 1, 1, 1),
(8, 'string@gmail.com', '2020-10-04 10:49:10.431000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:49:10.466000', 'string', 100, 5, 1, 1, 1),
(9, 'string@gmail.com', '2020-10-04 10:49:31.544000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:49:31.545000', 'string', 100, 5, 1, 1, 1),
(10, 'string@gmail.com', '2020-10-04 10:49:48.408000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:49:48.408000', 'String', 100, 5, 1, 1, 1),
(11, 'string@gmail.com', '2020-10-04 10:53:28.091000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:53:28.127000', 'String', 100, 5, 1, 1, 1),
(12, 'string@gmail.com', '2020-10-04 10:54:39.837000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:54:39.837000', 'String dsadasd', 25, 5, 1, 1, 1),
(13, 'string@gmail.com', '2020-10-04 10:54:57.069000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:54:57.069000', 'String dsadasd', 100, 5, 1, 1, 1),
(14, 'string@gmail.com', '2020-10-04 10:55:23.609000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:55:23.609000', 'String', 100, 5, 1, 1, 1),
(15, 'string@gmail.com', '2020-10-04 10:57:30.734000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:57:30.770000', 'String', 100, 5, 1, 1, 1),
(16, 'string@gmail.com', '2020-10-04 10:58:40.476000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:58:40.476000', 'String', 100, 5, 1, 1, 1),
(17, 'string@gmail.com', '2020-10-04 10:59:53.448000', 'string', 'string', 'string@gmail.com', '2020-10-04 10:59:53.448000', 'String string', 100, 5, 1, 1, 1),
(18, 'string@gmail.com', '2020-10-04 11:00:08.759000', 'string', 'string', 'string@gmail.com', '2020-10-04 11:00:08.760000', 'String string dsadasdas', 100, 5, 1, 1, 1),
(19, 'string@gmail.com', '2020-10-04 11:31:52.143000', 'string', 'string', 'string@gmail.com', '2020-10-04 11:31:52.178000', 'not string', 20, 0, 1, 1, 1),
(20, 'string@gmail.com', '2020-10-04 11:45:18.412000', 'string', 'string', 'string@gmail.com', '2020-10-04 11:45:18.449000', 'string', 200, 20, 1, 1, 1),
(21, 'string@gmail.com', '2020-10-04 11:45:36.996000', 'string', 'string', 'string@gmail.com', '2020-10-04 11:45:36.997000', 'String', 200, 20, 1, 1, 1),
(22, 'string@gmail.com', '2020-10-04 11:48:34.778000', 'string', 'string', 'string@gmail.com', '2020-10-04 11:48:34.778000', 'product', 200, 20, 2, 2, 1),
(23, 'string@gmail.com', '2020-10-04 11:48:46.693000', 'string', 'string', 'string@gmail.com', '2020-10-04 11:48:46.693000', 'product 2', 200, 20, 2, 2, 1),
(24, 'string@gmail.com', '2020-10-04 12:24:27.086000', 'string', 'string', 'string@gmail.com', '2020-10-04 12:24:27.129000', 'string', 10, 3, 1, 1, 1),
(25, 'string@gmail.com', '2020-10-04 12:24:44.898000', 'string', 'string', 'string@gmail.com', '2020-10-04 12:24:44.899000', 'string', 10, 9, 1, 1, 1),
(26, 'string@gmail.com', '2020-10-05 07:13:03.523000', 'string', 'string', 'string@gmail.com', '2020-10-05 07:13:03.574000', 'string', 20, 20, 1, 1, 1),
(27, 'string@gmail.com', '2020-10-05 07:13:27.761000', 'string', 'string', 'string@gmail.com', '2020-10-05 07:13:27.761000', 'string ', 30, 30, 1, 1, 1),
(28, 'string@gmail.com', '2020-10-05 07:13:34.997000', 'string', 'string', 'string@gmail.com', '2020-10-05 07:13:34.998000', 'String String', 90, 1000, 1, 1, 1),
(29, 'string@gmail.com', '2020-10-05 07:41:51.973000', 'string', 'string', 'string@gmail.com', '2020-10-05 07:41:52.010000', 'string', 20, 20, 1, 1, 1);



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



#create user x6
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


CREATE TABLE IF NOT EXISTS `copy_of_the_product` (
                                                     `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB;

/*
INSERT INTO `copy_of_the_product` (`id`) VALUES
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




