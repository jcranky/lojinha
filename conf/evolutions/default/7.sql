
# --- !Ups
CREATE TABLE feed_stats (
    origin VARCHAR(255) UNIQUE NOT NULL,
    download_count INTEGER NOT NULL
);

# --- !Downs
DROP TABLE feed_stats;
