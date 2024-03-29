create database `library`;
use `library`;

CREATE TABLE IF NOT EXISTS `User` (
    `UserId`             int          NOT NULL AUTO_INCREMENT,
    `PhoneNumber`        varchar(10)  NOT NULL,
    `Password`           varchar(255) NOT NULL,
    `UserName`           varchar(255) NOT NULL,
    `RegistrationTime`   datetime         NOT NULL,
    `LastLoginTime`      datetime,
    `Role`               varchar(15)  NOT NULL,
    PRIMARY KEY(`UserId`)
);

CREATE UNIQUE INDEX uph on `User` (`PhoneNumber`);

CREATE TABLE IF NOT EXISTS `Inventory` (
    `InventoryId`       int          NOT NULL AUTO_INCREMENT,
    `ISBN`              varchar(20)  NOT NULL,
    `StoreTime`         datetime     NOT NULL,
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
    `BorrowTime`        datetime    NOT NULL,
    `ReturnTime`        datetime
);

CREATE INDEX uid on `BorrowingRecord` (`UserId`);
CREATE INDEX iid on `BorrowingRecord` (`InventoryId`);

CREATE TABLE IF NOT EXISTS `JwtToken` (
    `UserId`            int         NOT NULL,
    `Token`             text        NOT NULL,
    `Expires`           datetime    NOT NULL,
    PRIMARY KEY(`UserId`)
);

