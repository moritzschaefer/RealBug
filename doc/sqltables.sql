-- Table: bug

-- DROP TABLE bug;

CREATE TABLE bug
(
  id serial NOT NULL,
  description text,
  lt double precision,
  ln double precision,
  image bytea
)
WITH (
  OIDS=FALSE
);
ALTER TABLE bug OWNER TO rmtcunwfewenbn;
