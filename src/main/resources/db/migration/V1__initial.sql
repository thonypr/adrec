CREATE TABLE ads (
    id               SERIAL PRIMARY KEY,
    title            TEXT NOT NULL,
    youtube_url      TEXT NOT NULL,
    youtube_video_id TEXT NOT NULL UNIQUE,
    brand            TEXT,
    year             INT,
    created_at       TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE tags (
    id   SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE
);

CREATE TABLE ad_tags (
    ad_id  INT REFERENCES ads(id)  ON DELETE CASCADE,
    tag_id INT REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (ad_id, tag_id)
);

CREATE TABLE views (
    id           SERIAL PRIMARY KEY,
    ad_id        INT REFERENCES ads(id) ON DELETE CASCADE,
    seen         BOOLEAN NOT NULL DEFAULT FALSE,
    rating       SMALLINT CHECK (rating BETWEEN 1 AND 5),
    last_seen_at TIMESTAMPTZ,
    created_at   TIMESTAMPTZ DEFAULT NOW()
);

CREATE UNIQUE INDEX views_ad_id_idx ON views(ad_id);

CREATE TABLE sessions (
    id           SERIAL PRIMARY KEY,
    playlist_id  TEXT NOT NULL,
    playlist_url TEXT NOT NULL,
    mode         TEXT NOT NULL,
    created_at   TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE session_ads (
    session_id INT REFERENCES sessions(id) ON DELETE CASCADE,
    ad_id      INT REFERENCES ads(id)      ON DELETE CASCADE,
    position   INT NOT NULL,
    PRIMARY KEY (session_id, ad_id)
);
