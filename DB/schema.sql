create database `library`;
use `library`;

CREATE TABLE IF NOT EXISTS `User` (
    `UserId`             int          NOT NULL AUTO_INCREMENT,
    `PhoneNumber`        varchar(10)  NOT NULL UNIQUE,
    `Password`           varchar(255) NOT NULL,
    `UserName`           varchar(255) NOT NULL,
    `RegistrationTime`   date         NOT NULL,
    `LastLoginTime`      date,
    `Role`               varchar(15)  NOT NULL,
    PRIMARY KEY(`UserId`)
);

CREATE TABLE IF NOT EXISTS `Inventory` (
    `InventoryId`       int          NOT NULL AUTO_INCREMENT,
    `ISBN`              varchar(20)  NOT NULL,
    `StoreTime`         date         NOT NULL,
    `Status`            ENUM('ALLOWED', 'BORROWED', 'BUSY', 'LOST', 'DAMAGED', 'ABANDONED'),
    PRIMARY KEY(`InventoryId`)
);

CREATE TABLE IF NOT EXISTS `Book` (
    `ISBN`              varchar(20)  NOT NULL,
    `Name`              varchar(255) NOT NULL,
    `Author`            varchar(255) NOT NULL,
    `Introduction`      text         NOT NULL,
    PRIMARY KEY(`ISBN`)
);

CREATE TABLE IF NOT EXISTS `BorrowingRecord` (
    `UserId`            int         NOT NULL,
    `InventoryId`       int         NOT NULL,
    `BorrowTime`        date        NOT NULL,
    `ReturnTime`        date
);

CREATE INDEX uid on `BorrowingRecord` (`UserId`);
CREATE INDEX iid on `BorrowingRecord` (`InventoryId`);

CREATE TABLE IF NOT EXISTS `JwtToken` (
    `UserId`            int         NOT NULL,
    `Token`             text NOT NULL,
    PRIMARY KEY(`UserId`)
);


