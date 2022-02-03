--Add db creation here
CREATE TABLE a_person (
    id bigint NOT NULL,
    accountname character varying(255) NOT NULL,
    accounttype character varying(255),
    displayname character varying(255),
);

CREATE SEQUENCE a_person_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE a_person_id_seq OWNED BY a_person.id;
