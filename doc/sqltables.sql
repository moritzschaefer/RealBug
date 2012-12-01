-- Table: bug

-- DROP TABLE bug;

CREATE TABLE bug
(
  description text,
  image bytea,
  lt double precision,
  ln double precision,
  id serial NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE bug OWNER TO rmtcunwfewenbn;
