
drop table if exists ld2;

CREATE TABLE ld2
(
  id bigserial NOT NULL,
  lead_id integer,
  salutation text,
  firstname text,
  lastname text,
  street text,
  streetnumber text,
  zipcode text,
  city text,
  country text,
  agbaccepted boolean,
  email text,
  campaignurl text,
  newsletteraccepted boolean,
  birthday date,
  source text,
  singleoptindate timestamp without time zone,
  singleoptinip text,
  formcaptchavalue text,
  expectedcaptcha text,
  answer text,
  winningpoints integer,
  hobby text,
  team text,
  salary integer,
  kids integer,
  CONSTRAINT pk_lead_data2 PRIMARY KEY (id)
)
WITH (
OIDS=FALSE
);
ALTER TABLE ld2
OWNER TO test;
