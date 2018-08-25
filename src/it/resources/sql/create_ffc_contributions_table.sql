CREATE TABLE IF NOT EXISTS `i7b0_ffc_contributions` (
  `id` int(10) unsigned AUTO_INCREMENT NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  `username` varchar(120) NOT NULL,
  `first_name` varchar(120) NOT NULL,
  `last_name` varchar(120) NOT NULL,
  `email_address` varchar(120) NOT NULL,
  `amount` decimal(13,2) NOT NULL,
  `date` int(10) unsigned NOT NULL DEFAULT 0,
  `stripe_token` varchar(120) NOT NULL default '',
  `reference` varchar(120) NOT NULL default '',
  `status` varchar(120) NOT NULL default '',
  `type` varchar(20) NOT NULL DEFAULT 0,
  `email_sent` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;