# adding sold status to items
# --- !Ups

ALTER TABLE item ADD COLUMN sold BOOLEAN DEFAULT FALSE;

# --- !Downs

ALTER TABLE item DROP COLUMN sold;
