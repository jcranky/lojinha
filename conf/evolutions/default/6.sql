
# --- !Ups
ALTER TABLE bid ADD COLUMN notify_better_bids BOOLEAN NOT NULL;

# --- !Downs
ALTER TABLE bid DROP COLUMN notify_better_bids;
