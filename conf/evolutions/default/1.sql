# Items schema
# --- !Ups

CREATE SEQUENCE item_id_seq;
CREATE TABLE item (
    id INTEGER NOT NULL DEFAULT nextval('item_id_seq'),
    name varchar(255),
    description varchar(2048),
    imageKeys varchar(1024)
);

CREATE SEQUENCE bid_id_seq;
CREATE TABLE bid (
    id INTEGER NOT NULL DEFAULT nextval('bid_id_seq'),
    bidder_email varchar(255) NOT NULL,
    value NUMBER(8,2),
    item_id INTEGER NOT NULL
);

# --- !Downs

DROP TABLE item;
DROP SEQUENCE item_id_seq;

DROP TABLE bid;
DROP SEQUENCE bid_id_seq;
