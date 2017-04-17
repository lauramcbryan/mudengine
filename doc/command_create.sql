set role MUDENGINE_COMMAND;

create sequence MUDENGINE_COMMAND.MUD_ACTION_SEQ;

CREATE TABLE MUDENGINE_COMMAND.MUD_COMMAND (
		ACTION_UID		bigint not null DEFAULT NEXTVAL('MUD_ACTION_SEQ'),
		ACTOR_CODE		integer not null,		
		
		ISSUER_CODE		integer not null,
		ACTION_CODE		varchar(15) not null,
		MEDIATOR_CODE	integer,
		WORLD_NAME		varchar(30) not null,
		PLACE_CODE		integer not null,
		TARGET_CODE		varchar(20) not null,
		TARGET_TYPE		varchar(20) not null,
		CONSTRAINT MUD_COMMAND_PK PRIMARY KEY (ACTION_UID, ACTOR_CODE)
);

CREATE TABLE MUDENGINE_COMMAND.MUD_ACTION (
		ACTION_UID		bigint not null,
		ACTOR_CODE		integer not null,

		START_TURN		integer not null,
		END_TURN		integer not null,
		CUR_STATE		integer not null,  -- RUNNING, STOPPED, CANCELLED, HOLD
		SUCCESS_RATE	real not null,
		CONSTRAINT MUD_ACTION_PK PRIMARY KEY (ACTION_UID, ACTOR_CODE),
		FOREIGN KEY(ACTION_UID, ACTOR_CODE) REFERENCES MUDENGINE_COMMAND.MUD_COMMAND(ACTION_UID, ACTOR_CODE)
);