DROP PROCEDURE IF EXISTS user_signup;
DELIMITER //
CREATE PROCEDURE user_signup(IN PhoneNumber varchar(10), IN Password varchar(255), IN UserName varchar(255), IN Role varchar(15))
BEGIN
    START TRANSACTION;
        INSERT INTO `User`(`PhoneNumber`, `Password`, `UserName`, `RegistrationTime`, `LastLoginTime`, `Role`)
        VALUES (PhoneNumber, Password, UserName, NOW(), NULL, Role);
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS user_signin;
DELIMITER //
CREATE PROCEDURE user_signin(IN PhoneNumber varchar(10), IN Token text, OUT pw varchar(255))
BEGIN
    DECLARE is_exist int;
    DECLARE id int;
    START TRANSACTION;
        SET pw = (SELECT `User`.`Password` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
        IF pw - NULL THEN
            UPDATE `User` SET `LastLoginTime` = NOW() WHERE `User`.`PhoneNumber` = PhoneNumber;
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
CREATE PROCEDURE user_signout(IN PhoneNumber varchar(10))
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
CREATE PROCEDURE get_user_by_phone_number(IN PhoneNumber varchar(10))
BEGIN
    START TRANSACTION;
        SELECT * FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS find_inventory_by_ISBN;
DELIMITER //
CREATE PROCEDURE find_inventory_by_ISBN(IN ISBN varchar(20))
BEGIN
    SELECT rc.`InventoryId`, rc.`Name`, rc.`Author`, rc.`Status`, `BorrowingRecord`.`BorrowTime`, `BorrowingRecord`.`ReturnTime` 
    FROM (SELECT `Book`.`Name`, `Book`.`Author`, `Inventory`.`InventoryId`, `Inventory`.`Status` FROM `Book`, `Inventory` WHERE `Book`.`ISBN` = ISBN and `Book`.`ISBN` = `Inventory`.`ISBN`) as rc
    LEFT JOIN `BorrowingRecord` ON rc.`InventoryId` = `BorrowingRecord`.`InventoryId`;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS find_book_by_ISBN;
DELIMITER //
CREATE PROCEDURE find_book_by_ISBN(IN ISBN varchar(20))
BEGIN
    SELECT * FROM `Book` WHERE `Book`.`ISBN` = ISBN;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS update_inventory_status_by_InventoryId;
DELIMITER //
CREATE PROCEDURE update_inventory_status_by_InventoryId(IN InventoryId int, IN Status varchar(15))
BEGIN
    START TRANSACTION;
        UPDATE `Inventory` SET `Inventory`.`Status` = Status WHERE `Inventory`.`InventoryId` = InventoryId;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS add_book;
DELIMITER //
CREATE PROCEDURE add_book(IN ISBN varchar(20), IN Name varchar(255), IN Author varchar(255), IN Introduction text, IN Status varchar(15))
BEGIN
    DECLARE is_exist int;
    START TRANSACTION;
        SET is_exist = (SELECT COUNT(*) FROM `Book` WHERE `Book`.`ISBN` = ISBN);
        IF is_exist = 1 THEN
            INSERT INTO `Book`(`ISBN`, `Name`, `Author`, `Introduction`)
            VALUES (ISBN, Name, Author, Introduction);
        END IF;
        INSERT INTO `Inventory`(`ISBN`, `StoreTime`, `Status`)
        VALUES (ISBN, NOW(), Status);
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS remove_book;
DELIMITER //
CREATE PROCEDURE remove_book(IN `InventoryId` int)
BEGIN
    START TRANSACTION;
        DELETE FROM `Inventory` WHERE `Inventory`.`InventoryId` = InventoryId;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS borrow_book;
DELIMITER //
CREATE PROCEDURE borrow_book(IN PhoneNumber varchar(10), IN InventoryId int)
BEGIN
    DECLARE is_allow varchar(15);
    DECLARE user_id int;
    START TRANSACTION;
        SELECT * FROM `Inventory` WHERE `Inventory`.`InventoryId` = InventoryId FOR UPDATE;
        SET is_allow = (SELECT `Status` FROM `Inventory` WHERE `Inventory`.`InventoryId` = InventoryId);
        IF is_allow = "ALLOWED" THEN
            SET user_id = (SELECT `User`.`UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
            INSERT INTO `BorrowingRecord`(`UserId`, `InventoryId`, `BorrowTime`, `ReturnTime`) VALUES(user_id, InventoryId, NOW(), NULL);
            UPDATE `Inventory` SET `Inventory`.`Status` = "BORROWED" WHERE `Inventory`.`InventoryId` = InventoryId;
        END IF;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS return_book;
DELIMITER //
CREATE PROCEDURE return_book(IN InventoryId int, IN PhoneNumber varchar(10))
BEGIN
    DECLARE user_id int;
    START TRANSACTION;
        SELECT * FROM `Inventory` WHERE `Inventory`.`InventoryId` = InventoryId FOR UPDATE;
        SET user_id = (SELECT `User`.`UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
        UPDATE `BorrowingRecord` SET `BorrowingRecord`.`ReturnTime` = NOW() WHERE `BorrowingRecord`.`InventoryId` = InventoryId and `BorrowingRecord`.`UserId` = user_id;
        UPDATE `Inventory` SET `Inventory`.`Status` = "ALLOWED" WHERE `Inventory`.`InventoryId` = InventoryId;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS get_all_books;
DELIMITER //
CREATE PROCEDURE get_all_books()
BEGIN
    SELECT * FROM `Book`;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS get_record_by_phone;
DELIMITER //
CREATE PROCEDURE get_record_by_phone(IN PhoneNumber varchar(10))
BEGIN
    DECLARE user_id int;
    START TRANSACTION;
        SET user_id = (SELECT `User`.`UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);

        SELECT `Inventory`.`InventoryId`, `Inventory`.`Status`, `Book`.`Name`, `Book`. `Author`, Record.`BorrowTime`, Record.`ReturnTime`
        FROM (SELECT * FROM `BorrowingRecord` WHERE `BorrowingRecord`.`UserId` = user_id) as Record, `Book`, `Inventory`
        WHERE  Record.`InventoryId`  = `Inventory`.`InventoryId` and `Inventory`.`ISBN` = `Book`.`ISBN`;
    COMMIT;
END//
DELIMITER ;


