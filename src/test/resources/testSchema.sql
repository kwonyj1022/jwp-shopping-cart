DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS member;
CREATE TABLE IF NOT EXISTS product
(
    id      LONG            NOT NULL    AUTO_INCREMENT,
    name    VARCHAR(255)    NOT NULL,
    imgURL  VARCHAR(8000)    NOT NULL,
    price   INT             NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS member
(
    id          LONG            NOT NULL    AUTO_INCREMENT,
    email       VARCHAR(255)    NOT NULL    UNIQUE,
    password    VARCHAR(255)    NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS cart
(
    id              LONG    NOT NULL    AUTO_INCREMENT,
    member_id       LONG    NOT NULL,
    product_id      LONG    NOT NULL,
    PRIMARY KEY (id)
);

