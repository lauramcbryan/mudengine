set role MUDENGINE_ACTION;

create sequence MUDENGINE_ACTION.MUD_ACTION_SEQ;

CREATE TABLE MUDENGINE_ACTION.MUD_ACTION (
		ACTION_UID		bigint not null,
		ISSUER_CODE		integer not null,
		ACTOR_CODE		integer not null,		
		ACTION_CODE		varchar(15) not null,
		MEDIATOR_CODE	integer,
		WORLD_NAME		varchar(30) not null,
		PLACE_CODE		integer not null,
		TARGET_CODE		varchar(20) not null,
		TARGET_TYPE		varchar(20) not null,
		CONSTRAINT MUD_ACTION_PK PRIMARY KEY (ACTION_UID)
);

CREATE TABLE MUDENGINE_ACTION.MUD_ACTION_STATE (
		ACTION_UID		bigint not null,
		
		START_TURN		integer not null,
		END_TURN		integer not null,
		CUR_STATE		integer not null,  -- RUNNING, STOPPED, CANCELLED, HOLD
		SUCCESS_RATE	real not null,
		CONSTRAINT MUD_ACTION_STATE_PK PRIMARY KEY (ACTION_UID),
		FOREIGN KEY(ACTION_UID) REFERENCES MUDENGINE_ACTION.MUD_ACTION(ACTION_UID)
);