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

INSERT INTO product (name, imgUrl, price) VALUES ('product1', 'https://url1.com', 1000);
INSERT INTO product (name, imgUrl, price) VALUES ('product2', 'https://url2.com', 2000);
INSERT INTO product (name, imgUrl, price) VALUES ('product3', 'https://url3.com', 3000);

INSERT INTO member (email, password) VALUES ('a@a.com', 'password1');
INSERT INTO member (email, password) VALUES ('b@b.com', 'password2');

INSERT INTO cart (member_id, product_id) VALUES (1, 1);
INSERT INTO cart (member_id, product_id) VALUES (1, 3);
INSERT INTO cart (member_id, product_id) VALUES (2, 2);
INSERT INTO cart (member_id, product_id) VALUES (2, 3);