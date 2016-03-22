drop table if exists ld2_bson;

CREATE TABLE ld2_bson (
  id bigserial NOT NULL,
  data jsonb,
  CONSTRAINT pk_ld2_bson PRIMARY KEY (id)
)
WITH (
OIDS=FALSE
);
ALTER TABLE ld2_bson
OWNER TO test;