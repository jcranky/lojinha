# Items schema
# --- !Ups

CREATE SEQUENCE user_id_seq;
CREATE TABLE user (
    id INTEGER NOT NULL DEFAULT nextval('user_id_seq'),
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    passwd VARCHAR(255) NOT NULL
);
INSERT INTO user(email, name, passwd) VALUES('admin@lojinha.com', 'jcranky', '1234');

CREATE SEQUENCE category_id_seq;
CREATE TABLE category (
    id INTEGER NOT NULL DEFAULT nextval('category_id_seq'),
    name varchar(255) UNIQUE
);

CREATE SEQUENCE item_id_seq;
CREATE TABLE item (
    id INTEGER NOT NULL DEFAULT nextval('item_id_seq'),
    name varchar(255),
    description varchar(2048),
    imageKeys varchar(1024),
    category_id INTEGER NOT NULL
);

CREATE SEQUENCE bid_id_seq;
CREATE TABLE bid (
    id INTEGER NOT NULL DEFAULT nextval('bid_id_seq'),
    bidder_email varchar(255) NOT NULL,
    value NUMBER(8,2),
    dateTime TIMESTAMP NOT NULL,
    item_id INTEGER NOT NULL
);

# --- !Downs

DROP TABLE user;
DROP SEQUENCE user_id_seq;

DROP TABLE category;
DROP SEQUENCE category_id_seq;

DROP TABLE item;
DROP SEQUENCE item_id_seq;

DROP TABLE bid;
DROP SEQUENCE bid_id_seq;
