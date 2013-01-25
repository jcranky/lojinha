
# --- !Ups
ALTER TABLE bid ADD COLUMN notify_better_bids BOOLEAN NOT NULL DEFAULT false;

# --- !Downs
ALTER TABLE bid DROP COLUMN notify_better_bids;
