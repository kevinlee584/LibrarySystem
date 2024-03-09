create database `library`;
use `library`;

CREATE TABLE IF NOT EXISTS `User` (
    `UserId`             int          NOT NULL AUTO_INCREMENT,
    `PhoneNumber`        varchar(255) NOT NULL UNIQUE,
    `Password`           varchar(255) NOT NULL,
    `UserName`           varchar(255) NOT NULL,
    `RegistrationTime`   date         NOT NULL,
    `LastLoginTime`      date,
    `Role`               varchar(225) NOT NULL,
    PRIMARY KEY(`UserId`)
);

CREATE TABLE IF NOT EXISTS `Inventory` (
    `InventoryId`       int          NOT NULL AUTO_INCREMENT,
    `ISBN`              varchar(255) NOT NULL UNIQUE,
    `StoreTime`         date         NOT NULL,
    `Statue`            varchar(255) NOT NULL,
    PRIMARY KEY(`InventoryId`)
);

CREATE TABLE IF NOT EXISTS `Book` (
    `ISBN`              varchar(255) NOT NULL,
    `Name`              varchar(255) NOT NULL,
    `Author`            varchar(255) NOT NULL,
    `Introduction`      text         NOT NULL,
    PRIMARY KEY(`ISBN`)
);

CREATE TABLE IF NOT EXISTS `BorrowingRecord` (
    `UserId`            int         NOT NULL,
    `InventoryId`       int         NOT NULL,
    `BorrowTime`        date        NOT NULL,
    `ReturnTime`        date,
    PRIMARY KEY(`UserId`, `InventoryId` )
);

CREATE TABLE IF NOT EXISTS `JwtToken` (
    `UserId`            int         NOT NULL,
    `Token`             text NOT NULL,
    PRIMARY KEY(`UserId`)
);


DROP PROCEDURE IF EXISTS user_signup;
DELIMITER //
CREATE PROCEDURE user_signup(IN PhoneNumber varchar(255), IN Password varchar(255), IN UserName varchar(255), IN Role varchar(255))
BEGIN
    START TRANSACTION;
        INSERT INTO `User`(`PhoneNumber`, `Password`, `UserName`, `RegistrationTime`, `LastLoginTime`, `Role`)
        VALUES (PhoneNumber, Password, UserName, NOW(), NULL, Role);
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS user_signin;
DELIMITER //
CREATE PROCEDURE user_signin(IN PhoneNumber varchar(255), IN Password varchar(255), IN Token text)
BEGIN
    DECLARE correct int;
    DECLARE is_exist int;
    DECLARE id int;
    START TRANSACTION;
        SET correct = (SELECT COUNT(*) FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber and `User`.`Password` = Password);
        IF correct = 1 THEN
            UPDATE `User`
            SET `LastLoginTime` = NOW()
            WHERE `User`.`PhoneNumber` = PhoneNumber;

            SET is_exist = (SELECT COUNT(*) FROM `JwtToken`, `User` WHERE `JwtToken`.`UserId` = `User`.`UserId`);
            SET id = (SELECT `UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
            IF is_exist = 0 THEN
                INSERT INTO `JwtToken` (`UserId`, `Token`) VALUES (id, Token);
            ELSE
                UPDATE `JwtToken` SET `JwtToken`.`Token` = Token WHERE `JwtToken`.`UserId` = id;
            END IF;
        END IF;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS user_signout;
DELIMITER //
CREATE PROCEDURE user_signout(IN PhoneNumber varchar(255))
BEGIN
    DECLARE user_id int;
    START TRANSACTION;
        SET user_id = (SELECT `UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber );
        DELETE FROM `JwtToken` WHERE `JwtToken`.`UserId` = user_id;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS get_user_by_phone_number;
DELIMITER //
CREATE PROCEDURE get_user_by_phone_number(IN PhoneNumber varchar(255))
BEGIN
    START TRANSACTION;
        SELECT * FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS find_inventory_by_ISBN;
DELIMITER //
CREATE PROCEDURE find_inventory_by_ISBN(IN ISBN varchar(255))
BEGIN
    SELECT * FROM `Inventory` WHERE `Inventory`.`ISBN` = ISBN;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS update_inventory_status_by_InventoryId;
DELIMITER //
CREATE PROCEDURE update_inventory_status_by_InventoryId(IN InventoryId int, IN Statue varchar(255))
BEGIN
    START TRANSACTION;
        UPDATE `Inventory` SET `Inventory`.`Statue` = Statue WHERE `Inventory`.`InventoryId` = InventoryId;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS add_book;
DELIMITER //
CREATE PROCEDURE add_book(IN ISBN varchar(255), IN Name varchar(255), IN Author varchar(255), IN Introduction text)
BEGIN
    START TRANSACTION;
        INSERT INTO `Book`(`ISBN`, `Name`, `Author`, `Introduction`)
        VALUES (ISBN, Name, Author, Introduction);
        INSERT INTO `Inventory`(`ISBN`, `StoreTime`, `Status`)
        VALUES (ISBN, NOW(), 'ALLOW');
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS remove_book;
DELIMITER //
CREATE PROCEDURE remove_book(IN `InventoryId` varchar(255))
BEGIN
    START TRANSACTION;
        DELETE FROM `Inventory` WHERE `Inventory`.`InventoryId` = InventoryId;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS borrow_book;
DELIMITER //
CREATE PROCEDURE borrow_book(IN PhoneNumber varchar(255), IN InventoryId varchar(255))
BEGIN
    DECLARE is_allow varchar(255);
    DECLARE user_id int;
    START TRANSACTION;
        SET is_allow = (SELECT `Statue` FROM `Inventory` WHERE `Inventory`.`InventoryId` = InventoryId);
        IF is_allow = `ALLOW` THEN
            SET user_id = (SELECT `UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
            INSERT INTO `BorrowingRecord`(`UserId`, `InventoryId`, `BorrowTime`, `ReturnTime`)
            VALUES(user_id, InventoryId, NOW(), NULL);
        END IF;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS return_book;
DELIMITER //
CREATE PROCEDURE return_book(IN InventoryId varchar(255))
BEGIN
    START TRANSACTION;
        UPDATE `BorrowingRecord` SET `BorrowingRecord`.`ReturnTime` = NOW() WHERE `BorrowingRecord`.`InventoryId` = InventoryId;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS get_all_books;
DELIMITER //
CREATE PROCEDURE get_all_book()
BEGIN
    SELECT * FROM `Book`;
END//
DELIMITER ;

