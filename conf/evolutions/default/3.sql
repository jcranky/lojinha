# adding fake delete column
# --- !Ups

ALTER TABLE item ADD COLUMN deleted BOOLEAN DEFAULT FALSE;

# --- !Downs

ALTER TABLE item DROP COLUMN deleted;