CREATE TABLE IF NOT EXISTS `Review` (
    `UserId`            int         NOT NULL,
    `ISBN`              varchar(20) NOT NULL,
    `Comment`           text        NOT NULL,
    `Rate`              int         NOT NULL,
    `ReviewTime`        datetime    NOT NULL,
    PRIMARY KEY(`UserId`, `ISBN`)
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

DROP PROCEDURE IF EXISTS find_user_password;
DELIMITER //
CREATE PROCEDURE find_user_password(IN PhoneNumber varchar(10), OUT pw varchar(255))
BEGIN
    SET pw = (SELECT `User`.`Password` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS user_signin;
DELIMITER //
CREATE PROCEDURE user_signin(IN PhoneNumber varchar(10), IN Token text)
BEGIN
    DECLARE user_id int;
    START TRANSACTION;
        SET user_id = (SELECT `UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
        UPDATE `User` SET `LastLoginTime` = NOW() WHERE `User`.`UserId` = user_id;
        INSERT INTO `JwtToken` (`UserId`, `Token`, `Expires`) VALUES (user_id, Token, date_add(NOW(),interval 5 minute)) ON DUPLICATE KEY UPDATE `JwtToken`.`Token` = Token, `JwtToken`.`Expires` = date_add(NOW(),interval 5 minute);
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS user_signout;
DELIMITER //
CREATE PROCEDURE user_signout(IN PhoneNumber varchar(10))
BEGIN
    DECLARE user_id int;
    START TRANSACTION;
        SET user_id = (SELECT `UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
        DELETE FROM `JwtToken` WHERE `JwtToken`.`UserId` = user_id;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS get_user_by_phone_number;
DELIMITER //
CREATE PROCEDURE get_user_by_phone_number(IN PhoneNumber varchar(10))
BEGIN
    SELECT * FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS find_inventory_by_ISBN;
DELIMITER //
CREATE PROCEDURE find_inventory_by_ISBN(IN ISBN varchar(20))
BEGIN
    START TRANSACTION;
        SELECT rc.`InventoryId`, rc.`Name`, rc.`Author`, rc.`Status`, `BorrowingRecord`.`BorrowTime`, `BorrowingRecord`.`ReturnTime` 
        FROM (SELECT `Book`.`Name`, `Book`.`Author`, `Inventory`.`InventoryId`, `Inventory`.`Status` FROM `Book`, `Inventory` WHERE `Book`.`ISBN` = ISBN and `Book`.`ISBN` = `Inventory`.`ISBN`) as rc
        LEFT JOIN `BorrowingRecord` ON rc.`InventoryId` = `BorrowingRecord`.`InventoryId` and `BorrowingRecord`.`ReturnTime` IS NULL;
    COMMIT;
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
    UPDATE `Inventory` SET `Inventory`.`Status` = Status WHERE `Inventory`.`InventoryId` = InventoryId and `Inventory`.`Status` != "ALLOWED";
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS add_book;
DELIMITER //
CREATE PROCEDURE add_book(IN ISBN varchar(20), IN Name varchar(255), IN Author varchar(255), IN Introduction text, IN Status varchar(15))
BEGIN
    START TRANSACTION;
        INSERT IGNORE INTO `Book`(`ISBN`, `Name`, `Author`, `Introduction`) VALUES (ISBN, Name, Author, Introduction);
        INSERT INTO `Inventory`(`ISBN`, `StoreTime`, `Status`) VALUES (ISBN, NOW(), Status);
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS remove_book;
DELIMITER //
CREATE PROCEDURE remove_book(IN `InventoryId` int)
BEGIN
    DELETE FROM `Inventory` WHERE `Inventory`.`InventoryId` = InventoryId and `Inventory`.`Status` != "ALLOWED";
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS borrow_book;
DELIMITER //
CREATE PROCEDURE borrow_book(IN PhoneNumber varchar(10), IN InventoryId int, OUT is_success boolean)
BEGIN
    DECLARE is_allow int;
    DECLARE user_id int;
    START TRANSACTION;
        SET is_success = false;
        SET is_allow = (SELECT COUNT(*) FROM `Inventory` WHERE `Inventory`.`InventoryId` = InventoryId and `Inventory`.`Status` = "ALLOWED" FOR UPDATE);
        IF is_allow IS true THEN
            SET user_id = (SELECT `User`.`UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
            IF user_id IS NOT NULL THEN
                INSERT INTO `BorrowingRecord`(`UserId`, `InventoryId`, `BorrowTime`, `ReturnTime`) VALUES(user_id, InventoryId, NOW(), NULL);
                UPDATE `Inventory` SET `Inventory`.`Status` = "BORROWED" WHERE `Inventory`.`InventoryId` = InventoryId;
                SET is_success = true;
            END IF;
        END IF;
    COMMIT;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS return_book;
DELIMITER //
CREATE PROCEDURE return_book(IN InventoryId int, IN PhoneNumber varchar(10), OUT is_success boolean)
BEGIN
    DECLARE is_borrow int;
    DECLARE user_id int;
    DECLARE is_borrowed_by_user int;
    START TRANSACTION;
        SET is_success = false;
        SET is_borrow = (SELECT COUNT(*) 
                        FROM `Inventory` 
                        WHERE `Inventory`.`InventoryId` = InventoryId and `Inventory`.`Status` = "BORROWED" FOR UPDATE);
        IF is_borrow IS NOT NULL THEN
            SET user_id = (SELECT `User`.`UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
            IF user_id IS NOT NULL THEN
                SET is_borrowed_by_user = (SELECT COUNT(*) 
                                            FROM `BorrowingRecord` 
                                            WHERE `BorrowingRecord`.`UserId` = user_id 
                                            and `BorrowingRecord`.`InventoryId` = InventoryId 
                                            and `BorrowingRecord`.`ReturnTime` = NULL);
                IF is_borrowed_by_user IS NOT NULL THEN
                    UPDATE `BorrowingRecord` 
                    SET `BorrowingRecord`.`ReturnTime` = NOW() 
                    WHERE `BorrowingRecord`.`InventoryId` = InventoryId and `BorrowingRecord`.`UserId` = user_id;
                    UPDATE `Inventory` 
                    SET `Inventory`.`Status` = "ALLOWED" 
                    WHERE `Inventory`.`InventoryId` = InventoryId;
                    SET is_success = true;
                END IF;
            END IF;
        END IF;
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
        IF user_id IS NOT NULL THEN
            SELECT `Inventory`.`InventoryId`, `Inventory`.`Status`, `Book`.`Name`, `Book`. `Author`, Record.`BorrowTime`, Record.`ReturnTime`
            FROM (SELECT * FROM `BorrowingRecord` WHERE `BorrowingRecord`.`UserId` = user_id) as Record, `Book`, `Inventory`
            WHERE  Record.`InventoryId`  = `Inventory`.`InventoryId` and `Inventory`.`ISBN` = `Book`.`ISBN`;
        END IF;
    COMMIT;
END//
DELIMITER ;


DROP PROCEDURE IF EXISTS is_jwt_expired;
DELIMITER //
CREATE PROCEDURE is_jwt_expired(IN PhoneNumber varchar(10), OUT is_expired boolean)
BEGIN
    SET is_expired = (SELECT TIMESTAMPDIFF(SECOND, NOW(), `JwtToken`.`Expires`) < 0
                        FROM `JwtToken`, `User` 
                        WHERE `User`.`PhoneNumber` = PhoneNumber and `User`.`UserId` = `JwtToken`.`UserId`);
    IF is_expired IS NULL THEN
        SET is_expired = true;
    END IF;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS remove_jwt_token_by_phone;
DELIMITER //
CREATE PROCEDURE remove_jwt_token_by_phone(IN PhoneNumber varchar(10))
BEGIN
    DECLARE user_id int;
    SET user_id = (SELECT `UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
    IF user_id IS NOT NULL THEN
        DELETE FROM `JwtToken` WHERE `JwtToken`.`UserId` = user_id;
    END IF;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS add_review;
DELIMITER //
CREATE PROCEDURE add_review(IN PhoneNumber varchar(10), IN ISBN varchar(20), IN Comment text, IN Rate int)
BEGIN
    DECLARE user_id int;
    SET user_id = (SELECT `UserId` FROM `User` WHERE `User`.`PhoneNumber` = PhoneNumber);
    IF user_id IS NOT NULL THEN
        INSERT INTO `Review`(`UserId`, `ISBN`, `Comment`, `Rate`, `ReviewTime`)
        VALUES (user_id, ISBN, Comment, Rate, NOW());
    END IF;
END//
DELIMITER ;

DROP PROCEDURE IF EXISTS get_reviews;
DELIMITER //
CREATE PROCEDURE get_reviews(IN ISBN varchar(20))
BEGIN
    SELECT * FROM `Review` WHERE `Review`.`ISBN` = ISBN;
END//
DELIMITER ;




