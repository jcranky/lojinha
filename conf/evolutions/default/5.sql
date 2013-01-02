
# --- !Ups
ALTER TABLE category ADD COLUMN display_name varchar(255);
ALTER TABLE category ADD COLUMN url_name varchar(64);

UPDATE category SET display_name = name;
UPDATE category SET url_name = name;

ALTER TABLE category ADD UNIQUE (display_name);
ALTER TABLE category ADD UNIQUE (url_name);

ALTER TABLE category DROP COLUMN name;


# --- !Downs
ALTER TABLE category ADD COLUMN name varchar(255);
UPDATE category SET name = display_name;
ALTER TABLE category ADD UNIQUE (name);

ALTER TABLE category DROP COLUMN display_name;
ALTER TABLE category DROP COLUMN url_name;
