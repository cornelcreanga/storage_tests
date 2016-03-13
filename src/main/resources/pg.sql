create or replace function random_string(length integer,chars text[]) returns text as
$$
declare
  result text := '';
  i integer := 0;
begin
  if length < 0 then
    raise exception 'Given length cannot be less than 0';
  end if;
  for i in 1..length loop
    result := result || chars[1+random()*(array_length(chars, 1)-1)];
  end loop;
  return result;
end;
$$ language plpgsql;

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
  CONSTRAINT pk_lead_data2 PRIMARY KEY (id)
)
WITH (
OIDS=FALSE
);
ALTER TABLE ld2
OWNER TO test;

insert into ld2(
  lead_id ,salutation,firstName,lastName,street,streetNumber,zipCode,city,country,agbAccepted,email,campaignURL,newsletterAccepted,birthDay,
  source,singleOptInDate,singleOptInIp,formCaptchaValue,expectedCaptcha,answer,winningPoints)
select
  lead_id,
  'Herr',
  random_string(8,'{A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z}'),
  random_string(9,'{A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z}'),
  random_string(12,'{A,B,C,D,E}'),
  '48',
  '76135',
  'Garlsrete',
  'AT',
  true,
  random_string(12,'{A,B,C,D,E}') || '@beq.en',
  'ABCDEFGHI',
  true,
  NOW() - '1 year'::INTERVAL * ROUND(RANDOM() * 100),
  'S',
  NOW() - '1 year'::INTERVAL * ROUND(RANDOM() * 100),
  random_string(2,'{1,2,3,4,5,6,7,8,9}') || '.' || random_string(2,'{1,2,3,4,5,6,7,8,9}') || '.' ||  random_string(2,'{1,2,3,4,5,6,7,8,9}') || '.' ||  random_string(3,'{1,2,3,4,5,6,7,8,9}'),
  'pleite',
  'abfuhr',
  'München',
  10+RANDOM() * 100
from generate_series(1,15000000);