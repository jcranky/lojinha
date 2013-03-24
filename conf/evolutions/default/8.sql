
# --- !Ups
ALTER TABLE item ADD COLUMN min_value NUMERIC(8,2) NOT NULL DEFAULT 0;

# --- !Downs
ALTER TABLE item DROP COLUMN min_value;
