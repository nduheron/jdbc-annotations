CREATE TABLE `user` (
  `id`			NUMBER GENERATED ALWAYS as IDENTITY(START with 1 INCREMENT by 1),
  `last_name` 		VARCHAR2(50) NOT NULL,
  `first_name` 		VARCHAR2(50) NOT NULL,
  `age`			NUMBER,
  `address_number`	NUMBER,
  `address_street`	VARCHAR2(150),
  `address_city`	VARCHAR2(50),		
  `login` 		VARCHAR2(50) NOT NULL,
  `email` 		VARCHAR2(128),
  `enabled` 		bit(1) NOT NULL,
  `password` 		VARCHAR2(155),
  `last_connexion` 	timestamp,
  `role` 		VARCHAR2(10),
  PRIMARY KEY (`id`),
  CONSTRAINT login_unique UNIQUE (`login`)
) ;

