-- Table: bug

-- DROP TABLE bug;

CREATE TABLE bug
(
  id serial NOT NULL,
  description text,
  image bytea,
  coordination_x double precision,
  coordination_y double precision
)
WITH (
  OIDS=FALSE
);
ALTER TABLE bug OWNER TO rmtcunwfewenbn;
