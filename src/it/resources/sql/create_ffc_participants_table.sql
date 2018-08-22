CREATE TABLE IF NOT EXISTS `i7b0_ffc_participants` (
  `id` int(10) unsigned AUTO_INCREMENT NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `title` varchar(120) NOT NULL,
  `first_name` varchar(120) NOT NULL,
  `last_name` varchar(120) NOT NULL,
  `email_address` varchar(120) NOT NULL,
  `address_line_1` varchar(120) NOT NULL,
  `address_line_2` varchar(120) DEFAULT NULL,
  `city` varchar(120) NOT NULL,
  `postcode` varchar(120) NOT NULL,
  `country` varchar(120) NOT NULL,
  `phone_number` varchar(120) NOT NULL,
  `contribution_amount` tinyint(1) NOT NULL DEFAULT 0,
  `contribution_date` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;